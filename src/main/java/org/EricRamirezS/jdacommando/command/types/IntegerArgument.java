package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class IntegerArgument extends Argument<Integer> {

    public IntegerArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.INTEGER);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        try {
            Integer number = Integer.parseInt(arg);
            return switch (inRange(number)) {
                case NOT_IN_BETWEEN ->
                        LocalizedFormat.format("Argument_Float_Between", event, number, getMin(), getMax());
                case LOWER_THAN -> LocalizedFormat.format("Argument_Float_GreaterThan", event, number, getMin());
                case BIGGER_THAN -> LocalizedFormat.format("Argument_Float_LessThan", event, number, getMax());
                default -> oneOf(number, event, Object::toString, "Argument_Float_OneOf");
            };
        } catch (Exception ex){
            return LocalizedFormat.format("Argument_Float_Invalid", event, arg);
        }
    }

    @Override
    public @NotNull Integer parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        return Integer.parseInt(arg);
    }
}
