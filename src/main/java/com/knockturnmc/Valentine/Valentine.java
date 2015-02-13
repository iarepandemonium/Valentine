package com.knockturnmc.Valentine;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Valentine extends JavaPlugin{
	
	private Map<UUID,UUID> loves = new HashMap<UUID,UUID>();
	private FileConfiguration config;
	
	public void onEnable(){
		this.config = this.getConfig();
		
		ConfigurationSection locations = config.getConfigurationSection("Valentines");
		for(String path : locations.getKeys(false)){
			loves.put(UUID.fromString(path), UUID.fromString(config.getString(path)));
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			public void run() {
				for(Entry<UUID, UUID> e : loves.entrySet()){
					Player lover = Bukkit.getPlayer(e.getKey());
					Player loved = Bukkit.getPlayer(e.getValue());
					if(lover.isOnline() && loved.isOnline() && 
							lover.getLocation().distance(loved.getLocation()) < 5){
						lover.getLocation().getWorld().playEffect(lover.getLocation(), Effect.HEART, 1);
					}
				}
				
			}
			
		}, 0L, 600L);
	}
	
	public void onDisable(){
		for(Entry<UUID, UUID> e: loves.entrySet()){
			config.set("Valentines." + e.getKey().toString(), e.getValue());
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!cmd.getName().equalsIgnoreCase("love")){
			return false;
		}
		
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Console can't love anyone, it's only a computer.");
			return false;
		}
		
		if(args.length != 1){
			sender.sendMessage(ChatColor.RED + "Not enough arguments or too many.");
			return false;
		}
		
		Player lover = (Player) sender;
		try{
		loves.put(lover.getUniqueId(), Bukkit.getPlayer(args[0]).getUniqueId());}
		catch(Exception e){
			lover.sendMessage(ChatColor.RED + "Either that person isn't online or that isn't a person.");
		}
		lover.sendMessage(ChatColor.DARK_RED + args[0] + " has been added as your love.");
		return true;}

}
