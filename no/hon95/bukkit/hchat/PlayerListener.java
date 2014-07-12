package no.hon95.bukkit.hchat;

import java.util.List;

import no.hon95.bukkit.hchat.format.Formatter.MessageType;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

	@EventHandler(priority = EventPriority.HIGH)
	public void onEvent(AsyncPlayerChatEvent ev) {
		gPlugin.getChatManager().onChat(ev);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEvent(PlayerDeathEvent ev) {
		if (gPlugin.getChatManager().getFormatDeath()) {
			String message = gPlugin.getFormatManager().formatString(MessageType.DEATH, ev.getEntity(), null, ev.getDeathMessage());
			ev.setDeathMessage(message);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEvent(PlayerJoinEvent ev) {
		gPlugin.getChatManager().loadPlayer(ev.getPlayer());
		if (gPlugin.getChatManager().getFormatJoin()) {
			String message = gPlugin.getFormatManager().formatString(MessageType.JOIN, ev.getPlayer(), null, null);
			ev.setJoinMessage(message);
		}
		if (gPlugin.getChatManager().getFormatMotd()) {
			List<String> motd = gPlugin.getFormatManager().formatList(MessageType.MOTD, ev.getPlayer(), null, null);
			if (motd.size() > 0)
				ev.getPlayer().sendMessage(motd.toArray(new String[0]));
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEvent(PlayerQuitEvent ev) {
		if (gPlugin.getChatManager().getFormatQuit()) {
			String message = gPlugin.getFormatManager().formatString(MessageType.QUIT, ev.getPlayer(), null, null);
			ev.setQuitMessage(message);
		}
		gPlugin.getChatManager().unloadPlayer(ev.getPlayer());
	}

	@EventHandler
	public void onEvent(PlayerChangedWorldEvent ev) {
		gPlugin.getChatManager().updateNamesNextTick(ev.getPlayer());
	}

	@EventHandler
	public void onEvent(PlayerLevelChangeEvent ev) {
		gPlugin.getChatManager().updateNamesNextTick(ev.getPlayer());
	}

	@EventHandler
	public void onEvent(FoodLevelChangeEvent ev) {
		if (ev.getEntityType() != EntityType.PLAYER)
			return;
		gPlugin.getChatManager().updateNamesNextTick((Player) ev.getEntity());
	}

	@EventHandler
	public void onEvent(EntityDamageEvent ev) {
		if (ev.getEntityType() != EntityType.PLAYER)
			return;
		gPlugin.getChatManager().updateNamesNextTick((Player) ev.getEntity());
	}
}
