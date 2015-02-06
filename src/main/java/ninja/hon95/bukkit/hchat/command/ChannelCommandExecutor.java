package ninja.hon95.bukkit.hchat.command;

import static ninja.hon95.bukkit.hchat.UITheme.*;
import static ninja.hon95.bukkit.hchat.HChatPermissions.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import ninja.hon95.bukkit.hchat.Channel;
import ninja.hon95.bukkit.hchat.ChatManager;
import ninja.hon95.bukkit.hchat.Group;
import ninja.hon95.bukkit.hchat.HChatCommands;
import ninja.hon95.bukkit.hchat.HChatPlugin;
import ninja.hon95.bukkit.hchat.util.ChannelAccessUtils;
import ninja.hon95.bukkit.hchat.util.ChannelEditUtils;
import ninja.hon95.bukkit.hchat.util.ChannelEditUtils.Key;
import ninja.hon95.bukkit.hcommonlib.AbstractCommandExecutor;
import ninja.hon95.bukkit.hcommonlib.CompatUtil;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChannelCommandExecutor extends AbstractCommandExecutor<HChatPlugin> {

	public static final String COMMAND = HChatCommands.CMD_CHANNEL;
	private static final String[] COMMAND_USAGE = new String[] { "",
			C_FRAGMENT + "  >>  " + C_HEADER + "hChat Channel  " + C_FRAGMENT + "<<",
			C_SEPARATOR + "================================================",
			C_BULLET + " * " + C_FORMAT + "/channel join " + C_FORMAT_DESC + "-> Join a channel you have access to.",
			C_BULLET + " * " + C_FORMAT + "/channel leave " + C_FORMAT_DESC + "-> Leave the channel.",
			C_BULLET + " * " + C_FORMAT + "/channel list [own|all] " + C_FORMAT_DESC + "-> List channels you have access to.",
			C_BULLET + " * " + C_FORMAT + "/channel info <channel> " + C_FORMAT_DESC + "-> Get all info about a channel.",
			C_BULLET + " * " + C_FORMAT + "/channel who " + C_FORMAT_DESC + "-> Get all info about a channel.",
			C_BULLET + " * " + C_FORMAT + "/channel create " + C_FORMAT_DESC + "-> Create a channel and become the owner.",
			C_BULLET + " * " + C_FORMAT + "/channel delete " + C_FORMAT_DESC + "-> Delete a channel if you are the owner.",
			C_BULLET + " * " + C_FORMAT + "/channel edit " + C_FORMAT_DESC + "-> Edit a channel if you are the owner.",
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
					if (channel == null) {
						sender.sendMessage(C_ERROR + "Channel not found: " + channelId);
					} else if (channelId.equalsIgnoreCase(getPlugin().getChatManager().getPlayerChannel(player.getUniqueId()))) {
						sender.sendMessage(C_ERROR + "You are already in channel " + channelId + C_ERROR + '.');
					} else {
						Group group = getPlugin().getChatManager().getGroup(player.getUniqueId());
						String color = ChannelAccessUtils.getRelativeColor(channel, player, group.getId());
						if (ChannelAccessUtils.hasBasicAccess(channel, player, group.getId())) {
							boolean access = false;
							if (ChannelAccessUtils.hasUnquestionableAccess(channel, player)) {
								access = true;
							} else if (ChannelAccessUtils.isPassworded(channel)) {
								if (gPasswordCooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() - gPasswordCooldowns.get(player.getUniqueId()) < 5000L) {
									sender.sendMessage(C_ERROR + "Please wait a few seconds, the cooldown has not ended.");
								} else {
									if (args.length < 3) {
										sender.sendMessage(C_ERROR + "The channel is password protected, please specify one.");
										sender.sendMessage(C_FORMAT_DESC + "Syntax: " + C_FORMAT + "/channel join <name> [password]");
									} else {
										String password = args[2];
										if (channel.getPassword().equalsIgnoreCase(password)) {
											access = true;
										} else {
											sender.sendMessage(C_ERROR + "Wrong password. Please wait five seconds before you retry.");
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
							sender.sendMessage(C_ERROR + "You are banned from channel " + color + channel.getName() + C_RESET + C_ERROR + '.');
						} else if (!ChannelAccessUtils.isMember(channel, player) && !ChannelAccessUtils.isGroupMember(channel, group.getId())) {
							sender.sendMessage(C_ERROR + "You not a member of the private channel " + color + channel.getName() + C_RESET + C_ERROR + '.');
						} else {
							sender.sendMessage(C_ERROR + "You don't have access to channel " + color + channel.getName() + C_RESET + C_ERROR + '.');
						}
					}
				} else {
					sender.sendMessage(C_FORMAT_DESC + "Join a chat channel: " + C_FORMAT + "/channel join <name> [password]");
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
					sender.sendMessage(C_ERROR + "You are not in a channel.");
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
					sender.sendMessage(C_FRAGMENT + "  >>  " + C_HEADER + "All Channels  " + C_FRAGMENT + "<< ");
				else if (showOnlyOwn)
					sender.sendMessage(C_FRAGMENT + "  >>  " + C_HEADER + "Your Channels  " + C_FRAGMENT + "<< ");
				else
					sender.sendMessage(C_FRAGMENT + "  >>  " + C_HEADER + "Accessable Channels  " + C_FRAGMENT + "<< ");
				sender.sendMessage(C_SEPARATOR + "================================================");
				sender.sendMessage(C_INFO + "Format: " + C_BRACKET + "[name (id)]");
				sender.sendMessage(C_INFO + "Use the ID, not the name, to join channels etc.");
				boolean any = false;
				String listFormat = C_BULLET + " * " + C_NAME_DEFAULT + "%s " + C_RESET + C_BRACKET + "(%s%s" + C_BRACKET + ")";
				for (Channel c : getPlugin().getChatManager().getChannels().values()) {
					boolean canSee = !c.isHidden() || (showAll && player.hasPermission(PERM_CHANNEL_SEE_ALL));
					boolean hasAccess = ChannelAccessUtils.hasBasicAccess(c, player, group.getId());
					boolean isOwner = ChannelAccessUtils.isOwner(c, player);
					if (canSee && (showAll || (hasAccess && !showOnlyOwn) || isOwner)) {
						sender.sendMessage(String.format(listFormat, c.getName(), ChannelAccessUtils.getRelativeColor(c, player, group.getId()), c.getId()));
						any = true;
					}
				}
				if (!any)
					sender.sendMessage(C_BULLET + " * " + C_NONE + "(none)");
			} else {
				sender.sendMessage("All channels [id (name)]:");
				for (Channel c : getPlugin().getChatManager().getChannels().values())
					sender.sendMessage(String.format("%s (%s%s)", c.getId(), c.getName(), C_RESET));
			}
		}
	}

	private void cmdInfo(final CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_INFO)) {
			if (args.length == 1) {
				Channel channel = getPlugin().getChatManager().getChannel(sender);
				sender.sendMessage(C_INFO + "You are currently in channel " + C_NAME_DEFAULT + channel.getName() + C_RESET + C_BRACKET + " (" + channel.getId() + ").");
				sender.sendMessage(C_FORMAT_DESC + "Get info about a channel: " + C_FORMAT + "/channel info <name>");
			} else {
				String channelId = args[1].toLowerCase();
				final Channel channel = getPlugin().getChatManager().getChannel(channelId).clone();
				if (channel == null) {
					sender.sendMessage(C_ERROR + "Channel '" + channelId + "' not found.");
				} else {
					boolean access = true;
					if (sender instanceof Player) {
						Player player = (Player) sender;
						Group group = getPlugin().getChatManager().getGroup(player.getUniqueId());
						access = ChannelAccessUtils.hasBasicAccess(channel, player, group.getId());
					}
					if (access) {
						getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {

							public void run() {
								sender.sendMessage("");
								sender.sendMessage(C_FRAGMENT + "  >>  " + C_HEADER + "Channel Info  " + C_FRAGMENT + "<<");
								sender.sendMessage(C_SEPARATOR + "================================================");
								sender.sendMessage(C_BULLET + "* " + C_INFO + "This command might take a little while.");
								sender.sendMessage(C_BULLET + "* ");
								sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "ID: " + C_TEXT + channel.getId());
								sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Name: " + C_TEXT + channel.getName());
								String ownerName = "";
								if (channel.getOwner() != null && channel.getOwner().length() > 0) {
									try {
										UUID uuid = UUID.fromString(channel.getOwner());
										if (CompatUtil.isUuidSupported()) {
											ownerName = CompatUtil.getPlayerName(uuid, true) + " " + C_BRACKET + "(" + C_TEXT + uuid + C_BRACKET + ")";
										} else {
											ownerName = CompatUtil.getLocalPlayerName(uuid) + " " + C_BRACKET + "(" + C_TEXT + uuid + C_BRACKET + ") " + "(local source)";
										}
									} catch (IllegalArgumentException ex) {
										ownerName = C_ERROR + "Malformed UUID: " + channel.getOwner();
									}
								} else {
									ownerName = "(none)";
								}
								sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Owner: " + C_TEXT + ownerName);
								sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Privacy: " + (channel.isPrivate() ? C_BAD + "Private" : C_GOOD + "Public"));
								sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Censored: " + (channel.isCensored() ? C_YES + "Yes" : C_NO + "No"));
								sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Formatting codes: " + (channel.allowColorCodes() ? C_YES + "Yes" : C_NO + "No"));
								sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Universal: " + (channel.isUniversal() ? C_YES + "Yes" : C_NO + "No"));
								sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Auto join if default: " + (channel.autoJoinIfDefault() ? C_YES + "Yes" : C_NO + "No"));
								sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Range: " + C_TEXT + String.format("%.1f", channel.getRange()));
								sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Password protected: " + (ChannelAccessUtils.isPassworded(channel) ? C_YES + "Yes" : C_NO + "No"));
								if (sender instanceof Player) {
									Player player = (Player) sender;
									Group group = getPlugin().getChatManager().getGroup(player.getUniqueId());
									sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "You have access: "
											+ (ChannelAccessUtils.hasBasicAccess(channel, player, group.getId()) ? C_YES + "Yes" : C_NO + "No"));
								}
								if (channel.getMonitorChannels() != null) {
									sender.sendMessage(C_BULLET + "* ");
									sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Monitored channels (" + C_TEXT + channel.getMonitorChannels().size() + C_TEXT_WEAK + "):");
									int c = 0;
									for (String cid : channel.getMonitorChannels()) {
										if (c < 10) {
											String text = cid;
											if (cid == null || cid.length() == 0)
												continue;
											if (getPlugin().getChatManager().getChannelExact(cid.toLowerCase()) == null)
												text += C_ERROR + " (not found)";
											sender.sendMessage(C_BULLET + "* > " + C_TEXT + text);
											c++;
										} else {
											sender.sendMessage(C_BULLET + "* > ...");
											break;
										}
									}
									if (c == 0)
										sender.sendMessage(C_BULLET + "* > " + C_NONE + "(none)");
								}
								if (channel.getMemberGroups() != null) {
									sender.sendMessage(C_BULLET + "* ");
									sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Member groups (" + C_TEXT + channel.getMemberGroups().size() + C_TEXT_WEAK + "):");
									int c = 0;
									for (String group : channel.getMemberGroups()) {
										if (c < 10) {
											String text = group;
											if (group == null || group.length() == 0)
												continue;
											sender.sendMessage(C_BULLET + "* > " + C_TEXT + text);
											c++;
										} else {
											sender.sendMessage(C_BULLET + "* > ...");
											break;
										}
									}
									if (c == 0)
										sender.sendMessage(C_BULLET + "* > (none)");
								}
								if (channel.getMembers() != null) {
									sender.sendMessage(C_BULLET + "* ");
									sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Members (" + C_TEXT + channel.getMembers().size() + C_TEXT_WEAK + "):");
									printPlayersForInfoCmd(channel.getMembers(), sender);
								}
								if (channel.getBannedMembers() != null) {
									sender.sendMessage(C_BULLET + "* ");
									sender.sendMessage(C_BULLET + "* " + C_TEXT_WEAK + "Banned members (" + C_TEXT + channel.getBannedMembers().size() + C_TEXT_WEAK + "):");
									printPlayersForInfoCmd(channel.getBannedMembers(), sender);
								}
							}
						});
					} else {
						sender.sendMessage(C_ERROR + "You do not have access to channel '" + channelId + "'.");
					}
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
					if (CompatUtil.isUuidSupported()) {
						text = CompatUtil.getPlayerName(uuid, true) + C_BRACKET + " (" + C_TEXT + uuid + C_BRACKET + ")";
					} else {
						text = CompatUtil.getLocalPlayerName(uuid) + C_BRACKET + " (" + C_TEXT + uuid + C_BRACKET + ") (local)";
					}
				} catch (IllegalArgumentException ex) {
					text = C_ERROR + "Malformed UUID: " + pid;
				}
				sender.sendMessage(C_BULLET + "* > " + C_TEXT + text);
				c++;
			} else {
				sender.sendMessage(C_BULLET + "* > ...");
				break;
			}
		}
		if (c == 0)
			sender.sendMessage(C_BULLET + "* > (none)");
	}

	private void cmdWho(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_WHO)) {
			sender.sendMessage("");
			sender.sendMessage(C_FRAGMENT + "  >>  " + C_HEADER + "Channel Player List  " + C_FRAGMENT + "<<");
			sender.sendMessage(C_SEPARATOR + "================================================");
			for (Channel c : getPlugin().getChatManager().getChannels().values()) {
				if (!c.isHidden() || !(sender instanceof Player) || sender.hasPermission(PERM_CHANNEL_SEE_ALL)) {
					StringBuilder sb = new StringBuilder();
					sb.append(C_BULLET).append(" * ").append(C_TEXT).append(c.getName()).append(C_RESET).append(C_TEXT_WEAK).append(": ").append(C_RESET);
					Iterator<UUID> iter = getPlugin().getChatManager().getChannelPlayers(c.getId()).iterator();
					boolean any = false;
					while (iter.hasNext()) {
						UUID pid = iter.next();
						if (any) {
							if (iter.hasNext())
								sb.append(C_RESET).append(C_BRACKET).append(", ").append(C_RESET);
							else
								sb.append(C_RESET).append(C_BRACKET).append(" and ").append(C_RESET);
						}
						sb.append(CompatUtil.getLocalPlayer(pid).getDisplayName());
						any = true;
					}
					if (!any)
						sb.append(C_NONE).append("(none)");
					sender.sendMessage(sb.toString());
				}
			}
		}
	}

	private void cmdCreate(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_CREATE)) {
			if (args.length > 2) {
				String channelId = args[1].toLowerCase();
				if (channelId.contains(" ")) {
					sender.sendMessage(C_ERROR + "Channel name can not contain spaces.");
				} else {
					Channel channel = getPlugin().getChatManager().getChannelExact(channelId);
					if (channel != null) {
						sender.sendMessage(C_ERROR + "Channel already exists: " + channel.getName());
					} else {
						boolean illegalArgs = false;
						String owner = "";
						boolean isPrivate = false;
						if (!StringUtils.isAlphanumeric(channelId)) {
							illegalArgs = true;
							sender.sendMessage(C_ERROR + "Channel id must be alphanumeric (letters, numbers, no spaces).");
						}
						if (sender instanceof Player)
							owner = ((Player) sender).getUniqueId().toString();
						if (args[2].equalsIgnoreCase("private"))
							isPrivate = true;
						else if (args[2].equalsIgnoreCase("public"))
							isPrivate = false;
						else {
							illegalArgs = true;
							sender.sendMessage(C_ERROR + "Unknown argument: " + args[2]);
						}
						if (illegalArgs) {
							sender.sendMessage(C_ERROR + "Failed to create channel because of illegal arguments.");
						} else {
							channel = new Channel(channelId, owner, isPrivate);
							getPlugin().getChatManager().addChannel(channel);
							sender.sendMessage(C_SUCCESS + "Successfully created channel " + channel.getName() + C_SUCCESS + ".");
						}
					}
				}
			} else {
				sender.sendMessage(C_FORMAT_DESC + "Create a channel: " + C_FORMAT + "/channel create <name> <public|private>");
			}
		}
	}

	private void cmdDelete(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL_DELETE)) {
			if (args.length > 1) {
				String channelId = args[1].toLowerCase();
				Channel channel = getPlugin().getChatManager().getChannelExact(channelId);
				if (channel == null) {
					sender.sendMessage(C_ERROR + "Channel doesn't exist: " + channelId);
				} else {
					boolean canDelete = false;
					if ((sender instanceof Player)) {
						if (channel.getOwner() != null && channel.getOwner().equalsIgnoreCase(((Player) sender).getUniqueId().toString())
								&& channel.getId().equalsIgnoreCase(ChatManager.DEFAULT_CHANNEL_NAME))
							canDelete = true;
					} else {
						canDelete = true;
					}
					if (canDelete) {
						getPlugin().getChatManager().removeChannel(channelId);
						sender.sendMessage(C_SUCCESS + "Successfully deleted channel " + channel.getName() + C_SUCCESS + ".");
					} else {
						sender.sendMessage(C_ERROR + "Failed to delete channel " + channel.getName() + C_ERROR + ".");
						sender.sendMessage(C_ERROR + "You are not the owner of this channel.");
					}
				}
			} else {
				sender.sendMessage(C_FORMAT_DESC + "Delete a channel: " + C_FORMAT + "/channel delete <name>");
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
					sender.sendMessage(C_ERROR + "Channel doesn't exist: " + channelId);
					stop = true;
				} else if (sender instanceof Player && !sender.hasPermission(PERM_CHANNEL_MODIFY_ALL)) {
					if (channel.getOwner() != null && channel.getOwner().length() > 0) {
						if (!((Player) sender).getUniqueId().toString().equalsIgnoreCase(channel.getOwner())) {
							sender.sendMessage(C_ERROR + "You don't have permission to edit this channel.");
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
										sender.sendMessage(C_SUCCESS + "Successfully changed key '" + key + "'.");
									}
								});
							} catch (ChannelEditUtils.EditException ex) {
								sender.sendMessage(C_ERROR + ex.getLocalizedMessage());
							}
						}
					});
				}
			} else {
				sender.sendMessage(C_FORMAT_DESC + "Edit a channel: " + C_FORMAT + "/channel edit <channel> <action> <key> <value>");
				sender.sendMessage(C_TEXT_WEAK + "Actions: " + C_TEXT + "add, remove, set, unset.");
				StringBuilder sb = new StringBuilder("Keys: ").append(C_TEXT);
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
