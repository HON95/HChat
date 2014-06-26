package no.hon95.bukkit.hchat.format;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;


public abstract class Formatter {

	public final List<String> format(MessageType type, List<String> formatList, CommandSender sender, CommandSender receiver, String message) {
		ArrayList<String> results = new ArrayList<String>(formatList.size());
		for (String format : formatList) {
			results.add(format(type, format, sender, receiver, message));
		}
		return results;
	}

	public String format(MessageType type, String format, CommandSender sender, CommandSender receiver, String message) {
		if (format == null)
			return "";
		char[] cf = format.toCharArray();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < cf.length; i++) {
			if (cf[i] == '%' && i < cf.length - 1) {
				String val = getValue(type, cf[i + 1], sender, receiver, message);
				if (val != null) {
					sb.append(val);
					i++;
				} else {
					sb.append(cf[i]);
				}
				continue;
			} else {
				sb.append(cf[i]);
			}
		}
		return sb.toString();
	}

	protected String getValue(MessageType type, char c, CommandSender sender, CommandSender receiver, String message) {
		return null;
	}

	public enum MessageType {
		NAME, LIST, CHAT, DEATH, JOIN, QUIT, CHANNEL_JOIN, CHANNEL_QUIT, ME, TELL_SENDER, TELL_RECEIVER, TELL_SPY, MOTD;
	}
}
