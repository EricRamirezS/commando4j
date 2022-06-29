package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;

import java.util.List;
import java.util.function.Function;

public interface IArgument<A extends IArgument, T> {

    boolean isRequired();

    Double getMax();

    Double getMin();

    String getName();

    String getPrompt();

    String getPrompt(Event event);

    String validateNull(String arg, MessageReceivedEvent event);

    String oneOf(T object, MessageReceivedEvent event, Function<T, String> mapper, String errorMessageKey);

    String validate(MessageReceivedEvent event, String arg);

    T getDefaultValue();

    T getValue();

    A setValue(T value);

    T parse(MessageReceivedEvent event, String arg);

    ArgumentTypes getType();

    A setSlashValue(Object value);

    List<T> getValidValues();

    A addValidValues(T... validValues);
}
