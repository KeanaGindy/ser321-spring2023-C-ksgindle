package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import org.json.*;

public class Node {
    private static int money;
    private static int port;
    private static int creditValue;
    private static String host;
    private static LinkedList<Creditor> creditors = new LinkedList<Creditor>();
    private static String nameString; // temp holder of a clients name
    private static PrintWriter sout;
    private static Socket socket;
    private static String quickMsgString; // simple string to indicate flow direction after payments
    private static int tab; // is the credit taken out by a client
    private static int checker;

    public static void main(String[] args){
        
        //Socket socket;
        OutputStream out = null;
        InputStream in = null;
        ServerSocket server = null;
        Scanner scanner = new Scanner(System.in);
        ObjectOutputStream os;
        BufferedReader bufferedReader;
        JSONObject json;
        //PrintWriter sout;
        String string;
        
        //bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        

        //This section deals with whether or not a money value has been passed by the user
        if(args.length == 2) {
            money = 2000;
            System.out.println("No money passed so money = " + money);
        }else {
          //accepts money passed in
            money = Integer.parseInt(args[2]);
        }
        
        
        //This section established the connection with the clients and the nodes
        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
            socket = new Socket(host,port);
            System.out.println("Hooked up!");
            
            
            
            System.out.println("Money on system is: " + money);
            try {
            while(true) {
                
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    json = new JSONObject(bufferedReader.readLine());
                
                
                
                
                
                if(json.getString("type").equals("clientID")) {
                    //retrieves
                    string = json.getString("data");
                    System.out.println("Client: " + string + " has been identified");
                    //saves name globally since this is the main creditor that we are talking about
                    nameString = string;
                    //stores
                    Creditor newCreditor = new Creditor(string);
                    creditors.add(newCreditor);//<<<<<<<<<<<<<<<<----------------------very important creditor
                    System.out.println(newCreditor.getIdString() + " Has been added to the list of creditors");
                    //---------------------------------
                    // sending back continuation
                    json = new JSONObject();
                    json.put("type", "idAccepted");
                    json.put("data", "client id accepted by node");
                    sout = new PrintWriter(socket.getOutputStream(), true);
                    sout.println(json.toString());
                    //System.out.println("sent out");
                
                }
                else if(json.getString("type").equals("wantcredit")) {
                    string = json.getString("data");
                    int valueWanted = Integer.parseInt(string);
                    valueWanted = (valueWanted);
                    System.out.println("Value received for credit request: " + valueWanted);
                    System.out.println("Sending response......");
       //===============================================================
                    //for checking if this node has enough money
                    if(money >= (valueWanted * (1.5))) {
                        
//                        submitCreditRequest(valueWanted,nameString);
//                        System.out.println("method has executed");
                        
                        // sending back continuation
                        json = new JSONObject();
                        json.put("type", "yes");
                        json.put("data", "yes");
                        sout = new PrintWriter(socket.getOutputStream(), true);
                        sout.println(json.toString());
                    }else{
                        // sending back continuation
                        json = new JSONObject();
                        json.put("type", "no");
                        json.put("data", "no");
                        sout = new PrintWriter(socket.getOutputStream(), true);
                        sout.println(json.toString());
                    }
       //===============================================================
                    
                    //waitng for leader to confirm credit should be applied
                    json = new JSONObject(bufferedReader.readLine());
                    string = json.getString("data");
                    System.out.println("Leader has said: " + string);
                    
                    if(string.equals("yes")){
                        //submits credit requests
                        submitCreditRequest(valueWanted,nameString);
                        
                        if(creditValue < 0) {
                            creditValue = 0;
                            json = new JSONObject();
                            json.put("type", "error");
                            json.put("data", Integer.toString(valueWanted));
                            sout = new PrintWriter(socket.getOutputStream(), true);
                            sout.println(json.toString());
                        }
                        //starts loop again
                        json = new JSONObject();
                        json.put("type", "idAccepted");
                        json.put("data", Integer.toString(tab));
                        sout = new PrintWriter(socket.getOutputStream(), true);
                        sout.println(json.toString());
                        
                    }else if(string.equals("no")) {
                        json = new JSONObject();
                        json.put("type", "cancelled");
                        json.put("data", "cancelled");
                        sout = new PrintWriter(socket.getOutputStream(), true);
                        sout.println(json.toString());
                    }
                    
                }
                
                
                else if(json.getString("type").equals("wantpay")) {
                    string = json.getString("data");
                    int payBackValue = Integer.parseInt(string)/2;
                    System.out.println("Value received for pay-back request: " + payBackValue);
                    
                    payRequest(payBackValue,nameString);
                    
                    if(quickMsgString.equals("n")) {
                        json = new JSONObject();
                        json.put("type", "error");
                        json.put("data", "nothing");
                        sout = new PrintWriter(socket.getOutputStream(), true);
                        sout.println(json.toString());
                    }else {
                        json = new JSONObject();
                        json.put("type", "payment");
                        json.put("data", Integer.toString(checker));
                        sout = new PrintWriter(socket.getOutputStream(), true);
                        sout.println(json.toString());
                    }
                    
                    
                    
                    
                }
                
                
                System.out.println("looping");
                
        }//while loop
            
            }catch(Exception e) {
                System.out.println("closed");
            } 
            
            
            
            

            
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        
    }

    public static int getMoney() {
        return money;
    }

    public static void setMoney(int money) {
        Node.money = money;
    }
    
    /**
     * Description: This method is used to credit a creditor.
     * @param amountToCredit || This param takes in the total amount requested but then halves it for the nodes
     * @param name || This param is used to ID the creditor we are talking aboutd
     */
    public static void submitCreditRequest(int amountToCredit,String name) {
        int temp;
        money = (money - amountToCredit);
        
        if(money < 0) {
            money = 0;
            System.out.println("not enough money left on this node");
            return;
        }
        
        for(Creditor s : creditors) {
            if(s.getIdString().equals(name)) {
                tab = s.getCreditAmount();
                tab += amountToCredit;
                s.setCreditAmount(tab);
                System.out.println((tab) + " has been credited to " + s.getIdString() + " the running credit amount for this creditor = " + (s.getCreditAmount()) );
            }
        }
        
        System.out.println("Total amount of money this node has left is: " + money);
        
//        try {
//            System.out.println("sent1");
//            JSONObject json = new JSONObject();
//            json.put("type", "idAccepted");
//            json.put("data", "yes");
//            sout = new PrintWriter(socket.getOutputStream(), true);
//            sout.println(json.toString());
//        }catch(Exception e) {
//            System.out.println("wrong shizz");
//        }
//        System.out.println("sent2");
    }
    
    
    public static void payRequest(int value,String name) {
        
        for(Creditor s : creditors) {
            
            if(s.getIdString().equals(name)) {
                //if they have credit to pay-back
                if(s.getCreditAmount() != 0) {
                    System.out.println("Credit for " + s.getIdString() + " is " + s.getCreditAmount());
                    s.setCreditAmount(s.getCreditAmount()-value);
                    System.out.println("Credit for " + s.getIdString() + " is now " + s.getCreditAmount());
                    //creditValue = s.getCreditAmount();
                    quickMsgString = "y";
                    checker = s.getCreditAmount();
                    money += checker;
                    
                }
                //if they dont have credit to pay-back
                if(s.getCreditAmount() == 0) {
                    quickMsgString = "n";
                }
                
                
            }
            
        }
        
        
    }
    
    
    
    
    
    
    
    
    
    
    
}
