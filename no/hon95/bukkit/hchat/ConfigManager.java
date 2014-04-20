package no.hon95.bukkit.hchat;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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

		gPlugin.setEnable(conf.getBoolean("enable"));
		gPlugin.setCheckForUpdates(conf.getBoolean("check_for_updates"));
		gPlugin.getChatManager().setFormatName(conf.getBoolean("format.name"));
		gPlugin.getChatManager().setFormatChat(conf.getBoolean("format.chat"));
		gPlugin.getChatManager().setFormatDeath(conf.getBoolean("format.death"));
		gPlugin.getChatManager().setFormatList(conf.getBoolean("format.list"));
		gPlugin.getChatManager().setFormatJoin(conf.getBoolean("format.join"));
		gPlugin.getChatManager().setFormatQuit(conf.getBoolean("format.quit"));
		gPlugin.getChatManager().setFormatMotd(conf.getBoolean("format.motd"));
		gPlugin.getChatManager().setFormatMe(conf.getBoolean("format.me"));

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
			groups.add(loadGroup(conf, group, defGroup));
		}
		gPlugin.getChatManager().setGroups(groups);

		if (gGroupChange)
			saveFile(conf, file);
	}

	private HGroup loadDefaultGroup(YamlConfiguration conf) {

		String group = DEFAULT_GROUP;
		String keyName = String.format(KEY_FORMAT, group, "name");
		String keyPrefix = String.format(KEY_FORMAT, group, "prefix");
		String keySuffix = String.format(KEY_FORMAT, group, "suffix");
		String keyNameFormat = String.format(KEY_FORMAT, group, "name_format");
		String keyListFormat = String.format(KEY_FORMAT, group, "list_format");
		String keyChatFormat = String.format(KEY_FORMAT, group, "chat_format");
		String keyDeathFormat = String.format(KEY_FORMAT, group, "death_format");
		String keyJoinFormat = String.format(KEY_FORMAT, group, "join_format");
		String keyQuitFormat = String.format(KEY_FORMAT, group, "quit_format");
		String keyMotdFormat = String.format(KEY_FORMAT, group, "motd_format");
		String keyMeFormat = String.format(KEY_FORMAT, group, "me_format");
		String keyCensor = String.format(KEY_FORMAT, group, "censor");
		String keyColorCodes = String.format(KEY_FORMAT, group, "colorcodes");
		String keyCanChat = String.format(KEY_FORMAT, group, "canchat");

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
			conf.set(keyJoinFormat, "%n &r&ejoined the game.");
			gGroupChange = true;
		}
		if (!conf.isString(keyQuitFormat)) {
			conf.set(keyQuitFormat, "%n &r&eleft the game.");
			gGroupChange = true;
		}
		if (!conf.isString(keyMotdFormat)) {
			conf.set(keyMotdFormat, "Welcome %n&r, there are %o players online.");
			gGroupChange = true;
		}
		if (!conf.isString(keyMeFormat)) {
			conf.set(keyMeFormat, "* %n &r%m");
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

		return loadGroup(conf, group, null);
	}

	private HGroup loadGroup(YamlConfiguration conf, String group, HGroup defGroup) {

		String keyName = String.format(KEY_FORMAT, group, "name");
		String keyPrefix = String.format(KEY_FORMAT, group, "prefix");
		String keySuffix = String.format(KEY_FORMAT, group, "suffix");
		String keyNameFormat = String.format(KEY_FORMAT, group, "name_format");
		String keyListFormat = String.format(KEY_FORMAT, group, "list_format");
		String keyChatFormat = String.format(KEY_FORMAT, group, "chat_format");
		String keyDeathFormat = String.format(KEY_FORMAT, group, "death_format");
		String keyJoinFormat = String.format(KEY_FORMAT, group, "join_format");
		String keyQuitFormat = String.format(KEY_FORMAT, group, "quit_format");
		String keyMotdFormat = String.format(KEY_FORMAT, group, "motd_format");
		String keyMeFormat = String.format(KEY_FORMAT, group, "me_format");
		String keyCensor = String.format(KEY_FORMAT, group, "censor");
		String keyColorCodes = String.format(KEY_FORMAT, group, "colorcodes");
		String keyCanChat = String.format(KEY_FORMAT, group, "canchat");
		String valName, valPrefix, valSuffix, valNameFormat, valChatFormat, valDeathFormat, valListFormat, valJoinFormat, valQuitFormat, valMotdFormat, valMeFormat;
		boolean valCensor, valColorCodes, valCanChat;

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

		if (conf.isString(keyMotdFormat))
			valMotdFormat = conf.getString(keyMotdFormat);
		else
			valMotdFormat = defGroup.motdFormat;

		if (conf.isString(keyMeFormat))
			valMeFormat = conf.getString(keyMeFormat);
		else
			valMeFormat = defGroup.meFormat;

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
		hgroup.motdFormat = valMotdFormat;
		hgroup.meFormat = valMeFormat;
		hgroup.censor = valCensor;
		hgroup.colorCodes = valColorCodes;
		hgroup.canChat = valCanChat;

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
}
