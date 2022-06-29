package org.EricRamirezS.jdacommando.command.command;

import edu.rice.cs.util.ArgumentTokenizer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.ICommandEngine;
import org.EricRamirezS.jdacommando.command.Throttling;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedArgumentNameException;
import org.EricRamirezS.jdacommando.command.exceptions.InvalidValueException;
import org.EricRamirezS.jdacommando.command.exceptions.MissingArgumentException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@SuppressWarnings("rawtypes")
public abstract class Command implements ICommand, PermissionsName {

    private final String name;
    private final String group;
    private final String description;
    private final List<IArgument> arguments = new ArrayList<>();
    private final List<String> aliases = new ArrayList<>();
    private final List<Permission> clientPermissions = new ArrayList<>();
    private final List<Permission> memberPermissions = new ArrayList<>();
    private final List<String> examples = new ArrayList<>();
    private String details;
    private Throttling throttling;
    private boolean nsfw = false;
    private boolean privateUseOnly = false;
    private boolean guildOnly = false;
    private boolean runInThread = true;
    private boolean threadOnly = false;

    public Command(@NotNull String name, @NotNull String group, String description, IArgument... args)
            throws DuplicatedArgumentNameException {
        this.name = name.toLowerCase(Locale.ROOT);
        this.group = group.toLowerCase(Locale.ROOT);
        this.description = description;
        this.arguments.addAll(Arrays.asList(args));
        List<IArgument> unique = getArguments().stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(IArgument::getName))),
                        ArrayList::new));
        if (getArguments().size() != unique.size())
            throw new DuplicatedArgumentNameException();
        checkWarnings();
    }

    private static Member getSelfMember(Event event) {
        if (event instanceof SlashCommandInteractionEvent slash) {
            return slash.isFromGuild() ? Objects.requireNonNull(slash.getGuild()).getSelfMember() : null;
        }
        if (event instanceof MessageReceivedEvent command) {
            return command.isFromGuild() ? Objects.requireNonNull(command.getGuild()).getSelfMember() : null;
        }
        return null;
    }

    private static Member getMember(Event event) {
        if (event instanceof SlashCommandInteractionEvent slash) {
            return slash.isFromGuild() ? slash.getMember() : null;
        }
        if (event instanceof MessageReceivedEvent command) {
            return command.isFromGuild() ? command.getMember() : null;
        }
        return null;
    }

    private static @NotNull String mapArgumentUsage(@NotNull IArgument a) {
        if (a.isRequired()) {
            return "<" + a.getName() + (a.getDefaultValue() == null ? ">" : "=" + a.getDefaultValue().toString() + ">");
        } else {
            return "[" + a.getName() + (a.getDefaultValue() == null ? "]" : "=" + a.getDefaultValue().toString() + "]");
        }
    }

    private void checkWarnings() {
        ICommandEngine engine = CommandEngine.getInstance();
        if (getName().length() > 10)
            engine.logWarn(LocalizedFormat.format("Command_Warning_NameSize", getName(), getName().length()));
        if (getDescription().length() > 90)
            engine.logWarn(LocalizedFormat.format("Command_Warning_DescriptionSize", getName(), getDescription().length()));
        if (getDetails() != null && getDetails().length() > 1500)
            engine.logWarn(LocalizedFormat.format("Command_Warning_DetailsSize", getName(), getDetails().length()));
    }

    protected final Command addAliases(String @NotNull ... aliases) {
        for (String alias : aliases) {
            this.aliases.add(alias.toLowerCase(Locale.ROOT));
        }
        return this;
    }

    protected final Command addExample(String example) {
        this.examples.add(example);
        return this;
    }

    protected final Command addExamples(String... example) {
        this.examples.addAll(Arrays.asList(example));
        return this;
    }

    protected final Command addClientPermissions(Permission... permissions) {
        clientPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    protected final Command addMemberPermissions(Permission... permissions) {
        memberPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    protected final boolean shouldRun(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return false;
        if (event.getAuthor().isSystem())
            return false;
        if (event.isFromThread()) {
            if (isRunInThread() || !isPrivateUseOnly())
                return checkCommand(event,
                        CommandEngine.getInstance().getPrefix(event),
                        CommandEngine.getInstance().isReactToMention());
            return false;
        } else if (event.isFromGuild()) {
            if (isThreadOnly() || isPrivateUseOnly())
                return false;
            return checkCommand(event,
                    CommandEngine.getInstance().getPrefix(event),
                    CommandEngine.getInstance().isReactToMention());
        }
        return !(isGuildOnly() || isThreadOnly());
    }

    protected final @Nullable String checkClientPermissions(Event event) {
        for (Permission per : clientPermissions) {
            Member member = getSelfMember(event);
            if (member == null) return null;
            if (!PermissionUtil.checkPermission(member, per)) {
                return LocalizedFormat
                        .format("Command_MissingClientPermission",
                                event,
                                CommandEngine.getInstance().getString(NAME.get(per),
                                        CommandEngine.getInstance().getLanguage(event)),
                                getName(event));
            }
        }
        return null;
    }

    protected final @Nullable String checkMemberPermissions(Event event) {
        for (Permission per : memberPermissions) {
            Member member = getMember(event);
            if (member == null) return null;
            if (!PermissionUtil.checkPermission(member, per)) {
                return LocalizedFormat
                        .format("Command_MissingMemberPermission",
                                event,
                                CommandEngine.getInstance().getString(NAME.get(per),
                                        CommandEngine.getInstance().getLanguage(event)),
                                name);
            }
        }
        return null;
    }

    /**
     * Custom check of permission
     *
     * @param event messageEvent
     * @return null if there's no permission issues with this command,
     * a string with the permission problem if the user or client cannot execute the command.
     */
    protected String hasPermission(MessageReceivedEvent event) {
        return hasPermission((Event) event);
    }

    protected String hasPermission(SlashCommandInteractionEvent event) {
        return hasPermission((Event) event);
    }

    protected String hasPermission(Event event) {
        return null;
    }

    public final void onDirectMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!shouldRun(event)) return;
        String unable;

        // Verifying that the command has not exceeded its usage limit, if there is a limit.
        if (throttling != null) {
            if (throttling.check(event)) {
                unable = LocalizedFormat.format("Command_Failed", event);
                sendReply(event, unable + "\n" + LocalizedFormat.format("Command_Throttling", event));
            }
        }

        Map<String, IArgument> objArgs = parseArguments(event);
        if (objArgs == null) return;

        if (getThrottling() != null) getThrottling().addUsage(event);
        run(event, objArgs);
    }

    public final void onGuildThreadMessageReceived(@NotNull MessageReceivedEvent event) {
        onGuildMessageReceived(event);
    }

    public final void onGuildMessageReceived(@NotNull MessageReceivedEvent event) {
        // Verify that the command is in the base format expected by the bot
        if (!shouldRun(event)) return;
        String unable;
        // Verifying that the command is on an appropriate channel if it is NSFW
        if (isNsfw() && !event.getTextChannel().isNSFW()) {
            unable = LocalizedFormat.format("Command_Failed", event);
            sendReply(event, unable + "\n" + LocalizedFormat.format("Command_NSFW", event, getName(event)));
            return;
        }

        // Verifying that the client and user have permissions to execute this command.
        String error = checkPermissions(event);
        if (error != null) {
            unable = LocalizedFormat.format("Command_Failed", event);
            sendReply(event, unable + "\n" + error);
            return;
        }

        // Verifying that the command has not exceeded its usage limit, if there is a limit.
        if (getThrottling() != null) {
            if (getThrottling().check(event)) {
                unable = LocalizedFormat.format("Command_Failed", event);
                sendReply(event, unable + "\n" + LocalizedFormat.format("Command_Throttling", event));
            }
        }

        Map<String, IArgument> objArgs = parseArguments(event);

        if (objArgs == null) return;

        if (getThrottling() != null) getThrottling().addUsage(event);
        run(event, objArgs);
    }

    private @Nullable @UnmodifiableView Map<String, IArgument> parseArguments(MessageReceivedEvent event) {
        String prefix = CommandEngine.getInstance().getPrefix(event);
        String messageRaw = event.getMessage().getContentRaw();
        if (messageRaw.startsWith(prefix)) {
            messageRaw = messageRaw.replaceFirst(prefix, "");
        }

        String commandName = getUsedAlias(event, prefix, CommandEngine.getInstance().isReactToMention());
        commandName = commandName == null ? name : commandName;
        String arguments = messageRaw.substring(commandName.length()).trim();
        List<String> args = ArgumentTokenizer.tokenize(arguments, getArguments().size());
        Map<String, IArgument> objArgs;
        try {
            return parseArgs(args, event);
        } catch (InvalidValueException | MissingArgumentException ex) {
            String unable = LocalizedFormat.format("Command_Failed", event);
            sendReply(event, unable + "\n" + ex.getMessage());
            return null;
        }
    }

    public String checkPermissions(Event event) {
        String error = checkClientPermissions(event);
        if (error != null) return error;
        error = checkMemberPermissions(event);
        if (error != null) return error;
        if (event instanceof MessageReceivedEvent e) error = hasPermission(e);
        else if (event instanceof SlashCommandInteractionEvent e) error = hasPermission(e);
        if (error != null) return error;
        if (event instanceof MessageReceivedEvent e)
            if (e.isFromThread()) {
                if (isNsfw() && !e.getTextChannel().isNSFW()) {
                    return "_";
                }
                if (isRunInThread() || !isPrivateUseOnly())
                    return null;
                return "_";
            } else if (e.isFromGuild()) {
                if (isNsfw() && !e.getTextChannel().isNSFW()) {
                    return "_";
                }
                if (isThreadOnly() || isPrivateUseOnly())
                    return "_";
                return null;
            }
        if (event instanceof SlashCommandInteractionEvent e)
            if (e.getChannel().getType().isThread()) {
                if (isNsfw() && !e.getTextChannel().isNSFW()) {
                    return "_";
                }
                if (isRunInThread() || !isPrivateUseOnly())
                    return null;
                return "_";
            } else if (e.isFromGuild()) {
                if (isNsfw() && !e.getTextChannel().isNSFW()) {
                    return "_";
                }
                if (isThreadOnly() || isPrivateUseOnly())
                    return "_";
                return null;
            }
        return !(isGuildOnly() || isThreadOnly()) ? null : "_";
    }

    private boolean isNameCall(@NotNull MessageReceivedEvent event, String prefix, boolean reactToMention) {
        String msg = event.getMessage().getContentRaw().toLowerCase();
        String mention = event.getJDA().getSelfUser().getAsMention();

        return msg.startsWith(prefix + name) ||
                (reactToMention && (msg.startsWith(mention + name) || msg.startsWith(mention + " " + name))) ||
                msg.startsWith(name) && !event.isFromGuild();
    }

    private boolean isAliasCall(@NotNull MessageReceivedEvent event, String prefix, boolean reactToMention) {
        return getUsedAlias(event, prefix, reactToMention) != null;
    }

    @Nullable
    final String getUsedAlias(@NotNull MessageReceivedEvent event, String prefix, boolean reactToMention) {
        String msg = event.getMessage().getContentRaw().toLowerCase();
        String mention = event.getJDA().getSelfUser().getAsMention();

        for (String alias : aliases) {
            if (msg.startsWith(prefix + alias) ||
                    (reactToMention && (msg.startsWith(mention + alias) || msg.startsWith(mention + " " + alias))) ||
                    msg.startsWith(alias) && !event.isFromGuild()) {
                return alias;
            }
        }
        return null;
    }

    private boolean checkCommand(@NotNull MessageReceivedEvent event, String prefix, boolean reactToMention) {
        return isNameCall(event, prefix, reactToMention) || isAliasCall(event, prefix, reactToMention);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private @UnmodifiableView Map<String, IArgument> parseArgs(
            @NotNull List<String> args,
            MessageReceivedEvent event
    ) throws InvalidValueException, MissingArgumentException {
        Map<String, IArgument> objArgs = new HashMap<>();
        boolean isLongLastArg = args.size() > arguments.size();
        for (int i = 0; i < arguments.size(); i++) {
            boolean isLast = i + 1 < arguments.size();
            IArgument argument = arguments.get(i);
            String arg = null;
            if (i < args.size()) arg = args.get(i);

            if (arg == null) {
                checkResult(event, argument, argument.validateNull(null, event));
                argument.setValue(argument.getDefaultValue());
            } else {
                checkResult(argument, argument.validateNull(arg, event), event);
                checkResult(argument, argument.validate(event, arg), event);
                argument.setValue(argument.parse(event, arg));
            }
            objArgs.put(argument.getName(), argument);
        }

        return Collections.unmodifiableMap(objArgs);
    }

    private void checkResult(MessageReceivedEvent event, IArgument argument, String string)
            throws MissingArgumentException {
        if (string != null && !string.equals("") && !string.trim().equals(""))
            throw new MissingArgumentException(this, argument, event);
    }

    private void checkResult(IArgument arguments, String string, MessageReceivedEvent event) throws InvalidValueException {
        if (string != null && !string.equals("") && !string.trim().equals(""))
            throw new InvalidValueException(arguments, string, this, event);
    }

    protected final void sendReply(@NotNull MessageReceivedEvent event, @NotNull String reply) {
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();

        if (event.isFromGuild()) {
            if (PermissionUtil.checkPermission(
                    event.getGuildChannel().getPermissionContainer(),
                    event.getGuild().getSelfMember(),
                    Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND)) {
                message.reply(reply).queue();
                return;
            }
            Member member = event.getMember();
            if (PermissionUtil.checkPermission(
                    event.getGuildChannel().getPermissionContainer(),
                    event.getGuild().getSelfMember(),
                    Permission.MESSAGE_SEND)) {
                if (reply.length() > 1) {
                    reply = Character.toLowerCase(reply.charAt(0)) + reply.substring(1);
                } else if (reply.length() == 1) {
                    reply = reply.toLowerCase(Locale.ROOT);
                }
                channel.sendMessage(Objects.requireNonNull(event.getMember()).getAsMention() + ", " + reply).queue();
                return;
            }
            @NotNull final String finalReply = reply;
            event.getAuthor().openPrivateChannel()
                    .flatMap(c -> c.sendMessage(finalReply))
                    .queue();
        }

        channel.sendMessage(reply).queue();
    }

    protected final void sendReply(@NotNull MessageReceivedEvent event, @NotNull EmbedBuilder reply) {
        sendReply(event, reply.build());
    }

    protected final void sendReply(@NotNull MessageReceivedEvent event, @NotNull MessageEmbed reply) {
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();

        if (event.isFromGuild()) {
            if (PermissionUtil.checkPermission(
                    event.getGuildChannel().getPermissionContainer(),
                    event.getGuild().getSelfMember(),
                    Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND)) {
                message.replyEmbeds(reply).queue();
                return;
            }
            Member member = event.getMember();
            if (PermissionUtil.checkPermission(
                    event.getGuildChannel().getPermissionContainer(),
                    event.getGuild().getSelfMember(),
                    Permission.MESSAGE_SEND)) {
                channel.sendMessageEmbeds(reply).queue();
                return;
            }
            event.getAuthor().openPrivateChannel()
                    .flatMap(c -> c.sendMessageEmbeds(reply))
                    .queue();
        }

        channel.sendMessageEmbeds(reply).queue();
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getDescription() {
        return description;
    }

    public String getName(Event event) {
        return getName();
    }

    public String getGroup(Event event) {
        return getGroup();
    }

    public String getDescription(Event event) {
        return getDescription();
    }

    public String getDetails(Event event) {
        return getDetails();
    }

    public List<IArgument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public List<String> getExamples() {
        return Collections.unmodifiableList(examples);
    }

    public IArgument getArgument(String name) {
        return arguments.stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Throttling getThrottling() {
        return throttling;
    }

    protected Command setThrottling(Throttling throttling) {
        this.throttling = throttling;
        return this;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    protected Command setNsfw() {
        this.nsfw = true;
        return this;
    }

    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }

    public List<Permission> getClientPermissions() {
        return Collections.unmodifiableList(clientPermissions);
    }

    public List<Permission> getMemberPermissions() {
        return Collections.unmodifiableList(memberPermissions);
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    protected Command setGuildOnly() {
        this.guildOnly = true;
        return this;
    }

    public boolean isRunInThread() {
        return runInThread;
    }

    protected Command setNotRunInThreads() {
        this.runInThread = false;
        return this;
    }

    public boolean isThreadOnly() {
        return threadOnly;
    }

    protected Command setThreadOnly() {
        this.threadOnly = true;
        return this;
    }

    public boolean isPrivateUseOnly() {
        return privateUseOnly;
    }

    protected Command setPrivateUseOnly() {
        this.privateUseOnly = true;
        return this;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getDetails() {
        return details;
    }

    public Command setDetails(String details) {
        this.details = details;
        return this;
    }

    public final @NotNull String anyUsage(Event event) {
        String prefix = CommandEngine.getInstance().getPrefix(event);
        boolean react = CommandEngine.getInstance().isReactToMention(event);
        String args = String.join(" ", getArguments().stream().map(Command::mapArgumentUsage).toList());
        List<String> usages = new ArrayList<>();
        usages.add(prefix + getName(event) + " " + args);
        if (react) {
            usages.add("@" + event.getJDA().getSelfUser().getName() + getName(event) + " " + args);
        }
        if (event instanceof MessageReceivedEvent e && !e.isFromGuild()) {
            usages.add(getName(event) + " " + args);
        } else if (event instanceof SlashCommandInteractionEvent e && !e.isFromGuild()) {
            usages.add(getName(event) + " " + args);
        }

        return "`" +
                String.join(LocalizedFormat.format("NormalText_Or", event, "` ", " `").toLowerCase(), usages) +
                "`";
    }

    public String usage(String arg, String prefix, @NotNull Event event) {
        prefix = prefix == null ? CommandEngine.getInstance().getPrefix(event) : prefix;
        return String.format("%s%s %s", prefix, getName(event), arg);
    }

    public abstract void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args);
}
