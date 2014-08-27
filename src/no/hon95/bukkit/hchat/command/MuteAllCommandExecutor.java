package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.*;
import static org.bukkit.ChatColor.*;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.command.CommandSender;


public final class MuteAllCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_MUTEALL;

	public MuteAllCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_MUTEALL)) {
			if (getPlugin().getChatManager().isMuteAll()) {
				sender.sendMessage(RED + "Global mute is already activated.");
			} else {
				getPlugin().getChatManager().setMuteAll(true);
				sender.sendMessage(GREEN + "Global mute has been activated.");
				sender.sendMessage("Only players with the specific permission might talk.");
			}
		}
	}

}
