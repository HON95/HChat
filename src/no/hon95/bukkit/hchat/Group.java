package no.hon95.bukkit.hchat;

import java.util.List;
import java.util.Map;

import no.hon95.bukkit.hchat.common.util.StringKeyHashMap;

public class Group {

	private String gId = null;
	private String gName = null;
	private String gPrefix = null;
	private String gSuffix = null;
	private String gNameFormat = null;
	private String gListFormat = null;
	private String gChatFormat = null;
	private String gDeathFormat = null;
	private String gJoinFormat = null;
	private String gQuitFormat = null;
	private String gChannelJoinFormat = null;
	private String gChannelQuitFormat = null;
	private String gAwayStartFormat = null;
	private String gAwayStopFormat = null;
	private String gAwayKickPlayerMessageFormat = null;
	private String gAwayKickServerMessageFormat = null;
	private String gMeFormat = null;
	private String gTellSenderFormat = null;
	private String gTellReceiverFormat = null;
	private String gTellSpyFormat = null;
	private List<String> gMotdFormat = null;
	private boolean gCensor = false;
	private boolean gColorCodes = true;
	private boolean gCanChat = true;
	private boolean gShowPersonalMessages = true;
	private int gAwayThreshold = -1;
	private String gAwayLongTag = null;
	private String gAwayShortTag = null;
	private boolean gKickOnAway = false;
	private String gDefaultChannel = null;
	private StringKeyHashMap<String> gDefautlWorldChannels = null;

	public String getId() {
		return gId;
	}

	public String getName() {
		return gName;
	}

	public String getPrefix() {
		return gPrefix;
	}

	public String getSuffix() {
		return gSuffix;
	}

	public String getNameFormat() {
		return gNameFormat;
	}

	public String getListFormat() {
		return gListFormat;
	}

	public String getChatFormat() {
		return gChatFormat;
	}

	public String getDeathFormat() {
		return gDeathFormat;
	}

	public String getJoinFormat() {
		return gJoinFormat;
	}

	public String getQuitFormat() {
		return gQuitFormat;
	}

	public String getChannelJoinFormat() {
		return gChannelJoinFormat;
	}

	public String getChannelQuitFormat() {
		return gChannelQuitFormat;
	}

	public String getAwayStartFormat() {
		return gAwayStartFormat;
	}

	public String getAwayStopFormat() {
		return gAwayStopFormat;
	}

	public String getAwayKickPlayerMessageFormat() {
		return gAwayKickPlayerMessageFormat;
	}

	public String getAwayKickServerMessageFormat() {
		return gAwayKickServerMessageFormat;
	}

	public String getMeFormat() {
		return gMeFormat;
	}

	public String getTellSenderFormat() {
		return gTellSenderFormat;
	}

	public String getTellReceiverFormat() {
		return gTellReceiverFormat;
	}

	public String getTellSpyFormat() {
		return gTellSpyFormat;
	}

	public List<String> getMotdFormat() {
		return gMotdFormat;
	}

	public boolean getCensor() {
		return gCensor;
	}

	public boolean getAllowColorCodes() {
		return gColorCodes;
	}

	public boolean getCanChat() {
		return gCanChat;
	}

	public boolean getShowPersonalMessages() {
		return gShowPersonalMessages;
	}

	public String getAwayLongTag() {
		return gAwayLongTag;
	}

	public String getAwayShortTag() {
		return gAwayShortTag;
	}

	public int getAwayThreshold() {
		return gAwayThreshold;
	}

	public boolean getKickOnAway() {
		return gKickOnAway;
	}

	public String getDefaultChannel() {
		return gDefaultChannel;
	}

	public StringKeyHashMap<String> getDefaultWorldChannels() {
		return gDefautlWorldChannels;
	}

	public void setId(String id) {
		gId = id;
	}

	public void setName(String name) {
		gName = name;
	}

	public void setPrefix(String prefix) {
		gPrefix = prefix;
	}

	public void setSuffix(String suffix) {
		gSuffix = suffix;
	}

	public void setNameFormat(String format) {
		gNameFormat = format;
	}

	public void setListFormat(String format) {
		gListFormat = format;
	}

	public void setChatFormat(String format) {
		gChatFormat = format;
	}

	public void setDeathFormat(String format) {
		gDeathFormat = format;
	}

	public void setJoinFormat(String format) {
		gJoinFormat = format;
	}

	public void setQuitFormat(String format) {
		gQuitFormat = format;
	}

	public void setChannelJoinFormat(String format) {
		gChannelJoinFormat = format;
	}

	public void setChannelQuitFormat(String format) {
		gChannelQuitFormat = format;
	}

	public void setAwayStartFormat(String format) {
		gAwayStartFormat = format;
	}

	public void setAwayStopFormat(String format) {
		gAwayStopFormat = format;
	}

	public void setAwayKickPlayerMessageFormat(String format) {
		gAwayKickPlayerMessageFormat = format;
	}

	public void setAwayKickServerMessageFormat(String format) {
		gAwayKickServerMessageFormat = format;
	}

	public void setMeFormat(String format) {
		gMeFormat = format;
	}

	public void setTellSenderFormat(String format) {
		gTellSenderFormat = format;
	}

	public void setTellReceiverFormat(String format) {
		gTellReceiverFormat = format;
	}

	public void setTellSpyFormat(String format) {
		gTellSpyFormat = format;
	}

	public void setMotdFormat(List<String> format) {
		gMotdFormat = format;
	}

	public void setCensor(boolean censor) {
		gCensor = censor;
	}

	public void setAllowColorCodes(boolean colorCodes) {
		gColorCodes = colorCodes;
	}

	public void setCanChat(boolean canChat) {
		gCanChat = canChat;
	}

	public void setShowPersonalMessages(boolean showPMs) {
		gShowPersonalMessages = showPMs;
	}

	public void setAwayLongTag(String tag) {
		gAwayLongTag = tag;
	}

	public void setAwayShortTag(String tag) {
		gAwayShortTag = tag;
	}

	public void setAwayThreshold(int threshold) {
		gAwayThreshold = threshold;
	}

	public void setKickOnAway(boolean kick) {
		gKickOnAway = kick;
	}

	public void setDefaultChannel(String defaultChannel) {
		gDefaultChannel = defaultChannel;
	}

	public void setDefaultWorldChannels(Map<String, String> defaultWorldChannels) {
		gDefautlWorldChannels = new StringKeyHashMap<String>(defaultWorldChannels);
	}
}
