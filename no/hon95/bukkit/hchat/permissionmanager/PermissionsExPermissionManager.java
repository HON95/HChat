package no.hon95.bukkit.hchat.permissionmanager;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;


public final class PermissionsExPermissionManager implements PermissionManager {

	@Override
	public String getTopGroup(String player, String world) {
		PermissionGroup[] groups = PermissionsEx.getUser(player).getGroups();
		PermissionGroup topGroup = null;
		for (PermissionGroup group : groups) {
			if (topGroup == null || group.isChildOf(topGroup))
				topGroup = group;
		}
		if (topGroup != null)
			return topGroup.getName();
		else
			return null;
	}

	@Override
	public String getPrefix(String player, String world) {
		return PermissionsEx.getUser(player).getPrefix(world);
	}

	@Override
	public String getSuffix(String player, String world) {
		return PermissionsEx.getUser(player).getSuffix(world);
	}
}
