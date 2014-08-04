package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_HCHAT_LIST;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_HCHAT_RELOAD;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_HCHAT_UPDATE;
import no.hon95.bukkit.hchat.ChatManager;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class HChatCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_HCHAT;
	private static final String[] COMMAND_USAGE = { "",
			"            §c>> §9hChat§c <<",
			"§a================================================",
			"§a * §7Chat formatter by HON95",
			"§a * §7Commands:",
			"§a * §6/hchat reload §r§7-> Reload files and update player info.",
			"§a * §6/hchat list <groups|channels|players> §r§7-> List groups, channels or player info.",
	};

	public HChatCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (hasPerm(sender, PERM_COMMAND_HCHAT_RELOAD)) {
				getPlugin().getConfigManager().load();
				getPlugin().getChatManager().reload();
				sender.sendMessage("§ahChat reloaded!");
			}
		} else if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
			if (hasPerm(sender, PERM_COMMAND_HCHAT_LIST)) {
				if (args.length > 1 && args[1].equalsIgnoreCase("groups")) {
					sender.sendMessage("§9hChat groups:");
					for (String s : getPlugin().getChatManager().getGroups().keySet())
						sender.sendMessage(s);
				} else if (args.length > 1 && args[1].equalsIgnoreCase("channels")) {
					sender.sendMessage("§9hChat channels:");
					for (String s : getPlugin().getChatManager().getChannels().keySet())
						sender.sendMessage(s);
				} else if (args.length > 1 && args[1].equalsIgnoreCase("players")) {
					sender.sendMessage("§9hChat players: §7(player : real group : group : channel)");
					ChatManager cm = getPlugin().getChatManager();
					for (Player p : Bukkit.getOnlinePlayers())
						sender.sendMessage(p.getName() + " §7|§f " + cm.getRealGroup(p) + " §7|§f " + cm.getGroup(p.getUniqueId()).getId() + " §7|§f " + cm.getChannel(p.getUniqueId()).getId());
				} else {
					sender.sendMessage("§6Syntax: §r/hchat list <groups|channels|players>");
				}
			}
		} else if (args.length > 0 && args[0].equalsIgnoreCase("update")) {
			if (hasPerm(sender, PERM_COMMAND_HCHAT_UPDATE)) {
				sender.sendMessage("Updating hCHat to latest version...");
				sender.sendMessage("This command will run a while in the background.");
				final CommandSender finalSender = sender;
				Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
					public void run() {
						getPlugin().getUpdateManager().checkForUpdates(true, finalSender);
					}
				});
			}
		} else {
			sender.sendMessage(COMMAND_USAGE);
		}
	}

}
