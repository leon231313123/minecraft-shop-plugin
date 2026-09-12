package com.shop.minecraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.shop.minecraft.manager.ShopManager;

public class ShopCommand implements CommandExecutor {
    
    private ShopManager shopManager;
    
    public ShopCommand(ShopManager shopManager) {
        this.shopManager = shopManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von Spielern verwendet werden!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Permissions check
        if (!player.hasPermission("minecraftshop.use")) {
            player.sendMessage("§c✗ Du hast keine Berechtigung für diesen Befehl!");
            return true;
        }
        
        // Shop öffnen
        shopManager.openShop(player);
        return true;
    }
}
