package de.magiczerda.xmas.plugin;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.magiczerda.xmas.commands.Gift;
import de.magiczerda.xmas.commands.Grow;
import de.magiczerda.xmas.commands.SaplingManager;
import de.magiczerda.xmas.database.DBManager;
import de.magiczerda.xmas.schematic.SchematicManager;
import de.magiczerda.xmas.schematic.Wand;
import de.magiczerda.xmas.utils.Items;
import de.magiczerda.xmas.utils.PlayerNotFoundException;
import de.magiczerda.xmas.utils.UUIDNameConverter;
import net.md_5.bungee.api.ChatColor;

public class Plugin extends JavaPlugin {
	
	SaplingManager saplingManager;
	Grow grow;
	Gift gift;
	DBManager database;
	SchematicManager schematicManager;
	Wand wand;
	
	@Override
	public void onEnable() {
		database = new DBManager("host", "database", "username", "password");
		database.createSchematicTable();
		schematicManager = new SchematicManager(this, database);
		wand = new Wand();
		
		saplingManager = new SaplingManager(database);
		grow = new Grow(database, schematicManager);
		gift = new Gift(database);
		
		getServer().getPluginManager().registerEvents(saplingManager, this);
		getServer().getPluginManager().registerEvents(gift, this);
		getServer().getPluginManager().registerEvents(wand, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {	//player commands
			
			Player p = (Player) sender;
			
//-----------------------------------------------------XMAS--------------------------------------------------------
			
			if(label.equalsIgnoreCase("xmas")) {
				if(!p.hasPermission("xmas.administrative")) {
					p.sendMessage(ChatColor.RED + "You don't have the permission to execute this command.");
					return true;
				}
				
				if(args.length != 1) {
					p.sendMessage(ChatColor.RED + "[X-mas] wrong argument count (use xmas start to distribute saplings, use xmas end to let the trees grow)");
					return false;
				}
				
				if(args[0].equalsIgnoreCase("start"))
					saplingManager.distribute(p);
				if(args[0].equalsIgnoreCase("end")) {
					if(!database.doesTableExist()) {
						p.sendMessage(ChatColor.RED + "[X-mas] You have to start the event first!");
						return true;
					}
					
					grow.grow();
					database.dropTables();
					getServer().broadcastMessage(ChatColor.DARK_RED + "Santa  " + ChatColor.WHITE + "just " + ChatColor.DARK_RED + "brought " + ChatColor.DARK_RED + "your " + ChatColor.WHITE + "presents!");
				}
					
				
				return true;
			}
			
//-----------------------------------------------------GIFT--------------------------------------------------------
			if(label.equalsIgnoreCase("gift")) {
				if(args.length != 1) {
					p.sendMessage(ChatColor.RED + "You need to specify a player who you want to gift your items");
					return false;
				}
				
				try {
					
					UUID uuid = UUIDNameConverter.uuid(args[0]);
					
					if(!database.hasSaplingPlaced(uuid)) {
						p.sendMessage(ChatColor.RED + "This player does not have a christmas tree");
						return true;
					}
					
					if(args[0].equalsIgnoreCase(p.getName())) {
						p.sendMessage(ChatColor.RED + "You can't look at your own presents! That's all boring!");
						return true;
					}
					
					//Player exists and has a tree
					gift.openInv(p, uuid);
					return true;
										
				} catch (PlayerNotFoundException e) {
					p.sendMessage(ChatColor.RED + "This player doen't exist!");
					return true;
				}
				
			}
			
//-----------------------------------------------------XWAND--------------------------------------------------------
			
			if(label.equalsIgnoreCase("xwand")) {
				if(!p.hasPermission("xmas.administrative")) {
					p.sendMessage(ChatColor.RED + "You don't have the permission to execute this command.");
					return true;
				}
				
				p.getInventory().addItem(Items.wand());
				p.sendMessage(ChatColor.GREEN + "[X-mas] you have been given a wand. Use this wand like worldedit and type xul to upload the schematic to the database. Make sure to type xul while looking at a chest in the selected area!");
			}
			
//-----------------------------------------------------XUL--------------------------------------------------------
			
			if(label.equalsIgnoreCase("xul")) {
				if(!p.hasPermission("xmas.administrative")) {
					p.sendMessage(ChatColor.RED + "You don't have the permission to execute this command.");
					return true;
				}
				
				ArrayList<Block> blocks = schematicManager.getBlocks(wand.getPos1(), wand.getPos2());
				ArrayList<String> list = schematicManager.convertBlocksToStrings(blocks, p.getTargetBlock(null, 5).getLocation());
				schematicManager.save(list);
				p.sendMessage(ChatColor.GREEN + "[X-mas] Your schematic has been uploaded to the database");
				return true;
			}
			
//-----------------------------------------------------CONSOLE--------------------------------------------------------
			
		} else {	//console commands
			if(label.equalsIgnoreCase("xmas")) {
				if(args.length != 1) {
					System.err.println("[X-mas] wrong argument count (use xmas start to distribute saplings, use xmas end to let the trees grow)");
					return false;
				}
				
				if(args[0].equalsIgnoreCase("start")) {
					saplingManager.distribute(null);
					System.out.println("[X-mas] all players will now get a christmas- tree sapling when they join the server (rejoining works of course)");
				}
				if(args[0].equalsIgnoreCase("end"))
					grow.grow();
				
				return true;
			}
		}
		
		return false;
	}
}
