package org.EricRamirezS.jdacommando.command.types;

public enum ArgumentTypes {

    BOOLEAN("boolean"),
    CATEGORY_CHANNEL("category_channel"),
    CHANNEL("channel"),
    COMMAND("command"),
    CUSTOM_EMOJI("custom_emoji"),
    FLOAT("float"),
    GROUP("group"),
    INTEGER("integer"),
    MEMBER("member"),
    MESSAGE("message"),
    ROLE("role"),
    STRING("string"),
    TEXT_CHANNEL("text_channel"),
    UNION("union"),
    USER("user"),
    VOICE_CHANNEL("voice_channel");

    private final String verbose;

    ArgumentTypes(String verbose) {
        this.verbose = verbose;
    }

    @Override
    public String toString() {
        return verbose;
    }
}
