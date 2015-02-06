package no.hon95.bukkit.hchat.hook;

import net.milkbowl.vault.permission.Permission;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class VaultPermissionHook implements Hook {

	private static final String CLASS = "net.milkbowl.vault.permission.Permission";

	private HChatPlugin gPlugin;
	private Permission gPermissionPlugin = null;

	public VaultPermissionHook(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public boolean hook() {
		try {
			Class.forName(CLASS);
			gPermissionPlugin = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
			gPlugin.getLogger().info("Hooked Vault permission plugin: " + gPermissionPlugin.getName());
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public boolean isHooked() {
		return (gPermissionPlugin != null);
	}

	public String getGroup(Player player) {
		if (gPermissionPlugin == null)
			return null;
		try {
			return gPermissionPlugin.getPrimaryGroup(player);
		} catch (Exception ex) {
			return null;
		}
	}

	public String[] getGroups(Player player) {
		if (gPermissionPlugin == null)
			return null;
		try {
			return gPermissionPlugin.getPlayerGroups(player);
		} catch (Exception ex) {
			return null;
		}
	}

}
