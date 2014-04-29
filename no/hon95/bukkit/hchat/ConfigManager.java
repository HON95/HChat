package no.hon95.bukkit.hchat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;


public final class ConfigManager {

	private static final String DEFAULT_GROUP = "default";
	private static final String KEY_FORMAT = "%s.%s";
	private static final String CONFIG_HEADER = "Configuration file for hChat.";
	private static final String GROUPS_HEADER = "Group configuration file for hChat."
			+ "\nThe ID needs to be the same as in your permission plugin."
			+ "\n'default' is the default group and is used if none of the others match.";
	private static final String CENSOR_HEADER = "Censor dictionary file for hChat."
			+ "\nWords to the left will be replaced by words to the right.";

	private final HChatPlugin gPlugin;
	private boolean gGroupChange = false;

	public ConfigManager(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	public void load() {
		loadConfig();
		loadGroups();
		loadCensoredWords();
	}

	private void loadConfig() {
		boolean change = false;
		File file = new File(gPlugin.getDataFolder(), "config.yml");
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
		conf.options().copyHeader(true);
		conf.options().header(CONFIG_HEADER);

		if (!file.isFile())
			change = true;
		if (!conf.isBoolean("enable")) {
			conf.set("enable", true);
			change = true;
		}
		if (!conf.isBoolean("check_for_updates")) {
			conf.set("check_for_updates", true);
			change = true;
		}
		if (!conf.isBoolean("update_if_available")) {
			conf.set("update_if_available", true);
			change = true;
		}
		if (!conf.isBoolean("format.name")) {
			conf.set("format.name", true);
			change = true;
		}
		if (!conf.isBoolean("format.chat")) {
			conf.set("format.chat", true);
			change = true;
		}
		if (!conf.isBoolean("format.death")) {
			conf.set("format.death", true);
			change = true;
		}
		if (!conf.isBoolean("format.list")) {
			conf.set("format.list", true);
			change = true;
		}
		if (!conf.isBoolean("format.join")) {
			conf.set("format.join", true);
			change = true;
		}
		if (!conf.isBoolean("format.quit")) {
			conf.set("format.quit", true);
			change = true;
		}
		if (!conf.isBoolean("format.motd")) {
			conf.set("format.motd", true);
			change = true;
		}
		if (!conf.isBoolean("format.me")) {
			conf.set("format.me", true);
			change = true;
		}
		if (!conf.isBoolean("format.tell")) {
			conf.set("format.tell", true);
			change = true;
		}

		gPlugin.setEnable(conf.getBoolean("enable"));
		gPlugin.setCheckForUpdates(conf.getBoolean("check_for_updates"));
		gPlugin.setUpdateIfAvailable(conf.getBoolean("update_if_available"));
		gPlugin.getChatManager().setFormatName(conf.getBoolean("format.name"));
		gPlugin.getChatManager().setFormatChat(conf.getBoolean("format.chat"));
		gPlugin.getChatManager().setFormatDeath(conf.getBoolean("format.death"));
		gPlugin.getChatManager().setFormatList(conf.getBoolean("format.list"));
		gPlugin.getChatManager().setFormatJoin(conf.getBoolean("format.join"));
		gPlugin.getChatManager().setFormatQuit(conf.getBoolean("format.quit"));
		gPlugin.getChatManager().setFormatMotd(conf.getBoolean("format.motd"));
		gPlugin.getChatManager().setFormatMe(conf.getBoolean("format.me"));
		gPlugin.getChatManager().setFormatTell(conf.getBoolean("format.tell"));

		if (change)
			saveFile(conf, file);
	}

	private void loadGroups() {
		File file = new File(gPlugin.getDataFolder(), "groups.yml");
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
		conf.options().copyHeader(true);
		conf.options().header(GROUPS_HEADER);

		if (!file.isFile())
			gGroupChange = true;

		HashSet<HGroup> groups = new HashSet<HGroup>();
		HGroup defGroup = loadDefaultGroup(conf);
		groups.add(defGroup);
		for (String group : conf.getKeys(false)) {
			if (group.equalsIgnoreCase(DEFAULT_GROUP))
				continue;
			groups.add(loadGroup(conf, group, defGroup));
		}
		gPlugin.getChatManager().setGroups(groups);

		if (gGroupChange)
			saveFile(conf, file);
	}

	private HGroup loadDefaultGroup(YamlConfiguration conf) {

		String group = DEFAULT_GROUP;
		convertOldFormat(conf, group);

		String keyName = String.format(KEY_FORMAT, group, "group_name");
		String keyPrefix = String.format(KEY_FORMAT, group, "prefix");
		String keySuffix = String.format(KEY_FORMAT, group, "suffix");
		String keyNameFormat = String.format(KEY_FORMAT, group, "format.name");
		String keyListFormat = String.format(KEY_FORMAT, group, "format.list");
		String keyChatFormat = String.format(KEY_FORMAT, group, "format.chat");
		String keyDeathFormat = String.format(KEY_FORMAT, group, "format.chat");
		String keyJoinFormat = String.format(KEY_FORMAT, group, "format.join");
		String keyQuitFormat = String.format(KEY_FORMAT, group, "format.quit");
		String keyMeFormat = String.format(KEY_FORMAT, group, "format.me");
		String keyTellSenderFormat = String.format(KEY_FORMAT, group, "format.tell_sender");
		String keyTellReceiverFormat = String.format(KEY_FORMAT, group, "format.tell_receiver");
		String keyMotdFormat = String.format(KEY_FORMAT, group, "format.motd");
		String keyCensor = String.format(KEY_FORMAT, group, "censor");
		String keyColorCodes = String.format(KEY_FORMAT, group, "color_codes");
		String keyCanChat = String.format(KEY_FORMAT, group, "can_chat");
		String keyShowPMs = String.format(KEY_FORMAT, group, "show_personal_messages");

		if (!conf.isString(keyName)) {
			conf.set(keyName, "default");
			gGroupChange = true;
		}
		if (!conf.isString(keyPrefix)) {
			conf.set(keyPrefix, "");
			gGroupChange = true;
		}
		if (!conf.isString(keySuffix)) {
			conf.set(keySuffix, "");
			gGroupChange = true;
		}
		if (!conf.isString(keyNameFormat)) {
			conf.set(keyNameFormat, "%p%N%s");
			gGroupChange = true;
		}
		if (!conf.isString(keyListFormat)) {
			conf.set(keyListFormat, "%n");
			gGroupChange = true;
		}
		if (!conf.isString(keyChatFormat)) {
			conf.set(keyChatFormat, "%n&r: %m");
			gGroupChange = true;
		}
		if (!conf.isString(keyDeathFormat)) {
			conf.set(keyDeathFormat, "%n&r%m");
			gGroupChange = true;
		}
		if (!conf.isString(keyJoinFormat)) {
			conf.set(keyJoinFormat, "&e%N joined the game.");
			gGroupChange = true;
		}
		if (!conf.isString(keyQuitFormat)) {
			conf.set(keyQuitFormat, "&e%N left the game.");
			gGroupChange = true;
		}
		if (!conf.isString(keyMeFormat)) {
			conf.set(keyMeFormat, "* %n &r%m");
			gGroupChange = true;
		}
		if (!conf.isString(keyTellSenderFormat)) {
			conf.set(keyTellSenderFormat, "[%n&r->%r&r] %m");
			gGroupChange = true;
		}
		if (!conf.isString(keyTellReceiverFormat)) {
			conf.set(keyTellReceiverFormat, "[%n&r->%r&r] %m");
			gGroupChange = true;
		}
		if (!conf.isList(keyMotdFormat)) {
			ArrayList<String> list = new ArrayList<String>(1);
			list.add("Welcome %n&r, there are %o players online.");
			conf.set(keyMotdFormat, list);
			gGroupChange = true;
		}
		if (!conf.isBoolean(keyCensor)) {
			conf.set(keyCensor, false);
			gGroupChange = true;
		}
		if (!conf.isBoolean(keyColorCodes)) {
			conf.set(keyColorCodes, true);
			gGroupChange = true;
		}
		if (!conf.isBoolean(keyCanChat)) {
			conf.set(keyCanChat, true);
			gGroupChange = true;
		}
		if (!conf.isBoolean(keyShowPMs)) {
			conf.set(keyShowPMs, true);
			gGroupChange = true;
		}

		return loadGroup(conf, group, null);
	}

	private HGroup loadGroup(YamlConfiguration conf, String group, HGroup defGroup) {

		if (defGroup != null)
			convertOldFormat(conf, group);

		String keyName = String.format(KEY_FORMAT, group, "group_name");
		String keyPrefix = String.format(KEY_FORMAT, group, "prefix");
		String keySuffix = String.format(KEY_FORMAT, group, "suffix");
		String keyNameFormat = String.format(KEY_FORMAT, group, "format.name");
		String keyListFormat = String.format(KEY_FORMAT, group, "format.list");
		String keyChatFormat = String.format(KEY_FORMAT, group, "format.chat");
		String keyDeathFormat = String.format(KEY_FORMAT, group, "format.chat");
		String keyJoinFormat = String.format(KEY_FORMAT, group, "format.join");
		String keyQuitFormat = String.format(KEY_FORMAT, group, "format.quit");
		String keyMeFormat = String.format(KEY_FORMAT, group, "format.me");
		String keyTellSenderFormat = String.format(KEY_FORMAT, group, "format.tell_sender");
		String keyTellReceiverFormat = String.format(KEY_FORMAT, group, "format.tell_receiver");
		String keyMotdFormat = String.format(KEY_FORMAT, group, "format.motd");
		String keyCensor = String.format(KEY_FORMAT, group, "censor");
		String keyColorCodes = String.format(KEY_FORMAT, group, "color_codes");
		String keyCanChat = String.format(KEY_FORMAT, group, "can_chat");
		String keyShowPMs = String.format(KEY_FORMAT, group, "show_personal_messages");
		String valName, valPrefix, valSuffix, valNameFormat, valChatFormat, valDeathFormat, valListFormat, valJoinFormat, valQuitFormat;
		String valMeFormat, valTellSenderFormat, valTellReceiverFormat;
		List<String> valMotdFormat;
		boolean valCensor, valColorCodes, valCanChat, valShowPMs;

		if (conf.isString(keyName))
			valName = conf.getString(keyName);
		else
			valName = defGroup.name;

		if (conf.isString(keyPrefix))
			valPrefix = conf.getString(keyPrefix);
		else
			valPrefix = defGroup.prefix;

		if (conf.isString(keySuffix))
			valSuffix = conf.getString(keySuffix);
		else
			valSuffix = defGroup.suffix;

		if (conf.isString(keyNameFormat))
			valNameFormat = conf.getString(keyNameFormat);
		else
			valNameFormat = defGroup.nameFormat;

		if (conf.isString(keyListFormat))
			valListFormat = conf.getString(keyListFormat);
		else
			valListFormat = defGroup.listFormat;

		if (conf.isString(keyChatFormat))
			valChatFormat = conf.getString(keyChatFormat);
		else
			valChatFormat = defGroup.chatFormat;

		if (conf.isString(keyDeathFormat))
			valDeathFormat = conf.getString(keyDeathFormat);
		else
			valDeathFormat = defGroup.deathFormat;

		if (conf.isString(keyJoinFormat))
			valJoinFormat = conf.getString(keyJoinFormat);
		else
			valJoinFormat = defGroup.joinFormat;

		if (conf.isString(keyQuitFormat))
			valQuitFormat = conf.getString(keyQuitFormat);
		else
			valQuitFormat = defGroup.quitFormat;

		if (conf.isString(keyMeFormat))
			valMeFormat = conf.getString(keyMeFormat);
		else
			valMeFormat = defGroup.meFormat;

		if (conf.isString(keyTellSenderFormat))
			valTellSenderFormat = conf.getString(keyTellSenderFormat);
		else
			valTellSenderFormat = defGroup.tellSenderFormat;

		if (conf.isString(keyTellReceiverFormat))
			valTellReceiverFormat = conf.getString(keyTellReceiverFormat);
		else
			valTellReceiverFormat = defGroup.tellReceiverFormat;

		if (conf.isList(keyMotdFormat))
			valMotdFormat = conf.getStringList(keyMotdFormat);
		else
			valMotdFormat = defGroup.motdFormat;

		if (conf.isBoolean(keyCensor))
			valCensor = conf.getBoolean(keyCensor);
		else
			valCensor = defGroup.censor;

		if (conf.isBoolean(keyColorCodes))
			valColorCodes = conf.getBoolean(keyColorCodes);
		else
			valColorCodes = defGroup.colorCodes;

		if (conf.isBoolean(keyCanChat))
			valCanChat = conf.getBoolean(keyCanChat);
		else
			valCanChat = defGroup.canChat;

		if (conf.isBoolean(keyShowPMs))
			valShowPMs = conf.getBoolean(keyShowPMs);
		else
			valShowPMs = defGroup.showPersonalMessages;

		HGroup hgroup = new HGroup();
		hgroup.id = group;
		hgroup.name = valName;
		hgroup.prefix = valPrefix;
		hgroup.suffix = valSuffix;
		hgroup.nameFormat = valNameFormat;
		hgroup.chatFormat = valChatFormat;
		hgroup.deathFormat = valDeathFormat;
		hgroup.listFormat = valListFormat;
		hgroup.joinFormat = valJoinFormat;
		hgroup.quitFormat = valQuitFormat;
		hgroup.meFormat = valMeFormat;
		hgroup.tellSenderFormat = valTellSenderFormat;
		hgroup.tellReceiverFormat = valTellReceiverFormat;
		hgroup.motdFormat = valMotdFormat;
		hgroup.censor = valCensor;
		hgroup.colorCodes = valColorCodes;
		hgroup.canChat = valCanChat;
		hgroup.showPersonalMessages = valShowPMs;

		return hgroup;
	}

	private void loadCensoredWords() {
		boolean change = false;
		File file = new File(gPlugin.getDataFolder(), "censor.yml");
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
		conf.options().copyHeader(true);
		conf.options().header(CENSOR_HEADER);

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
			saveFile(conf, file);
	}

	private void saveFile(YamlConfiguration conf, File file) {
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			conf.save(file);
		} catch (IOException ex) {
			gPlugin.getLogger().severe("Failed to save " + file.getName());
			ex.printStackTrace();
		}
	}

