package examples;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.arguments.LocalTimeArgument;
import org.EricRamirezS.jdacommando.command.command.Command;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedArgumentNameException;
import org.EricRamirezS.jdacommando.command.tools.DateTimeUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.Map;

public class TimeToTagCommandExample extends Command {

    public TimeToTagCommandExample() throws DuplicatedArgumentNameException {
        super("time", "datetime", "creates a discord timestamp tag from a given time",
                new LocalTimeArgument("time", "time in UTC").setRequired());
    }

    @Override
    public void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        LocalTime localTime = (LocalTime) args.get("time").getValue();
        sendReply(event, DateTimeUtils.toDiscordTimeStamp(localTime));
    }
}
