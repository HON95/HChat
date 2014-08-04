package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.PERM_CHANNEL_MODIFYALL;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_CREATE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_DELETE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_EDIT;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_INFO;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_JOIN;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_LEAVE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_LIST;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL_WHO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import no.hon95.bukkit.hchat.Channel;
import no.hon95.bukkit.hchat.ChatManager;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;
import no.hon95.bukkit.hchat.util.ChannelAccessTool;
import no.hon95.bukkit.hchat.util.ChannelEditTool;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ChannelCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_CHANNEL;
	private static final String[] COMMAND_USAGE = { "",
			"            §c>> §9hChat Channel§c <<",
			"§a================================================",
			"§a * §7Chat channels are public or private chat groups.",
			"§a * §7Commands:",
			"§a * §6/channel join §7-> Join a channel you have access to.",
			"§a * §6/channel leave §7-> Leave the channel.",
			"§a * §6/channel list [own|all] §7-> List channels you have access to.",
			"§a * §6/channel info <channel> §7-> Get all info about a channel.",
			"§a * §6/channel who §7-> Get all info about a channel.",
			"§a * §6/channel create §7-> Create a channel and become the owner.",
			"§a * §6/channel delete §7-> Delete a channel if you are the owner.",
			"§a * §6/channel edit §7-> Edit a channel if you are the owner.",
	};

	private HashMap<UUID, Long> gPasswordCooldowns = new HashMap<UUID, Long>();

	public ChannelCommandExecutor(HChatPlugin plugin) {
		super(plugin, COMMAND);
	}

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (args.length > 0 && args[0].equalsIgnoreCase("join"))
			cmdJoin(sender, args);
		else if (args.length > 0 && args[0].equalsIgnoreCase("leave"))
			cmdLeave(sender, args);
		else if (args.length > 0 && args[0].equalsIgnoreCase("list"))
			cmdList(sender, args);
		else if (args.length > 0 && args[0].equalsIgnoreCase("info"))
			cmdInfo(sender, args);
		else if (args.length > 0 && args[0].equalsIgnoreCase("who"))
			cmdWho(sender, args);
		else if (args.length > 0 && args[0].equalsIgnoreCase("create"))
			cmdCreate(sender, args);
		else if (args.length > 0 && args[0].equalsIgnoreCase("delete"))
			cmdDelete(sender, args);
		else if (args.length > 0 && args[0].equalsIgnoreCase("edit"))
			cmdEdit(sender, args);
		else
			sender.sendMessage(COMMAND_USAGE);
	}

	private void cmdJoin(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_JOIN)) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players may use this command.");
			} else {
				Player player = (Player) sender;
				if (args.length > 1) {
					String channelId = args[1].toLowerCase();
					Channel channel = getPlugin().getChatManager().getChannelExact(channelId);
					if (channel == null || channelId.equalsIgnoreCase(ChatManager.DEFAULT_CHANNEL_NAME)) {
						sender.sendMessage("§cChannel not found: " + channelId);
					} else if (channelId.equalsIgnoreCase(getPlugin().getChatManager().getPlayerChannel(player.getUniqueId()))) {
						sender.sendMessage("§cYou are already in channel " + channelId + "§r§c.");
					} else {
						String color = ChannelAccessTool.getRelativeColor(channel, player);
						if (ChannelAccessTool.hasBasicAccess(channel, player)) {
							boolean access = false;
							if (ChannelAccessTool.hasUnquestionableAccess(channel, player)) {
								access = true;
							} else if (ChannelAccessTool.isPassworded(channel)) {
								if (gPasswordCooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() - gPasswordCooldowns.get(player.getUniqueId()) < 5000L) {
									sender.sendMessage("§cPlease wait a few seconds, the cooldown has not ended.");
								} else {
									if (args.length < 3) {
										sender.sendMessage("§cThe channel is password protected, please specify one.");
										sender.sendMessage("§6Syntax: §r/channel join <name> [password]");
									} else {
										String password = args[2];
										if (channel.getPassword().equalsIgnoreCase(password)) {
											access = true;
										} else {
											sender.sendMessage("§cWrong password. Please wait five seconds before you retry.");
											getPlugin().getLogger().info(sender.getName() + " has entered the wrong password for channel + " + channel.getId());
											gPasswordCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
										}
									}
								}
							}
							if (access)
								getPlugin().getChatManager().changePlayerChannel(player.getUniqueId(), channelId, true);
						} else if (ChannelAccessTool.isBanned(channel, player)) {
							sender.sendMessage("§cYou are banned from channel " + color + channel.getName() + "§c.");
						} else if (!ChannelAccessTool.isMember(channel, player)) {
							sender.sendMessage("§cYou not a member of the private channel " + color + channel.getName() + "§c.");
						} else {
							sender.sendMessage("§cYou don't have access to channel " + color + channel.getName() + "§c.");
						}
					}
				} else {
					sender.sendMessage("§6Join a chat channel: §r/channel join <name> [password]");
				}
			}
		}
	}

	private void cmdLeave(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_LEAVE)) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players may use this command.");
			} else {
				Player player = (Player) sender;
				if (getPlugin().getChatManager().getPlayerChannel(player.getUniqueId()).equalsIgnoreCase(ChatManager.DEFAULT_CHANNEL_NAME))
					sender.sendMessage("§cYou are not in a channel.");
				else
					getPlugin().getChatManager().changePlayerChannel(player.getUniqueId(), null, true);
			}
		}
	}

	private void cmdList(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_LIST)) {
			boolean showAll = false;
			boolean showOnlyOwn = false;
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("all"))
					showAll = true;
				if (args[1].equalsIgnoreCase("own"))
					showOnlyOwn = true;
			}
			sender.sendMessage("");
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (showAll)
					sender.sendMessage("§9All channels §7[id (name)]§9:");
				else if (showOnlyOwn)
					sender.sendMessage("§9Your channels §7[id (name)]§9:");
				else
					sender.sendMessage("§9Accessable channels §7[id (name)]§9:");
				sender.sendMessage("§8================================================");
				sender.sendMessage("§o(Use the ID in the parentheses to join channels etc)");
				boolean any = false;
				for (Channel c : getPlugin().getChatManager().getChannels().values()) {
					if ((ChannelAccessTool.hasBasicAccess(c, player) && !showOnlyOwn && !c.getId().equalsIgnoreCase(ChatManager.DEFAULT_CHANNEL_NAME)) || showAll || ChannelAccessTool.isOwner(c, player)) {
						sender.sendMessage(String.format("§8 * §f%s §r§7(%s%s§7)", c.getName(), ChannelAccessTool.getRelativeColor(c, player), c.getId()));
						any = true;
					}
				}
				if (!any)
					sender.sendMessage("§8 * §7(none)");
			} else {
				sender.sendMessage("All channels (id (name)):");
				for (Channel c : getPlugin().getChatManager().getChannels().values())
					sender.sendMessage(String.format("%s (%s)", c.getId(), c.getName()));
			}
		}
	}

	private void cmdInfo(final CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_INFO)) {
			if (args.length == 1) {
				Channel channel = getPlugin().getChatManager().getChannel(sender);
				sender.sendMessage("§fYou are currently in channel " + channel.getName() + " §r§7(" + channel.getId() + ")§r.");
				sender.sendMessage("§6Get info about a channel: §r/channel info <name>");
			} else {
				String channelId = args[1].toLowerCase();
				final Channel channel = getPlugin().getChatManager().getChannel(channelId).clone();
				if (channel == null) {
					sender.sendMessage("§cChannel not found: " + channelId);
				} else {
					getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {

						public void run() {
							sender.sendMessage("");
							sender.sendMessage("§9########  §aChannel info  §9########");
							sender.sendMessage("§9# §7This command might take a little while.");
							sender.sendMessage("§9# ");
							sender.sendMessage("§9# §7ID: §f" + channel.getId());
							sender.sendMessage("§9# §7Name: §f" + channel.getName());
							String ownerName = "";
							if (channel.getOwner() != null && channel.getOwner().length() > 0) {
								try {
									UUID uuid = UUID.fromString(channel.getOwner());
									ownerName = getPlugin().getPlayerName(uuid, true) + " §7(§f" + uuid + "§f)";
								} catch (IllegalArgumentException ex) {
									ownerName = "§cMalformed UUID: " + channel.getOwner();
								}
							} else {
								ownerName = "(none)";
							}
							sender.sendMessage("§9# §7Owner: §f" + ownerName);
							sender.sendMessage("§9# §7Privacy: " + (channel.isPrivate() ? "§6Private" : "§aPublic"));
							sender.sendMessage("§9# §7Censored: " + (channel.isCensored() ? "§6Yes" : "§aNo"));
							sender.sendMessage("§9# §7Formatting codes: " + (channel.allowColorCodes() ? "§aYes" : "§cNo"));
							sender.sendMessage("§9# §7Password protected: " + (ChannelAccessTool.isPassworded(channel) ? "§cYes" : "§aNo"));
							if (sender instanceof Player)
								sender.sendMessage("§9# §7You have access: " + (ChannelAccessTool.hasBasicAccess(channel, (Player) sender) ? "§aYes" : "§cNo"));
							if (channel.getMonitorChannels() != null) {
								sender.sendMessage("§9# ");
								sender.sendMessage("§9# §7Monitored channels (" + channel.getMonitorChannels().size() + "):");
								int c = 0;
								for (String cid : channel.getMonitorChannels()) {
									if (c < 10) {
										String title = cid;
										if (cid.length() < 0)
											title = "§cEmpty name!";
										if (getPlugin().getChatManager().getChannelExact(cid.toLowerCase()) == null)
											title = title + " §r§c(not found)";
										sender.sendMessage("§9# §7* §f" + title);
									} else {
										sender.sendMessage("§9# §7* ...");
										break;
									}
									c++;
								}
								if (c == 0)
									sender.sendMessage("§9# §7* (none)");
							}
							if (channel.getMembers() != null) {
								sender.sendMessage("§9# ");
								sender.sendMessage("§9# §7Members (" + channel.getMembers().size() + "):");
								printPlayersForInfoCmd(channel.getMembers(), sender);
							}
							if (channel.getBannedMembers() != null) {
								sender.sendMessage("§9# ");
								sender.sendMessage("§9# §7Banned members (" + channel.getBannedMembers().size() + "):");
								printPlayersForInfoCmd(channel.getBannedMembers(), sender);
							}
						}
					});
				}
			}
		}
	}

	private void printPlayersForInfoCmd(List<String> pids, CommandSender sender) {
		int c = 0;
		for (String pid : pids) {
			if (c < 10) {
				String name = "§cEmpty name!";
				if (pid.length() > 0) {
					try {
						name = getPlugin().getPlayerName(UUID.fromString(pid), true);
					} catch (IllegalArgumentException ex) {
						name = "§cMalformed UUID: " + pid;
					}
				}
				sender.sendMessage("§9# §7* §f" + name);
			} else {
				sender.sendMessage("§9# §7* ...");
				break;
			}
			c++;
		}
		if (c == 0)
			sender.sendMessage("§9# §7* (none)");
	}

	private void cmdWho(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_WHO)) {
			sender.sendMessage("");
			sender.sendMessage("§9Channel player list:");
			sender.sendMessage("§8================================================");
			for (Channel c : getPlugin().getChatManager().getChannels().values()) {
				StringBuilder sb = new StringBuilder();
				sb.append("§8 * §r").append(c.getName()).append("§r§7: §r");
				Iterator<UUID> iter = getPlugin().getChatManager().getChannelPlayers(c.getId()).iterator();
				boolean any = false;
				while (iter.hasNext()) {
					UUID pid = iter.next();
					if (any) {
						if (iter.hasNext())
							sb.append("§r§8, §r");
						else
							sb.append("§r§8 and §r");
					}
					sb.append(Bukkit.getPlayer(pid).getDisplayName());
					any = true;
				}
				if (!any)
					sb.append("§7(none)");
				sender.sendMessage(sb.toString());
			}
		}
	}

	private void cmdCreate(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_CREATE)) {
			if (args.length > 2) {
				String channelId = args[1].toLowerCase();
				Channel channel = getPlugin().getChatManager().getChannelExact(channelId);
				if (channel != null) {
					sender.sendMessage("§cChannel already exists: " + channel.getName());
				} else {
					boolean illegalArgs = false;
					String owner = "";
					boolean isPrivate = false;
					if (!StringUtils.isAlphanumeric(channelId)) {
						illegalArgs = true;
						sender.sendMessage("§cChannel id must be alphanumeric.");
					}
					if (sender instanceof Player)
						owner = ((Player) sender).getUniqueId().toString();
					if (args[2].equalsIgnoreCase("private"))
						isPrivate = true;
					else if (args[2].equalsIgnoreCase("public"))
						isPrivate = false;
					else {
						illegalArgs = true;
						sender.sendMessage("§cUnknown argument: " + args[2]);
					}
					if (illegalArgs) {
						sender.sendMessage("§cFailed to create channel because of illegal arguments.");
					} else {
						channel = new Channel(channelId, owner, isPrivate);
						getPlugin().getChatManager().addChannel(channel);
						sender.sendMessage("§aSuccessfully created channel " + channel.getName() + "§a.");
					}
				}
			} else {
				sender.sendMessage("§6Create a channel: §r/channel create <name> <public|private>");
			}
		}
	}

	private void cmdDelete(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_DELETE)) {
			if (args.length > 1) {
				String channelId = args[1].toLowerCase();
				Channel channel = getPlugin().getChatManager().getChannelExact(channelId);
				if (channel == null) {
					sender.sendMessage("§cChannel doesn't exist: " + channelId);
				} else {
					boolean canDelete = false;
					if ((sender instanceof Player)) {
						if (channel.getOwner() != null && channel.getOwner().equalsIgnoreCase(((Player) sender).getUniqueId().toString()) && channel.getId().equalsIgnoreCase(ChatManager.DEFAULT_CHANNEL_NAME))
							canDelete = true;
					} else {
						canDelete = true;
					}
					if (canDelete) {
						getPlugin().getChatManager().removeChannel(channelId);
						sender.sendMessage("§aSuccessfully deleted channel " + channel.getName() + "§a.");
					} else {
						sender.sendMessage("§cFailed to delete channel " + channel.getName() + "§c.");
						sender.sendMessage("§cYou are not the owner of this channel.");
					}
				}
			} else {
				sender.sendMessage("§6Delete a channel: §r/channel delete <name>");
			}
		}
	}

	private void cmdEdit(final CommandSender sender, final String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_EDIT)) {
			if (args.length > 3) {
				final String channelId = args[1].toLowerCase();
				final Channel channel = getPlugin().getChatManager().getChannelExact(channelId);
				boolean stop = false;
				if (channel == null) {
					sender.sendMessage("§cChannel doesn't exist: " + channelId);
					stop = true;
				} else if (sender instanceof Player && !sender.hasPermission(PERM_CHANNEL_MODIFYALL)) {
					if (channel.getOwner() != null && channel.getOwner().length() > 0) {
						if (!((Player) sender).getUniqueId().toString().equalsIgnoreCase(channel.getOwner())) {
							sender.sendMessage("§cYou don't have permission to edit this channel.");
							stop = true;
						}
					}
				}
				if (!stop) {
					final String action = args[2];
					final String key = args[3];
					final String value = StringUtils.join(args, ' ', 4, args.length);
					Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
						public void run() {
							try {
								final Channel editedChannel = channel.clone();
								ChannelEditTool.edit(getPlugin(), sender, editedChannel, action, key, value);
								Bukkit.getScheduler().runTask(getPlugin(), new Runnable() {
									public void run() {
										getPlugin().getChatManager().updateAndSaveChannel(editedChannel);
										sender.sendMessage("§aSuccessfully changed key '" + key + "'.");
									}
								});
							} catch (ChannelEditTool.EditException ex) {
								sender.sendMessage("§c" + ex.getLocalizedMessage());
							}
						}
					});
				}
			} else {
				sender.sendMessage("§6Edit a channel: §r/channel edit <§7channel§f> <§7action§f> <§7key§f> <§7value§f>");
				sender.sendMessage("Actions: '§7add§f' and '§7remove§f' for lists, '§7set§f' and '§7unset§f' for text and booleans.");
				sender.sendMessage("Keys: §7name, owner, password, chat_format, private, censored, color_codes, universal, monitor_channels, members, banned_members.");
			}
		}
	}
}
