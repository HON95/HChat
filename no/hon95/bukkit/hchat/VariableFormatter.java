package no.hon95.bukkit.hchat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class VariableFormatter {

	public static final String BUKKIT_VAR_PLAYER = "%1$s";
	public static final String BUKKIT_VAR_MSG = "%2$s";

	// For reference //
	public static final String VAR_PERCENT = "%%";
	public static final String VAR_DISPLAY_NAME = "%n";
	public static final String VAR_NAME = "%N";
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

	public static List<String> format(List<String> formatList, Player player, HGroup group, String realGroup, String message, boolean chat, boolean death) {
		ArrayList<String> results = new ArrayList<String>(formatList.size());
		for (String format : formatList) {
			results.add(format(format, player, group, realGroup, message, chat, death));
		}
		return results;
	}

	public static String format(String format, Player player, HGroup group, String realGroup, String message, boolean chat, boolean death) {
		if (format == null)
			return "";
		char[] cf = format.toCharArray();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < cf.length; i++) {
			if (cf[i] == '%' && i < cf.length - 1) {
				String val = getValue(cf[i + 1], player, group, realGroup, message, chat, death);
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

	private static String getValue(char c, Player player, HGroup group, String realGroup, String message, boolean chat, boolean death) {
		switch (c) {
		case '%':
			if (chat)
				return "%%";
			return "%";
		case 'n':
			if (chat)
				return BUKKIT_VAR_PLAYER;
			return player.getDisplayName();
		case 'N':
			return player.getName();
		case 'm':
			if (chat)
				return BUKKIT_VAR_MSG;
			if (message != null) {
				if (death)
					return cutDeathMessage(player.getName(), message);
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
			return ""; //String.valueOf(100 * player.getHealth()/player.getMaxHealth()) + "%%"; //FIXME
		case 'H':
			return ""; //Health bars [|||||] //FIXME
		case 'f':
			return (player.getFoodLevel() * 5) + "%%";
		case 'l':
			return String.valueOf(player.getLevel());
		case 'M':
			return player.getGameMode().name();
		case 'w':
			return player.getLocation().getWorld().getName();
		case 'x':
			return String.valueOf(player.getLocation().getBlockX());
		case 'y':
			return String.valueOf(player.getLocation().getBlockY());
		case 'z':
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
