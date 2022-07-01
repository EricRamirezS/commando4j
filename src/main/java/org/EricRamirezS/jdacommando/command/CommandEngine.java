package org.EricRamirezS.jdacommando.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.EricRamirezS.jdacommando.command.arguments.FloatArgument;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.arguments.IntegerArgument;
import org.EricRamirezS.jdacommando.command.arguments.StringArgument;
import org.EricRamirezS.jdacommando.command.command.ICommand;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.customizations.MultiLocaleResourceBundle;
import org.EricRamirezS.jdacommando.command.data.IRepository;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedNameException;
import org.EricRamirezS.jdacommando.command.exceptions.InvalidCommandNameException;
import org.EricRamirezS.jdacommando.command.tools.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InvalidNameException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("rawtypes")
public class CommandEngine extends ListenerAdapter implements ICommandEngine {

    private static final Logger logger = LoggerFactory.getLogger(ICommandEngine.class);
    private static final List<String> commandPackage = new ArrayList<>();
    private static ICommandEngine commandConfig;
    private static boolean includesUtils;
    private static IRepository repository = new Repository();
    private final List<ICommand> commandList = new ArrayList<>();
    private final Map<String, ICommand> commandNames = new HashMap<>();
    private final MultiLocaleResourceBundle resourceBundle = new MultiLocaleResourceBundle();
    private JDA jda;
    private boolean commandLoaded = false;
    private String prefix = "~";
    private Locale language = new Locale("en", "US");
    private boolean reactToMention = true;
    private String help;
    private ICommand helpCommand;

    protected CommandEngine() {
        if (repository instanceof Repository rep) rep.createTables();
    }

    public static void addCommandPackage(String path) {
        commandPackage.add(path);
    }

    public static ICommandEngine getInstance() {
        if (commandConfig == null) {
            commandConfig = new CommandEngine();
        }
        return commandConfig;
    }

    public static void setInstance(CommandEngine newCommandConfig) {
        commandConfig = newCommandConfig;
    }

    public static void includeBuildInUtils() {
        //noinspection SpellCheckingInspection
        addCommandPackage("org.EricRamirezS.jdacommando.command.command");
        includesUtils = true;
    }

    public void logWarn(String message) {
        logger.warn(message);
    }

