package no.hon95.bukkit.hchat;

import static no.hon95.bukkit.hchat.HChatPermission.*;
import static org.bukkit.ChatColor.*;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class HCommandExecutor implements org.bukkit.command.CommandExecutor {

	private static final String[] COMMAND_MESSAGE = { "",
			RED + "            >> " + BLUE + BOLD + "hChat" + RED + " <<",
			GREEN + "================================================",
			GREEN + " * " + GRAY + "Chat formatter by HON95",
			GREEN + " * " + GRAY + "Commands:",
			GREEN + " * " + GOLD + ITALIC + "/hchat reload" + RESET + GRAY + " -> Reload files and update player info",
			GREEN + " * ",
	};
	private static final String[] COLOR_MESSAGE = { GOLD + "Color codes:",
			"§00 §r§11 §r§11 §r§11 §r§11 §r§11 §r§11 §r§11 §r§11 §r§11 §r§11 §r§11 §r§11 §r§11 §r§11 §r§11",
			GOLD + "Formatting codes:",
			"k §kabc     §rl §labc     §rm §mabc     §rn §nabc     §ro §oabc     §rr §rabc",
			"To use the code in chat or other places, write '&<code>', where <code> is the code." };
	private static final int CLEAR_LENGTH = 100;

	private final HChatPlugin gPlugin;

	public HCommandExecutor(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("hchat")) {
			if (!sender.hasPermission(PERM_COMMAND)) {
				sender.sendMessage(RED + "Permission denied!");
				return true;
			}
			if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
				if (!sender.hasPermission(PERM_COMMAND_RELOAD)) {
					sender.sendMessage(RED + "Permission denied!");
					return true;
				}
				gPlugin.getConfigManager().load();
				gPlugin.getChatManager().reload();
				sender.sendMessage(GREEN + "hChat reloaded!");
			} else {
				sender.sendMessage(COMMAND_MESSAGE);
			}
		} else if (cmd.getName().equalsIgnoreCase("clearchat")) {
			if (!sender.hasPermission(PERM_COMMAND_CLEAR)) {
				sender.sendMessage(RED + "Permission denied!");
				return true;
			}
			clearChat(sender);
		} else if (cmd.getName().equalsIgnoreCase("colors")) {
			if (!sender.hasPermission(PERM_COMMAND_COLORS)) {
				sender.sendMessage(RED + "Permission denied!");
				return true;
			}
			sender.sendMessage(COLOR_MESSAGE);
		} else if (cmd.getName().equalsIgnoreCase("me")) {
			if (!sender.hasPermission(PERM_COMMAND_ME)) {
				sender.sendMessage(RED + "Permission denied!");
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command.");
				return true;
			}
			Player player = (Player) sender;
			if (args.length == 0) {
				player.sendMessage(GOLD + "Syntax: " + RESET + "/me <action>");
			} else {
				String action = StringUtils.join(args, " ");
				String message;
				if (gPlugin.getChatManager().getFormatMe())
					message = gPlugin.getChatManager().formatMe(player, action);
				else
					message = "* " + player.getName() + " " + action;
				Bukkit.broadcastMessage(message);
			}
		} else {
			return false;
		}

		return true;
	}

	private void clearChat(CommandSender sender) {
		String name = (sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName();
		String[] message = new String[CLEAR_LENGTH];
		for (int i = 0; i < CLEAR_LENGTH; i++)
			message[i] = "";
		String[] clearedBy = new String[3];
		clearedBy[0] = "";
		clearedBy[1] = DARK_GRAY + "    [" + GRAY + "Chat has been cleared by " + name + DARK_GRAY + "]";
		clearedBy[2] = "";

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.hasPermission(PERM_UNCLEARABLE))
				player.sendMessage(message);
			player.sendMessage(clearedBy);
		}
	}
}
