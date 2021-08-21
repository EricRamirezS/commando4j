package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseArgument<T> {

    private final String name;
    private final String prompt;
    private final ArgumentTypes type;
    private int max;
    private int min;
    private T defaultValue;
    private boolean required = true;
    private List<T> validValues = new ArrayList<>();

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
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @SafeVarargs
    public final void addValidValues(T... validValues) {
        this.validValues.addAll(Arrays.asList(validValues));
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    @Contract(" -> new")
    public @NotNull List<T> getValidValues() {
        return new ArrayList<>(validValues);
    }

    protected BaseArgument(@NotNull String name, @Nullable String prompt, @NotNull ArgumentTypes type) {
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
}
