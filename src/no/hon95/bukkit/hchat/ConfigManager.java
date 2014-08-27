package no.hon95.bukkit.hchat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import no.hon95.bukkit.hchat.common.util.YamlConfigWrapper;

import org.bukkit.ChatColor;


public final class ConfigManager {

	private static final String DEFAULT_GROUP = "default";
	private static final String DEFAULT_CHANNEL = "default";
	private static final String FILENAME_CONFIG = "config.yml";
	private static final String FILENAME_GROUPS = "groups.yml";
	private static final String FILENAME_CHANNELS = "channels.yml";
	private static final String FILENAME_CENSOR = "censor.yml";
	private static final String HEADER_CONFIG = "Configuration file for hChat.";
	private static final String HEADER_GROUPS = "Group configuration file for hChat."
			+ "\nThe ID needs to be the same as in your permission plugin."
			+ "\n'default' is the default group and is used if none of the others match.";
	private static final String HEADER_CHANNELS = "Channel configuration file for hChat."
			+ "\n'default' is the default channel.";
	private static final String HEADER_CENSOR = "Censor dictionary file for hChat."
			+ "\nWords to the left will be replaced by words to the right.";

	private final HChatPlugin gPlugin;
	private YamlConfigWrapper gGroupsYaml = null;
	private YamlConfigWrapper gChannelsYaml = null;

