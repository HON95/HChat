package no.hon95.bukkit.hchat.command;

import static no.hon95.bukkit.hchat.HChatPermissions.*;
import static org.bukkit.ChatColor.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import no.hon95.bukkit.hchat.Channel;
import no.hon95.bukkit.hchat.ChatManager;
import no.hon95.bukkit.hchat.Group;
import no.hon95.bukkit.hchat.HChatCommands;
import no.hon95.bukkit.hchat.HChatPlugin;
import no.hon95.bukkit.hchat.common.util.PlayerIdUtils;
import no.hon95.bukkit.hchat.util.ChannelAccessUtils;
import no.hon95.bukkit.hchat.util.ChannelEditUtils;
import no.hon95.bukkit.hchat.util.ChannelEditUtils.Key;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ChannelCommandExecutor extends AbstractCommandExecutor {

	private static final String COMMAND = HChatCommands.CMD_CHANNEL;
	private static final String[] COMMAND_USAGE = translateColorCodes(new String[] { "",
			"            &c>> &9hChat Channel&c <<",
			"&a================================================",
			"&a * &7Chat channels are public or private chat groups.",
			"&a * &7Commands:",
			"&a * &6/channel join &7-> Join a channel you have access to.",
			"&a * &6/channel leave &7-> Leave the channel.",
			"&a * &6/channel list [own|all] &7-> List channels you have access to.",
			"&a * &6/channel info <channel> &7-> Get all info about a channel.",
			"&a * &6/channel who &7-> Get all info about a channel.",
			"&a * &6/channel create &7-> Create a channel and become the owner.",
			"&a * &6/channel delete &7-> Delete a channel if you are the owner.",
			"&a * &6/channel edit &7-> Edit a channel if you are the owner.",
	});

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
					if (channel == null) {
						sender.sendMessage(RED + "Channel not found: " + channelId);
					} else if (channelId.equalsIgnoreCase(getPlugin().getChatManager().getPlayerChannel(player.getUniqueId()))) {
						sender.sendMessage(RED + "You are already in channel " + channelId + RESET + RED + '.');
					} else {
						Group group = getPlugin().getChatManager().getGroup(player.getUniqueId());
						String color = ChannelAccessUtils.getRelativeColor(channel, player, group.getId());
						if (ChannelAccessUtils.hasBasicAccess(channel, player, group.getId())) {
							boolean access = false;
							if (ChannelAccessUtils.hasUnquestionableAccess(channel, player)) {
								access = true;
							} else if (ChannelAccessUtils.isPassworded(channel)) {
								if (gPasswordCooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() - gPasswordCooldowns.get(player.getUniqueId()) < 5000L) {
									sender.sendMessage(RED + "Please wait a few seconds, the cooldown has not ended.");
								} else {
									if (args.length < 3) {
										sender.sendMessage(RED + "The channel is password protected, please specify one.");
										sender.sendMessage(GOLD + "Syntax: " + RESET + "/channel join <name> [password]");
									} else {
										String password = args[2];
										if (channel.getPassword().equalsIgnoreCase(password)) {
											access = true;
										} else {
											sender.sendMessage(RED + "Wrong password. Please wait five seconds before you retry.");
											getPlugin().getLogger().info(sender.getName() + " has entered the wrong password for channel + " + channel.getId());
											gPasswordCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
										}
									}
								}
							} else {
								access = true;
							}
							if (access)
								getPlugin().getChatManager().changePlayerChannel(player, channelId, true);
						} else if (ChannelAccessUtils.isBanned(channel, player)) {
							sender.sendMessage(RED + "You are banned from channel " + color + channel.getName() + RESET + RED + '.');
						} else if (!ChannelAccessUtils.isMember(channel, player) && !ChannelAccessUtils.isGroupMember(channel, group.getId())) {
							sender.sendMessage(RED + "You not a member of the private channel " + color + channel.getName() + RESET + RED + '.');
						} else {
							sender.sendMessage(RED + "You don't have access to channel " + color + channel.getName() + RESET + RED + '.');
						}
					}
				} else {
					sender.sendMessage(GOLD + "Join a chat channel: " + RESET + "/channel join <name> [password]");
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
					sender.sendMessage(RED + "You are not in a channel.");
				else
					getPlugin().getChatManager().changePlayerChannel(player, null, true);
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
				Group group = getPlugin().getChatManager().getGroup(player.getUniqueId());
				if (showAll)
					sender.sendMessage(BLUE + "All channels " + GRAY + "[id (name)]" + BLUE + ':');
				else if (showOnlyOwn)
					sender.sendMessage(BLUE + "Your channels " + GRAY + "[id (name)]" + BLUE + ':');
				else
					sender.sendMessage(BLUE + "Accessable channels " + GRAY + "[id (name)]" + BLUE + ':');
				sender.sendMessage(DARK_GRAY + "================================================");
				sender.sendMessage(ITALIC + "(Use the ID in the parentheses to join channels etc)");
				boolean any = false;
				String format = DARK_GRAY + " * " + RESET + "%s " + RESET + GRAY + "(%s%s" + GRAY + ")";
				for (Channel c : getPlugin().getChatManager().getChannels().values()) {
					if ((ChannelAccessUtils.hasBasicAccess(c, player, group.getId()) && !showOnlyOwn) || showAll || ChannelAccessUtils.isOwner(c, player)) {
						sender.sendMessage(String.format(format, c.getName(), ChannelAccessUtils.getRelativeColor(c, player, group.getId()), c.getId()));
						any = true;
					}
				}
				if (!any)
					sender.sendMessage(DARK_GRAY + " * " + GRAY + "(none)");
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
				sender.sendMessage(GRAY + "You are currently in channel " + RESET + channel.getName() + RESET + GRAY + " (" + channel.getId() + ").");
				sender.sendMessage(GOLD + "Get info about a channel: " + RESET + "/channel info <name>");
			} else {
				String channelId = args[1].toLowerCase();
				final Channel channel = getPlugin().getChatManager().getChannel(channelId).clone();
				if (channel == null) {
					sender.sendMessage(RED + "Channel not found: " + channelId);
				} else {
					getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {

						public void run() {
							sender.sendMessage("");
							sender.sendMessage(BLUE + "########  " + GREEN + "Channel info  " + BLUE + "########");
							sender.sendMessage(BLUE + "# " + GRAY + "This command might take a little while.");
							sender.sendMessage(BLUE + "# ");
							sender.sendMessage(BLUE + "# " + GRAY + "ID: " + RESET + channel.getId());
							sender.sendMessage(BLUE + "# " + GRAY + "Name: " + RESET + channel.getName());
							String ownerName = "";
							if (channel.getOwner() != null && channel.getOwner().length() > 0) {
								try {
									UUID uuid = UUID.fromString(channel.getOwner());
									if (PlayerIdUtils.isUuidSupported()) {
										ownerName = PlayerIdUtils.getPlayerName(uuid, true) + " " + GRAY + "(" + RESET + uuid + GRAY + ")";
									} else {
										ownerName = PlayerIdUtils.getLocalPlayerName(uuid) + " " + GRAY + "(" + RESET + uuid + GRAY + ") " + "(local source)";
									}
								} catch (IllegalArgumentException ex) {
									ownerName = RED + "Malformed UUID: " + channel.getOwner();
								}
							} else {
								ownerName = "(none)";
							}
							sender.sendMessage(BLUE + "# " + GRAY + "Owner: " + RESET + ownerName);
							sender.sendMessage(BLUE + "# " + GRAY + "Privacy: " + (channel.isPrivate() ? GOLD + "Private" : GREEN + "Public"));
							sender.sendMessage(BLUE + "# " + GRAY + "Censored: " + (channel.isCensored() ? GOLD + "Yes" : GREEN + "No"));
							sender.sendMessage(BLUE + "# " + GRAY + "Formatting codes: " + (channel.allowColorCodes() ? GREEN + "Yes" : RED + "No"));
							sender.sendMessage(BLUE + "# " + GRAY + "Universal: " + (channel.isUniversal() ? GREEN + "Yes" : RED + "No"));
							sender.sendMessage(BLUE + "# " + GRAY + "Auto join if default: " + (channel.autoJoinIfDefault() ? RED + "Yes" : GREEN + "No"));
							sender.sendMessage(BLUE + "# " + GRAY + "Range: " + RESET + String.format("%.1f", channel.getRange()));
							sender.sendMessage(BLUE + "# " + GRAY + "Password protected: " + (ChannelAccessUtils.isPassworded(channel) ? RED + "Yes" : GREEN + "No"));
							if (sender instanceof Player) {
								Player player = (Player) sender;
								Group group = getPlugin().getChatManager().getGroup(player.getUniqueId());
								sender.sendMessage(BLUE + "# " + GRAY + "You have access: " + (ChannelAccessUtils.hasBasicAccess(channel, player, group.getId()) ? GREEN + "Yes" : RED + "No"));
							}
							if (channel.getMonitorChannels() != null) {
								sender.sendMessage(BLUE + "# ");
								sender.sendMessage(BLUE + "# " + GRAY + "Monitored channels (" + channel.getMonitorChannels().size() + "):");
								int c = 0;
								for (String cid : channel.getMonitorChannels()) {
									if (c < 10) {
										String text = cid;
										if (cid == null || cid.length() == 0)
											continue;
										if (getPlugin().getChatManager().getChannelExact(cid.toLowerCase()) == null)
											text = text + RESET + RED + " (not found)";
										sender.sendMessage(BLUE + "# " + GRAY + "* " + RESET + text);
										c++;
									} else {
										sender.sendMessage(BLUE + "# " + GRAY + "* ...");
										break;
									}
								}
								if (c == 0)
									sender.sendMessage(BLUE + "# " + GRAY + "* (none)");
							}
							if (channel.getMemberGroups() != null) {
								sender.sendMessage(BLUE + "# ");
								sender.sendMessage(BLUE + "# " + GRAY + "Member groups (" + channel.getMemberGroups().size() + "):");
								int c = 0;
								for (String group : channel.getMemberGroups()) {
									if (c < 10) {
										String text = group;
										if (group == null || group.length() == 0)
											continue;
										if (getPlugin().getChatManager().getGroupExact(group.toLowerCase()) == null)
											text = text + RESET + RED + " (not found)";
										sender.sendMessage(BLUE + "# " + GRAY + "* " + RESET + text);
										c++;
									} else {
										sender.sendMessage(BLUE + "# " + GRAY + "* ...");
										break;
									}
								}
								if (c == 0)
									sender.sendMessage(BLUE + "# " + GRAY + "* (none)");
							}
							if (channel.getMembers() != null) {
								sender.sendMessage(BLUE + "# ");
								sender.sendMessage(BLUE + "# " + GRAY + "Members (" + channel.getMembers().size() + "):");
								printPlayersForInfoCmd(channel.getMembers(), sender);
							}
							if (channel.getBannedMembers() != null) {
								sender.sendMessage(BLUE + "# ");
								sender.sendMessage(BLUE + "# " + GRAY + "Banned members (" + channel.getBannedMembers().size() + "):");
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
				String text = pid;
				if (pid == null || pid.length() == 0)
					continue;
				try {
					UUID uuid = UUID.fromString(pid);
					if (PlayerIdUtils.isUuidSupported()) {
						text = PlayerIdUtils.getPlayerName(uuid, true) + GRAY + " (" + RESET + uuid + GRAY + ")";
					} else {
						text = PlayerIdUtils.getLocalPlayerName(uuid) + GRAY + " (" + RESET + uuid + GRAY + ") (local source)";
					}
				} catch (IllegalArgumentException ex) {
					text = RED + "Malformed UUID: " + pid;
				}
				sender.sendMessage(BLUE + "# " + GRAY + "* " + RESET + text);
				c++;
			} else {
				sender.sendMessage(BLUE + "# " + GRAY + "* ...");
				break;
			}
		}
		if (c == 0)
			sender.sendMessage(BLUE + "# " + GRAY + "* (none)");
	}

	private void cmdWho(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_WHO)) {
			sender.sendMessage("");
			sender.sendMessage(BLUE + "Channel player list:");
			sender.sendMessage(DARK_GRAY + "================================================");
			for (Channel c : getPlugin().getChatManager().getChannels().values()) {
				StringBuilder sb = new StringBuilder();
				sb.append(DARK_GRAY).append(" * ").append(RESET).append(c.getName()).append(RESET).append(GRAY).append(": ").append(RESET);
				Iterator<UUID> iter = getPlugin().getChatManager().getChannelPlayers(c.getId()).iterator();
				boolean any = false;
				while (iter.hasNext()) {
					UUID pid = iter.next();
					if (any) {
						if (iter.hasNext())
							sb.append(RESET).append(DARK_GRAY).append(", ").append(RESET);
						else
							sb.append(RESET).append(DARK_GRAY).append(" and ").append(RESET);
					}
					sb.append(PlayerIdUtils.getLocalPlayer(pid).getDisplayName());
					any = true;
				}
				if (!any)
					sb.append(GRAY).append("(none)");
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
					sender.sendMessage(RED + "Channel already exists: " + channel.getName());
				} else {
					boolean illegalArgs = false;
					String owner = "";
					boolean isPrivate = false;
					if (!StringUtils.isAlphanumeric(channelId)) {
						illegalArgs = true;
						sender.sendMessage(RED + "Channel id must be alphanumeric.");
					}
					if (sender instanceof Player)
						owner = ((Player) sender).getUniqueId().toString();
					if (args[2].equalsIgnoreCase("private"))
						isPrivate = true;
					else if (args[2].equalsIgnoreCase("public"))
						isPrivate = false;
					else {
						illegalArgs = true;
						sender.sendMessage(RED + "Unknown argument: " + args[2]);
					}
					if (illegalArgs) {
						sender.sendMessage(RED + "Failed to create channel because of illegal arguments.");
					} else {
						channel = new Channel(channelId, owner, isPrivate);
						getPlugin().getChatManager().addChannel(channel);
						sender.sendMessage(GREEN + "Successfully created channel " + channel.getName() + GREEN + ".");
					}
				}
			} else {
				sender.sendMessage(GOLD + "Create a channel: " + RESET + "/channel create <name> <public|private>");
			}
		}
	}

	private void cmdDelete(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_DELETE)) {
			if (args.length > 1) {
				String channelId = args[1].toLowerCase();
				Channel channel = getPlugin().getChatManager().getChannelExact(channelId);
				if (channel == null) {
					sender.sendMessage(RED + "Channel doesn't exist: " + channelId);
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
						sender.sendMessage(GREEN + "Successfully deleted channel " + channel.getName() + GREEN + ".");
					} else {
						sender.sendMessage(RED + "Failed to delete channel " + channel.getName() + RED + ".");
						sender.sendMessage(RED + "You are not the owner of this channel.");
					}
				}
			} else {
				sender.sendMessage(GOLD + "Delete a channel: " + RESET + "/channel delete <name>");
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
					sender.sendMessage(RED + "Channel doesn't exist: " + channelId);
					stop = true;
				} else if (sender instanceof Player && !sender.hasPermission(PERM_CHANNEL_MODIFYALL)) {
					if (channel.getOwner() != null && channel.getOwner().length() > 0) {
						if (!((Player) sender).getUniqueId().toString().equalsIgnoreCase(channel.getOwner())) {
							sender.sendMessage(RED + "You don't have permission to edit this channel.");
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
								ChannelEditUtils.edit(getPlugin(), sender, editedChannel, action, key, value);
								Bukkit.getScheduler().runTask(getPlugin(), new Runnable() {
									public void run() {
										getPlugin().getChatManager().updateAndSaveChannel(editedChannel);
										sender.sendMessage(GREEN + "Successfully changed key '" + key + "'.");
									}
								});
							} catch (ChannelEditUtils.EditException ex) {
								sender.sendMessage(RED + ex.getLocalizedMessage());
							}
						}
					});
				}
			} else {
				sender.sendMessage(GOLD + "Edit a channel: " + RESET + "/channel edit <channel> <action> <key> <value>");
				sender.sendMessage("Actions: " + GRAY + "add, remove, set, unset.");
				StringBuilder sb = new StringBuilder("Keys: ").append(GRAY);
				Key[] keys = ChannelEditUtils.Key.values();
				for (int i = 0; i < keys.length; i++) {
					if (i != 0)
						sb.append(", ");
					sb.append(keys[i].toString().toLowerCase());
				}
				sb.append('.');
				sender.sendMessage(sb.toString());
			}
		}
	}
}
