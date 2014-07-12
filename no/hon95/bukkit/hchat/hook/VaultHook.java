package no.hon95.bukkit.hchat.hook;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public final class VaultHook implements Hook {

	private static final String CLASS_PERMISSION = "net.milkbowl.vault.permission.Permission";
	private static final String CLASS_ECONOMY = "net.milkbowl.vault.economy.Economy";

	private HChatPlugin gPlugin;
	private Permission gPermissionPlugin = null;
	private Economy gEconomyPlugin = null;

	public VaultHook(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public boolean hook() {
		boolean success = false;
		if (hookPermission())
			success = true;
		if (hookEconomy())
			success = true;
		return success;
	}

	private boolean hookPermission() {
		try {
			Class.forName(CLASS_PERMISSION);
			gPermissionPlugin = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
			gPlugin.getLogger().info("Hooked Vault permission plugin.");
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private boolean hookEconomy() {
		try {
			Class.forName(CLASS_ECONOMY);
			gEconomyPlugin = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
			gPlugin.getLogger().info("Hooked Vault economy plugin.");
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public boolean isHooked() {
		return (gPermissionPlugin != null || gEconomyPlugin != null);
	}

	public String getGroup(Player player) {
		if (gPermissionPlugin == null)
			return null;
		return gPermissionPlugin.getPrimaryGroup(player);
	}

	public String[] getGroups(Player player) {
		if (gPermissionPlugin == null)
			return null;
		return gPermissionPlugin.getPlayerGroups(player);
	}

	public double getCurrency(Player player) {
		if (gEconomyPlugin == null)
			return 0D;
		if (gEconomyPlugin.hasAccount(player))
			return gEconomyPlugin.getBalance(player);
		return 0D;
	}

	public String getCurrencyString(Player player) {
		if (gEconomyPlugin == null)
			return null;
		if (gEconomyPlugin.hasAccount(player))
			return gEconomyPlugin.format(gEconomyPlugin.getBalance(player));
		return null;
	}
}
