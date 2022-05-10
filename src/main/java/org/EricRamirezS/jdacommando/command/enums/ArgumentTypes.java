package org.EricRamirezS.jdacommando.command.enums;

public enum ArgumentTypes {

    ANNOUNCEMENT_CHANNEL("ArgumentTypes_AnnouncementChannel"),
    AUDIO_CHANNEL("ArgumentTypes_AudioChannel"),
    BOOLEAN("ArgumentTypes_Boolean"),
    CATEGORY_CHANNEL("ArgumentTypes_CategoryChannel"),
    CHANNEL("ArgumentTypes_Channel"),
    COMMAND("ArgumentTypes_Command"),
    CUSTOM_EMOJI("ArgumentTypes_CustomEmoji"),
    FLOAT("ArgumentTypes_Float"),
    GROUP("ArgumentTypes_Group"),
    GUILD("ArgumentTypes_Guild"),
    INTEGER("ArgumentTypes_Integer"),
    MEMBER("ArgumentTypes_Member"),
    MESSAGE("ArgumentTypes_Message"),
    MESSAGE_CHANNEL("ArgumentTypes_MessageChannel"),
    ROLE("ArgumentTypes_Role"),
    STAGE_CHANNEL("ArgumentTypes_StageChannel"),
    STRING("ArgumentTypes_String"),
    TEXT_CHANNEL("ArgumentTypes_TextChannel"),
    THREAD_CHANNEL("ArgumentTypes_ThreadChannel"),
    UNION("ArgumentTypes_Union"),
    USER("ArgumentTypes_User"),
    VOICE_CHANNEL("ArgumentTypes_VoiceChannel");

    private final String verbose;

    ArgumentTypes(String verbose) {
        this.verbose = verbose;
    }

    @Override
    public String toString() {
        return verbose;
    }
}
