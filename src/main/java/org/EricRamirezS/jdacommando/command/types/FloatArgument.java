package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FloatArgument extends Argument<Float> {

    public FloatArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.FLOAT);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        try {
            Float number = Float.parseFloat(arg);
            return switch (inRange(number)) {
                case NOT_IN_BETWEEN ->
                        LocalizedFormat.format("Argument_Integer_Between", event, number, getMin(), getMax());
                case LOWER_THAN -> LocalizedFormat.format("Argument_Integer_GreaterThan", event, number, getMin());
                case BIGGER_THAN -> LocalizedFormat.format("Argument_Integer_LessThan", event, number, getMax());
                default -> oneOf(number, event, Object::toString, "Argument_Integer_OneOf");
            };
        } catch (Exception ex) {
            return LocalizedFormat.format("Argument_Integer_Invalid", event, arg);
        }
    }

    @Override
    public @NotNull Float parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        return Float.parseFloat(arg);
    }
}
