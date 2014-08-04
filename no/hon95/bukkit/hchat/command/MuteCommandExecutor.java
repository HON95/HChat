package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_MUTE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_IMMUTABLE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_MUTE_GLOBAL;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_MUTE_LIST;

import java.util.UUID;

import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class MuteCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_MUTE;

	public MuteCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_MUTE)) {
			if (args.length == 0) {
				sender.sendMessage("§6Syntax: §r/mute [-[g][l]] <player>");
				sender.sendMessage("§7Use '-g' option to mute globally, '-l' to list players you have muted and '-gl' to list globally muted players.");
			} else {

				boolean global = false;
				boolean list = false;
				if (args[0].startsWith("-")) {
					if (args[0].contains("g"))
						global = true;
					if (args[0].contains("l"))
						list = true;
				}

				if (!list) {
					String playerName = (global ? args[1] : args[0]);
					UUID pid = getPlugin().getPlayerUuid(playerName, false);
					Player mutedPlayer = (pid != null ? Bukkit.getPlayer(pid) : null);
					if (mutedPlayer == null) {
						sender.sendMessage("§cPlayer not found: " + playerName);
					} else if (mutedPlayer.hasPermission(PERM_IMMUTABLE)) {
						sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§c can not be muted.");
					} else {
						if (global) {
							if (sender.hasPermission(PERM_MUTE_GLOBAL)) {
								if (getPlugin().getChatManager().isPlayerMutedGlobally(mutedPlayer.getUniqueId())) {
									sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§a is already globally muted.");
								} else {
									getPlugin().getChatManager().mutePlayerGlobally(mutedPlayer.getUniqueId(), true);
									sender.sendMessage("§aPlayer " + mutedPlayer.getName() + "§r§a has become globally muted.");
									mutedPlayer.sendMessage("§cYou have become globally muted.");
								}
							} else {
								sender.sendMessage("§cYou may not mute players globally.");
							}
						} else {
							if (sender instanceof Player) {
								Player player = (Player) sender;
								if (getPlugin().getChatManager().isPlayerMutedIndividually(player.getUniqueId(), mutedPlayer.getUniqueId())) {
									sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§a is already muted.");
								} else {
									getPlugin().getChatManager().mutePlayerIndividually(((Player) sender).getUniqueId(), mutedPlayer.getUniqueId(), true);
									sender.sendMessage("§aYou have muted " + mutedPlayer.getName() + "§r§a.");
								}
							} else {
								sender.sendMessage("Non-players can only mute players globally.");
							}
						}
					}
				} else {
					if (hasPerm(sender, PERM_MUTE_LIST)) {
						final boolean finalGlobal = global;
						final CommandSender finalSender = sender;
						getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {

							public void run() {
								if (finalGlobal) {
									if (hasPerm(finalSender, PERM_MUTE_GLOBAL)) {
										finalSender.sendMessage("§9Globally muted players:");
										finalSender.sendMessage("§8================================================");
										for (UUID pid : getPlugin().getChatManager().getGloballyMutedPlayers()) {
											finalSender.sendMessage("§8 * §f" + getPlugin().getPlayerName(pid, true));
										}
									}
								} else {
									if (finalSender instanceof Player) {
										finalSender.sendMessage("§9Players muted by you:");
										finalSender.sendMessage("§8================================================");
										for (UUID pid : getPlugin().getChatManager().getIndividuallyMutedPlayers().get(((Player) finalSender).getUniqueId())) {
											finalSender.sendMessage("§8 * §f" + getPlugin().getPlayerName(pid, true));
										}
									} else {
										finalSender.sendMessage("Only players may mute other players.");
									}
								}
							}
						});
					}
				}
			}
		}
	}

}
