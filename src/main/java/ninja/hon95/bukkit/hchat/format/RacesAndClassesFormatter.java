package ninja.hon95.bukkit.hchat.format;

import ninja.hon95.bukkit.hchat.Channel;
import ninja.hon95.bukkit.hchat.Group;
import ninja.hon95.bukkit.hchat.HChatPlugin;
import ninja.hon95.bukkit.hchat.format.FormatManager.MessageType;

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
		case '0':
			if (player != null)
				result = gPlugin.getRacesAndClassesHook().getRace(player.getUniqueId());
			break;
		case '1':
			if (player != null)
				result = gPlugin.getRacesAndClassesHook().getClass(player.getUniqueId());
			break;
		default:
			return null;
		}
		return result != null ? result : "";
	}
}
