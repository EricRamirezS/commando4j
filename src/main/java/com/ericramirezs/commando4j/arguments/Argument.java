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

package com.ericramirezs.commando4j.arguments;

import com.ericramirezs.commando4j.Slash;
import com.ericramirezs.commando4j.enums.ArgumentTypes;
import com.ericramirezs.commando4j.util.LocalizedFormat;
import com.ericramirezs.commando4j.util.StringUtils;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
    private final ArrayList<T> validValues = new ArrayList<>();
    private Double max;
    private Double min;
    private T defaultValue;
    private boolean required;
    private T value;
    private Function<Event, String> getPromptFunction = this::getPromptRaw;
    private Supplier<T> defaultValueFunction;

    /**
     * Creates an instance of an Argument Object.
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     * @param type   Category of the argument implementation.
     */
    protected Argument(@NotNull final String name, @NotNull final String prompt, @NotNull final ArgumentTypes type) {
        this.name = name;
        this.prompt = prompt;
        this.type = type;
    }

    /**
     * set a function to handle Argument's prompt.
     *
     * @param f function.
     */
    public void setPromptParser(final Function<Event, String> f) {
        this.getPromptFunction = f;
    }

    private String getPromptRaw(final Event event) {
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
    public A setDefaultValue(final T defaultValue) {
        this.defaultValue = defaultValue;
        return (A) this;
    }

    /**
     * Set a function to be called if the user did not input a value for this argument.
     *
     * @param defaultValueFunction function to generate a default value.
     * @return a reference to this object.
     */
    public A setDefaultValue(final Supplier<T> defaultValueFunction) {
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
     * @param max maximum value
     * @return a reference to this object.
     */
    public A setMax(final int max) {
        this.max = max * 1d;
        return (A) this;
    }

    /**
     * Set the maximum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @param max maximum value
     * @return a reference to this object.
     */
    public A setMax(final long max) {
        this.max = max * 1d;
        return (A) this;
    }

    /**
     * Set the maximum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @param max maximum value
     * @return a reference to this object.
     */
    public A setMax(final Double max) {
        this.max = max;
        return (A) this;
    }

    /**
     * Set the maximum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @param max maximum value
     * @return a reference to this object.
     */
    public A setMax(final float max) {
        this.max = max * 1d;
        return (A) this;
    }

    /**
     * Set the minimum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @param min minimum value
     * @return a reference to this object.
     */
    public A setMin(final int min) {
        this.min = min * 1d;
        return (A) this;
    }

    /**
     * Set the minimum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @param min minimum value
     * @return a reference to this object.
     */
    public A setMin(final long min) {
        this.min = min * 1d;
        return (A) this;
    }

    /**
     * Set the minimum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @param min minimum value
     * @return a reference to this object.
     */
    public A setMin(final Double min) {
        this.min = min;
        return (A) this;
    }

    /**
     * Set the minimum value allowed for this argument. It may be used to limit a
     * String length or a numeric max value.
     *
     * @param min minimum value
     * @return a reference to this object.
     */
    public A setMin(final float min) {
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
    protected RangeError inRange(final Number arg) {
        return inRange(arg.doubleValue());
    }

    /**
     * Checks if the value is between the expected range.
     *
     * @param arg value.
     * @return Range error type enum, RangeError.NONE if no error was found.
     * @see RangeError
     */
    protected RangeError inRange(final int arg) {
        return inRange(arg * 1d);
    }

    /**
     * Checks if the value is between the expected range.
     *
     * @param arg value.
     * @return Range error type enum, RangeError.NONE if no error was found.
     * @see RangeError
     */
    protected RangeError inRange(final long arg) {
        return inRange(arg * 1d);
    }

    /**
     * Checks if the value is between the expected range.
     *
     * @param arg value.
     * @return Range error type enum, RangeError.NONE if no error was found.
     * @see RangeError
     */
    protected RangeError inRange(final float arg) {
        return inRange(arg * 1d);
    }

    /**
     * Checks if the value is between the expected range.
     *
     * @param arg value.
     * @return Range error type enum, RangeError.NONE if no error was found.
     * @see RangeError
     */
    protected RangeError inRange(@NotNull final String arg) {
        return inRange(arg.length());
    }

    /**
     * Checks if the value is between the expected range.
     *
     * @param arg value.
     * @return Range error type enum, RangeError.NONE if no error was found.
     * @see RangeError
     */
    protected RangeError inRange(final double arg) {
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

    private boolean isOneOf(final T arg) {
        if (validValues.size() == 0) return true;
        return validValues.contains(arg);
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public T getValue() {
        if (value == null) value = getDefaultValue();
        return value;
    }

    @Override
    public A setValue(final T value) {
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
    public String getPrompt(final Event event) {
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
    public final A addValidValues(final T... validValues) {
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
    public final String validateNull(@Nullable final String arg, final MessageReceivedEvent event) {
        if (arg == null && getDefaultValue() == null && isRequired())
            return LocalizedFormat.format("Argument_InvalidNull", event, getName());
        return null;
    }

    @Override
    @Nullable
    public String oneOf(final T object, final Event event, final Function<T, String> mapper, final String errorMessageKey) {
        if (isOneOf(object)) return null;
        return LocalizedFormat.format(errorMessageKey, event,
                getValidValues().stream().map(mapper).collect(Collectors.joining("\n")));
    }

    @Override
    public A setSlashValue(final SlashCommandInteractionEvent event, final Object value) throws Exception {
        final String validate = validate(event, value.toString());
        if (!StringUtils.isNullOrWhiteSpace(validate)) {
            Slash.sendReply(event, validate);
            throw new Exception(validate);
        } else {
            try {
                setValue(parse(event, value.toString()));
            } catch (final Exception ignored) {
            }
        }
        return (A) this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If your Argument child doesn't have additional attribute, you may just do
     * <code>return clone(new MyArgument(getName(), getPrompt()));</code>
     * </p>
     *
     * @return a clone of this instance.
     */
    @Override
    public abstract A clone();

    protected A clone(final @NotNull A clone) {
        for (final T val : validValues) {
            clone.addValidValues(val);
        }
        clone.setMax(max);
        clone.setMin(min);
        clone.setDefaultValue(defaultValue);
        clone.setRequired(required);
        clone.setPromptParser(getPromptFunction);
        clone.setDefaultValue(defaultValueFunction);
        return clone;
    }

    void setRequired(final boolean required) {
        this.required = required;
    }
}
