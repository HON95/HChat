package no.hon95.bukkit.hchat;

import static no.hon95.bukkit.hchat.HChatCommands.CMD_CLEAR_CHAT;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_COLORS;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_HCHAT;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_ME;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_TELL;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CLEAR;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_COLORS;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_ME;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_RELOAD;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_UNCLEARABLE;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

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
	private static final String DEFAULT_ME_FORMAT = "* %s %s";
	private static final String DEFAULT_TELL_SENDER_FORMAT = "[%s->%s] %s";
	private static final String DEFAULT_TELL_RECEIVER_FORMAT = GRAY + "% whispers %s";

	private final HChatPlugin gPlugin;

	public HCommandExecutor(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase(CMD_HCHAT))
			cmdHChat(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_CLEAR_CHAT))
			cmdClearChat(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_COLORS))
			cmdColors(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_ME))
			cmdMe(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_TELL))
			cmdTell(sender, args);
		else
			return false;
		return true;
	}

	private void cmdHChat(CommandSender sender, String[] args) {
		if (nhasPerm(sender, PERM_COMMAND))
			return;

		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission(PERM_COMMAND_RELOAD)) {
				sender.sendMessage(RED + "Permission denied!");
				return;
			}
			gPlugin.getConfigManager().load();
			gPlugin.getChatManager().reload();
			sender.sendMessage(GREEN + "hChat reloaded!");
		} else {
			sender.sendMessage(COMMAND_MESSAGE);
		}
	}

	private void cmdClearChat(CommandSender sender, String[] args) {
		if (nhasPerm(sender, PERM_COMMAND_CLEAR))
			return;

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

	private void cmdColors(CommandSender sender, String[] args) {
		if (nhasPerm(sender, PERM_COMMAND_COLORS))
			return;

		sender.sendMessage(COLOR_MESSAGE);
	}

	private void cmdMe(CommandSender sender, String[] args) {
		if (nhasPerm(sender, PERM_COMMAND_ME))
			return;

		if (args.length == 0) {
			sender.sendMessage(GOLD + "Syntax: " + RESET + "/me <action>");
		} else {
			String action = StringUtils.join(args, " ");
			String message;
			if (gPlugin.getChatManager().getFormatMe())
				message = gPlugin.getChatManager().formatMe(sender, action);
			else
				message = String.format(DEFAULT_ME_FORMAT, sender.getName(), action);
			Bukkit.broadcastMessage(message);
		}
	}

	private void cmdTell(CommandSender sender, String[] args) {
		if (nhasPerm(sender, PERM_COMMAND_ME))
			return;

		if (args.length == 0) {
			sender.sendMessage(GOLD + "Syntax: " + RESET + "/tell <player> <message>");
		} else {
			CommandSender receiver;
			if (args[0].equalsIgnoreCase("console"))
				receiver = Bukkit.getConsoleSender();
			else {
				receiver = getPlayer(args[0]);
				if (receiver == null) {
					sender.sendMessage(RED + "Player not found: " + args[0]);
					return;
				}
			}
			String message = StringUtils.join(args, " ");
			String senderText;
			String receiverText;
			if (gPlugin.getChatManager().getFormatTell()) {
				senderText = gPlugin.getChatManager().formatTellSender(sender, message, receiver);
				receiverText = gPlugin.getChatManager().formatTellReceiver(sender, message, receiver);
			}
			else {
				senderText = String.format(DEFAULT_TELL_SENDER_FORMAT, sender.getName(), receiver.getName(), message);
				receiverText = String.format(DEFAULT_TELL_RECEIVER_FORMAT, sender.getName(), receiver.getName(), message);
			}
			if (gPlugin.getChatManager().getGroup(sender).showPersonalMessages)
				Bukkit.getLogger().info("PM: [" + sender.getName() + "->" + receiver.getName() + "] " + message);
			sender.sendMessage(senderText);
			receiver.sendMessage(receiverText);
		}
	}

	private boolean nhasPerm(CommandSender sender, String perm) {
		if (!sender.hasPermission(perm)) {
			sender.sendMessage(RED + "Permission denied!");
			return true;
		}
		return false;
	}

	private Player getPlayer(String name) {
		try {
			return Bukkit.getPlayer(UUIDUtil.getUUID(name));
		} catch (NoSuchMethodError err) {
			// Bukkit <= 1.7.3 support //
			@SuppressWarnings("deprecation")
			Player player = Bukkit.getPlayerExact(name);
			return player;
		}
	}
}
