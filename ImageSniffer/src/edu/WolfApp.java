package edu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FileChooserUI;

/**
 * A Test application for the Wolfram Celular Autonomon application
 * 
 * @author MMUNSON
 *
 */
public class WolfApp extends CAApp {

	private static Logger log = Logger.getLogger(WolfApp.class.getName());
	String sep = File.separator;
	String logPath = "logs" + sep + "server.log";
	Handler handler;
	protected JPanel northPanel = null;
	protected JButton startBtn = null;
	protected JButton stopBtn = null;
	protected JButton pauseBtn = null;
	protected JButton resumeBtn = null;
	//protected JButton resetBtn = null;
	protected JButton openFileBtn = null;
	protected JButton infoBtn = null;
	protected JButton exportDataBtn = null;
	private CACanvas caPanel = null;
	protected JPanel bottomPanel = null;
	private JTextArea display = new JTextArea(15, 150);
	private JRadioButton radLive = null;
	private JRadioButton radFile = null;
	private JFileChooser chooser = new JFileChooser();
	private String path = null;
	private JLabel label = null;
	private JLabel labellive = null;
	private ReadNetworkData rnd = new ReadNetworkData();

	private Task task;

	/**
	 * Sample app constructor
	 */
	public WolfApp() {
//		File basedir = new File("logs");
//		if (!basedir.exists()) {
//			basedir.mkdirs();
//		}
//		try{
//			handler = new FileHandler(logPath);
//			Logger.getLogger("").addHandler(handler);
//			SimpleFormatter formatter = new SimpleFormatter();
//			handler.setFormatter(formatter);
//		} catch (SecurityException | IOException e) {
//			log.severe(e.getMessage());
//			e.printStackTrace();
//		}
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setTitle("Image Sniffer");
		frame.setLayout(new BorderLayout());
		frame.add(getNorthPanel(), BorderLayout.NORTH);
		frame.add(getBottomPanel(), BorderLayout.SOUTH);
		frame.setVisible(true); // The UI is built, so display it
	}

	public JPanel getNorthPanel() {
		northPanel = new JPanel();
		northPanel.setPreferredSize(new Dimension(200, 100));
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		ButtonGroup group = new ButtonGroup();

		radLive = new JRadioButton("Live");
		group.add(radLive);
		northPanel.add(radLive);

		radFile = new JRadioButton("File");
		group.add(radFile);
		northPanel.add(radFile);

//		openFileBtn = new JButton("Open File");
//		openFileBtn.addActionListener(this); // Allow the app to hear about
//												// button pushes
//		northPanel.add(openFileBtn);

		label = new JLabel("", JLabel.CENTER);
		label.setText("File: No File Selected\n");
		northPanel.add(label);
		labellive = new JLabel("", JLabel.CENTER);
		labellive.setText("Interface: No Interface Selected\n");
		northPanel.add(labellive);
		return northPanel;
	}

