package de.magiczerda.xmas.commands;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.StructureGrowEvent;

import de.magiczerda.xmas.database.DBManager;
import de.magiczerda.xmas.utils.Items;
import de.magiczerda.xmas.utils.PlayerNotFoundException;
import de.magiczerda.xmas.utils.Sapling;
import de.magiczerda.xmas.utils.UUIDNameConverter;
import net.md_5.bungee.api.ChatColor;

public class SaplingManager implements Listener {
	
	DBManager database;
	
	public SaplingManager(DBManager database) {
		this.database = database;
	}
	
	public void distribute(Player p) {
		if(p != null)
			p.sendMessage(ChatColor.GREEN + "[X-mas] all players will now get a christmas- tree sapling when they join the server (rejoining works of course)");
		database.createTables();
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent e) {
		if(!database.doesTableExist())
			return;
		
		Player p = e.getPlayer();
		try {
			
			UUID uuid = UUIDNameConverter.uuid(p.getName());	//for cracked servers
			if(database.hasSaplingItem(uuid))
				return;
			
			p.sendMessage(ChatColor.AQUA + "You have been given a sapling. Plant it anywhere you like, but make sure, there is some space around it. Do not loose it as there is no possibility to get another one. Also, you won't be able to break the sapling until it grows.");
			p.getInventory().addItem(Items.sapling());
			
			database.addPlayer(uuid);
			
		} catch (PlayerNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	@EventHandler
	public void onSaplingPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(!(p.getInventory().getItemInMainHand().equals(Items.sapling()) || p.getInventory().getItemInOffHand().equals(Items.sapling()) )) return;
		
		try {
			UUID uuid = UUIDNameConverter.uuid(p.getName());	//for cracked servers
			Sapling sapling = new Sapling(uuid, e.getBlockPlaced().getLocation());
			database.addSapling(sapling);
			
			p.getInventory().removeItem(Items.sapling());
			p.sendMessage(ChatColor.GREEN + "Congratulations! You have a christmas- tree now! This sapling will grow into a tree on christmas!");
			
		} catch (PlayerNotFoundException e1) {
			e1.printStackTrace();
		}
		
	}
	
	@EventHandler
	public void onSaplingBreak(BlockBreakEvent e)  {
		if(!e.getBlock().getType().equals(Material.SPRUCE_SAPLING))
			return;
		
		Player p = e.getPlayer();
		Block b = e.getBlock();
		
		if(!database.isSapling(b.getLocation()))
			return;
		
		e.setCancelled(true);
		p.sendMessage(ChatColor.RED + "You can't break a christmas- tree sapling!");
	}
	
	@EventHandler
	public void onSaplingGrowEvent(StructureGrowEvent e) {
		if(!e.getSpecies().equals(TreeType.REDWOOD))
			return;
		
		if(!database.isSapling(e.getLocation()))
			return;
		
		e.setCancelled(true);
	}
	
}
