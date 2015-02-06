package ninja.hon95.bukkit.hchat.command;

import static ninja.hon95.bukkit.hchat.HChatPermissions.*;
import ninja.hon95.bukkit.hchat.HChatCommands;
import ninja.hon95.bukkit.hchat.HChatPlugin;
import ninja.hon95.bukkit.hcommonlib.AbstractCommandExecutor;

import org.bukkit.command.CommandSender;

public final class ColorsCommandExecutor extends AbstractCommandExecutor<HChatPlugin> {

	private static final String COMMAND = HChatCommands.CMD_COLORS;
	private static final String[] COLOR_MESSAGE = translateColorCodes(new String[] { "",
			"&6Color codes:",
			"&00 &11 &22 &33 &44 &55 &66 &77 &88 &99 &aa &bb &cc &dd &ee &ff",
			"",
			"&6Formatting codes:",
			"k: &kabc&r    l: &labc&r    m: &mabc&r    n: &nabc&r    o: &oabc&r    r: &rabc",
			"To use the code in chat or other places, write '&<code>', where <code> is the code.",
			"" });

	public ColorsCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_COLORS))
			sender.sendMessage(COLOR_MESSAGE);
	}

}
