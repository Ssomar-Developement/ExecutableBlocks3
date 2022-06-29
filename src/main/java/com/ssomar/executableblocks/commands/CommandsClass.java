package com.ssomar.executableblocks.commands;


import com.ssomar.executableblocks.ExecutableBlocks;
import com.ssomar.executableblocks.configs.api.PlaceholderAPI;
import com.ssomar.executableblocks.editor.NewExecutableBlocksEditor;
import com.ssomar.executableblocks.events.optimize.OptimizedEventsHandler;
import com.ssomar.executableblocks.executableblocks.ItemStackToExecutableBlockConverter;
import com.ssomar.executableblocks.executableblocks.NewExecutableBlock;
import com.ssomar.executableblocks.executableblocks.loader.NewExecutableBlockLoader;
import com.ssomar.executableblocks.executableblocks.manager.NewExecutableBlocksManager;
import com.ssomar.score.actionbar.ActionbarCommands;
import com.ssomar.score.commands.ClearCommand;
import com.ssomar.score.commands.DropCommand;
import com.ssomar.score.utils.NTools;
import com.ssomar.score.utils.SendMessage;
import com.ssomar.score.utils.StringConverter;
import com.ssomar.scoretestrecode.sobject.menu.NewSObjectsManagerEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CommandsClass implements CommandExecutor, TabExecutor {

    private ExecutableBlocks main;

    private static List<String> argumentsQuantity = new ArrayList<>();

    private static List<String> argumentsUsage = new ArrayList<>();

    public CommandsClass(ExecutableBlocks main) {
        this.main = main;

        argumentsQuantity.add("1");
        argumentsQuantity.add("3");
        argumentsQuantity.add("5");
        argumentsQuantity.add("10");
        argumentsQuantity.add("25");

        argumentsUsage.add("5");
        argumentsUsage.add("10");
        argumentsUsage.add("20");
        argumentsUsage.add("50");
        argumentsUsage.add("100");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {

            switch (args[0]) {
                case "reload":
                    this.runCommand(sender, "reload", args);
                    break;
                case "create":
                    this.runCommand(sender, "create", args);
                    break;
                case "show":
                    this.runCommand(sender, "show", args);
                    break;
                case "editor":
                    this.runCommand(sender, "editor", args);
                    break;
                case "give":
                    this.runCommand(sender, "give", args);
                    break;
                case "giveslot":
                    this.runCommand(sender, "giveslot", args);
                    break;
                case "drop":
                    this.runCommand(sender, "drop", args);
                    break;
                case "place":
                    this.runCommand(sender, "place", args);
                    break;
                case "we-place":
                    this.runCommand(sender, "we-place", args);
                    break;
                case "we-remove":
                    this.runCommand(sender, "we-remove", args);
                    break;
                case "giveall":
                    this.runCommand(sender, "giveall", args);
                    break;
                case "clear":
                    this.runCommand(sender, "clear", args);
                    break;
                case "actionbar":
                    this.runCommand(sender, "actionbar", args);
                    break;
                case "delete":
                    this.runCommand(sender, "delete", args);
                    break;
                case "checkevents":
                    this.runCommand(sender, "checkevents", args);
                    break;
                default:
                    sender.sendMessage(StringConverter.coloredString("&4" + ExecutableBlocks.NAME_2 + " &cInvalid argument /eb [ reload | give | giveslot | giveall | show | editor | actionbar | create | drop | place | clear ]"));
                    break;
            }
        } else {
            sender.sendMessage(StringConverter.coloredString("&4" + ExecutableBlocks.NAME_2 + " &cInvalid argument /eb [ reload | give | giveslot | giveall | show | editor | actionbar | create | drop | place | clear | delete ]"));
        }
        return true;
    }

    public void runCommand(CommandSender sender, String command, String[] fullArgs) {

        String[] args;
        if (fullArgs.length > 1) {
            args = new String[fullArgs.length - 1];
            for (int i = 0; i < fullArgs.length; i++) {
                if (i == 0) continue;
                else args[i - 1] = fullArgs[i];
            }
        } else args = new String[0];
        Player player = null;
        if ((sender instanceof Player)) {
            player = (Player) sender;
            if (!(player.hasPermission("eb.cmd." + command) || player.hasPermission("eb.cmds") || player.hasPermission("eb.*"))) {
                player.sendMessage(StringConverter.coloredString("&4" + ExecutableBlocks.NAME_2 + " &cYou don't have the permission to execute this command: " + "&6eb.cmd." + command));
                return;
            }
        }

        switch (command) {

            case "reload":
                main.onReload(true);
                sender.sendMessage(ChatColor.GREEN + ExecutableBlocks.NAME + " has been reload");
                ExecutableBlocks.plugin.getLogger().info(ExecutableBlocks.NAME_2 + " Successfully reload !");
                break;
            case "checkevents":
                OptimizedEventsHandler.getInstance().displayOptimisation();
                if (player != null) {
                    player.sendMessage(ChatColor.GREEN + "Check events sended in your console !");
                }
                break;
            case "create":
                if (player != null) {
                    if (args.length == 1) {
                        if (NewExecutableBlockLoader.getInstance().getAllObjects().contains(args[0])) {
                            player.sendMessage(StringConverter.coloredString("&4" + ExecutableBlocks.NAME_2 + " &cError this id already exist re-enter /eb create ID (ID is the id you want for your new block)"));
                        } else {
                            if (NewExecutableBlocksManager.getInstance().getLoadedObjects().size() >= 15 && PlaceholderAPI.isLotOfWork()) {
                                player.sendMessage(StringConverter.coloredString("&4" + ExecutableBlocks.NAME_2 + " &cREQUIRE PREMIUM: to add more than 15 blocks you need the premium version"));
                                return;
                            }

                            NewExecutableBlock eB = ItemStackToExecutableBlockConverter.convert(player.getInventory().getItemInMainHand(), args[0], "plugins/ExecutableBlocks/blocks/" + args[0] + ".yml");
                            eB.setId(args[0]);
                            eB.setPath("plugins/ExecutableBlocks/blocks/" + args[0] + ".yml");
                            eB.save();
                            NewExecutableBlocksManager.getInstance().addLoadedObject(eB);
                            eB.openEditor(player);
                        }
                    } else {
                        player.sendMessage(StringConverter.coloredString("&2" + ExecutableBlocks.NAME_2 + " &aTo create a new block you need to enter &e/eb create ID &a(ID is the id you want for your new block)"));
                    }
                }
                break;
            case "we-place":
                if (player != null) {
                    PlaceCommand.sendPlace(player, args);
                } else
                    SendMessage.sendMessageNoPlch(sender, "&4[ExecutableBlocks] &cERROR this command must be executed by a player");
                break;
            case "we-remove":
                if (player != null) {
                    RemoveCommand.sendRemove(player, args);
                } else
                    SendMessage.sendMessageNoPlch(sender, "&4[ExecutableBlocks] &cERROR this command must be executed by a player");
                break;

            case "show":
            case "editor":
                if (player != null) {
                    NewSObjectsManagerEditor.getInstance().startEditing(player, new NewExecutableBlocksEditor());
                }
                break;

            case "give":
                new GiveCommand(sender, args, false);
                break;

            case "drop":
                new DropCommand(ExecutableBlocks.getPluginSt(), sender, args);
                break;

            case "giveall":
                new GiveCommand(sender, args, true);
                break;

            case "clear":
                ClearCommand.clearCmd(ExecutableBlocks.plugin, sender, args);
                break;
            case "actionbar":
                if (sender instanceof Player) {
                    if (args.length == 1) {
                        ActionbarCommands.manageCommand((Player) sender, args[0]);
                    } else
                        sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &c/eb actionbar on or /eb actionbar off"));
                } else
                    sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &cThis is only an Ingame command !"));
                break;
            case "delete":
                if (args.length == 2) {
                    if (!args[1].equalsIgnoreCase("confirm")) {
                        sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &cTo confirm the delete type &6/eb delete {blockID} confirm"));
                        return;
                    }
                    Optional<NewExecutableBlock> eiOpt = NewExecutableBlocksManager.getInstance().getLoadedObjectWithID(args[0]);
                    if (eiOpt.isPresent()) {
                        NewExecutableBlocksManager.getInstance().deleteObject(args[0]);
                        sender.sendMessage(StringConverter.coloredString("&&2[ExecutableBlocks] &aItem &e" + args[0] + " &adeleted"));
                        if (player != null)
                            NewSObjectsManagerEditor.getInstance().startEditing(player, new NewExecutableBlocksEditor());
                    } else
                        sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &cItem &6" + args[0] + " &cnot found"));
                } else
                    sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &cTo confirm the delete type &6/eb delete {blockID} confirm"));
                break;
            case "place":
                if (args.length == 5) {

                    NewExecutableBlock executableBlock;
                    double x;
                    double y;
                    double z;
                    World world;

                    Optional<NewExecutableBlock> oOpt = NewExecutableBlocksManager.getInstance().getLoadedObjectWithID(args[0]);
                    if (!oOpt.isPresent()) {
                        sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &cInvalid block id: &6" + args[0] + " &7&o/eb place {EB_ID} {x} {y} {z} {world}"));
                        return;
                    } else executableBlock = oOpt.get();

                    if (!NTools.isNumber(args[1])) {
                        sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &cInvalid X position: &6" + args[1] + " &7&o/eb place {EB_ID} {x} {y} {z} {world}"));
                        return;
                    } else x = Double.valueOf(args[1]);

                    if (!NTools.isNumber(args[2])) {
                        sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &cInvalid Y position: &6" + args[2] + " &7&o/eb place {EB_ID} {x} {y} {z} {world}"));
                        return;
                    } else y = Double.valueOf(args[2]);

                    if (!NTools.isNumber(args[3])) {
                        sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &cInvalid Z position: &6" + args[3] + " &7&o/eb place {EB_ID} {x} {y} {z} {world}"));
                        return;
                    } else z = Double.valueOf(args[3]);

                    if (Bukkit.getServer().getWorld(args[4]) == null) {
                        sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &cInvalid world: &6" + args[4] + " &7&o/eb place {EB_ID} {x} {y} {z} {world}"));
                        return;
                    } else world = Bukkit.getServer().getWorld(args[4]);

                    Location loc = new Location(world, (int) x, (int) y, (int) z);

                    executableBlock.place(Optional.empty(), loc, true);
                } else
                    sender.sendMessage(StringConverter.coloredString("&4[ExecutableBlocks] &cError not enough arguments : &6/eb place {EB_ID} {x} {y} {z} {world} "));
                break;
            default:
                break;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("eb")) {
            ArrayList<String> arguments = new ArrayList<String>();
            if (args.length == 1) {

                arguments.add("reload");
                arguments.add("give");
                arguments.add("giveslot");
                arguments.add("giveall");
                arguments.add("show");
                arguments.add("editor");
                arguments.add("actionbar");
                arguments.add("create");
                arguments.add("drop");
                arguments.add("place");
                arguments.add("we-place");
                arguments.add("we-remove");
                arguments.add("clear");

                List<String> argumentsPerm = new ArrayList<String>();
                for (String str : arguments) {
                    if (sender.hasPermission("eb.cmd." + command) || sender.hasPermission("eb.cmds") || sender.hasPermission("eb.*")) {
                        argumentsPerm.add(str);
                    }
                }

                Collections.sort(argumentsPerm);
                return argumentsPerm;
            } else if (args.length >= 2) {

                switch (args[0]) {
                    case "giveall":
                        for (NewExecutableBlock eb : NewExecutableBlocksManager.getInstance().getLoadedObjects()) {
                            arguments.add(eb.getId());
                        }
                        Collections.sort(arguments);
                        return arguments;

                    case "give":
                        if (args.length == 3) {
                            return getLoadedBlocksWith(args[2]);
                        } else if (args.length == 4) {
                            arguments.addAll(argumentsQuantity);

                            return arguments;
                        }
                        break;
                    case "giveslot":
                        if (args.length == 3) {
                            return getLoadedBlocksWith(args[2]);
                        } else if (args.length == 4) {
                            arguments.addAll(argumentsQuantity);

                            return arguments;
                        } else if (args.length == 5) {
                            arguments.add("0");
                            arguments.add("1");
                            arguments.add("2");
                            arguments.add("3");
                            arguments.add("4");
                            arguments.add("5");
                            arguments.add("6");
                            arguments.add("7");
                            arguments.add("8");

                            return arguments;
                        }
                        break;
                    case "actionbar":
                        if (args.length == 2) {
                            arguments.add("on");
                            arguments.add("off");

                            return arguments;
                        }
                        break;
                    case "drop":
                        if (args.length == 2) {
                            return getLoadedBlocksWith(args[1]);
                        } else if (args.length == 3) {
                            arguments.addAll(argumentsQuantity);

                            return arguments;
                        } else if (args.length == 4) {
                            for (World world : Bukkit.getServer().getWorlds()) {
                                arguments.add(world.getName());
                            }

                            return arguments;
                        } else if (args.length == 5) {
                            arguments.add("X");

                            return arguments;
                        } else if (args.length == 6) {
                            arguments.add("Y");

                            return arguments;
                        } else if (args.length == 7) {
                            arguments.add("Z");

                            return arguments;
                        }
                        break;
                    case "place":
                        if (args.length == 2) {
                            return getLoadedBlocksWith(args[1]);
                        } else if (args.length == 6) {
                            for (World world : Bukkit.getServer().getWorlds()) {
                                arguments.add(world.getName());
                            }

                            return arguments;
                        }
                        break;
                    case "we-place":
                        if (args.length == 2) {
                            return getLoadedBlocksWith(args[1]);
                        }
                        break;
                    case "we-remove":
                        if (args.length == 2) {
                            arguments.add("true");
                            arguments.add("false");

                            return arguments;
                        }
                        break;
                    case "delete":
                        if (args.length == 2) {
                            return getLoadedBlocksWith(args[1]);
                        } else if (args.length == 3) {
                            arguments.add("confirm");

                            return arguments;
                        }
                    default:
                        break;
                }
            }
        }
        return null;
    }

    public List<String> getLoadedBlocksWith(String str) {
        List<String> arguments = new ArrayList<>();

        if (!str.isEmpty()) {
            for (NewExecutableBlock eb : NewExecutableBlocksManager.getInstance().getLoadedObjects()) {
                if (eb.getId().toUpperCase().contains(str.toUpperCase())) arguments.add(eb.getId());
            }
        } else {
            for (NewExecutableBlock eb : NewExecutableBlocksManager.getInstance().getLoadedObjects()) {
                arguments.add(eb.getId());
            }
        }

        return arguments;
    }

}

