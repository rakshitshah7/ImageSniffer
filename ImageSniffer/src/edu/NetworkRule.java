package edu;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Http.ContentType;
import org.jnetpcap.protocol.tcpip.Http.Request;

public class NetworkRule {

	public NetworkRule() {

	}

	public NetworkData HttpPackets(PcapPacket packet) {
		Http http = new Http();
		NetworkData httppacket = null;
		packet.getHeader(http);
		ContentType type = http.contentTypeEnum();

		if (type.name() == "OTHER" && http.fieldValue(Request.Host) != null) {

			String url = "http://" + http.fieldValue(Request.Host) + http.fieldValue(Request.RequestUrl);
			long time = packet.getCaptureHeader().timestampInMillis();
			long fno = packet.getFrameNumber();
			String host = http.fieldValue(Request.Host);
			httppacket = new NetworkData(fno, url, "http", time, host);

			httppacket.DisplayData();
		}

		return httppacket;
	}

}
