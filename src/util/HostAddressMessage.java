package util;

public class HostAddressMessage extends Message {
	private String hostIP;
	public HostAddressMessage(String action) {
		super(action);
	}

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}
}
