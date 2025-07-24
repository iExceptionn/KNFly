package me.iexception.knfly;

import me.iexception.knfly.commands.FlyCommand;
import me.iexception.knfly.events.playerEvents;
import me.iexception.knfly.managers.UserManager;
import me.iexception.knfly.utils.FileManager;
import me.iexception.knfly.utils.MessageUtils;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin implements Listener {

    private static Core instance;

    private final MessageUtils messageUtils = new MessageUtils();

    public static Core getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Config
        FileManager.load(this, "messages.yml");
        FileManager.load(this, "config.yml");
        FileManager.load(this, "playerdata.yml");
        instance = this;

        // Loading
        messageUtils.loadMessages();
        LoadCommands();
        LoadEvents();
        UserManager.getInstance().loadUsers();

        // Plugin startup logic
        this.getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("enable"));
    }

    @Override
    public void onDisable() {
        this.getServer().getConsoleSender().sendMessage(MessageUtils.getInstance().getMessage("disable"));

        UserManager.getInstance().saveUsers();
    }

    private void LoadCommands(){
        getCommand("fly").setExecutor(new FlyCommand());
    }

    private void LoadEvents(){

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new playerEvents(), this);

    }
}
