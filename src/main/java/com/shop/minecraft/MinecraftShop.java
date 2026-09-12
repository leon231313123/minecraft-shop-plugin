package com.shop.minecraft;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.shop.minecraft.commands.ShopCommand;
import com.shop.minecraft.listeners.InventoryListener;
import com.shop.minecraft.manager.ShopManager;
import com.shop.minecraft.manager.ConfigManager;

public class MinecraftShop extends JavaPlugin {
    
    private static MinecraftShop instance;
    private Economy economy;
    private ConfigManager configManager;
    private ShopManager shopManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Config laden
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        
        // Vault Economy initialisieren
        if (!setupEconomy()) {
            getLogger().severe("Vault nicht gefunden! Plugin wird deaktiviert.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        // Shop Manager initialisieren
        shopManager = new ShopManager(this, configManager, economy);
        
        // Commands registrieren
        getCommand("shop").setExecutor(new ShopCommand(shopManager));
        
        // Listener registrieren
        Bukkit.getPluginManager().registerEvents(new InventoryListener(shopManager), this);
        
        getLogger().info("✓ MinecraftShop Plugin aktiviert!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("✓ MinecraftShop Plugin deaktiviert!");
    }
    
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
    
    public static MinecraftShop getInstance() {
        return instance;
    }
    
    public Economy getEconomy() {
        return economy;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public ShopManager getShopManager() {
        return shopManager;
    }
}
