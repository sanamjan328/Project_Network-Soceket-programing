/**Author : sanamjan kurban
 * Project Name : Project 1 (Echo UDP packet and ICMP packet)
 */

//java library
   import java.io.*;
   import java.net.UnknownHostException;
   import javax.swing.*;
   import java.awt.*;
   import javax.swing.border.*;
   import java.awt.event.*;
   import java.util.*;
   import java.lang.*;
   import java.net.DatagramPacket;
   import java.net.DatagramSocket;
   import java.net.InetAddress;
   import java.net.Inet6Address;
   import java.net.DatagramPacket;
   import java.net.InetSocketAddress;
   import java.net.InterfaceAddress;
   
   import javax.swing.Timer;

   //jpcap library
   import jpcap.JpcapCaptor.*;
   import jpcap.*;
   import jpcap.packet.EthernetPacket;
   import jpcap.packet.ICMPPacket;
   import jpcap.packet.IPPacket;
   import jpcap.packet.Packet;
   import java.net.Inet4Address;
   import jpcap.packet.IPv6Option;
   import java.net.URL;
   import jpcap.packet.Packet;

   public class ipv6 extends JFrame implements ActionListener{
       
    // declaring global variables
     private JTextField jtfDIP = new JTextField();
     private JTextField jtfDS = new JTextField();
     private JTextField jtfDV = new JTextField();
     private JTextField jtfPCQ = new JTextField();
     private JTextField jtfPCT = new JTextField();
     private JComboBox jcboPType = new JComboBox(new String[]{"UDP","ICMP"});
     private JComboBox jcboDevice = new JComboBox(new String[]{});
     private JTextField jtfsrcadd = new JTextField();
     private JButton jbtGen = new JButton("Generate");
     private JTextArea jtaResult = new JTextArea();
     private String ip, HexStr, hex, src;
     private int size, value, packet, time, port, mCounter=0;
     private long start, stop;
     private byte buf[];
     private InetAddress add;
     private InetAddress address[];
     private DatagramSocket sock ;
     private DatagramPacket sendPacket, receivePacket;
     private Timer timer, timerDuration;
     private NetworkInterface[] devices = JpcapCaptor.getDeviceList();
       
   ipv6(){
         JPanel p = new JPanel();
         p.setLayout(new GridLayout(0,2));
         p.add(new Label("Source Address"));
         p.add(jtfsrcadd);
         p.add(new Label("Destination Address"));
         p.add(jtfDIP);
         p.add(new Label("Network Adapter"));
         p.add(jcboDevice);
         p.add(new Label("Protocol Type"));
         p.add(jcboPType);      
         p.add(new Label("Data Size"));
         p.add(jtfDS);
         p.add(new Label("Payload in Hexadecimal"));
         p.add(jtfDV);
         p.add(new Label("Quantity of packets per second"));
         p.add(jtfPCQ);
         p.add(new Label("Time in seconds"));
         p.add(jtfPCT);
         p.add(new Label("Result :")); 
        
        /* //setting default values
        jtfsrcadd.setText("2404:a8:400:1600:216:d3ff:fe06:8254");
        jtfDIP.setText("2404:a8:400:1600:208:2ff:fe69:9c89");
        jtfPCQ.setText("1");
        jtfDS.setText("256");
        jtfDV.setText("AB");
        jtfPCT.setText("1"); */
         
        //setting Client GUI Interface
        jtaResult.setBorder(BorderFactory.createLineBorder(Color.black));
        jtaResult.setBounds(new Rectangle(1, 1, 398, 510));
        jbtGen.setBounds(new Rectangle(104, 525, 188, 44));
        this.getContentPane().add(p,BorderLayout.NORTH);
        this.getContentPane().add(new JScrollPane(jtaResult), BorderLayout.CENTER);
        this.getContentPane().add(jbtGen,BorderLayout.SOUTH);
            
        //Register Listener
         jbtGen.addActionListener(this);
         jcboPType.addActionListener(this);  
   }
   

 public void actionPerformed(ActionEvent e){
     //When user choose UDP from protocol dropdown list box 
      if((jcboPType.getSelectedItem()).equals("UDP")){
       
       //Capture the input given by user in JText field
        try{
                ip = jtfDIP.getText().trim();
                size = Integer.parseInt(jtfDS.getText().trim()); 
                hex = jtfDV.getText().trim(); 
                //convert string to hexadecimal
                value = Integer.valueOf(hex,16);
                HexStr = String.valueOf(value);
                packet = Integer.parseInt(jtfPCQ.getText().trim());
                time = Integer.parseInt(jtfPCT.getText().trim());
             }
            catch(NumberFormatException num){
                
                //catch any error from input given and exit the system
                  JOptionPane.showMessageDialog(null,num.getMessage());
                  System.exit(-1);
            }
         
        //check whether the value of payload size given is within the range
          if (size<45 || size > 1500) {             
                JOptionPane.showMessageDialog(null,"Out of range");
               System.exit(0);
        }
      
       //Create buffer and declare local variable for port number  
        buf = new byte[size];
        port = 8000;

          
      // Another code for Ipv6 : Inet6Address address6= (Inet6Address)InetAddress.getByName(IPaddress6);
      //Create a datagram for sending packet
        try{ 
            add = InetAddress.getByName(ip);      
            sock = new DatagramSocket();
            sendPacket = new DatagramPacket(buf,buf.length,add,port);
          }
        catch(IOException ex){
             JOptionPane.showMessageDialog(null,ex.getMessage());
        }//end try

    //When click the Generate button
      if(e.getSource() == jbtGen){
            //clear the Result Text Area
            jtaResult.setText(null);
              
      try{
          //start timer, send how many packet per seconds eg. 1 packet per second
          timer = new javax.swing.Timer((1000/packet) ,this); 
          timer.start();  
          jtaResult.append("Normal timer start.");
          
          //time given eg. 10 seconds
          timerDuration = new javax.swing.Timer(1000*(time+1),this); 
          timerDuration.start();
          jtaResult.append("\nDuration timer start.");
         }
       catch(ArithmeticException ae){
              JOptionPane.showMessageDialog(null,ae.getMessage());
              timer.stop();
              timerDuration.stop();
              System.exit(-1);
         }
     }//end jbtGen
       else
       // generate each packet according to time given
       if(e.getSource() == timer) 
       {
          
             try{
                //inserting msgID inside the packet array on first byte, index 0 (increase automatically)
                 buf[0] = (byte) mCounter;
                //fill up the rest of the array with payload
                Arrays.fill( buf,1,buf.length - 1,  (byte) value); 
                sendPacket.setData(buf);
                sock.send(sendPacket);
                mCounter++; 

                //Start RTT 
                start = System.currentTimeMillis(); 
           }
          catch(IOException ex)
           {
            ex.printStackTrace();
            timer.stop();
            timerDuration.stop();  
            jtaResult.append("\nTime's up!");
            System.exit(-1);
           }//end catch     
       }//end timer
   else
       if(e.getSource()== timerDuration) 
       {     
       timer.stop();
       timerDuration.stop();   
       jtaResult.append("\nTime's up!");
       }//end if e.getSource   timerDuration
     }//end if UDP
   else
  if((jcboPType.getSelectedItem()).equals("ICMP")){
             
       // jtaResult.setText(null);
        jcboDevice.addActionListener(this);
        int index = jcboDevice.getSelectedIndex();
        
     if(e.getSource()==jbtGen){
       jtaResult.setText(null);
        ip = jtfDIP.getText().trim();
        src = jtfsrcadd.getText().trim();
        hex = jtfDV.getText().trim();
        // size = Integer.parseInt(jtfDS.getText().trim()); 
              
        try{          
        JpcapSender sender = JpcapSender.openDevice(devices[index]);
        JpcapCaptor captor = JpcapCaptor.openDevice(devices[index],2000,false,5000);
        
        //ICMP packet structure
        ICMPPacket p =new ICMPPacket();
        p.type = ICMPPacket.ICMP_ECHO;
        p.seq=1000;
        p.orig_timestamp=123;
        p.trans_timestamp=456;
        p.recv_timestamp=789;
             
       p.setIPv4Parameter(0,false,false,false,0,false,false,false,0, 1010101,128,IPPacket.IPPROTO_ICMP,InetAddress.getLocalHost(),InetAddress.getByName(ip));
       // p.setIPv6Parameter(0,0,0,128,InetAddress.getByName(src),InetAddress.getByName(ip));
        p.data= hex.getBytes();

        //Create Ethernet packet
        EthernetPacket ether=new EthernetPacket();
        ether.frametype= EthernetPacket.ETHERTYPE_IP;
        
       //specified source mac address
        ether.src_mac= devices[index].mac_address;
        byte[] r_mac=null;
        while(true){
               // Ping gateway to get destination mac address
                Packet ping= captor.getPacket();
                if(ping==null){
                        System.out.println("cannot obtain MAC address of default gateway.");
                        System.exit(-1);
                }else 
                    if(Arrays.equals(((EthernetPacket)ping.datalink).dst_mac,devices[index].mac_address))
                    continue;
                r_mac=((EthernetPacket)ping.datalink).dst_mac;
                break;
        }
        //specified destination mac address
        ether.dst_mac= r_mac; 
        p.datalink=ether;
        
       //sending 4 ICMP packet using for loop
       for(int y=1; y < 5; y++){   
        //identifier number increase automaticaly for each packet
        p.id = y;
        //sending ICMP packet
        sender.sendPacket(p);
        
        //Start RTT for ICMP
        long starticmp = System.currentTimeMillis();
        
        Packet reply = captor.getPacket();           
        //Stop RTT 
        long stopicmp = System.currentTimeMillis();
	       
        if(reply==null){
			jtaResult.append("No reply.");
	}
         else if(p.type==ICMPPacket.ICMP_ECHO){
	        p.src_ip.getHostName();
                String bite = String.valueOf(p.data.length);
              //Calculate RTT and printout ICMP reply on text area
                long rtt = (stopicmp - starticmp);
                jtaResult.append("\nReply from " + p.dst_ip + " id= "+ p.id +" bytes= " + bite + " time < "+ rtt + "ms" + " TTL="+ p.hop_limit );                           
              }
           }
        }
        catch(IOException g){
            JOptionPane.showMessageDialog(null,g.getMessage());
        }
      }//end jbtGen
  }//end icmp
}//end ActionEvent
 
 //To convert unsigned byte to integer
  public static int unsignedByteToInt(byte b) {
    return (int) b & 0xFF;
    }
  
public static void main(String args[]) throws IOException{
         ipv6 frame = new ipv6();
         frame.setTitle("Client Screen");
         frame.setSize(new Dimension(407,610));
         frame.setVisible(true);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
       //detecting Network Interface Card
        if(args.length<1){
                  for(int i=0; i<frame.devices.length;i++){
                  frame.jcboDevice.addItem(frame.devices[i].description);          
                }
        }    
     
   //all the programming for receive packet
    DatagramSocket socket = new DatagramSocket(15);
  
 
    frame.jtaResult.append("Start listening for incoming packet....\n");
   // int val = Integer.parseInt(frame.jtfDS.getText());
    while(true){
       try{    
        //receive packet from server
         byte buffer_rec[] = new byte[1500];
         DatagramPacket rec_Packet = new DatagramPacket(buffer_rec, buffer_rec.length);
         socket.receive(rec_Packet); 
         
        //Stop RTT
         frame.stop = System.currentTimeMillis();
        
         //print result
         frame.jtaResult.append("Receive packet.........\n");
         frame.jtaResult.append("\nReceived from host " + rec_Packet.getAddress().getHostAddress() +
                          "\nfrom Port Number " + rec_Packet.getPort() +
                          "\nDataSize: " + rec_Packet.getData().length +
                          "\nID: " + buffer_rec[0] + "\nwith Data Payload:");
          
         for (int i = 1 ; i < buffer_rec.length -1 ; i++)
          {
              String value = String.valueOf(buffer_rec[i]);
              int a = unsignedByteToInt(buffer_rec[i]);
              frame.jtaResult.append(String.valueOf(a));
          }
             frame.jtaResult.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"); 
             frame.jtaResult.append("\nTime elapsed (milliseconds) = " + (frame.stop - frame.start) + "\n" +
                         "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"); 
       }//end try
      catch(Exception ex){
           ex.printStackTrace();
       }      
  }//end while
 }//end main
}//end whole class
  
