package edu;
import java.awt.BorderLayout;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Http;

import edu.WolfApp.Task;

public class ReadNetworkData {

	private String FILENAME = "C:/Users/smart/workspace/test/tests/capture.pcap";
	private StringBuilder errbuf = new StringBuilder();
	private NetworkRule rule = new NetworkRule();
	private JPanel centerPanel = new JPanel();
	private String[] devicesList = null;
	// public static void main(String[] args) {
	private ArrayList<NetworkData> allPacketsFile = new ArrayList<NetworkData>();
	private ArrayList<NetworkData> allPacketsLive = new ArrayList<NetworkData>();
	// ReadNetworkData rnd = new ReadNetworkData();
	// packetsFile = rnd.ReadFromFile();
	// System.out.println("Live Data - Search Images\n");
	// packetsLive = rnd.ReadLive();
	//
	// }
	public String[] getDevices()
	{	
		List<PcapIf> alldevs = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder(); 
		int r = Pcap.findAllDevs(alldevs, errbuf);
		devicesList = new String[alldevs.size()];
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			devicesList[0] ="Can't read list of devices, error is %s";

		}
		else {
		for(int i=0;i<alldevs.size();i++)
		{
			devicesList[i] = alldevs.get(i).getDescription() + " | " + alldevs.get(i).getName();
		}
		}
		
		return devicesList;
	}
	
	public ArrayList<NetworkData> ReadFromFile(JFrame frame, JTextArea display,Task task,String fileName) {
		ArrayList<NetworkData> packetsfile = new ArrayList<NetworkData>();
		if(fileName != null)
		{
			FILENAME = fileName;
		}
                try{
                    final Pcap pcap = Pcap.openOffline(FILENAME, errbuf);
		
		if (pcap == null) {
			System.err.println(errbuf); // Error is stored in errbuf if any

		}
		PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

			public void nextPacket(PcapPacket packet, String user) {

				if (packet.hasHeader(Http.ID)) {
					NetworkData httppacket = rule.HttpPackets(packet);
					if (httppacket != null && httppacket.getUrl().substring(httppacket.getUrl().length() - 3).equalsIgnoreCase("jpg")) {
						display.append(httppacket.ReturnStringData());
						try {
							centerPanel = updateDisplay(httppacket.getUrl());
							frame.add(centerPanel,BorderLayout.CENTER);
							frame.setVisible(true);
							Thread.sleep(1000);
							if(task.isCancelled())
							{
								pcap.breakloop();
							}
							if(task.isPaused())
							{
								task.dowork();
							}
						} catch (InterruptedException | MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						allPacketsFile.add(httppacket);
					}
				}
			}
		};
		
		pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, "");

		pcap.close();
		return packetsfile;
}
                catch(Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
	}

	public ArrayList<NetworkData> ReadLive(JFrame frame, JTextArea display,Task task,String interfaceSelected) {
		ArrayList<NetworkData> packetslive = new ArrayList<NetworkData>();

		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with
														// NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs

		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());

		}
		int devicesel=0;
		for(devicesel = 0;devicesel<alldevs.size();devicesel++)
		{
			if(interfaceSelected.equals(alldevs.get(devicesel).getDescription()+" | "+ alldevs.get(devicesel).getName()))
					break;
		}
		
		PcapIf device = alldevs.get(devicesel); // We know we have atleast 1 device

		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 1 * 1000; // 10 seconds in millis
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: " + errbuf.toString());

		}
		PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

			public void nextPacket(PcapPacket packet, String user) {

				if (packet.hasHeader(Http.ID)) {
					NetworkData httppacket = rule.HttpPackets(packet);
					if (httppacket != null && httppacket.getUrl().substring(httppacket.getUrl().length() - 3).equalsIgnoreCase("jpg")) {
						try {
							display.append(httppacket.ReturnStringData());

							centerPanel = updateDisplay(httppacket.getUrl());
							frame.add(centerPanel,BorderLayout.CENTER);
							frame.setVisible(true);
							Thread.sleep(1000);
							if(task.isCancelled())
							{
								pcap.breakloop();
							}
							if(task.isPaused())
							{
								task.dowork();
							}
						} catch (InterruptedException | MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						allPacketsLive.add(httppacket);
					}
				}
			}
		};

		pcap.loop(pcap.LOOP_INFINATE, jpacketHandler, "");

		pcap.close();
		return packetslive;
	}

	public JPanel updateDisplay(String urlfrompackets) throws MalformedURLException {
		URL url = new URL(urlfrompackets);
		System.setProperty("http.agent", "Chrome");
		ImageIcon imageicon = new ImageIcon(url);
		Image image = imageicon.getImage(); // transform it
		Image newimg = image.getScaledInstance(900, 700, java.awt.Image.SCALE_SMOOTH); // scale
		imageicon = new ImageIcon(newimg); // transform it back
		JLabel label = new JLabel("", imageicon, JLabel.CENTER);
		JPanel centerPanelLocal = new JPanel();
		centerPanelLocal.add(label, BorderLayout.CENTER);
		return centerPanelLocal;
	}

	public ArrayList<NetworkData> getLivePackets()
	{
		return allPacketsLive;
	}
	public ArrayList<NetworkData> getFilePackets()
	{
		return allPacketsFile;
	}
}
