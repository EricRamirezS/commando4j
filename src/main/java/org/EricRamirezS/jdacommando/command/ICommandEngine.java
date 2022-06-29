package org.EricRamirezS.jdacommando.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.command.ICommand;
import org.EricRamirezS.jdacommando.command.data.IRepository;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedNameException;

import javax.naming.InvalidNameException;
import java.util.List;
import java.util.Locale;

public interface ICommandEngine {

    /**
     * Log a message at the WARN level.
     *
     * @param message the message string to be logged
     */
    void logWarn(String message);

    /**
     * Log a message at the DEBUG level.
     *
     * @param message the message string to be logged
     */
    void logDebug(String message);

    /**
     * Log a message at the INFO level.
     *
     * @param message the message string to be logged
     */
    void logInfo(String message);

    /**
     * Log a message at the ERROR level.
     *
     * @param message the message string to be logged
     */
    void logError(String message);

    /**
     * Set which command is related to user help.
     * The command will be recommended if the user fails to use a command.
     *
     * @param help command name
     * @return The ICommandEngine instance. Useful for chaining.
     */
    ICommandEngine setHelp(String help);

    /**
     * Return the ICommand object of the help command.
     *
     * @return Help Command object
     */
    ICommand getHelpCommand();

    /**
     * Get whether the bot should react to mention or not
     *
     * @return True if the bot should react to mention calls
     */
    boolean isReactToMention();

    /**
     * Sets if the bot should react to a message starting by mentioning the bot @bot_name
     * if sets to false, the bot will only react to prefix.
     *
     * @param reactToMention True if the bot should react to mention calls
     * @return The ICommandEngine instance. Useful for chaining.
     * @see #getPrefix(MessageReceivedEvent)
     */
    ICommandEngine setReactToMention(boolean reactToMention);

    /**
     * Registers ICommands into the engine.
     */
    void loadCommands();

    boolean isReactToMention(Event event);

    ICommand getCommand(String name);

    List<ICommand> getCommandsByPartialMatch(String name);

    List<ICommand> getCommandsByExactMatch(String name);

    List<ICommand> getCommands();

    /**
     * Registers an ICommand into the engine.
     *
     * @param command The command to be registered.
     * @return The ICommandEngine instance. Useful for chaining.
     * @throws DuplicatedNameException Error if there's another command with the same name or alias registered.
     * @throws InvalidNameException    Error if the command doesn't match the expected name.
     */
    ICommandEngine addCommand(ICommand command) throws DuplicatedNameException, InvalidNameException;

    /**
     * Add an alternative name to a command
     *
     * @param name    alternative name
     * @param command Command to be called
     * @return The ICommandEngine instance. Useful for chaining.
     * @throws DuplicatedNameException There's already a commando with the same name/alias
     */
    ICommandEngine addAlias(String name, ICommand command) throws DuplicatedNameException, InvalidNameException;

    /**
     * By default, it gets the prefix configured on the bot.
     * May be used to get a prefix based on the guild
     *
     * @param event Message Received event that triggered this request
     * @return prefix string
     */
    String getPrefix(MessageReceivedEvent event);

    /**
     * By default, it gets the prefix configured on the bot.
     * May be used to get a prefix based on the guild
     *
     * @param event Slash command that triggered this request
     * @return prefix string
     */
    String getPrefix(SlashCommandInteractionEvent event);

    /**
     * By default, it gets the prefix configured on the bot.
     * May be used to get a prefix based on the guild
     *
     * @param event Event that triggered this request
     * @return prefix string
     */
    String getPrefix(Event event);

    /**
     * Gets the prefix configured on the bot.
     *
     * @return prefix string
     */
    String getPrefix();

    /**
     * Sets the default prefix for the bot
     *
     * @param prefix prefix string
     * @return The ICommandEngine instance. Useful for chaining.
     */
    ICommandEngine setPrefix(String prefix);

    /**
     * Sets a prefix for a specific guild
     *
     * @param guild  Guild object
     * @param prefix prefix String
     * @return The ICommandEngine instance. Useful for chaining.
     */
    ICommandEngine setPrefix(Guild guild, String prefix);

    /**
     * Gets the default language to be used by the bot
     *
     * @return current language
     * @see org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle#getSupportedLocale()
     */
    Locale getLanguage();

    /**
     * Sets the default language to be used by the bot
     *
     * @param language Locale to use
     * @return The ICommandEngine instance. Useful for chaining.
     * @see org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle#getSupportedLocale()
     */
    ICommandEngine setLanguage(Locale language);

    /**
     * By default, gets the default language to be used by the bot.
     * May be used to get a Locale based on the Guild information.
     *
     * @return current language
     * @see org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle#getSupportedLocale()
     */
    Locale getLanguage(Event event);

    /**
     * Gets a text string in the configured language of the bot
     *
     * @param key String key in resourceBundle
     * @return String in the current language
     */
    String getString(String key);

    /**
     * Gets a text string in a specific language
     *
     * @param key String key in resourceBundle
     * @return String in the specific language
     */
    String getString(String key, Locale locale);

    /**
     * Gets a string Array in the configured language of the bot
     *
     * @param key String key in resourceBundle
     * @return String in the current language
     */
    String[] getStringArray(String key);

    /**
     * Gets a string Array in a specific language
     *
     * @param key String key in resourceBundle
     * @return String in the specific language
     */
    String[] getStringArray(String key, Locale locale);

    IRepository getRepository();
}
