package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.EricRamirezS.jdacommando.command.enums.RangeError;
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

    public void setPromptParser(Function<Event, String> f) {
        this.getPromptFunction = f;
    }

    private String getPromptRaw(Event event) {
        return prompt;
    }

    public String getPromptRaw() {
        return prompt;
    }

    public T getValue() {
        return value;
    }

    public A setValue(T value) {
        this.value = value;
        return (A) this;
    }

    public String getName() {
        return name;
    }

    public String getPrompt() {
        return getPrompt(null);
    }

    public String getPrompt(Event event) {
        return getPromptFunction.apply(event);
    }

    public T getDefaultValue() {
        if (defaultValueFunction != null){
            return defaultValueFunction.get();
        }
        return defaultValue;
    }

    public A setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return (A) this;
    }

    public A setDefaultValue(Supplier<T> defaultValueFunction){
        this.defaultValueFunction = defaultValueFunction;
        return (A) this;
    }

    public boolean isRequired() {
        return required;
    }

    public A setRequired() {
        this.required = true;
        return (A) this;
    }

    @SafeVarargs
    public final A addValidValues(T... validValues) {
        this.validValues.addAll(Arrays.asList(validValues));
        return (A) this;
    }

    public Double getMax() {
        return max;
    }

    public A setMax(int max) {
        this.max = max * 1d;
        return (A) this;
    }

    public A setMax(long max) {
        this.max = max * 1d;
        return (A) this;
    }

    public A setMax(double max) {
        this.max = max;
        return (A) this;
    }

    public A setMax(float max) {
        this.max = max * 1d;
        return (A) this;
    }

    public Double getMin() {
        return min;
    }

    public A setMin(int min) {
        this.min = min * 1d;
        return (A) this;
    }

    public A setMin(long min) {
        this.min = min * 1d;
        return (A) this;
    }

    public A setMin(double min) {
        this.min = min;
        return (A) this;
    }

    public A setMin(float min) {
        this.min = min * 1d;
        return (A) this;
    }

    @Contract(" -> new")
    public @NotNull List<T> getValidValues() {
        return Collections.unmodifiableList(validValues);
    }

    public ArgumentTypes getType() {
        return type;
    }

    public final String validateNull(@Nullable String arg, MessageReceivedEvent event) {
        if (arg == null && getDefaultValue() == null && isRequired())
            return LocalizedFormat.format("Argument_InvalidNull", event, getName());
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
    public String oneOf(T object, MessageReceivedEvent event, Function<T, String> mapper, String errorMessageKey) {
        if (isOneOf(object)) return null;
        return LocalizedFormat.format(errorMessageKey, event,
                getValidValues().stream().map(mapper).collect(Collectors.joining("\n")));
    }

    public A setSlashValue(Object value) {
        //noinspection unchecked
        setValue((T) value);
        return (A) this;
    }


}
