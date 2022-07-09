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
import com.ericramirezs.commando4j.command.enums.RangeError;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Implementation class of the IArgument Interface.
 *
 * @param <A> Self argument object that implements Argument.
 * @param <T> result object after parsing the argument.
 * @see IArgument
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class Argument<A extends Argument, T> implements IArgument<A, T> {

    private final String name;
    private final String prompt;
    private final ArgumentTypes type;
    private final List<T> validValues = new ArrayList<>();
    private Double max;
    private Double min;
    private T defaultValue;
    private boolean required;
    private T value;
    private Function<Event, String> getPromptFunction = this::getPromptRaw;
    private Supplier<T> defaultValueFunction;

    protected Argument(@NotNull String name, @NotNull String prompt, @NotNull ArgumentTypes type) {
        this.name = name;
        this.prompt = prompt;
        this.type = type;
    }

    /**
     * set a function to handle Argument's prompt.
     *
     * @param f function.
     */
    public void setPromptParser(Function<Event, String> f) {
        this.getPromptFunction = f;
    }

    private String getPromptRaw(Event event) {
        return prompt;
    }

    /**
     * Get the raw value of prompt property.
     *
     * @return prompt value's.
     */
    public String getPromptRaw() {
        return prompt;
    }

    /**
     * Set the default value if the user did not input a value for this argument.
     *
     * @param defaultValue Value to be used if no value was input by the user.
     * @return a reference to this object.
     */
    public A setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return (A) this;
    }

    /**
     * Set a function to be called if the user did not input a value for this argument.
     *
     * @param defaultValueFunction function to generate a default value.
     * @return a reference to this object.
     */
    public A setDefaultValue(Supplier<T> defaultValueFunction) {
        this.defaultValueFunction = defaultValueFunction;
        return (A) this;
    }

    /**
     * Set if the argument usage is mandatory for the user.
     *
     * @return a reference to this object.
     */
    public A setRequired() {
        this.required = true;
        return (A) this;
    }

    /**
     * Set the maximum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @return a reference to this object.
     */
    public A setMax(int max) {
        this.max = max * 1d;
        return (A) this;
    }

    /**
     * Set the maximum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @return a reference to this object.
     */
    public A setMax(long max) {
        this.max = max * 1d;
        return (A) this;
    }

    /**
     * Set the maximum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @return a reference to this object.
     */
    public A setMax(double max) {
        this.max = max;
        return (A) this;
    }

    /**
     * Set the maximum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @return a reference to this object.
     */
    public A setMax(float max) {
        this.max = max * 1d;
        return (A) this;
    }

    /**
     * Set the minimum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @return a reference to this object.
     */
    public A setMin(int min) {
        this.min = min * 1d;
        return (A) this;
    }

    /**
     * Set the minimum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @return a reference to this object.
     */
    public A setMin(long min) {
        this.min = min * 1d;
        return (A) this;
    }

    /**
     * Set the minimum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @return a reference to this object.
     */
    public A setMin(double min) {
        this.min = min;
        return (A) this;
    }

    /**
     * Set the minimum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @return a reference to this object.
     */
    public A setMin(float min) {
        this.min = min * 1d;
        return (A) this;
    }

    /**
     * Checks if the value is between the expected range.
     *
     * @param arg value.
     * @return Range error type enum, RangeError.NONE if no error was found.
     * @see RangeError
     */
    protected RangeError inRange(int arg) {
        return inRange(arg * 1d);
    }

    /**
     * Checks if the value is between the expected range.
     *
     * @param arg value.
     * @return Range error type enum, RangeError.NONE if no error was found.
     * @see RangeError
     */
    protected RangeError inRange(long arg) {
        return inRange(arg * 1d);
    }

    /**
     * Checks if the value is between the expected range.
     *
     * @param arg value.
     * @return Range error type enum, RangeError.NONE if no error was found.
     * @see RangeError
     */
    protected RangeError inRange(float arg) {
        return inRange(arg * 1d);
    }

    /**
     * Checks if the value is between the expected range.
     *
     * @param arg value.
     * @return Range error type enum, RangeError.NONE if no error was found.
     * @see RangeError
     */
    protected RangeError inRange(@NotNull String arg) {
        return inRange(arg.length());
    }

    /**
     * Checks if the value is between the expected range.
     *
     * @param arg value.
     * @return Range error type enum, RangeError.NONE if no error was found.
     * @see RangeError
     */
    protected RangeError inRange(double arg) {
        if (getMin() != null && getMax() == null) {
            if (getMin() > arg) {
                return RangeError.LOWER_THAN;
            }
        }
        if (getMax() != null && getMin() == null) {
            if (getMax() < arg) {
                return RangeError.BIGGER_THAN;
            }
        }
        if (getMin() != null && getMax() != null) {
            if (getMin() > arg && getMax() < arg) {
                return RangeError.NOT_IN_BETWEEN;
            }
        }
        return RangeError.NONE;
    }

    private boolean isOneOf(T arg) {
        if (validValues.size() == 0) return true;
        return validValues.contains(arg);
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public A setValue(T value) {
        this.value = value;
        return (A) this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrompt() {
        return getPrompt(null);
    }

    @Override
    public String getPrompt(Event event) {
        return getPromptFunction.apply(event);
    }

    @Override
    public T getDefaultValue() {
        if (defaultValueFunction != null) {
            return defaultValueFunction.get();
        }
        return defaultValue;
    }

    @Override
    @SafeVarargs
    public final A addValidValues(T... validValues) {
        this.validValues.addAll(Arrays.asList(validValues));
        return (A) this;
    }

    @Override
    public Double getMax() {
        return max;
    }

    @Override
    public Double getMin() {
        return min;
    }

    @Override
    @Contract(" -> new")
    public @NotNull List<T> getValidValues() {
        return Collections.unmodifiableList(validValues);
    }

    @Override
    public ArgumentTypes getType() {
        return type;
    }

    @Override
    public final String validateNull(@Nullable String arg, MessageReceivedEvent event) {
        if (arg == null && getDefaultValue() == null && isRequired())
            return LocalizedFormat.format("Argument_InvalidNull", event, getName());
        return null;
    }

    @Override
    @Nullable
    public String oneOf(T object, MessageReceivedEvent event, Function<T, String> mapper, String errorMessageKey) {
        if (isOneOf(object)) return null;
        return LocalizedFormat.format(errorMessageKey, event, getValidValues().stream().map(mapper).collect(Collectors.joining("\n")));
    }

    @Override
    public A setSlashValue(Object value) {
        //noinspection unchecked
        setValue((T) value);
        return (A) this;
    }
}
