package no.hon95.bukkit.hchat;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import no.hon95.bukkit.hchat.util.gravitydevelopment.Updater;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_NOTIFY_UPDATE;


public final class UpdateManager {

	private final HChatPlugin gPlugin;
	private final int gApiKey;
	private final File gFolder;

	public UpdateManager(HChatPlugin plugin, int apiKey, File folder) {
		gPlugin = plugin;
		gApiKey = apiKey;
		gFolder = folder;
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
			respond(commandSender, "Successfully downloaded an update: " + updater.getLatestName(), false);
			break;
		case UPDATE_AVAILABLE:
			respond(commandSender, "An update is available: " + updater.getLatestName(), false);
			break;
		case NO_UPDATE:
			respond(commandSender, "No updates available.", true);
			break;
		default:
			respond(commandSender, "Failed to check for updates.", false);
			break;
		}
	}

	private void respond(CommandSender commandSender, String message, boolean silent) {
		if (!silent) {
			gPlugin.getLogger().info(message);
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission(PERM_NOTIFY_UPDATE))
					player.sendMessage("§7[hChat] " + message);
			}
		}
		if (commandSender != null && (commandSender != Bukkit.getConsoleSender() || silent))
			commandSender.sendMessage("§7[hChat] " + message);
	}
}
