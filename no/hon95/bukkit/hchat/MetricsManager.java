package no.hon95.bukkit.hchat;

import java.io.IOException;

import no.hon95.bukkit.hchat.util.mcstats.Metrics;


public final class MetricsManager {

	private final HChatPlugin gPlugin;
	private Metrics gMetrics = null;
	private boolean gCollectData = false;

	public MetricsManager(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	public void load() {
		if (gCollectData) {
			try {
				gMetrics = new Metrics(gPlugin);
			} catch (IOException ex) {
				gPlugin.getLogger().warning("Failed to load Metrics.");
				ex.printStackTrace();
				return;
			}
		} else {
			gPlugin.getLogger().info("Data collection has been disabled.");
		}
	}

	public void start() {
		if (gMetrics != null)
			gMetrics.start();
	}

	public void setCollectData(boolean collect) {
		gCollectData = collect;
	}
}
