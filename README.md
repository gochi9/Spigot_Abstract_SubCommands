# Example

<details>
<summary>Reload Command Code (Click to view)</summary>

```java
public class ReloadConfig extends SubCommand {

    private final SASC main;

    public ReloadConfig(SASC main) {
        super("sasc.reload", CommandType.BOTH, 0, "/sasc reload - Reloads the config", "" /*No extra arguments, no need for a syntax message since it will never get called*/);
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.main.saveDefaultConfig();
        this.main.reloadConfig();
        sender.sendMessage("You have reloaded the config.");
    }
}
```

</details>

<details>
<summary>Simple Teleport to player command (Click to view)</summary>

```java
public class TeleportToPlayer extends SubCommand {

    private final SomeManager someManager;

    public TeleportToPlayer(SomeManager someManager) {
        super("sasc.teleport", CommandType.PLAYER, 1, "/sasc teleport {player} - Teleports to a player", "Invalid syntax, use: /sasc teleport {player}");
        this.someManager = someManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //We do not need to check for the argument size because we specifically tell the SubCommand that we need at least one extra argument after the sub command
        //If the arg length condition is not met then this method won't get called
        Player player = Bukkit.getPlayer(args[1]);

        if(player == null){
            sender.sendMessage("Player " + args[1] + " is offline.");
            return;
        }

        ((Player)sender).teleport(player);
    }

    @Override
    public List<String> tabCompleter(CommandSender sender, String[] args) {
        if(!canExecute(sender, 0, false) || args.length != 2)
            return MainCommand.EMPTY;

        if(!args[1].isEmpty())
            return someManager.getName(args[1]);

        else
            return someManager.getNames();
    }
}
```

</details>

<details>
<summary>Main class code (Click to view)</summary>

```java
public final class SASC extends JavaPlugin {

    private SomeManager someManager;

    @Override
    public void onEnable() {
        this.someManager = new SomeManager();

        this.getCommand("sasc").setExecutor(new MainCommand(this, someManager));
        //Add more commands if needed
    }

}
```

</details>

# MainCommand Class - How It Works

<details>
<summary>Main Command class (Click to view)</summary>

```java
public class MainCommand implements CommandExecutor, TabCompleter {

    public final static List<String> EMPTY = Collections.emptyList();

    private final HashMap<String, SubCommand> subCommands;

    public MainCommand(SASC main, SomeManager someManager) {
        this.subCommands = new HashMap<>();
        this.subCommands.put("teleport", new TeleportToPlayer(someManager));
        this.subCommands.put("reload", new ReloadConfig(main));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length < 1 || args[0].equalsIgnoreCase("help")){
            List<String> allowedCommands = getAllowedCommands(sender);

            if(allowedCommands.isEmpty())
                sender.sendMessage("No available commands.");
            else
                allowedCommands.forEach(allowedCommand -> sender.sendMessage(subCommands.get(allowedCommand).getCommandHelpMessage()));

            return true;
        }

        SubCommand subCommand = subCommands.get(args[0]);

        if(subCommand == null){
            sender.sendMessage("Invalid command.");
            return true;
        }

        if(!subCommand.canExecute(sender, args.length, true))
            return true;

        subCommand.execute(sender, args);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0)
            return EMPTY;

        if(args.length == 1)
            return getAllowedCommands(sender);

        SubCommand subCommand = subCommands.get(args[0]);
        return subCommand != null && subCommand.isVisible() && subCommand.canExecute(sender, 0, false) ? subCommand.tabCompleter(sender, args) : EMPTY;
    }

    private List<String> getAllowedCommands(CommandSender sender) {
        List<String> allowedCommands = new LinkedList<>();
        subCommands.forEach((k, v) -> {
            if(v.isVisible() && v.canExecute(sender, 0, false))
                allowedCommands.add(k);
        });

        return allowedCommands;
    }

}
```

</details>

The `MainCommand`(Name used for this example, you can change it) class is like the central hub for commands. When you type `/sasc` or any subcommand, this is the part of the code that figures out what to do. It uses Spigot's `CommandExecutor` interface to process the command and the `TabCompleter` interface for tab completion.

---

## What It Does

The class starts by setting up a map of subcommands. Each subcommand is linked to a name, so when you type something like `/sasc teleport`, the class checks its map, finds the corresponding subcommand (`TeleportToPlayer`), and runs it. If you type something invalid, it lets you know and stops there.

---

## Constructor

The constructor initializes the command. It sets up a `HashMap` that stores all subcommands by their name (like "teleport" or "reload"). Then it adds each subcommand you want to include in the plugin.

