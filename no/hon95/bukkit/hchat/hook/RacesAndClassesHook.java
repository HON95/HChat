package no.hon95.bukkit.hchat.hook;

import java.util.UUID;

import no.hon95.bukkit.hchat.HChatPlugin;
import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;


public final class RacesAndClassesHook implements Hook {

	private HChatPlugin gPlugin;
	private RacesAndClasses gOtherPlugin = null;

	public RacesAndClassesHook(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public boolean hook() {
		try {
			Class.forName("de.tobiyas.racesandclasses.RacesAndClasses");
			gPlugin.getLogger().info("Hooking RacesAndClasses.");
			gOtherPlugin = RacesAndClasses.getPlugin();
			if (gOtherPlugin == null) {
				gPlugin.getLogger().warning("Failed to hook into RacesAndClasses!");
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isHooked() {
		return gOtherPlugin != null;
	}

	public String getClass(UUID player) {
		if (gOtherPlugin == null)
			return null;
		AbstractTraitHolder holder = gOtherPlugin.getClassManager().getHolderOfPlayer(player);
		return holder != null ? holder.getName() : null; //FIXME
	}

	public String getRace(UUID player) {
		if (gOtherPlugin == null)
			return null;
		AbstractTraitHolder holder = gOtherPlugin.getRaceManager().getHolderOfPlayer(player);
		return holder != null ? holder.getName() : null; //FIXME
	}
}
