package com.shop.minecraft.manager;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager {
    
    private JavaPlugin plugin;
    private FileConfiguration config;
    private Map<String, Map<String, ShopItem>> categories;
    
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.categories = new HashMap<>();
        loadShopItems();
    }
    
    private void loadShopItems() {
        Set<String> categoryKeys = config.getConfigurationSection("shop.categories").getKeys(false);
        
        for (String categoryKey : categoryKeys) {
            Map<String, ShopItem> items = new HashMap<>();
            String categoryPath = "shop.categories." + categoryKey;
            
            String categoryName = config.getString(categoryPath + ".name", categoryKey);
            String categoryDesc = config.getString(categoryPath + ".description", "");
            
            Set<String> itemKeys = config.getConfigurationSection(categoryPath + ".items").getKeys(false);
            
            for (String itemKey : itemKeys) {
                String itemPath = categoryPath + ".items." + itemKey;
                String material = config.getString(itemPath + ".material");
                String displayName = config.getString(itemPath + ".display-name", material);
                double price = config.getDouble(itemPath + ".price", 0);
                int amount = config.getInt(itemPath + ".amount", 1);
                java.util.List<String> lore = config.getStringList(itemPath + ".lore");
                
                ShopItem shopItem = new ShopItem(
                    itemKey,
                    material,
                    displayName,
                    lore,
                    price,
                    amount
                );
                
                items.put(itemKey, shopItem);
            }
            
            categories.put(categoryKey, items);
        }
    }
    
    public Map<String, Map<String, ShopItem>> getCategories() {
        return categories;
    }
    
    public Map<String, ShopItem> getCategory(String categoryKey) {
        return categories.getOrDefault(categoryKey, new HashMap<>());
    }
    
    public ShopItem getItem(String categoryKey, String itemKey) {
        Map<String, ShopItem> category = getCategory(categoryKey);
        return category.getOrDefault(itemKey, null);
    }
    
    public static class ShopItem {
        public String key;
        public String material;
        public String displayName;
        public java.util.List<String> lore;
        public double price;
        public int amount;
        
        public ShopItem(String key, String material, String displayName, java.util.List<String> lore, double price, int amount) {
            this.key = key;
            this.material = material;
            this.displayName = displayName;
            this.lore = lore;
            this.price = price;
            this.amount = amount;
        }
    }
}
