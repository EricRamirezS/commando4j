package org.EricRamirezS.jdacommando.command;

public enum Language {

    SPANISH("es"),
    ENGLISH("en");
    private final String code;

    Language(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }
}
