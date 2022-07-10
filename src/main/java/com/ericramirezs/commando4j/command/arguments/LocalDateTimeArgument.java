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

package com.ericramirezs.commando4j.command.arguments;

import com.ericramirezs.commando4j.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;

public abstract class LocalDateTimeArgument<A extends LocalDateTimeArgument, T> extends Argument<A, T> {

    /**
     * Creates an instance of an LocalDateTimeArgument Object.
     *
     * @param name         Readable name to display to the final
     * @param prompt       Hint to indicate the user the expected value to be passed to this argument.
     * @param argumentType Category of the argument implementation.
     */
    public LocalDateTimeArgument(@NotNull final String name, final String prompt, final ArgumentTypes argumentType) {
        super(name, prompt, argumentType);
    }

    private boolean forcedDiscordTag;

    /**
     * Set if the user must use Discord's time tag syntax for this argument.
     * <p>
     * Visit <a href="https://discord.com/developers/docs/reference#message-formatting-formats">message-formatting-formats</a>
     * for more information about Discord's time tag syntax.
     * </p>
     *
     * @return a reference to this object.
     */
    public A setForcedTag() {
        forcedDiscordTag = true;
        //noinspection unchecked
        return (A) this;
    }

    /**
     * Checks if the user must use Discord's time tag syntax for this argument.
     * <p>
     * Visit <a href="https://discord.com/developers/docs/reference#message-formatting-formats">message-formatting-formats</a>
     * for more information about Discord's time tag syntax.
     * </p>
     *
     * @return true if the argument only accepts discord's time tag.
     */
    public boolean isForcedDiscordTag() {
        return forcedDiscordTag;
    }
}