	public JPanel getBottomPanel() {
		bottomPanel = new JPanel();
		bottomPanel.setPreferredSize(new Dimension(300, 300));
		startBtn = new JButton("Start");
		startBtn.addActionListener(this); // Allow the app to hear about button
											// pushes
		bottomPanel.add(startBtn);

		pauseBtn = new JButton("Pause");
		pauseBtn.addActionListener(this); // Allow the app to hear about button
											// pushes
		bottomPanel.add(pauseBtn);

		resumeBtn = new JButton("Resume");
		resumeBtn.addActionListener(this); // Allow the app to hear about button
											// pushes
		bottomPanel.add(resumeBtn);

		stopBtn = new JButton("Stop"); // Allow the app to hear about button
										// pushes
		stopBtn.addActionListener(this);
		bottomPanel.add(stopBtn);

//		resetBtn = new JButton("Reset"); // Allow the app to hear about button
//											// pushes
//		resetBtn.addActionListener(this);
//		bottomPanel.add(resetBtn);

		exportDataBtn = new JButton("Export To Text File"); // Allow the app to hear
														// about button
		// pushes
		exportDataBtn.addActionListener(this);
		bottomPanel.add(exportDataBtn);
		
		infoBtn = new JButton("Project Information"); // Allow the app to hear
		// about button
		infoBtn.addActionListener(this);
		bottomPanel.add(infoBtn);
		
		display.setEditable(false);
		JScrollPane scroll1 = new JScrollPane(display);
		scroll1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		// scroll = new JScrollPane(centerPanel);
		bottomPanel.add(scroll1);
		
		startBtn.setEnabled(true);
		pauseBtn.setEnabled(false);
		resumeBtn.setEnabled(false);
		stopBtn.setEnabled(false);
		exportDataBtn.setEnabled(false);
		//resetBtn.setEnabled(false);

		return bottomPanel;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == openFileBtn) {
			openFileBtnEvent();
		} else if (ae.getSource() == startBtn) {
			startBtnEvent();
		} else if (ae.getSource() == stopBtn) {
			stopBtnEvent();
		} else if (ae.getSource() == pauseBtn) {
			pauseBtnEvent();
		} else if (ae.getSource() == resumeBtn) {
			resumeBtnEvent();
		} else if (ae.getSource() == infoBtn) {
			infoBtnEvent();
		} else if (ae.getSource() == exportDataBtn) {
			exportDataBtnEvent();

		}
	}

	public void openFileBtnEvent()
	{
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("choosertitle");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Network Files", "pcap");
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setAcceptAllFileFilterUsed(false);

		int returnVal = chooser.showOpenDialog(frame);
		 
	      if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	  path = chooser.getSelectedFile().getAbsolutePath();
			label.setText("File: " + path);
			Border border = BorderFactory.createLineBorder(Color.RED, 5);
			label.setBorder(border);
			labellive.setBorder(new EmptyBorder(10, 10, 10, 10));
	      }
		
	}
	public void startBtnEvent()
	{
		System.out.println("Start pressed");
		if (radFile.isSelected() || radLive.isSelected()) {
				pauseBtn.setEnabled(true);
				stopBtn.setEnabled(true);
				resumeBtn.setEnabled(false);
				startBtn.setEnabled(false);
				task = new Task();
				task.execute();
			
		} else {
			JOptionPane.showMessageDialog(frame, "Please Select Live or File");

		}
	}
	public void stopBtnEvent(){
		task.cancel(true);
		System.out.println("Stop pressed");
	}
	public void pauseBtnEvent()
	{
		task.pause();
		pauseBtn.setEnabled(false);
		stopBtn.setEnabled(false);
		resumeBtn.setEnabled(true);
		startBtn.setEnabled(false);
		exportDataBtn.setEnabled(true);
		//resetBtn.setEnabled(true);
		System.out.println("Pause pressed");
	}
	public void resumeBtnEvent()
	{
		task.resume();
		pauseBtn.setEnabled(true);
		stopBtn.setEnabled(true);
		resumeBtn.setEnabled(false);
		startBtn.setEnabled(false);
		exportDataBtn.setEnabled(false);
		//resetBtn.setEnabled(false);
		System.out.println("Resume pressed");
	}
	public void resetBtnEvent()
	{
		
		
	}
	public void infoBtnEvent()
	{
		JOptionPane.showMessageDialog(frame,
				"****** Project : Image Sniffer ******\n Made by Rakshit Shah \n \nThis project identifies the images surfed by network and file.\n To use this project, use following steps : \n 1) Choose Live or File mode. \n 2) Press Start \n 3) Press Pause Resume Stop accordingly. ");
	}
	public void exportDataBtnEvent()
	{
		FileWriter fwriter;
		try {
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
			fc.setFileFilter(filter);
			int rVal =fc.showSaveDialog(frame);
			 if (rVal == JFileChooser.APPROVE_OPTION) {
		        String pathsave = fc.getSelectedFile().getAbsolutePath();
		       
		        if(!pathsave.substring(pathsave.length()-4).equalsIgnoreCase(".txt"))
		        	pathsave = pathsave+".txt";
		      
			fwriter = new FileWriter(pathsave);
			fwriter.write("File Feed");
			ArrayList<NetworkData> fileList = rnd.getFilePackets();
			for (int i = 0; i < fileList.size(); i++) {
				fwriter.write(fileList.get(i).ReturnStringData());
				fwriter.write(System.getProperty("line.separator"));
			}
			fwriter.write(System.getProperty("line.separator"));
			fwriter.write("Live Feed");
			ArrayList<NetworkData> liveList = rnd.getLivePackets();
			for (int i = 0; i < liveList.size(); i++) {
				fwriter.write(liveList.get(i).ReturnStringData());
				fwriter.write(System.getProperty("line.separator"));
			}

			System.out.println("All objects added successfully");
			fwriter.flush();
			fwriter.close();
			
			log.info("All objects added to File successfully");
			JOptionPane.showMessageDialog(frame,
					"File Saved");
		} else if (rVal == JFileChooser.CANCEL_OPTION) {
			JOptionPane.showMessageDialog(frame,
					"File Not Saved");
	      }
		}
		 catch (FileNotFoundException fex) {

			log.severe("FILE NOT FOUND!-exportdata");
			System.err.println("IO error recieved at exportdata" + fex.getMessage());
			fex.printStackTrace();
		} catch (IllegalArgumentException | SecurityException | IOException ex) {

			log.severe("IOException!-exportdata");
			System.err.println("Error recieved at exportdata" + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public void windowOpened(WindowEvent e) {
		log.info("Window opened");
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
		log.info("Window iconified");
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		log.info("Window deiconified");
	}

	@Override
	public void windowActivated(WindowEvent e) {
		log.info("Window activated");
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		log.info("Window deactivated");
	}

	/**
	 * Sample Wolf application starting point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				WolfApp wapp = new WolfApp();
			}
		});
		log.info("WolfApp started");
	}
	
	class Task extends SwingWorker<Void, Void> {
		/**
		 * Main task. Executed in background thread.
		 * 
		 * @return
		 * @throws IOException
		 */

		@Override
		public Void doInBackground() throws IOException {

			if (radFile.isSelected()) {
				fileEvent();
			} else if (radLive.isSelected()) {
			liveEvent();
			}
			return null;
		}
		public void liveEvent()
		{
			Object[] possibilities = rnd.getDevices();
			String networkInterface = (String) JOptionPane.showInputDialog(frame, "Choose Interface\n",
					"Image Sniffer: Capture an Interface", JOptionPane.PLAIN_MESSAGE, null, possibilities, "");

			// If a string was returned, say so.
			if ((networkInterface != null) && (networkInterface.length() > 0)) {
				labellive.setText("Interface: " + networkInterface);
				 Border border = BorderFactory.createLineBorder(Color.RED, 5);
				 labellive.setBorder(border);
				 label.setBorder(new EmptyBorder(10, 10, 10, 10));
				 //String networkInterfacename = networkInterface.split("*")[1].trim();
				rnd.ReadLive(frame, display, task, networkInterface);
			} else {
				JOptionPane.showMessageDialog(frame, "No Interface Available or Selected");
			}

		}
		
		public void fileEvent()
		{
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Image Sniffer: Open Capture File");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Network Files", "pcap");
			chooser.setFileFilter(filter);
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setAcceptAllFileFilterUsed(false);

			int returnVal = chooser.showOpenDialog(frame);
			 
		      if (returnVal == JFileChooser.APPROVE_OPTION) {
		    	  path = chooser.getSelectedFile().getAbsolutePath();
				label.setText("File: " + path);
				Border border = BorderFactory.createLineBorder(Color.RED, 5);
				label.setBorder(border);
				labellive.setBorder(new EmptyBorder(10, 10, 10, 10));
				rnd.ReadFromFile(frame, display, task, path);
		      }else {
					JOptionPane.showMessageDialog(frame, "No File Available or Selected");
				}
		}
		
		/**
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			startBtn.setEnabled(true);
			pauseBtn.setEnabled(false);
			resumeBtn.setEnabled(false);
			stopBtn.setEnabled(false);
			exportDataBtn.setEnabled(true);
			//resetBtn.setEnabled(true);
			labellive.setBorder(new EmptyBorder(10, 10, 10, 10));
			label.setBorder(new EmptyBorder(10, 10, 10, 10));
		}

		private volatile boolean isPaused;

		public final void pause() {
			if (!isPaused() && !isDone()) {
				isPaused = true;
				firePropertyChange("paused", false, true);
			}
		}

		public final void resume() {
			if (isPaused() && !isDone()) {
				isPaused = false;
				firePropertyChange("paused", true, false);
			}
		}

		public final boolean isPaused() {
			return isPaused;
		}

		public void dowork() {
			while (isPaused) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					log.severe(e.getMessage());
				}
				// System.out.println("hello");
			}
		}

	}
}
