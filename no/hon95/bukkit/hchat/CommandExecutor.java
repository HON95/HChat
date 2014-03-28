package no.hon95.bukkit.hchat;

import static no.hon95.bukkit.hchat.HChatPermission.PERM_COMMAND;
import static no.hon95.bukkit.hchat.HChatPermission.PERM_RELOAD;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public final class CommandExecutor implements org.bukkit.command.CommandExecutor {

	private static final String[] COMMAND_MESSAGE = { "",
			RED + "            >> " + BLUE + BOLD + "DynChat" + RED + " <<",
			GREEN + "================================================",
			GREEN + " * " + GRAY + "Chat formatter by HON95",
			GREEN + " * " + GRAY + "Commands:",
			GREEN + " * " + GOLD + ITALIC + "/dynchat reload" + RESET + GRAY + " -> Reload files and update player info",
			GREEN + " * ",
	};

	private final HChatPlugin gPlugin;

	public CommandExecutor(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("hchat")) {
			if (!sender.hasPermission(PERM_COMMAND)) {
				sender.sendMessage(RED + "Permission denied!");
				return false;
			}
			if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
				if (!sender.hasPermission(PERM_RELOAD)) {
					sender.sendMessage(RED + "Permission denied!");
					return false;
				}
				gPlugin.getConfigManager().load();
				gPlugin.getInfoManager().update();
				sender.sendMessage(GREEN + "DynChat reloaded!");
			} else {
				sender.sendMessage(COMMAND_MESSAGE);
			}

			return true;
		}

		return false;
	}

}
