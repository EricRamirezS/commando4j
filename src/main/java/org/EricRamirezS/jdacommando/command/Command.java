package org.EricRamirezS.jdacommando.command;

import edu.rice.cs.util.ArgumentTokenizer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedArgumentNameException;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedNameException;
import org.EricRamirezS.jdacommando.command.exceptions.InvalidValueException;
import org.EricRamirezS.jdacommando.command.exceptions.MissingArgumentException;
import org.EricRamirezS.jdacommando.command.types.Argument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import javax.naming.InvalidNameException;
import java.util.*;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@SuppressWarnings("rawtypes")
public abstract class Command implements PermissionsName {

    private final String name;
    private final String group;
    private final String description;
    private final List<Argument> arguments = new ArrayList<>();
    private final List<String> aliases = new ArrayList<>();
    private final List<Permission> clientPermissions = new ArrayList<>();
    private final List<Permission> memberPermissions = new ArrayList<>();
    private Throttling throttling;
    private boolean nsfw = false;
    private boolean privateUseOnly = false;
    private boolean guildOnly = false;
    private boolean runInThread = true;
    private boolean threadOnly = false;

    protected Command(@NotNull String name, @NotNull String group, String description, Argument... args)
            throws DuplicatedNameException, InvalidNameException, DuplicatedArgumentNameException {
        this.name = name.toLowerCase(Locale.ROOT);
        this.group = group.toLowerCase(Locale.ROOT);
        this.description = description;
        this.arguments.addAll(Arrays.asList(args));
        List<Argument> unique = arguments.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(Argument::getName))),
                        ArrayList::new));
        if (arguments.size() != unique.size())
            throw new DuplicatedArgumentNameException();
        CommandEngine.getInstance().addCommand(this);
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

    protected final Command addAliases(String @NotNull ... aliases) throws Exception {
        for (String alias : aliases) {
            CommandEngine.getInstance().addAlias(alias.toLowerCase(Locale.ROOT), this);
            this.aliases.add(alias.toLowerCase(Locale.ROOT));
        }
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

    public abstract void run(@NotNull MessageReceivedEvent event, @UnmodifiableView @NotNull Map<String, Argument> args);

    protected final @Nullable String checkClientPermissions(Event event) {
        for (Permission per : clientPermissions) {
            Member member = getSelfMember(event);
            if (member == null) return null;
            if (!PermissionUtil.checkPermission(member, per)) {
                return LocalizedFormat
                        .format("Command_MissingClientPermission",
                                CommandEngine.getInstance().getString(NAME.get(per),
                                        CommandEngine.getInstance().getLanguage(event)),
                                event,
                                name);
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
                                NAME.get(per),
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
    protected String hasPermission(Event event) {
        return null;
    }

    public final void onDirectMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!shouldRun(event)) return;

        // Verifying that the command has not exceeded its usage limit, if there is a limit.
        if (throttling != null) {
            if (throttling.check()) {
                sendReply(event, LocalizedFormat.format("Command_Throttling", event));
            }
        }

        String prefix = CommandEngine.getInstance().getPrefix(event);
        String messageRaw = event.getMessage().getContentRaw();
        if (messageRaw.startsWith(prefix)) {
            messageRaw = messageRaw.replaceFirst(prefix, "");
        }

        String commandName = getUsedAlias(event, prefix, CommandEngine.getInstance().isReactToMention());
        commandName = commandName == null ? name : commandName;
        String arguments = messageRaw.substring(commandName.length()).trim();
        List<String> args = ArgumentTokenizer.tokenize(arguments, getArguments().size());
        Map<String, Argument> objArgs;
        try {
            objArgs = parseArgs(args, event);
        } catch (InvalidValueException | MissingArgumentException ex) {
            sendReply(event, ex.getMessage());
            return;
        }

        run(event, objArgs);
    }

    public final void onGuildThreadMessageReceived(@NotNull MessageReceivedEvent event) {
        onGuildMessageReceived(event);
    }

    public final void onGuildMessageReceived(@NotNull MessageReceivedEvent event) {
        // Verify that the command is in the base format expected by the bot
        if (!shouldRun(event)) return;
        // Verifying that the command is on an appropriate channel if it is NSFW
        if (nsfw && !event.getTextChannel().isNSFW()) {
            sendReply(event, LocalizedFormat.format("Command_NSFW", event, name));
            return;
        }

        // Verifying that the client and user have permissions to execute this command.
        String error = checkPermissions(event);
        if (error != null) {
            sendReply(event, error);
            return;
        }

        // Verifying that the command has not exceeded its usage limit, if there is a limit.
        if (throttling != null) {
            if (throttling.check()) {
                sendReply(event, LocalizedFormat.format("Command_Throttling", event));
            }
        }

        Map<String, Argument> objArgs = parseArguments(event);

        if (objArgs != null) run(event, objArgs);
    }

    private @Nullable @UnmodifiableView Map<String, Argument> parseArguments(MessageReceivedEvent event) {
        String prefix = CommandEngine.getInstance().getPrefix(event);
        String commandName = getUsedAlias(event, prefix, CommandEngine.getInstance().isReactToMention());
        commandName = commandName == null ? name : commandName;
        String arguments = event.getMessage().getContentRaw()
                .substring((CommandEngine.getInstance().getPrefix(event) + commandName).length()).trim();

        List<String> args = ArgumentTokenizer.tokenize(arguments, getArguments().size());
        Map<String, Argument> objArgs;
        try {
            return parseArgs(args, event);
        } catch (InvalidValueException | MissingArgumentException ex) {
            sendReply(event, ex.getMessage());
            return null;
        }
    }

    private String checkPermissions(Event event) {
        String error = checkClientPermissions(event);
        if (error != null) return error;
        error = checkMemberPermissions(event);
        if (error != null) return error;
        error = hasPermission(event);
        return error;
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
    private @UnmodifiableView Map<String, Argument> parseArgs(
            @NotNull List<String> args,
            MessageReceivedEvent event
    ) throws InvalidValueException, MissingArgumentException {
        Map<String, Argument> objArgs = new HashMap<>();
        boolean isLongLastArg = args.size() > arguments.size();
        for (int i = 0; i < arguments.size(); i++) {
            boolean isLast = i + 1 < arguments.size();
            Argument argument = arguments.get(i);
            String arg = null;
            if (i < args.size()) arg = args.get(i);

            if (arg == null) {
                checkResult(event, argument, argument.validateNull(null, event));
                argument.setValue(argument.getDefaultValue());
            } else {
                checkResult(argument, argument.validateNull(arg, event));
                checkResult(argument, argument.validate(event, arg));
                argument.setValue(argument.parse(event, arg));
            }
            objArgs.put(argument.getName(), argument);
        }

        return Collections.unmodifiableMap(objArgs);
    }

    private void checkResult(MessageReceivedEvent event, Argument argument, String string)
            throws MissingArgumentException {
        if (string != null && !string.equals("") && !string.trim().equals(""))
            throw new MissingArgumentException(this, argument, event);
    }

    private void checkResult(Argument arguments, String string) throws InvalidValueException {
        if (string != null && !string.equals("") && !string.trim().equals(""))
            throw new InvalidValueException(arguments, string);
    }

    private void sendReply(@NotNull MessageReceivedEvent event, @NotNull String reply) {
        PrivateChannel privateChannel = event.getPrivateChannel();
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();

        if (event.isFromGuild() &&
                PermissionUtil.checkPermission(
                        event.getGuildChannel().getPermissionContainer(),
                        event.getGuild().getSelfMember(),
                        Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND)) {
            message.reply(reply).queue();
            return;
        }

        Member member = event.getMember();
        if (member != null) {
            if (reply.length() > 1) {
                reply = Character.toLowerCase(reply.charAt(0)) + reply.substring(1);
            } else if (reply.length() == 1) {
                reply = reply.toLowerCase(Locale.ROOT);
            }
            channel.sendMessage(event.getMember().getAsMention() + ", " + reply).queue();
        } else {
            privateChannel.sendMessage(reply).queue();
        }
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

    public List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    Argument getArgument(String name) {
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

    protected Command setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
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

    protected Command setGuildOnly(boolean guildOnly) {
        this.guildOnly = guildOnly;
        return this;
    }

    public boolean isRunInThread() {
        return runInThread;
    }

    protected Command setRunInThread(boolean runInThread) {
        this.runInThread = runInThread;
        return this;
    }

    public boolean isThreadOnly() {
        return threadOnly;
    }

    protected Command setThreadOnly(boolean threadOnly) {
        this.threadOnly = threadOnly;
        return this;
    }

    public boolean isPrivateUseOnly() {
        return privateUseOnly;
    }

    protected Command setPrivateUseOnly(boolean privateUseOnly) {
        this.privateUseOnly = privateUseOnly;
        return this;
    }

    @Override
    public String toString() {
        return name;
    }
}
