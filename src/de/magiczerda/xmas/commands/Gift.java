package de.magiczerda.xmas.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.magiczerda.xmas.database.DBManager;
import de.magiczerda.xmas.utils.PlayerNotFoundException;
import de.magiczerda.xmas.utils.Sapling;
import de.magiczerda.xmas.utils.UUIDNameConverter;
import net.md_5.bungee.api.ChatColor;

public class Gift implements Listener {
	
	DBManager database;
	private Map<Player, UUID> giftMap;
	
	public Gift(DBManager database) {
		this.database = database;
		giftMap = new HashMap<Player, UUID>();	//the first player is the player looking in the second player's presents inventory
	}
	
	public void openInv(Player player, UUID targetUUID) {
		if(!database.hasSaplingPlaced(targetUUID))
			return;
		
		try {
			Inventory inv;
			inv = Bukkit.createInventory(player, 27, "Christmas presents for " + UUIDNameConverter.name(targetUUID));	//24 gift slots per player
			ItemStack[] items = database.getItems(database.getSapling(targetUUID));
			if(items != null)
				for(ItemStack is : items)
					if(is != null)
						inv.addItem(is);
			
			player.openInventory(inv);
			
			giftMap.put(player, targetUUID);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (PlayerNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(!e.getView().getTitle().contains("Christmas presents for"))
			return;
		
		Player p = (Player) e.getPlayer();
		Sapling sapling = database.getSapling(giftMap.get(p));
		
		ItemStack[] prevContents = database.getItems(sapling);
		ItemStack[] contents = e.getInventory().getContents();
		List<ItemStack> addedItems = new LinkedList<ItemStack>(Arrays.asList(contents));
		
		for(ItemStack is : prevContents) {
			if(is == null)
				continue;
			addedItems.remove(is);
		}
		
		for(ItemStack is : addedItems) {
			if(is == null)
				continue;
			
			ItemMeta im = is.getItemMeta();
			im.setLore(Arrays.asList("A christmas present by", p.getName()));
			is.setItemMeta(im);
		}
		
		List<ItemStack> presents = new ArrayList<ItemStack>();
		presents.addAll(addedItems);
		for(ItemStack is : prevContents) {
			if(is == null)
				continue;
			presents.add(is);
		}
		
		boolean stolen = false;
		
		for(ItemStack is : p.getInventory().getContents()) {
			if(is == null)
				continue;
			
			ItemMeta im = is.getItemMeta();
			if(im.getLore() == null)
				continue;
			
			if(im.getLore().contains("A christmas present by")) {
				p.getInventory().removeItem(is);
				stolen = true;
			}
		}
		
		if(stolen)
			p.sendMessage(ChatColor.RED + "You shouldn't steal other people's presents! It's christmas!");
		
		stolen = false;
		
		ItemStack[] presentsArray = new ItemStack[presents.size()];
		for(int i = 0; i < presents.size(); i++)
			presentsArray[i] = presents.get(i);
		
		database.updateSapling(sapling, presentsArray);
		giftMap.remove(p);
	}
	
}
