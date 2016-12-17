package edu;
import org.jnetpcap.protocol.tcpip.Http.Request;

public class NetworkData {
	private long frameNumber;
	private String url;
	private String protocol;
	private long timeStamp;
	private String host;

	public NetworkData(long fno, String url, String protocol, long time, String host) {
		this.frameNumber = fno;
		this.url = url;
		this.protocol = protocol;
		this.timeStamp = time;
		this.host = host;
	}

	public void DisplayData() {
		System.out.println("\n |FRAME-" + frameNumber + " |Protocol-" + protocol + " |Received packet at-" + timeStamp
				+ " |HOST-" + host + " |URL-" + url + "\n");
	}

	public String ReturnStringData() {
		String data = "\n |FRAME-" + frameNumber + " |Protocol-" + protocol + " |Received packet at-" + timeStamp
				+ " |HOST-" + host + " |URL-" + url + "\n";
		return data;
	}
	
	public String getUrl()
	{
		return url;
	}
}
