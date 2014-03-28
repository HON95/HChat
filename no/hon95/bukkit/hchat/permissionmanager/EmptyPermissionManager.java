package no.hon95.bukkit.hchat.permissionmanager;

public final class EmptyPermissionManager implements PermissionManager {

	@Override
	public String getTopGroup(String player, String world) {
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
