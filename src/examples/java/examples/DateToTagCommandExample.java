package examples;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.arguments.LocalDateArgument;
import org.EricRamirezS.jdacommando.command.command.Command;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedArgumentNameException;
import org.EricRamirezS.jdacommando.command.tools.DateTimeUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Map;

public class DateToTagCommandExample extends Command {

    public DateToTagCommandExample() throws DuplicatedArgumentNameException {
        super("date", "datetime", "creates a discord timestamp tag from a given date",
                new LocalDateArgument("date","date").setRequired());
    }

    @Override
    public void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        LocalDate localDate = (LocalDate) args.get("date").getValue();
        sendReply(event, DateTimeUtils.toDiscordTimeStamp(localDate));
    }
}
