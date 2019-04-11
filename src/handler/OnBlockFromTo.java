package handler;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import io.netty.util.concurrent.CompleteFuture;
import main.GoodByeLava;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.manager.UserManager;
import org.apache.commons.lang3.concurrent.ConcurrentRuntimeException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.wasteofplastic.askyblock.ASkyBlockAPI.getInstance;

public class OnBlockFromTo implements Listener {
    private GoodByeLava instance;
    private ArrayList<BlockFace> faceList = new ArrayList<>();
    //private ArrayList<String> forbiddenWorlds = (ArrayList<String>) instance.getConfigManager().getGeneratorFile().getStringList("forbiddenWorld");

    public OnBlockFromTo(GoodByeLava instance) {
        this.instance = instance;

        faceList.add(BlockFace.NORTH);
        faceList.add(BlockFace.SOUTH);
        faceList.add(BlockFace.EAST);
        faceList.add(BlockFace.WEST);
        faceList.add(BlockFace.UP);
        faceList.add(BlockFace.DOWN);
    }

    @EventHandler
    public void BlockFromTo(BlockFromToEvent e) {
        Block from = e.getBlock();
        Block to = e.getToBlock();

        if (from.getType().equals(Material.WATER) || from.getType().equals(Material.STATIONARY_WATER)) {
            if (to.getType() == null || to.getType().equals(Material.AIR)) {
                Material item = Material.matchMaterial(instance.getConfigManager().getGeneratorFile().getString("item"));

                /**for (String ed : forbiddenWorlds) {
                    if (ed.equalsIgnoreCase(from.getWorld().getName())) {
                        return;
                    }
                } */

                for (BlockFace face : faceList) {
                    if (to.getRelative(face).getType().equals(item)) {
                        e.setCancelled(true);
                        UUID owner = getInstance().getOwner(from.getLocation());

                        if (Bukkit.getPlayer(owner) == null) {
                            List<UUID> members = ASkyBlockAPI.getInstance().getTeamMembers(owner);

                            String gen = getOfflineOwnerGen(members);

                            Material material = getRandomOre(gen);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {

                                @Override
                                public void run() {
                                    if(to.getType().equals(Material.AIR)){
                                        to.setType(material);
                                    }
                                }
                            }, 15L);
                            return;
                        } else {
                            String gen = getOnlineOwnerGen(owner);

                            Material material = getRandomOre(gen);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {

                                @Override
                                public void run() {
                                    if(to.getType().equals(Material.AIR)){
                                        to.setType(material);
                                    }
                                }
                            }, 15L);
                            return;
                        }
                    }
                }
            }
        }
    }

    private String getOfflineOwnerGen(List<UUID> members) {
        ArrayList<String> generators = new ArrayList<>(instance.getConfigManager().getGeneratorFile().getConfigurationSection("generator").getKeys(false));

        String gen = "default";
        int index = -1;

        for(UUID uuid : members){
            Player player = Bukkit.getPlayer(uuid);

            if(player == null){
                continue;
            }

            User user = instance.getPermissionAPI().getUser(uuid);
            List<Node> nodeList = user.getOwnNodes();

            for(String key : generators){
                String perm = instance.getConfigManager().getStringFromGen(key, "permission");
                for(Node n : nodeList){
                    if(n.getPermission().equals(perm)){
                        if(generators.indexOf(key) > index){
                            index = generators.indexOf(key);
                        }
                    }
                }
            }
        }

        if(index == -1){
            return "default";
        } else{
            gen = generators.get(index);
            return gen;
        }
    }


    public String getOnlineOwnerGen(UUID uuid) {
        User user = instance.getPermissionAPI().getUser(uuid);
        List<Node> nodeList = user.getOwnNodes();

        String gen = "default";

        for (String key : instance.getConfigManager().getGeneratorFile().getConfigurationSection("generator").getKeys(false)) {
            String perm = instance.getConfigManager().getStringFromGen(key, "permission");

            for (Node n : nodeList) {
                if (n.getPermission().equals(perm)) {
                    gen = key;
                }
            }
        }
        return gen;
    }

    public Material getRandomOre(String gen) {
        int bottom = 0;
        int ceiling = 0;

        for (String key : instance.getConfigManager().getGeneratorFile().getConfigurationSection("generator").getKeys(false)) {
            if (gen.equals(key)) {
                Random random = new Random();
                int chosen = random.nextInt(1000);

                ArrayList<String> materials = new ArrayList<>(instance.getConfigManager().getGeneratorFile().getConfigurationSection("generator." + key + ".percentage").getKeys(false));

                for (int i = 0; i < materials.size(); i++) {
                    String mat = materials.get(i);
                    Double per = instance.getConfigManager().getGeneratorFile().getDouble("generator." + key + ".percentage" + "." + materials.get(i)) * 10;
                    int d = per.intValue();

                    if (i == 0) {
                        ceiling += d;
                    } else {
                        bottom = ceiling;
                        ceiling += d;
                    }

                    if (chosen >= bottom && chosen < ceiling) {
                        Material ma = Material.matchMaterial(mat);
                        return ma;
                    }
                }
            }
        }
        return defaultGen();
    }

    public Material defaultGen() {
        int bottom = 0;
        int ceiling = 0;

        Random random = new Random();
        int chosen = random.nextInt(1000);

        ArrayList<String> materials = new ArrayList<>(instance.getConfigManager().getGeneratorFile().getConfigurationSection("default.percentage").getKeys(false));

        for (int i = 0; i < materials.size(); i++) {
            String mat = materials.get(i);
            Double per = instance.getConfigManager().getGeneratorFile().getDouble("default.percentage" + "." + materials.get(i)) * 10;
            int d = per.intValue();

            if (i == 0) {
                ceiling += d;
            } else {
                bottom = ceiling;
                ceiling += d;
            }

            if (chosen >= bottom && chosen < ceiling) {
                Material ma = Material.matchMaterial(mat);
                return ma;
            }
        }
        return Material.STONE;
    }
}
