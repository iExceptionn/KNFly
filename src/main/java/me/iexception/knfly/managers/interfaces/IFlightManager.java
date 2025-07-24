package me.iexception.knfly.managers.interfaces;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IFlightManager {

    void enableFlight(UUID uuid, boolean useTime);
    void disableFlight(UUID uuid, boolean useTime);
    void reloadMessages(CommandSender sender);
}
