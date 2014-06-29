package no.hon95.bukkit.hchat;

import static no.hon95.bukkit.hchat.util.ConfigUtil.KEY_FORMAT;
import static no.hon95.bukkit.hchat.util.ConfigUtil.getConfigBoolean;
import static no.hon95.bukkit.hchat.util.ConfigUtil.getConfigList;
import static no.hon95.bukkit.hchat.util.ConfigUtil.getConfigMap;
import static no.hon95.bukkit.hchat.util.ConfigUtil.getConfigString;
import static no.hon95.bukkit.hchat.util.ConfigUtil.saveYamlConf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.hon95.bukkit.hchat.util.BooleanObject;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;


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
	private BooleanObject gGroupsChange = new BooleanObject(false);
	private BooleanObject gChannelsChange = new BooleanObject(false);
	private YamlConfiguration gChannelsYaml = null;
	private final Object gChannelsSaveLock = new Object();

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
		File file = new File(gPlugin.getDataFolder(), FILENAME_CONFIG);
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
		conf.options().copyHeader(true);
		conf.options().header(HEADER_CONFIG);

		BooleanObject change = new BooleanObject(false);
		if (!file.isFile())
			change.val = true;

		gPlugin.setEnable(getConfigBoolean(conf, null, "enable", true, change, true));
		gPlugin.setCheckForUpdates(getConfigBoolean(conf, null, "check_for_updates", true, change, true));
		gPlugin.setUpdateIfAvailable(getConfigBoolean(conf, null, "update_if_available", true, change, true));
		gPlugin.getMetricsManager().setCollectData(getConfigBoolean(conf, null, "collect_data", true, change, true));
		gPlugin.getChatManager().setFormatName(getConfigBoolean(conf, "format", "name", true, change, true));
		gPlugin.getChatManager().setFormatChat(getConfigBoolean(conf, "format", "chat", true, change, true));
		gPlugin.getChatManager().setFormatDeath(getConfigBoolean(conf, "format", "death", true, change, true));
		gPlugin.getChatManager().setFormatList(getConfigBoolean(conf, "format", "list", true, change, true));
		gPlugin.getChatManager().setFormatJoin(getConfigBoolean(conf, "format", "join", true, change, true));
		gPlugin.getChatManager().setFormatQuit(getConfigBoolean(conf, "format", "quit", true, change, true));
		gPlugin.getChatManager().setFormatChannelJoin(getConfigBoolean(conf, "format", "channel_join", true, change, true));
		gPlugin.getChatManager().setFormatChannelQuit(getConfigBoolean(conf, "format", "channel_quit", true, change, true));
		gPlugin.getChatManager().setFormatMotd(getConfigBoolean(conf, "format", "motd", true, change, true));
		gPlugin.getChatManager().setFormatMe(getConfigBoolean(conf, "format", "me", true, change, true));
		gPlugin.getChatManager().setFormatTell(getConfigBoolean(conf, "format", "tell", true, change, true));

		if (change.val)
			saveYamlConf(conf, file);
	}

	//// GROUPS ////

	private void loadGroups() {
		File file = new File(gPlugin.getDataFolder(), FILENAME_GROUPS);
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
		conf.options().copyHeader(true);
		conf.options().header(HEADER_GROUPS);

		gGroupsChange.val = false;
		if (!file.isFile())
			gGroupsChange.val = true;

		HashSet<HGroup> groups = new HashSet<HGroup>();
		HGroup defGroup = loadDefaultGroup(conf);
		groups.add(defGroup);
		for (String group : conf.getKeys(false)) {
			if (group.equalsIgnoreCase(DEFAULT_GROUP))
				continue;
			group = group.toLowerCase();
			HGroup hgroup = loadGroup(conf, group, defGroup);
			groups.add(hgroup);
		}
		gPlugin.getChatManager().setGroups(groups);

		if (gGroupsChange.val)
			saveYamlConf(conf, file);
		gGroupsChange.val = false;
	}

	private HGroup loadDefaultGroup(Configuration conf) {
		String group = DEFAULT_GROUP;
		convertOldGroupsFormat(conf, group);
		HashMap<String, String> worldChannels = new HashMap<String, String>();
		worldChannels.put("example_world", "example_channel");
		HGroup hgroup = new HGroup();
		hgroup.setId(group);
		hgroup.setName(ChatColor.translateAlternateColorCodes('&', getConfigString(conf, group, "name", group, gGroupsChange, true)));
		hgroup.setPrefix(getConfigString(conf, group, "prefix", "", gGroupsChange, true));
		hgroup.setSuffix(getConfigString(conf, group, "suffix", "", gGroupsChange, true));
		hgroup.setNameFormat(getConfigString(conf, group, "format.name", "%p%N%s", gGroupsChange, true));
		hgroup.setListFormat(getConfigString(conf, group, "format.list", "%n", gGroupsChange, true));
		hgroup.setChatFormat(getConfigString(conf, group, "format.chat", "%n&r: %m", gGroupsChange, true));
		hgroup.setDeathFormat(getConfigString(conf, group, "format.death", "%n&r%m", gGroupsChange, true));
		hgroup.setJoinFormat(getConfigString(conf, group, "format.join", "&e%N joined the game.", gGroupsChange, true));
		hgroup.setQuitFormat(getConfigString(conf, group, "format.quit", "&e%N left the game.", gGroupsChange, true));
		hgroup.setChannelJoinFormat(getConfigString(conf, group, "format.channel_join", "&7%n joined channel %c&r&7.", gGroupsChange, true));
		hgroup.setChannelQuitFormat(getConfigString(conf, group, "format.channel_quit", "&7%n left channel %c&r&7.", gGroupsChange, true));
		hgroup.setMeFormat(getConfigString(conf, group, "format.me", "* %n &r%m", gGroupsChange, true));
		hgroup.setTellSenderFormat(getConfigString(conf, group, "format.tell_sender", "[%n&r->%r&r] %m", gGroupsChange, true));
		hgroup.setTellReceiverFormat(getConfigString(conf, group, "format.tell_receiver", "[%n&r->%r&r] %m", gGroupsChange, true));
		hgroup.setTellSpyFormat(getConfigString(conf, group, "format.tell_spy", "[%n&r->%r&r] %m", gGroupsChange, true));
		hgroup.setMotdFormat(getConfigList(conf, group, "format.motd", new ArrayList<String>(), gGroupsChange, true));
		hgroup.setCensor(getConfigBoolean(conf, group, "censor", false, gGroupsChange, true));
		hgroup.setColorCodes(getConfigBoolean(conf, group, "color_codes", true, gGroupsChange, true));
		hgroup.setCanChat(getConfigBoolean(conf, group, "can_chat", true, gGroupsChange, true));
		hgroup.setShowPersonalMessages(getConfigBoolean(conf, group, "show_personal_messages", true, gGroupsChange, true));
		hgroup.setDefaultChannel(getConfigString(conf, group, "channel_default", "default", gChannelsChange, true));
		hgroup.setDefaultWorldChannels(getConfigMap(conf, group, "channel_world_default", worldChannels, gGroupsChange, true));
		return hgroup;
	}

	private HGroup loadGroup(Configuration conf, String group, HGroup defGroup) {
		convertOldGroupsFormat(conf, group);
		HGroup hgroup = new HGroup();
		hgroup.setId(group);
		hgroup.setName(ChatColor.translateAlternateColorCodes('&', getConfigString(conf, group, "name", group, gGroupsChange, false)));
		hgroup.setPrefix(getConfigString(conf, group, "prefix", defGroup.getPrefix(), gGroupsChange, false));
		hgroup.setSuffix(getConfigString(conf, group, "suffix", defGroup.getSuffix(), gGroupsChange, false));
		hgroup.setNameFormat(getConfigString(conf, group, "format.name", defGroup.getNameFormat(), gGroupsChange, false));
		hgroup.setListFormat(getConfigString(conf, group, "format.list", defGroup.getListFormat(), gGroupsChange, false));
		hgroup.setChatFormat(getConfigString(conf, group, "format.chat", defGroup.getChatFormat(), gGroupsChange, false));
		hgroup.setDeathFormat(getConfigString(conf, group, "format.death", defGroup.getDeathFormat(), gGroupsChange, false));
		hgroup.setJoinFormat(getConfigString(conf, group, "format.join", defGroup.getJoinFormat(), gGroupsChange, false));
		hgroup.setQuitFormat(getConfigString(conf, group, "format.quit", defGroup.getQuitFormat(), gGroupsChange, false));
		hgroup.setChannelJoinFormat(getConfigString(conf, group, "format.channel_join", defGroup.getChannelJoinFormat(), gGroupsChange, false));
		hgroup.setChannelQuitFormat(getConfigString(conf, group, "format.channel_quit", defGroup.getChannelQuitFormat(), gGroupsChange, false));
		hgroup.setMeFormat(getConfigString(conf, group, "format.me", defGroup.getMeFormat(), gGroupsChange, false));
		hgroup.setTellSenderFormat(getConfigString(conf, group, "format.tell_sender", defGroup.getTellSenderFormat(), gGroupsChange, false));
		hgroup.setTellReceiverFormat(getConfigString(conf, group, "format.tell_receiver", defGroup.getTellReceiverFormat(), gGroupsChange, false));
		hgroup.setTellSpyFormat(getConfigString(conf, group, "format.tell_spy", defGroup.getTellSpyFormat(), gGroupsChange, false));
		hgroup.setMotdFormat(getConfigList(conf, group, "format.motd", defGroup.getMotdFormat(), gGroupsChange, false));
		hgroup.setCensor(getConfigBoolean(conf, group, "censor", defGroup.getCensor(), gGroupsChange, false));
		hgroup.setColorCodes(getConfigBoolean(conf, group, "color_codes", defGroup.getColorCodes(), gGroupsChange, false));
		hgroup.setCanChat(getConfigBoolean(conf, group, "can_chat", defGroup.getCanChat(), gGroupsChange, false));
		hgroup.setShowPersonalMessages(getConfigBoolean(conf, group, "show_personal_messages", defGroup.getShowPersonalMessages(), gGroupsChange, false));
		hgroup.setDefaultChannel(getConfigString(conf, group, "channel_default", defGroup.getDefaultChannel(), gChannelsChange, false));
		hgroup.setDefaultWorldChannels(getConfigMap(conf, group, "channel_world_default", defGroup.getDefaultWorldChannels(), gGroupsChange, false));
		return hgroup;
	}

	//// CHANNELS ////

	private void loadChannels() {
		File file = new File(gPlugin.getDataFolder(), FILENAME_CHANNELS);
		gChannelsYaml = YamlConfiguration.loadConfiguration(file);
		gChannelsYaml.options().copyHeader(true);
		gChannelsYaml.options().header(HEADER_CHANNELS);

		gChannelsChange.val = false;
		if (!file.isFile())
			gChannelsChange.val = true;

		HashSet<HChannel> channels = new HashSet<HChannel>();
		HChannel defChannel = loadChannel(gChannelsYaml, DEFAULT_CHANNEL);
		channels.add(defChannel);
		for (String channel : gChannelsYaml.getKeys(false)) {
			if (channel.equalsIgnoreCase(DEFAULT_CHANNEL))
				continue;
			channel = channel.toLowerCase();
			HChannel hchannel = loadChannel(gChannelsYaml, channel);
			channels.add(hchannel);
		}
		gPlugin.getChatManager().setChannels(channels);

		if (gChannelsChange.val)
			saveYamlConf(gChannelsYaml, file);
		gChannelsChange.val = false;
	}

	private HChannel loadChannel(Configuration conf, String channel) {
		String id = channel;
		String name = ChatColor.translateAlternateColorCodes('&', getConfigString(conf, channel, "name", channel, gChannelsChange, true));
		String owner = getConfigString(conf, channel, "owner", "", gChannelsChange, true);
		String password = getConfigString(conf, channel, "password", "", gChannelsChange, true);
		String chatFormat = getConfigString(conf, channel, "chat_format", "", gChannelsChange, true);
		boolean isPrivate = getConfigBoolean(conf, channel, "private", false, gChannelsChange, true);
		boolean isCensored = getConfigBoolean(conf, channel, "censor", false, gChannelsChange, true);
		boolean allowColorCodes = getConfigBoolean(conf, channel, "color_codes", false, gChannelsChange, true);
		boolean isUniversal = getConfigBoolean(conf, channel, "universal", true, gChannelsChange, true);
		List<String> monitorChannels = getConfigList(conf, channel, "monitor_channels", new ArrayList<String>(), gChannelsChange, true);
		List<String> members = getConfigList(conf, channel, "members", new ArrayList<String>(), gChannelsChange, true);
		List<String> bannedMembers = getConfigList(conf, channel, "banned_members", new ArrayList<String>(), gChannelsChange, true);

		return new HChannel(id, name, owner, password, chatFormat, isPrivate, isCensored, allowColorCodes, isUniversal, monitorChannels, members, bannedMembers);
	}

	public void addChannel(HChannel channel) {
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
		gChannelsYaml.set(id + ".monitor_channels", channel.getMonitorChannels());
		gChannelsYaml.set(id + ".members", channel.getMembers());
		gChannelsYaml.set(id + ".banned_members", channel.getBannedMembers());
	}

	public void removeChannel(String channel) {
		if (channel == null)
			throw new IllegalArgumentException();
		gChannelsYaml.set(channel, null);
	}

	public void saveChannels() {
		final File file = new File(gPlugin.getDataFolder(), FILENAME_CHANNELS);
		new Thread(new Runnable() {
			public void run() {
				synchronized (gChannelsSaveLock) {
					saveYamlConf(gChannelsYaml, file);
				}
			}
		}).start();
	}

	//// CENSOR ////

	private void loadCensoredWords() {
		boolean change = false;
		File file = new File(gPlugin.getDataFolder(), FILENAME_CENSOR);
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
		conf.options().copyHeader(true);
		conf.options().header(HEADER_CENSOR);

		Map<String, String> dictionary = gPlugin.getChatManager().getCensoredWords();
		Set<String> keys = conf.getKeys(false);
		if (!file.isFile()) {
			conf.set("somebadword", "flower");
			conf.set("i will slay you and steal your hair", "i like you");
			keys = conf.getKeys(false);
			change = true;
		}
		for (String key : keys) {
			if (!conf.isString(key)) {
				conf.set(key, null);
				change = true;
			}
			dictionary.put(key, conf.getString(key));
		}

		if (change)
			saveYamlConf(conf, file);
	}

	//// CONVERT ////

	private void convertOldGroupsFormat(Configuration conf, String group) {

		String keyName = String.format(KEY_FORMAT, group, "group_name");
		String keyNameFormat = String.format(KEY_FORMAT, group, "name_format");
		String keyListFormat = String.format(KEY_FORMAT, group, "list_format");
		String keyChatFormat = String.format(KEY_FORMAT, group, "chat_format");
		String keyDeathFormat = String.format(KEY_FORMAT, group, "death_format");
		String keyJoinFormat = String.format(KEY_FORMAT, group, "join_format");
		String keyQuitFormat = String.format(KEY_FORMAT, group, "quit_format");
		String keyMeFormat = String.format(KEY_FORMAT, group, "me_format");
		String keyTellSenderFormat = String.format(KEY_FORMAT, group, "tell_sender_format");
		String keyTellReceiverFormat = String.format(KEY_FORMAT, group, "tell_receiver_format");
		String keyMotdFormat = String.format(KEY_FORMAT, group, "motd_format");
		String keyColorCodes = String.format(KEY_FORMAT, group, "colorcodes");
		String keyCanChat = String.format(KEY_FORMAT, group, "canchat");

		String newKeyName = String.format(KEY_FORMAT, group, "name");
		String newKeyNameFormat = String.format(KEY_FORMAT, group, "format.name");
		String newKeyListFormat = String.format(KEY_FORMAT, group, "format.list");
		String newKeyChatFormat = String.format(KEY_FORMAT, group, "format.chat");
		String newKeyDeathFormat = String.format(KEY_FORMAT, group, "format.death");
		String newKeyJoinFormat = String.format(KEY_FORMAT, group, "format.join");
		String newKeyQuitFormat = String.format(KEY_FORMAT, group, "format.quit");
		String newKeyMeFormat = String.format(KEY_FORMAT, group, "format.me");
		String newKeyTellSenderFormat = String.format(KEY_FORMAT, group, "format.tell_sender");
		String newKeyTellReceiverFormat = String.format(KEY_FORMAT, group, "format.tell_receiver");
		String newKeyMotdFormat = String.format(KEY_FORMAT, group, "format.motd");
		String newKeyColorCodes = String.format(KEY_FORMAT, group, "color_codes");
		String newKeyCanChat = String.format(KEY_FORMAT, group, "can_chat");

		if (conf.isString(keyName)) {
			conf.set(newKeyName, conf.getString(keyName));
			conf.set(keyName, null);
			gGroupsChange.val = true;
		}
		if (conf.isString(keyNameFormat)) {
			conf.set(newKeyNameFormat, conf.getString(keyNameFormat));
			conf.set(keyNameFormat, null);
			gGroupsChange.val = true;
		}
		if (conf.isString(keyListFormat)) {
			conf.set(newKeyListFormat, conf.getString(keyListFormat));
			conf.set(keyListFormat, null);
			gGroupsChange.val = true;
		}
		if (conf.isString(keyChatFormat)) {
			conf.set(newKeyChatFormat, conf.getString(keyChatFormat));
			conf.set(keyChatFormat, null);
			gGroupsChange.val = true;
		}
		if (conf.isString(keyDeathFormat)) {
			conf.set(newKeyDeathFormat, conf.getString(keyDeathFormat));
			conf.set(keyDeathFormat, null);
			gGroupsChange.val = true;
		}
		if (conf.isString(keyJoinFormat)) {
			conf.set(newKeyJoinFormat, conf.getString(keyJoinFormat));
			conf.set(keyJoinFormat, null);
			gGroupsChange.val = true;
		}
		if (conf.isString(keyQuitFormat)) {
			conf.set(newKeyQuitFormat, conf.getString(keyQuitFormat));
			conf.set(keyQuitFormat, null);
			gGroupsChange.val = true;
		}
		if (conf.isString(keyMeFormat)) {
			conf.set(newKeyMeFormat, conf.getString(keyMeFormat));
			conf.set(keyMeFormat, null);
			gGroupsChange.val = true;
		}
		if (conf.isString(keyTellSenderFormat)) {
			conf.set(newKeyTellSenderFormat, conf.getString(keyTellSenderFormat));
			conf.set(keyTellSenderFormat, null);
			gGroupsChange.val = true;
		}
		if (conf.isString(keyTellReceiverFormat)) {
			conf.set(newKeyTellReceiverFormat, conf.getString(keyTellReceiverFormat));
			conf.set(keyTellReceiverFormat, null);
			gGroupsChange.val = true;
		}
		if (conf.isList(keyMotdFormat)) {
			conf.set(newKeyMotdFormat, conf.getStringList(keyMotdFormat));
			conf.set(keyMotdFormat, null);
			gGroupsChange.val = true;
		}
		if (conf.isBoolean(keyColorCodes)) {
			conf.set(newKeyColorCodes, conf.getBoolean(keyColorCodes));
			conf.set(keyColorCodes, null);
			gGroupsChange.val = true;
		}
		if (conf.isBoolean(keyCanChat)) {
			conf.set(newKeyCanChat, conf.getBoolean(keyCanChat));
			conf.set(keyCanChat, null);
			gGroupsChange.val = true;
		}
	}
}
