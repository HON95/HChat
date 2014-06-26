package no.hon95.bukkit.hchat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import no.hon95.bukkit.hchat.util.evilmidget38.NameFetcher;
import no.hon95.bukkit.hchat.util.evilmidget38.UUIDFetcher;


public final class UuidManager {

	private final ConcurrentHashMap<UUID, String> gNames = new ConcurrentHashMap<UUID, String>();
	private final ConcurrentHashMap<String, UUID> gUuids = new ConcurrentHashMap<String, UUID>();
	private final Set<UUID> gNameDownloads = Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
	private final Set<String> gUuidDownloads = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	private final Object gNameDownloadLock = new Object();
	private final Object gUuidDownloadLock = new Object();

	public String getName(UUID uuid) {
		String name = gNames.get(uuid);
		if (name != null)
			return name;
		while (gNameDownloads.contains(uuid)) {
			synchronized (gNameDownloadLock) {
			}
			name = gNames.get(uuid);
			if (name != null)
				return name;
		}
		return null;
	}

	public String getNameNotNullNoSave(UUID uuid) {
		String name = getName(uuid);
		if (name == null) {
			ArrayList<UUID> list = new ArrayList<UUID>();
			list.add(uuid);
			try {
				Map<UUID, String> names = new NameFetcher(list).call();
				for (Entry<UUID, String> e : names.entrySet()) {
					if (e.getKey().equals(uuid)) {
						name = e.getValue();
					}
				}
			} catch (Exception ex) {
				System.err.println("[hChat] Failed to download a name.");
				ex.printStackTrace();
			}
		}
		return name;
	}

	public UUID getUuid(String name) {
		name = name.toLowerCase();
		UUID uuid = gUuids.get(name);
		if (uuid != null)
			return uuid;
		while (gUuidDownloads.contains(name)) {
			synchronized (gUuidDownloadLock) {
			}
			gUuids.get(name);
			if (uuid != null)
				return uuid;
		}
		return null;
	}

	public void loadUuid(String name) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(name);
		loadUuids(list);
	}

	public void loadName(UUID uuid) {
		ArrayList<UUID> list = new ArrayList<UUID>();
		list.add(uuid);
		loadNames(list);
	}

	public void loadUuids(final List<String> names) {
		new Thread(new Runnable() {
			public void run() {
				synchronized (gUuidDownloadLock) {
					try {
						Map<String, UUID> uuids = new UUIDFetcher(names).call();
						for (Entry<String, UUID> e : uuids.entrySet()) {
							gNames.put(e.getValue(), e.getKey());
							gUuids.put(e.getKey().toLowerCase(), e.getValue());
							gUuidDownloads.remove(e.getKey());
						}
					} catch (Exception ex) {
						System.err.println("[hChat] Failed to download UUIDs for UuidManager.");
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void loadNames(final List<UUID> uuids) {
		new Thread(new Runnable() {
			public void run() {
				synchronized (gNameDownloadLock) {
					try {
						Map<UUID, String> names = new NameFetcher(uuids).call();
						for (Entry<UUID, String> e : names.entrySet()) {
							gNames.put(e.getKey(), e.getValue());
							gUuids.put(e.getValue().toLowerCase(), e.getKey());
							gNameDownloads.remove(e.getKey());
						}
					} catch (Exception ex) {
						System.err.println("[hChat] Failed to download names for UuidManager.");
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}
}
