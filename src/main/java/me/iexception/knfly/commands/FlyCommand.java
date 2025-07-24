package me.iexception.knfly.commands;

import com.mojang.brigadier.Message;
import me.iexception.knfly.Core;
import me.iexception.knfly.managers.FlightManager;
import me.iexception.knfly.managers.FlyTimeManager;
import me.iexception.knfly.managers.UserManager;
import me.iexception.knfly.managers.user.User;
import me.iexception.knfly.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlyCommand implements CommandExecutor {

    private final FlightManager flightManager = new FlightManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.getInstance().getMessage("not-player"));
                return true;
            }
            Player player = (Player) sender;
            User user = UserManager.getInstance().getUser(player.getUniqueId());
            if (FlightManager.flightEnabled.contains(player.getUniqueId())) {

                if (FlyTimeManager.getInstance().hasTimer(player.getUniqueId())) {
                    FlyTimeManager.getInstance().stopTimer(player.getUniqueId(), true);
                    return true;
                }

                flightManager.disableFlight(player.getUniqueId(), false);
                return true;
            }

            if (user.getTime() <= 0 && !player.hasPermission("knfly.bypass") && !player.hasPermission("knfly.admin")) {
                player.sendMessage(MessageUtils.getInstance().getMessage("no-fly-time"));
                return true;
            }
            flightManager.enableFlight(player.getUniqueId(), true);
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "info":
                    if (args.length == 1) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(MessageUtils.getInstance().getMessage("fly-info-console"));
                            return true;
                        }
                        Player player = (Player) sender;
                        User user = UserManager.getInstance().getUser(player.getUniqueId());
                        Integer hours = (user.getTime() / 3600);
                        Integer minutes = ((user.getTime() - (hours * 3600)) / 60);

                        sender.sendMessage(MessageUtils.getInstance().getMessage("fly-time")
                                .replaceAll("%hours%", String.valueOf(hours))
                                .replaceAll("%minutes%", String.valueOf(minutes))
                                .replaceAll("%seconds%", String.valueOf(user.getTime() - (minutes * 60) - (hours * 3600))));
                        break;
                    }

                    if (!sender.hasPermission("knfly.info.others") && !sender.hasPermission("knfly.admin")) {
                        sender.sendMessage(MessageUtils.getInstance().getMessage("no-permissions"));
                        Core.getInstance().getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("no-permissions-log")
                                .replaceAll("%command%", "/knfly info <other>")
                                .replaceAll("%player%", sender.getName()));
                        return true;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(MessageUtils.getInstance().getMessage("player-not-online").replaceAll("%target%", args[1]));
                        break;
                    }

                    User user = UserManager.getInstance().getUser(target.getUniqueId());
                    Integer hours = (user.getTime() / 3600);
                    Integer minutes = ((user.getTime() - (hours * 3600)) / 60);

                    sender.sendMessage(MessageUtils.getInstance().getMessage("player-fly-time")
                            .replaceAll("%target%", target.getName())
                            .replaceAll("%hours%", String.valueOf(hours))
                            .replaceAll("%minutes%", String.valueOf(minutes))
                            .replaceAll("%seconds%", String.valueOf(user.getTime() - (minutes * 60) - (hours * 3600))));
                    break;
                case "reload":
                    if (!sender.hasPermission("knfly.reload")) {
                        sender.sendMessage(MessageUtils.getInstance().getMessage("no-permissions"));
                        Core.getInstance().getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("no-permissions-log")
                                .replaceAll("%command%", "/knfly reload")
                                .replaceAll("%player%", sender.getName()));
                        break;
                    }
                    flightManager.reloadMessages(sender);
                    break;
                case "time":
                    if (!sender.hasPermission("knfly.time.add") && !sender.hasPermission("knfly.time.remove") && !sender.hasPermission("knfly.admin")) {
                        sender.sendMessage(MessageUtils.getInstance().getMessage("no-permissions"));
                        Core.getInstance().getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("no-permissions-log")
                                .replaceAll("%command%", "/knfly time")
                                .replaceAll("%player%", sender.getName()));
                        break;
                    }
                    if (args.length == 2) {
                        Player targetTime = Bukkit.getPlayer(args[1]);
                        if (targetTime == null) {
                            sender.sendMessage(MessageUtils.getInstance().getMessage("player-not-found").replaceAll("%target%", args[1]));
                            break;
                        }
                    }
                    switch (args.length) {
                        default:
                        case 1:
                            sender.sendMessage(MessageUtils.getInstance().getMessage("command-time-usage"));
                            break;
                        case 2:
                            sender.sendMessage(MessageUtils.getInstance().getMessage("command-time-addremove"));
                            break;
                        case 3:
                            sender.sendMessage(MessageUtils.getInstance().getMessage("command-time-amount"));
                            break;
                        case 4:
                            Player targetTime = Bukkit.getPlayer(args[1]);
                            if (targetTime == null) {
                                sender.sendMessage(MessageUtils.getInstance().getMessage("player-not-found").replaceAll("%target%", args[1]));
                                break;
                            }
                            User TUser = UserManager.getInstance().getUser(targetTime.getUniqueId());
                            try {
                                int amount = Integer.parseInt(args[3]) * 60;
                                if (args[2].equals("add")) {
                                    if (!sender.hasPermission("knfly.time.add") && !sender.hasPermission("knfly.admin")) {
                                        sender.sendMessage(MessageUtils.getInstance().getMessage("no-permissions"));
                                        Core.getInstance().getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("no-permissions-log")
                                                .replaceAll("%command%", "/knfly time <other> add")
                                                .replaceAll("%player%", sender.getName()));
                                        break;
                                    }

                                    UserManager.getInstance().addTime(TUser, sender, amount);
                                    break;
                                } else if (args[2].equalsIgnoreCase("remove")) {
                                    if (!sender.hasPermission("knfly.time.remove") && !sender.hasPermission("knfly.admin")) {
                                        sender.sendMessage(MessageUtils.getInstance().getMessage("no-permissions"));
                                        Core.getInstance().getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("no-permissions-log")
                                                .replaceAll("%command%", "/knfly time <other> remove")
                                                .replaceAll("%player%", sender.getName()));
                                        break;
                                    }

                                    UserManager.getInstance().removeTime(TUser, sender, amount);
                                    break;
                                } else {
                                    sender.sendMessage(MessageUtils.getInstance().getMessage("command-time-addremove"));
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(MessageUtils.getInstance().getMessage("not-valid-amount"));
                            }


                            break;
                    }
                    break;
                case "save":
                    if (!sender.hasPermission("knfly.reload") && !sender.hasPermission("knfly.admin")) {
                        sender.sendMessage(MessageUtils.getInstance().getMessage("no-permissions"));
                        Core.getInstance().getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("no-permissions-log")
                                .replaceAll("%command%", "/knfly save")
                                .replaceAll("%player%", sender.getName()));
                        break;
                    }

                    UserManager.getInstance().saveUsers();

                    break;
                default:
                    if (args.length == 1) {
                        if (!sender.hasPermission("knfly.others") || !sender.hasPermission("knfly.admin")) {
                            sender.sendMessage(MessageUtils.getInstance().getMessage("no-permissions"));
                            Core.getInstance().getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("no-permissions-log")
                                    .replaceAll("%command%", "/knfly <player>")
                                    .replaceAll("%player%", sender.getName()));

                            return true;
                        }
                        target = Bukkit.getServer().getPlayer(args[0]);
                        if (target == null) {
                            sender.sendMessage(MessageUtils.getInstance().getMessage("player-not-online").replaceAll("%target%", args[0]));
                            return true;
                        }
                        if (FlightManager.flightEnabled.contains(target.getUniqueId())) {
                            flightManager.disableFlight(target.getUniqueId(), false);
                            sender.sendMessage(MessageUtils.getInstance().getMessage("flight-disabled-staff").replaceAll("%target%", target.getName()));

                            return true;
                        }
                        flightManager.enableFlight(target.getUniqueId(), false);
                        sender.sendMessage(MessageUtils.getInstance().getMessage("flight-enabled-staff").replaceAll("%target%", target.getName()));

                    }
            }
        }
        return true;
    }
}