```java
    private final HashMap<String, SubCommand> subCommands;

    public MainCommand(SASC main, SomeManager someManager) {
        this.subCommands = new HashMap<>();
        this.subCommands.put("teleport", new TeleportToPlayer(someManager));
        this.subCommands.put("reload", new ReloadConfig(main));
    }
```

---

## onCommand Method

This is the method that gets triggered when you type `/sasc`. It checks the arguments you provided and tries to find a matching subcommand.

1. If you didn’t type any subcommand or used `/sasc help`, it lists all available subcommands.
2. If you typed an invalid subcommand, it sends a message saying it’s invalid.
3. If everything checks out, it calls the `execute` method of the matching subcommand.

This method uses the Spigot `CommandExecutor` API. When a command is run, Spigot calls this function and passes in everything it knows: who sent the command, the command itself, and any arguments.

```java
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length < 1 || args[0].equalsIgnoreCase("help")){
            List<String> allowedCommands = getAllowedCommands(sender);

            if(allowedCommands.isEmpty())
                sender.sendMessage("No available commands.");
            else
                allowedCommands.forEach(allowedCommand -> sender.sendMessage(subCommands.get(allowedCommand).getCommandHelpMessage()));

            return true;
        }

        SubCommand subCommand = subCommands.get(args[0]);

        if(subCommand == null){
            sender.sendMessage("Invalid command.");
            return true;
        }

        if(!subCommand.canExecute(sender, args.length, true))
            return true;

        subCommand.execute(sender, args);
        return true;
    }
```

---

## onTabComplete Method

This method handles tab completion, giving users suggestions for commands as they type. It first checks all registered subcommands to see which ones you have permission to use. If you’re allowed to run a command, it shows up in the suggestions. This means players only see commands they can actually execute.

Once you’ve typed enough to identify a specific subcommand (like `/sasc teleport`), it hands over control to that subcommand’s tab completion logic. The subcommand’s `tabCompleter` method then decides what suggestions to show based on the current argument you’re typing.

```java
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0)
            return EMPTY;

        if(args.length == 1)
            return getAllowedCommands(sender);

        SubCommand subCommand = subCommands.get(args[0]);
        return subCommand != null && subCommand.isVisible() && subCommand.canExecute(sender, 0, false) ? subCommand.tabCompleter(sender, args) : EMPTY;
    }
```

---

## getAllowedCommands Method

This is a helper function that figures out which subcommands the user is allowed to execute. It looks through all registered subcommands and checks if the sender has permission for each one (permission only applies to players since console already has permission). If they don’t, it skips that subcommand.

```java
    private List<String> getAllowedCommands(CommandSender sender) {
    List<String> allowedCommands = new LinkedList<>();
    subCommands.forEach((k, v) -> {
        if(v.isVisible() && v.canExecute(sender, 0, false))
            allowedCommands.add(k);
    });

    return allowedCommands;
}
```

# SubCommand Class - How It Works

<details>
<summary>SubCommand Abstract Class (Click to view)</summary>

```java

```

</details>

The `SubCommand` class is the backbone of this command system. It’s an abstract class, which means you don’t use it directly. Instead, you extend it to create specific commands like teleport or reload. This class provides a framework for handling permissions, argument validation, and command execution.

---

## What It Does

The `SubCommand` class handles the boring but necessary stuff that every command needs, like:
- Checking if the user is allowed to run the command.
- Making sure the right number of arguments is provided.
- Distinguishing between players and the console.
- Providing help messages or syntax error messages if the command is used incorrectly.

When you extend this class, you only need to implement the `execute` method and optionally the `tabCompleter` method if your command needs tab completion.

---

## Constructor

The constructor sets up all the important details about the command. This includes things like the permission required to run it, whether it’s for players, the console, or both, and what messages to display when something goes wrong.

- `permission`: The permission string required to execute the command.
- `commandType`: Specifies if the command is for players, the console, or both.
- `argsRequired`: How many arguments are needed for the command to work.
- `commandHelpMessage`: A help message that explains what the command does.
- `commandWrongSyntax`: The message displayed when the command is used incorrectly.
- `visible`: Determines if the command shows up in help messages or tab completion.

```java
    private final String permission;
    private final CommandType commandType;
    private final int argsRequired;
    private final String commandHelpMessage;
    private final String commandWrongSyntax;
    private final boolean visible;

    public SubCommand(String permission, CommandType commandType, int argsRequired, String commandHelpMessage, String commandWrongSyntax){
        this(permission, commandType, argsRequired, commandHelpMessage, commandWrongSyntax, true);
    }

    public SubCommand(String permission, CommandType commandType, int argsRequired, String commandHelpMessage, String commandWrongSyntax, boolean visible) {
        this.permission = permission;
        this.commandType = commandType;
        this.argsRequired = ++argsRequired;
        this.commandHelpMessage = commandHelpMessage;
        this.commandWrongSyntax = commandWrongSyntax;
        this.visible = visible;
    }
```

