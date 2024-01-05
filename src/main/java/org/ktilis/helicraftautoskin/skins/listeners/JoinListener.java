package org.ktilis.helicraftautoskin.skins.listeners;

import net.pinger.disguise.skin.Skin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.ktilis.helicraftautoskin.HeliCraftAutoSkin;
import org.ktilis.helicraftautoskin.skins.SkinsManager;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        String skinURL = HeliCraftAutoSkin.getInstance().getConfig().getString("skins-url").replaceAll("<nick>", player.getDisplayName());
        Skin skin = SkinsManager.getSkin(skinURL, player.getDisplayName());
        SkinsManager.setSkin(player, skin);
    }
}
