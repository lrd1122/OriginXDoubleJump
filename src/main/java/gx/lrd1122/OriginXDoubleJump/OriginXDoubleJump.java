package gx.lrd1122.OriginXDoubleJump;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;


public final class OriginXDoubleJump extends JavaPlugin implements Listener {

    private final HashMap<Player, Boolean> cooldown = new HashMap<>();
    private final HashMap<Player, Integer> playerJumps = new HashMap<>();
    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginCommand("OriginXDoubleJump").setExecutor(this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(getConfig().getStringList("EnableWorlds").contains(player.getWorld().getName())) {
            if(!player.isOp() && player.hasPermission("oxdj.maxjump." + (getConfig().getInt("MaxJump") + 1))){
                return;
            }
            if(!playerJumps.containsKey(player)){
                playerJumps.put(player, 0);
            }
            if (player.isOnGround())
                playerJumps.put(player, 0);
            if (playerJumps.get(player) < getConfig().getInt("MaxJump")) {
                if (!player.isOnGround() && player.getGameMode().equals(GameMode.SURVIVAL)
                && !player.isFlying()) {
                    player.setAllowFlight(true);
                }
            }
        }
    }
    @EventHandler
    public void onFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (getConfig().getStringList("EnableWorlds").contains(player.getWorld().getName())) {
            if (!playerJumps.containsKey(player))
                playerJumps.put(player, 0);
            int jump = playerJumps.get(player);
            if (!player.isOp() && player.hasPermission("oxdj.maxjump." + (jump + 1))) {
                return;
            }
            if (player.getGameMode().equals(GameMode.SURVIVAL)
                    || player.getGameMode().equals(GameMode.ADVENTURE)) {
            if (jump < getConfig().getInt("MaxJump")) {
                player.setAllowFlight(false);
                event.setCancelled(true);
                if(!cooldown.containsKey(player)) {

                        jump++;
                        playerJumps.put(player, jump);
                        Location location = player.getLocation().clone();
                        Vector vector = location.getDirection();
                        vector.setY(1);
                        double y = vector.getY();
                        vector.multiply(getConfig().getDouble("Forward"));
                        vector.setY(y * getConfig().getDouble("Up"));
                        player.setVelocity(vector);
                        player.playSound(player.getLocation(), Sound.valueOf(getConfig().getString("Sound").toUpperCase()), 100, 0);
                        cooldown.put(player, true);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                cooldown.remove(player);
                                this.cancel();
                            }
                        }.runTaskLater(this, getConfig().getInt("Cooldown"));
                    }
                }
            }
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        reloadConfig();
        sender.sendMessage("重载成功");
        return true;
    }
}
