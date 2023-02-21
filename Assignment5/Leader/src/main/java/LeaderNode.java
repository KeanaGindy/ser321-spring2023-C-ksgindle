package main.java;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.*;
import org.json.*;
import org.json.*;


public class LeaderNode {
    
    private static int port;
    private static String host;
    private static ArrayList<Socket> nodeSockets =  new ArrayList<Socket>();
    private static Socket socket;
    private static Socket node1Socket;
    private static Socket node2Socket;
    private static Socket clientSocket;
    private static Socket clientSocket2;
    private static PrintWriter sout;
    private static int creditOnTab;
    private static String r1;
    private static int tracker;
    
public static void main(String[] args){
    
   
    OutputStream out = null;
    InputStream in = null;
    ServerSocket server = null;
    BufferedReader bufferedReader;
    BufferedReader bufferedReaderNode1;
    BufferedReader bufferedReaderNode2;
    JSONObject json;
    String string;
    int connections = 0;
    String clientID;
    int dollarAmount;
    
    boolean client = false;
    boolean node1 = false;
    boolean node2 = false;
    
        try {
                //LEADER CONNECTION
                host = args[0];
                port =  Integer.parseInt(args[1]);
                server = new ServerSocket(port);
                System.out.println("You are in");
                //NODE1 CONNECTION
                node1Socket = server.accept();
                nodeSockets.add(node1Socket);
                connections++;
                bufferedReaderNode1 = new BufferedReader(new InputStreamReader(node1Socket.getInputStream())); 
                System.out.println("node1Socket connected");
                //NODE2 CONNECTION
                node2Socket = server.accept();
                nodeSockets.add(node2Socket);
                connections++;
                System.out.println("node2Socket connected");
                bufferedReaderNode2 = new BufferedReader(new InputStreamReader(node2Socket.getInputStream())); 
                
                clientSocket = server.accept();
                connections++;
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                
                //GETTING CLIENT ID
                json = new JSONObject();
                json.put("type", "id");
                json.put("data", "what is your clientID?");
                sout = new PrintWriter(clientSocket.getOutputStream(), true);
                sout.println(json.toString());
                
                while(true){
                    
                        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        json = new JSONObject(bufferedReader.readLine());
                    
                    if(json.getString("type").equals("IDresponse")) {
                        //print ID retrieved
                        string = json.getString("data");
                        System.out.println("Received client id: " + string);
                        
                        //ask for client id
                        json = new JSONObject();
                        json.put("type", "clientID");
                        json.put("data", string);
                        //sends id to the nodes
                        sendToNodes(json);
                        
                        json = new JSONObject(bufferedReaderNode1.readLine());
                        string = json.getString("data");
                        System.out.println("Received from node: " + string);
                        
                        json = new JSONObject(bufferedReaderNode2.readLine());
                        string = json.getString("data");
                        System.out.println("Received from node: " + string);

                        //GETTING CLIENT CHOICE
                        json = new JSONObject();
                        json.put("type", "choice");
                        json.put("data", "Client options: 'credit' or 'payback'");
                        sout = new PrintWriter(clientSocket.getOutputStream(), true);
                        sout.println(json.toString());
                        
                    } else if(json.getString("type").equals("idAccepted")) {
                        receiveFromNodes();
                        
                        json = new JSONObject();
                        json.put("type", "clientID");
                        json.put("data", "Options: 'credit' or 'pay-back'");
                        sout = new PrintWriter(clientSocket.getOutputStream(), true);
                        sout.println(json.toString());
                        
                    }
                    
                    
                    else if(json.getString("type").equals("response")) {
                        
                        string = json.getString("data");
                        System.out.println("Client choice: " + string);
                        if(string.equals("credit")) {
                            
                            json = new JSONObject();
                            json.put("type", "creditchoice");
                            json.put("data", "How much credit would you like?");
                            sout = new PrintWriter(clientSocket.getOutputStream(), true);
                            sout.println(json.toString());
                            
                            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            json = new JSONObject(bufferedReader.readLine());
                            
                            
                            string = json.getString("data");
                            System.out.println("CREDIT RECIEVED: " + string);
                            dollarAmount = Integer.parseInt(string);
                            
                            json = new JSONObject();
                            json.put("type", "wantcredit");
                            json.put("data", string);
                            
                            
                            String node1Confirmation;
                            String node2Confirmation;
                            int tempDollar2;
                            sendToNodes(json);
                            json = new JSONObject(bufferedReaderNode1.readLine());
                            node1Confirmation = json.getString("data");
                            json = new JSONObject(bufferedReaderNode2.readLine());
                            node2Confirmation = json.getString("data");
                            
                            System.out.println("Answers from the nodes: " + node1Confirmation + " and " + node2Confirmation);
                            
                            if(node1Confirmation.equals("yes") && node2Confirmation.equals("yes")) {
                                json = new JSONObject();
                                json.put("type", "yes");
                                json.put("data", "yes");
                                sendToNodes(json);
                                
                                json = new JSONObject(bufferedReaderNode1.readLine());
                                r1 = json.getString("data");
                                json = new JSONObject(bufferedReaderNode2.readLine());
                                String r2 = json.getString("data");
                                System.out.println("Nodes responses: " + r1 + " and " + r2);
                                
                                int one = Integer.parseInt(r1);
                                int two = Integer.parseInt(r2);
                                int three = one + two;
                                String senderString = Integer.toString(three);
                                
                                creditOnTab = one;
                                json = new JSONObject();
                                json.put("type", "choice");
                                json.put("data", "CCredit was ACCEPTED. \n You now have: " + one + " in credit. \n Do you want to request 'credit' or 'pay-back'");
                                sout = new PrintWriter(clientSocket.getOutputStream(), true);
                                sout.println(json.toString());
                                
                            }else if(node1Confirmation.equals("no") && node2Confirmation.equals("no") || 
                                    node1Confirmation.equals("yes") && node2Confirmation.equals("no") ||
                                    node1Confirmation.equals("no") && node2Confirmation.equals("yes")){
                                System.out.println("Leader is replying to node data flags");
                                System.out.println("Sending ---> no");
                                json = new JSONObject();
                                json.put("type", "no");
                                json.put("data", "no");
                                sendToNodes(json);
                                
                                
                                json = new JSONObject(bufferedReaderNode1.readLine());
                                node1Confirmation = json.getString("data");
                                json = new JSONObject(bufferedReaderNode2.readLine());
                                node2Confirmation = json.getString("data");
                                
                                if(node1Confirmation.equals("cancelled") && node2Confirmation.equals("cancelled")) {
                                    System.out.println("Node1 said: " + node1Confirmation + " and node2 said: " + node2Confirmation);
                                    json = new JSONObject();
                                    json.put("type", "choice");
                                    json.put("data", "Credit was DECLINED. \n Do you want to request 'credit' or 'pay-back'");
                                    sout = new PrintWriter(clientSocket.getOutputStream(), true);
                                    sout.println(json.toString());
                                }
                             
                            }
                              
                        }else if(string.equals("pay-back")) {
                              //ASKS HOW MUCH CREDIT THE CLIENT IS ASKING FOR
                              //sending choice to nodes
                              json = new JSONObject();
                              json.put("type", "paychoice");
                              json.put("data", "How much would you like to pay-back?");
                              sout = new PrintWriter(clientSocket.getOutputStream(), true);
                              sout.println(json.toString());
                              
                              bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                              json = new JSONObject(bufferedReader.readLine());
                              
                              string = json.getString("data");
                              System.out.println("pay-back amount recieved from client: " + string);
                              tracker = Integer.parseInt(string);
                              //sends value to nodes
                              json = new JSONObject();
                              json.put("type", "wantpay");
                              json.put("data", string);
                              sendToNodes(json);
                              
                              json = new JSONObject(bufferedReaderNode1.readLine());
                              String n1 = json.getString("data");
                              json = new JSONObject(bufferedReaderNode2.readLine());
                              String n2 = json.getString("data");
                              
                              int n3 = Integer.parseInt(n1);
                              int n4 = Integer.parseInt(n2);
                              int n5 = n3 + n4;
                              
                              System.out.println("data coming back is: " + n1 + " and "+ n2);
                              System.out.println("CHECK 1");
                              if(json.getString("type").equals("error")) {
                                  json = new JSONObject();
                                  json.put("type", "choice");
                                  json.put("data", "NO CREDIT TO PAY \n Your credit is now: " + creditOnTab + "\n Do you want to request 'credit' or 'pay-back'");
                                  sout = new PrintWriter(clientSocket.getOutputStream(), true);
                                  sout.println(json.toString());
                              }else{
                                 
                                  int temp = Integer.parseInt(n1) + Integer.parseInt(n2);
                                  System.out.println("temp --->>>>>>" + temp);
                          
                                  creditOnTab = creditOnTab - tracker;
                                  System.out.println("credit tab ==== " + creditOnTab);
                                  json = new JSONObject();
                                  json.put("type", "choice");
                                  json.put("data", "CREDIT WAS PAID \n Your credit is now: " + creditOnTab + "\n Do you want to request 'credit' or 'pay-back'");
                                  sout = new PrintWriter(clientSocket.getOutputStream(), true);
                                  sout.println(json.toString());
                                  
                              }
                              
                              
                              
                        }
                       
                    }
                     
                    System.out.println("LOOPING");
                    
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 * Description: simply sends a Json to all known nodes.
 * @param message
 */
public static void sendToNodes(JSONObject message) {
    try {
        for(Socket s : nodeSockets) {
            sout = new PrintWriter(s.getOutputStream(), true);
            sout.println(message.toString());
        }
        
    }catch (Exception e) {
        System.out.println("error brother");
    }
    
}

    public static void receiveFromNodes() {
        String string = " ";
        String string2 = " ";
        JSONObject json = new JSONObject();
        JSONObject json2 = new JSONObject();
        
        try {
            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(node1Socket.getInputStream()));
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(node2Socket.getInputStream()));
                
                json = new JSONObject(bufferedReader1.readLine());
                json2 = new JSONObject(bufferedReader2.readLine());
                
                string = json.getString("data");
                System.out.println("-----------");
                string2 = json.getString("data");
            
        }catch (Exception e) {
            System.out.println("error brother");
        }
        
    }
}