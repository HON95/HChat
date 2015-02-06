package ninja.hon95.bukkit.hchat.command;

import static ninja.hon95.bukkit.hchat.HChatPermissions.*;
import static org.bukkit.ChatColor.*;
import ninja.hon95.bukkit.hchat.HChatCommands;
import ninja.hon95.bukkit.hchat.HChatPlugin;
import ninja.hon95.bukkit.hchat.format.FormatManager.MessageType;
import ninja.hon95.bukkit.hcommonlib.AbstractCommandExecutor;
import ninja.hon95.bukkit.hcommonlib.CompatUtil;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TellCommandExecutor extends AbstractCommandExecutor<HChatPlugin> {

	private static final String COMMAND = HChatCommands.CMD_TELL;

	public TellCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_TELL)) {
			if (getPlugin().getChatManager().isMuteAll() && !sender.hasPermission(PERM_IMMUTABLE)) {
				sender.sendMessage(RED + "Global mute is active and you are not allowed to talk.");
			} else if (sender instanceof Player && getPlugin().getChatManager().isPlayerMutedGlobally(((Player) sender).getUniqueId())) {
				sender.sendMessage(RED + "You may not use this command because you are muted globally.");
			} else if (sender instanceof Player && !getPlugin().getChatManager().getGroup(sender).getCanChat()) {
				sender.sendMessage(RED + "You may not use this command because you are not allowed to chat.");
			} else {
				if (args.length < 2) {
					sender.sendMessage(GOLD + "Syntax: " + RESET + "/tell <player> <message>");
				} else {
					CommandSender receiver;
					if (args[0].equalsIgnoreCase("console"))
						receiver = Bukkit.getConsoleSender();
					else {
						receiver = CompatUtil.getLocalPlayer(args[0]);
						if (receiver == null) {
							sender.sendMessage(RED + "Player not found: " + args[0]);
							return;
						}
					}

					if (sender instanceof Player && receiver instanceof Player && getPlugin().getChatManager().isPlayerMutedIndividually(((Player) receiver).getUniqueId(), ((Player) sender).getUniqueId())) {
						sender.sendMessage(RED + "You may not use this command because that player has muted you.");
					}

					String message = StringUtils.join(args, ' ', 1, args.length);
					String senderText;
					String receiverText;
					String spyText;
					if (getPlugin().getChatManager().getFormatTell()) {
						senderText = getPlugin().getFormatManager().format(MessageType.TELL_SENDER, sender, receiver, message);
						receiverText = getPlugin().getFormatManager().format(MessageType.TELL_RECEIVER, sender, receiver, message);
						spyText = getPlugin().getFormatManager().format(MessageType.TELL_SPY, sender, receiver, message);
					}
					else {
						senderText = String.format("[%s->%s] %s", sender.getName(), receiver.getName(), message);
						receiverText = String.format(GRAY + "% whispers %s", sender.getName(), receiver.getName(), message);
						spyText = String.format("[%s->%s] %s", sender.getName(), receiver.getName(), message);
					}
					if (getPlugin().getChatManager().getGroup(sender).getShowPersonalMessages()) {
						Bukkit.getLogger().info("PM: [" + sender.getName() + "->" + receiver.getName() + "] " + message);
						for (Player player : CompatUtil.getOnlinePlayers()) {
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
