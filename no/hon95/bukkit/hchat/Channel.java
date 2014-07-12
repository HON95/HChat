package no.hon95.bukkit.hchat;

import java.util.ArrayList;
import java.util.List;


public class Channel implements Cloneable {

	private String gId = null;
	private String gName = null;
	private String gOwner = null;
	private String gPassword = null;
	private String gChatFormat = null;
	private boolean gIsPrivate = false;
	private boolean gIsCensored = false;
	private boolean gAllowColorCodes = false;
	private boolean gIsUniversal = true;
	private List<String> gMonitorChannels = null;
	private List<String> gMembers = null;
	private List<String> gBannedMembers = null;

	public Channel(String id, String owner, boolean isPrivate) {
		this(id, id, owner, null, null, isPrivate, false, true, true);
	}

	public Channel(String id, String name, String owner, String password, String chatFormat, boolean isPrivate, boolean isCensored, boolean allowColorCodes, boolean isUniversal) {
		this(id, name, owner, password, chatFormat, isPrivate, isCensored, allowColorCodes, isUniversal, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
	}

	public Channel(String id, String name, String owner, String password, String chatFormat, boolean isPrivate, boolean isCensored, boolean allowColorCodes, boolean isUniversal,
			List<String> monitorChannels, List<String> members, List<String> bannedMembers) {
		gId = id;
		gName = name;
		gOwner = owner;
		gPassword = password;
		gChatFormat = chatFormat;
		gIsPrivate = isPrivate;
		gIsCensored = isCensored;
		gAllowColorCodes = allowColorCodes;
		gIsUniversal = isUniversal;
		gMonitorChannels = monitorChannels;
		gMembers = members;
		gBannedMembers = bannedMembers;
	}

	@Override
	public Channel clone() {
		return new Channel(gId, gName, gOwner, gPassword, gChatFormat, gIsPrivate, gIsCensored, gAllowColorCodes, gIsUniversal,
				new ArrayList<String>(gMonitorChannels), new ArrayList<String>(gMembers), new ArrayList<String>(gBannedMembers));
	}

	public String getId() {
		return gId;
	}

	public String getName() {
		return gName;
	}

	public String getOwner() {
		return gOwner;
	}

	public String getPassword() {
		return gPassword;
	}

	public String getChatFormat() {
		return gChatFormat;
	}

	public boolean isPrivate() {
		return gIsPrivate;
	}

	public boolean isCensored() {
		return gIsCensored;
	}

	public boolean allowColorCodes() {
		return gAllowColorCodes;
	}

	public boolean isUniversal() {
		return gIsUniversal;
	}

	public List<String> getMonitorChannels() {
		return gMonitorChannels;
	}

	public List<String> getMembers() {
		return gMembers;
	}

	public List<String> getBannedMembers() {
		return gBannedMembers;
	}

	public void setId(String id) {
		gId = id;
	}

	public void setName(String name) {
		gName = name;
	}

	public void setOwner(String owner) {
		gOwner = owner;
	}

	public void setPassword(String password) {
		gPassword = password;
	}

	public void setChatFormat(String chatFormat) {
		gChatFormat = chatFormat;
	}

	public void setPrivate(boolean isPrivate) {
		gIsPrivate = isPrivate;
	}

	public void setCensored(boolean isCensored) {
		gIsCensored = isCensored;
	}

	public void setAllowColorCodes(boolean allowColorCodes) {
		gAllowColorCodes = allowColorCodes;
	}

	public void setUniversal(boolean isUniversal) {
		gIsUniversal = isUniversal;
	}
}
