package no.hon95.bukkit.hchat;

import static no.hon95.bukkit.hchat.InfoManager.BUKKIT_VAR_MSG;
import static no.hon95.bukkit.hchat.InfoManager.BUKKIT_VAR_PLAYER;
import static no.hon95.bukkit.hchat.InfoManager.VAR_FOOD;
import static no.hon95.bukkit.hchat.InfoManager.VAR_GAME_MODE;
import static no.hon95.bukkit.hchat.InfoManager.VAR_GROUP;
import static no.hon95.bukkit.hchat.InfoManager.VAR_GROUP_REAL;
import static no.hon95.bukkit.hchat.InfoManager.VAR_HEALTH;
import static no.hon95.bukkit.hchat.InfoManager.VAR_LEVEL;
import static no.hon95.bukkit.hchat.InfoManager.VAR_MSG;
import static no.hon95.bukkit.hchat.InfoManager.VAR_PLAYER;
import static no.hon95.bukkit.hchat.InfoManager.VAR_POS_X;
import static no.hon95.bukkit.hchat.InfoManager.VAR_POS_Y;
import static no.hon95.bukkit.hchat.InfoManager.VAR_POS_Z;
import static no.hon95.bukkit.hchat.InfoManager.VAR_PREFIX;
import static no.hon95.bukkit.hchat.InfoManager.VAR_SUFFIX;
import static no.hon95.bukkit.hchat.InfoManager.VAR_TIME;
import static no.hon95.bukkit.hchat.InfoManager.VAR_TIME_SHORT;
import static no.hon95.bukkit.hchat.InfoManager.VAR_WORLD;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public final class PlayerListener implements Listener {

	private final HChatPlugin gPlugin;

	public PlayerListener(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent ev) {

		Player player = ev.getPlayer();
		String playerName = player.getName();
		String worldName = player.getWorld().getName();

		User user = gPlugin.getInfoManager().getUser(playerName);
		Group group = gPlugin.getInfoManager().getGroup(user.group);

		if (group.censor)
			ev.setMessage(ChatCensor.censor(ev.getMessage(), gPlugin.getInfoManager().getCensoredWords()));
		if (group.colorCodes)
			ev.setMessage(ChatColor.translateAlternateColorCodes('&', ev.getMessage()));
		if (!group.canChat) {
			ev.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to chat!");
		}

		String realGroup = (user.realGroup != null) ? user.realGroup : "";
		String time = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
		String timeShort = new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()));
		String health = ""; // String.valueOf(100 * player.getHealth()/player.getMaxHealth()) + "%%"; //FIXME ambiguous problem
		String food = String.valueOf(player.getFoodLevel() * 5) + "%%";
		String level = String.valueOf(player.getLevel());
		Location loc = player.getLocation();
		String gameMode = player.getGameMode().name();
		String posX = String.valueOf(loc.getBlockX());
		String posY = String.valueOf(loc.getBlockY());
		String posZ = String.valueOf(loc.getBlockZ());

		String finishedFormat = group.format.replace(VAR_PLAYER, BUKKIT_VAR_PLAYER).replace(VAR_MSG, BUKKIT_VAR_MSG)
				.replace(VAR_GROUP, group.name).replace(VAR_GROUP_REAL, realGroup).replace(VAR_PREFIX, user.prefix).replace(VAR_SUFFIX, user.suffix)
				.replace(VAR_TIME, time).replace(VAR_TIME_SHORT, timeShort).replace(VAR_WORLD, worldName)
				.replace(VAR_HEALTH, health).replace(VAR_FOOD, food).replace(VAR_LEVEL, level).replace(VAR_GAME_MODE, gameMode)
				.replace(VAR_POS_X, posX).replace(VAR_POS_Y, posY).replace(VAR_POS_Z, posZ);
		finishedFormat = ChatColor.translateAlternateColorCodes('&', finishedFormat);

		ev.setFormat(finishedFormat);
	}

	@EventHandler
	public void onEvent(PlayerJoinEvent ev) {
		gPlugin.getInfoManager().updatePlayer(ev.getPlayer());
	}

	@EventHandler
	public void onEvent(PlayerQuitEvent ev) {
		gPlugin.getInfoManager().removePlayer(ev.getPlayer());
	}

	@EventHandler
	public void onEvent(PlayerChangedWorldEvent ev) {
		gPlugin.getInfoManager().updatePlayer(ev.getPlayer());
	}

	@EventHandler
	public void onEvent(PlayerLevelChangeEvent ev) {
		gPlugin.getInfoManager().updatePlayer(ev.getPlayer());
	}

	@EventHandler
	public void onEvent(FoodLevelChangeEvent ev) {
		if (ev.getEntityType() != EntityType.PLAYER)
			return;
		gPlugin.getInfoManager().updatePlayer((Player) ev.getEntity());
	}

	@EventHandler
	public void onEvent(EntityDamageEvent ev) {
		if (ev.getEntityType() != EntityType.PLAYER)
			return;
		gPlugin.getInfoManager().updatePlayer((Player) ev.getEntity());
	}
}
