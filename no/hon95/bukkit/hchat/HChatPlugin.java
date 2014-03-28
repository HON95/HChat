package no.hon95.bukkit.hchat;

import no.hon95.bukkit.hchat.permissionmanager.BPermissionsPermissionManager;
import no.hon95.bukkit.hchat.permissionmanager.EmptyPermissionManager;
import no.hon95.bukkit.hchat.permissionmanager.PermissionManager;
import no.hon95.bukkit.hchat.permissionmanager.PermissionsBukkitPermissionManager;
import no.hon95.bukkit.hchat.permissionmanager.PermissionsExPermissionManager;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public final class HChatPlugin extends JavaPlugin {

	private final ConfigManager gConfigManager = new ConfigManager(this);
	private final PlayerListener gChatListener = new PlayerListener(this);
	private CommandExecutor gCommandExecutor = new CommandExecutor(this);
	private InfoManager gInfoManager = new InfoManager(this);
	private PermissionManager gPermissionManager;

	private boolean gEnable = true;
	private boolean gEnableTimedUpdates = true;

	@Override
	public void onLoad() {
		gConfigManager.load();
		loadPermissionManager();
	}

	@Override
	public void onEnable() {
		if (!gEnable) {
			getLogger().warning("Plugin disabled by config.");
			getPluginLoader().disablePlugin(this);
			return;
		}

		getServer().getPluginManager().registerEvents(gChatListener, this);
		getCommand("hchat").setExecutor(gCommandExecutor);

		gInfoManager.update();
		if (gEnableTimedUpdates) {
			getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
				public void run() {
					gInfoManager.update();
				}
			}, 0, 200L);
		}
	}

	private void loadPermissionManager() {
		PluginManager pm = getServer().getPluginManager();
		if (pm.getPlugin("PermissionsEx") != null) {
			gPermissionManager = new PermissionsExPermissionManager();
			getLogger().info("Using PermissionsEx");
		} else if (pm.getPlugin("bPermissions") != null) {
			gPermissionManager = new BPermissionsPermissionManager();
			getLogger().info("Using bPermissions");
		} else if (pm.getPlugin("PermissionsBukkit") != null) {
			gPermissionManager = new PermissionsBukkitPermissionManager();
			getLogger().info("Using PermissionsBukkit");
		}
		else {
			gPermissionManager = new EmptyPermissionManager();
			getLogger().info("No supported permission plugins found (not related to hChat permissions)");
		}
	}

	public ConfigManager getConfigManager() {
		return gConfigManager;
	}

	public InfoManager getInfoManager() {
		return gInfoManager;
	}

	public PermissionManager getPermissionManager() {
		return gPermissionManager;
	}

	public void setEnable(boolean enable) {
		gEnable = enable;
	}

	public void setEnableTimedUpdates(boolean enable) {
		gEnableTimedUpdates = enable;
	}
}
