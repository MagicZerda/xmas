package de.magiczerda.xmas.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import de.magiczerda.xmas.utils.ItemSerializer;
import de.magiczerda.xmas.utils.Sapling;

public class DBManager {
	
	Connection connection;
	Statement statement;
	
	public DBManager(String host, String database, String user, String password) {
		try {
			
			connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, password);
			statement = connection.createStatement();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("[X-mas] Could not connect to database");
		}
		
	}
	
	public void createTables() {
		try {
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS distributeSaplings ("
					+ "uuid TEXT)");
			
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS presents ("
					+ "owner TEXT,"
					+ "world TEXT,"
					+ "posX INT,"
					+ "posY INT,"
					+ "posZ INT,"
					+ "presents TEXT)");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createSchematicTable() {
		try {
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS schematics ("
					+ "schematic TEXT)");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean doesTableExist() {
		ResultSet tables;
		try {
			DatabaseMetaData dbm = connection.getMetaData();
			tables = dbm.getTables(null, null, "presents", null);
			
			if(tables.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public void dropTables() {
		try {
			statement.executeUpdate("DROP TABLE distributeSaplings");
			statement.executeUpdate("DROP TABLE presents");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasSaplingItem(UUID uuid) {
		try {
			ResultSet rs = statement.executeQuery("SELECT uuid FROM distributeSaplings WHERE uuid = '" + uuid + "'");
			
			if(rs.next())
				return true;
			
			return false;
			
		} catch (SQLException e) {
			return true;
		}
	}
	
	public boolean hasSaplingPlaced(UUID uuid) {
		try {
			ResultSet rs = statement.executeQuery("SELECT owner FROM presents WHERE owner = '" + uuid + "'");
			
			if(rs.next())
				return true;
			
			return false;
			
		} catch (SQLException e) {
			return false;
		}
	}
	
	public void addPlayer(UUID uuid) {
		try {
			statement.executeUpdate("INSERT INTO distributeSaplings VALUES ('" + uuid + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addSapling(Sapling sapling) {
		try {
			statement.executeUpdate("INSERT INTO presents VALUES ("
					+ "'" + sapling.getOwner() + "',"								//owner
					+ "'" + sapling.getLocation().getWorld().getName() + "',"		//world
					+ sapling.getLocation().getBlockX() + ","						//posX
					+ sapling.getLocation().getBlockY() + ","						//posY
					+ sapling.getLocation().getBlockZ() + ","						//posZ
					+ "'')");														//presents (empty for now)
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ItemStack[] getItems(Sapling sapling) {
		try {
			ResultSet rs = statement.executeQuery("SELECT presents FROM presents WHERE owner='" + sapling.getOwner() + "'");
			
			if(rs == null)
				return new ItemStack[0];
			
			rs.next();
			
			String itemString = rs.getString("presents");
			if(itemString == null)
				return new ItemStack[0];
			
			ItemStack[] result = ItemSerializer.itemStackArrayFromBase64(itemString);
			
			if(result != null)
				return result;
			
			return new ItemStack[0];
			
		} catch (SQLException e) {
			return new ItemStack[0];
		}
	}
	
	public void updateSapling(Sapling sapling, ItemStack[] items) {
		String serializedItems = ItemSerializer.itemStackArrayToBase64(items);
		
		try {
			statement.executeUpdate("DELETE FROM presents WHERE owner='" + sapling.getOwner() + "'");
			
			statement.executeUpdate("INSERT INTO presents VALUES ("
					+ "'" + sapling.getOwner() + "',"								//owner
					+ "'" + sapling.getLocation().getWorld().getName() + "',"		//world
					+ sapling.getLocation().getBlockX() + ","						//posX
					+ sapling.getLocation().getBlockY() + ","						//posY
					+ sapling.getLocation().getBlockZ() + ","						//posZ
					+ "'" + serializedItems + "')");								//updated contents
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public Sapling getSapling(UUID uuid) {
		try {
			ResultSet rs = statement.executeQuery("SELECT world, posX, posY, posZ FROM presents WHERE owner='" + uuid + "'");
			
			rs.next();
			
			World world = Bukkit.getWorld(rs.getString("world"));
			int posX = rs.getInt("posX");
			int posY = rs.getInt("posY");
			int posZ = rs.getInt("posZ");
			
			Location loc = new Location(world, posX, posY, posZ);
			
			Sapling sapling = new Sapling(uuid, loc);
			
			return sapling;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isSapling(Location location) {
		try {
			
			String world = location.getWorld().getName();
			int posX = location.getBlockX();
			int posY = location.getBlockY();
			int posZ = location.getBlockZ();
			
			ResultSet rs = statement.executeQuery("SELECT world, posX, posY, posZ FROM presents WHERE "
					+ "world='" + world + "' AND "
					+ "posX=" + posX + " AND "
					+ "posY=" + posY + " AND "
					+ "posZ=" + posZ);
			
			if(rs.next())
				return true;
			else
				return false;
			
		} catch (SQLException e) {
			return false;
		}
	}
	
	public List<Sapling> getAllSaplingLocations() {
		try {
			ResultSet rs = statement.executeQuery("SELECT owner, world, posX, posY, posZ FROM presents");
			List<Sapling> locations = new ArrayList<Sapling>();
			
			while(rs.next()) {
				UUID owner = UUID.fromString(rs.getString("owner"));
				World world = Bukkit.getWorld(rs.getString("world"));
				int posX = rs.getInt("posX");
				int posY = rs.getInt("posY");
				int posZ = rs.getInt("posZ");
				locations.add(new Sapling(owner, new Location(world, posX, posY, posZ)));
			}
			
			return locations;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String loadSchematic() {
		try {
			ResultSet rs = statement.executeQuery("SELECT schematic FROM schematics");
			rs.next();
			String schematic = rs.getString("schematic");
			return schematic;
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public void saveSchematic(String schematic) {
		try {
			statement.executeUpdate("TRUNCATE TABLE schematics");
			
			PreparedStatement ps = connection.prepareStatement("INSERT INTO schematics VALUES(?)");
			ps.setString(1, schematic);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
