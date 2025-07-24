package me.iexception.knfly.managers;

import me.iexception.knfly.Core;
import me.iexception.knfly.managers.interfaces.IFlyTimeManager;
import me.iexception.knfly.managers.user.User;
import me.iexception.knfly.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FlyTimeManager implements IFlyTimeManager {

    private final FlightManager flightManager = new FlightManager();

    public final HashMap<UUID, Integer> flyTimer = new HashMap<>();
    private final HashMap<UUID, BukkitRunnable> runnable = new HashMap<>();

    private static final FlyTimeManager flyTimeManager = new FlyTimeManager();

    public void startFlyTimer(UUID uuid, Integer time) {

        if (!hasTimer(uuid)) {
            flyTimer.put(uuid, time);
            runnable.put(uuid, new BukkitRunnable() {
                @Override
                public void run() {

                    if (hasTimer(uuid)) {
                        flyTimer.put(uuid, getFlyTimer(uuid) - 1);
                        UserManager.getInstance().getUser(uuid).setTime(getFlyTimer(uuid));
                    }

                    Integer hours = (FlyTimeManager.flyTimeManager.getFlyTimer(uuid) / 3600);
                    Integer minutes = ((FlyTimeManager.flyTimeManager.getFlyTimer(uuid) - (hours * 3600)) / 60);
                    Integer seconds = FlyTimeManager.flyTimeManager.getFlyTimer(uuid) - (minutes * 60) - (hours * 3600);

                    if (getFlyTimer(uuid) == 600 || getFlyTimer(uuid) == 300 || getFlyTimer(uuid) == 240 || getFlyTimer(uuid) == 180 || getFlyTimer(uuid) == 120 || getFlyTimer(uuid) == 60 || getFlyTimer(uuid) == 30) {
                        Bukkit.getPlayer(uuid).sendMessage(MessageUtils.getInstance().getMessage("fly-time-ending")
                                .replaceAll("%hours%", String.valueOf(hours))
                                .replaceAll("%seconds%", String.valueOf(seconds))
                                .replaceAll("%minutes%", String.valueOf(minutes)));
                    }

                    if (getFlyTimer(uuid) <= 10 && getFlyTimer(uuid) != 0) {
                        Bukkit.getPlayer(uuid).sendMessage(MessageUtils.getInstance().getMessage("fly-time-ending")
                                .replaceAll("%hours%", String.valueOf(hours))
                                .replaceAll("%seconds%", String.valueOf(seconds))
                                .replaceAll("%minutes%", String.valueOf(minutes)));
                    }

                    if (getFlyTimer(uuid) <= 0) {
                        stopTimer(uuid, true);
                    }

                }
            });
            runnable.get(uuid).runTaskTimer(Core.getInstance(), 20, 20);
        }

    }

    @Override
    public void stopTimer(UUID uuid, boolean changeTime) {
        if (hasTimer(uuid)) {

            if(changeTime){
                UserManager.getInstance().getUser(uuid).setTime(getFlyTimer(uuid));
            }

            runnable.get(uuid).cancel();
            runnable.remove(uuid);
            flyTimer.remove(uuid);

            flightManager.disableFlight(uuid, true);
        }

    }

    @Override
    public boolean hasTimer(UUID uuid) {
        return runnable.containsKey(uuid);
    }

    @Override
    public Integer getFlyTimer(UUID uuid) {
        if (hasTimer(uuid)) {
            return flyTimer.get(uuid);
        }
        return 0;
    }

    public static FlyTimeManager getInstance() {
        return flyTimeManager;
    }
}
