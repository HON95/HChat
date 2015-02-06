package ninja.hon95.bukkit.hchat;

import static ninja.hon95.bukkit.hchat.HChatCommands.*;

import java.io.IOException;

import ninja.hon95.bukkit.hchat.command.AwayCommandExecutor;
import ninja.hon95.bukkit.hchat.command.ChannelCommandExecutor;
import ninja.hon95.bukkit.hchat.command.ClearChatCommandExecutor;
import ninja.hon95.bukkit.hchat.command.ColorsCommandExecutor;
import ninja.hon95.bukkit.hchat.command.HChatCommandExecutor;
import ninja.hon95.bukkit.hchat.command.MeCommandExecutor;
import ninja.hon95.bukkit.hchat.command.MuteAllCommandExecutor;
import ninja.hon95.bukkit.hchat.command.MuteCommandExecutor;
import ninja.hon95.bukkit.hchat.command.TellCommandExecutor;
import ninja.hon95.bukkit.hchat.command.UnmuteAllCommandExecutor;
import ninja.hon95.bukkit.hchat.command.UnmuteCommandExecutor;
import ninja.hon95.bukkit.hchat.format.FormatManager;
import ninja.hon95.bukkit.hchat.hook.RacesAndClassesHook;
import ninja.hon95.bukkit.hcommonlib.CompatUtil;
import ninja.hon95.bukkit.hcommonlib.UpdateTool;
import ninja.hon95.bukkit.hcommonlib.hook.VaultEconomyHook;
import ninja.hon95.bukkit.hcommonlib.hook.VaultPermissionHook;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public final class HChatPlugin extends JavaPlugin {

	private static final int SERVER_MODS_API_ID = 77039;
	private static final long TASK_PERIOD_UPDATE_FAST = 1L;
	private static final long TASK_PERIOD_UPDATE_SLOW = 20L;
	private static final long TASK_PERIOD_CHECK_FOR_UPDATES = 3600 * 20L;

	private final ConfigManager gConfigManager = new ConfigManager(this);
	private final PlayerListener gPlayerListener = new PlayerListener(this);
	private final ChatManager gChatManager = new ChatManager(this);
	private final FormatManager gFormatManager = new FormatManager(this);
	private final VaultPermissionHook gVaultPermHook = new VaultPermissionHook(this);
	private final VaultEconomyHook gVaultEconHook = new VaultEconomyHook(this);
	private final RacesAndClassesHook gRACHook = new RacesAndClassesHook(this);
	private final HChatApi gApi = new HChatApi(this);
	private UpdateTool gUpdateTool;

	private boolean gEnable = true;
	private boolean gCheckForUpdates = true;
	private boolean gUpdateIfAvailable = true;
	private boolean gCollectData = false;
	private int gPlayerNameUpdateInterval = 0;

	@Override
	public void onLoad() {
		if (!CompatUtil.isUuidSupported())
			getLogger().warning("The server is offline or doesn't support UUIDs, UUIDs will be buggy.");
		if (!CompatUtil.isPlayerListSupported())
			getLogger().warning("The server doesn't use a collection for the player list, but this shouldn't be a problem.");
		gConfigManager.load();
		gUpdateTool = new UpdateTool(this, getFile(), SERVER_MODS_API_ID, HChatPermissions.PERM_NOTIFY_UPDATE);
	}

	@Override
	public void onEnable() {
		if (gEnable) {
			registerListenersAndCommands();
			loadHooks();
			gChatManager.load();
			setupTasks();
			setupMetrics();
		} else {
			getLogger().warning("Plugin disabled by config.");
			getPluginLoader().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		gChatManager.unload();
	}

	private void registerListenersAndCommands() {
		getServer().getPluginManager().registerEvents(gPlayerListener, this);
		getCommand(CMD_HCHAT).setExecutor(new HChatCommandExecutor(this));
		getCommand(CMD_AWAY).setExecutor(new AwayCommandExecutor(this));
		getCommand(CMD_CHANNEL).setExecutor(new ChannelCommandExecutor(this));
		getCommand(CMD_CLEAR_CHAT).setExecutor(new ClearChatCommandExecutor(this));
		getCommand(CMD_COLORS).setExecutor(new ColorsCommandExecutor(this));
		getCommand(CMD_ME).setExecutor(new MeCommandExecutor(this));
		getCommand(CMD_MUTE).setExecutor(new MuteCommandExecutor(this));
		getCommand(CMD_UNMUTE).setExecutor(new UnmuteCommandExecutor(this));
		getCommand(CMD_MUTEALL).setExecutor(new MuteAllCommandExecutor(this));
		getCommand(CMD_UNMUTEALL).setExecutor(new UnmuteAllCommandExecutor(this));
		getCommand(CMD_TELL).setExecutor(new TellCommandExecutor(this));
	}

	private void loadHooks() {
		gVaultPermHook.hook();
		gVaultEconHook.hook();
		gRACHook.hook();
	}

	private void setupTasks() {
		if (gPlayerNameUpdateInterval > 0) {
			getServer().getScheduler().runTaskTimer(this, new Runnable() {
				public void run() {
					for (Player player : CompatUtil.getOnlinePlayers())
						gChatManager.updateNamesNextTick(player);
				}
			}, 0, gPlayerNameUpdateInterval);
		}
		getServer().getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				gChatManager.fastTimedUpdate();
			}
		}, 0, TASK_PERIOD_UPDATE_FAST);
		getServer().getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				gChatManager.slowTimedUpdate();
			}
		}, 0, TASK_PERIOD_UPDATE_SLOW);
		if (gCheckForUpdates) {
			getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
				public void run() {
					gUpdateTool.checkForUpdates(gUpdateIfAvailable, null);
				}
			}, 0, TASK_PERIOD_CHECK_FOR_UPDATES);
		} else {
			getLogger().info("Update checking has been disabled.");
		}
	}

	public void setupMetrics() {
		if (gCollectData) {
			try {
				new Metrics(this).start();
			} catch (IOException ex) {
				getLogger().warning("Failed to load Metrics.");
				ex.printStackTrace();
			}
		} else {
			getLogger().info("Data collection has been disabled.");
		}
	}

	public int getServerModsApiKey() {
		return SERVER_MODS_API_ID;
	}

	public ConfigManager getConfigManager() {
		return gConfigManager;
	}

	public ChatManager getChatManager() {
		return gChatManager;
	}

	public FormatManager getFormatManager() {
		return gFormatManager;
	}

	public UpdateTool getUpdateTool() {
		return gUpdateTool;
	}

	public HChatApi getApi() {
		return gApi;
	}

	public VaultPermissionHook getVaultPermission() {
		return gVaultPermHook;
	}

	public VaultEconomyHook getVaultEconomy() {
		return gVaultEconHook;
	}

	public RacesAndClassesHook getRacesAndClassesHook() {
		return gRACHook;
	}

	public void setEnable(boolean enable) {
		gEnable = enable;
	}

	public void setCheckForUpdates(boolean checkForUpdates) {
		gCheckForUpdates = checkForUpdates;
	}

	public void setUpdateIfAvailable(boolean updateIfAvailable) {
		gUpdateIfAvailable = updateIfAvailable;
	}

	public void setCollectData(boolean collect) {
		gCollectData = collect;
	}

	public void setPlayerNameUpdateInterval(int interval) {
		gPlayerNameUpdateInterval = interval;
	}
}
