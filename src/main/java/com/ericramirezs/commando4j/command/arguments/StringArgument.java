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

import com.ericramirezs.commando4j.command.enums.ArgumentTypes;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import com.ericramirezs.commando4j.command.util.StringUtils;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Class to request an argument of type String to the user.
 */
public final class StringArgument extends Argument<StringArgument, String> {

    @RegExp
    private String regex;
    private String regexVerbose = "";
    private Function<Event, String> regexVerboseGetter = this::getVerbose;

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public StringArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.STRING);
    }

    private String getVerbose(final Event x) {
        return regexVerbose;
    }

    private String validate(@NotNull final Event event, @NotNull final String arg) {
        if (!StringUtils.isNullOrWhiteSpace(regex)) {
            if (!arg.matches(regex) && !getValidValues().contains(arg)) {
                return LocalizedFormat.format("Argument_String_Regex", event, regex, regexVerboseGetter.apply(event));
            }
        }
        switch (inRange(arg)) {
            case NOT_IN_BETWEEN:
                return LocalizedFormat.format("Argument_String_Between", event, getMin(), getMax());
            case BIGGER_THAN:
                return LocalizedFormat.format("Argument_String_TooLong", event, getMax());
            case LOWER_THAN:
                return LocalizedFormat.format("Argument_String_TooShor", event, getMin());
            default:
                return oneOf(arg, event, String::toString, "Argument_String_OneOf");
        }
    }

    @Override
    public String validate(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        return validate((Event) event, arg);
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        return validate((Event) event, arg);
    }

    @Override
    public String parse(@NotNull final MessageReceivedEvent event, final String arg) {
        return arg;
    }

    @Override
    public String parse(final SlashCommandInteractionEvent event, final String arg) {
        return arg;
    }

    /**
     * Set a regex that the input string must match in order to be valid
     *
     * @param regex   Regular expression
     * @param verbose Explanation of regular expression
     * @return a reference to this object.
     */
    public StringArgument setRegex(@RegExp final String regex, final String verbose) {
        this.regex = regex;
        this.regexVerbose = verbose;
        return this;
    }

    /**
     * Set a regex that the input string must match in order to be valid
     *
     * @param regex              Regular expression
     * @param regexVerboseGetter Function to get the explanation of regular expression
     * @return a reference to this object.
     */
    public StringArgument setRegex(@RegExp final String regex, final Function<Event, String> regexVerboseGetter) {
        this.regex = regex;
        this.regexVerboseGetter = regexVerboseGetter;
        return this;
    }

    /**
     * Set a regex that the input string must match in order to be valid
     *
     * @param regex Regular expression
     * @return a reference to this object.
     */
    public StringArgument setRegex(@RegExp final String regex) {
        this.regex = regex;
        return this;
    }

    /**
     * Get the regex for validation
     *
     * @return Regular expression
     */
    public String getRegex() {
        return regex;
    }

    @Override
    public StringArgument clone() {
        final StringArgument a = clone(new StringArgument(getName(), getPrompt()));
        a.regex = regex;
        a.regexVerbose = regexVerbose;
        a.regexVerboseGetter = regexVerboseGetter;
        return a;
    }
}
