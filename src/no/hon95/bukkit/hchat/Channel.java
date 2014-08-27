package no.hon95.bukkit.hchat;

import java.util.ArrayList;
import java.util.List;

import no.hon95.bukkit.hchat.common.util.StringList;


public class Channel implements Cloneable {

	private String gId;
	private String gName;
	private String gOwner;
	private String gPassword;
	private String gChatFormat;
	private boolean gIsPrivate;
	private boolean gIsCensored;
	private boolean gAllowColorCodes;
	private boolean gIsUniversal;
	private boolean gAutoJoinIfDefault;
	private double gRange;
	private StringList gMonitorChannels;
	private StringList gMemberGroups;
	private StringList gMembers;
	private StringList gBannedMembers;

	public Channel() {
		this(null, null, false);
	}

	public Channel(String id, String owner, boolean isPrivate) {
		this(id, id, owner, null, null, isPrivate, false, true, true, false, -1D);
	}

	public Channel(String id, String name, String owner, String password, String chatFormat, boolean isPrivate, boolean isCensored, boolean allowColorCodes, boolean isUniversal, boolean autoJoinIfDefault, double range) {
		this(id, name, owner, password, chatFormat, isPrivate, isCensored, allowColorCodes, isUniversal, autoJoinIfDefault, range, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
	}

	public Channel(String id, String name, String owner, String password, String chatFormat, boolean isPrivate, boolean isCensored, boolean allowColorCodes, boolean isUniversal, boolean autoJoinIfDefault, double range,
			List<String> monitorChannels, List<String> memberGroups, List<String> members, List<String> bannedMembers) {
		gId = id;
		gName = name;
		gOwner = owner;
		gPassword = password;
		gChatFormat = chatFormat;
		gIsPrivate = isPrivate;
		gIsCensored = isCensored;
		gAllowColorCodes = allowColorCodes;
		gIsUniversal = isUniversal;
		gAutoJoinIfDefault = autoJoinIfDefault;
		gRange = range;
		gMonitorChannels = new StringList(monitorChannels);
		gMemberGroups = new StringList(memberGroups);
		gMembers = new StringList(members);
		gBannedMembers = new StringList(bannedMembers);
	}

	@Override
	public Channel clone() {
		return new Channel(gId, gName, gOwner, gPassword, gChatFormat, gIsPrivate, gIsCensored, gAllowColorCodes, gIsUniversal, gAutoJoinIfDefault, gRange,
				new ArrayList<String>(gMonitorChannels), new ArrayList<String>(gMemberGroups), new ArrayList<String>(gMembers), new ArrayList<String>(gBannedMembers));
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

	public boolean autoJoinIfDefault() {
		return gAutoJoinIfDefault;
	}

	public double getRange() {
		return gRange;
	}

	public StringList getMonitorChannels() {
		return gMonitorChannels;
	}

	public StringList getMemberGroups() {
		return gMemberGroups;
	}

	public StringList getMembers() {
		return gMembers;
	}

	public StringList getBannedMembers() {
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

	public void setAutoJoinIfDefault(boolean autoJoinIfDefault) {
		gAutoJoinIfDefault = autoJoinIfDefault;
	}

	public void setRange(double range) {
		gRange = range;
	}

	public void setMonitorChannels(List<String> list) {
		gMonitorChannels = new StringList(list);
	}

	public void setMemberGroups(List<String> list) {
		gMemberGroups = new StringList(list);
	}

	public void setMembers(List<String> list) {
		gMembers = new StringList(list);
	}

	public void setBannedMembers(List<String> list) {
		gBannedMembers = new StringList(list);
	}
}
