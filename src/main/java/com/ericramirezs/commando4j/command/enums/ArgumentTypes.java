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

package com.ericramirezs.commando4j.command.enums;

import com.ericramirezs.commando4j.command.util.LocalizedFormat;
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

    ANNOUNCEMENT_CHANNEL("ArgumentTypes_AnnouncementChannel", OptionType.CHANNEL),
    ATTACHMENT("ArgumentTypes_Attachment", OptionType.ATTACHMENT),
    AUDIO_CHANNEL("ArgumentTypes_AudioChannel", OptionType.CHANNEL),
    BOOLEAN("ArgumentTypes_Boolean", OptionType.BOOLEAN),
    CATEGORY_CHANNEL("ArgumentTypes_CategoryChannel", OptionType.CHANNEL),
    CHANNEL("ArgumentTypes_Channel", OptionType.CHANNEL),
    COMMAND("ArgumentTypes_Command", OptionType.STRING),
    CUSTOM_EMOJI("ArgumentTypes_CustomEmoji", OptionType.MENTIONABLE),
    FLOAT("ArgumentTypes_Float", OptionType.NUMBER),
    GROUP("ArgumentTypes_Group", OptionType.UNKNOWN),
    GUILD("ArgumentTypes_Guild", OptionType.STRING),
    INTEGER("ArgumentTypes_Integer", OptionType.INTEGER),
    MEMBER("ArgumentTypes_Member", OptionType.USER),
    MESSAGE("ArgumentTypes_Message", OptionType.STRING),
    MESSAGE_CHANNEL("ArgumentTypes_MessageChannel", OptionType.MENTIONABLE),
    ROLE("ArgumentTypes_Role", OptionType.ROLE),
    STAGE_CHANNEL("ArgumentTypes_StageChannel", OptionType.CHANNEL),
    STRING("ArgumentTypes_String", OptionType.STRING),
    TEXT_CHANNEL("ArgumentTypes_TextChannel", OptionType.CHANNEL),
    THREAD_CHANNEL("ArgumentTypes_ThreadChannel", OptionType.CHANNEL),
    UNION("ArgumentTypes_Union", OptionType.STRING),
    USER("ArgumentTypes_User", OptionType.USER),
    VOICE_CHANNEL("ArgumentTypes_VoiceChannel", OptionType.CHANNEL),
    DATE("ArgumentTypes_Date", OptionType.STRING),
    TIME("ArgumentTypes_Time", OptionType.STRING),
    DATETIME("ArgumentTypes_DateTime", OptionType.STRING),
    URL("ArgumentTypes_URL", OptionType.STRING);

    private final String verbose;

    private final OptionType optionType;

    ArgumentTypes(String verbose, OptionType optionType
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
    public @NotNull String toString(MessageReceivedEvent event) {
        return LocalizedFormat.format(verbose, event);
    }

    /**
     * Get a readable version of the argument type for final users.
     *
     * @param event Discord event that triggered this function call.
     * @return localized readable Argument type
     */
    public @NotNull String toString(SlashCommandInteractionEvent event) {
        return LocalizedFormat.format(verbose, event);
    }

    /**
     * Get a readable version of the argument type for final users.
     *
     * @param event Discord event that triggered this function call.
     * @return localized readable Argument type
     */
    public @NotNull String toString(Event event) {
        return LocalizedFormat.format(verbose, event);
    }

    /**
     * Get a readable version of the argument type for final users.
     *
     * @param locale desired locale.
     * @return localized readable Argument type
     */
    public @NotNull String toString(Locale locale) {
        return LocalizedFormat.format(verbose, locale);
    }

    /**
     * Get the argument representation as an {@link OptionType} object. Used mostly to
     * register ${@link com.ericramirezs.commando4j.command.command.Command}
     * that implements {@link com.ericramirezs.commando4j.command.Slash} as Slash commands
     *
     * @return argument representation as an {@link OptionType} object
     */
    public OptionType asOptionType() {
        return optionType;
    }
}
