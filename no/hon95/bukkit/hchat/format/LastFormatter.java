package no.hon95.bukkit.hchat.format;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


public final class LastFormatter extends Formatter {

	private static final String SENDER_NAME_VAR = "%1$s";
	private static final String SENDER_MESSAGE_VAR = "%2$s";

	@Override
	public String format(MessageType type, String format, CommandSender sender, CommandSender receiver, String message) {
		String result = super.format(type, format, sender, receiver, message);
		return ChatColor.translateAlternateColorCodes('&', result);
	}

	protected String getValue(MessageType type, char c, CommandSender sender, CommandSender receiver, String message) {

		String result = null;
		switch (c) {
		case 'n':
			if (type == MessageType.CHAT)
				result = SENDER_NAME_VAR;
			break;
		case 'm':
			if (type == MessageType.CHAT)
				result = SENDER_MESSAGE_VAR;
			break;
		default:
			break;
		}
		return result != null ? result : "";
	}
}
