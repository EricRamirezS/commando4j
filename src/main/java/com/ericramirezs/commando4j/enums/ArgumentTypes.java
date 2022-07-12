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

package com.ericramirezs.commando4j.enums;

import com.ericramirezs.commando4j.util.LocalizedFormat;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Argument types supported by the Engine
 */
public enum ArgumentTypes {

    /**
     * Argument that represents a Guild Message Channel used to post announcements.
     * @see net.dv8tion.jda.api.entities.NewsChannel
     */
    ANNOUNCEMENT_CHANNEL("ArgumentTypes_AnnouncementChannel", OptionType.CHANNEL),
    /**
     * Argument that represents an Attachment.
     * @see net.dv8tion.jda.api.utils.AttachedFile
     */
    ATTACHMENT("ArgumentTypes_Attachment", OptionType.ATTACHMENT),

    /**
     * Argument that represents any Audio Channel.
     * @see net.dv8tion.jda.api.entities.AudioChannel
     */
    AUDIO_CHANNEL("ArgumentTypes_AudioChannel", OptionType.CHANNEL),

    /**
     * Argument that represents a boolean value.
     *
     * @see Boolean
     */
    BOOLEAN("ArgumentTypes_Boolean", OptionType.BOOLEAN),

    /**
     * Argument that represents a category.
     *
     * @see net.dv8tion.jda.api.entities.Category
     */
    CATEGORY_CHANNEL("ArgumentTypes_CategoryChannel", OptionType.CHANNEL),

    /**
     * Argument that represents any type of Discord Channel.
     *
     * @see net.dv8tion.jda.api.entities.Channel
     */
    CHANNEL("ArgumentTypes_Channel", OptionType.CHANNEL),

    /**
     * Argument that represents a Command loaded in the engine.
     *
     * @see com.ericramirezs.commando4j.command.ICommand
     * @see com.ericramirezs.commando4j.ICommandEngine
     */
    COMMAND("ArgumentTypes_Command", OptionType.STRING),

    /**
     * Argument that represents a Custom Emoji uploaded to a Discord Server.
     *
     * @see net.dv8tion.jda.api.entities.emoji.Emoji
     */
    CUSTOM_EMOJI("ArgumentTypes_CustomEmoji", OptionType.MENTIONABLE),

    /**
     * Argument that represents a Number with decimal point.
     *
     * @see Double
     */
    FLOAT("ArgumentTypes_Float", OptionType.NUMBER),

    /**
     * Argument that represents a Discord Server.
     *
     * @see net.dv8tion.jda.api.entities.Guild
     */
    GUILD("ArgumentTypes_Guild", OptionType.STRING),

    /**
     * Argument that represents an Integer value.
     *
     * @see Long
     */
    INTEGER("ArgumentTypes_Integer", OptionType.INTEGER),

    /**
     * Argument that represents a Server's Member.
     *
     * @see net.dv8tion.jda.api.entities.Member
     */
    MEMBER("ArgumentTypes_Member", OptionType.USER),

    /**
     * Argument that represents a Discord Message.
     *
     * @see net.dv8tion.jda.api.entities.Message
     */
    MESSAGE("ArgumentTypes_Message", OptionType.STRING),

    /**
     * Argument that represents a Discord Message Channel.
     *
     * @see net.dv8tion.jda.api.entities.MessageChannel
     */
    MESSAGE_CHANNEL("ArgumentTypes_MessageChannel", OptionType.MENTIONABLE),

    /**
     * Argument that represents a Server's role.
     *
     * @see net.dv8tion.jda.api.entities.Role
     */
    ROLE("ArgumentTypes_Role", OptionType.ROLE),

    /**
     * Argument that represents a special Guild Voice Channel of type Stage.
     *
     * @see net.dv8tion.jda.api.entities.StageChannel
     */
    STAGE_CHANNEL("ArgumentTypes_StageChannel", OptionType.CHANNEL),

    /**
     * Argument that represents plain text.
     *
     * @see String
     */
    STRING("ArgumentTypes_String", OptionType.STRING),

    /**
     * Argument that represents a Text Channel.
     *
     * @see net.dv8tion.jda.api.entities.TextChannel
     */
    TEXT_CHANNEL("ArgumentTypes_TextChannel", OptionType.CHANNEL),

    /**
     * Argument that represents a Thread Channel.
     *
     * @see net.dv8tion.jda.api.entities.ThreadChannel
     */
    THREAD_CHANNEL("ArgumentTypes_ThreadChannel", OptionType.CHANNEL),

    /**
     * Argument that indicates multiple Argument types are going to be supported.
     *
     * @see com.ericramirezs.commando4j.arguments.UnionArgument
     */
    UNION("ArgumentTypes_Union", OptionType.STRING),

    /**
     * Argument that represents a Discord's user.
     *
     * @see net.dv8tion.jda.api.entities.User
     */
    USER("ArgumentTypes_User", OptionType.USER),

    /**
     * Argument that represents a Discord Voice Channel.
     *
     * @see net.dv8tion.jda.api.entities.VoiceChannel
     */
    VOICE_CHANNEL("ArgumentTypes_VoiceChannel", OptionType.CHANNEL),

    /**
     * Argument that represents a Date Object
     *
     * @see java.util.Date
     * @see java.time.LocalDate
     */
    DATE("ArgumentTypes_Date", OptionType.STRING),

    /**
     * Argument that represents a LocalTime Object
     *
     * @see java.time.LocalTime
     */
    TIME("ArgumentTypes_Time", OptionType.STRING),

    /**
     * Argument that represents a DateTime Object.
     *
     * @see java.time.LocalDateTime
     * @see java.util.Date
     */
    DATETIME("ArgumentTypes_DateTime", OptionType.STRING),

    /**
     * Argument that represents a URL object
     *
     * @see java.net.URL
     */
    URL("ArgumentTypes_URL", OptionType.STRING);

    private final String verbose;

    private final OptionType optionType;

    ArgumentTypes(final String verbose, final OptionType optionType
    ) {
        this.verbose = verbose;
        this.optionType = optionType;
    }

    /**
     * Get a readable version of the argument type for final users
     *
     * @return readable Argument type
     */
    @Override
    public @NotNull String toString() {
        return LocalizedFormat.format(verbose);
    }

    /**
     * Get a readable version of the argument type for final users.
     *
     * @param event Discord event that triggered this function call.
     * @return localized readable Argument type
     */
    public @NotNull String toString(final MessageReceivedEvent event) {
        return LocalizedFormat.format(verbose, event);
    }

    /**
     * Get a readable version of the argument type for final users.
     *
     * @param event Discord event that triggered this function call.
     * @return localized readable Argument type
     */
    public @NotNull String toString(final SlashCommandInteractionEvent event) {
        return LocalizedFormat.format(verbose, event);
    }

    /**
     * Get a readable version of the argument type for final users.
     *
     * @param event Discord event that triggered this function call.
     * @return localized readable Argument type
     */
    public @NotNull String toString(final Event event) {
        return LocalizedFormat.format(verbose, event);
    }

    /**
     * Get a readable version of the argument type for final users.
     *
     * @param locale desired locale.
     * @return localized readable Argument type
     */
    public @NotNull String toString(final Locale locale) {
        return LocalizedFormat.format(verbose, locale);
    }

    /**
     * Get the argument representation as an {@link OptionType} object. Used mostly to
     * register ${@link com.ericramirezs.commando4j.command.Command}
     * that implements {@link com.ericramirezs.commando4j.Slash} as Slash commands
     *
     * @return argument representation as an {@link OptionType} object
     */
    public OptionType asOptionType() {
        return optionType;
    }
}
