package no.hon95.bukkit.hchat.format;

import java.text.SimpleDateFormat;
import java.util.Date;

import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class DefaultFormatter extends Formatter {

	private final HChatPlugin gPlugin;

	public DefaultFormatter(HChatPlugin plugin) {
		gPlugin = plugin;
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
		case 'n':
			if (type == MessageType.CHAT)
				result = null;
			else if (player != null)
				result = player.getDisplayName();
			else
				result = sender.getName();
			break;
		case 'N':
			result = sender.getName();
			break;
		case 'r':
			if (receiver != null) {
				if (receiverPlayer != null)
					result = receiverPlayer.getDisplayName();
				else
					result = receiver.getName();
			} else {
				result = "";
			}
			break;
		case 'R':
			if (receiver != null)
				result = receiver.getName();
			else
				result = "";
			break;
		case 'm':
			if (type == MessageType.CHAT) {
				result = null;
			}
			else if (message != null) {
				if (type == MessageType.DEATH)
					result = cutDeathMessage(sender.getName(), message);
				else
					result = message;
			} else {
				result = "";
			}
			break;
		case 'g':
			result = gPlugin.getChatManager().getGroup(sender).getName();
			if (result == null)
				result = "";
			break;
		case 'G':
			result = gPlugin.getChatManager().getRealGroup(sender);
			if (result == null)
				result = "";
			break;
		case 'c':
			result = gPlugin.getChatManager().getChannel(sender).getName();
			if (result == null)
				result = "";
			break;
		case 'p':
			result = gPlugin.getChatManager().getGroup(sender).getPrefix();
			if (result == null)
				result = "";
			break;
		case 's':
			result = gPlugin.getChatManager().getGroup(sender).getSuffix();
			if (result == null)
				result = "";
			break;
		case 't':
			result = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
			break;
		case 'T':
			result = new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()));
			break;
		case 'h':
			result = ""; //String.valueOf(100 * player.getHealth()/player.getMaxHealth()) + percent; //FIXME
			break;
		case 'H':
			result = ""; //Health bars [|||||] //FIXME
			break;
		case 'f':
			if (player != null)
				result = (player.getFoodLevel() * 5) + percent;
			else
				result = "0" + percent;
			break;
		case 'l':
			if (player != null)
				result = String.valueOf(player.getLevel());
			break;
		case 'M':
			if (player != null)
				result = player.getGameMode().name();
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
		case 'S':
			result = Bukkit.getServerName();
			if (result == null)
				result = "";
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
		case 'v':
			result = Bukkit.getBukkitVersion();
			if (result == null)
				result = "";
			break;
		default:
			break;
		}
		return result;
	}

	private static String cutDeathMessage(String name, String message) {
		return message.replace(name, "");
	}
}
