package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.EricRamirezS.jdacommando.command.tools.StringUtils;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public final class StringArgument extends Argument<StringArgument, String> {

    private @RegExp String regex;
    private String regexVerbose = "";
    private Function<MessageReceivedEvent, String> regexVerboseGetter = this::getVerbose;

    public StringArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.STRING);
    }

    private String getVerbose(Event x) {
        return regexVerbose;
    }

    @Override
    public String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (!StringUtils.isNullOrWhiteSpace(regex)) {
            if (!arg.matches(regex) && !getValidValues().contains(arg)) {
                return LocalizedFormat.format("Argument_String_Regex", event, regex, regexVerboseGetter.apply(event));
            }
        }
        return switch (inRange(arg)) {
            case NOT_IN_BETWEEN -> LocalizedFormat.format("Argument_String_Between", event, getMin(), getMax());
            case BIGGER_THAN -> LocalizedFormat.format("Argument_String_TooLong", event, getMax());
            case LOWER_THAN -> LocalizedFormat.format("Argument_String_TooShor", event, getMin());
            default -> oneOf(arg, event, Objects::toString, "Argument_String_OneOf");
        };
    }

    @Override
    public String parse(@NotNull MessageReceivedEvent event, String arg) {
        return arg;
    }

    public StringArgument setRegex(@RegExp String regex, String verbose) {
        this.regex = regex;
        this.regexVerbose = verbose;
        return this;
    }

    public StringArgument setRegex(@RegExp String regex, Function<MessageReceivedEvent, String> regexVerboseGetter) {
        this.regex = regex;
        this.regexVerboseGetter = regexVerboseGetter;
        return this;
    }

    public StringArgument setRegex(@RegExp String regex) {
        this.regex = regex;
        return this;
    }

    public String getRegex(String regex) {
        return regex;
    }
}
