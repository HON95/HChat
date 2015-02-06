package ninja.hon95.bukkit.hchat.command;

import static ninja.hon95.bukkit.hchat.UITheme.*;
import static ninja.hon95.bukkit.hchat.HChatPermissions.*;
import ninja.hon95.bukkit.hchat.ChatManager;
import ninja.hon95.bukkit.hchat.HChatCommands;
import ninja.hon95.bukkit.hchat.HChatPlugin;
import ninja.hon95.bukkit.hcommonlib.AbstractCommandExecutor;
import ninja.hon95.bukkit.hcommonlib.CompatUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HChatCommandExecutor extends AbstractCommandExecutor<HChatPlugin> {

	private static final String COMMAND = HChatCommands.CMD_HCHAT;
	private static final String[] COMMAND_USAGE = translateColorCodes(new String[] { "",
			C_FRAGMENT + "  >>  " + C_HEADER + "hChat  " + C_FRAGMENT + "<<",
			C_SEPARATOR + "================================================",
			C_BULLET + " * " + C_INFO + "Chat formatter by HON95",
			C_BULLET + " * ",
			C_BULLET + " * " + C_FORMAT + "/hchat list <groups|channels|players> " + C_FORMAT_DESC + "-> List groups, channels or player info.",
			C_BULLET + " * " + C_FORMAT + "/hchat reload " + C_FORMAT_DESC + "-> Reload files and update player info.",
			C_BULLET + " * " + C_FORMAT + "/hchat update " + C_FORMAT_DESC + "-> Update the plugin to the latest version.",
	});

	public HChatCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
			if (hasPerm(sender, PERM_COMMAND_HCHAT_LIST)) {
				if (args.length > 1 && args[1].equalsIgnoreCase("groups")) {
					sender.sendMessage(C_HEADER + "hChat groups:");
					for (String s : getPlugin().getChatManager().getGroups().keySet())
						sender.sendMessage(s);
				} else if (args.length > 1 && args[1].equalsIgnoreCase("channels")) {
					sender.sendMessage(C_HEADER + "hChat channels:");
					for (String s : getPlugin().getChatManager().getChannels().keySet())
						sender.sendMessage(s);
				} else if (args.length > 1 && args[1].equalsIgnoreCase("players")) {
					sender.sendMessage(C_HEADER + "hChat players: " + C_BRACKET + "[player : real group : group : channel]");
					ChatManager cm = getPlugin().getChatManager();
					for (Player p : CompatUtil.getOnlinePlayers())
						sender.sendMessage(C_TEXT + p.getName() + C_FRAGMENT + " | " + C_TEXT + cm.getRealGroup(p) + C_FRAGMENT + " | " + C_TEXT + cm.getGroup(p.getUniqueId()).getId()
								+ C_FRAGMENT + " | " + C_TEXT + cm.getChannel(p.getUniqueId()).getId());
				} else {
					sender.sendMessage(C_FORMAT_DESC + "Format: " + C_FORMAT + "/hchat list <groups|channels|players>");
				}
			}
		} else if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (hasPerm(sender, PERM_COMMAND_HCHAT_RELOAD)) {
				getPlugin().getConfigManager().load();
				getPlugin().getChatManager().reload();
				sender.sendMessage(C_SUCCESS + "hChat reloaded!");
			}
		} else if (args.length > 0 && args[0].equalsIgnoreCase("update")) {
			if (hasPerm(sender, PERM_COMMAND_HCHAT_UPDATE)) {
				sender.sendMessage("Updating hCHat to latest version...");
				sender.sendMessage("This command will run a while in the background.");
				final CommandSender finalSender = sender;
				Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
					public void run() {
						getPlugin().getUpdateTool().checkForUpdates(true, finalSender);
					}
				});
			}
		} else {
			sender.sendMessage(COMMAND_USAGE);
		}
	}

}
