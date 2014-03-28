package no.hon95.bukkit.hchat.permissionmanager;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.CalculableType;


public final class BPermissionsPermissionManager implements PermissionManager {

	@Override
	public String getTopGroup(String player, String world) {
		String[] groups = ApiLayer.getGroups(world, CalculableType.USER, player);
		String topGroup = null;
		for (String group : groups) {
			if (topGroup == null)
				topGroup = group;
			String[] groups2 = ApiLayer.getGroups(world, CalculableType.GROUP, group);
			for (String group2 : groups2) {
				if (group2.equalsIgnoreCase(topGroup)) {
					topGroup = group;
					break;
				}
			}
		}
		if (topGroup != null)
			return topGroup;
		else
			return null;
	}

	@Override
	public String getPrefix(String player, String world) {
		return ApiLayer.getValue(world, CalculableType.USER, player, "prefix");
	}

	@Override
	public String getSuffix(String player, String world) {
		return ApiLayer.getValue(world, CalculableType.USER, player, "suffix");
	}

}
