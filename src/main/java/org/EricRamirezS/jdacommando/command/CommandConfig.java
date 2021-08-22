package org.EricRamirezS.jdacommando.command;

import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedNameException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CommandConfig {

    private CommandConfig(){}

    private static final List<Command> commandList = new ArrayList<>();
    private static final Map<String, Command> commandNames = new HashMap<>();

    private static String prefix = "~";
    private static Locale language = new Locale("en");
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("texts", language);

    private static boolean reactToMention = true;

    public static boolean isReactToMention() {
        return reactToMention;
    }

    public static @Nullable Command getCommand(@NotNull String name){
        if (commandNames.containsKey(name.toLowerCase(Locale.ROOT)))
        return commandNames.get(name.toLowerCase(Locale.ROOT));
        return null;
    }

    public static @NotNull List<Command> getCommandsByPartialMatch(String name){
        Set<String> keys = commandNames.keySet();
        List<String> found = keys.stream().filter(c -> c.contains(name.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        Set<Command> commands = new HashSet<>();
        for (String k: found) {
            commands.add(commandNames.get(k));
        }
        return new ArrayList<>(commands);
    }

    public static void setReactToMention(boolean reactToMention) {
        CommandConfig.reactToMention = reactToMention;
    }

    @Contract(" -> new")
    public static @NotNull List<Command> getCommands(){
        return new ArrayList<>(commandList);
    }

    static void addCommand(@NotNull Command command) throws DuplicatedNameException {
        addAlias(command.getName(), command);
        commandList.add(command);
    }

    static void addAlias(@NotNull String name, Command command) throws DuplicatedNameException {
        if (commandNames.containsKey(name))
            throw new DuplicatedNameException(name);
        commandNames.put(name, command);
    }
    public static String getPrefix() {
        return prefix;
    }

    public static void setPrefix(@NotNull String prefix) {
        CommandConfig.prefix = prefix;
    }

    public static Locale getLanguage() {
        return language;
    }

    public static void setLanguage(@NotNull Locale language) {
        CommandConfig.language = language;
        resourceBundle = ResourceBundle.getBundle("texts", language);
    }

    public static @NotNull String getText(@NotNull String key) {
        return resourceBundle.getString(key);
    }

}
