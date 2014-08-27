package no.hon95.bukkit.hchat;

import java.util.Map;
import java.util.UUID;

import no.hon95.bukkit.hchat.common.util.PlayerIdUtils;


public final class HChatApi {

	private static final int VERSION = 1;

	private static HChatApi gInstance = null;

	private final HChatPlugin gPlugin;

	HChatApi(HChatPlugin plugin) {
		gPlugin = plugin;
		if (gInstance == null)
			gInstance = this;
	}

	public HChatPlugin getPlugin() {
		return gPlugin;
	}

	public String getVersion() {
		return gPlugin.getDescription().getVersion();
	}

	public int getApiVersion() {
		return VERSION;
	}

	public boolean isEnabled() {
		return gPlugin.isEnabled();
	}

	public String getPlayerPrefix(String name) {
		UUID id = PlayerIdUtils.getLocalPlayerUuid(name);
		if (id != null)
			return getPlayerPrefix(id);
		return null;
	}

	public String getPlayerSuffix(String name) {
		UUID id = PlayerIdUtils.getLocalPlayerUuid(name);
		if (id != null)
			return getPlayerSuffix(id);
		return null;
	}

	public String getPlayerPrefix(UUID id) {
		return gPlugin.getChatManager().getGroup(id).getPrefix();
	}

	public String getPlayerSuffix(UUID id) {
		return gPlugin.getChatManager().getGroup(id).getSuffix();
	}

	public String getGroupPrefix(String group) {
		Group hgroup = gPlugin.getChatManager().getGroupExact(group);
		if (hgroup != null)
			return hgroup.getPrefix();
		return null;
	}

	public String getGroupSuffix(String group) {
		Group hgroup = gPlugin.getChatManager().getGroupExact(group);
		if (hgroup != null)
			return hgroup.getSuffix();
		return null;
	}

	public String getPlayerMetaData(String name, String key) {
		UUID id = PlayerIdUtils.getLocalPlayerUuid(name);
		if (id != null)
			return getPlayerMetaData(id, key);
		return null;
	}

	public String getPlayerMetaData(UUID id, String key) {
		return null;
	}

	public String getGroupMetaData(String id, String key) {
		return null;
	}

	public void setPlayerPrefix(String name, String prefix) {
		UUID id = PlayerIdUtils.getLocalPlayerUuid(name);
		if (id != null)
			setPlayerPrefix(id, prefix);
	}

	public void setPlayerSuffix(String name, String suffix) {
		UUID id = PlayerIdUtils.getLocalPlayerUuid(name);
		if (id != null)
			setPlayerSuffix(id, suffix);
	}

	public void setPlayerPrefix(UUID id, String prefix) {
		Group group = gPlugin.getChatManager().getGroup(id);
		group.setSuffix(prefix);
		gPlugin.getChatManager().updateAndSaveGroup(group);
	}

	public void setPlayerSuffix(UUID id, String suffix) {
		Group group = gPlugin.getChatManager().getGroup(id);
		group.setSuffix(suffix);
		gPlugin.getChatManager().updateAndSaveGroup(group);
	}

	public boolean setGroupPrefix(String group, String prefix) {
		Group hgroup = gPlugin.getChatManager().getGroupExact(group);
		if (hgroup != null) {
			hgroup.setPrefix(prefix);
			gPlugin.getChatManager().updateAndSaveGroup(group);
			return true;
		}
		return false;
	}

	public boolean setGroupSuffix(String group, String suffix) {
		Group hgroup = gPlugin.getChatManager().getGroupExact(group);
		if (hgroup != null) {
			hgroup.setSuffix(suffix);
			gPlugin.getChatManager().updateAndSaveGroup(group);
			return true;
		}
		return false;
	}

	public boolean setPlayerMetaData(String name, String key, String value) {
		UUID id = PlayerIdUtils.getLocalPlayerUuid(name);
		if (id != null)
			return setPlayerMetaData(id, key, value);
		return false;
	}

	public boolean setPlayerMetaData(UUID id, String key, String value) {
		return false;
	}

	public boolean setGroupMetaData(String id, String key, String value) {
		return false;
	}

	public Group getGroup(String group) {
		return gPlugin.getChatManager().getGroupExact(group);
	}

	public Channel getChannel(String channel) {
		return gPlugin.getChatManager().getChannelExact(channel);
	}

	public Group getPlayerGroup(UUID playerId) {
		return gPlugin.getChatManager().getGroup(playerId);
	}

	public Channel getPlayerChannel(UUID playerId) {
		return gPlugin.getChatManager().getChannel(playerId);
	}

	public String getPlayerGroupId(UUID playerId) {
		return gPlugin.getChatManager().getPlayerGroup(playerId);
	}

	public String getPlayerChannelId(UUID playerId) {
		return gPlugin.getChatManager().getPlayerChannel(playerId);
	}

	public Map<String, Group> getGroups() {
		return gPlugin.getChatManager().getGroups();
	}

	public Map<String, Channel> getChannels() {
		return gPlugin.getChatManager().getChannels();
	}

	public static HChatApi getInstance() {
		return gInstance;
	}
}
