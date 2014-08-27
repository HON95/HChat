package no.hon95.bukkit.hchat.format;

import no.hon95.bukkit.hchat.Channel;
import no.hon95.bukkit.hchat.Group;
import no.hon95.bukkit.hchat.format.FormatManager.MessageType;

import org.bukkit.command.CommandSender;


public abstract class Formatter {

	public String formatEarly(MessageType type, String format, CommandSender sender, CommandSender receiver, String message, Group group, Channel channel) {
		return format;
	}

	public String formatLate(MessageType type, String format, CommandSender sender, CommandSender receiver, String message, Group group, Channel channel) {
		return format;
	}

	public String getCodeValue(MessageType type, char c, CommandSender sender, CommandSender receiver, String message, Group group, Channel channel) {
		return null;
	}
}
