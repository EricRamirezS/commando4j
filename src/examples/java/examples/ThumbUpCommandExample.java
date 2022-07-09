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

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.Slash;
import org.EricRamirezS.jdacommando.command.arguments.BooleanArgument;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.command.Command;
import org.EricRamirezS.jdacommando.command.enums.Emoji;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedArgumentNameException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public class ThumbUpCommandExample extends Command implements Slash {

    public ThumbUpCommandExample() throws DuplicatedArgumentNameException {
        super("ThumbUp", "misc", "I'll reply with thumbs up if I agree.",
                new BooleanArgument("agree", "Should I agree?")
                        .setRequired()
                        .setDefaultValue(true));
    }

    @Override
    public void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        boolean bool = (boolean) args.get("agree").getValue();
        if (bool)
            sendReply(event, Emoji.THUMBS_UP);
        else
            sendReply(event, Emoji.THUMBS_DOWN);
    }

    @Override
    public void run(@NotNull SlashCommandInteractionEvent event, @UnmodifiableView @NotNull Map<String, IArgument> args) {
        boolean bool = (boolean) args.get("agree").getValue();
        if (bool)
            Slash.sendReply(event, Emoji.THUMBS_UP, false);
        else
            Slash.sendReply(event, Emoji.THUMBS_DOWN, false);
    }
}
