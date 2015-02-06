package ninja.hon95.bukkit.hchat.format;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import ninja.hon95.bukkit.hchat.Channel;
import ninja.hon95.bukkit.hchat.Group;
import ninja.hon95.bukkit.hchat.HChatPlugin;
import ninja.hon95.bukkit.hchat.format.FormatManager.MessageType;
import ninja.hon95.bukkit.hcommonlib.CompatUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class DefaultFormatter extends Formatter {

	private static final String SENDER_NAME_VAR = "%1$s";
	private static final String SENDER_MESSAGE_VAR = "%2$s";

	private final HChatPlugin gPlugin;

	public DefaultFormatter(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public String formatEarly(MessageType type, String format, CommandSender sender, CommandSender receiver, String message, Group group, Channel channel) {
		return format.replace(SENDER_NAME_VAR, "%n").replace(SENDER_MESSAGE_VAR, "%m");
	}

	@Override
	public String getCodeValue(MessageType type, char c, CommandSender sender, CommandSender receiver, String message, Group group, Channel channel) {
		Player player = null;
		Player receiverPlayer = null;
		if (sender instanceof Player)
			player = (Player) sender;
		if (receiver instanceof Player)
			receiverPlayer = (Player) receiver;
		String percentSymbol = type == MessageType.CHAT ? "%%" : "%";
		String result = null;

		switch (c) {
		case '%':
			result = percentSymbol;
			break;
		case '$':
			result = "$";
			break;
		case 'a':
			if (player != null) {
				if (gPlugin.getChatManager().isPlayerAway(player.getUniqueId()))
					result = group.getAwayLongTag();
			}
			break;
		case 'A':
			if (player != null) {
				if (gPlugin.getChatManager().isPlayerAway(player.getUniqueId()))
					result = group.getAwayShortTag();
			}
			break;
		case 'c':
			result = channel.getName();
			break;
		case 'd':
			if (player != null)
				result = gPlugin.getVaultEconomy().getCurrencyString(player);
			break;
		case 'D':
			if (player != null) {
				double currency = gPlugin.getVaultEconomy().getCurrency(player);
				result = String.format("%.0f", currency);
			}
			break;
		case 'f':
			if (player != null)
				result = (player.getFoodLevel() * 5) + percentSymbol;
			else
				result = "0" + percentSymbol;
			break;
		case 'g':
			result = group.getName();
			break;
		case 'G':
			result = gPlugin.getChatManager().getRealGroup(sender);
			break;
		case 'h':
			result = ""; // FIXME health percentage
			break;
		case 'H':
			result = ""; // FIXME health bars
			break;
		case 'i':
			result = ""; // TODO group meta info
			break;
		case 'I':
			result = ""; // TODO global meta info
			break;
		case 'l':
			if (player != null)
				result = String.valueOf(player.getLevel());
			break;
		case 'm':
			if (type == MessageType.CHAT) {
				result = SENDER_MESSAGE_VAR;
			} else if (message != null) {
				if (type == MessageType.DEATH)
					result = cutDeathMessage(sender.getName(), message);
				else
					result = message;
			}
			break;
		case 'M':
			if (player != null)
				result = player.getGameMode().name();
			break;
		case 'n':
			if (type == MessageType.CHAT)
				result = SENDER_NAME_VAR;
			else if (type == MessageType.NAME)
				result = "";
			else if (player != null)
				result = player.getDisplayName();
			else
				result = sender.getName();
			break;
		case 'N':
			result = sender.getName();
			break;
		case 'o':
			result = String.valueOf(CompatUtil.getOnlinePlayers().size());
			break;
		case 'O':
			StringBuilder builder = new StringBuilder();
			Collection<? extends Player> players = CompatUtil.getOnlinePlayers();
			int playersFound = 0;
			for (Player currentPlayer : players) {
				playersFound++;
				if (playersFound > 1) {
					builder.append(ChatColor.RESET);
					if (playersFound == players.size())
						builder.append(" & ");
					else
						builder.append(", ");
				}
				builder.append(currentPlayer.getDisplayName());
			}
			result = builder.toString();
			break;
		case 'p':
			result = group.getPrefix();
			break;
		case 'r':
			if (receiver != null) {
				if (receiverPlayer != null)
					result = receiverPlayer.getDisplayName();
				else
					result = receiver.getName();
			}
			break;
		case 'R':
			if (receiver != null)
				result = receiver.getName();
			break;
		case 's':
			result = group.getSuffix();
			break;
		case 'S':
			result = Bukkit.getServerName();
			break;
		case 't':
			result = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
			break;
		case 'T':
			result = new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()));
			break;
		case 'v':
			result = Bukkit.getBukkitVersion();
			break;
		case 'w':
			if (player != null)
				result = player.getLocation().getWorld().getName();
			break;
		case 'x':
			if (player != null)
				result = String.valueOf(player.getLocation().getBlockX());
			break;
		case 'y':
			if (player != null)
				result = String.valueOf(player.getLocation().getBlockY());
			break;
		case 'z':
			if (player != null)
				result = String.valueOf(player.getLocation().getBlockZ());
			break;
		default:
			break;
		}

		if (result == null)
			result = "";
		return result;
	}

	@Override
	public String formatLate(MessageType type, String format, CommandSender sender, CommandSender receiver, String message, Group group, Channel channel) {
		return ChatColor.translateAlternateColorCodes('&', format);
	}

	private static String cutDeathMessage(String name, String message) {
		return message.replace(name, "");
	}
}
