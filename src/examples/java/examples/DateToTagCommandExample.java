/*
 *
 *    Copyright 2022 Eric Bastian Ramírez Santis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package examples;

import com.ericramirezs.commando4j.command.Slash;
import com.ericramirezs.commando4j.command.arguments.IArgument;
import com.ericramirezs.commando4j.command.arguments.LocalDateArgument;
import com.ericramirezs.commando4j.command.command.Command;
import com.ericramirezs.commando4j.command.exceptions.DuplicatedArgumentNameException;
import com.ericramirezs.commando4j.command.util.DateTimeUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Map;

public class DateToTagCommandExample extends Command implements Slash {

    public DateToTagCommandExample() throws DuplicatedArgumentNameException {
        super("date", "examples", "creates a discord timestamp tag from a given date",
                new LocalDateArgument("date", "date").setRequired());
    }

    @Override
    public void run(MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        LocalDate localDate = (LocalDate) args.get("date").getValue();
        sendReply(event, DateTimeUtils.toDiscordTimeStamp(localDate));
    }

    public void run(SlashCommandInteractionEvent event, Map<String, IArgument> args) {
        LocalDate localDate = (LocalDate) args.get("date").getValue();
        sendReply(event, DateTimeUtils.toDiscordTimeStamp(localDate));
    }
}
