package edu.usc.cs587.examples;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.net.*;

class SocketServer extends JFrame
		implements ActionListener {

   JButton button;
   JLabel label = new JLabel("Text received over socket:");
   JPanel panel;
   JTextArea textArea = new JTextArea();
   ServerSocket server = null;
   Socket client = null;
   BufferedReader in = null;
   PrintWriter out = null;
   String line;
   final int port = 9998;

   SocketServer(){ //Begin Constructor
     button = new JButton("Click Me");
     button.addActionListener(this);

     panel = new JPanel();
     panel.setLayout(new BorderLayout());
     panel.setBackground(Color.white);
     getContentPane().add(panel);
     panel.add("North", label);
     panel.add("Center", textArea);
     panel.add("South", button);

   } //End Constructor

  public void actionPerformed(ActionEvent event) {
     Object source = event.getSource();

     if(source == button){
         textArea.setText(line);
     }
  }

  public void listenSocket(){

	  System.out.println("Starting server...");
    try{
      server = new ServerSocket(port); 
      System.out.println("Server is created on port="+port);
    } catch (IOException e) {
      System.out.println("Could not listen on port="+port);
      System.exit(-1);
    }

    try{
      client = server.accept();
      System.out.println("accepted a incoming connection"+client.getPort());
    } catch (IOException e) {
      System.out.println("Accept failed: 4444");
      System.exit(-1);
    }

    try{
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      out = new PrintWriter(client.getOutputStream(), true);
    } catch (IOException e) {
      System.out.println("Accept failed: 4444");
      System.exit(-1);
    }
 
    while(true){
      try{
        line = in.readLine();
        System.out.println("server receives from client="+line);
//Send data back to client
        
        String os = System.getProperty("os.name");
        System.out.println("os.name="+os);
        if (!os.startsWith("Win")) {
        	System.setProperty("line.separator", "/r/n");
        }
        out.println(line);
      } catch (IOException e) {
        System.out.println("Read failed");
        System.exit(-1);
      }
    }
  }

  protected void finalize(){
//Clean up 
     try{
        in.close();
        out.close();
        server.close();
    } catch (IOException e) {
        System.out.println("Could not close.");
        System.exit(-1);
    }
  }

  public static void main(String[] args){
        SocketServer frame = new SocketServer();
        frame.setTitle("Server Program");
        WindowListener l = new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                        System.exit(0);
                }
        };
        frame.addWindowListener(l);
        frame.pack();
//        frame.setVisible(true);
        frame.listenSocket();
  }
}
