package com.knockturnmc.Valentine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class Valentine extends JavaPlugin {

  private Map<UUID, UUID> loves = new HashMap<>();
  private FileConfiguration config;

  public void onEnable() {
    this.config = this.getConfig();

    final ConfigurationSection locations = config.getConfigurationSection("Valentines");
    if (locations != null && config != null) {
      for (String path : locations.getKeys(false)) {
        String love = config.getString(path);
        if (love == null) continue;

        loves.put(UUID.fromString(path), UUID.fromString(love));
      }
    }

    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
      for (Entry<UUID, UUID> e : loves.entrySet()) {
        Player lover = Bukkit.getPlayer(e.getKey());
        Player loved = Bukkit.getPlayer(e.getValue());
        if (lover == null || loved == null) continue;

        Location loverL = lover.getLocation();
        Location lovedL = loved.getLocation();

        if (lover.isOnline() && loved.isOnline() && loverL.distance(lovedL) < 5) {
          World world = loverL.getWorld();
          if (world == null) continue;

          world.spawnParticle(Particle.HEART, loverL, 1);
        }
      }

    }, 0L, 600L);
  }

  public void onDisable() {
    for (Entry<UUID, UUID> e : loves.entrySet()) {
      config.set("Valentines." + e.getKey().toString(), e.getValue());
    }
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!cmd.getName().equalsIgnoreCase("love")) {
      return false;
    }

    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "Console can't love anyone, it's only a computer.");
      return false;
    }

    if (args.length != 1) {
      sender.sendMessage(ChatColor.RED + "Not enough arguments or too many.");
      return false;
    }

    Player lover = (Player) sender;

    Player loved = Bukkit.getPlayer(args[0]);
    if (loved == null) {
      lover.sendMessage(ChatColor.RED + "Either that person isn't online or that isn't a person.");
      return false;
    }
    loves.put(lover.getUniqueId(), loved.getUniqueId());


    lover.sendMessage(ChatColor.DARK_RED + args[0] + " has been added as your love.");
    return true;
  }

}
