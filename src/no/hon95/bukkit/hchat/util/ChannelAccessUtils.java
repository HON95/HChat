package no.hon95.bukkit.hchat.util;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_CHANNEL_ALL;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_CHANNEL_PREFIX;
import no.hon95.bukkit.hchat.Channel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ChannelAccessUtils {

	private ChannelAccessUtils() {}

	public static final boolean hasUnquestionableAccess(Channel channel, Player player) {
		if (player.hasPermission(PERM_CHANNEL_ALL) || player.hasPermission(PERM_CHANNEL_PREFIX + channel.getId()))
			return true;
		else if (isOwner(channel, player))
			return true;
		return false;
	}

	public static final boolean hasBasicAccess(Channel channel, Player player, String group) {
		return (hasUnquestionableAccess(channel, player) || ((!channel.isPrivate() || isGroupMember(channel, group) || isMember(channel, player)) && !isBanned(channel, player)));
	}

	public static final boolean isOwner(Channel channel, Player player) {
		String strUuid = player.getUniqueId().toString();
		return (channel.getOwner() != null && channel.getOwner().equalsIgnoreCase(strUuid));
	}

	public static final boolean isGroupMember(Channel channel, String group) {
		if (channel.getMemberGroups() != null)
			return channel.getMemberGroups().containsCaseInsensitive(group);
		return false;
	}

	public static final boolean isMember(Channel channel, Player player) {
		String strUuid = player.getUniqueId().toString();
		if (channel.getMembers() != null) {
			return channel.getMembers().containsCaseInsensitive(strUuid);
		}
		return false;
	}

	public static final boolean isBanned(Channel channel, Player player) {
		String strUuid = player.getUniqueId().toString();
		if (channel.getBannedMembers() != null)
			return channel.getBannedMembers().containsCaseInsensitive(strUuid);
		return false;
	}

	public static final boolean isPassworded(Channel channel) {
		return channel.getPassword() != null && channel.getPassword().length() > 0;
	}

	public static final String getRelativeColor(Channel channel, Player player, String group) {
		ChatColor color;
		if (isOwner(channel, player)) {
			color = ChatColor.BLUE;
		} else if (hasBasicAccess(channel, player, group)) {
			if (channel.isPrivate())
				color = ChatColor.GOLD;
			else if (isPassworded(channel))
				color = ChatColor.LIGHT_PURPLE;
			else
				color = ChatColor.GREEN;
		} else {
			color = ChatColor.RED;
		}
		return color.toString();
	}
}
