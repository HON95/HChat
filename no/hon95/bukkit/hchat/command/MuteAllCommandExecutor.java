package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_MUTEALL;
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
				sender.sendMessage("§cGlobal mute is already activated.");
			} else {
				getPlugin().getChatManager().setMuteAll(true);
				sender.sendMessage("§aGlobal mute has been activated.");
				sender.sendMessage("Only players with the specific permission might talk.");
			}
		}
	}

}
