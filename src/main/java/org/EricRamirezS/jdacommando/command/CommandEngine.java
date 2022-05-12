package org.EricRamirezS.jdacommando.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedNameException;
import org.EricRamirezS.jdacommando.command.exceptions.InvalidCommandNameException;
import org.EricRamirezS.jdacommando.command.types.Argument;
import org.EricRamirezS.jdacommando.command.types.FloatArgument;
import org.EricRamirezS.jdacommando.command.types.IntegerArgument;
import org.EricRamirezS.jdacommando.command.types.StringArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;

import javax.naming.InvalidNameException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("rawtypes")
public class CommandEngine extends ListenerAdapter {

    private static final List<String> commandPackage = new ArrayList<>();
    private static CommandEngine commandConfig;
    private final List<Command> commandList = new ArrayList<>();
    private final Map<String, Command> commandNames = new HashMap<>();
    private final MultiLocaleResourceBundle resourceBundle = new MultiLocaleResourceBundle();
    private JDA jda;
    private boolean commandLoaded = false;
    private String prefix = "~";
    private Locale language = new Locale("en");
    private boolean reactToMention = true;
    private String help;
    private Command helpCommand;

    private CommandEngine() {
    }

    public static void addCommandPackage(String path) {
        commandPackage.add(path);
    }

    public static CommandEngine getInstance() {
        if (commandConfig == null) {
            commandConfig = new CommandEngine();
        }
        return commandConfig;
    }

    private static void setInstance(CommandEngine newCommandConfig) {
        commandConfig = newCommandConfig;
    }

    public CommandEngine setHelp(String help) {
        this.help = help;
        return this;
    }

    public Command getHelpCommand() {
        if (helpCommand == null) {
            helpCommand = commandNames.get(help);
        }
        return helpCommand;
    }

