package util;

import main.GoodByeLava;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class ConfigManager {
    private GoodByeLava instance;
    private File generatorsFile;
    private YamlConfiguration generators;
    private File messageFile;
    private YamlConfiguration message;
    private File purchaseFile;
    private YamlConfiguration purchase;

    public ConfigManager(GoodByeLava instance){
        this.instance = instance;

        generatorsFile = new File(instance.getDataFolder(), "Generator.yml");
        if(!generatorsFile.exists()){
            instance.saveResource("Generator.yml", false);
        }
        generators = YamlConfiguration.loadConfiguration(generatorsFile);

        messageFile = new File(instance.getDataFolder(), "message.yml");
        if(!messageFile.exists()){
            instance.saveResource("message.yml", false);
        }
        message = YamlConfiguration.loadConfiguration(messageFile);

        purchaseFile = new File(instance.getDataFolder(), "purchaseGUI.yml");
        if(!purchaseFile.exists()){
            instance.saveResource("purchaseGUI.yml", false);
        }
        purchase = YamlConfiguration.loadConfiguration(purchaseFile);
    }

    public String getMessage(String path){
        String e = message.getString(path);
        e = ChatColor.translateAlternateColorCodes('&', e);
        return e;
    }

    public String getInventoryName(){
        String e = purchase.getString("setting.inventoryName");
        e = ChatColor.translateAlternateColorCodes('&', e);
        return e;
    }

    public int getInventorySize(){
        return purchase.getInt("setting.inventorySize");
    }

    public YamlConfiguration getGeneratorFile(){
        return generators;
    }

    public int getIntFromGUI(String gen, String path){
        int a = purchase.getInt("purchaseGUI." + gen + "." + path);
        return a;
    }

    public String getStringFromGUI(String gen, String path){
        String uncolored = purchase.getString("purchaseGUI." + gen + "." + path);
        String colored = ChatColor.translateAlternateColorCodes('&', uncolored);
        return colored;
    }

    public String getStringFromGen(String gen, String path){
        String s = generators.getString("generator." + gen + "." + path);
        return s;
    }

    public ArrayList<String> getStringListFromGUI(String gen, String path){
        ArrayList<String> coloredList = new ArrayList<>();

        if(purchase.getStringList("purchaseGUI." + gen + "." + path) != null){
            purchase.getStringList("purchaseGUI." + gen + "." + path).forEach((e) -> {
                String colored = ChatColor.translateAlternateColorCodes('&', e);
                coloredList.add(colored);
            });
        }
        return coloredList;
    }

    public YamlConfiguration getPurchase(){
        return purchase;
    }
}