	public ConfigManager(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	public void load() {
		loadConfig();
		loadGroups();
		loadChannels();
		loadCensoredWords();
	}

	//// CONFIG ////

	private void loadConfig() {
		YamlConfigWrapper conf = new YamlConfigWrapper(new File(gPlugin.getDataFolder(), FILENAME_CONFIG), HEADER_CONFIG, gPlugin.getLogger(), true);

		gPlugin.setEnable(conf.getBoolean("enable", true, true));
		gPlugin.setCheckForUpdates(conf.getBoolean("check_for_updates", true, true));
		gPlugin.setUpdateIfAvailable(conf.getBoolean("update_if_available", false, true));
		gPlugin.setCollectData(conf.getBoolean("collect_data", true, true));
		gPlugin.setPlayerNameUpdateInterval(conf.getInt("player_name_update_interval_ticks", 200, true));
		gPlugin.getChatManager().setFormatName(conf.getBoolean("format.name", true, true));
		gPlugin.getChatManager().setFormatChat(conf.getBoolean("format.chat", true, true));
		gPlugin.getChatManager().setFormatDeath(conf.getBoolean("format.death", true, true));
		gPlugin.getChatManager().setFormatList(conf.getBoolean("format.list", true, true));
		gPlugin.getChatManager().setFormatJoin(conf.getBoolean("format.join", true, true));
		gPlugin.getChatManager().setFormatQuit(conf.getBoolean("format.quit", true, true));
		gPlugin.getChatManager().setFormatChannelJoin(conf.getBoolean("format.channel_join", true, true));
		gPlugin.getChatManager().setFormatChannelQuit(conf.getBoolean("format.channel_quit", true, true));
		gPlugin.getChatManager().setFormatAwayStart(conf.getBoolean("format.away_start", true, true));
		gPlugin.getChatManager().setFormatAwayStop(conf.getBoolean("format.away_stop", true, true));
		gPlugin.getChatManager().setFormatAwayKick(conf.getBoolean("format.away_kick", true, true));
		gPlugin.getChatManager().setFormatMotd(conf.getBoolean("format.motd", true, true));
		gPlugin.getChatManager().setFormatMe(conf.getBoolean("format.me", true, true));
		gPlugin.getChatManager().setFormatTell(conf.getBoolean("format.tell", true, true));
		gPlugin.getChatManager().setReplaceChatFormat(conf.getBoolean("replace_chat_format", true, true));
		gPlugin.getChatManager().setAnnounceAutoJoinDefaultChannel(conf.getBoolean("announce_auto_join_default_channel", true, true));

		conf.saveAsync();
	}

	//// GROUPS ////

	private void loadGroups() {
		gGroupsYaml = new YamlConfigWrapper(new File(gPlugin.getDataFolder(), FILENAME_GROUPS), HEADER_GROUPS, gPlugin.getLogger(), true);
		HashSet<Group> groups = new HashSet<Group>();
		Group defGroup = loadGroup(DEFAULT_GROUP, createExampleGroup(), true);
		groups.add(defGroup);
		for (String group : gGroupsYaml.getConfig().getKeys(false)) {
			if (!group.equalsIgnoreCase(DEFAULT_GROUP))
				groups.add(loadGroup(group.toLowerCase(), defGroup, false));
		}
		gPlugin.getChatManager().setGroups(groups);
		saveGroups();
	}

	private Group loadGroup(String groupId, Group defGroup, boolean setIfUnset) {
		Group group = new Group();
		group.setId(groupId);
		group.setName(ChatColor.translateAlternateColorCodes('&', gGroupsYaml.getString(groupId + ".name", groupId, true)));
		group.setPrefix(gGroupsYaml.getString(groupId + ".prefix", defGroup.getPrefix(), setIfUnset));
		group.setSuffix(gGroupsYaml.getString(groupId + ".suffix", defGroup.getSuffix(), setIfUnset));
		group.setNameFormat(gGroupsYaml.getString(groupId + ".format.name", defGroup.getNameFormat(), setIfUnset));
		group.setListFormat(gGroupsYaml.getString(groupId + ".format.list", defGroup.getListFormat(), setIfUnset));
		group.setChatFormat(gGroupsYaml.getString(groupId + ".format.chat", defGroup.getChatFormat(), setIfUnset));
		group.setDeathFormat(gGroupsYaml.getString(groupId + ".format.death", defGroup.getDeathFormat(), setIfUnset));
		group.setJoinFormat(gGroupsYaml.getString(groupId + ".format.join", defGroup.getJoinFormat(), setIfUnset));
		group.setQuitFormat(gGroupsYaml.getString(groupId + ".format.quit", defGroup.getQuitFormat(), setIfUnset));
		group.setChannelJoinFormat(gGroupsYaml.getString(groupId + ".format.channel_join", defGroup.getChannelJoinFormat(), setIfUnset));
		group.setChannelQuitFormat(gGroupsYaml.getString(groupId + ".format.channel_quit", defGroup.getChannelQuitFormat(), setIfUnset));
		group.setAwayStartFormat(gGroupsYaml.getString(groupId + ".format.away_start", defGroup.getAwayStartFormat(), setIfUnset));
		group.setAwayStopFormat(gGroupsYaml.getString(groupId + ".format.away_stop", defGroup.getAwayStopFormat(), setIfUnset));
		group.setAwayKickPlayerMessageFormat(gGroupsYaml.getString(groupId + ".format.away_kick_player_message", defGroup.getAwayKickPlayerMessageFormat(), setIfUnset));
		group.setAwayKickServerMessageFormat(gGroupsYaml.getString(groupId + ".format.away_kick_server_message", defGroup.getAwayKickServerMessageFormat(), setIfUnset));
		group.setMeFormat(gGroupsYaml.getString(groupId + ".format.me", defGroup.getMeFormat(), setIfUnset));
		group.setTellSenderFormat(gGroupsYaml.getString(groupId + ".format.tell_sender", defGroup.getTellSenderFormat(), setIfUnset));
		group.setTellReceiverFormat(gGroupsYaml.getString(groupId + ".format.tell_receiver", defGroup.getTellReceiverFormat(), setIfUnset));
		group.setTellSpyFormat(gGroupsYaml.getString(groupId + ".format.tell_spy", defGroup.getTellSpyFormat(), setIfUnset));
		group.setMotdFormat(gGroupsYaml.getList(groupId + ".format.motd", new ArrayList<String>(defGroup.getMotdFormat()), setIfUnset));
		group.setCensor(gGroupsYaml.getBoolean(groupId + ".censor", defGroup.getCensor(), setIfUnset));
		group.setAllowColorCodes(gGroupsYaml.getBoolean(groupId + ".color_codes", defGroup.getAllowColorCodes(), setIfUnset));
		group.setCanChat(gGroupsYaml.getBoolean(groupId + ".can_chat", defGroup.getCanChat(), setIfUnset));
		group.setShowPersonalMessages(gGroupsYaml.getBoolean(groupId + ".show_personal_messages", defGroup.getShowPersonalMessages(), setIfUnset));
		group.setAwayLongTag(gGroupsYaml.getString(groupId + ".away_long_tag", defGroup.getAwayLongTag(), setIfUnset));
		group.setAwayShortTag(gGroupsYaml.getString(groupId + ".away_short_tag", defGroup.getAwayShortTag(), setIfUnset));
		group.setAwayThreshold(gGroupsYaml.getInt(groupId + ".away_threshold_seconds", defGroup.getAwayThreshold(), setIfUnset));
		group.setKickOnAway(gGroupsYaml.getBoolean(groupId + ".kick_on_away", defGroup.getKickOnAway(), setIfUnset));
		group.setDefaultChannel(gGroupsYaml.getString(groupId + ".channel_default", defGroup.getDefaultChannel(), setIfUnset));
		group.setDefaultWorldChannels(gGroupsYaml.getMap(groupId + ".channel_world_default", defGroup.getDefaultWorldChannels(), setIfUnset));
		return group;
	}

	public void updateGroup(Group group) {
		if (group == null)
			throw new IllegalArgumentException();
		String id = group.getId();
		gGroupsYaml.set(id, null);
		gGroupsYaml.set(id + ".name", group.getName());
		gGroupsYaml.set(id + ".prefix", group.getPrefix());
		gGroupsYaml.set(id + ".suffix", group.getSuffix());
		gGroupsYaml.set(id + ".format.name", group.getNameFormat());
		gGroupsYaml.set(id + ".format.list", group.getListFormat());
		gGroupsYaml.set(id + ".format.chat", group.getChatFormat());
		gGroupsYaml.set(id + ".format.death", group.getDeathFormat());
		gGroupsYaml.set(id + ".format.join", group.getJoinFormat());
		gGroupsYaml.set(id + ".format.quit", group.getQuitFormat());
		gGroupsYaml.set(id + ".format.channel_join", group.getChannelJoinFormat());
		gGroupsYaml.set(id + ".format.channel_quit", group.getChannelQuitFormat());
		gGroupsYaml.set(id + ".format.away_start", group.getAwayStartFormat());
		gGroupsYaml.set(id + ".format.away_stop", group.getAwayStopFormat());
		gGroupsYaml.set(id + ".format.away_kick_player_message", group.getAwayKickPlayerMessageFormat());
		gGroupsYaml.set(id + ".format.away_kick_server_message", group.getAwayKickServerMessageFormat());
		gGroupsYaml.set(id + ".format.me", group.getMeFormat());
		gGroupsYaml.set(id + ".format.tell_sender", group.getTellSenderFormat());
		gGroupsYaml.set(id + ".format.tell_receiver", group.getTellReceiverFormat());
		gGroupsYaml.set(id + ".format.tell_spy", group.getTellSpyFormat());
		gGroupsYaml.set(id + ".format.motd", group.getMotdFormat());
		gGroupsYaml.set(id + ".censor", group.getCensor());
		gGroupsYaml.set(id + ".color_codes", group.getAllowColorCodes());
		gGroupsYaml.set(id + ".can_chat", group.getCanChat());
		gGroupsYaml.set(id + ".show_personal_messages", group.getShowPersonalMessages());
		gGroupsYaml.set(id + ".away_long_tag", group.getAwayLongTag());
		gGroupsYaml.set(id + ".away_short_tag", group.getAwayShortTag());
		gGroupsYaml.set(id + ".away_threshold_seconds", group.getAwayThreshold());
		gGroupsYaml.set(id + ".kick_on_away", group.getKickOnAway());
		gGroupsYaml.set(id + ".channel_default", group.getDefaultChannel());
		for (Entry<String, String> e : group.getDefaultWorldChannels().entrySet()) {
			gGroupsYaml.set(id + ".channel_world_default." + e.getKey(), e.getValue());
		}
	}

	public void saveGroups() {
		gGroupsYaml.saveAsync();
	}

	private Group createExampleGroup() {
		ArrayList<String> motd = new ArrayList<String>();
		motd.add("&2Welcome to %S");
		motd.add(" ");
		HashMap<String, String> worldChannels = new HashMap<String, String>();
		worldChannels.put("example_world", "example_channel");
		Group group = new Group();
		group.setId("example");
		group.setName("Example");
		group.setPrefix("");
		group.setSuffix("");
		group.setNameFormat("%p%N%s");
		group.setListFormat("%p%A%N%s");
		group.setChatFormat("%n&r: %m");
		group.setDeathFormat("%n&r%m");
		group.setJoinFormat("&e%n&r&e joined the game.");
		group.setQuitFormat("&e%n&r&e left the game.");
		group.setChannelJoinFormat("&7%n&r&7 joined channel %f&r&7.");
		group.setChannelQuitFormat("&7%n&r&7 left channel %f&r&7.");
		group.setAwayStartFormat("&7%n&r&7 is now away.");
		group.setAwayStopFormat("&7%n&r&7 is no longer away.");
		group.setAwayKickPlayerMessageFormat("You were kicked for being away.");
		group.setAwayKickServerMessageFormat("&c%n &r&cwas kicked for being away.");
		group.setMeFormat("* %n &r%m");
		group.setTellSenderFormat("[%n&r->%r&r] %m");
		group.setTellReceiverFormat("[%n&r->%r&r] %m");
		group.setTellSpyFormat("[%n&r->%r&r] %m");
		group.setMotdFormat(motd);
		group.setCensor(false);
		group.setAllowColorCodes(true);
		group.setCanChat(true);
		group.setShowPersonalMessages(true);
		group.setAwayLongTag("[AFK]");
		group.setAwayShortTag("&7");
		group.setAwayThreshold(300);
		group.setKickOnAway(false);
		group.setDefaultChannel("default");
		group.setDefaultWorldChannels(worldChannels);
		return group;
	}

	//// CHANNELS ////

	private void loadChannels() {
		gChannelsYaml = new YamlConfigWrapper(new File(gPlugin.getDataFolder(), FILENAME_CHANNELS), HEADER_CHANNELS, gPlugin.getLogger(), true);
		HashSet<Channel> channels = new HashSet<Channel>();
		Channel defChannel = loadChannel(DEFAULT_CHANNEL, true);
		channels.add(defChannel);
		for (String channel : gChannelsYaml.getConfig().getKeys(false)) {
			if (!channel.equalsIgnoreCase(DEFAULT_CHANNEL))
				channels.add(loadChannel(channel.toLowerCase(), false));
		}
		gPlugin.getChatManager().setChannels(channels);
		saveChannels();
	}

	private Channel loadChannel(String channelId, boolean def) {
		Channel channel = new Channel();
		channel.setId(channelId);
		channel.setName(ChatColor.translateAlternateColorCodes('&', gChannelsYaml.getString(channelId + ".name", channelId, true)));
		channel.setOwner(gChannelsYaml.getString(channelId + ".owner", "", true));
		channel.setPassword(gChannelsYaml.getString(channelId + ".password", "", def));
		channel.setChatFormat(gChannelsYaml.getString(channelId + ".chat_format", "", def));
		channel.setPrivate(gChannelsYaml.getBoolean(channelId + ".private", true, true));
		channel.setCensored(gChannelsYaml.getBoolean(channelId + ".censor", false, def));
		channel.setAllowColorCodes(gChannelsYaml.getBoolean(channelId + ".color_codes", false, def));
		channel.setUniversal(gChannelsYaml.getBoolean(channelId + ".universal", true, def));
		channel.setAutoJoinIfDefault(gChannelsYaml.getBoolean(channelId + ".auto_join_if_default", false, def));
		channel.setRange(gChannelsYaml.getDouble(channelId + ".range", -1D, def));
		channel.setMonitorChannels(gChannelsYaml.getList(channelId + ".monitor_channels", new ArrayList<String>(), def));
		channel.setMemberGroups(gChannelsYaml.getList(channelId + ".member_groups", new ArrayList<String>(), def));
		channel.setMembers(gChannelsYaml.getList(channelId + ".members", new ArrayList<String>(), def));
		channel.setBannedMembers(gChannelsYaml.getList(channelId + ".banned_members", new ArrayList<String>(), def));
		return channel;
	}

	public void updateChannel(Channel channel) {
		if (channel == null)
			throw new IllegalArgumentException();
		String id = channel.getId();
		gChannelsYaml.set(id, null);
		gChannelsYaml.set(id + ".name", channel.getName());
		gChannelsYaml.set(id + ".owner", channel.getOwner());
		gChannelsYaml.set(id + ".password", channel.getPassword());
		gChannelsYaml.set(id + ".chat_format", channel.getChatFormat());
		gChannelsYaml.set(id + ".private", channel.isPrivate());
		gChannelsYaml.set(id + ".censor", channel.isCensored());
		gChannelsYaml.set(id + ".color_codes", channel.allowColorCodes());
		gChannelsYaml.set(id + ".universal", channel.isUniversal());
		gChannelsYaml.set(id + ".auto_join_if_default", channel.autoJoinIfDefault());
		gChannelsYaml.set(id + ".range", channel.getRange());
		gChannelsYaml.set(id + ".monitor_channels", channel.getMonitorChannels());
		gChannelsYaml.set(id + ".member_groups", channel.getMemberGroups());
		gChannelsYaml.set(id + ".members", channel.getMembers());
		gChannelsYaml.set(id + ".banned_members", channel.getBannedMembers());
	}

	public void removeChannel(String channel) {
		if (channel == null)
			throw new IllegalArgumentException();
		gChannelsYaml.set(channel, null);
	}

	public void saveChannels() {
		gChannelsYaml.saveAsync();
	}

	//// CENSOR ////

	private void loadCensoredWords() {
		YamlConfigWrapper conf = new YamlConfigWrapper(new File(gPlugin.getDataFolder(), FILENAME_CENSOR), HEADER_CENSOR, gPlugin.getLogger(), true);
		Map<String, String> dictionary = gPlugin.getChatManager().getCensoredWords();
		if (!conf.getFile().isFile()) {
			conf.set("badword", "flower");
			conf.set("Minecraft sucks", "Minecraft is awesome");
		}
		for (String key : conf.getConfig().getKeys(false)) {
			if (!conf.getConfig().isString(key)) {
				conf.set(key, null);
			}
			dictionary.put(key, conf.getConfig().getString(key));
		}
		conf.saveAsync();
	}
}
