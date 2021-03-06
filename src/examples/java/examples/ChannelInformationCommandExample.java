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
import com.ericramirezs.commando4j.arguments.ChannelArgument;
import com.ericramirezs.commando4j.arguments.IArgument;
import com.ericramirezs.commando4j.command.Command;
import com.ericramirezs.commando4j.util.DateTimeUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChannelInformationCommandExample extends Command implements Slash {

    public ChannelInformationCommandExample() throws Exception {
        super("channelinfo", "examples", "Get detailed information about an specific channel",
                new ChannelArgument("channel", "Channel from which to obtain information")
                        .setRequired()
        );
        addExamples("channelinfo General",
                "channelinfo <#888888888888888888>",
                "channelinfo #General");

        addClientPermissions(Permission.MESSAGE_HISTORY, Permission.MESSAGE_EMBED_LINKS);

        setGuildOnly();
    }

    @Override
    public void run(MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        Channel channel = (Channel) args.get("channel").getValue();
        if (channel instanceof TextChannel) sendReply(event, prepareBuilder((TextChannel) channel));
        else if (channel instanceof VoiceChannel) sendReply(event, prepareBuilder((VoiceChannel) channel));
        else if (channel instanceof Category) sendReply(event, prepareBuilder((Category) channel));
        else sendReply(event, prepareBuilder(channel));
    }

    public void run(SlashCommandInteractionEvent event, Map<String, IArgument> args) {
        Channel channel = (Channel) args.get("channel").getValue();

        if (channel instanceof TextChannel)
            event.replyEmbeds(prepareBuilder((TextChannel) channel).build()).setEphemeral(true).queue();
        else if (channel instanceof VoiceChannel)
            event.replyEmbeds(prepareBuilder((VoiceChannel) channel).build()).setEphemeral(true).queue();
        else if (channel instanceof Category)
            event.replyEmbeds(prepareBuilder((Category) channel).build()).setEphemeral(true).queue();
        else
            event.replyEmbeds(prepareBuilder(channel).build()).setEphemeral(true).queue();
    }

    public static Member mostCommonUser(List<Message> list) {
        Map<Member, Integer> map = new HashMap<>();

        for (Message t : list) {
            Integer val = map.get(t.getMember());
            map.put(t.getMember(), val == null ? 1 : val + 1);
        }

        Map.Entry<Member, Integer> max = null;

        for (Map.Entry<Member, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return Objects.requireNonNull(max).getKey();
    }

    private EmbedBuilder prepareBuilder(TextChannel ch) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(ch.getName(), null);

        eb.setColor(new Color(255, 0, 54));
        eb.setDescription(ch.getTopic());
        eb.addField("Category:", Objects.requireNonNull(Objects.requireNonNull(ch.getParentCategory()).getName()), false);

        try {
            MessageHistory h = ch.getHistory();
            Member top = mostCommonUser(h.retrievePast(100).complete());
            eb.addField("Top Last 100 Messages", top.getEffectiveName(), false);
        } catch (Exception ignored) {
        }
        eb.addField("Created Date", DateTimeUtils.toDiscordTimeStamp(ch.getTimeCreated(), TimeFormat.DATE_TIME_LONG), false);
        eb.addField("Threads:", String.valueOf(ch.getThreadChannels().size()), false);
        eb.addField("id:", ch.getId(), false);

        return eb;
    }

    private EmbedBuilder prepareBuilder(VoiceChannel ch) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(ch.getName(), null);
        eb.setColor(new Color(220, 186, 0));
        eb.addField("Category:", Objects.requireNonNull(Objects.requireNonNull(ch.getParentCategory()).getName()), false);
        eb.addField("Created Date", DateTimeUtils.toDiscordTimeStamp(ch.getTimeCreated(), TimeFormat.DATE_TIME_LONG), false);
        eb.addField("id:", ch.getId(), false);
        return eb;
    }

    private EmbedBuilder prepareBuilder(Channel ch) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(ch.getName(), null);
        eb.setColor(new Color(19, 255, 0));
        eb.addField("Created Date", DateTimeUtils.toDiscordTimeStamp(ch.getTimeCreated(), TimeFormat.DATE_TIME_LONG), false);
        eb.addField("id:", ch.getId(), false);
        return eb;
    }

    private EmbedBuilder prepareBuilder(Category ch) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(ch.getName(), null);
        eb.setColor(new Color(0, 62, 224));
        eb.addField("Created Date", DateTimeUtils.toDiscordTimeStamp(ch.getTimeCreated(), TimeFormat.DATE_TIME_LONG), false);
        eb.addField("id:", ch.getId(), false);

        return eb;
    }
}