---

## canExecute Method

This method checks if the command can actually be run by the sender. It handles several checks:
1. **Player vs. Console**: If the command is restricted to players, it makes sure the sender is a player. If it’s restricted to the console, it ensures the sender isn’t a player.
2. **Permission Checks**: Verifies if the sender has the required permission.
3. **Argument Validation**: Makes sure the sender provided enough arguments.

If any of these checks fail, it optionally sends an error message back to the sender if the command is run, since this method is also called by the tabCompletion logic, it should not throw an error message to the player just for trying to type of the command.

```java
    protected boolean canExecute(CommandSender sender, int argsLength, boolean sendMessage){
        boolean isPlayer = sender instanceof Player;
        if(commandType == CommandType.PLAYER && !isPlayer){
            if(sendMessage)
                sender.sendMessage("Only a player may execute this command.");
            return false;
        }

        if(isPlayer && !sender.hasPermission(permission)){
            if(sendMessage)
                sender.sendMessage("You do not have the permission to execute this command.");
            return false;
        }

        if(commandType == CommandType.CONSOLE && !(sender instanceof ConsoleCommandSender)){
            if(sendMessage)
                sender.sendMessage("Command can only be executed by the console.");
            return false;
        }

        if(sendMessage && argsRequired > 1 && argsLength < argsRequired){
            sender.sendMessage(commandWrongSyntax);
            return false;
        }

        return true;
    }
```

---

## execute Method

This is the method you must implement when extending `SubCommand`. It’s where the actual logic for the command goes, like teleporting a player or reloading a config file. This method gets called when the main command determines the subcommand should be executed.

```java
    public abstract void execute(CommandSender sender, String[] args);
```

---

## tabCompleter Method

This method is optional. If you want your subcommand to support tab completion, you override this method. By default, it returns an empty list, meaning no suggestions are provided.

```java
    public List<String> tabCompleter(CommandSender sender, String[] args){
        return MainCommand.EMPTY;
    }
```

---

## getCommandHelpMessage Method

This method simply returns the help message you set when creating the subcommand. It’s useful for showing the user a list of available commands or giving instructions when they ask for help.

```java
    public String getCommandHelpMessage(){
        return commandHelpMessage;
    }
```

---

## isVisible Method

This method checks whether the command should appear in the list of available commands, or in tab completer suggestions. If you set `visible` to `false` in the constructor, the command won’t show up in help messages but can still be executed if the name is typed and the conditions are met.

```java
    public boolean isVisible(){
        return visible;
    }
```

---

# SomeManager Class - What It Does

<details>
<summary>SomeManager Class (Click to view)</summary>

```java
public class SomeManager {

    private final NameSearcher nameSearcher;
    private final HashMap<String, UUID> uuids;

    public SomeManager() {
        this.nameSearcher = new NameSearcher();
        this.uuids = new HashMap<>();

        for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String name = offlinePlayer.getName();
            if (name == null)
                continue;

            name = name.toLowerCase();
            uuids.put(name, offlinePlayer.getUniqueId());
            nameSearcher.addName(name);
        }
    }

    public List<String> getName(String index){
        return index == null || index.isBlank() ? MainCommand.EMPTY : nameSearcher.search(index.toLowerCase());
    }

    public List<String> getNames(){
        return new LinkedList<>(uuids.keySet());
    }

    public UUID getOfflineUUID(String name){
        return name != null ? uuids.get(name.toLowerCase()) : null;
    }

    public void onJoin(Player player){
        String name = player.getName().toLowerCase();
        uuids.put(name, player.getUniqueId());
        nameSearcher.addName(name);
    }

}
```

</details>

The `SomeManager` (Example name, please use better names) class helps deal with player-related stuff. It’s not strictly necessary, but it’s handy if you need to work with offline players or want to suggest player names in tab completion. This class takes care of storing names and UUIDs for all players who have ever logged into the server, so you can easily access them later.

---

## Purpose

This class exists to simplify two things:
1. **Name Suggestions**: If you need to suggest player names, this class can handle it, even for players who are offline.
2. **UUID Management**: Quickly find a player’s UUID using their name, which is useful for a lot of backend operations.

You can remove this class and its companion, `NameSearcher`, if you don’t care about offline players or name-based lookups. Everything else in the command system will still work.

---

## Constructor

When `SomeManager` is initialized, it:
- Creates a `NameSearcher` instance for name lookups.
- Creates a `HashMap` to store lowercase player names as keys and their UUIDs as values.
- Loops through all offline players on the server using `Bukkit.getOfflinePlayers()` and populates the map and the `NameSearcher`.

