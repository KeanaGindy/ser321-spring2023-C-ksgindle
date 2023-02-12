/**
  File: Performer.java
  Author: Student in Fall 2020B
  Description: Performer class in package taskone.
*/

package taskone;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

/**
 * Class: Performer 
 * Description: Threaded Performer for server tasks.
 */
class Performer implements Runnable{

    private StringList state;
    private Socket conn;

    public Performer(Socket sock, StringList strings) {
        this.conn = sock;
        this.state = strings;
    }

    public JSONObject add(String str) {
        JSONObject json = new JSONObject();
        json.put("datatype", 1);
        json.put("type", "add");
        state.add(str);
        json.put("data", state.toString());
        return json;
    }

    public JSONObject clear() {
        JSONObject json = new JSONObject();
        json.put("datatype", 2);
        json.put("type", "clear");
        json.put("data", state.toString());
        return json;
    }

    public static JSONObject error(String err) {
        JSONObject json = new JSONObject();
        json.put("error", err);
        return json;
    }

    public JSONObject find(int index) {
        JSONObject json = new JSONObject();
        json.put("datatype", 3);
        json.put("type", "find");
        json.put("data",String.valueOf(index));
        return json;
    }

    public JSONObject display() {
        JSONObject json = new JSONObject();
        json.put("datatype", 4);
        json.put("type", "display");
        json.put("data", state.toString());
        return json;
    }

    public JSONObject sort() {
        JSONObject json = new JSONObject();
        json.put("datatype", 5);
        json.put("type", "sort");
        json.put("data", state.toString());
        return json;
    }

    public JSONObject prepend() {
        JSONObject json = new JSONObject();
        json.put("datatype", 6);
        json.put("type", "prepend");
        json.put("data", state.toString());
        return json;
    }

    public JSONObject quit() {
        JSONObject json = new JSONObject();
        json.put("datatype", 0);
        json.put("type", "quit");
        json.put("data", "");
        return json;
    }

    public void run() {
        boolean quit = false;
        OutputStream out = null;
        InputStream in = null;
        try {
            out = conn.getOutputStream();
            in = conn.getInputStream();
            System.out.println("Server connected to client:");
            while (!quit) {
                byte[] messageBytes = NetworkUtils.receive(in);
                JSONObject message = JsonUtils.fromByteArray(messageBytes);
                JSONObject returnMessage = new JSONObject();
   
                int choice = message.getInt("selected");
                    switch (choice) {
                        case (0):
                            returnMessage = quit();
                            break;
                        case (1):
                            String inStr = (String) message.get("data");
                            returnMessage = add(inStr);
                            break;
                        case (2):
                            StringList cuStringList = this.state;
                            cuStringList.clear();
                            returnMessage = clear();
                            break;
                        case (3):
                        String findStr = (String) message.get("data");
                            if(this.state.contains(findStr)){
                                for(int i = 0; i < this.state.size(); i++){
                                    if(this.state.get(i).equalsIgnoreCase(findStr)){
                                        returnMessage = find(i);
                                    }
                                }
                            }else{
                                returnMessage = find(-1);
                            }
                            break;
                        case (4):
                            returnMessage = display();
                            break;
                        case (5):
                            this.state.sort();
                            returnMessage = sort();
                            break;
                        case (6):
                            String prepend = (String) message.get("data");
                            String[] spPrepend = prepend.split(" ");
                            int index = Integer.valueOf(spPrepend[0]);
                            String string = spPrepend[1];
                            for(int i = 0; i < this.state.size(); i++){
                                if(i == index){
                                    String strChange = this.state.get(i);
                                    string += strChange;
                                    this.state.set(i,string);
                                }
                            }   
                            returnMessage = prepend();
                            break;
                        default:
                            returnMessage = error("Invalid selection: " + choice 
                                    + " is not an option");
                            break;
                    }
                // we are converting the JSON object we have to a byte[]
                byte[] output = JsonUtils.toByteArray(returnMessage);
                NetworkUtils.send(out, output);
            }
            // close the resource
            System.out.println("close the resources of client ");
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
