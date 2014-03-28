package no.hon95.bukkit.hchat.permissionmanager;

import java.util.List;

import org.bukkit.Bukkit;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;


public final class PermissionsBukkitPermissionManager implements PermissionManager {

	@Override
	public String getTopGroup(String player, String world) {
		PermissionsPlugin plugin = (PermissionsPlugin) Bukkit.getPluginManager().getPlugin("PermissionsBukkit");
		List<Group> groups = plugin.getGroups(player);
		Group topGroup = null;
		for (Group group : groups) {
			if (topGroup == null)
				topGroup = group;
			List<Group> groups2 = group.getInfo().getGroups();
			for (Group group2 : groups2) {
				if (group2.getName().equalsIgnoreCase(topGroup.getName())) {
					topGroup = group;
					break;
				}
			}
		}
		if (topGroup != null)
			return topGroup.getName();
		else
			return null;
	}

	@Override
	public String getPrefix(String player, String world) {
		return null;
	}

	@Override
	public String getSuffix(String player, String world) {
		return null;
	}
}
