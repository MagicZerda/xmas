package de.magiczerda.xmas.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class UUIDNameConverter {
	
	/**
	 * These two methods were designed by Proxygames14.
	 * https://bukkit.org/threads/how-to-convert-uuid-to-name-and-name-to-uuid-uising-mojang-api.460828/
	 * 
	 * @param uuid
	 * @return
	 * @throws PlayerNotFoundException
	 */
	
	public static String name(UUID uuid) throws PlayerNotFoundException {
		try {
			String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";
			
			@SuppressWarnings("deprecation")
			String nameJson = IOUtils.toString(new URL(url));
			
			JSONArray nameValue = (JSONArray) JSONValue.parseWithException(nameJson);
			String playerSlot = nameValue.get(nameValue.size() - 1).toString();
			
			JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
			
			String name = nameObject.get("name").toString();
			return name;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		throw new PlayerNotFoundException("Player " + uuid.toString() + " was not found in the Mojang API");
	}
	
	public static UUID uuid(String name) throws PlayerNotFoundException {
		try {
			String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
			
			@SuppressWarnings("deprecation")
			String UUIDJson = IOUtils.toString(new URL(url));
			
			if(UUIDJson.isEmpty())
				throw new PlayerNotFoundException("Player " + name + " was not found in the Mojang API");
			
			JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
			
			String trimmedUUID = UUIDObject.get("id").toString();
			
			return untrimUUID(trimmedUUID);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * This method was not written by me.
	 * See https://www.geeksforgeeks.org/insert-a-string-into-another-string-in-java/
	 * 
	 * @param originalString
	 * @param stringToBeInserted
	 * @param index
	 * @return
	 */
	
	private static String insertString(String originalString, String stringToBeInserted, int index) { 
		// Create a new string
		String newString = new String();
		
		for (int i = 0; i < originalString.length(); i++) {
			
			// Insert the original string character
			// into the new string
			newString += originalString.charAt(i);
			if (i == index) {
				
				// Insert the string to be inserted
				// into the new string
				newString += stringToBeInserted;
			} 
		}
		
		// return the modified String
		return newString;
	}
	
	private static UUID untrimUUID(String trimmedUUID) {
		String hyphan1 = insertString(trimmedUUID, "-", 7);
		String hyphan2 = insertString(hyphan1, "-", 12);
		String hyphan3 = insertString(hyphan2, "-", 17);
		String hyphan4 = insertString(hyphan3, "-", 22);
		UUID uuid = UUID.fromString(hyphan4);
		return uuid;
	}
	
}
