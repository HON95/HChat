package no.hon95.bukkit.hchat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import no.hon95.bukkit.hchat.util.mcstats.Metrics;
import no.hon95.bukkit.hchat.util.mcstats.Metrics.Graph;
import no.hon95.bukkit.hchat.util.mcstats.Metrics.Plotter;

import org.bukkit.Bukkit;


public final class MetricsManager {

	private final HChatPlugin gPlugin;
	private Metrics gMetrics = null;
	private boolean gCollectData = false;

	public MetricsManager(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	public void load() {
		if (!gCollectData) {
			gPlugin.getLogger().info("Data collection has been disabled.");
			return;
		}
		try {
			gMetrics = new Metrics(gPlugin);
		} catch (IOException ex) {
			gPlugin.getLogger().warning("Failed to load Metrics.");
			ex.printStackTrace();
			return;
		}

		Graph groupGraph = gMetrics.createGraph("Number of groups");
		groupGraph.addPlotter(new GroupPlotter());
		Graph channelGraph = gMetrics.createGraph("Number of channels");
		channelGraph.addPlotter(new ChannelPlotter());
	}

	public void start() {
		if (gCollectData)
			gMetrics.start();
		else
			gPlugin.getLogger().info("Data collection disabled.");
	}

	public void setCollectData(boolean collect) {
		gCollectData = collect;
	}

	private class GroupPlotter extends Plotter {

		private int gNumGroups = -1;
		private CountDownLatch gLatch = null;

		@Override
		public int getValue() {
			gLatch = new CountDownLatch(1);
			Bukkit.getScheduler().runTask(gPlugin, new Runnable() {
				public void run() {
					gNumGroups = gPlugin.getChatManager().getGroups().size();
					gLatch.countDown();
				}
			});
			try {
				gLatch.await();
			} catch (InterruptedException ex) {
				gPlugin.getLogger().warning("Failed to wait for GroupPlotter to get data.");
				ex.printStackTrace();
			}
			return gNumGroups;
		}
	}

	private class ChannelPlotter extends Plotter {

		private int gNumChannels = -1;
		private CountDownLatch gLatch = null;

		@Override
		public int getValue() {
			gLatch = new CountDownLatch(1);
			Bukkit.getScheduler().runTask(gPlugin, new Runnable() {
				public void run() {
					gNumChannels = gPlugin.getChatManager().getChannels().size();
					gLatch.countDown();
				}
			});
			try {
				gLatch.await();
			} catch (InterruptedException ex) {
				gPlugin.getLogger().warning("Failed to wait for ChannelPlotter to get data.");
				ex.printStackTrace();
			}
			return gNumChannels;
		}
	}
}
