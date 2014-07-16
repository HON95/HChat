package no.hon95.bukkit.hchat.hook;

import net.milkbowl.vault.economy.Economy;
import no.hon95.bukkit.hchat.HChatPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public final class VaultEconomyHook implements Hook {

	private static final String CLASS = "net.milkbowl.vault.economy.Economy";

	private HChatPlugin gPlugin;
	private Economy gEconomyPlugin = null;

	public VaultEconomyHook(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public boolean hook() {
		try {
			Class.forName(CLASS);
			gEconomyPlugin = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
			gPlugin.getLogger().info("Hooked Vault economy plugin.");
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public boolean isHooked() {
		return (gEconomyPlugin != null);
	}

	public double getCurrency(Player player) {
		if (gEconomyPlugin == null)
			return 0D;
		try {
			if (gEconomyPlugin.hasAccount(player))
				return gEconomyPlugin.getBalance(player);
		} catch (Exception ex) {
			return 0D;
		}
		return 0D;
	}

	public String getCurrencyString(Player player) {
		if (gEconomyPlugin == null)
			return null;
		try {
			if (gEconomyPlugin.hasAccount(player))
				return gEconomyPlugin.format(gEconomyPlugin.getBalance(player));
		} catch (Exception ex) {
			return null;
		}
		return null;
	}
}
