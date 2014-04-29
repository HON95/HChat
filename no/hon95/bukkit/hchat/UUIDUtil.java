package no.hon95.bukkit.hchat;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import com.evilmidget38.NameFetcher;
import com.evilmidget38.UUIDFetcher;


public final class UUIDUtil {

	private UUIDUtil() {}

	public static String getName(UUID uuid) {
		ArrayList<UUID> uuidList = new ArrayList<UUID>();
		uuidList.add(uuid);
		try {
			Map<UUID, String> names = new NameFetcher(uuidList).call();
			return names.get(uuid);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static UUID getUUID(String name) {
		ArrayList<String> nameList = new ArrayList<String>();
		nameList.add(name);
		try {
			Map<String, UUID> uuids = new UUIDFetcher(nameList).call();
			return uuids.get(name);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
