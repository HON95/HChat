package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_UNMUTE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_IMMUTABLE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_MUTE_GLOBAL;

import java.util.UUID;

import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.Bukkit;
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
				sender.sendMessage("§6Syntax: §r/unmute <player>");
			} else {
				boolean global = (args.length > 1 && args[0].equalsIgnoreCase("-g"));
				String playerName = (global ? args[1] : args[0]);
				UUID pid = getPlugin().getPlayerUuid(playerName, false);
				Player mutedPlayer = (pid != null ? Bukkit.getPlayer(pid) : null);

				if (mutedPlayer == null) {
					sender.sendMessage("§cPlayer not found: " + playerName);
				} else if (mutedPlayer.hasPermission(PERM_IMMUTABLE)) {
					sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§c is immutable");
				} else {
					if (global) {
						if (sender.hasPermission(PERM_MUTE_GLOBAL)) {
							if (!getPlugin().getChatManager().isPlayerMutedGlobally(mutedPlayer.getUniqueId())) {
								sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§a is not globally muted.");
							} else {
								getPlugin().getChatManager().mutePlayerGlobally(mutedPlayer.getUniqueId(), false);
								sender.sendMessage("§aPlayer " + mutedPlayer.getName() + "§r§a has become globally unmuted.");
								mutedPlayer.sendMessage("§aYou have become globally unmuted.");
							}
						} else {
							sender.sendMessage("§cYou may not unmute players globally.");
						}
					} else {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (!getPlugin().getChatManager().isPlayerMutedIndividually(player.getUniqueId(), mutedPlayer.getUniqueId())) {
								sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§a is not muted.");
							} else {
								getPlugin().getChatManager().mutePlayerIndividually(((Player) sender).getUniqueId(), mutedPlayer.getUniqueId(), false);
								sender.sendMessage("§aYou have unmuted " + mutedPlayer.getName() + "§r§a.");
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
