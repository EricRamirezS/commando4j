package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.EricRamirezS.jdacommando.command.enums.RangeError;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public abstract class Argument<T> {

    private final String name;
    private final String prompt;
    private final ArgumentTypes type;
    private final List<T> validValues = new ArrayList<>();
    private Double max;
    private Double min;
    private T defaultValue = null;
    private boolean required = true;
    private T value;

    protected Argument(@NotNull String name, @Nullable String prompt, @NotNull ArgumentTypes type) {
        this.name = name;
        this.prompt = prompt;
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public Argument<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getPrompt() {
        return prompt;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Argument<T> setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public Argument<T> setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @SafeVarargs
    public final Argument<T> addValidValues(T... validValues) {
        this.validValues.addAll(Arrays.asList(validValues));
        return this;
    }

    public Double getMax() {
        return max;
    }

    public Argument<T> setMax(int max) {
        this.max = max * 1d;
        return this;
    }

    public Argument<T> setMax(long max) {
        this.max = max * 1d;
        return this;
    }

    public Argument<T> setMax(double max) {
        this.max = max;
        return this;
    }

    public Argument<T> setMax(float max) {
        this.max = max * 1d;
        return this;
    }

    public Double getMin() {
        return min;
    }

    public Argument<T> setMin(int min) {
        this.min = min * 1d;
        return this;
    }

    public Argument<T> setMin(long min) {
        this.min = min * 1d;
        return this;
    }

    public Argument<T> setMin(double min) {
        this.min = min;
        return this;
    }

    public Argument<T> setMin(float min) {
        this.min = min * 1d;
        return this;
    }

    @Contract(" -> new")
    public @NotNull List<T> getValidValues() {
        return new ArrayList<>(validValues);
    }

    public ArgumentTypes getType() {
        return type;
    }

    public final String validateNull(@Nullable String arg, MessageReceivedEvent event) {
        if (arg == null && getDefaultValue() == null && isRequired())
            return LocalizedFormat.format("Argument_Invalid", event, getName());
        return null;
    }

    private boolean isOneOf(T arg) {
        if (validValues.size() == 0) return true;
        return validValues.contains(arg);
    }

    protected RangeError inRange(int arg) {
        return inRange(arg * 1d);
    }

    protected RangeError inRange(long arg) {
        return inRange(arg * 1d);
    }

    protected RangeError inRange(float arg) {
        return inRange(arg * 1d);
    }

    protected RangeError inRange(@NotNull String arg) {
        return inRange(arg.length());
    }

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

    @Nullable
    String oneOf(T object, MessageReceivedEvent event, Function<T, String> mapper, String errorMessageKey) {
        if (isOneOf(object)) return null;
        return LocalizedFormat.format(errorMessageKey, event,
                getValidValues().stream().map(mapper).collect(Collectors.joining("\n")));
    }

    public abstract String validate(@NotNull MessageReceivedEvent event, @NotNull String arg);

    public abstract T parse(@NotNull MessageReceivedEvent event, String arg);

    public Argument<T> setSlashValue(Object value) {
        //noinspection unchecked
        setValue((T) value);
        return this;
    }
}
