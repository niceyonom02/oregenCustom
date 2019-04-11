package gui;

import main.GoodByeLava;
import me.lucko.luckperms.api.Node;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class PurchaseGUI {
    private GoodByeLava instance;
    private Inventory inventory;
    private Player player;

    public PurchaseGUI(GoodByeLava instance, Player player){
        this.instance = instance;

        inventory = Bukkit.createInventory(null, instance.getConfigManager().getInventorySize(), instance.getConfigManager().getInventoryName());
        this.player = player;

        setItem();
    }

    public void setItem(){
        for(String gen : instance.getConfigManager().getPurchase().getConfigurationSection("purchaseGUI").getKeys(false)){
            String name = instance.getConfigManager().getStringFromGUI(gen, "displayName");
            Material mat = Material.matchMaterial(instance.getConfigManager().getStringFromGUI(gen, "material"));
            int slot = instance.getConfigManager().getIntFromGUI(gen, "slot");

            ArrayList<String> lore = instance.getConfigManager().getStringListFromGUI(gen, "lore");

            lore.add("");

            boolean found = false;
            for(Node e : instance.getPermissionAPI().getUser(player.getName()).getOwnNodes()){
                if(e.getPermission().equals(instance.getConfigManager().getStringFromGen(gen, "permission"))){
                    lore.add("Unlocked!");
                    found = true;
                    break;
                }
            }

            if(!found){
                lore.add("locked!");
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
        }
    }

    public void openInv(){
        player.openInventory(inventory);
    }
}
