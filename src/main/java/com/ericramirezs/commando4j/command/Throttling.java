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

package com.ericramirezs.commando4j.command;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Object for limiting the number of requests a guild can make in a certain period.
 */
public class Throttling {

    private final List<Date> dates = new ArrayList<>(0);
    private final Map<String, List<Date>> dateByServer = new HashMap<>(0);

    private final int usages;
    private final int duration;
    private final boolean globalThrottling;

    /**
     * Creates a new instance.
     *
     * @param usages           Limit of usages.
     * @param duration         how long until the command can be used again.
     * @param globalThrottling true if limit is handled globally, false is limit is guild-based.
     */
    public Throttling(int usages, int duration, boolean globalThrottling) {
        this.usages = usages;
        this.duration = duration;
        this.globalThrottling = globalThrottling;
    }

    /**
     * Checks whether the ICommand can be used or if it has reached its limit.
     *
     * @param event Event that triggered this call.
     * @return true if the ICommand can be used, false otherwise..
     */
    public boolean check(Event event) {
        synchronized (this) {
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
    }

    /**
     * Register an usage of a Command. It will also remove registry that are no longer valid.
     *
     * @param event Discord event that triggered this function call.
     */
    public void addUsage(Event event) {
        synchronized (this) {
            if (globalThrottling) {
                dates.add(new Date());
            } else {
                String id = getId(event);
                if (dateByServer.containsKey(id)) {
                    dateByServer.get(id).add(new Date());
                } else {
                    dateByServer.put(id, new ArrayList<>(1));
                    dateByServer.get(id).add(new Date());
                }
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
