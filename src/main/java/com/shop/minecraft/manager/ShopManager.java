package com.shop.minecraft.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import com.shop.minecraft.manager.ConfigManager.ShopItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopManager {
    
    private JavaPlugin plugin;
    private ConfigManager configManager;
    private Economy economy;
    private Map<Player, String> playerCurrentMenu;
    
    private static final int INVENTORY_SIZE = 45;
    private static final String MAIN_MENU = "main";
    
    public ShopManager(JavaPlugin plugin, ConfigManager configManager, Economy economy) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.economy = economy;
        this.playerCurrentMenu = new HashMap<>();
    }
    
    public void openShop(Player player) {
        playerCurrentMenu.put(player, MAIN_MENU);
        openMainMenu(player);
    }
    
    private void openMainMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, "§6═══ SHOP §6═══");
        
        // Background - fill with glass panes
        ItemStack background = createGlassPane();
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            inventory.setItem(i, background);
        }
        
        // Kategorien in der Mitte anordnen (Slots 19, 21, 23, 25, 27)
        int[] categorySlots = {19, 21, 23, 25, 27};
        String[] categoryKeys = {"end", "nether", "gear", "food", "shards"};
        
        for (int i = 0; i < categoryKeys.length; i++) {
            Map<String, ShopItem> category = configManager.getCategory(categoryKeys[i]);
            if (!category.isEmpty()) {
                // Nimm das erste Item der Kategorie als Icon
                ShopItem firstItem = category.values().iterator().next();
                ItemStack categoryItem = createCategoryItem(categoryKeys[i], firstItem);
                inventory.setItem(categorySlots[i], categoryItem);
            }
        }
        
        player.openInventory(inventory);
    }
    
    public void openCategory(Player player, String categoryKey) {
        Map<String, ShopItem> items = configManager.getCategory(categoryKey);
        
        if (items.isEmpty()) {
            player.sendMessage("§cKategorie nicht gefunden!");
            return;
        }
        
        int inventorySize = ((items.size() + 8) / 9) * 9;
        if (inventorySize < 27) inventorySize = 27;
        if (inventorySize > 54) inventorySize = 54;
        
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "§6" + categoryKey.toUpperCase());
        
        // Items hinzufügen
        int slot = 0;
        for (ShopItem item : items.values()) {
            if (slot >= inventorySize - 9) break; // Letzte Reihe für Navigation
            inventory.setItem(slot, createShopItemDisplay(categoryKey, item));
            slot++;
        }
        
        // Zurück Button (unten links)
        ItemStack backButton = createBackButton();
        inventory.setItem(inventorySize - 9, backButton);
        
        playerCurrentMenu.put(player, categoryKey);
        player.openInventory(inventory);
    }
    
    public void buyItem(Player player, String categoryKey, String itemKey) {
        ShopItem shopItem = configManager.getItem(categoryKey, itemKey);
        
        if (shopItem == null) {
            player.sendMessage("§cItem nicht gefunden!");
            return;
        }
        
        double totalPrice = shopItem.price * shopItem.amount;
        
        // Prüfe ob Spieler genug Geld hat
        if (economy.getBalance(player) < totalPrice) {
            player.sendMessage("§c✗ Du hast nicht genug Geld! §e(Benötigt: $" + totalPrice + ", Hast: $" + economy.getBalance(player) + ")");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }
        
        // Geld abziehen
        economy.withdrawPlayer(player, totalPrice);
        
        // Item geben
        try {
            Material material = Material.valueOf(shopItem.material);
            ItemStack item = new ItemStack(material, shopItem.amount);
            
            Map<String, Integer> leftover = player.getInventory().addItem(item);
            
            if (!leftover.isEmpty()) {
                // Items droppen wenn Inventar voll
                for (ItemStack drop : leftover.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), drop);
                }
                player.sendMessage("§e⚠ Dein Inventar war voll! Items wurden gedroppt.");
            }
            
            player.sendMessage("§a✓ Du hast " + shopItem.displayName + " §akauft! §7(-$" + totalPrice + ")");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            
            // Inventar nicht schließen - bleibt offen für schnelle Käufe!
            // Inventar aktualisieren
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                openCategory(player, categoryKey);
            }, 1L);
            
        } catch (IllegalArgumentException e) {
            player.sendMessage("§c✗ Fehler beim Item-Material: " + shopItem.material);
        }
    }
    
    private ItemStack createCategoryItem(String categoryKey, ShopItem icon) {
        try {
            Material material = Material.valueOf(icon.material);
            ItemStack item = new ItemStack(material, 1);
            ItemMeta meta = item.getItemMeta();
            
            if (meta != null) {
                meta.setDisplayName("§e" + categoryKey.toUpperCase());
                List<String> lore = new ArrayList<>();
                lore.add("§7Klick um zu öffnen");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            
            return item;
        } catch (IllegalArgumentException e) {
            return new ItemStack(Material.CHEST);
        }
    }
    
    private ItemStack createShopItemDisplay(String categoryKey, ShopItem shopItem) {
        try {
            Material material = Material.valueOf(shopItem.material);
            ItemStack item = new ItemStack(material, 1);
            ItemMeta meta = item.getItemMeta();
            
            if (meta != null) {
                meta.setDisplayName(shopItem.displayName);
                
                List<String> lore = new ArrayList<>(shopItem.lore);
                lore.add("");
                lore.add("§6Preis: §e$" + shopItem.price);
                lore.add("§6Menge: §e" + shopItem.amount);
                lore.add("");
                lore.add("§aKlick zum Kaufen");
                
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            
            return item;
        } catch (IllegalArgumentException e) {
            return new ItemStack(Material.BARRIER);
        }
    }
    
    private ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c← Zurück");
            List<String> lore = new ArrayList<>();
            lore.add("§7Klick zum Zurückgehen");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createGlassPane() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }
    
    public String getPlayerMenu(Player player) {
        return playerCurrentMenu.getOrDefault(player, MAIN_MENU);
    }
    
    public void setPlayerMenu(Player player, String menu) {
        playerCurrentMenu.put(player, menu);
    }
    
    public void removePlayer(Player player) {
        playerCurrentMenu.remove(player);
    }
}
