package org.EricRamirezS.jdacommando.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.command.ICommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public interface Slash {

    /**
     * Sends an ephemeral reply to a Slash command request
     * @param event The event that triggered this response
     * @param message The response message for the user.
     */
    static void sendReply(@NotNull SlashCommandInteractionEvent event, @NotNull String message) {
        sendReply(event, message, true);
    }

    /**
     * Sends a reply to a Slash command request
     * @param event The event that triggered this response
     * @param message The response message for the user.
     * @param ephemeral True, if this message should be invisible for other users
     */
    static void sendReply(@NotNull SlashCommandInteractionEvent event, @NotNull String message, boolean ephemeral) {
        event.reply(message).setEphemeral(ephemeral).queue();
    }

    /**
     *
     * @param event
     * @param message
     */
    static void sendReply(@NotNull SlashCommandInteractionEvent event, @NotNull EmbedBuilder message) {
        sendReply(event, message.build());
    }

    /**
     *
     * @param event
     * @param message
     */
    static void sendReply(@NotNull SlashCommandInteractionEvent event, @NotNull MessageEmbed message) {
        sendReply(event, message, true);
    }

    /**
     *
     * @param event
     * @param message
     * @param ephemeral
     */
    static void sendReply(@NotNull SlashCommandInteractionEvent event, @NotNull EmbedBuilder message, boolean ephemeral) {
        sendReply(event, message.build(), ephemeral);
    }

    /**
     *
     * @param event
     * @param message
     * @param ephemeral
     */
    static void sendReply(@NotNull SlashCommandInteractionEvent event, @NotNull MessageEmbed message, boolean ephemeral) {
        event.replyEmbeds(message).setEphemeral(ephemeral).queue();
    }

    /**
     *
     * @param event
     * @param command
     * @return
     */
    static boolean shouldRun(@NotNull SlashCommandInteractionEvent event, @NotNull ICommand command) {
        try {
            event.getThreadChannel();
            return command.isRunInThread() || !command.isPrivateUseOnly();
        } catch (Exception ignores) {
        }
        if (event.isFromGuild()) {
            return !command.isThreadOnly() && !command.isPrivateUseOnly();
        }
        return !(command.isGuildOnly() || command.isThreadOnly());
    }

    /**
     *
     * @param event
     * @param args
     */
    void run(@NotNull SlashCommandInteractionEvent event, @UnmodifiableView @NotNull Map<String, IArgument> args);
}
