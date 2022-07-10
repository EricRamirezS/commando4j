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
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Class to request an argument of type Category to the user.
 *
 * @see net.dv8tion.jda.api.entities.Category
 */
public final class CategoryChannelArgument extends GenericChannelArgument<CategoryChannelArgument, Category> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public CategoryChannelArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.CATEGORY_CHANNEL);
    }

    @Override
    public @Nullable String validate(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        final List<Category> data = event.getGuild().getCategories();

        return validateFromList(data, arg, event,
                "Argument_CategoryChannel_OneOf",
                "Argument_CategoryChannel_NotFound",
                "Argument_CategoryChannel_TooMany");
    }

    @Override
    public String validate(final @NotNull SlashCommandInteractionEvent event, final String arg) {
        final List<Category> data = Objects.requireNonNull(event.getGuild()).getCategories();

        return validateFromList(data, arg, event,
                "Argument_CategoryChannel_OneOf",
                "Argument_CategoryChannel_NotFound",
                "Argument_CategoryChannel_TooMany");
    }

    @Override
    public @Nullable Category parse(@NotNull final MessageReceivedEvent event, final String arg) {
        final List<Category> data = event.getGuild().getCategories();

        return parseFromList(data, arg);
    }

    @Override
    public Category parse(final @NotNull SlashCommandInteractionEvent event, final String arg) {
        final List<Category> data = Objects.requireNonNull(event.getGuild()).getCategories();

        return parseFromList(data, arg);
    }
}
