package no.hon95.bukkit.hchat;

import static no.hon95.bukkit.hchat.HChatCommands.CMD_CHANNEL;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_CLEAR_CHAT;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_COLORS;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_HCHAT;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_ME;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_MUTE;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_TELL;
import static no.hon95.bukkit.hchat.HChatCommands.CMD_UNMUTE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_CHANNEL_ALL;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_CHANNEL_PREFIX;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CHANNEL;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_CLEAR;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_COLORS;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_LIST;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_ME;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_MUTE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_RELOAD;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_COMMAND_TELL;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_IMMUTABLE;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_MUTE_GLOBAL;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_SPY;
import static no.hon95.bukkit.hchat.HChatPermissions.PERM_UNCLEARABLE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import no.hon95.bukkit.hchat.format.Formatter.MessageType;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class HCommandExecutor implements CommandExecutor {

	private static final String[] COMMAND_USAGE = { "",
			"            §c>> §9hChat§c <<",
			"§a================================================",
			"§a * §7Chat formatter by HON95",
			"§a * §7Commands:",
			"§a * §6/hchat reload §r§7-> Reload files and update player info.",
			"§a * §6/hchat list <groups|channels|players> §r§7-> List groups, channels or player info.",
	};
	private static final String[] COMMAND_CHANNEL_USAGE = { "",
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
			"§a * §6/channel delete §7-> Delete a channelif you are the owner.",
			"§a * §6/channel edit §7-> Edit a channelif you are the owner.",
	};
	private static final String[] COLOR_MESSAGE = { "",
			"§6Color codes:",
			"§00 §r§11 §r§22 §r§33 §r§44 §r§55 §r§66 §r§77 §r§88 §r§99 §r§aa §r§bb §r§cc §r§dd §r§ee §r§ff",
			"",
			"§6Formatting codes:",
			"k: §kabc§r    l: §labc§r    m: §mabc§r    n: §nabc§r    o: §oabc§r    r: §rabc",
			"To use the code in chat or other places, write '&<code>', where <code> is the code.",
			"" };
	private static final int CLEAR_LENGTH = 100;

	private final HChatPlugin gPlugin;
	private HashMap<UUID, Long> gPasswordCooldowns = new HashMap<UUID, Long>();

	public HCommandExecutor(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase(CMD_HCHAT))
			cmdHChat(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_CHANNEL))
			cmdChannel(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_CLEAR_CHAT))
			cmdClearChat(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_COLORS))
			cmdColors(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_ME))
			cmdMe(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_MUTE))
			cmdMute(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_UNMUTE))
			cmdUnmute(sender, args);
		else if (cmd.getName().equalsIgnoreCase(CMD_TELL))
			cmdTell(sender, args);
		else
			return false;
		return true;
	}

	private void cmdHChat(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND)) {
			if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
				if (hasPerm(sender, PERM_COMMAND_RELOAD)) {
					gPlugin.getConfigManager().load();
					gPlugin.getChatManager().reload();
					sender.sendMessage("§ahChat reloaded!");
				}
			} else if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
				if (hasPerm(sender, PERM_COMMAND_LIST)) {
					if (args.length > 1 && args[1].equalsIgnoreCase("groups")) {
						sender.sendMessage("§9hChat groups:");
						for (String s : gPlugin.getChatManager().getGroups().keySet())
							sender.sendMessage(s);
					} else if (args.length > 1 && args[1].equalsIgnoreCase("channels")) {
						sender.sendMessage("§9hChat channels:");
						for (String s : gPlugin.getChatManager().getChannels().keySet())
							sender.sendMessage(s);
					} else if (args.length > 1 && args[1].equalsIgnoreCase("players")) {
						sender.sendMessage("§9hChat players §7(player : real group : group : channel)§9:");
						ChatManager cm = gPlugin.getChatManager();
						for (Player p : Bukkit.getOnlinePlayers())
							sender.sendMessage(p.getName() + " : " + cm.getRealGroup(sender) + " : " + cm.getGroup(p.getUniqueId()).getId() + " : " + cm.getChannel(p.getUniqueId()).getId());
					} else {
						sender.sendMessage("§6Syntax: §r/hchat list <groups|channels|players>");
					}
				}
			} else {
				sender.sendMessage(COMMAND_USAGE);
			}
		}
	}

	private void cmdChannel(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CHANNEL)) {
			if (args.length > 0 && args[0].equalsIgnoreCase("join"))
				cmdChannelJoin(sender, args);
			else if (args.length > 0 && args[0].equalsIgnoreCase("leave"))
				cmdChannelLeave(sender, args);
			else if (args.length > 0 && args[0].equalsIgnoreCase("list"))
				cmdChannelList(sender, args);
			else if (args.length > 0 && args[0].equalsIgnoreCase("info"))
				cmdChannelInfo(sender, args);
			else if (args.length > 0 && args[0].equalsIgnoreCase("who"))
				cmdChannelWho(sender, args);
			else if (args.length > 0 && args[0].equalsIgnoreCase("create"))
				cmdChannelCreate(sender, args);
			else if (args.length > 0 && args[0].equalsIgnoreCase("delete"))
				cmdChannelDelete(sender, args);
			else if (args.length > 0 && args[0].equalsIgnoreCase("edit"))
				cmdChannelEdit(sender, args);
			else
				sender.sendMessage(COMMAND_CHANNEL_USAGE);
		}
	}

	private void cmdChannelJoin(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players may use this command.");
		} else {
			Player player = (Player) sender;
			if (args.length > 1) {
				String channelId = args[1].toLowerCase();
				HChannel channel = gPlugin.getChatManager().getChannelExact(channelId);
				if (channel == null || channelId.equalsIgnoreCase(ChatManager.DEFAULT_CHANNEL_NAME)) {
					sender.sendMessage("§cChannel not found: " + channelId);
				} else if (channelId.equalsIgnoreCase(gPlugin.getChatManager().getPlayerChannel(player.getUniqueId()))) {
					sender.sendMessage("§cYou are already in channel " + channelId + "§r§c.");
				} else {
					ChannelResult cr = checkCannel(channel, sender);
					if (cr.hasAccess) {
						boolean access = true;
						if (cr.isPassworded && !cr.isOwner) {
							access = false;
							if (gPasswordCooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() - gPasswordCooldowns.get(player.getUniqueId()) < 5000L) {
								sender.sendMessage("§cPlease wait a few seconds, the cooldown has not ended.");
							} else {
								if (args.length < 3) {
									sender.sendMessage("§cThe channel is password protected, please specify one.");
									sender.sendMessage("§6Syntax: §r/channel join <name> [password]");
								} else {
									String password = args[2];
									if (channel.getPassword().equalsIgnoreCase(password))
										access = true;
									else {
										sender.sendMessage("§cWrong password. Please wait five seconds before you retry.");
										gPlugin.getLogger().info(sender.getName() + " has entered the wrong password for channel + " + channel.getId());
										gPasswordCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
									}
								}
							}
						}
						if (access)
							gPlugin.getChatManager().changePlayerChannel(player.getUniqueId(), channelId, true);
					} else if (cr.isBanned) {
						sender.sendMessage("§cYou are banned from channel " + cr.color + channel.getName() + "§c.");
					} else if (cr.isMember) {
						sender.sendMessage("§cYou not a member of the private channel " + cr.color + channel.getName() + "§c.");
					} else {
						sender.sendMessage("§cYou don't have access to channel " + cr.color + channel.getName() + "§c.");
					}
				}
			} else {
				sender.sendMessage("§6Join a chat channel: §r/channel join <name> [password]");
			}
		}
	}

	private void cmdChannelLeave(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players may use this command.");
		} else {
			Player player = (Player) sender;
			if (gPlugin.getChatManager().getPlayerChannel(player.getUniqueId()).equalsIgnoreCase(ChatManager.DEFAULT_CHANNEL_NAME))
				sender.sendMessage("§cYou are not in a channel.");
			else
				gPlugin.getChatManager().changePlayerChannel(player.getUniqueId(), null, true);
		}
	}

	private void cmdChannelList(CommandSender sender, String[] args) {
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
			if (showAll)
				sender.sendMessage("§9All channels §7[id (name)]§9:");
			else if (showOnlyOwn)
				sender.sendMessage("§9Your channels §7[id (name)]§9:");
			else
				sender.sendMessage("§9Accessable channels §7[id (name)]§9:");
			sender.sendMessage("§8================================================");
			sender.sendMessage("§7(Use the ID in the parentheses to join channels etc)");
			boolean any = false;
			for (HChannel c : gPlugin.getChatManager().getChannels().values()) {
				ChannelResult cr = checkCannel(c, sender);
				if ((cr.hasAccess && !showOnlyOwn && !c.getId().equalsIgnoreCase(ChatManager.DEFAULT_CHANNEL_NAME)) || showAll || cr.isOwner) {
					sender.sendMessage(String.format("§8 * %s%s §7(%s)", cr.color, c.getName(), c.getId()));
					any = true;
				}
			}
			if (!any)
				sender.sendMessage("§8 * §7(none)");
		} else {
			sender.sendMessage("All channels (id (name)):");
			for (HChannel c : gPlugin.getChatManager().getChannels().values())
				sender.sendMessage(String.format("%s (%s)", c.getId(), c.getName()));
		}
	}

	private void cmdChannelInfo(final CommandSender sender, String[] args) {
		if (args.length == 1) {
			HChannel channel = gPlugin.getChatManager().getChannel(sender);
			sender.sendMessage("§fYou are currently in channel " + channel.getName() + " §r§7(" + channel.getId() + ")§r.");
			sender.sendMessage("§6Get info about a channel: §r/channel info <name>");
		} else {
			String channelId = args[1].toLowerCase();
			final HChannel channel = gPlugin.getChatManager().getChannel(channelId).clone();
			if (channel == null) {
				sender.sendMessage("§cChannel not found: " + channelId);
			} else {
				gPlugin.getServer().getScheduler().runTaskAsynchronously(gPlugin, new Runnable() {

					public void run() {
						ChannelResult cr = checkCannel(channel, sender);
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
								ownerName = gPlugin.getUuidManager().getName(uuid) + " (" + uuid + ")";
							} catch (IllegalArgumentException ex) {
								ownerName = "§ccMalformed UUID: " + channel.getOwner();
							}
						} else {
							ownerName = "(none)";
						}
						sender.sendMessage("§9# §7Owner: §f" + ownerName);
						sender.sendMessage("§9# §7Privacy: " + (channel.isPrivate() ? "§6Private" : "§aPublic"));
						sender.sendMessage("§9# §7Censored: " + (channel.isCensored() ? "§6Yes" : "§aNo"));
						sender.sendMessage("§9# §7Formatting codes: " + (channel.allowColorCodes() ? "§aYes" : "§cNo"));
						sender.sendMessage("§9# §7You have access: " + (cr.hasAccess ? "§aYes" : "§cNo"));
						sender.sendMessage("§9# §7Password protected: " + (cr.isPassworded ? "§cYes" : "§aNo"));
						if (channel.getMonitorChannels() != null) {
							sender.sendMessage("§9# ");
							sender.sendMessage("§9# §7Monitored channels (" + channel.getMonitorChannels().size() + "):");
							int c = 0;
							for (String memberId : channel.getMembers()) {
								if (c > 10) {
									String name = "§cEmpty name!";
									if (memberId.length() > 0) {
										try {
											name = gPlugin.getUuidManager().getNameNotNullNoSave(UUID.fromString(memberId));
										} catch (IllegalArgumentException ex) {
											name = "§cMalformed UUID: " + memberId;
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
						if (channel.getMembers() != null) {
							sender.sendMessage("§9# ");
							sender.sendMessage("§9# §7Members (" + channel.getMembers().size() + "):");
							int c = 0;
							for (String memberId : channel.getMembers()) {
								if (c > 10) {
									String name = "§cEmpty name!";
									if (memberId.length() > 0) {
										try {
											name = gPlugin.getUuidManager().getNameNotNullNoSave(UUID.fromString(memberId));
										} catch (IllegalArgumentException ex) {
											name = "§cMalformed UUID: " + memberId;
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
						if (channel.getBannedMembers() != null) {
							sender.sendMessage("§9# ");
							sender.sendMessage("§9# §7Banned members (" + channel.getBannedMembers().size() + "):");
							int c = 0;
							for (String memberId : channel.getBannedMembers()) {
								if (c > 10) {
									String name = "§cEmpty name!";
									if (memberId.length() > 0) {
										try {
											name = gPlugin.getUuidManager().getName(UUID.fromString(memberId));
										} catch (IllegalArgumentException ex) {
											name = "§cMalformed UUID: " + memberId;
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
					}
				});
			}
		}
	}

	private void cmdChannelWho(CommandSender sender, String[] args) {
		sender.sendMessage("");
		sender.sendMessage("§9Channel player list:");
		sender.sendMessage("§8================================================");
		for (HChannel c : gPlugin.getChatManager().getChannels().values()) {
			StringBuilder sb = new StringBuilder();
			sb.append("§8 * §r").append(c.getName()).append("§r§8: §r");
			Iterator<UUID> iter = gPlugin.getChatManager().getChannelPlayers(c.getId()).iterator();
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

	private void cmdChannelCreate(CommandSender sender, String[] args) {
		if (args.length > 2) {
			String channelId = args[1].toLowerCase();
			HChannel channel = gPlugin.getChatManager().getChannelExact(channelId);
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
					channel = new HChannel(channelId, owner, isPrivate);
					gPlugin.getChatManager().addChannel(channel);
					sender.sendMessage("§aSuccessfully created channel " + channel.getName() + "§a.");
				}
			}
		} else {
			sender.sendMessage("§6Create a chat channel: §r/channel create <name> <public|private>");
		}
	}

	private void cmdChannelDelete(CommandSender sender, String[] args) {
		if (args.length > 1) {
			String channelId = args[1].toLowerCase();
			HChannel channel = gPlugin.getChatManager().getChannelExact(channelId);
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
					gPlugin.getChatManager().removeChannel(channelId);
					sender.sendMessage("§aSuccessfully deleted channel " + channel.getName() + "§a.");
				} else {
					sender.sendMessage("§cFailed to delete channel " + channel.getName() + "§c.");
					sender.sendMessage("§cYou are not the owner of this channel.");
				}
			}
		} else {
			sender.sendMessage("§6Delete a chat channel: §r/channel delete <name>");
		}
	}

	private void cmdChannelEdit(CommandSender sender, String[] args) {
		if (args.length > 10) {
			String channelId = args[1].toLowerCase();
			HChannel channel = gPlugin.getChatManager().getChannelExact(channelId);
			if (channel == null) {
				sender.sendMessage("§cChannel doesn't exist: " + channelId);
			} else {
				sender.sendMessage("§7This feature is currently not implemented. The channels will have to be editet manually in the channels.yml file.");
				// TODO stuff
				// gPlugin.getChatManager().updateChannel(HChannel channel);
			}
		} else {
			sender.sendMessage("§6Edit a chat channel: §r/channel edit <name> {todo}");
		}
	}

	private void cmdClearChat(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_CLEAR)) {
			String name = (sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName();
			String[] message = new String[CLEAR_LENGTH];
			for (int i = 0; i < CLEAR_LENGTH; i++)
				message[i] = "";
			String[] clearedBy = new String[3];
			clearedBy[0] = "";
			clearedBy[1] = "§8    [§7Chat has been cleared by " + name + "§8]";
			clearedBy[2] = "";

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!player.hasPermission(PERM_UNCLEARABLE))
					player.sendMessage(message);
				player.sendMessage(clearedBy);
			}
		}
	}

	private void cmdColors(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_COLORS))
			sender.sendMessage(COLOR_MESSAGE);
	}

	private void cmdMe(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_ME)) {
			if (args.length == 0) {
				sender.sendMessage("§6Syntax: §r/me <action>");
			} else {
				String action = StringUtils.join(args, " ");
				String message;
				if (gPlugin.getChatManager().getFormatMe())
					message = gPlugin.getFormatManager().formatString(MessageType.ME, sender, null, action);
				else
					message = String.format("* %s %s", sender.getName(), action);
				Bukkit.broadcastMessage(message);
			}
		}
	}

	private void cmdMute(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_MUTE)) {
			if (args.length == 0) {
				sender.sendMessage("§6Syntax: §r/mute [-g] <player>");
				sender.sendMessage("§7Use '-g' option to mute globally.");
			} else {
				String playerName = args[0];
				boolean global = false;
				if (args.length > 1 && args[0].equalsIgnoreCase("-g")) {
					playerName = args[1];
					global = true;
				}

				Player mutedPlayer = Bukkit.getPlayer(gPlugin.getUuidManager().getUuid(playerName));
				if (mutedPlayer == null) {
					sender.sendMessage("§cPlayer not found: " + playerName);
				} else if (mutedPlayer.hasPermission(PERM_IMMUTABLE)) {
					sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§c can not be muted.");
				} else {
					if (global) {
						if (sender.hasPermission(PERM_MUTE_GLOBAL)) {
							if (gPlugin.getChatManager().isPlayerMutedGlobally(mutedPlayer.getUniqueId())) {
								sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§a is already globally muted.");
							} else {
								gPlugin.getChatManager().mutePlayerGlobally(mutedPlayer.getUniqueId(), true);
								sender.sendMessage("§aPlayer " + mutedPlayer.getName() + "§r§a has become globally muted.");
								mutedPlayer.sendMessage("§cYou have become globally muted.");
							}
						} else {
							sender.sendMessage("§cYou may not mute players globally.");
						}
					} else {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (gPlugin.getChatManager().isPlayerMutedIndividually(player.getUniqueId(), mutedPlayer.getUniqueId())) {
								sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§a is already muted.");
							} else {
								gPlugin.getChatManager().mutePlayerIndividually(((Player) sender).getUniqueId(), mutedPlayer.getUniqueId(), true);
								sender.sendMessage("§aYou have muted " + mutedPlayer.getName() + "§r§a.");
							}
						} else {
							sender.sendMessage("Non-players can only mute players globally.");
						}
					}
				}
			}
		}
	}

	private void cmdUnmute(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_MUTE)) {
			if (args.length == 0) {
				sender.sendMessage("§6Syntax: §r/unmute <player>");
			} else {
				String playerName = args[0];
				boolean global = false;
				if (args.length > 1 && args[0].equalsIgnoreCase("-g")) {
					playerName = args[1];
					global = true;
				}

				Player mutedPlayer = Bukkit.getPlayer(gPlugin.getUuidManager().getUuid(playerName));
				if (mutedPlayer == null) {
					sender.sendMessage("§cPlayer not found: " + playerName);
				} else if (mutedPlayer.hasPermission(PERM_IMMUTABLE)) {
					sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§c is immutable");
				} else {
					if (global) {
						if (sender.hasPermission(PERM_MUTE_GLOBAL)) {
							if (!gPlugin.getChatManager().isPlayerMutedGlobally(mutedPlayer.getUniqueId())) {
								sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§a is not globally muted.");
							} else {
								gPlugin.getChatManager().mutePlayerGlobally(mutedPlayer.getUniqueId(), false);
								sender.sendMessage("§aPlayer " + mutedPlayer.getName() + "§r§a has become globally unmuted.");
								mutedPlayer.sendMessage("§aYou have become globally unmuted.");
							}
						} else {
							sender.sendMessage("§cYou may not unmute players globally.");
						}
					} else {
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (!gPlugin.getChatManager().isPlayerMutedIndividually(player.getUniqueId(), mutedPlayer.getUniqueId())) {
								sender.sendMessage("§cPlayer " + mutedPlayer.getName() + "§r§a is not muted.");
							} else {
								gPlugin.getChatManager().mutePlayerIndividually(((Player) sender).getUniqueId(), mutedPlayer.getUniqueId(), false);
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

	private void cmdTell(CommandSender sender, String[] args) {
		if (hasPerm(sender, PERM_COMMAND_TELL)) {
			if (args.length < 2) {
				sender.sendMessage("§6Syntax: §r/tell <player> <message>");
			} else {
				CommandSender receiver;
				if (args[0].equalsIgnoreCase("console"))
					receiver = Bukkit.getConsoleSender();
				else {
					receiver = Bukkit.getPlayer(gPlugin.getUuidManager().getUuid(args[0]));
					if (receiver == null) {
						sender.sendMessage("§cPlayer not found: " + args[0]);
						return;
					}
				}
				String message = StringUtils.join(args, ' ', 1, args.length);
				String senderText;
				String receiverText;
				String spyText;
				if (gPlugin.getChatManager().getFormatTell()) {
					senderText = gPlugin.getFormatManager().formatString(MessageType.TELL_SENDER, sender, receiver, message);
					receiverText = gPlugin.getFormatManager().formatString(MessageType.TELL_RECEIVER, sender, receiver, message);
					spyText = gPlugin.getFormatManager().formatString(MessageType.TELL_SPY, sender, receiver, message);
				}
				else {
					senderText = String.format("[%s->%s] %s", sender.getName(), receiver.getName(), message);
					receiverText = String.format("§7% whispers %s", sender.getName(), receiver.getName(), message);
					spyText = String.format("[%s->%s] %s", sender.getName(), receiver.getName(), message);
				}
				if (gPlugin.getChatManager().getGroup(sender).getShowPersonalMessages()) {
					Bukkit.getLogger().info("PM: [" + sender.getName() + "->" + receiver.getName() + "] " + message);
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player != sender && player.hasPermission(PERM_SPY))
							player.sendMessage(spyText);
					}
				}
				if (sender != Bukkit.getConsoleSender())
					sender.sendMessage(senderText);
				receiver.sendMessage(receiverText);
			}
		}
	}

	private boolean hasPerm(CommandSender sender, String perm) {
		if (!sender.hasPermission(perm)) {
			sender.sendMessage("§cPermission denied!");
			return false;
		}
		return true;
	}

	private ChannelResult checkCannel(HChannel channel, CommandSender sender) {
		ChannelResult cr = new ChannelResult();

		if (sender instanceof Player) {
			Player player = (Player) sender;
			String strUuid = player.getUniqueId().toString();

			if (channel.getPassword() != null && channel.getPassword().length() > 0)
				cr.isPassworded = true;
			if (channel.isPrivate()) {
				if (player.hasPermission(PERM_CHANNEL_ALL) || player.hasPermission(PERM_CHANNEL_PREFIX + channel.getId())) {
					cr.hasAccess = true;
				} else if (channel.getMembers() != null && channel.getMembers().contains(strUuid)) {
					cr.hasAccess = true;
					cr.isMember = true;
				} else {
					cr.hasAccess = false;
				}
			} else {
				cr.hasAccess = true;
			}
			if (channel.getBannedMembers() != null && channel.getBannedMembers().contains(strUuid)) {
				cr.isBanned = true;
				cr.hasAccess = false;
			}
			if (channel.getOwner() != null && channel.getOwner().equalsIgnoreCase(strUuid)) {
				cr.hasAccess = true;
				cr.isOwner = true;
			}
		} else {
			cr.hasAccess = true;
		}
		if (cr.isOwner) {
			cr.color = ChatColor.BLUE;
		} else if (cr.hasAccess) {
			if (channel.isPrivate())
				cr.color = ChatColor.GOLD;
			else
				cr.color = ChatColor.GREEN;
		} else {
			cr.color = ChatColor.RED;
		}

		return cr;
	}

	private class ChannelResult {
		public boolean hasAccess = false;
		public boolean isOwner = false;
		public boolean isMember = false;
		public boolean isBanned = false;
		public boolean isPassworded = false;
		public ChatColor color = null;

	}
}
