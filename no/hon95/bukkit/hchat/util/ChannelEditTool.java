package no.hon95.bukkit.hchat.util;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_BANNED_MEMBERS;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_CENSORED;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_CHAT_FORMAT;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_COLOR_CODES;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_MEMBERS;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_MONITOR_CHANNELS;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_NAME;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_OWNER;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_PASSWORD;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_PRIVATE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT_UNIVERSAL;

import java.util.UUID;

import no.hon95.bukkit.hchat.Channel;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


public final class ChannelEditTool {

	public static void edit(HChatPlugin plugin, CommandSender sender, Channel channel, String strAction, String strKey, String value) throws EditException {
		if (channel == null || strAction == null || strKey == null)
			throw new EditException("One or more arguments are null.");
		Action action = null;
		for (Action a : Action.values()) {
			if (a.toString().equalsIgnoreCase(strAction)) {
				action = a;
				break;
			}
		}
		if (action == null)
			throw new EditException("Unknown action: " + strAction);
		Key key = null;
		for (Key k : Key.values()) {
			if (k.toString().equalsIgnoreCase(strKey)) {
				key = k;
				break;
			}
		}
		if (key == null)
			throw new EditException("Unknown key: " + strKey);
		if (!sender.hasPermission(key.getEditPermission()))
			throw new EditException("You don't have permission to edit '" + key.toString().toLowerCase() + "'.");
		if (!key.supports(action))
			throw new EditException("Unsupported combination between action and key.");
		if (action != Action.UNSET && (value == null || value.length() == 0))
			throw new EditException("Value is empty.");
		String lowerValue = value.toLowerCase();
		UUID uuid = null;
		if (key == Key.OWNER || key == Key.MEMBERS || key == Key.BANNED_MEMBERS) {
			uuid = plugin.getPlayerUuid(lowerValue, true);
			if (uuid == null)
				throw new EditException("Player '" + lowerValue + "' not found.");
		}

		switch (key) {
		case NAME:
			if (action == Action.SET)
				channel.setName(ChatColor.translateAlternateColorCodes('&', value));
			break;
		case OWNER:
			if (action == Action.SET)
				channel.setOwner(uuid.toString());
			break;
		case PASSWORD:
			if (action == Action.SET)
				channel.setPassword(value);
			else if (action == Action.UNSET)
				channel.setPassword("");
			break;
		case CHAT_FORMAT:
			if (action == Action.SET)
				channel.setChatFormat(value);
			else if (action == Action.UNSET)
				channel.setChatFormat("");
			break;
		case PRIVATE:
			if (action == Action.SET)
				channel.setPrivate(toBoolean(value));
			break;
		case CENSORED:
			if (action == Action.SET)
				channel.setCensored(toBoolean(value));
			break;
		case COLOR_CODES:
			if (action == Action.SET)
				channel.setAllowColorCodes(toBoolean(value));
			break;
		case UNIVERSAL:
			if (action == Action.SET)
				channel.setUniversal(toBoolean(value));
			break;
		case MONITOR_CHANNELS:
			if (action == Action.ADD) {
				if (channel.getMonitorChannels().contains(lowerValue))
					throw new EditException("List already contains value '" + lowerValue + "'.");
				channel.getMonitorChannels().add(lowerValue);
			} else if (action == Action.REMOVE) {
				if (!channel.getMonitorChannels().contains(lowerValue))
					throw new EditException("List doesn't contain value '" + lowerValue + "'.");
				channel.getMonitorChannels().remove(lowerValue);
			}
			break;
		case MEMBERS:
			if (action == Action.ADD) {
				if (channel.getMembers().contains(uuid))
					throw new EditException("List already contains player '" + lowerValue + "'.");
				channel.getMembers().add(uuid.toString());
			} else if (action == Action.REMOVE) {
				if (!channel.getMembers().contains(uuid))
					throw new EditException("List doesn't contain player '" + lowerValue + "'.");
				channel.getMembers().remove(uuid);
			}
			break;
		case BANNED_MEMBERS:
			if (action == Action.ADD) {
				if (channel.getBannedMembers().contains(uuid))
					throw new EditException("List already contains player '" + lowerValue + "'.");
				channel.getBannedMembers().add(uuid.toString());
			} else if (action == Action.REMOVE) {
				if (!channel.getBannedMembers().contains(uuid))
					throw new EditException("List doesn't contain player '" + lowerValue + "'.");
				channel.getBannedMembers().remove(uuid);
			}
			break;
		default:
			break;
		}
	}

	private static boolean toBoolean(String string) throws EditException {
		if (string.equalsIgnoreCase("true"))
			return true;
		else if (string.equalsIgnoreCase("false"))
			return false;
		else
			throw new EditException("Value is not a boolean, it needs to be 'true' or 'false'.");
	}

	private static enum Action {
		ADD, REMOVE, SET, UNSET;
	}

	private static enum Key {
		NAME(PERM_COMMAND_CHANNEL_EDIT_NAME, Action.SET), OWNER(PERM_COMMAND_CHANNEL_EDIT_OWNER, Action.SET),
		PASSWORD(PERM_COMMAND_CHANNEL_EDIT_PASSWORD, Action.SET, Action.UNSET), CHAT_FORMAT(PERM_COMMAND_CHANNEL_EDIT_CHAT_FORMAT, Action.SET, Action.UNSET),
		PRIVATE(PERM_COMMAND_CHANNEL_EDIT_PRIVATE, Action.SET), CENSORED(PERM_COMMAND_CHANNEL_EDIT_CENSORED, Action.SET),
		COLOR_CODES(PERM_COMMAND_CHANNEL_EDIT_COLOR_CODES, Action.SET), UNIVERSAL(PERM_COMMAND_CHANNEL_EDIT_UNIVERSAL, Action.SET),
		MONITOR_CHANNELS(PERM_COMMAND_CHANNEL_EDIT_MONITOR_CHANNELS, Action.ADD, Action.REMOVE), MEMBERS(PERM_COMMAND_CHANNEL_EDIT_MEMBERS, Action.ADD, Action.REMOVE),
		BANNED_MEMBERS(PERM_COMMAND_CHANNEL_EDIT_BANNED_MEMBERS, Action.ADD, Action.REMOVE);

		private final String gPermission;
		private final Action[] gSupportedAction;

		private Key(String permission, Action... supportedActions) {
			gPermission = permission;
			gSupportedAction = supportedActions;
		}

		private String getEditPermission() {
			return gPermission;
		}

		private boolean supports(Action action) {
			for (Action a : gSupportedAction) {
				if (action.equals(a))
					return true;
			}
			return false;
		}
	}

	public static class EditException extends Exception {

		private static final long serialVersionUID = 1L;

		public EditException(String message) {
			super(message);
		}
	}
}
