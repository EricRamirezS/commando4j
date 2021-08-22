package org.EricRamirezS.jdacommando.command;

import edu.rice.cs.util.ArgumentTokenizer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.EricRamirezS.jdacommando.command.exceptions.MissingArgumentException;
import org.EricRamirezS.jdacommando.command.types.Argument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

@SuppressWarnings("rawtypes")
public abstract class Command extends ListenerAdapter implements PermissionsName {

    private Throttling throttling;
    private boolean nsfw;
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
        CommandConfig.addCommand(this);
    }

    protected void setThrottling(Throttling throttling) {
        this.throttling = throttling;
    }

    protected void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    protected final void addAliases(String @NotNull ... aliases) throws Exception {
        for (String alias : aliases) {
            CommandConfig.addAlias(alias.toLowerCase(Locale.ROOT), this);
            this.aliases.add(alias.toLowerCase(Locale.ROOT));
        }
    }

    protected boolean shouldRun(@NotNull GuildMessageReceivedEvent event){
        if (event.getAuthor().isBot())
            return false;
        if (!checkCommand(event, CommandConfig.getPrefix(), CommandConfig.isReactToMention()))
            return false;
        return true;
    }
    public abstract void run(@NotNull GuildMessageReceivedEvent event, Map<String, Argument> args);

    protected void addClientPermissions(Permission... permissions) {
        clientPermissions.addAll(Arrays.asList(permissions));
    }

    protected void addMemberPermissions(Permission... permissions) {
        memberPermissions.addAll(Arrays.asList(permissions));
    }

    private @Nullable String checkClientPermissions(GuildMessageReceivedEvent event) {
        for (Permission per : clientPermissions) {
            if (!PermissionUtil.checkPermission(event.getChannel(), event.getGuild().getSelfMember(), per)) {
                return MessageFormat
                        .format("Necesito el permiso \"{0}\" para que el comando `{1}` funcione.",
                                NAME.get(per),
                                name);
            }
        }
        return null;
    }

    private @Nullable String checkMemberPermissions(GuildMessageReceivedEvent event) {
        for (Permission per : memberPermissions) {
            Member member = event.getMember();
            if (member == null) return null;
            if (!PermissionUtil.checkPermission(event.getChannel(), member, per)) {
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
    protected String hasPermission(GuildMessageReceivedEvent event) {
        return null;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);

        if (!shouldRun(event)) return;

        // Verificando que el comando no sea NSFW
        if (nsfw && !event.getChannel().isNSFW()) {
            sendReply(event, MessageFormat.format("El comando `{0}` solo puede usarse en canales NSFW.", name));
            return;
        }

        // Verificando el que cliente y el usuario tengan permisos para ejecutar este comando.
        String error = checkPermissions(event);
        if (error != null) {
            sendReply(event, error);
            return;
        }

        // Verificando que el comando no ha superado su límite de usos, si existe límite.
        if (throttling != null) {
            if (throttling.check()) {
                sendReply(event, "El comando ha superado el número máximo de usos, por favor intentelo más tarde.");
            }
        }
        String arguments = event.getMessage().getContentRaw().substring((CommandConfig.getPrefix() + name).length());
        List<String> args = ArgumentTokenizer.tokenize(arguments);
        Map<String, Argument> objArgs = new HashMap<>();
        try {
            objArgs = parseArgs(args);
        } catch (MissingArgumentException ex) {
            sendReply(event, ex.getMessage());
            return;
        }

        run(event, objArgs);
    }

    protected String checkPermissions(GuildMessageReceivedEvent event) {
        String error = checkClientPermissions(event);
        if (error != null) return error;
        error = checkMemberPermissions(event);
        if (error != null) return error;
        error = hasPermission(event);
        return error;
    }

    private boolean checkCommand(@NotNull GuildMessageReceivedEvent event, String prefix, boolean reactToMention) {
        String msg = event.getMessage().getContentRaw().toLowerCase();
        String mention = event.getGuild().getSelfMember().getAsMention();

        if (msg.startsWith(prefix + name) ||
                (reactToMention && msg.startsWith(mention + name))) {
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

    @NotNull
    private Map<String, Argument>  parseArgs(List<String> args) throws MissingArgumentException {
        Map<String, Argument> objArgs = new HashMap<>();
        for (int i = 0; i < arguments.size(); i++) {
            //TODO: parse Arg
            if (arguments.get(i).getDefaultValue() == null)
                throw new MissingArgumentException(arguments.get(i));
        }

        return objArgs;
    }



    private void sendReply(@NotNull GuildMessageReceivedEvent event, @NotNull String reply) {
        if (PermissionUtil.checkPermission(event.getChannel(), event.getGuild().getSelfMember(), Permission.MESSAGE_WRITE)) {
            if (PermissionUtil.checkPermission(event.getChannel(), event.getGuild().getSelfMember(), Permission.MESSAGE_HISTORY)) {
                event.getMessage()
                        .reply(reply)
                        .queue();
            } else {
                Member member = event.getMember();
                if (member != null) {
                    if (reply.length() > 1) {
                        reply = Character.toLowerCase(reply.charAt(0)) + reply.substring(1);
                    } else if (reply.length() == 1) {
                        reply = reply.toLowerCase(Locale.ROOT);
                    }
                    event.getChannel()
                            .sendMessage(event.getMember().getAsMention() + ", " + reply)
                            .queue();
                } else {
                    event.getMessage().getChannel().sendMessage(reply).queue();
                }
            }
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
}
