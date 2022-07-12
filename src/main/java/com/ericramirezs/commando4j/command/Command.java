/*
 *
 *    Copyright 2022 Eric Bastian Ramírez Santis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ericramirezs.commando4j.command;

import com.ericramirezs.commando4j.CommandEngine;
import com.ericramirezs.commando4j.ICommandEngine;
import com.ericramirezs.commando4j.Slash;
import com.ericramirezs.commando4j.arguments.IArgument;
import com.ericramirezs.commando4j.exceptions.DuplicatedArgumentNameException;
import com.ericramirezs.commando4j.exceptions.InvalidValueException;
import com.ericramirezs.commando4j.exceptions.MissingArgumentException;
import com.ericramirezs.commando4j.util.LocalizedFormat;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * Implementation of ICommand interface.
 */
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
    private String details = "";
    private com.ericramirezs.commando4j.command.Throttling throttling;
    private boolean nsfw;
    private boolean privateUseOnly;
    private boolean guildOnly;
    private boolean runInThread = true;
    private boolean threadOnly;

    /**
     * Creates a new Command Object. Run method is required to be implemented.
     * <p>
     * You may implement {@link com.ericramirezs.commando4j.Slash} to include this command
     * as a Discord's Slash Command
     * </p>
     *
     * @param name        Command name. <strong>It must be unique.</strong>
     * @param group       Group's name. It is used by the default help command to categorize commands.
     *                    It has no real effects in the engine.
     * @param description Command's description, It is used by the default help command to explain what the command do.
     *                    It has no real effects in the engine.
     * @param args        List of Arguments accepted by this command.
     * @throws DuplicatedArgumentNameException Thrown when another Command with the same name has already been
     *                                         registered in the Engine
     */
    public Command(@NotNull final String name, @NotNull final String group, final String description, final IArgument... args)
            throws DuplicatedArgumentNameException {
        this.name = name.toLowerCase(Locale.ROOT);
        this.group = group.toLowerCase(Locale.ROOT);
        this.description = description;
        this.arguments.addAll(Arrays.asList(args));
        final List<IArgument> unique = getArguments().stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(IArgument::getName))),
                        ArrayList::new));
        if (getArguments().size() != unique.size())
            throw new DuplicatedArgumentNameException();
        checkWarnings();
    }

    /**
     * Command execution after all validations has been passed.
     *
     * @param event Discord event that triggered this command call
     * @param args  Already parsed arguments, Map's keys are the argument's name
     */
    public abstract void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args);

    /**
     * Set a detailed description of this command
     *
     * @param details detailed description
     * @return a reference to this object.
     */
    public Command setDetails(final String details) {
        this.details = details;
        return this;
    }

    /**
     * Add alternative names to call this command.
     *
     * @param aliases list of aliases or alternative names.
     * @return a reference to this object.
     */
    protected final Command addAliases(final String @NotNull ... aliases) {
        for (final String alias : aliases) {
            this.aliases.add(alias.toLowerCase(Locale.ROOT));
        }
        return this;
    }

    /**
     * Add a usage example to display in help command.
     *
     * @param example usage example.
     * @return a reference to this object.
     */
    protected final Command addExample(final String example) {
        this.examples.add(example);
        return this;
    }

    /**
     * Add multiple usage examples to display in help command.
     *
     * @param example usage examples list.
     * @return a reference to this object.
     */
    protected final Command addExamples(final String... example) {
        this.examples.addAll(Arrays.asList(example));
        return this;
    }

    /**
     * Add Discord Permissions required by the bot in order to run this command.
     * <p>
     * i.e. SEND_MESSAGE in order to send a message to a Discord Channel.
     * </p>
     *
     * @param permissions Discord Permissions list.
     * @return a reference to this object.
     * @see net.dv8tion.jda.api.Permission
     */
    protected final Command addClientPermissions(final Permission... permissions) {
        clientPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    /**
     * Add Discord Permissions required by the user in order to run this command.
     * <p>
     * i.e. ADMINISTRATOR in order to prevent normal users to modify the bot setting in this server.
     * </p>
     *
     * @param permissions Discord Permissions list.
     * @return a reference to this object.
     * @see net.dv8tion.jda.api.Permission
     */
    protected final Command addMemberPermissions(final Permission... permissions) {
        memberPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    /**
     * Check if the command should be run in the current Message Channel.
     *
     * @param event Discord Event that triggered this function call.
     * @return true if the command is compatible with the current Message Channel.
     */
    protected final boolean shouldRun(@NotNull final MessageReceivedEvent event) {
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

    /**
     * Check if the Client has the required permissions to run this command.
     *
     * @param event Discord Event that triggered this function call.
     * @return Error message with the first missing Permission find. Null if no Permission is missing.
     * @see net.dv8tion.jda.api.Permission
     */
    protected final @Nullable String checkClientPermissions(final Event event) {
        for (final Permission per : clientPermissions) {
            final Member member = getSelfMember(event);
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

    /**
     * Check if the User has the required permissions to run this command.
     *
     * @param event Discord Event that triggered this function call.
     * @return Error message with the first missing Permission find. Null if no Permission is missing.
     * @see net.dv8tion.jda.api.Permission
     */
    protected final @Nullable String checkMemberPermissions(final Event event) {
        for (final Permission per : memberPermissions) {
            final Member member = getMember(event);
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
     * @param event Message Event that triggered this function Call
     * @return null if there's no permission issues with this command,
     * a string with the permission problem if the user or client cannot execute the command.
     */
    protected String hasPermission(final MessageReceivedEvent event) {
        return hasPermission((Event) event);
    }

    /**
     * Custom check of permission
     *
     * @param event Slash Command Event that triggered this function Call
     * @return null if there's no permission issues with this command,
     * a string with the permission problem if the user or client cannot execute the command.
     */
    protected String hasPermission(final SlashCommandInteractionEvent event) {
        return hasPermission((Event) event);
    }

    /**
     * Custom check of permission. Override to add complex or customized usage validations.
     *
     * @param event Generic Event that triggered this function Call
     * @return null if there's no permission issues with this command,
     * a string with the permission problem if the user or client cannot execute the command.
     */
    @SuppressWarnings("SameReturnValue")
    protected String hasPermission(final Event event) {
        return null;
    }

    /**
     * Reply to the Original Message that executed this command.
     * <p>
     * If the command was called inside a Guild
     * </p>
     * <ul>
     *      <li>
     *          If the bot has MESSAGE_HISTORY and MESSAGE_SEND permissions,
     *          it will reply referencing the original Message
     *      </li>
     *      <li>
     *          If the bot has MESSAGE_SEND permissions,
     *          it will reply mentioning the author of the original message.
     *      </li>
     *      <li>
     *          If the bot doesn't has any of the previous permissions,
     *          it will send a DM to the author of the original message.
     *      </li>
     * </ul>
     *
     * @param event Discord event that triggered this function call
     * @param reply reply message
     * @see net.dv8tion.jda.api.Permission
     * @see net.dv8tion.jda.api.entities.Guild
     */
    public final void sendReply(@NotNull final MessageReceivedEvent event, @NotNull String reply) {
        final MessageChannel channel = event.getChannel();
        final Message message = event.getMessage();

        if (event.isFromGuild()) {
            if (PermissionUtil.checkPermission(
                    event.getGuildChannel().getPermissionContainer(),
                    event.getGuild().getSelfMember(),
                    Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND)) {
                message.reply(reply).queue();
                return;
            }
            final Member member = event.getMember();
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
                    .onErrorMap(throwable -> null) // Exception if User doesn't allow private messages.
                    .queue();
        } else {
            channel.sendMessage(reply).queue();
        }
    }

    /**
     * Reply to the Original Message that executed this command.
     * <p>
     * If the command was called inside a Guild
     * </p>
     * <ul>
     *      <li>
     *          If the bot has MESSAGE_HISTORY and MESSAGE_SEND permissions,
     *          it will reply referencing the original Message
     *      </li>
     *      <li>
     *          If the bot has MESSAGE_SEND permissions,
     *          it will reply mentioning the author of the original message.
     *      </li>
     *      <li>
     *          If the bot doesn't has any of the previous permissions,
     *          it will send a DM to the author of the original message.
     *      </li>
     * </ul>
     *
     * @param event Discord event that triggered this function call
     * @param reply EmbedBuilder message
     * @see net.dv8tion.jda.api.Permission
     * @see net.dv8tion.jda.api.entities.Guild
     * @see net.dv8tion.jda.api.EmbedBuilder
     */
    protected final void sendReply(@NotNull final MessageReceivedEvent event, @NotNull final EmbedBuilder reply) {
        sendReply(event, reply.build());
    }

    /**
     * Reply to the Original Message that executed this command.
     * <p>
     * If the command was called inside a Guild
     * </p>
     * <ul>
     *      <li>
     *          If the bot has MESSAGE_HISTORY and MESSAGE_SEND permissions,
     *          it will reply referencing the original Message
     *      </li>
     *      <li>
     *          If the bot has MESSAGE_SEND permissions,
     *          it will reply mentioning the author of the original message.
     *      </li>
     *      <li>
     *          If the bot doesn't has any of the previous permissions,
     *          it will send a DM to the author of the original message.
     *      </li>
     * </ul>
     *
     * @param event Discord event that triggered this function call
     * @param reply MessageEmbed
     * @see net.dv8tion.jda.api.Permission
     * @see net.dv8tion.jda.api.entities.Guild
     * @see net.dv8tion.jda.api.entities.MessageEmbed
     */
    protected final void sendReply(@NotNull final MessageReceivedEvent event, @NotNull final MessageEmbed reply) {
        final MessageChannel channel = event.getChannel();
        final Message message = event.getMessage();

        if (event.isFromGuild()) {
            if (PermissionUtil.checkPermission(
                    event.getGuildChannel().getPermissionContainer(),
                    event.getGuild().getSelfMember(),
                    Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND)) {
                message.replyEmbeds(reply).queue();
                return;
            }
            final Member member = event.getMember();
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
        } else {
            channel.sendMessageEmbeds(reply).queue();
        }
    }

    /**
     * Reply to the Original Message that executed this command.
     * <p>
     * If the command was called inside a Guild
     * </p>
     * <ul>
     *      <li>
     *          If the bot has MESSAGE_HISTORY and MESSAGE_SEND permissions,
     *          it will reply referencing the original Message
     *      </li>
     *      <li>
     *          If the bot has MESSAGE_SEND permissions,
     *          it will reply mentioning the author of the original message.
     *      </li>
     *      <li>
     *          If the bot doesn't has any of the previous permissions,
     *          it will send a DM to the author of the original message.
     *      </li>
     * </ul>
     *
     * @param event Discord event that triggered this function call
     * @param reply reply message
     * @see net.dv8tion.jda.api.Permission
     * @see net.dv8tion.jda.api.entities.Guild
     */
    protected final void sendReply(@NotNull final Event event, @NotNull final String reply) {
        if (event instanceof SlashCommandInteractionEvent) Slash.sendReply((SlashCommandInteractionEvent) event, reply);
        if (event instanceof MessageReceivedEvent) sendReply((MessageReceivedEvent) event, reply);
    }

    /**
     * Reply to the Original Message that executed this command.
     * <p>
     * If the command was called inside a Guild
     * </p>
     * <ul>
     *      <li>
     *          If the bot has MESSAGE_HISTORY and MESSAGE_SEND permissions,
     *          it will reply referencing the original Message
     *      </li>
     *      <li>
     *          If the bot has MESSAGE_SEND permissions,
     *          it will reply mentioning the author of the original message.
     *      </li>
     *      <li>
     *          If the bot doesn't has any of the previous permissions,
     *          it will send a DM to the author of the original message.
     *      </li>
     * </ul>
     *
     * @param event Discord event that triggered this function call
     * @param reply EmbedBuilder message
     * @see net.dv8tion.jda.api.Permission
     * @see net.dv8tion.jda.api.entities.Guild
     * @see net.dv8tion.jda.api.EmbedBuilder
     */
    protected final void sendReply(@NotNull final Event event, @NotNull final EmbedBuilder reply) {
        if (event instanceof SlashCommandInteractionEvent)
            Slash.sendReply((SlashCommandInteractionEvent) event, reply.build());
        if (event instanceof MessageReceivedEvent) sendReply((MessageReceivedEvent) event, reply.build());
    }

    /**
     * Reply to the Original Message that executed this command.
     * <p>
     * If the command was called inside a Guild
     * </p>
     * <ul>
     *      <li>
     *          If the bot has MESSAGE_HISTORY and MESSAGE_SEND permissions,
     *          it will reply referencing the original Message
     *      </li>
     *      <li>
     *          If the bot has MESSAGE_SEND permissions,
     *          it will reply mentioning the author of the original message.
     *      </li>
     *      <li>
     *          If the bot doesn't has any of the previous permissions,
     *          it will send a DM to the author of the original message.
     *      </li>
     * </ul>
     *
     * @param event Discord event that triggered this function call
     * @param reply MessageEmbed
     * @see net.dv8tion.jda.api.Permission
     * @see net.dv8tion.jda.api.entities.Guild
     * @see net.dv8tion.jda.api.entities.MessageEmbed
     */
    protected final void sendReply(@NotNull final Event event, @NotNull final MessageEmbed reply) {
        if (event instanceof SlashCommandInteractionEvent) Slash.sendReply((SlashCommandInteractionEvent) event, reply);
        if (event instanceof MessageReceivedEvent) sendReply((MessageReceivedEvent) event, reply);
    }

    /**
     * Reply to the Original Message that executed this command.
     * <p>
     * If the command was called inside a Guild
     * </p>
     * <ul>
     *      <li>
     *          If the bot has MESSAGE_HISTORY and MESSAGE_SEND permissions,
     *          it will reply referencing the original Message
     *      </li>
     *      <li>
     *          If the bot has MESSAGE_SEND permissions,
     *          it will reply mentioning the author of the original message.
     *      </li>
     *      <li>
     *          If the bot doesn't has any of the previous permissions,
     *          it will send a DM to the author of the original message.
     *      </li>
     * </ul>
     *
     * @param event     Discord event that triggered this function call
     * @param reply     reply message
     * @param ephemeral True, if this message should be invisible for other users.
     * @see net.dv8tion.jda.api.Permission
     * @see net.dv8tion.jda.api.entities.Guild
     */
    protected final void sendReply(@NotNull final SlashCommandInteractionEvent event, @NotNull final String reply
            , final boolean ephemeral) {
        Slash.sendReply(event, reply, ephemeral);
    }

    /**
     * Reply to the Original Message that executed this command.
     * <p>
     * If the command was called inside a Guild
     * </p>
     * <ul>
     *      <li>
     *          If the bot has MESSAGE_HISTORY and MESSAGE_SEND permissions,
     *          it will reply referencing the original Message
     *      </li>
     *      <li>
     *          If the bot has MESSAGE_SEND permissions,
     *          it will reply mentioning the author of the original message.
     *      </li>
     *      <li>
     *          If the bot doesn't has any of the previous permissions,
     *          it will send a DM to the author of the original message.
     *      </li>
     * </ul>
     *
     * @param event     Discord event that triggered this function call
     * @param reply     EmbedBuilder message
     * @param ephemeral True, if this message should be invisible for other users.
     * @see net.dv8tion.jda.api.Permission
     * @see net.dv8tion.jda.api.entities.Guild
     * @see net.dv8tion.jda.api.EmbedBuilder
     */
    protected final void sendReply(@NotNull final SlashCommandInteractionEvent event, @NotNull final EmbedBuilder reply
            , final boolean ephemeral) {
        Slash.sendReply(event, reply, ephemeral);
    }

    /**
     * Reply to the Original Message that executed this command.
     * <p>
     * If the command was called inside a Guild
     * </p>
     * <ul>
     *      <li>
     *          If the bot has MESSAGE_HISTORY and MESSAGE_SEND permissions,
     *          it will reply referencing the original Message
     *      </li>
     *      <li>
     *          If the bot has MESSAGE_SEND permissions,
     *          it will reply mentioning the author of the original message.
     *      </li>
     *      <li>
     *          If the bot doesn't has any of the previous permissions,
     *          it will send a DM to the author of the original message.
     *      </li>
     * </ul>
     *
     * @param event     Discord event that triggered this function call
     * @param reply     MessageEmbed
     * @param ephemeral True, if this message should be invisible for other users.
     * @see net.dv8tion.jda.api.Permission
     * @see net.dv8tion.jda.api.entities.Guild
     * @see net.dv8tion.jda.api.entities.MessageEmbed
     */
    protected final void sendReply(@NotNull final SlashCommandInteractionEvent event, @NotNull final MessageEmbed reply
            , final boolean ephemeral) {
        Slash.sendReply(event, reply, ephemeral);
    }

    /**
     * Set the throttling configuration for this command.
     *
     * @param throttling throttling configuration
     * @return a reference to this object.
     */
    protected Command setThrottling(final Throttling throttling) {
        this.throttling = throttling;
        return this;
    }

    /**
     * Mark this command to only be usable in channels marked as NSFW.
     * <p>
     * <strong>NOTE: </strong>If the command is called in a Private message, it will be treated as a NSFW channel.
     * </p>
     *
     * @return a reference to this object.
     */
    protected Command setNsfw() {
        this.nsfw = true;
        return this;
    }

    /**
     * Mark this command as Guild only.
     * <p>
     * The command will not be available inside a Private message.
     * </p>
     * <p>
     * The command will not be listed by the default help command
     * in a Private message, unless the argument all is included
     * </p>
     *
     * @return a reference to this object.
     */
    protected Command setGuildOnly() {
        this.guildOnly = true;
        return this;
    }

    /**
     * Mark this command as not compatible with Threads
     * <p>
     * The command will not be available to run inside Discord's threads
     * </p>
     * <p>
     * <strong>NOTE: </strong> this flag will remove Thread Only flag.
     * </p>
     *
     * @return a reference to this object.
     */
    protected Command setNotRunInThreads() {
        this.runInThread = false;
        this.threadOnly = false;
        return this;
    }

    /**
     * Mark this command as Thread only
     * <p>
     * The command will not be available to run in a normal Text Channel
     * </p>
     * <p>
     * The command will not be available inside a Private Message.
     * </p>
     * <p>
     * <strong>NOTE: </strong> this flag will remove the Not Run in threads flag.
     * </p>
     *
     * @return a reference to this object.
     */
    protected Command setThreadOnly() {
        this.threadOnly = true;
        this.runInThread = true;
        return this;
    }

    /**
     * Mark this command as Private Only
     * <p>
     * This command will not be able inside any Discord Server.
     * </p>
     * <p>
     * <strong>NOTE: </strong> this flag has priority over Guild only and Thread only tags.
     * </p>
     *
     * @return a reference to this object.
     */
    protected Command setPrivateUseOnly() {
        this.privateUseOnly = true;
        return this;
    }

    private static Member getSelfMember(final Event event) {
        if (event instanceof SlashCommandInteractionEvent) {
            final SlashCommandInteractionEvent slash = (SlashCommandInteractionEvent) event;
            return slash.isFromGuild() ? Objects.requireNonNull(slash.getGuild()).getSelfMember() : null;
        }
        if (event instanceof MessageReceivedEvent) {
            final MessageReceivedEvent command = (MessageReceivedEvent) event;
            return command.isFromGuild() ? Objects.requireNonNull(command.getGuild()).getSelfMember() : null;
        }
        return null;
    }

    private static Member getMember(final Event event) {
        if (event instanceof SlashCommandInteractionEvent) {
            final SlashCommandInteractionEvent slash = (SlashCommandInteractionEvent) event;
            return slash.isFromGuild() ? slash.getMember() : null;
        }
        if (event instanceof MessageReceivedEvent) {
            final MessageReceivedEvent command = (MessageReceivedEvent) event;
            return command.isFromGuild() ? command.getMember() : null;
        }
        return null;
    }

    private static @NotNull String mapArgumentUsage(@NotNull final IArgument a) {
        if (a.isRequired()) {
            return "<" + a.getName() + (a.getDefaultValue() == null ? ">" : "=" + a.getDefaultValue().toString() + ">");
        } else {
            return "[" + a.getName() + (a.getDefaultValue() == null ? "]" : "=" + a.getDefaultValue().toString() + "]");
        }
    }

    private void checkWarnings() {
        final ICommandEngine engine = CommandEngine.getInstance();
        if (getName().length() > 10)
            engine.logWarn(LocalizedFormat.format("Command_Warning_NameSize", getName(), getName().length()));
        if (getDescription().length() > 90)
            engine.logWarn(LocalizedFormat.format("Command_Warning_DescriptionSize", getName(), getDescription().length()));
        if (getDetails() != null && getDetails().length() > 1500)
            engine.logWarn(LocalizedFormat.format("Command_Warning_DetailsSize", getName(), getDetails().length()));
    }

    private @Nullable @UnmodifiableView Map<String, IArgument> parseArguments(final MessageReceivedEvent event) {
        final String prefix = CommandEngine.getInstance().getPrefix(event);

        final String messageRaw = removePrefix(
                prefix,
                event.getMessage().getContentRaw(),
                event.getJDA().getSelfUser().getAsMention());

        final List<String> args = getArgsList(event, prefix, messageRaw);

        Map<String, IArgument> objArgs;
        try {
            return parseArgs(args, event);
        } catch (final InvalidValueException | MissingArgumentException ex) {
            final String unable = LocalizedFormat.format("Command_Failed", event);
            sendReply(event, unable + "\n" + ex.getMessage());
            return null;
        } catch (final Exception ex) {
            CommandEngine.getInstance().logError(ex.getMessage() + "\n" + Arrays.stream(ex.getStackTrace())
                    .map(StackTraceElement::toString).collect(Collectors.joining("\n")));
            return null;
        }
    }

    private @NotNull String removePrefix(final String prefix, @NotNull final String message, final String mention) {
        if (message.startsWith(prefix)) {
            return message.replaceFirst(prefix, "");
        } else if (message.startsWith(mention)) {
            return message.replaceFirst(mention, "").trim();
        }
        return message;
    }

    private @NotNull List<String> getArgsList(final MessageReceivedEvent event, final String prefix, @NotNull final String messageRaw) {
        String commandName = getUsedAlias(event, prefix, CommandEngine.getInstance().isReactToMention());
        commandName = commandName == null ? name : commandName;
        final String arguments = messageRaw.substring(commandName.length()).trim();
        return ArgumentTokenizer.tokenize(arguments, getArguments().size());
    }

    private boolean isNameCall(@NotNull final MessageReceivedEvent event, final String prefix, final boolean reactToMention) {
        final String msg = event.getMessage().getContentRaw().toLowerCase();
        final String mention = event.getJDA().getSelfUser().getAsMention();

        return msg.startsWith(prefix + name) ||
                (reactToMention && (msg.startsWith(mention + name) || msg.startsWith(mention + " " + name))) ||
                msg.startsWith(name) && !event.isFromGuild();
    }

    private boolean isAliasCall(@NotNull final MessageReceivedEvent event, final String prefix, final boolean reactToMention) {
        return getUsedAlias(event, prefix, reactToMention) != null;
    }

    @Nullable
    private String getUsedAlias(@NotNull final MessageReceivedEvent event, final String prefix, final boolean reactToMention) {
        final String msg = event.getMessage().getContentRaw().toLowerCase();
        final String mention = event.getJDA().getSelfUser().getAsMention();
        final List<String> aliases = new ArrayList<String>() {{
            add(name);
        }};
        aliases.addAll(this.aliases);
        for (final String alias : aliases) {
            final boolean prefixCall = msg.startsWith(prefix + alias);
            final boolean mentionCall = reactToMention && (msg.startsWith(mention + alias) || msg.startsWith(mention + " " + alias));
            final boolean dmCall = msg.startsWith(alias) && !event.isFromGuild();
            if (prefixCall || mentionCall || dmCall) {
                return alias;
            }
        }
        return null;
    }

    private boolean checkCommand(@NotNull final MessageReceivedEvent event, final String prefix, final boolean reactToMention) {
        return isNameCall(event, prefix, reactToMention) || isAliasCall(event, prefix, reactToMention);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private @UnmodifiableView Map<String, IArgument> parseArgs(@NotNull final List<String> args, final MessageReceivedEvent event)
            throws Exception {
        final Map<String, IArgument> objArgs = new HashMap<>();
        final boolean isLongLastArg = args.size() > arguments.size();
        for (int i = 0; i < arguments.size(); i++) {
            final boolean isLast = i + 1 < arguments.size();
            final IArgument argument = arguments.get(i).clone();
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

    private void checkResult(final MessageReceivedEvent event, final IArgument argument, final String string)
            throws MissingArgumentException {
        if (string != null && !string.equals("") && !string.trim().equals(""))
            throw new MissingArgumentException(this, argument, event);
    }

    private void checkResult(final IArgument arguments, final String string, final MessageReceivedEvent event) throws InvalidValueException {
        if (string != null && !string.equals("") && !string.trim().equals(""))
            throw new InvalidValueException(arguments, string, this, event);
    }

    @Override
    public final void onDirectMessageReceived(@NotNull final MessageReceivedEvent event) {
        if (!shouldRun(event)) return;
        final String unable;

        // Verifying that the command has not exceeded its usage limit, if there is a limit.
        if (throttling != null) {
            if (throttling.check(event)) {
                unable = LocalizedFormat.format("Command_Failed", event);
                sendReply(event, unable + "\n" + LocalizedFormat.format("Command_Throttling", event));
            }
        }

        final Map<String, IArgument> objArgs = parseArguments(event);
        if (objArgs == null) return;

        if (getThrottling() != null) getThrottling().addUsage(event);
        run(event, objArgs);
    }

    @Override
    public final void onGuildThreadMessageReceived(@NotNull final MessageReceivedEvent event) {
        onGuildMessageReceived(event);
    }

    @Override
    public final void onGuildMessageReceived(@NotNull final MessageReceivedEvent event) {
        // Verify that the command is in the base format expected by the bot
        if (!shouldRun(event)) return;
        final String unable;
        // Verifying that the command is on an appropriate channel if it is NSFW
        if (isNsfw() && !event.getTextChannel().isNSFW()) {
            unable = LocalizedFormat.format("Command_Failed", event);
            sendReply(event, unable + "\n" + LocalizedFormat.format("Command_NSFW", event, getName(event)));
            return;
        }

        // Verifying that the client and user have permissions to execute this command.
        final String error = checkPermissions(event);
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

        final Map<String, IArgument> objArgs = parseArguments(event);

        if (objArgs == null) return;

        if (getThrottling() != null) getThrottling().addUsage(event);
        run(event, objArgs);
    }

    @Override
    public String checkPermissions(final Event event) {
        String error = checkClientPermissions(event);
        if (error != null) return error;
        error = checkMemberPermissions(event);
        if (error != null) return error;
        if (event instanceof MessageReceivedEvent) error = hasPermission((MessageReceivedEvent) event);
        else if (event instanceof SlashCommandInteractionEvent)
            error = hasPermission((SlashCommandInteractionEvent) event);
        if (error != null) return error;
        if (event instanceof MessageReceivedEvent) {
            final MessageReceivedEvent e = (MessageReceivedEvent) event;
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
        }
        if (event instanceof SlashCommandInteractionEvent) {
            final SlashCommandInteractionEvent e = (SlashCommandInteractionEvent) event;
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
        }
        return !(isGuildOnly() || isThreadOnly()) ? null : "_";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName(final Event event) {
        return getName();
    }

    @Override
    public String getGroup(final Event event) {
        return getGroup();
    }

    @Override
    public String getDescription(final Event event) {
        return getDescription();
    }

    @Override
    public String getDetails(final Event event) {
        return getDetails();
    }

    @Override
    public List<IArgument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public List<String> getExamples() {
        return Collections.unmodifiableList(examples);
    }

    @Override
    public IArgument getArgument(final String name) {
        return arguments.stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Throttling getThrottling() {
        return throttling;
    }

    @Override
    public boolean isNsfw() {
        return nsfw;
    }

    @Override
    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }

    @Override
    public List<Permission> getClientPermissions() {
        return Collections.unmodifiableList(clientPermissions);
    }

    @Override
    public List<Permission> getMemberPermissions() {
        return Collections.unmodifiableList(memberPermissions);
    }

    @Override
    public boolean isGuildOnly() {
        return guildOnly;
    }

    @Override
    public boolean isRunInThread() {
        return runInThread;
    }

    @Override
    public boolean isThreadOnly() {
        return threadOnly;
    }

    @Override
    public boolean isPrivateUseOnly() {
        return privateUseOnly;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getDetails() {
        return details;
    }

    @Override
    public final @NotNull String anyUsage(final Event event) {
        final String prefix = CommandEngine.getInstance().getPrefix(event);
        final boolean react = CommandEngine.getInstance().isReactToMention(event);
        final String args = getArguments().stream().map(Command::mapArgumentUsage).collect(Collectors.joining(" "));
        final List<String> usages = new ArrayList<>();
        usages.add(prefix + getName(event) + " " + args);
        if (react) {
            usages.add("@" + event.getJDA().getSelfUser().getName() + getName(event) + " " + args);
        }
        if (event instanceof MessageReceivedEvent && !((MessageReceivedEvent) event).isFromGuild()) {
            usages.add(getName(event) + " " + args);
        } else if (event instanceof SlashCommandInteractionEvent && !((SlashCommandInteractionEvent) event).isFromGuild()) {
            usages.add(getName(event) + " " + args);
        }

        return "`" +
                String.join(LocalizedFormat.format("NormalText_Or", event, "` ", " `").toLowerCase(), usages) +
                "`";
    }

    @Override
    public String usage(final String arg, @NotNull final Event event) {
        final String prefix = CommandEngine.getInstance().getPrefix(event);
        return String.format("%s%s %s", prefix, getName(event), arg);
    }
}
