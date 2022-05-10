package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.EricRamirezS.jdacommando.command.enums.RangeError;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public abstract class Argument<T> {

    private final String name;
    private final String prompt;
    private final ArgumentTypes type;
    private Double max;
    private Double min;
    private T defaultValue = null;
    private boolean required = true;
    private final List<T> validValues = new ArrayList<>();
    private T value;

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getPrompt() {
        return prompt;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public Argument setValue(T value) {
        this.value = value;
        return this;
    }

    public Argument setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Argument setMin(int min) {
        this.min = min * 1d;
        return this;
    }

    public Argument setMax(int max) {
        this.max = max * 1d;
        return this;
    }

    public Argument setMin(long min) {
        this.min = min * 1d;
        return this;
    }

    public Argument setMax(long max) {
        this.max = max * 1d;
        return this;
    }

    public Argument setMax(double max) {
        this.max = max;
        return this;
    }

    public Argument setMin(double min) {
        this.min = min;
        return this;
    }

    public Argument setMax(float max) {
        this.max = max * 1d;
        return this;
    }

    public Argument setMin(float min) {
        this.min = min * 1d;
        return this;
    }

    public Argument setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @SafeVarargs
    public final Argument addValidValues(T... validValues) {
        this.validValues.addAll(Arrays.asList(validValues));
        return this;
    }

    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    @Contract(" -> new")
    public @NotNull List<T> getValidValues() {
        return new ArrayList<>(validValues);
    }

    protected Argument(@NotNull String name, @Nullable String prompt, @NotNull ArgumentTypes type) {
        this.name = name;
        this.prompt = prompt;
        this.type = type;
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

}
