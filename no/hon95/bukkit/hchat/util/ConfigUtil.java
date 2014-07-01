package no.hon95.bukkit.hchat.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;


public final class ConfigUtil {

	public static final String KEY_FORMAT = "%s.%s";

	public static String getConfigString(Configuration conf, String section, String key, String def, BooleanObject change, boolean set) {
		String path = section != null ? String.format(KEY_FORMAT, section, key) : key;
		if (conf.isString(path))
			return conf.getString(path);
		if (set) {
			conf.set(path, def);
			change.val = true;
		}
		return def;
	}

	public static boolean getConfigBoolean(Configuration conf, String section, String key, boolean def, BooleanObject change, boolean set) {
		String path = section != null ? String.format(KEY_FORMAT, section, key) : key;
		if (conf.isBoolean(path))
			return conf.getBoolean(path);
		if (set) {
			conf.set(path, def);
			change.val = true;
		}
		return def;
	}

	public static List<String> getConfigList(Configuration conf, String section, String key, List<String> def, BooleanObject change, boolean set) {
		String path = section != null ? String.format(KEY_FORMAT, section, key) : key;
		if (conf.isList(path))
			return conf.getStringList(path);
		if (set) {
			conf.set(path, def);
			change.val = true;
		}
		return def;
	}

	public static Map<String, String> getConfigMap(Configuration conf, String section, String key, Map<String, String> def, BooleanObject change, boolean set) {
		String path = section != null ? String.format(KEY_FORMAT, section, key) : key;
		if (conf.isConfigurationSection(path)) {
			HashMap<String, String> map = new HashMap<String, String>();
			for (String s : conf.getConfigurationSection(path).getKeys(false)) {
				String path2 = String.format(KEY_FORMAT, path, s);
				if (conf.isString(path2))
					map.put(s, conf.getString(path2));
				else
					conf.set(path2, null);
			}
			return map;
		}
		if (set) {
			for (Entry<String, String> e : def.entrySet()) {
				String path2 = String.format(KEY_FORMAT, path, e.getKey());
				conf.set(path2, e.getValue());
			}
			change.val = true;
		}
		return def;
	}

	public static void removeIfSet(Configuration conf, String section, String key, BooleanObject change) {
		String path = section != null ? String.format(KEY_FORMAT, section, key) : key;
		if (conf.isSet(path)) {
			conf.set(path, null);
			change.val = true;
		}
	}

	public static boolean saveYamlConf(YamlConfiguration conf, File file) {
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			conf.save(file);
			return true;
		} catch (IOException ex) {
			Bukkit.getLogger().warning("[hChat] Failed to save " + file.getName());
			ex.printStackTrace();
			return false;
		}
	}

	private ConfigUtil() {}
}
