package me.iexception.knfly.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private final static Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");

    public static String format(String input){

        Matcher match = pattern.matcher(input);
        while (match.find()){
            String color = input.substring(match.start(), match.end());
            input = input.replace(color, ChatColor.of(color) + "");
            match = pattern.matcher(input);
        }

        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
