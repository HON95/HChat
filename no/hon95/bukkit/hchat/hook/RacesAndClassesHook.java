package no.hon95.bukkit.hchat.hook;

import java.util.UUID;

import no.hon95.bukkit.hchat.HChatPlugin;
import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;


public final class RacesAndClassesHook implements Hook {

	private static final String CLASS = "de.tobiyas.racesandclasses.RacesAndClasses";

	private HChatPlugin gPlugin;
	private RacesAndClasses gOtherPlugin = null;

	public RacesAndClassesHook(HChatPlugin plugin) {
		gPlugin = plugin;
	}

	@Override
	public boolean hook() {
		try {
			Class.forName(CLASS);
			gOtherPlugin = RacesAndClasses.getPlugin();
			if (gOtherPlugin == null) {
				gPlugin.getLogger().warning("Failed to hook RacesAndClasses!");
				return false;
			}
			gPlugin.getLogger().info("Hooked RacesAndClasses.");
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public boolean isHooked() {
		return gOtherPlugin != null;
	}

	public String getClass(UUID player) {
		if (gOtherPlugin == null)
			return null;
		try {
			AbstractTraitHolder holder = gOtherPlugin.getClassManager().getHolderOfPlayer(player);
			return holder != null ? holder.getName() : null;
		} catch (Exception ex) {
			return null;
		}
	}

	public String getRace(UUID player) {
		if (gOtherPlugin == null)
			return null;
		try {
			AbstractTraitHolder holder = gOtherPlugin.getRaceManager().getHolderOfPlayer(player);
			return holder != null ? holder.getName() : null;
		} catch (Exception ex) {
			return null;
		}
	}
}
