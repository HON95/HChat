package no.hon95.bukkit.hchat.common.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import no.hon95.bukkit.hchat.common.evilmidget38.NameFetcher;
import no.hon95.bukkit.hchat.common.evilmidget38.UUIDFetcher;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerIdUtils {

	private static final boolean BUKKIT_UUID_SUPPORT;
	static {
		boolean support;
		try {
			Bukkit.getPlayer(UUID.fromString("00000000-0000-0000-0000-000000000000"));
			support = true;
		} catch (NoSuchMethodError err) {
			support = false;
		}
		BUKKIT_UUID_SUPPORT = support;
	}

	private PlayerIdUtils() {}

	public static UUID getPlayerUuid(String name, boolean downloadIfNecessary) {
		UUID uuid = null;
		if (isUuidSupported()) {
			Player player = getLocalPlayer(name);
			if (player != null)
				uuid = player.getUniqueId();
		}
		if (uuid == null && downloadIfNecessary) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(name);
			try {
				Map<String, UUID> uuids = new UUIDFetcher(list).call();
				for (Entry<String, UUID> e : uuids.entrySet()) {
					if (e.getKey().equalsIgnoreCase(name)) {
						uuid = e.getValue();
						break;
					}
				}
			} catch (Exception ex) {
				Bukkit.getLogger().warning("Failed to download UUID for local player name '" + name + "'.");
				ex.printStackTrace();
			}
		}
		return uuid;
	}

	public static String getPlayerName(UUID uuid, boolean downloadIfNecessary) {
		String name = null;
		if (isUuidSupported()) {
			Player player = getLocalPlayer(uuid);
			if (player != null)
				name = player.getName();
		}
		if (name == null && downloadIfNecessary) {
			ArrayList<UUID> list = new ArrayList<UUID>();
			list.add(uuid);
			try {
				Map<UUID, String> names = new NameFetcher(list).call();
				for (Entry<UUID, String> e : names.entrySet()) {
					if (e.getKey().equals(uuid)) {
						name = e.getValue();
						break;
					}
				}
			} catch (Exception ex) {
				Bukkit.getLogger().warning("Failed to download name for player UUID '" + uuid + "'.");
				ex.printStackTrace();
			}
		}
		return name;
	}

	public static Player getLocalPlayer(UUID uuid) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getUniqueId().equals(uuid))
				return p;
		}
		return null;
	}

	public static Player getLocalPlayer(String name) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(name))
				return p;
		}
		return null;
	}

	public static String getLocalPlayerName(UUID uuid) {
		Player player = getLocalPlayer(uuid);
		if (player != null)
			return player.getName();
		else
			return null;
	}

	public static UUID getLocalPlayerUuid(String name) {
		Player player = getLocalPlayer(name);
		if (player != null)
			return player.getUniqueId();
		else
			return null;
	}

	public static boolean isUuidSupported() {
		return BUKKIT_UUID_SUPPORT && Bukkit.getOnlineMode();
	}
}
