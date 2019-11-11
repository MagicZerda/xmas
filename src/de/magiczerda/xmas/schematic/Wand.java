package de.magiczerda.xmas.schematic;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.magiczerda.xmas.utils.Items;
import net.md_5.bungee.api.ChatColor;

public class Wand implements Listener {
	
	/**
	 * There are only two Locations
	 * -> only one player should have the xwand at one time (to avoid problems)
	 */
	
	private Location pos1;
	private Location pos2;
	
	public Wand() {
		
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(!p.hasPermission("xmas.administrative"))
			return;
		if(!(p.getInventory().getItemInMainHand().equals(Items.wand()) || p.getInventory().getItemInOffHand().equals(Items.wand()) ))
			return;
		
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			pos2 = e.getClickedBlock().getLocation();
			p.sendMessage(ChatColor.GREEN + "[X-mas] You have set position 1");
		} if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			pos1 = e.getClickedBlock().getLocation();
			p.sendMessage(ChatColor.GREEN + "[X-mas] You have set position 2");
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(!(p.getInventory().getItemInMainHand().equals(Items.wand()) || p.getInventory().getItemInOffHand().equals(Items.wand()) ))
			return;
		if(!p.hasPermission("xmas.administrative"))
			return;
		
		e.setCancelled(true);
	}

	public Location getPos1() {
		return pos1;
	}
	
	public Location getPos2() {
		return pos2;
	}
	
}
