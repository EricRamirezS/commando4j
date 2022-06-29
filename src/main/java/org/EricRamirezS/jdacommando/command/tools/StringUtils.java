package org.EricRamirezS.jdacommando.command.tools;

public interface StringUtils {

    static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }

    static boolean isNullOrWhiteSpace(String s) {
        return isNullOrEmpty(s) || s.trim().equals("");
    }
}
