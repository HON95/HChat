package no.hon95.bukkit.hchat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class ChatManager {

	public static final String DEFAULT_GROUP_NAME = "default";

	private final HChatPlugin gPlugin;
	private boolean gFormatName = true;
	private boolean gFormatChat = true;
	private boolean gFormatDeath = true;
	private boolean gFormatList = true;
	private boolean gFormatJoin = true;
	private boolean gFormatQuit = true;
	private boolean gFormatMotd = true;
	private boolean gFormatMe = true;
	private boolean gFormatTell = true;
	private final HashMap<String, HGroup> gGroups = new HashMap<String, HGroup>();
	private final HashMap<UUID, String> gPlayerGroups = new HashMap<UUID, String>();
	private final HashMap<String, String> gCensoredWords = new HashMap<String, String>();
	private final HashSet<Player> gNameUpdatesPending = new HashSet<Player>();

	public ChatManager(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	public void reload() {
		gPlayerGroups.clear();
		update();
	}

	public void update() {
		for (Player player : Bukkit.getOnlinePlayers())
			updatePlayer(player);
	}

	public void updatePlayer(Player player) {
		String group = gPlugin.getPlayerGroup(player.getWorld().getName(), player.getName());
		if (group == null)
			group = DEFAULT_GROUP_NAME;
		String oldGroup = gPlayerGroups.get(player.getUniqueId());
		if (oldGroup == null || !oldGroup.equalsIgnoreCase(group)) {
			gPlayerGroups.put(player.getUniqueId(), group);
			updatePlayerNames(player);
		}
	}

	public void removePlayer(Player player) {
		gPlayerGroups.remove(player.getUniqueId());
	}

	public HGroup getGroup(UUID playerUuid) {
		String playerGroup = gPlayerGroups.get(playerUuid);
		return getGroup(playerGroup);
	}

	public HGroup getGroup(String name) {
		HGroup group = gGroups.get(name);
		if (group == null)
			group = gGroups.get(DEFAULT_GROUP_NAME);
		return group;
	}

	public HGroup getGroup(CommandSender sender) {
		if (sender instanceof Player)
			return getGroup(((Player) sender).getUniqueId());
		return getGroup(DEFAULT_GROUP_NAME);
	}

	public String getRealGroup(CommandSender sender) {
		if (sender instanceof Player)
			return gPlayerGroups.get(((Player) sender).getUniqueId());
		return null;
	}

	public void doPendingNameUpdates() {
		Iterator<Player> it = gNameUpdatesPending.iterator();
		while (it.hasNext()) {
			updatePlayerNames(it.next());
			it.remove();
		}
	}

	public void updatePlayerNames(Player player) {
		HGroup group = getGroup(player.getUniqueId());
		String realGroup = gPlayerGroups.get(player.getUniqueId());
		updateDisplayName(player, group, realGroup);
		updateListName(player, group, realGroup);
	}

	private void updateDisplayName(Player player, HGroup group, String realGroup) {
		if (gFormatName)
			player.setDisplayName(VariableFormatter.format(group.nameFormat, player, group, realGroup, null, null, false, false, false));
	}

	private void updateListName(Player player, HGroup group, String realGroup) {
		if (gFormatList)
			player.setPlayerListName(VariableFormatter.format(group.listFormat, player, group, realGroup, null, null, false, false, false));
	}

	public String formatChat(Player player) {
		HGroup group = getGroup(player.getUniqueId());
		String realGroup = gPlayerGroups.get(player.getUniqueId());
		return VariableFormatter.format(group.chatFormat, player, group, realGroup, null, null, true, false, false);
	}

	public String formatDeath(Player player, String deathMessage) {
		HGroup group = getGroup(player.getUniqueId());
		String realGroup = gPlayerGroups.get(player.getUniqueId());
		return VariableFormatter.format(group.deathFormat, player, group, realGroup, deathMessage, null, false, true, false);
	}

	public String formatJoin(Player player) {
		HGroup group = getGroup(player.getUniqueId());
		String realGroup = gPlayerGroups.get(player.getUniqueId());
		return VariableFormatter.format(group.joinFormat, player, group, realGroup, null, null, false, false, false);
	}

	public String formatQuit(Player player) {
		HGroup group = getGroup(player.getUniqueId());
		String realGroup = gPlayerGroups.get(player.getUniqueId());
		return VariableFormatter.format(group.quitFormat, player, group, realGroup, null, null, false, false, false);
	}

	public List<String> formatMotd(Player player) {
		HGroup group = getGroup(player.getUniqueId());
		String realGroup = gPlayerGroups.get(player.getUniqueId());
		return VariableFormatter.format(group.motdFormat, player, group, realGroup, null, null, false, false, false);
	}

	public String formatMe(CommandSender sender, String message) {
		HGroup group = getGroup(sender);
		String realGroup = getRealGroup(sender);
		return VariableFormatter.format(group.meFormat, sender, group, realGroup, message, null, false, false, false);
	}

	public String formatTellSender(CommandSender sender, String message, CommandSender receiver) {
		HGroup group = getGroup(sender);
		String realGroup = getRealGroup(sender);
		return VariableFormatter.format(group.tellSenderFormat, sender, group, realGroup, message, receiver, false, false, true);
	}

	public String formatTellReceiver(CommandSender sender, String message, CommandSender receiver) {
		HGroup group = getGroup(receiver);
		String realGroup = getRealGroup(receiver);
		return VariableFormatter.format(group.tellReceiverFormat, sender, group, realGroup, message, receiver, false, false, true);
	}

	public void updateNamesNextTick(Player player) {
		gNameUpdatesPending.add(player);
	}

	public void setFormatName(boolean format) {
		gFormatName = format;
	}

	public void setFormatChat(boolean format) {
		gFormatChat = format;
	}

	public void setFormatDeath(boolean format) {
		gFormatDeath = format;
	}

	public void setFormatList(boolean format) {
		gFormatList = format;
	}

	public void setFormatJoin(boolean format) {
		gFormatJoin = format;
	}

	public void setFormatQuit(boolean format) {
		gFormatQuit = format;
	}

	public void setFormatMotd(boolean format) {
		gFormatMotd = format;
	}

	public void setFormatMe(boolean format) {
		gFormatMe = format;
	}

	public void setFormatTell(boolean format) {
		gFormatTell = format;
	}

	public boolean getFormatName() {
		return gFormatName;
	}

	public boolean getFormatChat() {
		return gFormatChat;
	}

	public boolean getFormatDeath() {
		return gFormatDeath;
	}

	public boolean getFormatList() {
		return gFormatList;
	}

	public boolean getFormatJoin() {
		return gFormatJoin;
	}

	public boolean getFormatQuit() {
		return gFormatQuit;
	}

	public boolean getFormatMotd() {
		return gFormatMotd;
	}

	public boolean getFormatMe() {
		return gFormatMe;
	}

	public boolean getFormatTell() {
		return gFormatTell;
	}

	public Map<UUID, String> getPlayerGroups() {
		return gPlayerGroups;
	}

	public void setGroups(Set<HGroup> groups) {
		gGroups.clear();
		for (HGroup g : groups)
			gGroups.put(g.id, g);
	}

	public Map<String, String> getCensoredWords() {
		return gCensoredWords;
	}
}
