package main;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import command.InGameCommand;
import handler.OnBlockFromTo;
import handler.OnClick;
import handler.OnJoin;
import handler.OnPurchaseUpgrade;
import me.lucko.luckperms.api.LuckPermsApi;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import util.ConfigManager;

import java.io.File;

public class GoodByeLava extends JavaPlugin {
    boolean usingGUI = false;

    private ConfigManager configManager;
    private InGameCommand inGameCommand;
    private OnPurchaseUpgrade onPurchaseUpgrade;
    private OnBlockFromTo onBlockFromTo;
    private OnClick onClick;
    private GoodByeLava instance;
    private LuckPermsApi permissionAPI;
    private Economy economy;

    @Override
    public void onEnable(){
        Bukkit.getLogger().info("GoodByeLava plugin is enabled!");

        instance = this;
        inGameCommand = new InGameCommand(instance);
        configManager = new ConfigManager(instance);
        onPurchaseUpgrade = new OnPurchaseUpgrade(instance);
        onBlockFromTo = new OnBlockFromTo(instance);
        onClick = new OnClick(instance);

        reloadYaml();
        registerEvents();
        registerCommands();
        initializeLuckPerms();
        initializeVault();

        usingGUI = configManager.getPurchase().getBoolean("setting.enabled");

    }

    public void registerEvents(){
        Bukkit.getPluginManager().registerEvents(onBlockFromTo, instance);
        Bukkit.getPluginManager().registerEvents(onPurchaseUpgrade, instance);
        Bukkit.getPluginManager().registerEvents(onClick, instance);
    }

    public void registerCommands(){
        getCommand("gbl").setExecutor(inGameCommand);
    }

    public void initializeLuckPerms(){
        RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
        if(provider != null){
            permissionAPI = provider.getProvider();
        } else{
            Bukkit.getLogger().severe("LuckPerms is not detected!");
        }
    }

    public void initializeVault(){
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        } else{
            Bukkit.getLogger().severe("Vault or Economy plugin is nor detected!");
        }
    }

    public boolean getUsingHGUI(){
        return usingGUI;
    }

    @Override
    public void onDisable(){
        Bukkit.getLogger().info("GoodByeLava plugin is disabled!");
    }

    public Economy getEconomy() {
        return economy;
    }

    public LuckPermsApi getPermissionAPI() {
        return permissionAPI;
    }

    public ConfigManager getConfigManager(){
        return configManager;
    }

    public void reloadYaml(){
        configManager = new ConfigManager(instance);
    }
}
