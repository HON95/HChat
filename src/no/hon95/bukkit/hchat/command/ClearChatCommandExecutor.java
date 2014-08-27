package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.*;
import static org.bukkit.ChatColor.*;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class ClearChatCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_CLEAR_CHAT;
	private static final int CLEAR_LENGTH = 100;

	public ClearChatCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CLEAR)) {
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

}
