package me.iexception.knfly.events;

import me.iexception.knfly.Core;
import me.iexception.knfly.managers.FlightManager;
import me.iexception.knfly.managers.FlyTimeManager;
import me.iexception.knfly.managers.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class playerEvents implements Listener {

    private ArrayList<UUID> leftInFly = new ArrayList<>();
    public static ArrayList<UUID> cancelFall = new ArrayList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UserManager.getInstance().loadUser(player.getUniqueId());

        if (leftInFly.contains(player.getUniqueId())) {
            FlightManager.getInstance().enableFlight(player.getUniqueId(), true);
            leftInFly.remove(player.getUniqueId());
        }

        if (!player.isOnGround()) {
            if (UserManager.getInstance().getUser(player.getUniqueId()).getTime() >= 1) {
                FlightManager.getInstance().enableFlight(player.getUniqueId(), true);
            } else if (player.hasPermission("knfly.bypass")) {
                FlightManager.getInstance().enableFlight(player.getUniqueId(), false);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (FlyTimeManager.getInstance().hasTimer(player.getUniqueId())) {
            FlyTimeManager.getInstance().stopTimer(player.getUniqueId(), true);
            leftInFly.add(player.getUniqueId());
        }

    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {

        if (!(event.getEntityType() == EntityType.PLAYER)) return;
        Player player = (Player) event.getEntity();

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && cancelFall.contains(player.getUniqueId())) {
            event.setCancelled(true);
            cancelFall.remove(player.getUniqueId());
        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (cancelFall.contains(player.getUniqueId())) {
            if (player.isOnGround()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {

                        cancelFall.remove(player.getUniqueId());
                        cancel();

                    }
                }.runTaskTimer(Core.getInstance().getInstance(), 0, 10);
            }
        }
    }

    @EventHandler
    public void onWorldChangeEvent(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer();

        if (FlightManager.flightEnabled.contains(player.getUniqueId())) {

            player.setAllowFlight(true);
            player.setFlying(true);

        }

    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {

        Player player = event.getPlayer();
        if (player.hasPermission("knfly.bypass") || player.hasPermission("knfly.bypass.staff")) {
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                    FlightManager.getInstance().enableFlight(player.getUniqueId(), false);
                }
            }, 1L);
        }
    }
}
