package no.hon95.bukkit.hchat.format;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import no.hon95.bukkit.hchat.HChatPlugin;
import no.hon95.bukkit.hchat.Group;
import no.hon95.bukkit.hchat.format.Formatter.MessageType;

import org.bukkit.command.CommandSender;


public final class FormatManager {

	private final HashSet<Formatter> gFormatters = new HashSet<Formatter>();
	private DefaultFormatter gDefaultFormatter;
	private HChatPlugin gPlugin;

	public FormatManager(HChatPlugin plugin) {
		gPlugin = plugin;
		gDefaultFormatter = new DefaultFormatter(plugin);
		gFormatters.add(new RacesAndClassesFormatter(plugin));
	}

	public String formatString(MessageType type, CommandSender sender, CommandSender receiver, String message) {
		String result = getFormatString(type, sender);
		for (Formatter formatter : gFormatters)
			result = formatter.format(type, result, sender, receiver, message);
		result = gDefaultFormatter.format(type, result, sender, receiver, message);
		return result;
	}

	public List<String> formatList(MessageType type, CommandSender sender, CommandSender receiver, String message) {
		List<String> result = getFormatList(type, sender);
		for (Formatter formatter : gFormatters)
			result = formatter.format(type, result, sender, receiver, message);
		result = gDefaultFormatter.format(type, result, sender, receiver, message);
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
}
