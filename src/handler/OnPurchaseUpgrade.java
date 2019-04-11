package handler;

import event.PurchaseUpgradeEvent;
import main.GoodByeLava;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.common.node.model.ImmutableNode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class OnPurchaseUpgrade implements Listener {
    private GoodByeLava instance;

    public OnPurchaseUpgrade(GoodByeLava instance){
        this.instance = instance;
    }

    @EventHandler
    public void onPurchase(PurchaseUpgradeEvent e){
        Player player = e.getPlayer();
        String generatorName = e.getGenName();

        if(permCheck(player, generatorName)){
            if(costCheck(player, generatorName)){
                success(player, generatorName);
            }
        }
    }

    public boolean permCheck(Player player, String gen){
        List<Node> playerPermissions =  instance.getPermissionAPI().getUser(player.getName()).getOwnNodes();

        for(Node n : playerPermissions){
            if(n.getPermission().equals(instance.getConfigManager().getStringFromGen(gen, "permission"))){
                player.sendMessage("이미 있음!");
                return false;
            }
        }

        ArrayList<String> shouldHavePermission = new ArrayList<>();

        for(String e : instance.getConfigManager().getPurchase().getConfigurationSection("purchaseGUI").getKeys(false)){
            if(e.equals(gen)){
                break;
            } else{
                shouldHavePermission.add(instance.getConfigManager().getStringFromGen(e, "permission"));
            }
        }

        if(shouldHavePermission.isEmpty()){
            player.sendMessage("필요 펄미션 없음!");
            return true;
        }

        for(String e : shouldHavePermission){
            boolean found = false;

            for(Node n : playerPermissions){
                if(n.getPermission().equals(e)){
                    found = true;
                    break;
                }
            }

            if(!found){
                player.sendMessage("펄미션 부족!");
                return false;
            }
        }
        return true;
    }

    public boolean costCheck(Player player, String gen){
        for(String e : instance.getConfigManager().getPurchase().getConfigurationSection("purchaseGUI").getKeys(false)){
            if(e.equals(gen)){
                int cost = instance.getConfigManager().getIntFromGUI(gen, "cost");

                if(instance.getEconomy().getBalance(player.getName()) < cost){
                    player.sendMessage("돈 부족!");
                    return false;
                } else{
                    instance.getEconomy().withdrawPlayer(player.getName(), cost);
                    player.sendMessage("돈 완료!");
                    return true;
                }
            }
        }
        return false;
    }

    public void success(Player player, String gen){
        player.sendMessage("완료!");
        player.closeInventory();
        User user = instance.getPermissionAPI().getUser(player.getName());
        Node node = instance.getPermissionAPI().buildNode(instance.getConfigManager().getStringFromGen(gen, "permission")).build();
        user.setPermission(node);
        instance.getPermissionAPI().getUserManager().saveUser(user);
    }
}
