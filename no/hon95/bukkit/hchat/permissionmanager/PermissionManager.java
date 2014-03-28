package no.hon95.bukkit.hchat.permissionmanager;

public interface PermissionManager {

	public String getTopGroup(String player, String world);

	public String getPrefix(String player, String world);

	public String getSuffix(String player, String world);
}
