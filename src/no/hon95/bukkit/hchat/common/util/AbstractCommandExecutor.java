package no.hon95.bukkit.hchat.common.util;

import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractCommandExecutor<P extends JavaPlugin> implements CommandExecutor {

	private final P gPlugin;
	private final String gCommand;

	public AbstractCommandExecutor(P plugin, String command) {
		gPlugin = plugin;
		gCommand = command;
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase(gCommand)) {
			onCommand(sender, args);
			return true;
		}
		return false;
	}

	protected final P getPlugin() {
		return gPlugin;
	}

	protected static final boolean hasPerm(CommandSender sender, String perm) {
		if (!sender.hasPermission(perm)) {
			sender.sendMessage(RED + "Permission denied!");
			return false;
		}
		return true;
	}

	protected static final boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			return true;
		} else {
			sender.sendMessage("Only players may use this command.");
			return false;
		}
	}

	protected static final String[] translateColorCodes(String[] array) {
		for (int i = 0; i < array.length; i++)
			array[i] = translateColorCodes(array[i]);
		return array;
	}

	protected static final String translateColorCodes(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	abstract protected void onCommand(CommandSender sender, String[] args);
}
