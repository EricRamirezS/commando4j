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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.EricRamirezS.jdacommando.command.Slash;
import org.EricRamirezS.jdacommando.command.arguments.ChannelArgument;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.command.Command;
import org.EricRamirezS.jdacommando.command.tools.DateTimeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChannelInformationCommandExample extends Command implements Slash {

    public ChannelInformationCommandExample() throws Exception {
        super("channelinfo", "utils", "Get detailed information about an specific channel",
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
    public void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        Channel channel = (Channel) args.get("channel").getValue();
        if (channel instanceof TextChannel c) sendReply(event, prepareBuilder(c));
        else if (channel instanceof VoiceChannel c) sendReply(event, prepareBuilder(c));
        else if (channel instanceof Category c) sendReply(event, prepareBuilder(c));
        else sendReply(event, prepareBuilder(channel));
    }

    @Override
    public void run(@NotNull SlashCommandInteractionEvent event, @UnmodifiableView @NotNull Map<String, IArgument> args) {
        Channel channel = (Channel) args.get("channel").getValue();

        if (channel instanceof TextChannel c)
            event.replyEmbeds(prepareBuilder(c).build()).setEphemeral(true).queue();
        else if (channel instanceof VoiceChannel c)
            event.replyEmbeds(prepareBuilder(c).build()).setEphemeral(true).queue();
        else if (channel instanceof Category c)
            event.replyEmbeds(prepareBuilder(c).build()).setEphemeral(true).queue();
        else
            event.replyEmbeds(prepareBuilder(channel).build()).setEphemeral(true).queue();
    }

    public static Member mostCommonUser(@NotNull List<Message> list) {
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

    private @NotNull EmbedBuilder prepareBuilder(@NotNull TextChannel ch) {
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

    private @NotNull EmbedBuilder prepareBuilder(@NotNull VoiceChannel ch) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(ch.getName(), null);
        eb.setColor(new Color(220, 186, 0));
        eb.addField("Category:", Objects.requireNonNull(Objects.requireNonNull(ch.getParentCategory()).getName()), false);
        eb.addField("Created Date", DateTimeUtils.toDiscordTimeStamp(ch.getTimeCreated(), TimeFormat.DATE_TIME_LONG), false);
        eb.addField("id:", ch.getId(), false);
        return eb;
    }

    private @NotNull EmbedBuilder prepareBuilder(@NotNull Channel ch) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(ch.getName(), null);
        eb.setColor(new Color(19, 255, 0));
        eb.addField("Created Date", DateTimeUtils.toDiscordTimeStamp(ch.getTimeCreated(), TimeFormat.DATE_TIME_LONG), false);
        eb.addField("id:", ch.getId(), false);
        return eb;
    }

    private @NotNull EmbedBuilder prepareBuilder(@NotNull Category ch) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(ch.getName(), null);
        eb.setColor(new Color(0, 62, 224));
        eb.addField("Created Date", DateTimeUtils.toDiscordTimeStamp(ch.getTimeCreated(), TimeFormat.DATE_TIME_LONG), false);
        eb.addField("id:", ch.getId(), false);

        return eb;
    }
}
