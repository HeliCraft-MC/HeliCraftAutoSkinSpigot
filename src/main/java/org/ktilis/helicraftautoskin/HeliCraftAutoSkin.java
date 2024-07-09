package org.ktilis.helicraftautoskin;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.ktilis.helicraftautoskin.commands.ReloadCMD;
import org.ktilis.helicraftautoskin.commands.SkinCMD;
import org.ktilis.helicraftautoskin.skins.Database;
import org.ktilis.helicraftautoskin.skins.listeners.JoinListener;
import org.ktilis.helicraftautoskin.skins.listeners.UpdateListener;

import java.util.Objects;

public final class HeliCraftAutoSkin extends JavaPlugin {
    @Getter
    private static HeliCraftAutoSkin instance;

    private static UpdateListener updateListenerInstance;

    public static final String MINESKIN_REQUEST_URL = "https://api.mineskin.org/generate/url";

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if(!getConfig().getBoolean("enabled")) return;

        Database.start();

        Objects.requireNonNull(getCommand("hcas")).setExecutor(new ReloadCMD());
        Objects.requireNonNull(getCommand("skin")).setExecutor(new SkinCMD());

        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        if(getConfig().getBoolean("updateServer.enabled")) {
            getLogger().info("Starting \"update server\"...");
            updateListenerInstance = new UpdateListener();
            updateListenerInstance.init((!Objects.isNull(getConfig().getInt("updateServer.port"))) ? getConfig().getInt("updateServer.port") : 3000);
        }

        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        //if(DisguiseAPI.getProvider() == null) return;

        if(updateListenerInstance != null) updateListenerInstance.stop();

        Database.stop();
    }

}
