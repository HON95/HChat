package no.hon95.bukkit.hchat.command;

import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public abstract class AbstractCommandExecutor implements CommandExecutor {

	private final HChatPlugin gPlugin;
	private final String gCommand;

	public AbstractCommandExecutor(HChatPlugin plugin, String command) {
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

	protected final HChatPlugin getPlugin() {
		return gPlugin;
	}

	protected boolean hasPerm(CommandSender sender, String perm) {
		if (!sender.hasPermission(perm)) {
			sender.sendMessage("§cPermission denied!");
			return false;
		}
		return true;
	}

	abstract protected void onCommand(CommandSender sender, String[] args);
}
