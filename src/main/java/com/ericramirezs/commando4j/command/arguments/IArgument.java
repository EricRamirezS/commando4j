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

package com.ericramirezs.commando4j.command.arguments;

import com.ericramirezs.commando4j.command.Slash;
import com.ericramirezs.commando4j.command.command.ICommand;
import com.ericramirezs.commando4j.command.enums.ArgumentTypes;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.function.Function;

/**
 * Interface to implement in any object to be used as an argument for a Command.
 * <p>
 * An argument refers to any text entered by the user after the command name,
 * which will be used to make a command function perform certain functions based on these arguments.
 * </p>
 * <p>
 * The goal of the implementation is to manage the validation and conversion of
 * the parameter to a specific object, to simplify the development of the command.
 * </p>
 *
 * @param <A> Self argument object that implements IArgument.
 * @param <T> result object after parsing the argument.
 * @see ICommand
 */
public interface IArgument<A extends IArgument, T> {

    /**
     * Checks if the argument requires a value to be set.
     *
     * @return true if value cannot be null, false otherwise.
     * @see IArgument#getValue()
     */
    boolean isRequired();

    /**
     * Get the maximum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @return Maximum value related to the Object, null if not defined.
     */
    Double getMax();

    /**
     * Get the minimum value allowed for this argument. It may be used to limit a
     * String length or a numeric minimum value.
     *
     * @return Minimum value related to the Object, null if not defined.
     */
    Double getMin();

    /**
     * Get the argument's name.
     *
     * @return the argument's name.
     */
    String getName();

    /**
     * Get the argument's prompt.
     * Used to give the final user a hint of the expected value.
     *
     * @return the argument's prompt.
     */
    String getPrompt();

    /**
     * Get the argument's prompt.
     * Used to give the final user a hint of the expected value.
     *
     * @param event Discord event that triggered this function call.
     * @return the argument's prompt.
     */
    String getPrompt(Event event);

    /**
     * Checks if the argument accepts null values. If null values are not
     * accepted and the value input by the user.
     *
     * @param arg   argument input by the user.
     * @param event Discord event that triggered this function call.
     * @return null if no problems found, Error message if the argument cannot be null, but the actual Value is null.
     */
    String validateNull(String arg, MessageReceivedEvent event);

    /**
     * Checks if the argument is one of the specified valid values.
     *
     * @param object          Value input by the user.
     * @param event           Discord event that triggered this function call.
     * @param mapper          Mapper to convert the Valid object into readable String for the final user.
     * @param errorMessageKey Error message's key in resource bundle.
     * @return null if no problems found, Error message if the argument is not a valid option.
     */
    String oneOf(T object, Event event, Function<T, String> mapper, String errorMessageKey);

    /**
     * Validates if the argument input by the user is a valid value.
     * This method validates any possible problem with the value input by the user.
     *
     * @param event Discord event that triggered this function call.
     * @param arg   argument input by the user.
     * @return null if no argument is valid, Error message if the argument has a problem to parse it.
     */
    String validate(MessageReceivedEvent event, String arg);

    /**
     * Validates if the argument input by the user is a valid value.
     * This method validates any possible problem with the value input by the user.
     *
     * @param event Discord event that triggered this function call.
     * @param arg   argument input by the user.
     * @return null if no argument is valid, Error message if the argument has a problem to parse it.
     */
    String validate(SlashCommandInteractionEvent event, String arg);

    /**
     * Get the default value of the argument if the user does not input a value.
     *
     * @return default value object for the argument.
     */
    T getDefaultValue();

    /**
     * Get the current value of the argument.
     *
     * @return argument's value after parsing.
     */
    T getValue();

    /**
     * Set the parsed value after validations, to be used into the command.
     *
     * @param value final value after parsing.
     * @return a reference to this object.
     */
    A setValue(T value);

    /**
     * To be called if all validations passed.
     * Convert's the input String argument into the expected final object.
     *
     * @param event Discord event that triggered this function call.
     * @param arg   argument input by the user.
     * @return parsed argument.
     * @throws Exception The method should <strong>NEVER</strong> throw an exception, any possible exceptions
     *                   should be checked in the validate method.
     * @see IArgument#validate(MessageReceivedEvent, String)
     */
    T parse(MessageReceivedEvent event, String arg) throws Exception;


    /**
     * To be called if all validations passed.
     * Convert's the input String argument into the expected final object.
     *
     * @param event Discord event that triggered this function call.
     * @param arg   argument input by the user.
     * @return parsed argument.
     * @throws Exception The method should <strong>NEVER</strong> throw an exception, any possible exceptions
     *                   should be checked in the validate method.
     * @see IArgument#validate(MessageReceivedEvent, String)
     */
    T parse(SlashCommandInteractionEvent event, String arg) throws Exception;

    /**
     * Gets the argument's type.
     *
     * @return ArgumentTypes enum value.
     */
    ArgumentTypes getType();

    /**
     * Sets the value when the command is called via discord's slash command.
     * Commands are only included as Slash if Slash interface is implemented.
     *
     * @param event Discord event that triggered this function call.
     * @param value final value after parsing.
     * @return a reference to this object.
     * @see ICommand
     * @see Slash
     */
    A setSlashValue(SlashCommandInteractionEvent event, Object value) throws Exception;

    /**
     * return a list of valid values set for this argument.
     *
     * @return list of valid values.
     */
    List<T> getValidValues();

    /**
     * Add valid values to the argument.
     *
     * @param validValues list of valid values for the argument.
     * @return a reference to this object.
     */
    @SuppressWarnings("unchecked")
    A addValidValues(T... validValues);
}
