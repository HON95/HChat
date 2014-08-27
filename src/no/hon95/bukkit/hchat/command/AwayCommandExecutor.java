package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.*;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class AwayCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_AWAY;

	public AwayCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_AWAY) && isPlayer(sender)) {
			getPlugin().getChatManager().setPlayerAway(true, (Player) sender);
		}
	}

}
