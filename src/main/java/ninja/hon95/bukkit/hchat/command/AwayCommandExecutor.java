package ninja.hon95.bukkit.hchat.command;

import static ninja.hon95.bukkit.hchat.HChatPermissions.*;
import ninja.hon95.bukkit.hchat.HChatCommands;
import ninja.hon95.bukkit.hchat.HChatPlugin;
import ninja.hon95.bukkit.hcommonlib.AbstractCommandExecutor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AwayCommandExecutor extends AbstractCommandExecutor<HChatPlugin> {

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
