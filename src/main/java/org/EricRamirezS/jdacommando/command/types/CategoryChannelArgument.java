package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CategoryChannelArgument extends Argument<Category> {

    public CategoryChannelArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.CATEGORY_CHANNEL);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(\\d+)$")) {
            Optional<Category> category = event.getGuild().getCategories().stream()
                    .filter(c -> c.getId().equals(arg))
                    .findFirst();
            if (category.isPresent())
                return oneOf(category.get(), event, Channel::getName, "Argument_CategoryChannel_OneOf");
            else return LocalizedFormat.format("Argument_CategoryChannel_NotFound", event);
        }
        List<Category> categories = event.getGuild().getCategories().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (categories.size() == 0) return LocalizedFormat.format("Argument_CategoryChannel_NotFound", event);
        if (categories.size() == 1)
            return oneOf(categories.get(0), event, Channel::getName, "Argument_CategoryChannel_OneOf");
        categories = event.getGuild().getCategories().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (categories.size() == 1)
            return oneOf(categories.get(0), event, Channel::getName, "Argument_CategoryChannel_OneOf");
        return LocalizedFormat.format("Argument_CategoryChannel_TooMany", event);
    }

    @Override
    public @Nullable Category parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(\\d+)$")) {
            Optional<Category> category = event.getGuild().getCategories().stream()
                    .filter(c -> c.getId().equals(arg))
                    .findFirst();
            if (category.isPresent()) {
                return category.get();
            }
        }
        List<Category> categories = event.getGuild().getCategories().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (categories.size() == 0) return null;
        if (categories.size() == 1) return categories.get(0);
        categories = event.getGuild().getCategories().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (categories.size() == 1) return categories.get(0);
        return null;
    }
}
