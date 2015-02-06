package no.hon95.bukkit.hchat.format;

import no.hon95.bukkit.hchat.Channel;
import no.hon95.bukkit.hchat.Group;
import no.hon95.bukkit.hchat.HChatPlugin;
import no.hon95.bukkit.hchat.format.FormatManager.MessageType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RacesAndClassesFormatter extends Formatter {

	private final HChatPlugin gPlugin;

	public RacesAndClassesFormatter(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public String getCodeValue(MessageType type, char c, CommandSender sender, CommandSender receiver, String message, Group group, Channel channel) {
		Player player = null;
		if (sender instanceof Player)
			player = (Player) sender;
		String result = null;

		switch (c) {
		case 'a':
			if (player != null)
				result = gPlugin.getRacesAndClassesHook().getRace(player.getUniqueId());
			break;
		case 'A':
			if (player != null)
				result = gPlugin.getRacesAndClassesHook().getClass(player.getUniqueId());
			break;
		default:
			return null;
		}
		return result != null ? result : "";
	}
}
