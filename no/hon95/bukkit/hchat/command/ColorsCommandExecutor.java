package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_COLORS;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.command.CommandSender;


public final class ColorsCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_COLORS;
	private static final String[] COLOR_MESSAGE = { "",
			"§6Color codes:",
			"§00 §r§11 §r§22 §r§33 §r§44 §r§55 §r§66 §r§77 §r§88 §r§99 §r§aa §r§bb §r§cc §r§dd §r§ee §r§ff",
			"",
			"§6Formatting codes:",
			"k: §kabc§r    l: §labc§r    m: §mabc§r    n: §nabc§r    o: §oabc§r    r: §rabc",
			"To use the code in chat or other places, write '&<code>', where <code> is the code.",
			"" };

	public ColorsCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_COLORS))
			sender.sendMessage(COLOR_MESSAGE);
	}

}
