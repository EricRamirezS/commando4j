package org.EricRamirezS.jdacommando.command;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class Throttling {

    private final List<Date> dates = new ArrayList<>(0);
    private final Map<String, List<Date>> dateByServer = new HashMap<>(0);

    private final int usages;
    private final int duration;
    private final boolean globalThrottling;

    public Throttling(int usages, int duration, boolean globalThrottling) {
        this.usages = usages;
        this.duration = duration;
        this.globalThrottling = globalThrottling;
    }

    public boolean check(Event event) {
        if (globalThrottling) {
            Date limit = new Date(System.currentTimeMillis() - duration * 1000L);
            dates.removeIf(d -> d.before(limit));
            return dates.size() < usages;
        } else {
            String id = getId(event);
            if (!dateByServer.containsKey(id)) return 0 < usages;
            List<Date> dates = dateByServer.get(id);
            Date limit = new Date(System.currentTimeMillis() - duration * 1000L);
            dates.removeIf(d -> d.before(limit));
            return dates.size() < usages;

        }
    }

    public void addUsage(Event event) {
        if (globalThrottling) {
            dates.add(new Date());
        } else {
            String id = getId(event);
            if (dateByServer.containsKey(id)) {
                dateByServer.get(id).add(new Date());
            } else {
                dateByServer.put(id,new ArrayList<>(1));
                dateByServer.get(id).add(new Date());
            }
        }
    }

    private String getId(Event event) {
        String id = null;
        if (event instanceof MessageReceivedEvent e && e.isFromGuild()) id = e.getGuild().getId();
        if (event instanceof SlashCommandInteractionEvent e && e.isFromGuild())
            id = Objects.requireNonNull(e.getGuild()).getId();
        if (event instanceof MessageReceivedEvent e && !e.isFromGuild()) id = e.getAuthor().getAsMention();
        if (event instanceof SlashCommandInteractionEvent e && !e.isFromGuild()) id = e.getUser().getAsMention();
        return id;
    }
}
