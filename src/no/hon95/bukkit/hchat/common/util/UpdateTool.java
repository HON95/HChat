package no.hon95.bukkit.hchat.common.util;

import static org.bukkit.ChatColor.*;

import java.io.File;

import no.hon95.bukkit.hchat.common.gravitydevelopment.Updater;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public final class UpdateTool {

	private final JavaPlugin gPlugin;
	private final int gApiKey;
	private final String gNotifPerm;
	private final File gFolder;

	public UpdateTool(JavaPlugin plugin, File folder, int apiKey, String notificationPermission) {
		gPlugin = plugin;
		gFolder = folder;
		gApiKey = apiKey;
		gNotifPerm = notificationPermission;
	}

	public void checkForUpdates(boolean update, CommandSender commandSender) {
		Updater.UpdateType type;
		if (update)
			type = Updater.UpdateType.DEFAULT;
		else
			type = Updater.UpdateType.NO_DOWNLOAD;
		Updater updater = new Updater(gPlugin, gApiKey, gFolder, type, false);
		switch (updater.getResult()) {
		case SUCCESS:
			respond(commandSender, "Successfully downloaded an update: " + updater.getLatestName(), true);
			break;
		case UPDATE_AVAILABLE:
			respond(commandSender, "An update is available: " + updater.getLatestName(), true);
			break;
		case NO_UPDATE:
			respond(commandSender, "No updates available.", false);
			break;
		default:
			respond(commandSender, "Failed to check for updates.", true);
			break;
		}
	}

	private void respond(CommandSender commandSender, String message, boolean announce) {
		if (announce) {
			if (commandSender == null || commandSender != Bukkit.getConsoleSender())
				gPlugin.getLogger().info(message);
			if (commandSender == null) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.hasPermission(gNotifPerm))
						player.sendMessage(DARK_GRAY + "[" + BLUE + gPlugin.getName() + DARK_GRAY + "] " + RESET + message);
				}
			}
		}
		if (commandSender != null)
			commandSender.sendMessage(DARK_GRAY + "[" + BLUE + gPlugin.getName() + DARK_GRAY + "] " + RESET + message);
	}
}
