package no.hon95.bukkit.hchat.util;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_CHANNEL_ALL;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_CHANNEL_PREFIX;
import no.hon95.bukkit.hchat.Channel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public final class ChannelAccessTool {

	private ChannelAccessTool() {}

	public static final boolean hasUnquestionableAccess(Channel channel, Player player) {
		if (player.hasPermission(PERM_CHANNEL_ALL) || player.hasPermission(PERM_CHANNEL_PREFIX + channel.getId()))
			return true;
		else if (isOwner(channel, player))
			return true;
		return false;
	}

	public static final boolean hasBasicAccess(Channel channel, Player player) {
		return (hasUnquestionableAccess(channel, player) || ((!channel.isPrivate() || isMember(channel, player)) && !isBanned(channel, player)));
	}

	public static final boolean isOwner(Channel channel, Player player) {
		String strUuid = player.getUniqueId().toString();
		return (channel.getOwner() != null && channel.getOwner().equalsIgnoreCase(strUuid));
	}

	public static final boolean isMember(Channel channel, Player player) {
		String strUuid = player.getUniqueId().toString();
		return (channel.getMembers() != null && channel.getMembers().contains(strUuid));
	}

	public static final boolean isBanned(Channel channel, Player player) {
		String strUuid = player.getUniqueId().toString();
		return (channel.getBannedMembers() != null && channel.getBannedMembers().contains(strUuid));
	}

	public static final boolean isPassworded(Channel channel) {
		return channel.getPassword() != null && channel.getPassword().length() > 0;
	}

	public static final String getRelativeColor(Channel channel, Player player) {
		ChatColor color;
		if (isOwner(channel, player)) {
			color = ChatColor.BLUE;
		} else if (hasUnquestionableAccess(channel, player)) {
			color = ChatColor.GREEN;
		} else if (hasBasicAccess(channel, player)) {
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
