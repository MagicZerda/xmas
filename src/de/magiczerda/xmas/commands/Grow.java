package de.magiczerda.xmas.commands;

import java.util.List;

import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.magiczerda.xmas.database.DBManager;
import de.magiczerda.xmas.schematic.SchematicManager;
import de.magiczerda.xmas.utils.Sapling;

public class Grow {
	
	DBManager database;
	SchematicManager schematicManager;
	
	public Grow(DBManager database, SchematicManager schematicManager) {
		this.database = database;
		this.schematicManager = schematicManager;
	}
	
	public void grow() {
		List<Sapling> saplingLocations = database.getAllSaplingLocations();
		schematicManager.load();
		
		for(Sapling s : saplingLocations) {
			schematicManager.paste(s.getLocation());
			Chest c = (Chest) s.getLocation().getBlock().getState();
			Inventory cI = c.getInventory();
			
			for(ItemStack is: database.getItems(s)) {
				if(is == null)
					continue;
				
				cI.addItem(is);
			}
		}
	}

}
