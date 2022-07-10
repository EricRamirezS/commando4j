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

package com.ericramirezs.commando4j.command.command;

import com.ericramirezs.commando4j.command.Throttling;
import com.ericramirezs.commando4j.command.arguments.IArgument;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface ICommand {

    /**
     * Checks if the command should only be used in channels marked as NSFW.
     *
     * @return true if the command is marked as NSFW, false otherwise.
     */
    boolean isNsfw();

    /**
     * Checks if the command should only be used inside a Discord server.
     *
     * @return true if the command is only usable in a Discord Server, false if compatible with Direct Message.
     */
    boolean isGuildOnly();

    /**
     * Checks if the command should run inside a Discord Thread.
     *
     * @return true if the command can run inside a Discord Thread, false if it can only run in normal channels.
     */
    boolean isRunInThread();

    /**
     * Checks if the command should only be used inside threads.
     *
     * @return true if the command is only usable in threads, false otherwise.
     */
    boolean isThreadOnly();

    /**
     * Checks if the command should only be used in Direct Messages.
     *
     * @return True if command should only be used inside Direct Messages.
     */
    boolean isPrivateUseOnly();

    /**
     * get the command's name.
     *
     * @return command's name.
     */
    String getName();

    /**
     * get the command group's name or category.
     *
     * @return command group's name.
     */
    String getGroup();

    /**
     * Get the command's short description.
     *
     * @return command's description.
     */
    String getDescription();

    /**
     * Get the command's detailed description.
     *
     * @return command's detailed description.
     */
    String getDetails();

    /**
     * get the command's name.
     *
     * @param event Discord's event that triggered this function call.
     * @return command's name.
     */
    String getName(Event event);

    /**
     * get the command group's name or category.
     *
     * @param event Discord's event that triggered this function call.
     * @return command group's name.
     */
    String getGroup(Event event);

    /**
     * Get the command's short description.
     *
     * @param event Discord's event that triggered this function call.
     * @return command's description.
     */
    String getDescription(Event event);

    /**
     * Get the command's detailed description.
     *
     * @param event Discord's event that triggered this function call.
     * @return command's detailed description.
     */
    String getDetails(Event event);

    /**
     * Generates example usages of the command.
     *
     * @param event Discord's event that triggered this function call.
     * @return Usage example for discord chat.
     */
    String anyUsage(Event event);

    /**
     * Generates an example usage of the command.
     *
     * @param arg   Arguments in the example.
     * @param event Discord event that triggered this function call.
     * @return example usage of the command.
     */
    String usage(String arg, Event event);

    /**
     * Check if the user has permission to run this command, and to run it in the current channel.
     *
     * @param event Discord event that triggered this function call.
     * @return Error message explaining why the command cannot be executed.
     */
    String checkPermissions(Event event);

    /**
     * Handles a command call from a Thread Server Channel.
     *
     * @param event Discord event that triggered this function call.
     */
    void onGuildThreadMessageReceived(MessageReceivedEvent event);

    /**
     * Handles a command call from a Normal Server Channel.
     *
     * @param event Discord event that triggered this function call.
     */
    void onGuildMessageReceived(MessageReceivedEvent event);

    /**
     * Handles a command call from Direct Messages.
     *
     * @param event Discord event that triggered this function call.
     */
    void onDirectMessageReceived(MessageReceivedEvent event);

    /**
     * Get a specific argument by its name.
     *
     * @param name Argument's name.
     * @return Argument's object.
     */
    IArgument getArgument(String name);

    /**
     * Get the Throttling configuration for this command.
     *
     * @return Throttling configuration.
     */
    Throttling getThrottling();

    /**
     * Get a list of arguments expected by this command.
     *
     * @return list of arguments.
     */
    List<IArgument> getArguments();

    /**
     * Get a list of example usages.
     *
     * @return list of example usages.
     */
    List<String> getExamples();

    /**
     * Get a list of alternative names to call this command.
     *
     * @return list of alternative names.
     */
    List<String> getAliases();

    /**
     * Get the list of Permission required by the bot in the server to execute this command.
     *
     * @return list of Permission.
     */
    List<Permission> getClientPermissions();

    /**
     * Get the list of Permission required by the Discord member in the server to execute this command.
     *
     * @return list of Permission.
     */
    List<Permission> getMemberPermissions();
}
