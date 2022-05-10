package org.EricRamirezS.jdacommando.command;

import edu.rice.cs.util.ArgumentTokenizer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.EricRamirezS.jdacommando.command.exceptions.InvalidValueException;
import org.EricRamirezS.jdacommando.command.exceptions.MissingArgumentException;
import org.EricRamirezS.jdacommando.command.types.Argument;
import org.EricRamirezS.jdacommando.command.types.IntegerArgument;
import org.EricRamirezS.jdacommando.command.types.StringArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

@SuppressWarnings("rawtypes")
public abstract class Command extends ListenerAdapter implements PermissionsName {

    private Throttling throttling;
    private boolean nsfw = false;
    private boolean privateUseOnly = false;
    private boolean guildOnly = false;
    private boolean runInThread = false;
    private boolean threadOnly = false;
    private final String name;
    private final String group;
    private final String description;
    private final List<Argument> arguments = new ArrayList<>();
    private final List<String> aliases = new ArrayList<>();
    private final List<Permission> clientPermissions = new ArrayList<>();
    private final List<Permission> memberPermissions = new ArrayList<>();

    protected Command(@NotNull String name, @NotNull String group, String description, Argument... args) throws Exception {
        this.name = name.toLowerCase(Locale.ROOT);
        this.group = group.toLowerCase(Locale.ROOT);
        this.description = description;
        this.arguments.addAll(Arrays.asList(args));
        Engine.getInstance().addCommand(this);
    }

    protected Command setThrottling(Throttling throttling) {
        this.throttling = throttling;
        return this;
    }

    protected Command setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
        return this;
    }

    protected final Command addAliases(String @NotNull ... aliases) throws Exception {
        for (String alias : aliases) {
            Engine.getInstance().addAlias(alias.toLowerCase(Locale.ROOT), this);
            this.aliases.add(alias.toLowerCase(Locale.ROOT));
        }
        return this;
    }

    protected Command addClientPermissions(Permission... permissions) {
        clientPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    protected Command addMemberPermissions(Permission... permissions) {
        memberPermissions.addAll(Arrays.asList(permissions));
        return this;
    }


    public Command setGuildOnly(boolean guildOnly) {
        this.guildOnly = guildOnly;
        return this;
    }

    public Command setPrivateUseOnly(boolean privateUseOnly) {
        this.privateUseOnly = privateUseOnly;
        return this;
    }

    public Command setRunInThread(boolean runInThread) {
        this.runInThread = runInThread;
        return this;
    }

    public Command setThreadOnly(boolean threadOnly) {
        this.threadOnly = threadOnly;
        return this;
    }

    protected boolean shouldRun(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return false;
        if (event.isFromThread()) {
            if (isRunInThread() || !isPrivateUseOnly())
                return checkCommand(event, Engine.getInstance().getPrefix(event), Engine.getInstance().isReactToMention());
            return false;
        } else if (event.isFromGuild()) {
            if (isThreadOnly() || isPrivateUseOnly())
                return false;
            return checkCommand(event, Engine.getInstance().getPrefix(event), Engine.getInstance().isReactToMention());
        }
        return !isGuildOnly() && !isThreadOnly();

    }


    public abstract void run(@NotNull MessageReceivedEvent event, Map<String, Argument> args);

    private @Nullable String checkClientPermissions(MessageReceivedEvent event) {
        for (Permission per : clientPermissions) {
            if (!PermissionUtil.checkPermission(event.getGuild().getSelfMember(), per)) {
                return MessageFormat
                        .format("Necesito el permiso \"{0}\" para que el comando `{1}` funcione.",
                                Engine.getInstance().getString(NAME.get(per),
                                        Engine.getInstance().getLanguage(event)),
                                name);
            }
        }
        return null;
    }

    private @Nullable String checkMemberPermissions(MessageReceivedEvent event) {
        for (Permission per : memberPermissions) {
            Member member = event.getMember();
            if (member == null) return null;
            if (!PermissionUtil.checkPermission(member, per)) {
                return MessageFormat
                        .format("Necesitas el permiso \"{0}\" para que el comando `{1}` funcione.",
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
    protected String hasPermission(MessageReceivedEvent event) {
        return null;
    }

    @Override
    public final void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);

        if (event.isFromGuild()) {
            if (event.isFromThread()) {
                onGuildThreadMessageReceived(event);
            } else {
                onGuildMessageReceived(event);
            }
        } else {
            onDirectMessageReceived(event);
        }
    }

    public void onDirectMessageReceived(@NotNull MessageReceivedEvent event) {
        onGuildMessageReceived(event);
    }

    public void onGuildThreadMessageReceived(@NotNull MessageReceivedEvent event) {
        onGuildMessageReceived(event);
    }

    public void onGuildMessageReceived(@NotNull MessageReceivedEvent event) {
        // Verify that the command is in the base format expected by the bot
        if (!shouldRun(event)) return;

        // Verifying that the command is on an appropriate channel if it is NSFW
        if (nsfw && !event.getTextChannel().isNSFW()) {
            sendReply(event, MessageFormat.format("El comando `{0}` solo puede usarse en canales NSFW.", name));
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
                sendReply(event, "El comando ha superado el número máximo de usos, por favor intentelo más tarde.");
            }
        }
        String arguments = event.getMessage().getContentRaw().substring((Engine.getInstance().getPrefix(event) + name).length()).trim();
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

    private String checkPermissions(MessageReceivedEvent event) {
        String error = checkClientPermissions(event);
        if (error != null) return error;
        error = checkMemberPermissions(event);
        if (error != null) return error;
        error = hasPermission(event);
        return error;
    }

    private boolean checkCommand(@NotNull MessageReceivedEvent event, String prefix, boolean reactToMention) {
        String msg = event.getMessage().getContentRaw().toLowerCase();
        String mention = event.getGuild().getSelfMember().getAsMention();

        if (msg.startsWith(prefix + name) || (reactToMention && msg.startsWith(mention + name))) {
            return true;
        }
        for (String alias : aliases) {

            if (msg.startsWith(prefix + alias) ||
                    (reactToMention && msg.startsWith(mention + alias))) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private Map<String, Argument> parseArgs(
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
        }

        return objArgs;
    }

    private void checkResult(MessageReceivedEvent event, Argument argument, String string) throws MissingArgumentException {
        if (string != null && !string.equals("") && !string.trim().equals(""))
            throw new MissingArgumentException(argument, event);
    }

    private void checkResult(Argument arguments, String string) throws InvalidValueException {
        if (string != null && !string.equals("") && !string.trim().equals(""))
            throw new InvalidValueException(arguments, string);
    }

    private void sendReply(@NotNull MessageReceivedEvent event, @NotNull String reply) {
        PrivateChannel privateChannel = event.getPrivateChannel();
        MessageChannel channel = event.getChannel();
        GuildMessageChannel guildChannel = event.getGuildChannel();
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
        return arguments;
    }

    public Throttling getThrottling() {
        return throttling;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<Permission> getClientPermissions() {
        return clientPermissions;
    }

    public List<Permission> getMemberPermissions() {
        return memberPermissions;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    public boolean isRunInThread() {
        return runInThread;
    }

    public boolean isThreadOnly() {
        return threadOnly;
    }

    public boolean isPrivateUseOnly() {
        return privateUseOnly;
    }
}
