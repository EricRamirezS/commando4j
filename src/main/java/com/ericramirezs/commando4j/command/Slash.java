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

import com.ericramirezs.commando4j.command.arguments.IArgument;
import com.ericramirezs.commando4j.command.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

/**
 * Interface to handle command as Discord's slash commands.
 * It tells the {@link ICommandEngine} that an implementation of {@link ICommand} should be also treated
 * as a Slash command.
 * <p>
 * <strong>Note:</strong> Implementing this in a class that doesn't implements
 * {@link ICommand} will make this implementation meaningless.
 * </p>
 *
 * @see ICommand
 */
public interface Slash {

    /**
     * Sends an ephemeral reply to a Slash command request.
     *
     * @param event   The event that triggered this response.
     * @param message The response message for the user.
     */
    static void sendReply(@NotNull final SlashCommandInteractionEvent event, @NotNull final String message) {
        sendReply(event, message, true);
    }

    /**
     * Sends a reply to a Slash command request.
     *
     * @param event     The event that triggered this response.
     * @param message   The response message for the user.
     * @param ephemeral True, if this message should be invisible for other users.
     */
    static void sendReply(@NotNull final SlashCommandInteractionEvent event, @NotNull final String message, final boolean ephemeral) {
        event.reply(message).setEphemeral(ephemeral).queue();
    }

    /**
     * Sends an ephemeral reply to a Slash command request.
     *
     * @param event   Discord event that triggered this function call
     * @param message EmbedBuilder message
     * @see net.dv8tion.jda.api.EmbedBuilder
     */
    static void sendReply(@NotNull final SlashCommandInteractionEvent event, @NotNull final EmbedBuilder message) {
        sendReply(event, message.build());
    }

    /**
     * Sends an ephemeral reply to a Slash command request.
     *
     * @param event   Discord event that triggered this function call
     * @param message MessageEmbed
     * @see net.dv8tion.jda.api.entities.MessageEmbed
     */
    static void sendReply(@NotNull final SlashCommandInteractionEvent event, @NotNull final MessageEmbed message) {
        sendReply(event, message, true);
    }

    /**
     * @param event     Discord Event that triggered this function call.
     * @param message   EmbedBuilder message
     * @param ephemeral True, if this message should be invisible for other users.
     * @see net.dv8tion.jda.api.EmbedBuilder
     */
    static void sendReply(@NotNull final SlashCommandInteractionEvent event, @NotNull final EmbedBuilder message, final boolean ephemeral) {
        sendReply(event, message.build(), ephemeral);
    }

    /**
     * @param event     Discord Event that triggered this function call.
     * @param message   MessageEmbed
     * @param ephemeral True, if this message should be invisible for other users.
     * @see net.dv8tion.jda.api.entities.MessageEmbed
     */
    static void sendReply(@NotNull final SlashCommandInteractionEvent event, @NotNull final MessageEmbed message, final boolean ephemeral) {
        event.replyEmbeds(message).setEphemeral(ephemeral).queue();
    }

    /**
     * Check if the command should be run in the current Message Channel.
     *
     * @param event   Discord Event that triggered this function call.
     * @param command Representation of a Slash Command as a {@link ICommand}.
     * @return true if the command is compatible with the current Message Channel.
     */
    static boolean shouldRun(@NotNull final SlashCommandInteractionEvent event, @NotNull final ICommand command) {
        try {
            event.getThreadChannel();
            return command.isRunInThread() || !command.isPrivateUseOnly();
        } catch (final Exception ignores) {
        }
        if (event.isFromGuild()) {
            return !command.isThreadOnly() && !command.isPrivateUseOnly();
        }
        return !(command.isGuildOnly() || command.isThreadOnly());
    }

    /**
     * Command execution after all validations has been passed.
     *
     * @param event Discord event that triggered this command call
     * @param args  Already parsed arguments, Map's keys are the argument's name
     */
    void run(@NotNull SlashCommandInteractionEvent event, @UnmodifiableView @NotNull Map<String, IArgument> args);
}
