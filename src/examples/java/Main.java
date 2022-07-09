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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.ICommandEngine;
import examples;

public class Main {

    public static void main(String[] args) throws Exception {
        //Creates an get instance of command engine
        ICommandEngine engine = CommandEngine.getInstance();

        // Building a JDA object
        // Including the CommandEngine as one of the Event Listeners
        JDA jda = JDABuilder.createLight(System.getenv("BOT_TOKEN"))
                .addEventListeners(engine)
                .build();

        // Loading Command package
        addCommandPackages();
        // You may also add commands manually
        // engine.addCommand(new TimeToTagCommandExample())
        engine.loadCommands();

        jda.awaitReady();
    }

    /**
     * Setting the packages where commands are located.
     * Any class that extends ICommand interface will be loaded
     */
    private static void addCommandPackages() {
        CommandEngine.includeBuildInUtils(); // Include Command in the engine
        CommandEngine.addCommandPackage("examples"); // Command Classpath package
    }
}
