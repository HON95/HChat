package no.hon95.bukkit.hchat.format;

import java.text.SimpleDateFormat;
import java.util.Date;

import no.hon95.bukkit.hchat.HChatPlugin;

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
	public String format(MessageType type, String format, CommandSender sender, CommandSender receiver, String message) {
		return ChatColor.translateAlternateColorCodes('&', super.format(type, format, sender, receiver, message));
	}

	protected String getValue(MessageType type, char c, CommandSender sender, CommandSender receiver, String message) {
		Player player = null;
		Player receiverPlayer = null;
		if (sender instanceof Player)
			player = (Player) sender;
		if (receiver instanceof Player)
			receiverPlayer = (Player) receiver;
		String percent = type == MessageType.CHAT ? "%%" : "%";
		String result = null;

		switch (c) {
		case '%':
			result = percent;
			break;
		case 'c':
			result = gPlugin.getChatManager().getChannel(sender).getName();
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
				result = (player.getFoodLevel() * 5) + percent;
			else
				result = "0" + percent;
			break;
		case 'g':
			result = gPlugin.getChatManager().getGroup(sender).getName();
			break;
		case 'G':
			result = gPlugin.getChatManager().getRealGroup(sender);
			break;
		case 'h':
			result = ""; //String.valueOf(100 * player.getHealth()/player.getMaxHealth()) + percent; //FIXME
			break;
		case 'H':
			result = ""; //Health bars [|||||] //FIXME
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
			else if (player != null)
				result = player.getDisplayName();
			else
				result = sender.getName();
			break;
		case 'N':
			result = sender.getName();
			break;
		case 'o':
			result = String.valueOf(Bukkit.getOnlinePlayers().length);
			break;
		case 'O':
			Player[] players = Bukkit.getOnlinePlayers();
			if (players.length > 0) {
				StringBuilder sb = new StringBuilder(players[0].getDisplayName());
				for (int i = 1; i < players.length; i++) {
					sb.append(ChatColor.RESET.toString());
					if (i == players.length - 1)
						sb.append(" & ");
					else
						sb.append(", ");
					sb.append(players[i].getDisplayName());
				}
				result = sb.toString();
			}
			break;
		case 'p':
			result = gPlugin.getChatManager().getGroup(sender).getPrefix();
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
			result = gPlugin.getChatManager().getGroup(sender).getSuffix();
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

	private static String cutDeathMessage(String name, String message) {
		return message.replace(name, "");
	}
}
