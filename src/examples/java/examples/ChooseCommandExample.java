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

import edu.rice.cs.util.ArgumentTokenizer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.command.Command;
import org.EricRamirezS.jdacommando.command.Slash;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.arguments.StringArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChooseCommandExample extends Command implements Slash {

    public ChooseCommandExample() throws Exception {
        super(
                "choose",
                "utils",
                "Chooses a random option from a given list. Options separated by whitespaces",
                new StringArgument("options", "list of options")
                        .setRequired()
        );
        addClientPermissions(Permission.MESSAGE_SEND);
    }

    @Override
    public void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        String optionsRaw = args.get("options").getValue().toString();
        List<String> options = ArgumentTokenizer.tokenize(optionsRaw);
        MessageChannel channel = event.getChannel();
        Random rand = new Random();
        String randomElement = options.get(rand.nextInt(options.size()));
        sendReply(event, randomElement);

    }

    @Override
    public void run(@NotNull SlashCommandInteractionEvent event, @UnmodifiableView @NotNull Map<String, IArgument> args) {
        String optionsRaw = args.get("options").getValue().toString();
        List<String> options = ArgumentTokenizer.tokenize(optionsRaw);
        Random rand = new Random();
        String randomElement = options.get(rand.nextInt(options.size()));
        Slash.sendReply(event, randomElement);
    }
}
