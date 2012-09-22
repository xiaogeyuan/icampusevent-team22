package edu.usc.cs587.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class SocketClient extends JFrame implements ActionListener {
	final String host = "128.125.163.165";// "hamedaan.usc.edu";//"128.125.163.165";"128.125.163.180";//
	final int port = 9998;
	final int timeout = 20000;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7641653748434397135L;
	JLabel text, clicked;
	JButton button;
	JPanel panel;
	JTextField textField;
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;

	SocketClient() { // Begin Constructor
		text = new JLabel("Text to send over socket:");
		textField = new JTextField(20);
		button = new JButton("Click Me");
		button.addActionListener(this);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.white);
		getContentPane().add(panel);
		panel.add("North", text);
		panel.add("Center", textField);
		panel.add("South", button);
	} // End Constructor

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == button) {
			// Send data over socket
			String text = textField.getText();
			// out.print(text + "\r\n");
			out.println(text);
			textField.setText(new String(""));
			// Receive text from server
			try {
				String line = in.readLine();
				System.out.println("Text received :" + line);
			} catch (IOException e) {
				System.out.println("Read failed");
				System.exit(1);
			}
		}
	}

	public void listenSocket() {
		// Create socket connection
		try {

			InetAddress inetAddr = InetAddress.getByName(host);
			SocketAddress socAddr = new InetSocketAddress(inetAddr, port);
			socket = new Socket();
			socket.connect(socAddr, timeout);

			// socket = new Socket(host, 4444);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + host);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("No I/O");
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		SocketClient frame = new SocketClient();
		frame.setTitle("Client Program");
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};

		frame.addWindowListener(l);
		frame.pack();
		frame.setVisible(true);
		frame.listenSocket();
	}
}
