package org.ktilis.helicraftautoskin.skins.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.ktilis.helicraftautoskin.skins.SkinProvider;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        SkinProvider.updateSkin(player);
    }
}
