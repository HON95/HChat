package no.hon95.bukkit.hchat.hook;

import net.milkbowl.vault.permission.Permission;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public final class VaultHook implements Hook {

	private HChatPlugin gPlugin;
	private Permission gPermissionPlugin = null;

	public VaultHook(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public boolean hook() {
		try {
			Class.forName("net.milkbowl.vault.permission.Permission");
			gPlugin.getLogger().info("Hooking Vault.");
			gPermissionPlugin = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
			if (gPermissionPlugin == null) {
				gPlugin.getLogger().warning("Failed to hook into Vault!");
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isHooked() {
		return gPermissionPlugin != null;
	}

	public String getPlayerGroup(Player player) {
		if (gPermissionPlugin == null)
			return null;
		return gPermissionPlugin.getPrimaryGroup(player);
	}

	public String[] getPlayerGroups(Player player) {
		if (gPermissionPlugin == null)
			return null;
		return gPermissionPlugin.getPlayerGroups(player);
	}
}
