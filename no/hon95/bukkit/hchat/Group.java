package no.hon95.bukkit.hchat;

import java.util.List;
import java.util.Map;


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
	private String gMeFormat = null;
	private String gTellSenderFormat = null;
	private String gTellReceiverFormat = null;
	private String gTellSpyFormat = null;
	private List<String> gMotdFormat = null;
	private boolean gCensor = false;
	private boolean gColorCodes = true;
	private boolean gCanChat = true;
	private boolean gShowPersonalMessages = true;
	private String gDefaultChannel = null;
	private Map<String, String> gDefautlWorldChannels = null;

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

	public boolean isCensored() {
		return gCensor;
	}

	public boolean allowColorCodes() {
		return gColorCodes;
	}

	public boolean canChat() {
		return gCanChat;
	}

	public boolean showPersonalMessages() {
		return gShowPersonalMessages;
	}

	public String getDefaultChannel() {
		return gDefaultChannel;
	}

	public Map<String, String> getDefaultWorldChannels() {
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

	public void setNameFormat(String nameFormat) {
		gNameFormat = nameFormat;
	}

	public void setListFormat(String listFormat) {
		gListFormat = listFormat;
	}

	public void setChatFormat(String chatFormat) {
		gChatFormat = chatFormat;
	}

	public void setDeathFormat(String deathFormat) {
		gDeathFormat = deathFormat;
	}

	public void setJoinFormat(String joinFormat) {
		gJoinFormat = joinFormat;
	}

	public void setQuitFormat(String quitFormat) {
		gQuitFormat = quitFormat;
	}

	public void setChannelJoinFormat(String channelJoinFormat) {
		gChannelJoinFormat = channelJoinFormat;
	}

	public void setChannelQuitFormat(String channelQuitFormat) {
		gChannelQuitFormat = channelQuitFormat;
	}

	public void setMeFormat(String meFormat) {
		gMeFormat = meFormat;
	}

	public void setTellSenderFormat(String tellSenderFormat) {
		gTellSenderFormat = tellSenderFormat;
	}

	public void setTellReceiverFormat(String rellReceiverFormat) {
		gTellReceiverFormat = rellReceiverFormat;
	}

	public void setTellSpyFormat(String tellSpyFormat) {
		gTellSpyFormat = tellSpyFormat;
	}

	public void setMotdFormat(List<String> motdFormat) {
		gMotdFormat = motdFormat;
	}

	public void setCensor(boolean censor) {
		gCensor = censor;
	}

	public void setColorCodes(boolean colorCodes) {
		gColorCodes = colorCodes;
	}

	public void setCanChat(boolean canChat) {
		gCanChat = canChat;
	}

	public void setShowPersonalMessages(boolean showPMs) {
		gShowPersonalMessages = showPMs;
	}

	public void setDefaultChannel(String defaultChannel) {
		gDefaultChannel = defaultChannel;
	}

	public void setDefaultWorldChannels(Map<String, String> defaultWorldChannels) {
		gDefautlWorldChannels = defaultWorldChannels;
	}
}