This setup ensures that all known player names and UUIDs are ready to be used at any time.

---

## getName Method

This method takes a string (like a search query) and returns a list of matching player names. It uses the `NameSearcher` to find all names that start with the provided string.

If the query is null or blank, it returns an empty list. Otherwise, it searches the `NameSearcher` for matches.

---

## getNames Method

This method returns a complete list of all known player names. It creates a new list from the keys of the UUID map. This is useful for tab completion when no partial input is provided.

---

## getOfflineUUID Method

This method takes a player name and returns their UUID. If the name doesn’t exist in the map, it returns `null`. It’s case-insensitive because all names are stored in lowercase.

---

## onJoin Method

When a player joins the server, this method can be called to update the manager’s data. It adds the player’s name and UUID to both the `HashMap` and the `NameSearcher`. This keeps the manager up to date with new players.

---

## How It Helps

`SomeManager` is mostly about convenience. Instead of writing code every time to fetch player names or UUIDs, this class does it for you. It also ensures you don’t have to worry about whether a player is online or offline—it just works. If


---

## NameSearcher: What's Going On Here?

<details>
<summary>NameSearcher Helper Class (Click to view)</summary>

```java
public class NameSearcher {

    private final TrieNode root;

    public NameSearcher() {
        root = new TrieNode();
    }

    public void addName(String name) {
        TrieNode node = root;
        for (char c : name.toCharArray()) {
            node = node.getChildren()
                    .computeIfAbsent(c, k -> new TrieNode());
        }
        node.setEndOfWord(true);
    }

    public List<String> search(String prefix) {
        List<String> result = new LinkedList<>();
        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            node = node.getChildren().get(c);
            if (node == null)
                return result;

        }

        dfs(node, new StringBuilder(prefix), result);
        return result;
    }

    private void dfs(TrieNode node, StringBuilder prefix, List<String> result) {
        if (node.isEndOfWord())
            result.add(prefix.toString());

        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            prefix.append(entry.getKey());
            dfs(entry.getValue(), prefix, result);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    private static class TrieNode {
        private final Map<Character, TrieNode> children = new HashMap<>();
        private boolean endOfWord;

        public Map<Character, TrieNode> getChildren() {
            return children;
        }

        public boolean isEndOfWord() {
            return endOfWord;
        }

        public void setEndOfWord(boolean endOfWord) {
            this.endOfWord = endOfWord;
        }
    }
}


```

</details>

The `NameSearcher` is this thing I threw together to make it easy to look up player names based on prefixes. It uses a data structure called a "Trie," which sounds fancy but really just means it’s a tree where each node is a character in a string. The whole point is to make prefix searches (like autocompleting names) really fast.

Here’s how it works in a nutshell:

---

### Adding Names

When you add a name to the `NameSearcher`, it’s split into characters. Each character gets its own spot in the tree, starting at the root. If the next character doesn’t exist yet, a new node is created for it. By the time you get to the end of the name, the last node is marked as "this is the end of a word." That way, we know where valid names stop.

---

### Searching for Names

If you want to find all names that start with, say, "steve," you first go down the tree following the characters 's', 't', 'e', 'v', and 'e'. If you can get to the last 'e', you know the prefix exists. From there, it runs a search to find all the possible names that branch out from that point. Basically, it collects everything below that node that’s marked as the end of a word.

---

### Efficiency and Stuff

So, why even bother with this? 

Well, It's pretty fast.
- Adding a name takes as long as the name is (like if it’s "Notch," it’s five steps).
- Searching for all names with a prefix also just depends on how long the prefix is, plus some time to gather the matches.

It also saves memory by sharing nodes when names have the same starting letters. Like, if you’ve got "Steve" and "Steven," they both share the same path for 's', 't', 'e', and 'v', and the tree only branches when the names start to differ.

---

### Why This Matters

Spigot's default TabCompletion implementation can be problematic, as the thing runs on the main thread. If your lookup logic isn’t efficient, and you’ve got a decent number of players who’ve joined your server, things can go south real quick. Inefficient lookups give bad actors a way to slow down your server with scripts that spam tab completion requests. Sure, most servers have ways to limit this, but honestly, I think it’s better to be safe than sorry. The NameSearcher helps by making lookups as fast as possible, reducing the risk of any performance issues from spamming tab completions.

This is most useful when you need to cycle through *every* offline player that has every played for whatever reason, maybe give them an item, maybe ban them? Idk

---

### Downsides

If you’ve got a like million players, the tree can get big, and it might use more memory than a plain list. But realistically the server won't grow big enough for this to actually become an issue

---