package command;

import gui.PurchaseGUI;
import main.GoodByeLava;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class InGameCommand implements CommandExecutor {
    private GoodByeLava instance;

    public InGameCommand(GoodByeLava instance){
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arg){
        if(!(sender instanceof Player)){
            return false;
        }

        Player player = (Player) sender;

        if(label.equalsIgnoreCase("gbl")){
            if(player.isOp()){
                if(arg.length < 1){
                    adminHelp(player);
                    return false;
                }

                if(arg[0].equalsIgnoreCase("reload")){
                    instance.reloadYaml();
                }

                if(arg[0].equalsIgnoreCase("open")){
                    if(instance.getUsingHGUI()){
                        PurchaseGUI gui = new PurchaseGUI(instance, player);
                        gui.openInv();
                        return true;
                    } else{
                        player.sendMessage("The server does not allowed to use GUI!");
                        return false;
                    }
                }

                if(arg[0].equalsIgnoreCase("permission")){
                    if(arg.length < 2){
                        adminHelp(player);
                    } else{
                        HashMap<String, String> permList = new HashMap<>();

                        try{
                            User user = instance.getPermissionAPI().getUser(arg[1]);

                            for(Node n : user.getOwnNodes()){
                                player.sendMessage(n.getPermission());
                            }
                            return true;
                        }catch (NullPointerException e){
                            player.sendMessage("§cUser is not currently online or doesn't have permissions!");
                            return false;
                        }
                    }
                }

                return false;
            }else{
                sender.sendMessage("§fUnknown command. Type \"/help\" for help.");
                return false;
            }
        }
        return false;
    }

    public void adminHelp(Player player){
        player.sendMessage("§7[ §eAdmin Help §7]");
        player.sendMessage("§e/gbl reload §7: reload the plugin and configs.");
        player.sendMessage("§e/gbl permission [playerName] §7: show you the current permission List of specific player (only about generators).");
        player.sendMessage("§e/gbl open §7: open tou the GUI.");
        player.sendMessage("§e/gbl author §7: You can get the information about the plugin author.");
    }
}
