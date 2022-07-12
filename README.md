[![](https://jitpack.io/v/EricRamirezS/commando4j.svg)](https://jitpack.io/#EricRamirezS/commando4j)
[![Crowdin](https://badges.crowdin.net/jdacommand4j/localized.svg)](https://crowdin.com/project/jdacommand4j)
[license]: https://github.com/EricRamirezS/commando4j/blob/master/LICENSE

# commando4j

commando4j is an unofficial extension for
[JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA)
, that aims to simplify the creation of bot that react to commands
by automating the input and validation of arguments, permission validation,
and many other requirements you may need. also offering feedback in multiple languages.

## Summary

_Please see the [Discord docs](https://discord.com/developers/docs/reference) for more information about bot accounts._

1. [Getting Started](#initializing-the-Command-Engine)
2. [Command](#creating-the-first-command)
3. [Slash](#make-it-slash-command)
4. [Arguments](#arguments)
5. [Constrains](#command-constrains)
6. [Final step](#lets-link-everything)
7. [Download](#download)
8. [Dependencies](#dependencies)

## UserBots and SelfBots

Discord is currently prohibiting creation and usage of automated client accounts (AccountType.CLIENT).
Since we use the latest version of [JDA](https://github.com/DV8FromTheWorld/JDA), there is no support for client login.
Note that [JDA](https://github.com/DV8FromTheWorld/JDA) is not a good
tool to build a custom discord client as it loads all servers/guilds on startup unlike
a client which does this via lazy loading instead.
If you need a bot, use a bot account from the [Application Dashboard](https://discord.com/developers/applications).

# Initializing the Command Engine

Initializing the command Engine is done using the JDABuilder class. After setting the token and other options via
setters,
create the JDA via `CommandEngine.create()` instead of `build()`. When `CommandEngine.create()` returns, JDA might
not have finished starting up. However, you can use `awaitReady()` on the JDA object to ensure that the entire cache is
loaded before proceeding. Note that this method is blocking and will cause the thread to sleep
until startup has completed.

```java
public class Main {
    public static void main(String[] args) {
        JDABuilder jdaBuilder = JDABuilder.createDefault("token");
        JDA jda = CommandEngine.create(jdaBuilder);
    }
}
```

> For further information about setting JDABuilder see
[JDABuilder](https://ci.dv8tion.net/job/JDA5/javadoc/net/dv8tion/jda/api/JDABuilder.html)

# Creating the First Command

In order to create a Command, just create a new Class, and Extend Command.
You'll need to call the Parent constructor with three parameters:

1. `Command Name`: this is how the user will call the command (It **must** be unique)
2. `Command group`: To categorize the command in the help command.
3. `Command Description`: What does the command do?

You'll also need to override the run method, this is where the main functionality goes

1. `event`: JDA's MessageReceivedEvent that made this command call.
2. `args`: A map with already parsed arguments required to run this argument.

````java
import com.ericramirezs.commando4j.command.Command;

public class MyFirstCommand extends Command {

    public HelloWorldExample() throws DuplicatedArgumentNameException {
        super("hello", "utils", "I will answer this command with a \"Hello World\"");
    }

    @Override
    public void run(MessageReceivedEvent event, Map<String, IArgument> args) {
        sendReply(event, "Hello world");
    }
}
````

## Make it Slash Command

With just a few steps, we can make our previous command into a Discord's slash command,
we just need to implement the Slash interface and override another run method. It also comes with two
parameters

1. `event`: JDA's SlashCommandInteractionEvent that made this command call.
2. `args`: A map with already parsed arguments required to run this argument.

````java
import com.ericramirezs.commando4j.Slash;
import com.ericramirezs.commando4j.command.Command;

public class MyFirstCommand extends Command implements Slash {

    public HelloWorldExample() throws DuplicatedArgumentNameException {
        super("hello", "utils", "I will answer this command with a \"Hello World\"");
    }

    @Override //Normal call through chat
    public void run(MessageReceivedEvent event, Map<String, IArgument> args) {
        sendReply(event, "Hello world");
    }

    @Override //Call through Application Command <<  /  >>
    public void run(SlashCommandInteractionEvent event, Map<String, IArgument> args) {
        sendReply(event, "Hello world");
    }
}
````

# Let's get serious

Ok, we are not here to make a Hello World Command, right?

With Command, we can automatize most validations and constrains for our command...
and... Command's arguments.

## Arguments

There are multiple types of arguments already implementing for you to use. The `run()`
methods will not be executed until all argument are valid. We take care of asking
the user to do it right, you just have to wait for us to call `run()`.

To set the Arguments, we need to include them in the constructor.
Arguments require two elements to be created:

1. `name`: a name to identify this argument
2. `prompt`: to explain the user what you want him to input.

```java
public class HelloWorldExample extends Command {

    public HelloWorldExample() throws DuplicatedArgumentNameException {
        super("hello", "utils", "I will answer this command with a \"Hello World\"",
                new StringArgument("param1", "I'm an argument."));
    }
    ...
}
```

Then, to get the Argument in our run function

```java
public class HelloWorldExample extends Command {
    ...

    public void run(MessageReceivedEvent event, Map<String, IArgument> args) {
        StringArgument param1 = (StringArgument) args.get("param1");
        String param1Value = param1.getValue();
    }
}
```

Keep in mind that, by default, all arguments are optional, so their value might be null.
To force the use to input the Argument, we need to call `setRequired()`.
You may also use `setDefaultValue()` to allow the user skip the argument.

- `getValue()` of a "Required" argument will never be `null`.
- `getValue()` of an Argument with a "`default value`" argument will never be `null`

```java
public class HelloWorldExample extends Command {
    public HelloWorldExample() throws DuplicatedArgumentNameException {
        super("hello", "utils", "I will answer this command with a \"Hello World\"",
                new StringArgument("param1", "I'm a required argument.")
                        .setRequired(),
                new StringArgument("param2", "I'm an optional argument."));
    }
    ...
}
```

> For more information about Supported Argument Types,
> see [javadocs Arguments](http://commando4j.javadocs.ericramirezs.com/com/ericramirezs/commando4j/arguments/package-summary.html)

## Command Constrains

There are multiples validations we can add on the creation of our Command that will be
checked automatically. The Command Engine will handle with the user all validations required. The run command will not
be
called until every single validation is met. The validations will apply for Slash Commands as well.

```java
public class ImportantRoleCommand extends Command {
    public ImportantRoleCommand() throws DuplicatedArgumentNameException {
        super("roleAdmin", "examples", "Hello world");
        // This command will only run inside Discord Servers
        setGuildOnly();
        // This command will only run if the bot has Manage Roles Permission
        addClientPermissions(Permission.MANAGE_ROLES);
        // This command will only run if the User calling the command is an Admin
        addMemberPermissions(Permission.ADMINISTRATOR);
    }
     ...
}
```

> To see more constraint, validations and additional info related to Commands, see
> [javadocs Command](http://commando4j.javadocs.ericramirezs.com/index.html?com/ericramirezs/commando4j/arguments/package-summary.html)

# Let's link everything

The final step is to add the Command we created into the Command Engine.

You can do that by just telling the Command Engine where are you Command located **BEFORE** creating the JDA object.

For example: in the package "my.package.bot.command",

You may also include the build-in utils Commands:

* `help`: display info about commands.
* `prefix`: changes the prefix the bot reacts to in a Server.
* `ping`: test to see if the bot is reachable.
* `language`: changes the language the bot uses in a server.

```java
public class Main {
    public static void main(String[] args) {
        CommandEngine.addCommandPackage("my.package.bot.command");
        CommandEngine.includeBuildInUtils();
        JDABuilder jdaBuilder = JDABuilder.createDefault("token");
        JDA jda = CommandEngine.create(jdaBuilder);
    }
}
```

# Download

[![](https://jitpack.io/v/EricRamirezS/commando4j.svg)](https://jitpack.io/#EricRamirezS/commando4j)

Latest Release:  [GitHub Release](https://github.com/EricRamirezS/commando4j/releases/latest)

Be sure to replace the VERSION key below with the one of the versions shown above! For snapshots, please use the
instructions provided by [JitPack](https://jitpack.io/#Ericramirezs/commando4j).

### Gradle

Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency

```gradle
dependencies {
	implementation 'com.github.EricRamirezS:commando4j:-SNAPSHOT'
}
```

### Maven

Add the JitPack repository to your build file.

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency

```xml

<dependency>
    <groupId>com.github.EricRamirezS</groupId>
    <artifactId>commando4j</artifactId>
    <version>-SNAPSHOT</version>
</dependency>
```

# Logging Framework - SLF4J

commando4j is using SLF4J to log its messages.

That means you should add some SLF4J implementation to your build path in addition to JDA. If no implementation is
found, following message will be printed to the console on startup:

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

commando4j logs thought the fallback Logger provided by JDA In case that no SLF4J implementation is present.
We strongly recommend to use one though, as that can improve speed and allows you to customize the Logger as well as log to files

There is a guide for logback-classic available in our wiki: [Logging Setup](https://github.com/DV8FromTheWorld/JDA/wiki/Logging-Setup)

# Dependencies:

This project requires **Java 8+**.
All dependencies are managed automatically by Maven.

* JDA
    * Version: 5.0.0-alpha.13}
    * [Github](https://github.com/DV8FromTheWorld/JDA)
        * Note: Opus excluded
* Jetbrains annotations
    * Version: 23.0.0
    * [Github](https://github.com/JetBrains/java-annotations)
* reflections
    * Version: 0.10.2
    * [Github](https://github.com/ronmamo/reflections)
* SQLite JDBC Driver
    * Version: 3.36.0.3
    * [Github](https://github.com/xerial/sqlite-jdbc)
* JUnit Jupiter API
    * Version: 5.8.2
    * [Github](https://github.com/junit-team/junit5)
* slf4j-api
  * Version: **1.7.36**
  * [Website](https://www.slf4j.org/)

**See also:** https://discord.com/developers/docs/topics/community-resources#libraries