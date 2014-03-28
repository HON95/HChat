package no.hon95.bukkit.hchat;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;


public final class ConfigManager {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String CONFIG_HEADER = "Configuration file for hChat.";
	private static final String GROUPS_HEADER = "Group configuration file for hChat."
			+ LINE_SEPARATOR + "The ID needs to be the same as in your permission plugin."
			+ LINE_SEPARATOR + "'default' is the default group and is used if none of the others match.";
	private static final String CENSOR_HEADER = "Censor dictionary file for hChat."
			+ LINE_SEPARATOR + "Words to the left will be replaced by words to the right.";

	private final HChatPlugin gPlugin;

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
		if (!conf.isBoolean("enable_timed_updates")) {
			conf.set("enable_timed_updates", false);
			change = true;
		}
		if (!conf.isBoolean("enable_player_list_formatting")) {
			conf.set("enable_player_list_formatting", true);
			change = true;
		}
		if (!conf.isBoolean("use_permission_plugin_prefix_and_suffix")) {
			conf.set("use_permission_plugin_prefix_and_suffix", false);
			change = true;
		}

		gPlugin.setEnable(conf.getBoolean("enable"));
		gPlugin.getInfoManager().setUsePermissionPluginPrefixAndSuffix(conf.getBoolean("use_permission_plugin_prefix_and_suffix"));
		gPlugin.setEnableTimedUpdates(conf.getBoolean("enable_timed_updates"));
		gPlugin.getInfoManager().setFormatList(conf.getBoolean("enable_player_list_formatting"));

		if (change)
			saveFile(conf, file);
	}

	private void loadGroups() {
		boolean change = false;
		File file = new File(gPlugin.getDataFolder(), "groups.yml");
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
		conf.options().copyHeader(true);
		conf.options().header(GROUPS_HEADER);

		if (!file.isFile())
			change = true;
		if (!conf.isConfigurationSection("default")) {
			conf.set("default.name", "Default");
			conf.set("default.prefix", "");
			conf.set("default.suffix", "");
			conf.set("default.format", "%p%n%s: %m");
			conf.set("default.listformat", "%p%n%s");
			conf.set("default.censor", true);
			conf.set("default.colorcodes", true);
			conf.set("default.canchat", true);
			change = true;
		}
		Set<String> keys = conf.getKeys(false);
		HashSet<Group> groups = new HashSet<Group>();
		for (String key : keys) {
			if (!conf.isConfigurationSection(key)) {
				conf.set(key, null);
				change = true;
			}
			if (!conf.isString(key + ".name")) {
				conf.set(key + ".name", "");
				change = true;
			}
			if (!conf.isString(key + ".prefix")) {
				conf.set(key + ".prefix", "");
				change = true;
			}
			if (!conf.isString(key + ".suffix")) {
				conf.set(key + ".suffix", "");
				change = true;
			}
			if (!conf.isString(key + ".format")) {
				conf.set(key + ".format", "%p%n%s: %m");
				change = true;
			}
			if (!conf.isString(key + ".listformat")) {
				conf.set(key + ".listformat", "%p%n%s");
				change = true;
			}
			if (!conf.isBoolean(key + ".censor")) {
				conf.set(key + ".censor", true);
				change = true;
			}
			if (!conf.isBoolean(key + ".colorcodes")) {
				conf.set(key + ".colorcodes", true);
				change = true;
			}
			if (!conf.isBoolean(key + ".canchat")) {
				conf.set(key + ".canchat", true);
				change = true;
			}
			Group group = new Group();
			group.id = key;
			group.name = conf.getString(key + ".name");
			group.prefix = conf.getString(key + ".prefix");
			group.suffix = conf.getString(key + ".suffix");
			group.format = conf.getString(key + ".format");
			group.listFormat = conf.getString(key + ".listformat");
			group.censor = conf.getBoolean(key + ".censor");
			group.colorCodes = conf.getBoolean(key + ".colorcodes");
			group.canChat = conf.getBoolean(key + ".canchat");
			groups.add(group);
		}
		gPlugin.getInfoManager().putGroups(groups);

		if (change)
			saveFile(conf, file);
	}

	private void loadCensoredWords() {
		boolean change = false;
		File file = new File(gPlugin.getDataFolder(), "censor.yml");
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
		conf.options().copyHeader(true);
		conf.options().header(CENSOR_HEADER);

		Map<String, String> dictionary = gPlugin.getInfoManager().getCensoredWords();
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
