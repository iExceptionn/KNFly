package me.iexception.knfly.managers.interfaces;

import me.iexception.knfly.managers.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface iUserManager {

    void createUser(UUID uuid);
    void loadUser(UUID uuid);
    void loadUsers();
    void saveUsers();
    User getUser(UUID uuid);
    void addTime(User user, CommandSender sender, Integer time);
    void removeTime(User user, CommandSender sender, Integer time);
}
