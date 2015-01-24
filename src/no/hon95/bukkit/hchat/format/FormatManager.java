package no.hon95.bukkit.hchat.format;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import no.hon95.bukkit.hchat.Channel;
import no.hon95.bukkit.hchat.Group;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.command.CommandSender;

public final class FormatManager {

	private static final char CODE_PREFIX_A = '%';
	private static final char CODE_PREFIX_B = '$';

	private final HashSet<Formatter> gFormatters = new HashSet<Formatter>();
	private HChatPlugin gPlugin;

	public FormatManager(HChatPlugin plugin) {
		gPlugin = plugin;
		gFormatters.add(new DefaultFormatter(plugin));
		gFormatters.add(new RacesAndClassesFormatter(plugin));
	}

	public String formatString(MessageType type, CommandSender sender, CommandSender receiver, String message) {
		return format(type, getFormatString(type, sender), sender, receiver, message);
	}

	public List<String> formatList(MessageType type, CommandSender sender, CommandSender receiver, String message) {
		List<String> formats = getFormatList(type, sender);
		ArrayList<String> results = new ArrayList<String>(formats.size());
		for (String format : formats)
			results.add(format(type, format, sender, receiver, message));
		return results;
	}

	public String format(MessageType type, String format, CommandSender sender, CommandSender receiver, String message) {
		String result = (format != null ? format : "");
		Group group = gPlugin.getChatManager().getGroup(sender);
		Channel channel = gPlugin.getChatManager().getChannel(sender);

		result = formatEarly(type, format, sender, receiver, message, group, channel);

		char[] cf = result.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cf.length; i++) {
			if (cf[i] == CODE_PREFIX_A || cf[i] == CODE_PREFIX_B) {
				i++;
				if (i < cf.length) {
					String val = getCodeValue(type, cf[i], sender, receiver, message, group, channel);
					if (val != null)
						sb.append(val);
				}
			} else {
				sb.append(cf[i]);
			}
		}
		result = sb.toString();

		result = formatLate(type, result, sender, receiver, message, group, channel);
		return result;
	}

	private String formatEarly(MessageType type, String format, CommandSender sender, CommandSender receiver, String message, Group group, Channel channel) {
		String result = format;
		for (Formatter formatter : gFormatters)
			result = formatter.formatEarly(type, result, sender, receiver, message, group, channel);
		return result;
	}

	private String formatLate(MessageType type, String format, CommandSender sender, CommandSender receiver, String message, Group group, Channel channel) {
		String result = format;
		for (Formatter formatter : gFormatters)
			result = formatter.formatLate(type, result, sender, receiver, message, group, channel);
		return result;
	}

	private String getCodeValue(MessageType type, char code, CommandSender sender, CommandSender receiver, String message, Group group, Channel channel) {
		String result = null;
		for (Formatter formatter : gFormatters) {
			result = formatter.getCodeValue(type, code, sender, receiver, message, group, channel);
			if (result != null)
				break;
		}
		return result;
	}

	public final String getFormatString(MessageType type, CommandSender sender) {
		if (type == null || sender == null)
			throw new IllegalArgumentException();
		Group group = gPlugin.getChatManager().getGroup(sender);
		String format = null;
		switch (type) {
		case NAME:
			format = group.getNameFormat();
			break;
		case LIST:
			format = group.getListFormat();
			break;
		case CHAT:
			format = gPlugin.getChatManager().getChannel(sender).getChatFormat();
			if (format == null || format.length() == 0)
				format = group.getChatFormat();
			break;
		case DEATH:
			format = group.getDeathFormat();
			break;
		case JOIN:
			format = group.getJoinFormat();
			break;
		case QUIT:
			format = group.getQuitFormat();
			break;
		case CHANNEL_JOIN:
			format = group.getChannelJoinFormat();
			break;
		case CHANNEL_QUIT:
			format = group.getChannelQuitFormat();
			break;
		case AWAY_START:
			format = group.getAwayStartFormat();
			break;
		case AWAY_STOP:
			format = group.getAwayStopFormat();
			break;
		case AWAY_KICK_PLAYER:
			format = group.getAwayKickPlayerMessageFormat();
			break;
		case AWAY_KICK_SERVER:
			format = group.getAwayKickServerMessageFormat();
			break;
		case ME:
			format = group.getMeFormat();
			break;
		case TELL_SENDER:
			format = group.getTellSenderFormat();
			break;
		case TELL_RECEIVER:
			format = group.getTellReceiverFormat();
			break;
		case TELL_SPY:
			format = group.getTellSpyFormat();
			break;
		default:
			break;
		}
		if (format == null)
			format = "";
		return format;
	}

	public final List<String> getFormatList(MessageType type, CommandSender sender) {
		if (type == null || sender == null)
			throw new IllegalArgumentException();
		Group group = gPlugin.getChatManager().getGroup(sender);
		List<String> format = null;
		switch (type) {
		case MOTD:
			format = group.getMotdFormat();
			break;
		default:
			break;
		}
		if (format == null)
			format = new ArrayList<String>();
		return format;
	}

	public void addFormatter(Formatter formatter) {
		if (formatter == null)
			throw new IllegalArgumentException();
		gFormatters.add(formatter);
	}

	public void removeFormatter(Formatter formatter) {
		if (formatter == null)
			throw new IllegalArgumentException();
		gFormatters.remove(formatter);
	}

	public enum MessageType {
		NAME, LIST, CHAT, DEATH, JOIN, QUIT, CHANNEL_JOIN, CHANNEL_QUIT, AWAY_START, AWAY_STOP, AWAY_KICK_PLAYER, AWAY_KICK_SERVER, ME, TELL_SENDER, TELL_RECEIVER, TELL_SPY, MOTD;
	}
}
