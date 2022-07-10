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

package com.ericramirezs.commando4j.command;

import com.ericramirezs.commando4j.command.command.ICommand;
import com.ericramirezs.commando4j.command.customizations.MultiLocaleResourceBundle;
import com.ericramirezs.commando4j.command.data.IRepository;
import com.ericramirezs.commando4j.command.exceptions.DuplicatedNameException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
     * @return A reference to this object.
     * @see com.ericramirezs.commando4j.command.command.util.HelpCommand
     */
    ICommandEngine setHelp(String help);

    /**
     * Return the ICommand object of the help command.
     *
     * @return Help Command object.
     */
    ICommand getHelpCommand();

    /**
     * Get whether the bot should react to mention or not.
     *
     * @return True if the bot should react to mention calls.
     */
    boolean isReactToMention();

    /**
     * Sets if the bot should react to a message starting by mentioning the bot @bot_name
     * if sets to false, the bot will only react to prefix.
     *
     * @param reactToMention True if the bot should react to mention calls.
     * @return A reference to this object.
     * @see #getPrefix(MessageReceivedEvent)
     */
    ICommandEngine setReactToMention(boolean reactToMention);

    /**
     * Registers ICommands from packages set into the engine.
     * It also registers into JDA any ICommand object that also implements Slash.
     */
    void loadCommands();

    /**
     * Checks if bot should react to mention calls (Messages that starts with @BOT_NAME).
     *
     * @param event Discord event that triggered this function call.
     * @return true if the bot should react to mention, false if only reacts to prefix.
     */
    boolean isReactToMention(Event event);

    /**
     * Get an ICommand from the registered commands in the Engine.
     *
     * @param name command's name.
     * @return {@link ICommand} instance.
     * @see ICommand
     */
    ICommand getCommand(String name);

    /**
     * Get a list of ICommand from the registered commands in the Engine.
     *
     * @param name partial command's name.
     * @return List of ICommand that match the partial search.
     */
    List<ICommand> getCommandsByPartialMatch(String name);

    /**
     * Get a list of ICommand from the registered commands in the Engine.
     *
     * @param name expected command's name.
     * @return List of ICommand that match the search.
     */
    List<ICommand> getCommandsByExactMatch(String name);

    /**
     * Get a list of {@link ICommand} registered in the Engine.
     *
     * @return list of ICommand instances loaded in the Engine.
     */
    List<ICommand> getCommands();

    /**
     * Registers an ICommand into the engine.
     *
     * @param command The command to be registered.
     * @return A reference to this object.
     * @throws DuplicatedNameException Error if there's another command with the same name or alias registered.
     * @throws InvalidNameException    Error if the command doesn't match the expected name.
     */
    ICommandEngine addCommand(ICommand command) throws DuplicatedNameException, InvalidNameException;

    /**
     * Add an alternative name to a command.
     *
     * @param name    alternative name.
     * @param command Command to be called.
     * @return A reference to this object.
     * @throws DuplicatedNameException Thrown when there's already a commando with the same name/alias.
     * @throws InvalidNameException    Thrown when the command's alias uses non-alphanumeric characters.
     */
    ICommandEngine addAlias(String name, ICommand command) throws DuplicatedNameException, InvalidNameException;

    /**
     * By default, it gets the prefix configured on the bot.
     * May be used to get a prefix based on the guild.
     *
     * @param event Message Received event that triggered this request.
     * @return prefix string.
     */
    String getPrefix(MessageReceivedEvent event);

    /**
     * By default, it gets the prefix configured on the bot.
     * May be used to get a prefix based on the guild.
     *
     * @param event Slash command that triggered this request.
     * @return prefix string.
     */
    String getPrefix(SlashCommandInteractionEvent event);

    /**
     * By default, it gets the prefix configured on the bot.
     * May be used to get a prefix based on the guild.
     *
     * @param event Event that triggered this request.
     * @return prefix string.
     */
    String getPrefix(Event event);

    /**
     * Gets the prefix configured on the bot.
     *
     * @return prefix string.
     */
    String getPrefix();

    /**
     * Sets the default prefix for the bot.
     *
     * @param prefix prefix string.
     * @return A reference to this object.
     */
    ICommandEngine setPrefix(String prefix);

    /**
     * Sets a prefix for a specific guild.
     *
     * @param guild  Guild object.
     * @param prefix prefix String.
     * @return A reference to this object.
     */
    ICommandEngine setPrefix(Guild guild, String prefix);

    /**
     * Gets the default language to be used by the bot.
     *
     * @return current language.
     * @see MultiLocaleResourceBundle#getSupportedLocale()
     */
    Locale getLanguage();

    /**
     * Sets the default language to be used by the bot.
     *
     * @param language Locale to use.
     * @return A reference to this object.
     * @see MultiLocaleResourceBundle#getSupportedLocale()
     */
    ICommandEngine setLanguage(Locale language);

    /**
     * By default, gets the default language to be used by the bot.
     * May be used to get a Locale based on the Guild information.
     *
     * @param event Discord event that triggered this function call.
     * @return current language.
     * @see MultiLocaleResourceBundle#getSupportedLocale()
     */
    Locale getLanguage(Event event);

    /**
     * Gets a text string in the configured language of the bot.
     *
     * @param key String key in resourceBundle.
     * @return String in the current language.
     */
    String getString(String key);

    /**
     * Gets a text string in a specific language.
     *
     * @param key String key in resourceBundle.
     * @param locale expected language.
     * @return String in the specific language.
     */
    String getString(String key, Locale locale);

    /**
     * Gets a string Array in the configured language of the bot.
     *
     * @param key String key in resourceBundle.
     * @return String in the current language.
     */
    String[] getStringArray(String key);

    /**
     * Gets a string Array in a specific language.
     *
     * @param key String key in resourceBundle.
     * @param locale Expected language.
     * @return String in the specific language.
     */
    String[] getStringArray(String key, Locale locale);

    /**
     * Return the repository Controller.
     *
     * @return IRepository object.
     */
    IRepository getRepository();
}