    public void logDebug(String message) {
        logger.debug(message);
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logError(String message) {
        logger.error(message);
    }

    public ICommandEngine setHelp(String help) {
        this.help = help;
        return this;
    }

    public ICommand getHelpCommand() {
        if (helpCommand == null) {
            helpCommand = commandNames.get(help);
        }
        return helpCommand;
    }

    public void loadCommands() {

        if (!commandLoaded) {
            Set<Class<? extends ICommand>> classes = new HashSet<>();
            for (String path : commandPackage) {
                try {
                    Reflections reflections = new Reflections(path);
                    classes.addAll(reflections.getSubTypesOf(ICommand.class));
                } catch (Exception e) {
                    CommandEngine.getInstance().logError(LocalizedFormat.format("DevelopmentError_ClassPath", path));
                    CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                }
            }
            CommandListUpdateAction commands = null;
            if (jda != null) {
                commands = jda.updateCommands();
            }
            for (Class<? extends ICommand> command : classes) {
                try {
                    //noinspection SpellCheckingInspection
                    if (command.getPackageName().equals("org.EricRamirezS.jdacommando.command.command")) continue;
                    if (command.getName().equals("org.EricRamirezS.jdacommando.command.command.util.SampleCommand"))
                        continue;

                    ICommand c = command.getDeclaredConstructor().newInstance();

                    if (includesUtils && command.getName().equals("org.EricRamirezS.jdacommando.command.command.util.HelpCommand"))
                        setHelp(c.getName());
                    addCommand(c);
                    for (String alias : c.getAliases()) addAlias(alias, c);
                    if (c instanceof Slash) {
                        if (commands != null) {
                            setSlashCommand(commands, c);
                        } else {
                            CommandEngine.getInstance().logError(LocalizedFormat.format("DevelopmentError_JDA"));
                        }
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    CommandEngine.getInstance().logError(LocalizedFormat.format("DevelopmentError_CommandInstance", command.getSimpleName(), Arrays.toString(e.getStackTrace())));
                    CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                } catch (DuplicatedNameException | InvalidNameException e) {
                    CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                }
            }
            if (commands != null) {
                commands.queue();
            }
            commandLoaded = false;
        }
    }

    /**
     * Sets a Slash command based on a ICommand to be registered into JDA
     *
     * @param commands Command updater to add the command
     * @param c ICommand to base the Slash Command
     */
    private void setSlashCommand(CommandListUpdateAction commands, @NotNull ICommand c) {
        SlashCommandData slash = Commands.slash(c.getName(), c.getDescription());
        for (IArgument arg : c.getArguments()) {
            OptionData argData = new OptionData(arg.getType().asOptionType(), arg.getName(), arg.getPrompt(), arg.isRequired(), arg.getType().asOptionType().canSupportChoices());
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
    }

    public boolean isReactToMention() {
        return reactToMention;
    }

    public ICommandEngine setReactToMention(boolean reactToMention) {
        this.reactToMention = reactToMention;
        return this;
    }

    public boolean isReactToMention(Event event) {
        return reactToMention;
    }

    public @Nullable ICommand getCommand(@NotNull String name) {
        if (commandNames.containsKey(name.toLowerCase(Locale.ROOT)))
            return commandNames.get(name.toLowerCase(Locale.ROOT));
        return null;
    }

    public @NotNull List<ICommand> getCommandsByPartialMatch(String name) {
        Set<String> keys = commandNames.keySet();
        List<String> found = keys.stream().filter(c -> c.contains(name.toLowerCase(Locale.ROOT))).toList();
        Set<ICommand> commands = new HashSet<>();
        for (String k : found) {
            commands.add(commandNames.get(k));
        }
        return new ArrayList<>(commands);
    }

    public @NotNull List<ICommand> getCommandsByExactMatch(String name) {
        Set<String> keys = commandNames.keySet();
        List<String> found = keys.stream().filter(c -> c.equals(name.toLowerCase(Locale.ROOT))).toList();
        Set<ICommand> commands = new HashSet<>();
        for (String k : found) {
            commands.add(commandNames.get(k));
        }
        return new ArrayList<>(commands);
    }

    @Contract(" -> new")
    public final @NotNull @UnmodifiableView List<ICommand> getCommands() {
        return Collections.unmodifiableList(commandList);
    }

    public ICommandEngine addCommand(@NotNull ICommand command) throws DuplicatedNameException, InvalidNameException {
        addAlias(command.getName(), command);
        commandList.add(command);
        return this;
    }

    public ICommandEngine addAlias(@NotNull String name, ICommand command) throws DuplicatedNameException, InvalidNameException {
        if (!name.matches("[a-zA-Z0-9]")) throw new InvalidCommandNameException();
        if (commandNames.containsKey(name)) throw new DuplicatedNameException(name);
        commandNames.put(name, command);
        return this;
    }

    public String getPrefix(@NotNull MessageReceivedEvent event) {
        if (event.isFromGuild()) return repository.getPrefix(event.getGuild().getId());
        return prefix;
    }

    public String getPrefix(@NotNull SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) return repository.getPrefix(Objects.requireNonNull(event.getGuild()).getId());
        return prefix;
    }

    public String getPrefix(@NotNull Event event) {
        if (event instanceof MessageReceivedEvent m && m.isFromGuild())
            return repository.getPrefix(Objects.requireNonNull(m.getGuild()).getId());
        if (event instanceof SlashCommandInteractionEvent s && s.isFromGuild())
            return repository.getPrefix(Objects.requireNonNull(s.getGuild()).getId());
        return prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public ICommandEngine setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
        return this;
    }

    public ICommandEngine setPrefix(@NotNull Guild guild, @NotNull String prefix) {
        repository.setPrefix(guild.getId(), prefix);
        return this;
    }

    public Locale getLanguage() {
        return language;
    }

    public ICommandEngine setLanguage(@NotNull Locale language) {
        this.language = language;
        return this;
    }

    public Locale getLanguage(@Nullable Event event) {
        String id = null;
        if (event instanceof MessageReceivedEvent e && e.isFromGuild()) id = e.getGuild().getId();
        if (event instanceof SlashCommandInteractionEvent e && e.isFromGuild())
            id = Objects.requireNonNull(e.getGuild()).getId();
        if (event instanceof SlashCommandInteractionEvent e && !e.isFromGuild()) return e.getUserLocale();

        if (id == null) return getLanguage();

        String lang = getRepository().getLanguage(id);
        return Locale.forLanguageTag(lang);
    }

    public final @NotNull String getString(@NotNull String key) {
        return resourceBundle.getString(key);
    }

    public final @NotNull String getString(@NotNull String key, @NotNull Locale locale) {
        return resourceBundle.getString(locale, key);
    }

    public final @NotNull String @NotNull [] getStringArray(@NotNull String key) {
        return resourceBundle.getStringArray(key);
    }

    public final @NotNull String @NotNull [] getStringArray(@NotNull String key, @NotNull Locale locale) {
        return resourceBundle.getStringArray(locale, key);
    }

    private ICommand getCalledCommand(@NotNull MessageReceivedEvent event) {
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

    public ICommandEngine setJda(JDA jda) {
        this.jda = jda;
        return this;
    }

    @Override
    public IRepository getRepository() {
        return repository;
    }

    public static void setRepository(IRepository repository) {
        CommandEngine.repository = repository;
    }

    @Override
    public final void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {
            ICommand command = getCalledCommand(event);
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
        } catch (Exception ex) {
            logError(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        ICommand c = getCommand(event.getName());

        if (c == null) return;

        if (!Slash.shouldRun(event, c)) {
            Slash.sendReply(event, LocalizedFormat.format("Slash_InvalidChannel", event));
            return;
        }

        String permissionError = c.checkPermissions(event);

        if (!StringUtils.isNullOrWhiteSpace(permissionError)) {
            Slash.sendReply(event, permissionError);
            return;
        }

        if (c.getArguments().size() == 0) ((Slash) c).run(event, Collections.unmodifiableMap(new HashMap<>(0)));
        else {
            Map<String, IArgument> args = new HashMap<>(0);
            List<OptionMapping> opts = event.getOptions();
            for (OptionMapping opt : opts) {
                String key = opt.getName();
                IArgument argument = c.getArgument(opt.getName());
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
            ((Slash) c).run(event, Collections.unmodifiableMap(args));
        }
    }
}
