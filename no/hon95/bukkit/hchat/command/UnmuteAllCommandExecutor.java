package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_UNMUTEALL;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.command.CommandSender;


public final class UnmuteAllCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_UNMUTEALL;

	public UnmuteAllCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_UNMUTEALL)) {
			if (!getPlugin().getChatManager().isMuteAll()) {
				sender.sendMessage("§cGlobal mute is currently deactivated.");
			} else {
				getPlugin().getChatManager().setMuteAll(false);
				sender.sendMessage("§aGlobal mute has been deactivated.");
			}
		}
	}

}
