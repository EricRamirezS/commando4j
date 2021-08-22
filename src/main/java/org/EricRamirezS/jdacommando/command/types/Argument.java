package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.EricRamirezS.jdacommando.command.enums.RangeError;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Argument<T> {

    private final String name;
    private final String prompt;
    private final ArgumentTypes type;
    private Double max;
    private Double min;
    private T defaultValue;
    private boolean required = true;
    private final List<T> validValues = new ArrayList<>();
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName(){
        return name;
    }

    public String getPrompt(){
        return prompt;
    }

    public Object getDefaultValue(){
        return defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setMin(int min) {
        this.min = min*1d;
    }

    public void setMax(int max) {
        this.max = max*1d;
    }

    public void setMin(long min) {
        this.min = min*1d;
    }

    public void setMax(long max) {
        this.max = max*1d;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(float max) {
        this.max = max*1d;
    }

    public void setMin(float min) {
        this.min = min*1d;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @SafeVarargs
    public final void addValidValues(T... validValues) {
        this.validValues.addAll(Arrays.asList(validValues));
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

    public abstract String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg);
    public abstract T parse(@NotNull GuildMessageReceivedEvent event, String arg);

    public ArgumentTypes getType() {
        return type;
    }

    public final String validateNull(@Nullable String arg){
        if (arg == null && getDefaultValue() == null && isRequired())
                return MessageFormat.format("Se necesita un valor válido para el parámetro `{0}`", getName());
        return null;
    }

    protected boolean isOneOf(T arg){
        if (validValues.size() == 0) return true;
        return validValues.contains(arg);
    }

    protected RangeError inRange(int arg){
        return inRange(arg * 1d);
    }

    protected RangeError inRange(long arg){
        return inRange(arg * 1d);
    }

    protected RangeError inRange(float arg){
        return inRange(arg * 1d);
    }

    protected RangeError inRange(@NotNull String arg){
        return inRange(arg.length());
    }

    protected RangeError inRange(double arg){
        if (getMin() != null && getMax() == null){
            if (getMin()> arg){
                return RangeError.LOWER_THAN;
            }
        }
        if (getMax() != null && getMin() == null){
            if (getMax() < arg){
                return RangeError.BIGGER_THAN;
            }
        }
        if (getMin() != null && getMax() != null){
            if (getMin()> arg && getMax() < arg){
                return RangeError.NOT_IN_BETWEEN;
            }
        }
        return RangeError.NONE;
    }
}
