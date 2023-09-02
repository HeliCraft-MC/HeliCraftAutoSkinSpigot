package org.ktilis.helicraftautoskin;

import net.pinger.disguise.DisguiseAPI;
import org.bukkit.plugin.java.JavaPlugin;
import org.ktilis.helicraftautoskin.commands.ReloadCMD;
import org.ktilis.helicraftautoskin.skins.Database;
import org.ktilis.helicraftautoskin.commands.SkinCMD;
import org.ktilis.helicraftautoskin.skins.listeners.JoinListener;
import org.ktilis.helicraftautoskin.skins.listeners.UpdateListener;

import java.util.Objects;

public final class HeliCraftAutoSkin extends JavaPlugin {


    private static HeliCraftAutoSkin instance;
    public static final String MineSkinRequestUrl = "https://api.mineskin.org/generate/url";

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if(!getConfig().getBoolean("enabled")) return;
        if (DisguiseAPI.getProvider() == null) {
            getLogger().info("Failed to find the provider for this version");
            getLogger().info("Disabling...");

            // Disable the plugin
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        getCommand("hcas").setExecutor(new ReloadCMD());

        Database.start();
        getCommand("skin").setExecutor(new SkinCMD());
        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        if(getConfig().getBoolean("updateServer.enabled")) {
            getLogger().info("Starting \"update server\"...");
            UpdateListener.init((!Objects.isNull(getConfig().getInt("updateServer.port"))) ? getConfig().getInt("updateServer.port") : 3000);
        }

        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        if(DisguiseAPI.getProvider() == null) return;

        Database.stop();
    }

    public static HeliCraftAutoSkin getInstance() {
        return instance;
    }

}
