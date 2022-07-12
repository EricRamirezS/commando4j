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

import com.ericramirezs.commando4j.Slash;
import com.ericramirezs.commando4j.arguments.BooleanArgument;
import com.ericramirezs.commando4j.arguments.IArgument;
import com.ericramirezs.commando4j.command.Command;
import com.ericramirezs.commando4j.enums.Emoji;
import com.ericramirezs.commando4j.exceptions.DuplicatedArgumentNameException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ThumbUpCommandExample extends Command implements Slash {

    public ThumbUpCommandExample() throws DuplicatedArgumentNameException {
        super("ThumbUp", "examples", "I'll reply with thumbs up if I agree.",
                new BooleanArgument("agree", "Should I agree?")
                        .setRequired()
                        .setDefaultValue(true));
    }

    @Override
    public void run(MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        boolean bool = (boolean) args.get("agree").getValue();
        if (bool)
            sendReply(event, Emoji.THUMBS_UP);
        else
            sendReply(event, Emoji.THUMBS_DOWN);
    }

    public void run(SlashCommandInteractionEvent event, Map<String, IArgument> args) {
        boolean bool = (boolean) args.get("agree").getValue();
        if (bool)
            sendReply(event, Emoji.THUMBS_UP, false);
        else
            sendReply(event, Emoji.THUMBS_DOWN, false);
    }
}
