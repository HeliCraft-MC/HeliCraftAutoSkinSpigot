package org.ktilis.helicraftautoskin.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.ktilis.helicraftautoskin.HeliCraftAutoSkin;
import org.ktilis.helicraftautoskin.skins.Database;
import org.ktilis.helicraftautoskin.skins.Skin;
import org.ktilis.helicraftautoskin.skins.SkinProvider;

import java.io.IOException;
import java.net.URL;

public class SkinCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 0) {
            if(
                    !sender.hasPermission("hcas.reload")
                            && !sender.hasPermission("hcas.addSkin")
                            && !sender.hasPermission("hcas.setSkin")
                            && !sender.hasPermission("hcas.reloadSkin")
            ) {
                sender.sendMessage(ChatColor.RED+ HeliCraftAutoSkin.getInstance().getConfig().getString("noPermissionsMessage"));
                return true;
            }

            TextComponent message1 = new TextComponent("Команды, которые Вам доступны:");
            message1.setColor(ChatColor.AQUA);
            message1.setBold(true);
            sender.spigot().sendMessage(message1);
            if(sender.hasPermission("hcas.reload")) {
                sender.sendMessage(
                        ChatColor.YELLOW+"/hcas \n    "
                        + ChatColor.DARK_GRAY + "- "
                        + ChatColor.WHITE + "Перезагружает конфиг плагина"
                );
            }
            if(sender.hasPermission("hcas.addSkin")) {
                sender.sendMessage(
                        ChatColor.YELLOW+"/"+label+" add <название> <url скина(ТОЛЬКО png)> \n    "
                        + ChatColor.DARK_GRAY + "- "
                        + ChatColor.WHITE + "Добавляет скин с базу данных со свои именем."
                );
            }
            if(sender.hasPermission("hcas.setSkin")) {
                sender.sendMessage(
                        ChatColor.YELLOW+"/"+label+" set <название скина в базе данных> [игрок(необязательно)] \n    "
                        + ChatColor.DARK_GRAY + "- "
                        + ChatColor.WHITE + "Устанавливает временный скин Вам или игроку. Действует до перезахода."
                );
            }
            if(sender.hasPermission("hcas.reloadSkin")) {
                sender.sendMessage(
                        ChatColor.YELLOW+"/"+label+" reload \n    "
                                + ChatColor.DARK_GRAY + "- "
                                + ChatColor.WHITE + "Перезагрузить скин, который вы установили на сайте."
                );
            }
            return true;
        } else if(args[0].equalsIgnoreCase("add")) {
            if(!sender.hasPermission("hcas.addSkin")) return true;

            if(args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Какой-то из параметров не написан.");
                return true;
            }
            try {
                String skinName = args[1];
                String skinUrl = args[2];

                if(Database.isSkinExists(skinName)) {
                    sender.sendMessage("В базе данных уже есть скин с таким именем!");
                    return true;
                }
                Skin skin = SkinProvider.getSkin(skinUrl, skinName);
                if(!Database.addSkin(skin, SkinProvider.tryGetFileSize(new URL(skinUrl)), skinName)) {
                    sender.sendMessage(ChatColor.GREEN+"Скин успешно добавлен в базу данных!");
                } else {
                    sender.sendMessage(ChatColor.RED+"Ошибка добавления скина #2.");
                }
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED+"Ошибка добавления скина #1.");
                throw new RuntimeException(e);
            }
        } else if(args[0].equalsIgnoreCase("set")) {
            if(!sender.hasPermission("hcas.addSkin")) return true;

            Player target;
            if(args.length == 3) {
                Player p = Bukkit.getPlayer(args[2]);
                if (p != null) {
                    target = p;
                } else {
                    sender.sendMessage(ChatColor.RED+"Ошибка: данный игрок не онлайн.");
                    return true;
                }
            } else {
                target = (Player) sender;
            }
            String skinName = args[1];
            if(!Database.isSkinExists(skinName)) {
                sender.sendMessage(ChatColor.RED+"Данного скина не существует!");
                return true;
            }
            Skin skin = Database.getSkin(skinName);
            SkinProvider.setSkin(target, skin);
            sender.sendMessage(ChatColor.GREEN+"Временный скин установлен.");
            return true;
        } else if (args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("hcas.reloadSkin")) return true;
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(
                        ChatColor.RED + "Ошибка: данная команда доступна только игрокам!"
                );
                return true;
            }
            SkinProvider.updateSkin((Player) sender);
            sender.sendMessage(ChatColor.GREEN+"Скин успешно обновлён!");
        }
        return true;
    }
}
