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

package com.ericramirezs.commando4j.command.data;

import com.ericramirezs.commando4j.command.CommandEngine;

/**
 * Minimum methods required for Data Persistence if build-in commands are going to be used.
 * By default, an implementation with SQLite will be used.
 * <p>
 * Implement this interface in order to use your own database to store language and prefix settings.
 * Then replace the default implementation by using {@link CommandEngine#setRepository}
 * </p>
 *
 * @see com.ericramirezs.commando4j.command.CommandEngine#setRepository(IRepository)
 */
public interface IRepository {

    /**
     * Get the preferred prefix for the guild
     *
     * @param guildId Discord server's unique ID
     * @return prefix set for the guild
     */
    String getPrefix(String guildId);

    /**
     * Set the preferred prefix for the guild
     *
     * @param guildId Unique ID of the discord server
     * @param prefix  new prefix for the guild
     * @return prefix set for the guild
     */
    String setPrefix(String guildId, String prefix);

    /**
     * Get the preferred language for the guild
     *
     * @param guildId Unique ID of the discord server
     * @return string that represents the locale tag
     * @see java.util.Locale#forLanguageTag(String)
     */
    String getLanguage(String guildId);

    /**
     * Set the preferred language for the guild
     *
     * @param guildId Unique ID of the discord server
     * @param locale  new locale tag
     * @return string that represents the locale tag
     * @see java.util.Locale#forLanguageTag(String)
     */
    String setLanguage(String guildId, String locale);
}
