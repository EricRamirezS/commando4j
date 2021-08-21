package org.EricRamirezS.jdacommando.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Throttling {

    private final List<Date> dates = new ArrayList<>();
    private final int usages;
    private final int duration;

    public Throttling(int usages, int duration) {
        this.usages = usages;
        this.duration = duration;
    }

    public boolean check(){
        Date limit = new Date(System.currentTimeMillis() - duration * 1000L);
        dates.removeIf(d -> d.before(limit));
        return dates.size() >= usages;
    }
}
