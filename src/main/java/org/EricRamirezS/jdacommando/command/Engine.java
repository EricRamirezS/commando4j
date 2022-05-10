package org.EricRamirezS.jdacommando.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedNameException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class Engine {

    private static Engine commandConfig = new Engine();

    public static Engine getInstance() {
        return commandConfig;
    }

    private static void setInstance(Engine newCommandConfig) {
        commandConfig = newCommandConfig;
    }

    private Engine() {
    }

    private final List<Command> commandList = new ArrayList<>();
    private final Map<String, Command> commandNames = new HashMap<>();

    private String prefix = "~";
    private Locale language = new Locale("en");
    private final MultiLocaleResourceBundle resourceBundle = new MultiLocaleResourceBundle();

    private boolean reactToMention = true;

    public boolean isReactToMention() {
        return reactToMention;
    }

    public @Nullable Command getCommand(@NotNull String name) {
        if (commandNames.containsKey(name.toLowerCase(Locale.ROOT)))
            return commandNames.get(name.toLowerCase(Locale.ROOT));
        return null;
    }

    public @NotNull List<Command> getCommandsByPartialMatch(String name) {
        Set<String> keys = commandNames.keySet();
        List<String> found = keys.stream().filter(c -> c.contains(name.toLowerCase(Locale.ROOT))).toList();
        Set<Command> commands = new HashSet<>();
        for (String k : found) {
            commands.add(commandNames.get(k));
        }
        return new ArrayList<>(commands);
    }

    public @NotNull List<Command> getCommandsByExactMatch(String name) {
        Set<String> keys = commandNames.keySet();
        List<String> found = keys.stream().filter(c -> c.equals(name.toLowerCase(Locale.ROOT))).toList();
        Set<Command> commands = new HashSet<>();
        for (String k : found) {
            commands.add(commandNames.get(k));
        }
        return new ArrayList<>(commands);
    }

    /**
     * Sets if the bot should react to a message starting by mentioning the bot @bot_name
     * if sets to false, the bot will only react to prefix
     * @see #getPrefix(MessageReceivedEvent)
     * @param reactToMention Should react to mention?
     */
    public Engine setReactToMention(boolean reactToMention) {
        this.reactToMention = reactToMention;
        return this;
    }

    @Contract(" -> new")
    public final @NotNull @UnmodifiableView List<Command> getCommands() {
        return Collections.unmodifiableList(commandList);
    }

    Engine addCommand(@NotNull Command command) throws DuplicatedNameException {
        addAlias(command.getName(), command);
        commandList.add(command);
        return this;
    }

    /**
     * Add an alternative name to a command
     * @param name alternative name
     * @param command Command to be called
     * @throws DuplicatedNameException There's already a commando with the same name/alias
     */
    Engine addAlias(@NotNull String name, Command command) throws DuplicatedNameException {
        if (commandNames.containsKey(name))
            throw new DuplicatedNameException(name);
        commandNames.put(name, command);
        return this;
    }

    /**
     * By default, it gets the prefix configured on the bot.
     * May be used to get a prefix based on the guild
     * @return prefix string
     */
    public String getPrefix(@NotNull MessageReceivedEvent event) {
        return prefix;
    }

    /**
     * Sets the default prefix for the bot
     * @param prefix prefix string
     */
    public Engine setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Gets the default language to be used by the bot
     *
     * @return current language
     * @see org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle#getSupportedLocale()
     */
    public Locale getLanguage() {
        return language;
    }

    /**
     * By default, gets the default language to be used by the bot.
     * May be used to get a Locale based on the Guild information.
     *
     * @return current language
     * @see org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle#getSupportedLocale()
     */
    public Locale getLanguage(@Nullable MessageReceivedEvent event) {
        return getLanguage();
    }

    /**
     * Sets the default language to be used by the bot
     *
     * @param language Locale to use
     * @see org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle#getSupportedLocale()
     */
    public Engine setLanguage(@NotNull Locale language) {
        this.language = language;
        return this;
    }

    /**
     * Gets a text string in the configured language of the bot
     *
     * @param key String key in resourceBundle
     * @return String in the current language
     */
    public final @NotNull String getString(@NotNull String key) {
        return resourceBundle.getString(key);
    }

    /**
     * Gets a text string in a specific language
     *
     * @param key String key in resourceBundle
     * @return String in the specific language
     */
    public final @NotNull String getString(@NotNull String key, @NotNull Locale locale) {
        return resourceBundle.getString(locale, key);
    }

    /**
     * Gets a string Array in the configured language of the bot
     *
     * @param key String key in resourceBundle
     * @return String in the current language
     */
    public final @NotNull String[] getStringArray(@NotNull String key) {
        return resourceBundle.getStringArray(key);
    }

    /**
     * Gets a string Array in a specific language
     *
     * @param key String key in resourceBundle
     * @return String in the specific language
     */
    public final @NotNull String[] getStringArray(@NotNull String key, @NotNull Locale locale) {
        return resourceBundle.getStringArray(locale, key);

    }
}
