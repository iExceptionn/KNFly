package me.iexception.knfly.utils;

import java.util.HashMap;

public class MessageUtils {

    private static MessageUtils messageUtils;

    public MessageUtils() {
        messageUtils = this;
    }

    public final HashMap<String, String> messages = new HashMap<>();

    public void loadMessages() {

        for(String key : FileManager.get("messages.yml").getConfigurationSection("messages").getKeys(false)){
            String string = FileManager.get("messages.yml").getString("messages." + key);
            messages.put(key, string);
        }

        for(String key : FileManager.get("messages.yml").getConfigurationSection("commands").getKeys(false)){
            String string = FileManager.get("messages.yml").getString("commands." + key);
            messages.put(key, string);
        }

        for(String key : FileManager.get("messages.yml").getConfigurationSection("systeem").getKeys(false)){
            String string = FileManager.get("messages.yml").getString("systeem." + key);
            messages.put(key, string);
        }
    }

    public void reloadMessages(){
        messages.clear();
        loadMessages();

    }

    public String getMessage(String string, String... args){

        for(String key : messages.keySet()){
            if(key.equalsIgnoreCase(string)){
                String cmessage = messages.get(key);
                String prefix = FileManager.get("messages.yml").getString("prefix");

                String message = cmessage
                        .replaceAll("%prefix%", prefix);

                return ChatUtils.format(message);
            }
        }
        return ChatUtils.format("&cmessage not found, contact Developer. &8[&7" + string + "&8]");
    }

    public static MessageUtils getInstance(){
        return messageUtils;
    }
}
