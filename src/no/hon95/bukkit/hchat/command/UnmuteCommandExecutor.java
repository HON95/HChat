package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.*;
import static org.bukkit.ChatColor.*;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;
import no.hon95.bukkit.hchat.common.util.PlayerIdUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class UnmuteCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_UNMUTE;

	public UnmuteCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_UNMUTE)) {
			if (args.length == 0) {
				sender.sendMessage(GOLD + "Syntax: " + RESET + "/unmute <player>");
			} else {
				boolean global = (args.length > 1 && args[0].equalsIgnoreCase("-g"));
				String playerName = (global ? args[1] : args[0]);
				Player mutedPlayer = PlayerIdUtils.getLocalPlayer(playerName);

				if (mutedPlayer == null) {
					sender.sendMessage(RED + "Player not found: " + playerName);
				} else if (mutedPlayer.hasPermission(PERM_IMMUTABLE)) {
					sender.sendMessage(RED + "Player " + mutedPlayer.getName() + RESET + RED + " is immutable");
				} else {
					if (global) {
						if (sender.hasPermission(PERM_MUTE_GLOBAL)) {
							if (!getPlugin().getChatManager().isPlayerMutedGlobally(mutedPlayer.getUniqueId())) {
								sender.sendMessage(RED + "Player " + mutedPlayer.getName() + RESET + RED + " is not globally muted.");
							} else {
								getPlugin().getChatManager().mutePlayerGlobally(mutedPlayer.getUniqueId(), false);
								sender.sendMessage(GREEN + "Player " + mutedPlayer.getName() + RESET + GREEN + " has become globally unmuted.");
								mutedPlayer.sendMessage(GREEN + "You have become globally unmuted.");
							}
						} else {
							sender.sendMessage(RED + "You may not unmute players globally.");
						}
					} else {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (!getPlugin().getChatManager().isPlayerMutedIndividually(player.getUniqueId(), mutedPlayer.getUniqueId())) {
								sender.sendMessage(RED + "Player " + mutedPlayer.getName() + RESET + RED + " is not muted.");
							} else {
								getPlugin().getChatManager().mutePlayerIndividually(((Player) sender).getUniqueId(), mutedPlayer.getUniqueId(), false);
								sender.sendMessage(GREEN + "You have unmuted " + mutedPlayer.getName() + RESET + GREEN + ".");
							}
						} else {
							sender.sendMessage("Non-players can only unmute players globally.");
						}
					}
				}
			}
		}
	}

}
