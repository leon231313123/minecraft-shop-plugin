package com.shop.minecraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import com.shop.minecraft.manager.ShopManager;

public class InventoryListener implements Listener {
    
    private ShopManager shopManager;
    
    public InventoryListener(ShopManager shopManager) {
        this.shopManager = shopManager;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Prüfe ob es ein Shop-Inventar ist
        if (!event.getView().getTitle().contains("SHOP") && 
            !event.getView().getTitle().toUpperCase().contains("END") &&
            !event.getView().getTitle().toUpperCase().contains("NETHER") &&
            !event.getView().getTitle().toUpperCase().contains("GEAR") &&
            !event.getView().getTitle().toUpperCase().contains("FOOD") &&
            !event.getView().getTitle().toUpperCase().contains("SHARDS")) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        String currentMenu = shopManager.getPlayerMenu(player);
        String title = event.getView().getTitle();
        
        // Zurück Button
        if (clicked.getType() == Material.ARROW) {
            if (!currentMenu.equals("main")) {
                shopManager.openShop(player);
            }
            return;
        }
        
        // Hauptmenü - Kategorien
        if (title.contains("SHOP")) {
            String[] categories = {"end", "nether", "gear", "food", "shards"};
            
            for (String category : categories) {
                String categoryName = getDisplayName(clicked);
                if (categoryName.toUpperCase().contains(category.toUpperCase())) {
                    shopManager.openCategory(player, category);
                    return;
                }
            }
        }
        // Shop-Items kaufen
        else {
            String categoryKey = title.substring(title.lastIndexOf(" ")).trim().toLowerCase();
            
            // Versuche die Kategorie zu ermitteln
            if (title.toUpperCase().contains("END")) {
                categoryKey = "end";
            } else if (title.toUpperCase().contains("NETHER")) {
                categoryKey = "nether";
            } else if (title.toUpperCase().contains("GEAR")) {
                categoryKey = "gear";
            } else if (title.toUpperCase().contains("FOOD")) {
                categoryKey = "food";
            } else if (title.toUpperCase().contains("SHARDS")) {
                categoryKey = "shards";
            }
            
            String displayName = getDisplayName(clicked);
            // Finde das Item anhand des Display-Names
            String itemKey = findItemKey(categoryKey, displayName, clicked.getType());
            
            if (itemKey != null && !itemKey.isEmpty()) {
                shopManager.buyItem(player, categoryKey, itemKey);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().contains("SHOP")) {
            shopManager.removePlayer((Player) event.getPlayer());
        }
    }
    
    private String getDisplayName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return item.getType().name();
    }
    
    private String findItemKey(String categoryKey, String displayName, Material material) {
        // Einfache Zuordnung: Versuche anhand des Materials den Item-Key zu finden
        String[] possibleKeys = {
            "totem_of_undying", "ender_pearl", "golden_apple", "enchanted_golden_apple",
            "experience_bottle", "end_crystal", "respawn_anchor", "glowstone",
            "obsidian", "ender_chest", "dragon_egg", "shulker_shell", "crying_obsidian",
            "ancient_debris", "cooked_beef", "golden_carrot", "glow_berries", "amethyst_shard"
        };
        
        for (String key : possibleKeys) {
            if (displayName.toLowerCase().contains(key.replace("_", " "))) {
                return key;
            }
        }
        
        // Fallback: Versuche anhand des Materials zu matchen
        return material.name().toLowerCase();
    }
}
