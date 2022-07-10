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

package com.ericramirezs.commando4j.command;

import com.ericramirezs.commando4j.command.arguments.FloatArgument;
import com.ericramirezs.commando4j.command.arguments.IArgument;
import com.ericramirezs.commando4j.command.arguments.IntegerArgument;
import com.ericramirezs.commando4j.command.arguments.StringArgument;
import com.ericramirezs.commando4j.command.command.ICommand;
import com.ericramirezs.commando4j.command.customizations.MultiLocaleResourceBundle;
import com.ericramirezs.commando4j.command.data.IRepository;
import com.ericramirezs.commando4j.command.exceptions.DuplicatedNameException;
import com.ericramirezs.commando4j.command.exceptions.InvalidCommandNameException;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import com.ericramirezs.commando4j.command.util.StringUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.naming.InvalidNameException;
import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * The core of commando4j, It will handle all command calls registered through this engine.
 */
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
    private boolean commandLoaded;
    private String prefix = "~";
    private Locale language = new Locale("en", "US");
    private boolean reactToMention = true;
    private String help = "help";
    private ICommand helpCommand;

    /**
     * Creates an Instance of this {@link ICommandEngine} implementations.
     * It will initialize a SQLite database if the {@link Repository} implementation is set as the
     * {@link IRepository} implementation for the Engine (preset by default). You may change it
     * by using {@link CommandEngine#setRepository(IRepository)} method.
     */
    protected CommandEngine() {
        if (repository instanceof Repository rep) rep.createTables();
    }

    /**
     * Set the package name where the classes that extends ICommand are located in your project.
     * <p>
     * <strong>
     * It will also look though sub-packaged.
     * </strong>
     * </p>
     *
     * @param path the package name as described after the package keyword in your *.java files
     * @see ICommand
     * @see Slash
     */
    public static void addCommandPackage(final String path) {
        commandPackage.add(path);
    }

    /**
     * Get the actual instance of {@link ICommandEngine} that is being used.
     * <p>
     * <strong>By default</strong>, {@link CommandEngine} implementation will be used.
     * </p>
     *
     * @return a reference of the active instance.
     * @see ICommandEngine
     */
    public static ICommandEngine getInstance() {
        if (commandConfig == null) {
            commandConfig = new CommandEngine();
        }
        return commandConfig;
    }

    /**
     * Set a custom implementation of {@link ICommandEngine}. It if highly recommended that to customize the
     * engine, you extend from the default implementation {@link CommandEngine} instead of a raw version.
     * <p>
     * <strong>By default</strong>, {@link CommandEngine} implementation will be used.
     * </p>
     *
     * @param newCommandConfig custom implementation of {@link ICommandEngine}
     */
    public static void setInstance(final ICommandEngine newCommandConfig) {
        commandConfig = newCommandConfig;
    }

    /**
     * Include pre-created commands, such has Help, Ping, Language and prefix.
     * <p>
     * <Strong>Help:</Strong> command to get simple and detailed information about commands.
     * </p>
     * <p>
     * <Strong>Ping:</Strong> Test to see if the bot is reachable.
     * </p>
     * <p>
     * <Strong>Language:</Strong> Set the language used in a guild.
     * </p>
     * <p>
     * <Strong>prefix:</Strong> Set the prefix the bot react to in a guild.
     * </p>
     * <ul>
     *     <li>
     *         <strong>NOTE:</strong> To include only some of these command,
     *         use {@link ICommandEngine#addCommand(ICommand)} instead.
     *     </li>
     * </ul>
     *
     * @see com.ericramirezs.commando4j.command.command.util.HelpCommand
     * @see com.ericramirezs.commando4j.command.command.util.PrefixCommand
     * @see com.ericramirezs.commando4j.command.command.util.LanguageCommand
     * @see com.ericramirezs.commando4j.command.command.util.PrefixCommand
     */
    public static void includeBuildInUtils() {
        addCommandPackage("com.ericramirezs.commando4j.command.command");
        includesUtils = true;
    }

    @Override
    public void logWarn(final String message) {
        logger.warn(message);
    }

    @Override
    public void logDebug(final String message) {
        logger.debug(message);
    }

    @Override
    public void logInfo(final String message) {
        logger.info(message);
    }

    @Override
    public void logError(final String message) {
        logger.error(message);
    }

    @Override
    public ICommandEngine setHelp(final String help) {
        this.help = help;
        return this;
    }

    @Override
    public ICommand getHelpCommand() {
        if (helpCommand == null) {
            helpCommand = commandNames.get(help);
        }
        return helpCommand;
    }

    @Override
    public void loadCommands() {

        if (!commandLoaded) {
            final Set<Class<? extends ICommand>> classes = new HashSet<>();
            for (final String path : commandPackage) {
                try {
                    final Reflections reflections = new Reflections(path);
                    classes.addAll(reflections.getSubTypesOf(ICommand.class));
                } catch (final Exception e) {
                    CommandEngine.getInstance().logError(LocalizedFormat.format("DevelopmentError_ClassPath", path));
                    CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                }
            }
            CommandListUpdateAction commands = null;
            if (jda != null) {
                commands = jda.updateCommands();
            }
            for (final Class<? extends ICommand> command : classes) {
                try {
                    if (command.getPackageName().equals("com.ericramirezs.commando4j.command.command")) continue;
                    if (command.getName().equals("com.ericramirezs.commando4j.command.command.util.SampleCommand"))
                        continue;

                    final ICommand c = command.getDeclaredConstructor().newInstance();

                    if (includesUtils && command.getName().equals("com.ericramirezs.commando4j.command.command.util.HelpCommand"))
                        setHelp(c.getName());
                    addCommand(c);
                    logInfo(LocalizedFormat.format("CommandEngine_Info_CommandLoaded", c.getName()));
                    for (final String alias : c.getAliases()) addAlias(alias, c);
                } catch (final InstantiationException | IllegalAccessException | InvocationTargetException |
                               NoSuchMethodException e) {
                    CommandEngine.getInstance().logError(LocalizedFormat.format("DevelopmentError_CommandInstance", command.getSimpleName(), Arrays.toString(e.getStackTrace())));
                    CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                } catch (final DuplicatedNameException | InvalidNameException e) {
                    CommandEngine.getInstance().logError(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                }
            }
            final List<ICommand> iCommands = getCommands().stream()
                    .filter(c -> c instanceof Slash).toList();
            if (!iCommands.isEmpty()) {
                if (commands == null) {
                    CommandEngine.getInstance().logError(LocalizedFormat.format("DevelopmentError_JDA"));
                } else {
                    final CommandListUpdateAction finalCommands = commands;
                    iCommands.forEach(c -> {
                        setSlashCommand(finalCommands, c);
                        logInfo(LocalizedFormat.format("CommandEngine_Info_SlashLoaded", c.getName()));
                    });
                    commands.queue();
                }
            }
            commandLoaded = false;
        }
    }

    /**
     * Sets a Slash command based on a ICommand to be registered into JDA
     *
     * @param commands Command updater to add the command
     * @param c        ICommand to base the Slash Command
     */
    private void setSlashCommand(final CommandListUpdateAction commands, @NotNull final ICommand c) {
        final SlashCommandData slash = Commands.slash(c.getName(), c.getDescription());
        for (final IArgument arg : c.getArguments()) {
            final OptionData argData = new OptionData(
                    arg.getType().asOptionType(),
                    arg.getName().toLowerCase(), arg.getPrompt(),
                    arg.isRequired(),
                    arg.getValidValues().isEmpty() && arg.getType().asOptionType().canSupportChoices());
            if (!argData.isAutoComplete()) {
                if (arg instanceof IntegerArgument intArg) {
                    for (final Long opt : intArg.getValidValues()) {
                        argData.addChoice(opt.toString(), opt);
                    }
                    if (arg.getMax() != null) argData.setMaxValue(arg.getMax());
                    if (arg.getMin() != null) argData.setMaxValue(arg.getMin());
                }
                if (arg instanceof FloatArgument intArg) {
                    for (final Double opt : intArg.getValidValues()) {
                        argData.addChoice(opt.toString(), opt);
                    }
                    if (arg.getMax() != null) argData.setMaxValue(arg.getMax());
                    if (arg.getMin() != null) argData.setMaxValue(arg.getMin());
                }
                if (arg instanceof StringArgument intArg) {
                    for (final String opt : intArg.getValidValues()) {
                        argData.addChoice(opt, opt);
                    }
                }
            }
            slash.addOptions(argData);
        }
        //noinspection ResultOfMethodCallIgnored
        commands.addCommands(slash);
    }

    @Override
    public boolean isReactToMention() {
        return reactToMention;
    }

    @Override
    public ICommandEngine setReactToMention(final boolean reactToMention) {
        this.reactToMention = reactToMention;
        return this;
    }

    @Override
    public boolean isReactToMention(final Event event) {
        return reactToMention;
    }

    @Override
    public @Nullable ICommand getCommand(@NotNull final String name) {
        if (commandNames.containsKey(name.toLowerCase(Locale.ROOT)))
            return commandNames.get(name.toLowerCase(Locale.ROOT));
        return null;
    }

    @Override
    public @NotNull List<ICommand> getCommandsByPartialMatch(final String name) {
        final Set<String> keys = commandNames.keySet();
        final List<String> found = keys.stream().filter(c -> c.contains(name.toLowerCase(Locale.ROOT))).toList();
        final Set<ICommand> commands = new HashSet<>();
        for (final String k : found) {
            commands.add(commandNames.get(k));
        }
        return new ArrayList<>(commands);
    }

    @Override
    public @NotNull List<ICommand> getCommandsByExactMatch(final String name) {
        final Set<String> keys = commandNames.keySet();
        final List<String> found = keys.stream().filter(c -> c.equals(name.toLowerCase(Locale.ROOT))).toList();
        final Set<ICommand> commands = new HashSet<>();
        for (final String k : found) {
            commands.add(commandNames.get(k));
        }
        return new ArrayList<>(commands);
    }

    @Override
    @Contract(" -> new")
    public final @NotNull @UnmodifiableView List<ICommand> getCommands() {
        return Collections.unmodifiableList(commandList);
    }

    @Override
    public ICommandEngine addCommand(@NotNull final ICommand command) throws DuplicatedNameException, InvalidNameException {
        addAlias(command.getName(), command);
        commandList.add(command);
        return this;
    }

    @Override
    public ICommandEngine addAlias(@NotNull final String name, final ICommand command) throws DuplicatedNameException, InvalidNameException {
        //noinspection RegExpSimplifiable
        if (!name.matches("[a-zA-Z0-9]+")) throw new InvalidCommandNameException(name);
        if (commandNames.containsKey(name)) throw new DuplicatedNameException(name);
        commandNames.put(name, command);
        return this;
    }

    @Override
    public String getPrefix(@NotNull final MessageReceivedEvent event) {
        if (event.isFromGuild()) return repository.getPrefix(event.getGuild().getId());
        return prefix;
    }

    @Override
    public String getPrefix(@NotNull final SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) return repository.getPrefix(Objects.requireNonNull(event.getGuild()).getId());
        return prefix;
    }

    @Override
    public String getPrefix(@NotNull final Event event) {
        if (event instanceof MessageReceivedEvent m && m.isFromGuild())
            return repository.getPrefix(Objects.requireNonNull(m.getGuild()).getId());
        if (event instanceof SlashCommandInteractionEvent s && s.isFromGuild())
            return repository.getPrefix(Objects.requireNonNull(s.getGuild()).getId());
        return prefix;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public ICommandEngine setPrefix(@NotNull final String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public ICommandEngine setPrefix(@NotNull final Guild guild, @NotNull final String prefix) {
        repository.setPrefix(guild.getId(), prefix);
        return this;
    }

    @Override
    public Locale getLanguage() {
        return language;
    }

    @Override
    public ICommandEngine setLanguage(@NotNull final Locale language) {
        this.language = language;
        return this;
    }

    @Override
    public Locale getLanguage(@Nullable final Event event) {
        String id = null;
        if (event instanceof MessageReceivedEvent e && e.isFromGuild()) id = e.getGuild().getId();
        if (event instanceof SlashCommandInteractionEvent e && e.isFromGuild())
            id = Objects.requireNonNull(e.getGuild()).getId();
        if (event instanceof SlashCommandInteractionEvent e && !e.isFromGuild()) return e.getUserLocale();

        if (id == null) return getLanguage();

        final String lang = getRepository().getLanguage(id);
        return Locale.forLanguageTag(lang);
    }

    @Override
    public final @NotNull String getString(@NotNull final String key) {
        return resourceBundle.getString(key);
    }

    @Override
    public final @NotNull String getString(@NotNull final String key, @NotNull final Locale locale) {
        return resourceBundle.getString(locale, key);
    }

    @Override
    public final @NotNull String @NotNull [] getStringArray(@NotNull final String key) {
        return resourceBundle.getStringArray(key);
    }

    @Override
    public final @NotNull String @NotNull [] getStringArray(@NotNull final String key, @NotNull final Locale locale) {
        return resourceBundle.getStringArray(locale, key);
    }

    private ICommand getCalledCommand(@NotNull final MessageReceivedEvent event) {
        final String msg = event.getMessage().getContentRaw().toLowerCase();
        final String mention = event.getJDA().getSelfUser().getAsMention();
        final String prefix = CommandEngine.getInstance().getPrefix(event);
        final int index = msg.startsWith(mention + " ") ? 1 : 0;
        final String command = msg.split(" ")[index];
        final String name;

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

    public ICommandEngine setJda(final JDA jda) {
        this.jda = jda;
        return this;
    }

    @Override
    public IRepository getRepository() {
        return repository;
    }

    public static void setRepository(final IRepository repository) {
        CommandEngine.repository = repository;
    }

    @Override
    public final void onMessageReceived(@NotNull final MessageReceivedEvent event) {
        try {
            final ICommand command = getCalledCommand(event);
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
        } catch (final Exception ex) {
            logError(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {

        final ICommand c = getCommand(event.getName());

        if (c == null) return;

        if (!Slash.shouldRun(event, c)) {
            Slash.sendReply(event, LocalizedFormat.format("Slash_InvalidChannel", event));
            return;
        }

        final String permissionError = c.checkPermissions(event);

        if (!StringUtils.isNullOrWhiteSpace(permissionError)) {
            Slash.sendReply(event, permissionError);
            return;
        }

        if (c.getArguments().size() == 0) ((Slash) c).run(event, Collections.unmodifiableMap(new HashMap<>(0)));
        else {
            try {
                final List<OptionMapping> opts = event.getOptions();
                for (final OptionMapping opt : opts) {
                    final String key = opt.getName();
                    final IArgument argument = c.getArgument(opt.getName());
                    switch (opt.getType()) {
                        case STRING -> argument.setSlashValue(event, opt.getAsString());
                        case INTEGER -> argument.setSlashValue(event, opt.getAsLong());
                        case BOOLEAN -> argument.setSlashValue(event, opt.getAsBoolean());
                        case USER, MENTIONABLE, CHANNEL ->
                                argument.setSlashValue(event, opt.getAsMentionable().getAsMention());
                        case ROLE -> argument.setSlashValue(event, opt.getAsRole().getAsMention());
                        case NUMBER -> argument.setSlashValue(event, opt.getAsDouble());
                        case ATTACHMENT -> argument.setSlashValue(event, opt.getAsAttachment());
                    }
                }
                final Map<String, IArgument> args = new HashMap<>();
                for (final IArgument arg : c.getArguments()) {
                    args.put(arg.getName(), arg);
                }
                ((Slash) c).run(event, Collections.unmodifiableMap(args));
            } catch (final Exception ex) {
                Slash.sendReply(event, ex.getMessage(), true);
            }
        }
    }

    /**
     * Creates a JDA with low memory profile settings and with ICommandEngine preset.
     *
     * @param token The bot token to use.
     * @return A JDA instance that has started the login process.
     * It is unknown to whether or not loading has finished when this returns.
     * @throws LoginException           If the provided token is invalid.
     * @throws IllegalArgumentException If the provided token is empty or null.
     *                                  Or the provided intents/cache configuration is not possible.
     * @see JDABuilder#createLight(String)
     * @see JDABuilder#build()
     */
    public static @NotNull JDA createLight(final String token) throws LoginException, IllegalArgumentException {
        return create(JDABuilder.createLight(token));
    }

    /**
     * Creates a JDA with low memory profile settings and with ICommandEngine preset.
     *
     * @param token   The bot token to use.
     * @param intents The gateway intents to use
     * @return A JDA instance that has started the login process.
     * It is unknown to whether or not loading has finished when this returns.
     * @throws LoginException           If the provided token is invalid.
     * @throws IllegalArgumentException If the provided token is empty or null.
     *                                  Or the provided intents/cache configuration is not possible.
     * @see JDABuilder#createLight(String, Collection)
     * @see JDABuilder#build()
     */
    public static @NotNull JDA createLight(final String token, final Collection<GatewayIntent> intents)
            throws LoginException, IllegalArgumentException {
        return create(JDABuilder.createLight(token, intents));
    }

    /**
     * Creates a JDA with low memory profile settings and with ICommandEngine preset.
     *
     * @param token   The bot token to use
     * @param intent  The first intent to use
     * @param intents The other gateway intents to use
     * @return A JDA instance that has started the login process.
     * It is unknown to whether or not loading has finished when this returns.
     * @throws LoginException           If the provided token is invalid.
     * @throws IllegalArgumentException If the provided token is empty or null.
     *                                  Or the provided intents/cache configuration is not possible.
     * @see JDABuilder#createLight(String, GatewayIntent, GatewayIntent...)
     * @see JDABuilder#build()
     */
    public static @NotNull JDA createLight(final String token, final GatewayIntent intent, final GatewayIntent... intents)
            throws LoginException, IllegalArgumentException {
        return create(JDABuilder.createLight(token, intent, intents));
    }

    /**
     * Creates a JDA with the predefined token.
     *
     * @param token   The bot token to use
     * @param intents The  gateway intents to use
     * @return A JDA instance that has started the login process.
     * It is unknown to whether or not loading has finished when this returns.
     * @throws LoginException           If the provided token is invalid.
     * @throws IllegalArgumentException If the provided token is empty or null.
     *                                  Or the provided intents/cache configuration is not possible.
     * @see JDABuilder#create(String, GatewayIntent, GatewayIntent...)
     * @see JDABuilder#build()
     */
    public static @NotNull JDA create(final String token, @Nonnull final Collection<GatewayIntent> intents)
            throws LoginException, IllegalArgumentException {
        return create(JDABuilder.create(token, intents));
    }

    /**
     * Creates a JDA with the predefined token.
     *
     * @param token   The bot token to use
     * @param intent  The first intent to use
     * @param intents The other gateway intents to use
     * @return A JDA instance that has started the login process.
     * It is unknown to whether or not loading has finished when this returns.
     * @throws LoginException           If the provided token is invalid.
     * @throws IllegalArgumentException If the provided token is empty or null.
     *                                  Or the provided intents/cache configuration is not possible.
     * @see JDABuilder#create(String, GatewayIntent, GatewayIntent...)
     * @see JDABuilder#build()
     */
    public static @NotNull JDA create(final String token, final GatewayIntent intent, final GatewayIntent... intents)
            throws LoginException, IllegalArgumentException {
        return create(JDABuilder.create(token, intent, intents));
    }

    /**
     * Creates a JDABuilder with recommended default settings.
     *
     * @param token The bot token to use.
     * @return A JDA instance that has started the login process.
     * It is unknown to whether or not loading has finished when this returns.
     * @throws LoginException           If the provided token is invalid.
     * @throws IllegalArgumentException If the provided token is empty or null.
     *                                  Or the provided intents/cache configuration is not possible.
     * @see JDABuilder#createDefault(String)
     * @see JDABuilder#build()
     */
    public static @NotNull JDA createDefault(final String token) throws LoginException, IllegalArgumentException {
        return create(JDABuilder.createDefault(token));
    }

    /**
     * Creates a JDABuilder with recommended default settings.
     *
     * @param token   The bot token to use.
     * @param intents The gateway intents to use
     * @return A JDA instance that has started the login process.
     * It is unknown to whether or not loading has finished when this returns.
     * @throws LoginException           If the provided token is invalid.
     * @throws IllegalArgumentException If the provided token is empty or null.
     *                                  Or the provided intents/cache configuration is not possible.
     * @see JDABuilder#createDefault(String, Collection)
     * @see JDABuilder#build()
     */
    public static @NotNull JDA createDefault(final String token, final Collection<GatewayIntent> intents)
            throws LoginException, IllegalArgumentException {
        return create(JDABuilder.createDefault(token, intents));
    }

    /**
     * Creates a JDABuilder with recommended default settings.
     *
     * @param token   The bot token to use
     * @param intent  The first intent to use
     * @param intents The other gateway intents to use
     * @return A JDA instance that has started the login process.
     * It is unknown to whether or not loading has finished when this returns.
     * @throws LoginException           If the provided token is invalid.
     * @throws IllegalArgumentException If the provided token is empty or null.
     *                                  Or the provided intents/cache configuration is not possible.
     * @see JDABuilder#createDefault(String, GatewayIntent, GatewayIntent...)
     * @see JDABuilder#build()
     */
    public static @NotNull JDA createDefault(final String token, final GatewayIntent intent, final GatewayIntent... intents)
            throws LoginException, IllegalArgumentException {
        return create(JDABuilder.createDefault(token, intent, intents));
    }

    /**
     * <p>
     * <strong>This will build the JDA object.</strong>
     * </p>
     *
     * @param builder preset JDABuilder
     * @return A JDA instance that has started the login process.
     * It is unknown to whether or not loading has finished when this returns.
     * @throws LoginException           If the provided token is invalid.
     * @throws IllegalArgumentException If the provided token is empty or null.
     *                                  Or the provided intents/cache configuration is not possible.
     */
    public static @NotNull JDA create(@NotNull final JDABuilder builder) throws LoginException, IllegalArgumentException {
        final ICommandEngine engine = CommandEngine.getInstance();
        final JDA jda = builder.addEventListeners(engine)
                .build();
        if (engine instanceof CommandEngine e) e.setJda(jda);

        engine.loadCommands();

        return jda;
    }
}
