package dk.frv.enav.esd.gui;

public class NotificationService {

	public String name;
	public int unreadMessages;
	
	public String getName() {
		return this.name;
	}
	
	public int getUnreadMessages() {
		return this.unreadMessages;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setUnreadMessages(int unreadMessages) {
		this.unreadMessages = unreadMessages;
	}
	
}
