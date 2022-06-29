package org.EricRamirezS.jdacommando.command.enums;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ArgumentTypes {

    ANNOUNCEMENT_CHANNEL("ArgumentTypes_AnnouncementChannel", 7),
    ATTACHMENT("ArgumentTypes_Attachment", 11),
    AUDIO_CHANNEL("ArgumentTypes_AudioChannel", 7),
    BOOLEAN("ArgumentTypes_Boolean", 5),
    CATEGORY_CHANNEL("ArgumentTypes_CategoryChannel", 7),
    CHANNEL("ArgumentTypes_Channel", 7),
    COMMAND("ArgumentTypes_Command", 3),
    CUSTOM_EMOJI("ArgumentTypes_CustomEmoji", 9),
    FLOAT("ArgumentTypes_Float", 10),
    GROUP("ArgumentTypes_Group", -1),
    GUILD("ArgumentTypes_Guild", 3),
    INTEGER("ArgumentTypes_Integer", 4),
    MEMBER("ArgumentTypes_Member", 6),
    MESSAGE("ArgumentTypes_Message", 3),
    MESSAGE_CHANNEL("ArgumentTypes_MessageChannel", 9),
    ROLE("ArgumentTypes_Role", 8),
    STAGE_CHANNEL("ArgumentTypes_StageChannel", 7),
    STRING("ArgumentTypes_String", 3),
    TEXT_CHANNEL("ArgumentTypes_TextChannel", 7),
    THREAD_CHANNEL("ArgumentTypes_ThreadChannel", 7),
    UNION("ArgumentTypes_Union", 3),
    USER("ArgumentTypes_User", 6),
    VOICE_CHANNEL("ArgumentTypes_VoiceChannel", 7),
    DATE("ArgumentTypes_Date", 3),
    TIME("ArgumentTypes_Time", 3),
    DATETIME("ArgumentTypes_DateTime", 3);

    private final String verbose;

    private final int key;

    ArgumentTypes(String verbose, int key
    ) {
        this.verbose = verbose;
        this.key = key;
    }

    @Override
    public @NotNull String toString() {
        return LocalizedFormat.format(verbose);
    }

    public @NotNull String toString(MessageReceivedEvent event) {
        return LocalizedFormat.format(verbose, event);
    }

    public @NotNull String toString(SlashCommandInteractionEvent event) {
        return LocalizedFormat.format(verbose, event);
    }

    public @NotNull String toString(Event event) {
        return LocalizedFormat.format(verbose, event);
    }

    public @NotNull String toString(Locale locale){
        return LocalizedFormat.format(verbose, locale);
    }

    public int getKey() {
        return key;
    }

    public OptionType asOptionType() {
        return switch (key) {
            case 1 -> OptionType.SUB_COMMAND;
            case 2 -> OptionType.SUB_COMMAND_GROUP;
            case 3 -> OptionType.STRING;
            case 4 -> OptionType.INTEGER;
            case 5 -> OptionType.BOOLEAN;
            case 6 -> OptionType.USER;
            case 7 -> OptionType.CHANNEL;
            case 8 -> OptionType.ROLE;
            case 9 -> OptionType.MENTIONABLE;
            case 10 -> OptionType.NUMBER;
            default -> OptionType.UNKNOWN;
        };
    }
}
