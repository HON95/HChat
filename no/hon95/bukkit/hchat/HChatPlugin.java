package no.hon95.bukkit.hchat;

import static no.hon95.bukkit.hchat.HChatCommands.CMD_CHANNEL;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_CLEAR_CHAT;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_COLORS;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_HCHAT;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_ME;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_MUTE;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_MUTEALL;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_TELL;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_UNMUTE;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_UNMUTEALL;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import no.hon95.bukkit.hchat.format.FormatManager;
import no.hon95.bukkit.hchat.hook.RacesAndClassesHook;
import no.hon95.bukkit.hchat.hook.VaultHook;
import no.hon95.bukkit.hchat.util.evilmidget38.NameFetcher;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public final class HChatPlugin extends JavaPlugin {

	private static final int SERVER_MODS_API_ID = 77039;
	private static final long TASK_PERIOD_UPDATE = 20L;
	private static final long TASK_PERIOD_UPDATE_PENDING_NAMES = 1L;
	private static final long TASK_PERIOD_CHECK_FOR_UPDATES = 3 * 3600 * 20L;

	private final ConfigManager gConfigManager = new ConfigManager(this);
	private final PlayerListener gPlayerListener = new PlayerListener(this);
	private final HCommandExecutor gCommandExecutor = new HCommandExecutor(this);
	private final ChatManager gChatManager = new ChatManager(this);
	private final FormatManager gFormatManager = new FormatManager(this);
	private final MetricsManager gMetricsManager = new MetricsManager(this);
	private final VaultHook gVaultHook = new VaultHook(this);
	private final RacesAndClassesHook gRACHook = new RacesAndClassesHook(this);
	private final UpdateManager gUpdateManager = new UpdateManager(this, SERVER_MODS_API_ID);
	private final HChatAPI gApi = new HChatAPI(this);

	private boolean gEnable = true;
	private boolean gCheckForUpdates = true;
	private boolean gUpdateIfAvailable = true;

	@Override
	public void onLoad() {
		gUpdateManager.setFolder(getFile());
		gConfigManager.load();
		gMetricsManager.load();
	}

	@Override
	public void onEnable() {
		if (gEnable) {
			registerListenersAndCommands();
			loadUuids();
			loadHooks();
			gChatManager.load();
			gMetricsManager.start();
			setupTasks();
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
		getCommand(CMD_HCHAT).setExecutor(gCommandExecutor);
		getCommand(CMD_CHANNEL).setExecutor(gCommandExecutor);
		getCommand(CMD_CLEAR_CHAT).setExecutor(gCommandExecutor);
		getCommand(CMD_COLORS).setExecutor(gCommandExecutor);
		getCommand(CMD_ME).setExecutor(gCommandExecutor);
		getCommand(CMD_MUTE).setExecutor(gCommandExecutor);
		getCommand(CMD_UNMUTE).setExecutor(gCommandExecutor);
		getCommand(CMD_MUTEALL).setExecutor(gCommandExecutor);
		getCommand(CMD_UNMUTEALL).setExecutor(gCommandExecutor);
		getCommand(CMD_TELL).setExecutor(gCommandExecutor);
	}

	private void loadHooks() {
		gVaultHook.hook();
		gRACHook.hook();
	}

	private void loadUuids() {
		ArrayList<UUID> uuids = new ArrayList<UUID>();
		for (Player p : getServer().getOnlinePlayers())
			uuids.add(p.getUniqueId());
	}

	private void setupTasks() {
		getServer().getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				gChatManager.doPendingNameUpdates();
			}
		}, 0, TASK_PERIOD_UPDATE_PENDING_NAMES);
		getServer().getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				gChatManager.updateGroups();
			}
		}, 0, TASK_PERIOD_UPDATE);

		if (gCheckForUpdates) {
			getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
				public void run() {
					gUpdateManager.checkForUpdates(gUpdateIfAvailable, null);
				}
			}, 0, TASK_PERIOD_CHECK_FOR_UPDATES);
		} else {
			getLogger().info("Update checking has been disabled.");
		}
	}

	public UUID getPlayerUuid(String name) {
		for (Player p : getServer().getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(name))
				return p.getUniqueId();
		}
		return null;
	}

	public String getPlayerName(UUID uuid, boolean downloadIfNecessary) {
		String name = null;
		Player player = getServer().getPlayer(uuid);
		if (player != null)
			name = player.getName();
		if (name == null && downloadIfNecessary) {
			ArrayList<UUID> list = new ArrayList<UUID>();
			list.add(uuid);
			try {
				Map<UUID, String> names = new NameFetcher(list).call();
				for (Entry<UUID, String> e : names.entrySet()) {
					if (e.getKey().equals(uuid)) {
						name = e.getValue();
						break;
					}
				}
			} catch (Exception ex) {
				getLogger().warning("Failed to download a name because of: " + ex.getLocalizedMessage());
			}
		}
		return name;
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

	public MetricsManager getMetricsManager() {
		return gMetricsManager;
	}

	public UpdateManager getUpdateManager() {
		return gUpdateManager;
	}

	public HChatAPI getApi() {
		return gApi;
	}

	public VaultHook getVault() {
		return gVaultHook;
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
}
