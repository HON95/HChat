package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_TELL;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_IMMUTABLE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_SPY;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;
import no.hon95.bukkit.hchat.format.Formatter.MessageType;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class TellCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_TELL;

	public TellCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_TELL)) {
			if (getPlugin().getChatManager().isMuteAll() && !sender.hasPermission(PERM_IMMUTABLE)) {
				sender.sendMessage("�cGlobal mute is active and you are not allowed to talk.");
			} else if (sender instanceof Player && getPlugin().getChatManager().isPlayerMutedGlobally(((Player) sender).getUniqueId())) {
				sender.sendMessage("�cYou may not use this command because you are muted globally.");
			} else if (sender instanceof Player && !getPlugin().getChatManager().getGroup(sender).canChat()) {
				sender.sendMessage("�cYou may not use this command because you are not allowed to chat.");
			} else {
				if (args.length < 2) {
					sender.sendMessage("�6Syntax: �r/tell <player> <message>");
				} else {
					CommandSender receiver;
					if (args[0].equalsIgnoreCase("console"))
						receiver = Bukkit.getConsoleSender();
					else {
						receiver = Bukkit.getPlayer(getPlugin().getPlayerUuid(args[0], false));
						if (receiver == null) {
							sender.sendMessage("�cPlayer not found: " + args[0]);
							return;
						}
					}

					if (sender instanceof Player && receiver instanceof Player && getPlugin().getChatManager().isPlayerMutedIndividually(((Player) receiver).getUniqueId(), ((Player) sender).getUniqueId())) {
						sender.sendMessage("�cYou may not use this command because that player has muted you.");
					}

					String message = StringUtils.join(args, ' ', 1, args.length);
					String senderText;
					String receiverText;
					String spyText;
					if (getPlugin().getChatManager().getFormatTell()) {
						senderText = getPlugin().getFormatManager().formatString(MessageType.TELL_SENDER, sender, receiver, message);
						receiverText = getPlugin().getFormatManager().formatString(MessageType.TELL_RECEIVER, sender, receiver, message);
						spyText = getPlugin().getFormatManager().formatString(MessageType.TELL_SPY, sender, receiver, message);
					}
					else {
						senderText = String.format("[%s->%s] %s", sender.getName(), receiver.getName(), message);
						receiverText = String.format("�7% whispers %s", sender.getName(), receiver.getName(), message);
						spyText = String.format("[%s->%s] %s", sender.getName(), receiver.getName(), message);
					}
					if (getPlugin().getChatManager().getGroup(sender).showPersonalMessages()) {
						Bukkit.getLogger().info("PM: [" + sender.getName() + "->" + receiver.getName() + "] " + message);
						for (Player player : Bukkit.getOnlinePlayers()) {
							if (player != sender && player != receiver && player.hasPermission(PERM_SPY))
								player.sendMessage(spyText);
						}
					}
					if (sender != Bukkit.getConsoleSender())
						sender.sendMessage(senderText);
					receiver.sendMessage(receiverText);
				}
			}
		}
	}

}