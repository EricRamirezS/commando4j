package org.EricRamirezS.jdacommando.command.command.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.ICommandEngine;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.arguments.StringArgument;
import org.EricRamirezS.jdacommando.command.arguments.UnionArgument;
import org.EricRamirezS.jdacommando.command.command.Command;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedArgumentNameException;
import org.EricRamirezS.jdacommando.command.tools.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class LanguageCommand extends Command {

    public LanguageCommand() throws DuplicatedArgumentNameException {
        super("language", "util", "Command_Language_Description",
                new UnionArgument("languageCode", "Command_Language_UnionArgument")
                        .addArgumentType(new StringArgument("", "")
                                .setRegex("[a-z]{2,3}-[A-Z]{2,3}$",
                                        "Two or three characters representing language code, and score " +
                                                "and two or three characters representing country code"))
                        .addArgumentType(new StringArgument("", "")
                                .addValidValues("default"))
        );
        UnionArgument arg = (UnionArgument) getArguments().get(0);
        StringArgument regexArg = (StringArgument) arg.getArguments().get(0);

        arg.setPromptParser(x -> localizePrompt(arg, x));
        regexArg.setRegex("[a-z]{2,3}-[A-Z]{2,3}$", this::localizeRegex);

        setGuildOnly();
        addMemberPermissions(Permission.ADMINISTRATOR);

        addExamples("language",
                "language en-US",
                "language es-ES",
                "language default");

    }

    private @NotNull String localizePrompt(UnionArgument arg, Event event) {
        if (event == null) return LocalizedFormat.format(arg.getPromptRaw());
        return LocalizedFormat.format(arg.getPromptRaw(), event);
    }

    private @NotNull String localizeRegex(MessageReceivedEvent event) {
        if (event == null) return LocalizedFormat.format("Command_Language_Regex");
        return LocalizedFormat.format("Command_Language_Regex", event);
    }

    @Override
    public String getDescription() {
        return LocalizedFormat.format(super.getDescription());
    }

    @Override
    public String getDescription(Event event) {
        return LocalizedFormat.format(super.getDescription(), event);
    }

    @Override
    public void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        StringArgument languageCode = (StringArgument) args.get("languageCode").getValue();
        if (languageCode == null || StringUtils.isNullOrWhiteSpace(languageCode.getValue())) {
            displayLanguage(event);
        } else if (languageCode.getValue().equals("default")) {
            resetDefaultLanguage(event);
        } else {
            setNewLanguage(event, Locale.forLanguageTag(languageCode.getValue()));
        }
    }

    private void displayLanguage(@NotNull MessageReceivedEvent event) {
        ICommandEngine engine = CommandEngine.getInstance();
        String lang = engine.getRepository().getLanguage(event.getGuild().getId());
        Locale locale = Locale.forLanguageTag(lang);
        String message = LocalizedFormat.format("Locale_CurrentLanguage", locale,
                locale.getDisplayLanguage(locale), locale.getDisplayCountry(locale));
        sendReply(event, message);
    }

    private void resetDefaultLanguage(MessageReceivedEvent event) {
        Locale locale = CommandEngine.getInstance().getLanguage();
        if (configureLanguage(event, locale))
            sendReply(event,
                    LocalizedFormat.format("Locale_CurrentLanguage", locale,
                            locale.getDisplayLanguage(locale), locale.getDisplayCountry(locale)));
    }

    private void setNewLanguage(MessageReceivedEvent event, Locale locale) {
        if (MultiLocaleResourceBundle.getSupportedLocale().contains(locale)) {
            if (configureLanguage(event, locale))
                sendReply(event,
                        LocalizedFormat.format("Locale_NewLanguage", locale,
                                locale.getDisplayLanguage(locale), locale.getDisplayCountry(locale)));
        } else {
            sendReply(event, LocalizedFormat.format("Locale_UnsupportedLanguage", event));
        }
    }

    private boolean configureLanguage(MessageReceivedEvent event, Locale locale) {
        ICommandEngine engine = CommandEngine.getInstance();
        try {
            engine.getRepository().setLanguage(event.getGuild().getId(), locale.toLanguageTag());
            return true;
        } catch (Exception ex) {
            engine.logError(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        return false;
    }
}
