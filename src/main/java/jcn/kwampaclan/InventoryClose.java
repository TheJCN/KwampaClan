package jcn.kwampaclan;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class InventoryClose implements Listener {
    NamespacedKey namespacedKey = new NamespacedKey((Plugin) Bukkit.getServer().getPluginManager().getPlugin("KwampaClan"), "KwampaClan");
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(event.getPlayer() instanceof Player) {
            Inventory inventory = event.getInventory();
            PersistentDataHolder holder = (PersistentDataHolder) inventory.getHolder();
            PersistentDataContainer container = holder.getPersistentDataContainer();
            if(container.get(namespacedKey, PersistentDataType.STRING) != null && container.get(namespacedKey, PersistentDataType.STRING).equals("KwampaClan")){
                container.set(namespacedKey, PersistentDataType.STRING, "Defualt");
            }
        }
    }
}
