package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.AbstractChannel;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CategoryChannelArgument extends Argument<Category> {

    public CategoryChannelArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.CATEGORY_CHANNEL);
    }

    @Override
    public @Nullable String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^([0-9]+)$")){
            Optional<Category> category =event.getGuild().getCategories().stream().filter(c -> c.getId().equals(arg)).findFirst();
            if (category.isPresent()){
                return oneOf(category.get());
            } else {
                return MessageFormat.format("No he podido encontrar la {0} indicado", "categoría");
            }
        }
        List<Category> categories = event.getGuild().getCategories().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (categories.size() == 0) return "No he podido encontrar el canal indicado";
        if (categories.size() == 1) return oneOf(categories.get(0));
        categories = event.getGuild().getCategories().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (categories.size() == 1) return oneOf(categories.get(0));
        return "Se han encontrado multiples canales, se más específico, por favor.";
    }

    @Override
    public @Nullable Category parse(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^([0-9]+)$")){
            Optional<Category> category =event.getGuild().getCategories().stream().filter(c -> c.getId().equals(arg)).findFirst();
            if (category.isPresent()){
                return category.get();
            }
        }
        List<Category> categories = event.getGuild().getCategories().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (categories.size() == 0) return null;
        if (categories.size() == 1) return categories.get(0);
        categories = event.getGuild().getCategories().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (categories.size() == 1) return categories.get(0);
        return null;
    }

    private @Nullable String oneOf(Category category){
        if (isOneOf(category))return null;
        return MessageFormat.format("Por favor, ingrese una de las siguientes opciones: \n{0}",
                getValidValues().stream().map(AbstractChannel::getName).collect(Collectors.joining("\n")));
    }
}
