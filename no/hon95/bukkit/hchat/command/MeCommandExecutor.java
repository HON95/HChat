package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_ME;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_IMMUTABLE;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;
import no.hon95.bukkit.hchat.format.Formatter.MessageType;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class MeCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_ME;

	public MeCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_ME)) {
			if (getPlugin().getChatManager().isMuteAll() && !sender.hasPermission(PERM_IMMUTABLE)) {
				sender.sendMessage("�cGlobal mute is active and you are not allowed to talk.");
			} else if (sender instanceof Player && getPlugin().getChatManager().isPlayerMutedGlobally(((Player) sender).getUniqueId())) {
				sender.sendMessage("�cYou may not use this command because you are muted globally.");
			} else if (sender instanceof Player && !getPlugin().getChatManager().getGroup(sender).canChat()) {
				sender.sendMessage("�cYou may not use this command because you are not allowed to chat.");
			} else {
				if (args.length == 0) {
					sender.sendMessage("�6Syntax: �r/me <action>");
				} else {
					String action = StringUtils.join(args, " ");
					String message;
					if (getPlugin().getChatManager().getFormatMe())
						message = getPlugin().getFormatManager().formatString(MessageType.ME, sender, null, action);
					else
						message = String.format("* %s %s", sender.getName(), action);
					Bukkit.broadcastMessage(message);
				}
			}
		}
	}

}