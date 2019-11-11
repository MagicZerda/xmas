package de.magiczerda.xmas.schematic;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;

import de.magiczerda.xmas.database.DBManager;
import de.magiczerda.xmas.plugin.Plugin;

public class SchematicManager {
	
	Plugin plugin;
	DBManager database;
	ArrayList<String> schematics;
	
	public SchematicManager(Plugin plugin, DBManager database) {
		this.plugin = plugin;
		this.database = database;
		schematics = new ArrayList<String>();
	}
	
	public void load() {
		ArrayList<String> list = getStringListFromFile();
		
		if(list != null && list.size() > 0)
			for(String s : getStringListFromFile())
				schematics.add(s);
	}
	
	public ArrayList<String> getStringListFromFile() {
		String raw = database.loadSchematic();
		if(raw == "")
			return null;
		String[] blocks = raw.split("umm_meow");
		ArrayList<String> list = new ArrayList<String>();
		for(String s : blocks)
			list.add(s);
		return list;
	}
	
	public void save(ArrayList<String> list) {
		String output = "";
		if(list == null)
			return;
		for(String s : list)
			if(s != "")
				output += s + "umm_meow";
		
		database.saveSchematic(output);
	}
	
	public ArrayList<String> convertBlocksToStrings(ArrayList<Block> blocks, Location start) {
		ArrayList<String> list = new ArrayList<String>();
		
		for(Block b : blocks)
			list.add(blockToString(b, start));
		
		return list;
	}
	
	public ArrayList<Block> getBlocks(Location pos1, Location pos2) {
		return new Cuboid(pos1, pos2).getBlocks();
	}
	
	public void paste(Location start) {
		if(schematics == null)
			return;
		for(String str : schematics) {
			String[] s = str.split("/");
			
			int x = Integer.parseInt(s[0]);
			int y = Integer.parseInt(s[1]);
			int z = Integer.parseInt(s[2]);
			start.clone().add(x, y, z).getBlock().setBlockData(plugin.getServer().createBlockData(s[3]));
		}
	}
	private String blockToString(Block b, Location start) {
		int diffX = b.getX() - start.getBlockX();
		int diffY = b.getY() - start.getBlockY();
		int diffZ = b.getZ() - start.getBlockZ();
		String blockData = b.getBlockData().getAsString();
		if(blockData.equalsIgnoreCase("minecraft:air"))
			return "";
		return diffX + "/" + diffY + "/" + diffZ + "/" + blockData;
	}
	
}