	private void convertOldFormat(YamlConfiguration conf, String group) {

		String keyName = String.format(KEY_FORMAT, group, "name");
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

		String newKeyName = String.format(KEY_FORMAT, group, "group_name");
		String newKeyNameFormat = String.format(KEY_FORMAT, group, "format.name");
		String newKeyListFormat = String.format(KEY_FORMAT, group, "format.list");
		String newKeyChatFormat = String.format(KEY_FORMAT, group, "format.chat");
		String newKeyDeathFormat = String.format(KEY_FORMAT, group, "format.chat");
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
			gGroupChange = true;
		}
		if (conf.isString(keyNameFormat)) {
			conf.set(newKeyNameFormat, conf.getString(keyNameFormat));
			conf.set(keyNameFormat, null);
			gGroupChange = true;
		}
		if (conf.isString(keyListFormat)) {
			conf.set(newKeyListFormat, conf.getString(keyListFormat));
			conf.set(keyListFormat, null);
			gGroupChange = true;
		}
		if (conf.isString(keyChatFormat)) {
			conf.set(newKeyChatFormat, conf.getString(keyChatFormat));
			conf.set(keyChatFormat, null);
			gGroupChange = true;
		}
		if (conf.isString(keyDeathFormat)) {
			conf.set(newKeyDeathFormat, conf.getString(keyDeathFormat));
			conf.set(keyDeathFormat, null);
			gGroupChange = true;
		}
		if (conf.isString(keyJoinFormat)) {
			conf.set(newKeyJoinFormat, conf.getString(keyJoinFormat));
			conf.set(keyJoinFormat, null);
			gGroupChange = true;
		}
		if (conf.isString(keyQuitFormat)) {
			conf.set(newKeyQuitFormat, conf.getString(keyQuitFormat));
			conf.set(keyQuitFormat, null);
			gGroupChange = true;
		}
		if (conf.isString(keyMeFormat)) {
			conf.set(newKeyMeFormat, conf.getString(keyMeFormat));
			conf.set(keyMeFormat, null);
			gGroupChange = true;
		}
		if (conf.isString(keyTellSenderFormat)) {
			conf.set(newKeyTellSenderFormat, conf.getString(keyTellSenderFormat));
			conf.set(keyTellSenderFormat, null);
			gGroupChange = true;
		}
		if (conf.isString(keyTellReceiverFormat)) {
			conf.set(newKeyTellReceiverFormat, conf.getString(keyTellReceiverFormat));
			conf.set(keyTellReceiverFormat, null);
			gGroupChange = true;
		}
		if (conf.isList(keyMotdFormat)) {
			conf.set(newKeyMotdFormat, conf.getStringList(keyMotdFormat));
			conf.set(keyMotdFormat, null);
			gGroupChange = true;
		}
		if (conf.isBoolean(keyColorCodes)) {
			conf.set(newKeyColorCodes, conf.getBoolean(keyColorCodes));
			conf.set(keyColorCodes, null);
			gGroupChange = true;
		}
		if (conf.isBoolean(keyCanChat)) {
			conf.set(newKeyCanChat, conf.getBoolean(keyCanChat));
			conf.set(keyCanChat, null);
			gGroupChange = true;
		}
	}
}
