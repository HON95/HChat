package no.hon95.bukkit.hchat;

import java.util.Map;

import org.bukkit.OfflinePlayer;


public final class HChatAPI {
	
	/* WARNING: DO NOT USE THIS API ON VERSIONS BELOW 1.5 */

	public static final String LAST_VERSION_UPDATED = "1.5";
	
	private final HChatPlugin gPlugin;

	HChatAPI(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	public boolean isEnabled() {
		return gPlugin.isEnabled();
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

	public boolean setGroupPrefix(String group, String prefix) {
		Group hgroup = gPlugin.getChatManager().getGroupExact(group);
		if (hgroup != null) {
			hgroup.setPrefix(prefix);
			return true;
		}
		return false;
	}

	public boolean setGroupSuffix(String group, String suffix) {
		Group hgroup = gPlugin.getChatManager().getGroupExact(group);
		if (hgroup != null) {
			hgroup.setSuffix(suffix);
			return true;
		}
		return false;
	}

	public Group getGroup(String group) {
		return gPlugin.getChatManager().getGroupExact(group);
	}

	public Channel getChannel(String channel) {
		return gPlugin.getChatManager().getChannelExact(channel);
	}

	public Group getPlayerGroup(OfflinePlayer player) {
		return gPlugin.getChatManager().getGroup(player.getUniqueId());
	}

	public Channel getPlayerChannel(OfflinePlayer player) {
		return gPlugin.getChatManager().getChannel(player.getUniqueId());
	}

	public String getPlayerGroupId(OfflinePlayer player) {
		return gPlugin.getChatManager().getPlayerGroup(player.getUniqueId());
	}

	public String getPlayerChannelId(OfflinePlayer player) {
		return gPlugin.getChatManager().getPlayerChannel(player.getUniqueId());
	}

	public Map<String, Group> getGroups() {
		return gPlugin.getChatManager().getGroups();
	}

	public Map<String, Channel> getChannels() {
		return gPlugin.getChatManager().getChannels();
	}
}
