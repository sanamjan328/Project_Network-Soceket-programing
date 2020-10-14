/**Author : sanamjan kurban
 * Project Name : (Echo UDP packet)
 */

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;

public class Server extends JFrame 
{
	private JTextArea su=new JTextArea();
	int j=1;
	public static void main(String[] args) 
	{new Server(); }
	
	public Server()
	{
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(su),BorderLayout.CENTER);
		setTitle("Server");
		setSize(500,400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	
	    try{
		
			//create a server socket
			DatagramSocket socket=new DatagramSocket(8000);
			su.append("Server started at " + new Date() +"\n");
		        su.append("Start listening for incoming packet....  "+"\n");
			
			//create second socket
			DatagramSocket socket_2=new DatagramSocket();
			int port=15;
			
			 //create first buffer
		          byte buf_1[]=new byte[1500];
			
			//create a packet for receiving data
		       DatagramPacket recievePacket=new DatagramPacket(buf_1,buf_1.length);
		      
	      while(true){
		//Initialize buffer for each iteration
		Arrays.fill(buf_1,(byte)0);
		    
                //get client information
                socket.receive(recievePacket);
                System.out.println("\n");
                System.out.println("Start receiving packet ....\n");
                su.append("Receive packet #" +j+ ".....\n");
                su.append("\nReceived from: " +recievePacket.getAddress().getHostAddress()+ 
                "\nFrom port number: " +recievePacket.getPort()+
                "\nData size: " +recievePacket.getData().length+
                "\nID: " +buf_1[0]+ 
                "\nwith Data payload: ");
    	
		    //print and unsigned value which is in payload
		    for (int i=1; i < buf_1.length-1; i++)
		    {
		    	String value = String.valueOf(unsignedByteToInt(buf_1[i]));
		    	int b=unsignedByteToInt(buf_1[i]);
                        su.append(String.valueOf(b));
                     }//end for 
            
                    su.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		    
                     InetAddress add = recievePacket.getAddress();
                     //criate second buffer
                     byte buf_2[] = new byte[1500];

                     //create a packet sending data
                     DatagramPacket sendPacket=new DatagramPacket(buf_2,buf_2.length,add,port);

                     //send to client in packet 
                     sendPacket.setAddress(recievePacket.getAddress());
                     sendPacket.setData(recievePacket.getData());
                     socket_2.send(sendPacket);
                     j++;
		 }//end while
            }//end try
    	 catch (IOException ex){ 
    	 ex.printStackTrace();
    	 }//end catch
    }//end public server
    
    public static int unsignedByteToInt(byte c) {
    return (int) c & 0xFF;
    }

  }//end whole programming