    public void loadCommands() {

        if (!commandLoaded) {
            Set<Class<? extends Command>> classes = new HashSet<>();
            for (String path : commandPackage) {
                try {
                    Reflections reflections = new Reflections(path);
                    classes.addAll(reflections.getSubTypesOf(Command.class));
                } catch (Exception ex) {
                    System.err.println(LocalizedFormat.format("DevelopmentError_ClassPath", path));
                }
            }
            CommandListUpdateAction commands = null;
            if (jda != null) {
                commands = jda.updateCommands();
            }
            for (Class<? extends Command> command : classes) {
                try {
                    Command c = command.getDeclaredConstructor().newInstance();
                    if (c instanceof Slash) {
                        if (commands != null) {
                            setSlashCommand(commands, c);
                        } else {
                            System.err.println(LocalizedFormat.format("DevelopmentError_JDA"));
                        }
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    System.err.println(LocalizedFormat.format("DevelopmentError_CommandInstance", command.getSimpleName(), e.getStackTrace()));
                }
            }
            if (commands != null) {
                commands.queue();
            }
            commandLoaded = false;
        }
    }

    @Contract("_, _ -> param1")
    private @NotNull CommandListUpdateAction setSlashCommand(CommandListUpdateAction commands, @NotNull Command c) {
        SlashCommandData slash = Commands.slash(c.getName(), c.getDescription());
        for (Argument arg : c.getArguments()) {
            OptionData argData = new OptionData(arg.getType().asOptionType(), arg.getName(),
                    arg.getPrompt(), arg.isRequired(), arg.getType().asOptionType().canSupportChoices());
            if (argData.isAutoComplete()) {
                int i = 0;
                if (arg instanceof IntegerArgument intArg) {
                    for (Long opt : intArg.getValidValues()) {
                        argData.addChoice((++i + ""), opt);
                    }
                    if (arg.getMax() != null) argData.setMaxValue(arg.getMax());
                    if (arg.getMin() != null) argData.setMaxValue(arg.getMin());
                }
                if (arg instanceof FloatArgument intArg) {
                    for (Double opt : intArg.getValidValues()) {
                        argData.addChoice((++i + ""), opt);
                    }
                    if (arg.getMax() != null) argData.setMaxValue(arg.getMax());
                    if (arg.getMin() != null) argData.setMaxValue(arg.getMin());
                }
                if (arg instanceof StringArgument intArg) {
                    for (String opt : intArg.getValidValues()) {
                        argData.addChoice((++i + ""), opt);
                    }
                }
            }
            slash.addOptions(argData);
        }
        //noinspection ResultOfMethodCallIgnored
        commands.addCommands(slash);
        return commands;
    }

    public boolean isReactToMention() {
        return reactToMention;
    }

    /**
     * Sets if the bot should react to a message starting by mentioning the bot @bot_name
     * if sets to false, the bot will only react to prefix
     *
     * @param reactToMention Should react to mention?
     * @see #getPrefix(MessageReceivedEvent)
     */
    public CommandEngine setReactToMention(boolean reactToMention) {
        this.reactToMention = reactToMention;
        return this;
    }

    public @Nullable Command getCommand(@NotNull String name) {
        if (commandNames.containsKey(name.toLowerCase(Locale.ROOT)))
            return commandNames.get(name.toLowerCase(Locale.ROOT));
        return null;
    }

    public @NotNull List<Command> getCommandsByPartialMatch(String name) {
        Set<String> keys = commandNames.keySet();
        List<String> found = keys.stream().filter(c -> c.contains(name.toLowerCase(Locale.ROOT))).toList();
        Set<Command> commands = new HashSet<>();
        for (String k : found) {
            commands.add(commandNames.get(k));
        }
        return new ArrayList<>(commands);
    }

    public @NotNull List<Command> getCommandsByExactMatch(String name) {
        Set<String> keys = commandNames.keySet();
        List<String> found = keys.stream().filter(c -> c.equals(name.toLowerCase(Locale.ROOT))).toList();
        Set<Command> commands = new HashSet<>();
        for (String k : found) {
            commands.add(commandNames.get(k));
        }
        return new ArrayList<>(commands);
    }

    @Contract(" -> new")
    public final @NotNull @UnmodifiableView List<Command> getCommands() {
        return Collections.unmodifiableList(commandList);
    }

    CommandEngine addCommand(@NotNull Command command) throws DuplicatedNameException, InvalidNameException {
        addAlias(command.getName(), command);
        commandList.add(command);
        return this;
    }

    /**
     * Add an alternative name to a command
     *
     * @param name    alternative name
     * @param command Command to be called
     * @throws DuplicatedNameException There's already a commando with the same name/alias
     */
    CommandEngine addAlias(@NotNull String name, Command command) throws DuplicatedNameException, InvalidNameException {
        if (!name.matches("[a-zA-Z]+")) throw new InvalidCommandNameException();
        if (commandNames.containsKey(name))
            throw new DuplicatedNameException(name);
        commandNames.put(name, command);
        return this;
    }

    /**
     * By default, it gets the prefix configured on the bot.
     * May be used to get a prefix based on the guild
     *
     * @return prefix string
     */
    public String getPrefix(@NotNull MessageReceivedEvent event) {
        return prefix;
    }

    public String getPrefix(@NotNull SlashCommandInteractionEvent event) {
        return prefix;
    }

    public String getPrefix(@NotNull Event event) {
        return prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the default prefix for the bot
     *
     * @param prefix prefix string
     */
    public CommandEngine setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Gets the default language to be used by the bot
     *
     * @return current language
     * @see org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle#getSupportedLocale()
     */
    public Locale getLanguage() {
        return language;
    }

    /**
     * Sets the default language to be used by the bot
     *
     * @param language Locale to use
     * @see org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle#getSupportedLocale()
     */
    public CommandEngine setLanguage(@NotNull Locale language) {
        this.language = language;
        return this;
    }

    /**
     * By default, gets the default language to be used by the bot.
     * May be used to get a Locale based on the Guild information.
     *
     * @return current language
     * @see org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle#getSupportedLocale()
     */
    public Locale getLanguage(@Nullable Event event) {
        return getLanguage();
    }

    /**
     * Gets a text string in the configured language of the bot
     *
     * @param key String key in resourceBundle
     * @return String in the current language
     */
    public final @NotNull String getString(@NotNull String key) {
        return resourceBundle.getString(key);
    }

    /**
     * Gets a text string in a specific language
     *
     * @param key String key in resourceBundle
     * @return String in the specific language
     */
    public final @NotNull String getString(@NotNull String key, @NotNull Locale locale) {
        return resourceBundle.getString(locale, key);
    }

    /**
     * Gets a string Array in the configured language of the bot
     *
     * @param key String key in resourceBundle
     * @return String in the current language
     */
    public final @NotNull String @NotNull [] getStringArray(@NotNull String key) {
        return resourceBundle.getStringArray(key);
    }

    /**
     * Gets a string Array in a specific language
     *
     * @param key String key in resourceBundle
     * @return String in the specific language
     */
    public final @NotNull String @NotNull [] getStringArray(@NotNull String key, @NotNull Locale locale) {
        return resourceBundle.getStringArray(locale, key);

    }

    private Command getCalledCommand(@NotNull MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw().toLowerCase();
        String mention = event.getJDA().getSelfUser().getAsMention();
        String prefix = CommandEngine.getInstance().getPrefix(event);
        int index = msg.startsWith(mention + " ") ? 1 : 0;
        String command = msg.split(" ")[index];
        String name;

        if (command.startsWith(mention)) {
            name = command.replaceFirst(mention, "");
        } else if (command.startsWith(prefix)) {
            name = command.replaceFirst(prefix, "");
        } else {
            name = command;
        }

        return commandNames.get(name.trim());
    }

    public JDA getJda() {
        return jda;
    }

    public CommandEngine setJda(JDA jda) {
        this.jda = jda;
        return this;
    }

    @Override
    public final void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {
            Command command = getCalledCommand(event);
            if (command == null) return;

            if (event.isFromGuild()) {
                if (event.isFromThread()) {
                    command.onGuildThreadMessageReceived(event);
                } else {
                    command.onGuildMessageReceived(event);
                }
            } else {
                command.onDirectMessageReceived(event);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        Command c = getCommand(event.getName());

        if (c == null) return;

        if (c.getArguments().size() == 0)
            ((Slash) c).runSlash(event, Collections.unmodifiableMap(new HashMap<>(0)));
        else {
            Map<String, Argument> args = new HashMap<>(0);
            List<OptionMapping> opts = event.getOptions();
            for (OptionMapping opt : opts) {
                String key = opt.getName();
                Argument argument = c.getArgument(opt.getName());
                switch (opt.getType()) {
                    case STRING -> argument.setSlashValue(opt.getAsString());
                    case INTEGER -> argument.setSlashValue(opt.getAsLong());
                    case BOOLEAN -> argument.setSlashValue(opt.getAsBoolean());
                    case USER, MENTIONABLE, CHANNEL -> argument.setSlashValue(opt.getAsMentionable());
                    case ROLE -> argument.setSlashValue(opt.getAsRole());
                    case NUMBER -> argument.setSlashValue(opt.getAsDouble());
                    case ATTACHMENT -> argument.setSlashValue(opt.getAsAttachment());
                }
                args.put(key, argument);
            }
            ((Slash) c).runSlash(event, Collections.unmodifiableMap(args));
        }
    }

}
