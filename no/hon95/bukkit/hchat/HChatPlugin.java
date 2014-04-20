package no.hon95.bukkit.hchat;

import net.gravitydevelopment.updater.Updater;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public final class HChatPlugin extends JavaPlugin {

	/*
	 * NEW
	 * Format join, quit, death, display name, motd, me
	 * Using evilmidget and gravity
	 * Changed config keys
	 * clearchat, colors, me command
	 * more permissions
	 * new variables, N, H, o, O, S, v
	 */

	private static final int SERVER_MODS_API_ID = 0;
	private static final long TASK_DELAY_UPDATE = 20L;
	private static final long TASK_DELAY_UPDATE_PENDING_NAMES = 5L;

	private final ConfigManager gConfigManager = new ConfigManager(this);
	private final PlayerListener gPlayerListener = new PlayerListener(this);
	private HCommandExecutor gCommandExecutor = new HCommandExecutor(this);
	private ChatManager gChatManager = new ChatManager(this);
	private Permission gPermissionPlugin = null;

	private boolean gEnable = true;
	private boolean gCheckForUpdates = true;

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
		getCommand("hchat").setExecutor(gCommandExecutor);
		getCommand("clearchat").setExecutor(gCommandExecutor);
		getCommand("colors").setExecutor(gCommandExecutor);
		getCommand("me").setExecutor(gCommandExecutor);

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

		if (gCheckForUpdates)
			new Updater(this, SERVER_MODS_API_ID, getFile(), Updater.UpdateType.DEFAULT, false);
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
}
