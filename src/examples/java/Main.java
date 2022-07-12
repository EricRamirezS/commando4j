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

import com.ericramirezs.commando4j.CommandEngine;
import net.dv8tion.jda.api.JDA;

public class Main {

    public static void main(final String[] args) throws Exception {
        CommandEngine.includeBuildInUtils(); // Include build-in Commands in the engine
        CommandEngine.addCommandPackage("examples"); // Command Classpath package

        /*
          Building a JDA object
          Doing it through CommandEngine will include it as one of the Event Listeners
          You may still set a JDABuilder by your own and then call CommandEngine#create(JDABuilder)
         */
        final JDA jda = CommandEngine.createLight(System.getenv("BOT_TOKEN"));

        jda.awaitReady(); // optionally block until JDA is ready
    }
}
