package no.hon95.bukkit.hchat.format;

import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class RacesAndClassesFormatter extends Formatter {

	public static final String BUKKIT_VAR_PLAYER = "%1$s";
	public static final String BUKKIT_VAR_MSG = "%2$s";

	private final HChatPlugin gPlugin;

	public RacesAndClassesFormatter(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	protected String getValue(MessageType type, char c, CommandSender sender, CommandSender receiver, String message) {
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
