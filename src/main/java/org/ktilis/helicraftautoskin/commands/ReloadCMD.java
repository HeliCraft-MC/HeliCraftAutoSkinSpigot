package org.ktilis.helicraftautoskin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ktilis.helicraftautoskin.HeliCraftAutoSkin;

public class ReloadCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        HeliCraftAutoSkin.getInstance().reloadConfig();
        sender.sendMessage("Success!");
        return true;
    }
}
