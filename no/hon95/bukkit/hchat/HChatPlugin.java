package no.hon95.bukkit.hchat;

import static no.hon95.bukkit.hchat.HChatCommands.CMD_CLEAR_CHAT;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_COLORS;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_HCHAT;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_ME;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_TELL;
import net.gravitydevelopment.updater.Updater;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public final class HChatPlugin extends JavaPlugin {

	private static final int SERVER_MODS_API_ID = 77039;
	private static final long TASK_DELAY_UPDATE = 20L;
	private static final long TASK_DELAY_UPDATE_PENDING_NAMES = 5L;

	private final ConfigManager gConfigManager = new ConfigManager(this);
	private final PlayerListener gPlayerListener = new PlayerListener(this);
	private HCommandExecutor gCommandExecutor = new HCommandExecutor(this);
	private ChatManager gChatManager = new ChatManager(this);
	private Permission gPermissionPlugin = null;

	private boolean gEnable = true;
	private boolean gCheckForUpdates = true;
	private boolean gUpdateIfAvailable = true;

	@Override
	public void onLoad() {
		gConfigManager.load();
	}

	@Override
	public void onEnable() {
		if (!gEnable) {
			getLogger().warning("Plugin disabled by config.");
			getPluginLoader().disablePlugin(this);
			return;
		}

		hookIntoVault();

		getServer().getPluginManager().registerEvents(gPlayerListener, this);
		getCommand(CMD_HCHAT).setExecutor(gCommandExecutor);
		getCommand(CMD_CLEAR_CHAT).setExecutor(gCommandExecutor);
		getCommand(CMD_COLORS).setExecutor(gCommandExecutor);
		getCommand(CMD_ME).setExecutor(gCommandExecutor);
		getCommand(CMD_TELL).setExecutor(gCommandExecutor);

		getServer().getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				gChatManager.doPendingNameUpdates();
			}
		}, 0, TASK_DELAY_UPDATE_PENDING_NAMES);
		getServer().getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				gChatManager.update();
			}
		}, 0, TASK_DELAY_UPDATE);

		if (gCheckForUpdates) {
			final HChatPlugin plugin = this;
			getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
				public void run() {
					Updater.UpdateType type;
					if (gUpdateIfAvailable)
						type = Updater.UpdateType.DEFAULT;
					else
						type = Updater.UpdateType.NO_DOWNLOAD;
					Updater updater = new Updater(plugin, SERVER_MODS_API_ID, getFile(), type, false);
					switch (updater.getResult()) {
					case SUCCESS:
						plugin.getLogger().info("An update has been downloaded: " + updater.getLatestName());
						break;
					case UPDATE_AVAILABLE:
						plugin.getLogger().info("An update is available: " + updater.getLatestName());
						break;
					default:
						break;
					}
				}
			});
		}
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
	}

	private boolean hookIntoVault() {
		try {
			Class.forName("net.milkbowl.vault.permission.Permission");
		} catch (ClassNotFoundException ex) {
			getLogger().warning("Vault not found!");
			return false;
		}
		RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
		gPermissionPlugin = rsp.getProvider();
		if (gPermissionPlugin == null) {
			getLogger().warning("Failed to hook into Vault!");
			return false;
		}
		return true;
	}

	public String getPlayerGroup(String world, String player) {
		if (gPermissionPlugin == null)
			return null;
		return gPermissionPlugin.getPrimaryGroup(world, player);
	}

	public ConfigManager getConfigManager() {
		return gConfigManager;
	}

	public ChatManager getChatManager() {
		return gChatManager;
	}

	public void setEnable(boolean enable) {
		gEnable = enable;
	}

	public void setCheckForUpdates(boolean checkForUpdates) {
		gCheckForUpdates = checkForUpdates;
	}

	public void setUpdateIfAvailable(boolean update) {
		gUpdateIfAvailable = update;
	}
}
