package me.iexception.knfly.managers;

import me.iexception.knfly.Core;
import me.iexception.knfly.managers.interfaces.iUserManager;
import me.iexception.knfly.managers.user.User;
import me.iexception.knfly.utils.FileManager;
import me.iexception.knfly.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class UserManager implements iUserManager {

    private final static ArrayList<User> loadedUsers = new ArrayList<>();
    private final static UserManager userManager = new UserManager();

    @Override
    public void createUser(UUID uuid) {
        User user = new User(Bukkit.getPlayer(uuid).getName(), uuid, FileManager.get("config.yml").getInt("settings.default-time"));
        loadedUsers.add(user);
    }

    @Override
    public void loadUser(UUID uuid) {

        if (getUser(uuid) == null) {
            createUser(uuid);
        }

    }

    @Override
    public void loadUsers() {

        for (String key : FileManager.get("playerdata.yml").getStringList("players")) {

            String[] data = key.split(";");

            UUID uuid = UUID.fromString(data[0]);
            String name = data[1];
            Integer flyTime = Integer.valueOf(data[2]);

            User user = new User(name, uuid, flyTime);
            loadedUsers.add(user);

        }

        Core.getInstance().getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("loaded").replaceAll("%count%", String.valueOf(loadedUsers.size())));

        autoSave();
    }

    @Override
    public void saveUsers() {

        FileManager.get("playerdata.yml").set("players", (""));

        ArrayList<String> players = new ArrayList<>();

        for (User user : loadedUsers) {

            if(Core.getInstance().getServer().isStopping()){
                FlyTimeManager.getInstance().stopTimer(user.getUuid(), true);
            }

            players.add(user.getUuid() + ";" + user.getName() + ";" + user.getTime());
        }

        FileManager.get("playerdata.yml").set("players", players);
        FileManager.save(Core.getInstance(), "playerdata.yml");


        Core.getInstance().getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("saved").replaceAll("%count%", String.valueOf(loadedUsers.size())));

    }

    public void autoSave() {

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                saveUsers();

                autoSave();
            }
        };
        // elke 30min.
        task.runTaskLater(Core.getInstance(), 20 * 1800);

    }

    @Override
    public User getUser(UUID uuid) {

        for (User user : loadedUsers) {
            if (user.getUuid().equals(uuid)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void addTime(User user, CommandSender sender, Integer time) {
        user.setTime(user.getTime() + time);

        Player player = Bukkit.getPlayer(user.getUuid());
        player.sendMessage(MessageUtils.getInstance().getMessage("flytime-added")
                .replaceAll("%amount%", String.valueOf(time / 60)));

        sender.sendMessage(MessageUtils.getInstance().getMessage("flytime-added-staff")
                .replaceAll("%amount%", String.valueOf(time / 60))
                .replaceAll("%target%", user.getName()));

        loadedUsers.remove(user);
        loadedUsers.add(new User(user.getName(), user.getUuid(), user.getTime()));
        if(FlyTimeManager.getInstance().hasTimer(user.getUuid())){
            FlyTimeManager.getInstance().flyTimer.replace(user.getUuid(), FlyTimeManager.getInstance().getFlyTimer(user.getUuid()), (FlyTimeManager.getInstance().getFlyTimer(user.getUuid()) + time));
        }
    }

    @Override
    public void removeTime(User user, CommandSender sender, Integer time) {
        user.setTime(user.getTime() - time);
        if (user.getTime() < 0) {
            user.setTime(0);

            if(FlyTimeManager.getInstance().hasTimer(user.getUuid())){
                FlyTimeManager.getInstance().stopTimer(user.getUuid(), false);
            }

        }
        Player player = Bukkit.getPlayer(user.getUuid());
        player.sendMessage(MessageUtils.getInstance().getMessage("flytime-removed")
                .replaceAll("%amount%", String.valueOf(time / 60)));

        sender.sendMessage(MessageUtils.getInstance().getMessage("flytime-removed-staff")
                .replaceAll("%amount%", String.valueOf(time / 60))
                .replaceAll("%target%", user.getName()));

        loadedUsers.remove(user);
        loadedUsers.add(new User(user.getName(), user.getUuid(), user.getTime()));

    }

    public static UserManager getInstance() {
        return userManager;
    }
}
