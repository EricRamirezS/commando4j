package org.EricRamirezS.jdacommando.command.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.Throttling;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;

import java.util.List;
import java.util.Map;

public interface ICommand {


    boolean isNsfw();

    boolean isGuildOnly();

    boolean isRunInThread();

    boolean isThreadOnly();

    boolean isPrivateUseOnly();

    String getName();

    String getGroup();

    String getDescription();

    String getDetails();

    String getName(Event event);

    String getGroup(Event event);

    String getDescription(Event event);

    String getDetails(Event event);

    String anyUsage(Event event);

    String usage(String arg, String prefix, Event event);

    String checkPermissions(Event event);

    void onGuildThreadMessageReceived(MessageReceivedEvent event);

    void onGuildMessageReceived(MessageReceivedEvent event);

    void onDirectMessageReceived(MessageReceivedEvent event);

    IArgument getArgument(String name);

    Throttling getThrottling();

    List<IArgument> getArguments();

    List<String> getExamples();

    List<String> getAliases();

    List<Permission> getClientPermissions();

    List<Permission> getMemberPermissions();


}
