package ninja.hon95.bukkit.hchat.command;

import static ninja.hon95.bukkit.hchat.HChatPermissions.*;
import static org.bukkit.ChatColor.*;

import java.util.UUID;

import ninja.hon95.bukkit.hchat.HChatCommands;
import ninja.hon95.bukkit.hchat.HChatPlugin;
import ninja.hon95.bukkit.hcommonlib.AbstractCommandExecutor;
import ninja.hon95.bukkit.hcommonlib.CompatUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MuteCommandExecutor extends AbstractCommandExecutor<HChatPlugin> {

	private static final String COMMAND = HChatCommands.CMD_MUTE;

	public MuteCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_MUTE)) {
			if (args.length == 0) {
				sender.sendMessage(GOLD + "Syntax: " + RESET + "/mute [-[g][l]] <player>");
				sender.sendMessage(GRAY + "Use '-g' option to mute globally, '-l' to list players you have muted and '-gl' to list globally muted players.");
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
					Player mutedPlayer = CompatUtil.getLocalPlayer(playerName);
					if (mutedPlayer == null) {
						sender.sendMessage(RED + "Player not found: " + playerName);
					} else if (mutedPlayer.hasPermission(PERM_IMMUTABLE)) {
						sender.sendMessage(RED + "Player " + mutedPlayer.getName() + RESET + RED + " can not be muted.");
					} else {
						if (global) {
							if (sender.hasPermission(PERM_MUTE_GLOBAL)) {
								if (getPlugin().getChatManager().isPlayerMutedGlobally(mutedPlayer.getUniqueId())) {
									sender.sendMessage(RED + "Player " + mutedPlayer.getName() + RESET + RED + " is already globally muted.");
								} else {
									getPlugin().getChatManager().mutePlayerGlobally(mutedPlayer.getUniqueId(), true);
									sender.sendMessage(RED + "Player " + mutedPlayer.getName() + RESET + RED + " has become globally muted.");
									mutedPlayer.sendMessage(RED + "You have become globally muted.");
								}
							} else {
								sender.sendMessage(RED + "You may not mute players globally.");
							}
						} else {
							if (sender instanceof Player) {
								Player player = (Player) sender;
								if (getPlugin().getChatManager().isPlayerMutedIndividually(player.getUniqueId(), mutedPlayer.getUniqueId())) {
									sender.sendMessage(RED + "Player " + mutedPlayer.getName() + RESET + RED + " is already muted.");
								} else {
									getPlugin().getChatManager().mutePlayerIndividually(((Player) sender).getUniqueId(), mutedPlayer.getUniqueId(), true);
									sender.sendMessage(GREEN + "You have muted " + mutedPlayer.getName() + RESET + GREEN + ".");
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
										finalSender.sendMessage(BLUE + "Globally muted players:");
										finalSender.sendMessage(DARK_GRAY + "================================================");
										for (UUID pid : getPlugin().getChatManager().getGloballyMutedPlayers()) {
											finalSender.sendMessage(DARK_GRAY + " * " + RESET + CompatUtil.getLocalPlayerName(pid));
										}
									}
								} else {
									if (finalSender instanceof Player) {
										finalSender.sendMessage(BLUE + "Players muted by you:");
										finalSender.sendMessage(DARK_GRAY + "================================================");
										for (UUID pid : getPlugin().getChatManager().getIndividuallyMutedPlayers().get(((Player) finalSender).getUniqueId())) {
											finalSender.sendMessage(DARK_GRAY + " * " + RESET + CompatUtil.getLocalPlayerName(pid));
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
