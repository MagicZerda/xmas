package de.magiczerda.xmas.utils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Items {
	
	public static ItemStack sapling() {
		ItemStack sapling = new ItemStack(Material.SPRUCE_SAPLING, 1);
		ItemMeta saplingMeta = sapling.getItemMeta();
		saplingMeta.setDisplayName("Christmas tree sapling");
		saplingMeta.setLore(Arrays.asList("Place this sapling in an open area", "and it will grow into a christmas tree", "with presents on the 25th of December!"));
		sapling.setItemMeta(saplingMeta);
		
		return sapling;
	}
	
	public static ItemStack wand() {
		ItemStack wand = new ItemStack(Material.BLAZE_ROD, 1);
		ItemMeta wandMeta = wand.getItemMeta();
		wandMeta.setDisplayName("X-mas wand");
		wandMeta.setLore(Arrays.asList("This item is used to make a schematic", "for the christmas tree"));
		wand.setItemMeta(wandMeta);
		
		return wand;
	}
	
}
