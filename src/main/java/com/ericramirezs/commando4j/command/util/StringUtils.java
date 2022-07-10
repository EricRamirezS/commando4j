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

package com.ericramirezs.commando4j.command.util;

import java.util.Arrays;

public interface StringUtils {

    /**
     * Indicates whether the specified string is null or an empty string ("").
     *
     * @param s The string to test.
     * @return true if the value parameter is null or an empty string (""); otherwise, false.
     */
    static boolean isNullOrEmpty(final String s) {
        return s == null || s.equals("");
    }

    /**
     * Indicates whether a specified string is null, empty, or consists only of white-space characters.
     *
     * @param s The string to test.
     * @return true if the value parameter is null or Empty, or if value consists exclusively of white-space characters.
     */
    static boolean isNullOrWhiteSpace(final String s) {
        return isNullOrEmpty(s) || s.trim().equals("");
    }

    /**
     * Evaluates the arguments in order and returns the current value of the
     * first expression that initially doesn't evaluate to null, empty or whitespace.
     *
     * @param s Array of string objects
     * @return first expression that doesn't evaluate to null, empty or whitespace. Null otherwise.
     */
    static String coalesce(final String... s) {
        return Arrays.stream(s).filter(str -> !isNullOrWhiteSpace(str)).findFirst().orElse(null);
    }
}
