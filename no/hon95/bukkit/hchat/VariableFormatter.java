package no.hon95.bukkit.hchat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class VariableFormatter {

	public static final String BUKKIT_VAR_PLAYER = "%1$s";
	public static final String BUKKIT_VAR_MSG = "%2$s";

	// For reference //
	public static final String VAR_PERCENT = "%%";
	public static final String VAR_DISPLAY_NAME = "%n";
	public static final String VAR_NAME = "%N";
	public static final String VAR_RECEIVER_DISPLAY_NAME = "%r";
	public static final String VAR_RECEIVER_NAME = "%R";
	public static final String VAR_MSG = "%m";
	public static final String VAR_GROUP = "%g";
	public static final String VAR_GROUP_REAL = "%G";
	public static final String VAR_PREFIX = "%p";
	public static final String VAR_SUFFIX = "%s";
	public static final String VAR_TIME = "%t";
	public static final String VAR_TIME_SHORT = "%T";
	public static final String VAR_WORLD = "%w";
	public static final String VAR_HEALTH = "%h";
	public static final String VAR_FOOD = "%f";
	public static final String VAR_LEVEL = "%l";
	public static final String VAR_GAME_MODE = "%M";
	public static final String VAR_POS_X = "%x";
	public static final String VAR_POS_Y = "%y";
	public static final String VAR_POS_Z = "%z";
	public static final String VAR_SERVER_NAME = "%S";
	public static final String VAR_ONLINE_PLAYERS = "%o";
	public static final String VAR_PLAYER_LIST = "%O";
	public static final String VAR_VERSION_MINECRAFT = "%v";

	public static List<String> format(List<String> formatList, CommandSender sender, HGroup group, String realGroup, String message, CommandSender receiver, boolean chat, boolean death, boolean hasReceiver) {
		ArrayList<String> results = new ArrayList<String>(formatList.size());
		for (String format : formatList) {
			results.add(format(format, sender, group, realGroup, message, receiver, chat, death, hasReceiver));
		}
		return results;
	}

	public static String format(String format, CommandSender sender, HGroup group, String realGroup, String message, CommandSender receiver, boolean chat, boolean death, boolean hasReceiver) {
		if (format == null)
			return "";
		char[] cf = format.toCharArray();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < cf.length; i++) {
			if (cf[i] == '%' && i < cf.length - 1) {
				String val = getValue(cf[i + 1], sender, group, realGroup, message, receiver, chat, death, hasReceiver);
				if (val != null) {
					sb.append(val);
					i++;
				}
				continue;
			}
			sb.append(cf[i]);
		}
		return ChatColor.translateAlternateColorCodes('&', sb.toString());
	}

	private static String getValue(char c, CommandSender sender, HGroup group, String realGroup, String message, CommandSender receiver, boolean chat, boolean death, boolean hasReceiver) {
		Player player = null;
		Player receiverPlayer = null;
		if (sender instanceof Player)
			player = (Player) sender;
		if (receiver instanceof Player)
			receiverPlayer = (Player) receiver;
		String percent = chat ? "%%" : "%";

		switch (c) {
		case '%':
			return percent;
		case 'n':
			if (chat)
				return BUKKIT_VAR_PLAYER;
			if (player != null)
				return player.getDisplayName();
			return sender.getName();
		case 'N':
			return sender.getName();
		case 'r':
			if (hasReceiver) {
				if (receiverPlayer != null)
					return receiverPlayer.getDisplayName();
				return receiver.getName();
			}
			return "";
		case 'R':
			if (hasReceiver)
				return receiver.getName();
			return "";
		case 'm':
			if (chat)
				return BUKKIT_VAR_MSG;
			if (message != null) {
				if (death)
					return cutDeathMessage(sender.getName(), message);
				return message;
			}
			return "";
		case 'g':
			return group.name;
		case 'G':
			if (realGroup != null)
				return realGroup;
			return "";
		case 'p':
			return group.prefix;
		case 's':
			return group.suffix;
		case 't':
			return new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
		case 'T':
			return new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()));
		case 'h':
			return ""; //String.valueOf(100 * player.getHealth()/player.getMaxHealth()) + percent; //FIXME
		case 'H':
			return ""; //Health bars [|||||] //FIXME
		case 'f':
			if (player != null)
				return (player.getFoodLevel() * 5) + percent;
			return "0" + percent;
		case 'l':
			if (player != null)
				return String.valueOf(player.getLevel());
		case 'M':
			if (player != null)
				return player.getGameMode().name();
		case 'w':
			if (player != null)
				return player.getLocation().getWorld().getName();
		case 'x':
			if (player != null)
				return String.valueOf(player.getLocation().getBlockX());
		case 'y':
			if (player != null)
				return String.valueOf(player.getLocation().getBlockY());
		case 'z':
			if (player != null)
				return String.valueOf(player.getLocation().getBlockZ());
		case 'S':
			return Bukkit.getServerName();
		case 'o':
			return String.valueOf(Bukkit.getOnlinePlayers().length);
		case 'O':
			Player[] players = Bukkit.getOnlinePlayers();
			if (players.length == 0)
				return "";
			StringBuilder sb = new StringBuilder(players[0].getDisplayName());
			for (int i = 1; i < players.length; i++) {
				sb.append(ChatColor.RESET.toString());
				if (i == players.length - 1)
					sb.append(" & ");
				else
					sb.append(", ");
				sb.append(players[i].getDisplayName());
			}
			return sb.toString();
		case 'v':
			return Bukkit.getBukkitVersion();
		default:
			return null;
		}
	}

	private static String cutDeathMessage(String name, String message) {
		return message.replace(name, "");
	}
}
