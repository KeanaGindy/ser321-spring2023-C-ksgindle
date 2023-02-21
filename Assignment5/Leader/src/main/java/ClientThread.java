package main.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import org.json.*;

public class ClientThread {
    private static int port;
    private static String host;
     
    public static void main(String[] args){
        Socket socket;
        ServerSocket server = null;
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        String string;
        JSONObject json;
        PrintWriter sout;
        
        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
            socket = new Socket(host,port);
            System.out.println("Client has connected to the Leader!");
            
            while(true) {
            
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                json = new JSONObject(bufferedReader.readLine());
                if(json.getString("type").equals("error")) {
                    string = json.getString("data");
                    System.out.println(string);
                }
                else if(json.getString("type").equals("id")) {
                    string = json.getString("data");
                    System.out.println(string);
                    string = " ";
                    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    string = bufferedReader.readLine();
                    json = new JSONObject();
                    json.put("type", "IDresponse");
                    json.put("data", string);
                    //writes the json back out to the Leader
                    sout = new PrintWriter(socket.getOutputStream(), true);
                    sout.println(json.toString());
                    
                }
                
                else if(json.getString("type").equals("choice")) {
                    string = json.getString("data");
                    System.out.println(string);
                    string = " ";
                    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    string = bufferedReader.readLine();
                    json = new JSONObject();
                    json.put("type", "response");
                    json.put("data", string);
                    sout = new PrintWriter(socket.getOutputStream(), true);
                    sout.println(json.toString());
                    
                }           
                else if(json.getString("type").equals("creditchoice")) {
                    string = json.getString("data");
                    System.out.println(string);
                    string = " ";
                    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    string = bufferedReader.readLine();
                    json = new JSONObject();
                    json.put("type", "credit");
                    json.put("data", string);
                    sout = new PrintWriter(socket.getOutputStream(), true);
                    sout.println(json.toString());
                    
                }
                else if(json.getString("type").equals("paychoice")) {
                    string = json.getString("data");
                    System.out.println(string);
                    string = " ";
                    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    string = bufferedReader.readLine();
                    json = new JSONObject();
                    json.put("type", "payment");
                    json.put("data", string);
                    sout = new PrintWriter(socket.getOutputStream(), true);
                    sout.println(json.toString());
                    
                }
            }
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}