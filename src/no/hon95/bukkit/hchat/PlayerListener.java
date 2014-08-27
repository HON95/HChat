package no.hon95.bukkit.hchat;

import java.util.List;

import no.hon95.bukkit.hchat.format.FormatManager.MessageType;

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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public final class PlayerListener implements Listener {

	private final HChatPlugin gPlugin;

	public PlayerListener(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEvent(AsyncPlayerChatEvent ev) {
		cm().onPlayerMoveOrUpdate(ev.getPlayer());
		cm().onPlayerChat(ev);
	}
	
	@EventHandler
	public void onEvent(PlayerCommandPreprocessEvent ev) {
		cm().onPlayerMoveOrUpdate(ev.getPlayer());
	}
	
	@EventHandler
	public void onEvent(PlayerDropItemEvent ev) {
		cm().onPlayerMoveOrUpdate(ev.getPlayer());
	}
	
	@EventHandler
	public void onEvent(PlayerInteractEvent ev) {
		cm().onPlayerMoveOrUpdate(ev.getPlayer());
	}
	
	@EventHandler
	public void onEvent(PlayerInteractEntityEvent ev) {
		cm().onPlayerMoveOrUpdate(ev.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEvent(PlayerDeathEvent ev) {
		if (cm().getFormatDeath()) {
			String message = gPlugin.getFormatManager().formatString(MessageType.DEATH, ev.getEntity(), null, ev.getDeathMessage());
			ev.setDeathMessage(message);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEvent(PlayerJoinEvent ev) {
		cm().loadPlayer(ev.getPlayer());
		if (cm().getFormatJoin()) {
			String message = gPlugin.getFormatManager().formatString(MessageType.JOIN, ev.getPlayer(), null, null);
			ev.setJoinMessage(message);
		}
		if (cm().getFormatMotd()) {
			List<String> motd = gPlugin.getFormatManager().formatList(MessageType.MOTD, ev.getPlayer(), null, null);
			if (motd.size() > 0)
				ev.getPlayer().sendMessage(motd.toArray(new String[0]));
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEvent(PlayerQuitEvent ev) {
		if (cm().getFormatQuit()) {
			String message = gPlugin.getFormatManager().formatString(MessageType.QUIT, ev.getPlayer(), null, null);
			ev.setQuitMessage(message);
		}
		if (cm().getKickPlayers().contains(ev.getPlayer().getUniqueId()))
			ev.setQuitMessage("");
		cm().unloadPlayer(ev.getPlayer());
	}

	@EventHandler
	public void onEvent(PlayerChangedWorldEvent ev) {
		cm().updatePlayerGroup(ev.getPlayer());
		cm().onPlayerWorldChanged(ev);
	}

	@EventHandler
	public void onEvent(PlayerLevelChangeEvent ev) {
		cm().updateNamesNextTick(ev.getPlayer());
	}

	@EventHandler
	public void onEvent(FoodLevelChangeEvent ev) {
		if (ev.getEntityType() != EntityType.PLAYER)
			return;
		cm().updateNamesNextTick((Player) ev.getEntity());
	}

	@EventHandler
	public void onEvent(EntityDamageEvent ev) {
		if (ev.getEntityType() != EntityType.PLAYER)
			return;
		cm().updateNamesNextTick((Player) ev.getEntity());
	}

	@EventHandler
	public void inEvent(PlayerMoveEvent ev) {
		cm().onPlayerMoveOrUpdate(ev.getPlayer());
	}
	
	private ChatManager cm() {
		return gPlugin.getChatManager();
	}
}
