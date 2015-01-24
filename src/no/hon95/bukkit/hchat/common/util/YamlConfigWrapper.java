package no.hon95.bukkit.hchat.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public final class YamlConfigWrapper {

	private final File gFile;
	private final String gHeader;
	private final Logger gLogger;
	private YamlConfiguration gConf = null;
	private volatile boolean gChange = false;
	private final Object gSaveLock = new Object();

	public YamlConfigWrapper(File file, String header, Logger logger, boolean load) {
		gFile = file;
		gHeader = header;
		gLogger = logger;
		if (load)
			load();
	}

	public File getFile() {
		return gFile;
	}

	public String getHeader() {
		return gHeader;
	}

	public YamlConfiguration getConfig() {
		return gConf;
	}

	public boolean hasChanged() {
		return gChange;
	}

	public boolean load() {
		gChange = false;
		gConf = new YamlConfiguration();
		boolean success = false;
		try {
			gConf.load(gFile);
			success = true;
		} catch (FileNotFoundException ex) {
			gChange = true;
			gLogger.warning("Configuration file '" + gFile.getName() + "' not found, creating new.");
		} catch (IOException ex) {
			gChange = true;
			gLogger.warning("Failed to load configuration file '" + gFile.getName() + "', creating new.");
			ex.printStackTrace();
		} catch (InvalidConfigurationException ex) {
			gChange = true;
			gLogger.warning("Configuration file '" + gFile.getName() + "' is invalid, creating new.");
			ex.printStackTrace();
		}
		if (gChange) {
			gFile.renameTo(new File(gFile.getPath() + ".old"));
		}
		gConf.options().copyHeader(true);
		gConf.options().header(gHeader);
		return success;
	}

	public void saveAsync() {
		if (gChange) {
			new Thread(new Runnable() {
				public void run() {
					save();
				}
			}).start();
		}
	}

	public synchronized boolean save() {
		if (gChange) {
			gChange = false;
			try {
				if (!gFile.isFile()) {
					gFile.getParentFile().mkdirs();
					gFile.createNewFile();
				}
				synchronized (gSaveLock) {
					gConf.save(gFile);
				}
			} catch (Exception ex) {
				gChange = true;
				gLogger.warning("Failed to save configuration file '" + gFile.getName() + "'.");
				ex.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public String getString(String path, String def, boolean set) {
		String value = def;
		if (gConf.isString(path)) {
			value = gConf.getString(path);
		} else if (set) {
			gConf.set(path, def);
			gChange = true;
		}
		return value;
	}

	public boolean getBoolean(String path, boolean def, boolean set) {
		boolean value = def;
		if (gConf.isBoolean(path)) {
			value = gConf.getBoolean(path);
		} else if (set) {
			gConf.set(path, def);
			gChange = true;
		}
		return value;
	}

	public int getInt(String path, int def, boolean set) {
		int value = def;
		if (gConf.isInt(path)) {
			value = gConf.getInt(path);
		} else if (set) {
			gConf.set(path, def);
			gChange = true;
		}
		return value;
	}

	public long getLong(String path, long def, boolean set) {
		long value = def;
		if (gConf.isLong(path)) {
			value = gConf.getLong(path);
		} else if (set) {
			gConf.set(path, def);
			gChange = true;
		}
		return value;
	}

	public double getDouble(String path, double def, boolean set) {
		double value = def;
		if (gConf.isDouble(path)) {
			value = gConf.getDouble(path);
		} else if (gConf.isInt(path)) {
			value = gConf.getInt(path);
		} else if (set) {
			gConf.set(path, def);
			gChange = true;
		}
		return value;
	}

	public List<String> getList(String path, List<String> def, boolean set) {
		List<String> value = def;
		if (gConf.isList(path)) {
			value = gConf.getStringList(path);
		} else if (set) {
			gConf.set(path, def);
			gChange = true;
		}
		return value;
	}

	public Map<String, String> getMap(String path, Map<String, String> def, boolean set) {
		Map<String, String> map = def;
		if (gConf.isConfigurationSection(path)) {
			map = new HashMap<String, String>();
			for (String key : gConf.getConfigurationSection(path).getKeys(false)) {
				String path2 = path + '.' + key;
				if (gConf.isString(path2))
					map.put(key, gConf.getString(path2));
				else
					gConf.set(path2, null);
			}
		} else if (set) {
			for (Entry<String, String> e : def.entrySet()) {
				String path2 = path + '.' + e.getKey();
				;
				gConf.set(path2, e.getValue());
			}
			gChange = true;
		}
		return map;
	}

	public void set(String path, Object value) {
		if (gConf.get(path) != value) {
			synchronized (gSaveLock) {
				gConf.set(path, value);
			}
			gChange = true;
		}
	}

	public void remove(String path) {
		if (gConf.isSet(path)) {
			gConf.set(path, null);
			gChange = true;
		}
	}
}
