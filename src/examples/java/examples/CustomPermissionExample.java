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
import com.ericramirezs.commando4j.arguments.IArgument;
import com.ericramirezs.commando4j.command.Command;
import com.ericramirezs.commando4j.enums.Emoji;
import com.ericramirezs.commando4j.exceptions.DuplicatedArgumentNameException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CustomPermissionExample extends Command implements Slash {

    public CustomPermissionExample() throws DuplicatedArgumentNameException {
        super("permTest", "examples", "This command only works if the user's name contains an \"R\"");
    }

    protected String hasPermission(MessageReceivedEvent event) {
        if (event.getAuthor().getName().contains("R")) return null;
        return "You cannot execute this command, only users whose nickname contains an \"R\" may execute it";
    }

    @Override
    protected String hasPermission(SlashCommandInteractionEvent event) {
        if (event.getUser().getName().contains("R")) return null;
        return "You cannot execute this command, only users whose nickname contains an \"R\" may execute it";
    }

    /* You may use the generic version of hasPermission to handle both cases in one method.
    @Override
    protected String hasPermission(Event event) {
        if (event instanceof MessageReceivedEvent e) {
            if (e.getAuthor().getName().contains("R")) return null;
        } else if (event instanceof SlashCommandInteractionEvent e)
            if (e.getUser().getName().contains("R")) return null;
        return "You cannot execute this command, only users whose nickname contains an \"R\" may execute it";
    }*/

    @Override
    public void run(MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        sendReply(event,"Hey!, your name contains an \"R\", so you're cool " + Emoji.SUNGLASSES);
    }

    @Override
    public void run(SlashCommandInteractionEvent event, Map<String, IArgument> args) {
        event.reply("Hey!, your name contains an \"R\", so you're cool " + Emoji.SUNGLASSES).setEphemeral(true).queue();
    }
}
