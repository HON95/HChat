package ninja.hon95.bukkit.hchat;

import static ninja.hon95.bukkit.hchat.HChatPermissions.PERM_IMMUTABLE;
import static org.bukkit.ChatColor.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import ninja.hon95.bukkit.hchat.format.FormatManager.MessageType;
import ninja.hon95.bukkit.hchat.util.ChannelAccessUtils;
import ninja.hon95.bukkit.hcommonlib.CompatUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

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
	private boolean gFormatAwayStart = true;
	private boolean gFormatAwayStop = true;
	private boolean gFormatAwayKick = true;
	private boolean gFormatMotd = true;
	private boolean gFormatMe = true;
	private boolean gFormatTell = true;
	private boolean gReplaceChatFormat = true;
	private boolean gAnnounceAutoJoinDefaultChannel = true;
	private final HashMap<String, Group> gGroups = new HashMap<String, Group>();
	private final HashMap<String, Channel> gChannels = new HashMap<String, Channel>();
	private final HashMap<UUID, String> gPlayerRealGroups = new HashMap<UUID, String>();
	private final HashMap<UUID, String> gPlayerGroups = new HashMap<UUID, String>();
	private final HashMap<UUID, String> gPlayerChannels = new HashMap<UUID, String>();
	private final HashMap<String, String> gCensoredWords = new HashMap<String, String>();
	private final HashSet<Player> gNameUpdatesPending = new HashSet<Player>();
	private final HashSet<UUID> gMutedPlayers = new HashSet<UUID>();
	private boolean gMuteAll = false;
	private final HashMap<UUID, Set<UUID>> gIndividuallyMutedPlayers = new HashMap<UUID, Set<UUID>>();
	private final HashMap<UUID, Long> gPlayerLastMove = new HashMap<UUID, Long>();
	private final HashSet<UUID> gAwayPlayers = new HashSet<UUID>();
	private final HashSet<UUID> gKickPlayers = new HashSet<UUID>();

	public ChatManager(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	public void load() {
		for (Player player : CompatUtil.getOnlinePlayers())
			loadPlayer(player);
	}

	public void unload() {
		gPlayerGroups.clear();
		gPlayerChannels.clear();
		gPlayerRealGroups.clear();
		gNameUpdatesPending.clear();
	}

	public void reload() {
		unload();
		load();
	}

	public void fastTimedUpdate() {
		doPendingNameUpdates();
	}

	public void slowTimedUpdate() {
		updatePlayerGroups();
		long time = System.currentTimeMillis();
		for (Entry<UUID, Long> e : gPlayerLastMove.entrySet()) {
			if (!isPlayerAway(e.getKey())) {
				long threshold = getGroup(e.getKey()).getAwayThreshold() * 1000;
				if (threshold > 0 && e.getValue() + threshold < time)
					setPlayerAway(true, CompatUtil.getLocalPlayer(e.getKey()));
			}
		}
	}

	public void updatePlayerGroups() {
		for (Player player : CompatUtil.getOnlinePlayers())
			updatePlayerGroup(player);
	}

	public void loadPlayer(Player player) {
		UUID id = player.getUniqueId();
		updatePlayerGroup(player);
		updatePlayerChannel(player);
		gIndividuallyMutedPlayers.put(id, new HashSet<UUID>());
		gPlayerLastMove.put(id, System.currentTimeMillis());
	}

	public void unloadPlayer(Player player) {
		UUID id = player.getUniqueId();
		gPlayerGroups.remove(id);
		gPlayerChannels.remove(id);
		gPlayerRealGroups.remove(id);
		gIndividuallyMutedPlayers.remove(id);
		gPlayerLastMove.remove(id);
		gAwayPlayers.remove(id);
	}

	public void updatePlayerGroup(Player player) {
		UUID uuid = player.getUniqueId();
		String realGroup = calculatePlayerGroup(player);
		String oldRealGroup = gPlayerRealGroups.get(uuid);
		if (oldRealGroup == null || !oldRealGroup.equalsIgnoreCase(realGroup)) {
			gPlayerRealGroups.put(uuid, realGroup);
			gPlayerGroups.put(uuid, getGroup(realGroup).getId());
			updatePlayerNames(player);
		}
	}

	public void updatePlayerChannel(Player player) {
		if (gPlayerChannels.get(player.getUniqueId()) == null) {
			Group group = getGroup(player.getUniqueId());
			Channel channel = getDefaultChannel(group, player.getWorld().getName());
			changePlayerChannel(player, channel.getName(), false);
		}
	}

	public void doPendingNameUpdates() {
		if (gNameUpdatesPending.size() > 0) {
			Iterator<Player> it = gNameUpdatesPending.iterator();
			while (it.hasNext()) {
				Player player = it.next();
				if (player.isOnline())
					updatePlayerNames(player);
				it.remove();
			}
		}
	}

	public void updateNamesNextTick(Player player) {
		gNameUpdatesPending.add(player);
	}

	public void setGroups(Set<Group> groups) {
		gGroups.clear();
		for (Group g : groups)
			gGroups.put(g.getId(), g);
	}

	public void setChannels(Set<Channel> channels) {
		gChannels.clear();
		for (Channel c : channels)
			gChannels.put(c.getId(), c);
	}

	public Group getGroup(CommandSender sender) {
		if (sender instanceof Player)
			return getGroup(((Player) sender).getUniqueId());
		return getGroup(DEFAULT_GROUP_NAME);
	}

	public Group getGroup(UUID playerUuid) {
		return getGroup(gPlayerGroups.get(playerUuid));
	}

	public Group getGroup(String name) {
		Group group = null;
		if (name != null)
			group = gGroups.get(name.toLowerCase());
		if (group == null)
			group = gGroups.get(DEFAULT_GROUP_NAME);
		return group;
	}

	public Group getGroupExact(String name) {
		if (name != null)
			return gGroups.get(name.toLowerCase());
		return null;
	}

	public String getPlayerGroup(UUID id) {
		if (id == null)
			return null;
		return gPlayerGroups.get(id);
	}

	public Channel getChannel(CommandSender sender) {
		if (sender instanceof Player)
			return getChannel(((Player) sender).getUniqueId());
		return getChannel(DEFAULT_CHANNEL_NAME);
	}

	public Channel getChannel(UUID playerUuid) {
		return getChannel(gPlayerChannels.get(playerUuid));
	}

	public Channel getChannel(String name) {
		Channel channel = null;
		if (name != null)
			channel = gChannels.get(name.toLowerCase());
		if (channel == null)
			channel = gChannels.get(DEFAULT_CHANNEL_NAME);
		return channel;
	}

	public Channel getChannelExact(String name) {
		if (name != null)
			return gChannels.get(name.toLowerCase());
		return null;
	}

	public String getPlayerChannel(UUID id) {
		if (id == null)
			return null;
		return gPlayerChannels.get(id);
	}

	public String getRealGroup(CommandSender sender) {
		if (sender instanceof Player)
			return gPlayerRealGroups.get(((Player) sender).getUniqueId());
		return null;
	}

	public void changePlayerChannel(Player player, String channelId, boolean announce) {
		boolean join = channelId != null;
		Channel channel = (join ? getChannel(channelId) : getDefaultChannel(getGroup(player), player.getWorld().getName()));
		if (!join && announce && gFormatChannelQuit)
			Bukkit.broadcastMessage(gPlugin.getFormatManager().format(MessageType.CHANNEL_QUIT, player, null, null));
		gPlayerChannels.put(player.getUniqueId(), channel.getId());
		if (join && announce && gFormatChannelJoin)
			Bukkit.broadcastMessage(gPlugin.getFormatManager().format(MessageType.CHANNEL_JOIN, player, null, null));
	}

	public void addChannel(Channel channel) {
		if (channel == null || channel.getId() == null)
			throw new IllegalArgumentException("Channel ID can not be null.");
		channel.setId(channel.getId().toLowerCase());
		gChannels.put(channel.getId(), channel);
		gPlugin.getConfigManager().updateChannel(channel);
		gPlugin.getConfigManager().saveChannels();
	}

	public void removeChannel(String channelId) {
		if (channelId == null)
			throw new IllegalArgumentException("Channel ID can not be null.");
		channelId = channelId.toLowerCase();
		Channel channel = gChannels.get(channelId);
		if (channel != null) {
			for (UUID pid : getChannelPlayers(channelId)) {
				changePlayerChannel(CompatUtil.getLocalPlayer(pid), null, true);
				Player player = CompatUtil.getLocalPlayer(pid);
				if (player != null)
					player.sendMessage("You have left channel " + channel.getName() + "'" + RESET + " because the channel was removed.");
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

	public Set<UUID> getGloballyMutedPlayers() {
		return gMutedPlayers;
	}

	public Map<UUID, Set<UUID>> getIndividuallyMutedPlayers() {
		return gIndividuallyMutedPlayers;
	}

	public void updatePlayerNames(Player player) {
		if (gFormatName)
			player.setDisplayName(gPlugin.getFormatManager().format(MessageType.NAME, player, null, null));
		if (gFormatList) {
			String listName = gPlugin.getFormatManager().format(MessageType.LIST, player, null, null);
			if (listName.length() > 16)
				listName = listName.substring(0, 15) + ".";
			player.setPlayerListName(listName);
		}
	}

	public boolean isPlayerAway(UUID player) {
		return gAwayPlayers.contains(player);
	}

	public void setPlayerAway(boolean away, Player player) {
		if (away) {
			if (!isPlayerAway(player.getUniqueId())) {
				Group group = getGroup(player.getUniqueId());
				gAwayPlayers.add(player.getUniqueId());
				if (group.getKickOnAway())
					kickAwayPlayer(player);
				else if (gFormatAwayStart)
					Bukkit.broadcastMessage(gPlugin.getFormatManager().format(MessageType.AWAY_START, player, null, null));
				updatePlayerNames(player);
			}
		} else {
			if (isPlayerAway(player.getUniqueId())) {
				gAwayPlayers.remove(player.getUniqueId());
				if (gFormatAwayStop)
					Bukkit.broadcastMessage(gPlugin.getFormatManager().format(MessageType.AWAY_STOP, player, null, null));
				updatePlayerNames(player);
			}
		}
	}

	public void kickAwayPlayer(final Player player) {
		gKickPlayers.add(player.getUniqueId());
		Bukkit.getScheduler().runTaskLater(gPlugin, new Runnable() {
			public void run() {
				if (player.isOnline()) {
					player.kickPlayer(gPlugin.getFormatManager().format(MessageType.AWAY_KICK_PLAYER, player, null, null));
					if (gFormatAwayKick)
						Bukkit.broadcastMessage(gPlugin.getFormatManager().format(MessageType.AWAY_KICK_SERVER, player, null, null));
				}
			}
		}, 1L);
	}

	// UPDATE AND SAVE //

	public void updateAndSaveGroup(String groupId) {
		updateAndSaveGroup(getGroupExact(groupId));
	}

	public void updateAndSaveGroup(Group group) {
		if (group == null)
			throw new IllegalArgumentException();
		gGroups.put(group.getId(), group);
		gPlugin.getConfigManager().updateGroup(group);
		gPlugin.getConfigManager().saveGroups();
	}

	public void updateAndSaveChannel(String channelId) {
		updateAndSaveChannel(getChannelExact(channelId));
	}

	public void updateAndSaveChannel(Channel channel) {
		if (channel == null)
			throw new IllegalArgumentException();
		gChannels.put(channel.getId(), channel);
		gPlugin.getConfigManager().updateChannel(channel);
		gPlugin.getConfigManager().saveChannels();
	}

	// MISC //

	public String calculatePlayerGroup(Player player) {
		String chosenGroup = null;
		if (gPlugin.getVaultPermission().isHooked()) {
			String[] groups = gPlugin.getVaultPermission().getGroups(player);
			if (groups != null) {
				for (String group : gPlugin.getVaultPermission().getGroups(player)) {
					if (getGroups().containsKey(group)) {
						chosenGroup = group;
						break;
					}
				}
			}
			if (chosenGroup == null)
				chosenGroup = gPlugin.getVaultPermission().getGroup(player);
		}
		return chosenGroup;
	}

	public Channel getDefaultChannel(Group group, String world) {
		String channel = group.getDefaultWorldChannels().getCaseInsensitiveValue(world);
		if (channel == null)
			group.getDefaultChannel();
		return getChannel(channel);
	}

	// EVENT PROCESSING //

	public void onPlayerChat(AsyncPlayerChatEvent ev) {
		if (!ev.isCancelled()) {
			Player sender = ev.getPlayer();
			Group group = getGroup(sender.getUniqueId());

			if (!group.getCanChat()) {
				ev.setCancelled(true);
				sender.sendMessage(RED + "You are not allowed to chat!");
			} else if (isPlayerMutedGlobally(sender.getUniqueId()) && !sender.hasPermission(PERM_IMMUTABLE)) {
				ev.setCancelled(true);
				sender.sendMessage(RED + "You are globally muted.");
			} else if (gMuteAll && !sender.hasPermission(PERM_IMMUTABLE)) {
				ev.setCancelled(true);
				sender.sendMessage(RED + "Most people are currently muted.");
			} else {
				Channel channel = getChannel(sender.getUniqueId());
				String format = ev.getFormat();
				String message = ev.getMessage();
				if (gFormatChat) {
					if (gReplaceChatFormat)
						format = gPlugin.getFormatManager().format(MessageType.CHAT, sender, null, null);
					else
						format = gPlugin.getFormatManager().format(MessageType.CHAT, format, sender, null, null);
					ev.setFormat(format);
				}
				if (group.getCensor() || channel.isCensored())
					message = ChatCensor.censor(message, getCensoredWords());
				if (group.getAllowColorCodes() || channel.allowColorCodes())
					message = ChatColor.translateAlternateColorCodes('&', message);
				ev.setMessage(message);

				HashSet<Player> newRecipients = new HashSet<Player>();
				for (UUID pid : getChannelPlayers(channel.getId())) {
					Player recipient = CompatUtil.getLocalPlayer(pid);
					if (recipient != null)
						newRecipients.add(recipient);
				}

				if (!ChannelAccessUtils.isPassworded(channel)) {
					for (Channel c : gChannels.values()) {
						if (!c.equals(channel) && c.getMonitorChannels() != null && c.getMonitorChannels().containsCaseInsensitive(channel.getId())) {
							for (UUID pid : getChannelPlayers(c.getId())) {
								Player recipient = CompatUtil.getLocalPlayer(pid);
								if (recipient != null) {
									if (ChannelAccessUtils.hasBasicAccess(channel, recipient, group.getId()))
										newRecipients.add(recipient);
								}
							}
						}
					}
				}

				World world = sender.getWorld();
				Location loc = sender.getLocation();
				double range = channel.getRange();
				Iterator<Player> iter = newRecipients.iterator();
				while (iter.hasNext()) {
					Player recipient = iter.next();
					if ((isPlayerMutedIndividually(recipient.getUniqueId(), sender.getUniqueId()) && !sender.hasPermission(PERM_IMMUTABLE))
							|| (!channel.isUniversal() && recipient.getWorld() != world))
						iter.remove();
					if (recipient.getWorld() == world) {
						if (range >= 0 && recipient.getLocation().distance(loc) > range)
							iter.remove();
					} else {
						if (!channel.isUniversal())
							iter.remove();
					}
				}
				try {
					Set<Player> recipients = ev.getRecipients();
					recipients.clear();
					recipients.addAll(newRecipients);
				} catch (UnsupportedOperationException ex) {
					gPlugin.getLogger().warning("Failed to change chat message recipient set, sending private message to each recipient instead.");
					ev.setCancelled(true);
					String fullMessage = String.format(format, sender.getDisplayName(), message);
					Bukkit.getLogger().info(fullMessage);
					for (Player recipient : newRecipients)
						recipient.sendMessage(fullMessage);
				}
			}
		}
	}

	public void onPlayerWorldChanged(PlayerChangedWorldEvent ev) {
		Player player = ev.getPlayer();
		Group group = getGroup(player.getUniqueId());
		Channel oldChannel = getChannel(player.getUniqueId());
		boolean fromDefault = getDefaultChannel(group, ev.getFrom().getName()).equals(oldChannel);
		Channel newChannel = getDefaultChannel(group, player.getWorld().getName());

		if (!newChannel.equals(oldChannel) && (fromDefault || newChannel.autoJoinIfDefault()))
			changePlayerChannel(player, newChannel.getId(), gAnnounceAutoJoinDefaultChannel);
	}

	public void onPlayerMoveOrUpdate(Player player) {
		gPlayerLastMove.put(player.getUniqueId(), System.currentTimeMillis());
		setPlayerAway(false, player);
	}

	// GETTERS AND SETTERS //

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

	public void setFormatAwayStart(boolean format) {
		gFormatAwayStart = format;
	}

	public void setFormatAwayStop(boolean format) {
		gFormatAwayStop = format;
	}

	public void setFormatAwayKick(boolean format) {
		gFormatAwayKick = format;
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

	public void setReplaceChatFormat(boolean replace) {
		gReplaceChatFormat = replace;
	}

	public void setAnnounceAutoJoinDefaultChannel(boolean announce) {
		gAnnounceAutoJoinDefaultChannel = announce;
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

	public boolean getFormatAwayStart() {
		return gFormatAwayStart;
	}

	public boolean getFormatAwayStop() {
		return gFormatAwayStop;
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

	public Map<String, Group> getGroups() {
		return gGroups;
	}

	public Map<String, Channel> getChannels() {
		return gChannels;
	}

	public void setMuteAll(boolean muteAll) {
		gMuteAll = muteAll;
	}

	public boolean isMuteAll() {
		return gMuteAll;
	}

	public Set<UUID> getKickPlayers() {
		return gKickPlayers;
	}
}
