package me.iexception.knfly.managers;

import me.iexception.knfly.Core;
import me.iexception.knfly.commands.FlyCommand;
import me.iexception.knfly.events.playerEvents;
import me.iexception.knfly.managers.interfaces.IFlightManager;
import me.iexception.knfly.managers.user.User;
import me.iexception.knfly.utils.FileManager;
import me.iexception.knfly.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class FlightManager implements IFlightManager {

    public static ArrayList<UUID> flightEnabled = new ArrayList<>();
    private final static FlightManager flightManager = new FlightManager();

    @Override
    public void enableFlight(UUID uuid, boolean useTime) {
        Player player = Bukkit.getPlayer(uuid);
        player.setAllowFlight(true);
        player.setFlying(true);

        flightEnabled.add(uuid);

        if(useTime && !player.hasPermission("knfly.bypass") && !player.hasPermission("knfly.admin")){

            User user = UserManager.getInstance().getUser(uuid);
            Integer hours = (user.getTime() / 3600);
            Integer minutes = ((user.getTime() - (hours * 3600)) / 60);

            player.sendMessage(MessageUtils.getInstance().getMessage("flight-enabled-time")
                    .replaceAll("%hours%", String.valueOf(hours))
                    .replaceAll("%minutes%", String.valueOf(minutes))
                    .replaceAll("%seconds%", String.valueOf(user.getTime() - (minutes * 60) - (hours * 3600))));
            FlyTimeManager.getInstance().startFlyTimer(uuid, user.getTime());
            return;
        }
        player.sendMessage(MessageUtils.getInstance().getMessage("flight-enabled"));
    }

    @Override
    public void disableFlight(UUID uuid, boolean useTime) {
        Player player = Bukkit.getPlayer(uuid);

        player.setFlying(false);
        player.setAllowFlight(false);

        flightEnabled.remove(uuid);
        playerEvents.cancelFall.add(player.getUniqueId());
        if(useTime && !player.hasPermission("knfly.bypass") && !player.hasPermission("knfly.admin")){

            User user = UserManager.getInstance().getUser(uuid);
            Integer hours = (user.getTime() / 3600);
            Integer minutes = ((user.getTime() - (hours * 3600)) / 60);

            player.sendMessage(MessageUtils.getInstance().getMessage("flight-disabled-time")
                    .replaceAll("%hours%", String.valueOf(hours))
                    .replaceAll("%minutes%", String.valueOf(minutes))
                    .replaceAll("%seconds%", String.valueOf(user.getTime() - (minutes * 60) - (hours * 3600))));
            return;
        }

        player.sendMessage(MessageUtils.getInstance().getMessage("flight-disabled"));
    }

    @Override
    public void reloadMessages(CommandSender sender) {
        FileManager.reload(Core.getInstance(), "messages.yml");
        MessageUtils.getInstance().reloadMessages();
        sender.sendMessage(MessageUtils.getInstance().getMessage("reload-flight-messages"));
    }

    public static FlightManager getInstance(){
        return flightManager;
    }
}
