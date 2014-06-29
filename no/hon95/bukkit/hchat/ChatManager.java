package no.hon95.bukkit.hchat;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_IMMUTABLE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import no.hon95.bukkit.hchat.format.Formatter.MessageType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public final class ChatManager {

	public static final String DEFAULT_GROUP_NAME = "default";
	public static final String DEFAULT_CHANNEL_NAME = "default";

	private final HChatPlugin gPlugin;
	private boolean gFormatName = true;
	private boolean gFormatChat = true;
	private boolean gFormatDeath = true;
	private boolean gFormatList = true;
	private boolean gFormatJoin = true;
	private boolean gFormatQuit = true;
	private boolean gFormatChannelJoin = true;
	private boolean gFormatChannelQuit = true;
	private boolean gFormatMotd = true;
	private boolean gFormatMe = true;
	private boolean gFormatTell = true;
	private final HashMap<String, HGroup> gGroups = new HashMap<String, HGroup>();
	private final HashMap<String, HChannel> gChannels = new HashMap<String, HChannel>();
	private final HashMap<UUID, String> gPlayerGroups = new HashMap<UUID, String>();
	private final HashMap<UUID, String> gPlayerChannels = new HashMap<UUID, String>();
	private final HashMap<String, String> gCensoredWords = new HashMap<String, String>();
	private final HashSet<Player> gNameUpdatesPending = new HashSet<Player>();
	private final HashSet<UUID> gMutedPlayers = new HashSet<UUID>();
	private final HashMap<UUID, Set<UUID>> gIndividuallyMutedPlayers = new HashMap<UUID, Set<UUID>>();

	public ChatManager(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	public void load() {
		for (Player player : Bukkit.getOnlinePlayers())
			loadPlayer(player);
	}

	public void unload() {
		gPlayerGroups.clear();
		gPlayerChannels.clear();
		gNameUpdatesPending.clear();
	}

	public void reload() {
		unload();
		load();
	}

	public void updateGroups() {
		for (Player player : Bukkit.getOnlinePlayers())
			updatePlayerGroup(player);
	}

	public void loadPlayer(Player player) {
		updatePlayerGroup(player);
		updatePlayerChannel(player);
		gIndividuallyMutedPlayers.put(player.getUniqueId(), new HashSet<UUID>());
	}

	public void unloadPlayer(Player player) {
		UUID id = player.getUniqueId();
		gPlayerGroups.remove(id);
		gPlayerChannels.remove(id);
		gIndividuallyMutedPlayers.remove(id);
	}

	public void updatePlayerGroup(Player player) {
		String group = getPlayerGroup(player);
		String oldGroup = gPlayerGroups.get(player.getUniqueId());
		HGroup hgroup = getGroup(group);
		if (oldGroup == null || !oldGroup.equalsIgnoreCase(hgroup.getId())) {
			gPlayerGroups.put(player.getUniqueId(), hgroup.getId());
			updatePlayerNames(player);
		}
	}

	public void updatePlayerChannel(Player player) {
		if (gPlayerChannels.get(player.getUniqueId()) == null) {
			HGroup group = getGroup(player.getUniqueId());
			String channel = group.getDefaultWorldChannels().get(player.getWorld().getName().toLowerCase());
			if (channel == null)
				channel = group.getDefaultChannel();
			if (channel == null)
				channel = DEFAULT_CHANNEL_NAME;
			changePlayerChannel(player.getUniqueId(), channel, false);
		}
	}

	public void doPendingNameUpdates() {
		Iterator<Player> it = gNameUpdatesPending.iterator();
		while (it.hasNext()) {
			Player player = it.next();
			if (player.isOnline())
				updatePlayerNames(player);
			it.remove();
		}
	}

	public void updateNamesNextTick(Player player) {
		gNameUpdatesPending.add(player);
	}

	public void setGroups(Set<HGroup> groups) {
		gGroups.clear();
		for (HGroup g : groups)
			gGroups.put(g.getId(), g);
	}

	public void setChannels(Set<HChannel> channels) {
		gChannels.clear();
		for (HChannel c : channels)
			gChannels.put(c.getId(), c);
	}

	public HGroup getGroup(CommandSender sender) {
		if (sender instanceof Player)
			return getGroup(((Player) sender).getUniqueId());
		return getGroup(DEFAULT_GROUP_NAME);
	}

	public HGroup getGroup(UUID playerUuid) {
		String playerGroup = gPlayerGroups.get(playerUuid);
		return getGroup(playerGroup);
	}

	public HGroup getGroup(String name) {
		HGroup group = null;
		if (name != null)
			group = gGroups.get(name.toLowerCase());
		if (group == null)
			group = gGroups.get(DEFAULT_GROUP_NAME);
		return group;
	}

	public HChannel getChannel(CommandSender sender) {
		if (sender instanceof Player)
			return getChannel(((Player) sender).getUniqueId());
		return getChannel(DEFAULT_CHANNEL_NAME);
	}

	public HChannel getChannel(UUID playerUuid) {
		return getChannel(gPlayerChannels.get(playerUuid));
	}

	public HChannel getChannel(String name) {
		HChannel channel = null;
		if (name != null)
			channel = gChannels.get(name.toLowerCase());
		if (channel == null)
			channel = gChannels.get(DEFAULT_CHANNEL_NAME);
		return channel;
	}

	public HChannel getChannelExact(String name) {
		return gChannels.get(name.toLowerCase());
	}

	public String getRealGroup(CommandSender sender) {
		if (sender instanceof Player)
			return getPlayerGroup(((Player) sender));
		return null;
	}

	public String getPlayerChannel(UUID id) {
		if (id == null)
			return null;
		return gPlayerChannels.get(id);
	}

	public void changePlayerChannel(UUID player, String channel, boolean announce) {
		boolean join = channel != null;
		HChannel hchannel = getChannel(channel);
		gPlayerChannels.put(player, hchannel.getId());
		if (announce) {
			if (join) {
				if (gFormatChannelJoin)
					Bukkit.broadcastMessage(gPlugin.getFormatManager().formatString(MessageType.CHANNEL_JOIN, Bukkit.getPlayer(player), null, null));
			} else {
				if (gFormatChannelQuit)
					Bukkit.broadcastMessage(gPlugin.getFormatManager().formatString(MessageType.CHANNEL_QUIT, Bukkit.getPlayer(player), null, null));
			}
		}
	}

	public void addChannel(HChannel channel) {
		if (channel == null || channel.getId() == null)
			throw new IllegalArgumentException("Channel ID can not be null.");
		channel.setId(channel.getId().toLowerCase());
		gChannels.put(channel.getId(), channel);
		gPlugin.getConfigManager().addChannel(channel);
		gPlugin.getConfigManager().saveChannels();
	}

	public void removeChannel(String channelId) {
		if (channelId == null)
			throw new IllegalArgumentException("Channel ID can not be null.");
		channelId = channelId.toLowerCase();
		HChannel channel = gChannels.get(channelId);
		if (channel != null) {
			for (UUID pid : getChannelPlayers(channelId)) {
				changePlayerChannel(pid, null, true);
				Player player = Bukkit.getPlayer(pid);
				if (player != null)
					player.sendMessage("You have left channel " + channel.getName() + "§f because the channel was removed.");
			}
			gChannels.remove(channelId);
			gPlugin.getConfigManager().removeChannel(channelId);
			gPlugin.getConfigManager().saveChannels();
		}
	}

	public Set<UUID> getChannelPlayers(String channel) {
		HashSet<UUID> players = new HashSet<UUID>();
		for (Entry<UUID, String> e : gPlayerChannels.entrySet()) {
			if (e.getValue().equalsIgnoreCase(channel))
				players.add(e.getKey());
		}
		return players;
	}

	public void mutePlayerGlobally(UUID id, boolean mute) {
		if (id == null)
			throw new IllegalArgumentException();

		if (mute)
			gMutedPlayers.add(id);
		else
			gMutedPlayers.remove(id);
	}

	public void mutePlayerIndividually(UUID player, UUID mutedPlayer, boolean mute) {
		if (player == null || mutedPlayer == null)
			throw new IllegalArgumentException();
		Set<UUID> mutedPlayers = gIndividuallyMutedPlayers.get(player);
		if (mute)
			mutedPlayers.add(mutedPlayer);
		else
			mutedPlayers.remove(mutedPlayer);
	}

	public boolean isPlayerMutedGlobally(UUID id) {
		return gMutedPlayers.contains(id);
	}

	public boolean isPlayerMutedIndividually(UUID player, UUID mutedPlayer) {
		return gIndividuallyMutedPlayers.get(player).contains(mutedPlayer);
	}

	public void updatePlayerNames(Player player) {
		if (gFormatName)
			player.setDisplayName(gPlugin.getFormatManager().formatString(MessageType.NAME, player, null, null));
		if (gFormatList)
			player.setPlayerListName(gPlugin.getFormatManager().formatString(MessageType.NAME, player, null, null));
	}

	//// MISC ////

	public String getPlayerGroup(Player player) {
		String chosenGroup = null;
		for (String group : gPlugin.getVault().getPlayerGroups(player)) {
			if (getGroups().containsKey(group))
				chosenGroup = group;
		}
		if (chosenGroup == null)
			chosenGroup = gPlugin.getVault().getPlayerGroup(player);
		return chosenGroup;
	}

	//// CHAT PROCESSING ////

	public void onChat(AsyncPlayerChatEvent ev) {
		if (!ev.isCancelled()) {
			Player sender = ev.getPlayer();
			HGroup group = getGroup(sender.getUniqueId());

			if (!group.getCanChat()) {
				ev.setCancelled(true);
				sender.sendMessage(ChatColor.RED + "You are not allowed to chat!");
			} else if (isPlayerMutedGlobally(sender.getUniqueId()) && !sender.hasPermission(PERM_IMMUTABLE)) {
				ev.setCancelled(true);
				sender.sendMessage(ChatColor.RED + "You are globally muted.");
			} else {
				HChannel channel = getChannel(sender.getUniqueId());
				String format = ev.getFormat();
				String message = ev.getMessage();
				if (gFormatChat) {
					format = gPlugin.getFormatManager().formatString(MessageType.CHAT, sender, null, null);
					ev.setFormat(format);
				}
				if (group.getCensor() || channel.isCensored())
					message = ChatCensor.censor(message, getCensoredWords());
				if (group.getColorCodes() || channel.allowColorCodes())
					message = ChatColor.translateAlternateColorCodes('&', message);
				ev.setMessage(message);

				if (!channel.isUniversal() || channel.getId() != DEFAULT_CHANNEL_NAME) {
					HashSet<Player> newRecipients = new HashSet<Player>();
					for (UUID pid : getChannelPlayers(channel.getId())) {
						Player recipient = Bukkit.getPlayer(pid);
						if (recipient != null)
							newRecipients.add(recipient);
					}
					for (HChannel c : gChannels.values()) {
						if (!c.getId().equalsIgnoreCase(channel.getId())) {
							if (c.getMonitorChannels() != null && c.getMonitorChannels().contains(channel.getId())) {
								for (UUID pid : getChannelPlayers(c.getId())) {
									Player recipient = Bukkit.getPlayer(pid);
									if (recipient != null)
										newRecipients.add(recipient);
								}
							}
						}
					}
					World world = sender.getWorld();
					Iterator<Player> iter = newRecipients.iterator();
					while (iter.hasNext()) {
						Player recipient = iter.next();
						if ((isPlayerMutedIndividually(recipient.getUniqueId(), sender.getUniqueId()) && !sender.hasPermission(PERM_IMMUTABLE))
								|| (!channel.isUniversal() && recipient.getWorld() != world))
							iter.remove();
					}
					try {
						Set<Player> recipients = ev.getRecipients();
						recipients.clear();
						recipients.addAll(newRecipients);
					} catch (UnsupportedOperationException ex) {
						gPlugin.getLogger().warning("Failed to change chat message recipient set, sending private message to each recipient instead.");
						ev.setCancelled(true);
						for (Player recipient : newRecipients)
							recipient.sendMessage(String.format(format, sender.getDisplayName(), message));
					}
				}
			}
		}
	}

	//// GETTERS AND SETTERS ////

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

	public void setFormatChannelJoin(boolean format) {
		gFormatChannelJoin = format;
	}

	public void setFormatChannelQuit(boolean format) {
		gFormatChannelQuit = format;
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

	public boolean getFormatChannelJoin() {
		return gFormatChannelJoin;
	}

	public boolean getFormatChannelQuit() {
		return gFormatChannelQuit;
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

	public Map<String, String> getCensoredWords() {
		return gCensoredWords;
	}

	public Map<String, HGroup> getGroups() {
		return gGroups;
	}

	public Map<String, HChannel> getChannels() {
		return gChannels;
	}
}
