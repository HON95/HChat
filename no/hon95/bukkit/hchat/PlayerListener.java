package no.hon95.bukkit.hchat;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

	@EventHandler
	public void onEvent(AsyncPlayerChatEvent ev) {
		if (!gPlugin.getChatManager().getFormatChat())
			return;

		Player player = ev.getPlayer();
		HGroup group = gPlugin.getChatManager().getGroup(player.getUniqueId());
		if (group.censor)
			ev.setMessage(ChatCensor.censor(ev.getMessage(), gPlugin.getChatManager().getCensoredWords()));
		if (group.colorCodes)
			ev.setMessage(ChatColor.translateAlternateColorCodes('&', ev.getMessage()));
		if (!group.canChat) {
			ev.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to chat!");
		}
		String chatFormat = gPlugin.getChatManager().formatChat(player);
		ev.setFormat(chatFormat);
	}

	@EventHandler
	public void onEvent(PlayerDeathEvent ev) {
		if (gPlugin.getChatManager().getFormatDeath())
			ev.setDeathMessage(gPlugin.getChatManager().formatDeath(ev.getEntity(), ev.getDeathMessage()));
	}

	@EventHandler
	public void onEvent(PlayerJoinEvent ev) {
		gPlugin.getChatManager().updatePlayer(ev.getPlayer());
		if (gPlugin.getChatManager().getFormatJoin())
			ev.setJoinMessage(gPlugin.getChatManager().formatJoin(ev.getPlayer()));
		if (gPlugin.getChatManager().getFormatMotd()) {
			List<String> motd = gPlugin.getChatManager().formatMotd(ev.getPlayer());
			if (motd.size() > 0)
				ev.getPlayer().sendMessage(motd.toArray(new String[0]));
		}
	}

	@EventHandler
	public void onEvent(PlayerQuitEvent ev) {
		if (gPlugin.getChatManager().getFormatQuit())
			ev.setQuitMessage(gPlugin.getChatManager().formatQuit(ev.getPlayer()));
		gPlugin.getChatManager().removePlayer(ev.getPlayer());
	}

	//Events after this line; update next tick

	@EventHandler
	public void onEvent(PlayerChangedWorldEvent ev) {
		gPlugin.getChatManager().updatePlayer(ev.getPlayer()); //Useless, player is still in same world
	}

	@EventHandler
	public void onEvent(PlayerLevelChangeEvent ev) {
		gPlugin.getChatManager().updatePlayer(ev.getPlayer()); //Useless, player still has same level
	}

	@EventHandler
	public void onEvent(FoodLevelChangeEvent ev) {
		if (ev.getEntityType() != EntityType.PLAYER)
			return;
		gPlugin.getChatManager().updatePlayer((Player) ev.getEntity()); //Useless, still same state
	}

	@EventHandler
	public void onEvent(EntityDamageEvent ev) {
		if (ev.getEntityType() != EntityType.PLAYER)
			return;
		gPlugin.getChatManager().updatePlayer((Player) ev.getEntity());//Useless, still same state
	}
}
