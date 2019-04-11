package handler;

import event.PurchaseUpgradeEvent;
import main.GoodByeLava;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OnClick implements Listener {
    private GoodByeLava instance;

    public OnClick(GoodByeLava instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        if (e.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) e.getWhoClicked();

        if (e.getInventory().getName().equalsIgnoreCase(instance.getConfigManager().getInventoryName())) {
            e.setCancelled(true);

            if(e.getClick().equals(ClickType.NUMBER_KEY)){
                e.setCancelled(true);
            }

            for (String key : instance.getConfigManager().getPurchase().getConfigurationSection("purchaseGUI").getKeys(false)) {
                if (e.getSlot() == instance.getConfigManager().getIntFromGUI(key, "slot")) {
                   PurchaseUpgradeEvent event = new PurchaseUpgradeEvent(player, key);
                   Bukkit.getPluginManager().callEvent(event);
                }
            }
        }
    }
}
