package no.hon95.bukkit.hchat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import no.hon95.bukkit.hchat.permissionmanager.PermissionManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public final class InfoManager {

	public static final String DEFAULT_GROUP_NAME = "default";
	public static final String BUKKIT_VAR_PLAYER = "%1$s";
	public static final String BUKKIT_VAR_MSG = "%2$s";
	public static final String VAR_PLAYER = "%n";
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

	private final HChatPlugin gPlugin;
	private boolean gFormatList = true;
	private boolean gUsePermissionPluginPrefixAndSuffix = true;
	private final Object gLock = new Object();
	private final Map<String, Group> gGroups = new HashMap<String, Group>();
	private final Map<String, String> gCensoredWords = new HashMap<String, String>();
	private final Map<String, User> gUsers = new HashMap<String, User>();

	public InfoManager(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	public void update() {
		for (Player player : Bukkit.getOnlinePlayers())
			updatePlayer(player);
	}

	public void updatePlayer(Player player) {
		synchronized (gLock) {
			if (!player.isOnline())
				return;
			String playerName = player.getName();
			String worldName = player.getWorld().getName();
			PermissionManager pm = gPlugin.getPermissionManager();
			User user = gUsers.get(playerName);
			if (user == null) {
				user = new User();
				user.id = playerName;
				gUsers.put(playerName, user);
			}
			String groupId = pm.getTopGroup(playerName, worldName);
			Group group = null;
			if (groupId != null) {
				group = gGroups.get(groupId);
			}
			if (group == null)
				group = gGroups.get(DEFAULT_GROUP_NAME);
			if (group == null)
				throw new RuntimeException("Default group not found");
			user.realGroup = groupId;
			user.group = group.id;
			if (gUsePermissionPluginPrefixAndSuffix) {
				user.prefix = pm.getPrefix(playerName, worldName);
				user.suffix = pm.getSuffix(playerName, worldName);
			} else {
				user.prefix = group.prefix;
				user.suffix = group.suffix;
			}

			updatePlayerListName(player);
		}
	}

	public void updatePlayerListName(Player player) {
		if (!gFormatList)
			return;

		User user = gPlugin.getInfoManager().getUser(player.getName());
		Group group = gPlugin.getInfoManager().getGroup(user.group);

		String worldName = player.getWorld().getName();
		String playerName = player.getName();
		String realGroup = (user.realGroup != null) ? user.realGroup : "";
		String health = ""; // String.valueOf(100 * player.getHealth()/player.getMaxHealth()) + "%"; //FIXME ambiguous problem 
		String food = String.valueOf(player.getFoodLevel() * 5) + "%";
		String level = String.valueOf(player.getLevel());
		String gameMode = player.getGameMode().name();
		String listName = group.listFormat.replace(VAR_PLAYER, playerName).replace(VAR_GROUP, group.name).replace(VAR_GROUP_REAL, realGroup)
				.replace(VAR_PREFIX, user.prefix).replace(VAR_SUFFIX, user.suffix).replace(VAR_WORLD, worldName)
				.replace(VAR_HEALTH, health).replace(VAR_FOOD, food).replace(VAR_LEVEL, level).replace(VAR_GAME_MODE, gameMode);
		listName = ChatColor.translateAlternateColorCodes('&', listName);
		if (listName.length() > 16)
			listName = listName.substring(0, 16);
		player.setPlayerListName(listName);
	}

	public void removePlayer(Player player) {
		gUsers.remove(player.getName());
	}

	public void setUsePermissionPluginPrefixAndSuffix(boolean use) {
		gUsePermissionPluginPrefixAndSuffix = use;
	}

	public boolean getUsePermissionPluginPrefixAndSuffix() {
		return gUsePermissionPluginPrefixAndSuffix;
	}

	public void setFormatList(boolean format) {
		gFormatList = format;
	}

	public boolean getFormatList() {
		return gFormatList;
	}

	public User getUser(String name) {
		synchronized (gLock) {
			return gUsers.get(name);
		}
	}

	public Group getGroup(String name) {
		synchronized (gLock) {
			return gGroups.get(name);
		}
	}

	public void putGroups(Set<Group> groups) {
		for (Group g : groups)
			gGroups.put(g.id, g);
	}

	public Map<String, String> getCensoredWords() {
		return gCensoredWords;
	}
}
