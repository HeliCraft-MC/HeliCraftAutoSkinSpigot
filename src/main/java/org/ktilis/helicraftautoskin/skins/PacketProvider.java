package org.ktilis.helicraftautoskin.skins;

import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketProvider {
    private static void sendPacket(ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }
    public static void setSkin(Player player, Skin skin) {
        ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer
                .getGameProfile()
                .getProperties()
                .removeAll("textures");
        entityPlayer
                .getGameProfile()
                .getProperties()
                .put("textures", new Property(
                        "textures",
                        skin.getValue(),
                        skin.getSignature()
                ));
    }
    public static void refreshPlayer(Player player) {
        ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        // Slowly getting from object to object till we get what is needed for
        // the respawn packet
        ServerLevel world = entityPlayer.serverLevel();

        CommonPlayerSpawnInfo spawnInfo = entityPlayer.createCommonSpawnInfo(world);
        ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(
                spawnInfo,
                ClientboundRespawnPacket.KEEP_ALL_DATA
        );

        sendPacket(entityPlayer, respawn);

        sendPacket(entityPlayer, new ClientboundPlayerInfoRemovePacket(List.of(player.getUniqueId())));
        sendPacket(entityPlayer, ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(entityPlayer)));

        entityPlayer.onUpdateAbilities();

        entityPlayer.connection.teleport(player.getLocation());

        // Send health, food, experience (food is sent together with health)
        entityPlayer.resetSentInfo();

        PlayerList playerList = entityPlayer.server.getPlayerList();
        playerList.sendPlayerPermissionLevel(entityPlayer);
        playerList.sendLevelInfo(entityPlayer, world);
        playerList.sendAllPlayerInfo(entityPlayer);

        // Resend their effects
        for (MobEffectInstance effect : entityPlayer.getActiveEffects()) {
            sendPacket(entityPlayer, new ClientboundUpdateMobEffectPacket(entityPlayer.getId(), effect, false));
        }
    }
}
