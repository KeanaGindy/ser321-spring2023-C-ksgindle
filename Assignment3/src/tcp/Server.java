package Solution.tcp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.text.DefaultStyledDocument.ElementSpec;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.json.*;

public class Server {
  /*
   * request: { "selected": <int: 1=joke, 2=quote, 3=image, 4=random> }
   * 
   * response: {"datatype": <int: 1-string, 2-byte array>, "type": <"joke",
   * "quote", "image">, "data": <thing to return> }
   * 
   * error response: {"error": <error string> }
   */


   public static JSONObject messageStart() {
    JSONObject json = new JSONObject();
    json.put("datatype", 10);
    json.put("type", "messageStart");
    json.put("data", "Hello, start by inputting your name!");
    return json;
  }
  public static JSONObject messageOptions() {
    JSONObject json = new JSONObject();
    json.put("datatype", 10);
    json.put("type", "messageOptions");
    return json;
  }
  public static JSONObject leaderboard() {
    JSONObject json = new JSONObject();
    json.put("datatype", 10);
    json.put("type", "leaderboard");
    return json;
  }

  public static JSONObject cities() throws IOException{
    JSONObject json = new JSONObject();
    json.put("datatype", 11);

    json.put("type", "imageCity");

    File berlin = new File("img/city/berlin.jpg");
    File paris = new File("img/city/paris.jpg");
    File phoenix = new File("img/city/phoenix.jpg");
    File rome = new File("img/city/rome.jpg");

    //ERROR HANDLING
    if (!berlin.exists()) {
      System.err.println("Cannot find file: " + berlin.getAbsolutePath());
      System.exit(-1);
    }
    if (!paris.exists()) {
        System.err.println("Cannot find file: " + paris.getAbsolutePath());
        System.exit(-1);
    }
    if (!phoenix.exists()) {
        System.err.println("Cannot find file: " + phoenix.getAbsolutePath());
        System.exit(-1);
    }
    if (!rome.exists()) {
        System.err.println("Cannot find file: " + rome.getAbsolutePath());
        System.exit(-1);
    }


    int random_int = (int)Math.floor(Math.random() * (4 - 1 + 1) + 1);

    // Read in image, chooses random image.
    BufferedImage img;
    if(random_int == 1){
        img = ImageIO.read(berlin);
        json.put("name", "berlin");
    }else if(random_int == 2){
        img = ImageIO.read(paris);
        json.put("name", "paris");
    }else if(random_int == 3){
        img = ImageIO.read(phoenix);
        json.put("name", "phoenix");
    }else{
        img = ImageIO.read(rome);
        json.put("name", "rome");
    }
    byte[] bytes = null;
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      ImageIO.write(img, "png", out);
      bytes = out.toByteArray();
    }
    if (bytes != null) {
      Base64.Encoder encoder = Base64.getEncoder();
      json.put("data", encoder.encodeToString(bytes));
      return json;
    }
    return error("Unable to save image to byte array");
  }

  public static JSONObject countries() throws IOException {
    JSONObject json = new JSONObject();
    json.put("datatype", 11);

    json.put("type", "imageCountry");

    File germany = new File("img/country/germany.jpg");
    File ireland = new File("img/country/ireland.jpeg");
    File southafrica = new File("img/country/southafrica.jpg");

    //ERROR HANDLING
    if (!germany.exists()) {
      System.err.println("Cannot find file: " + germany.getAbsolutePath());
      System.exit(-1);
    }
    if (!ireland.exists()) {
        System.err.println("Cannot find file: " + ireland.getAbsolutePath());
        System.exit(-1);
    }
    if (!southafrica.exists()) {
        System.err.println("Cannot find file: " + southafrica.getAbsolutePath());
        System.exit(-1);
    }

    int random_int = (int)Math.floor(Math.random() * (3 - 1 + 1) + 1);

    // Read in image, chooses random image.
    BufferedImage img;
    if(random_int == 1){
        img = ImageIO.read(germany);
        json.put("name", "germany");
    }else if(random_int == 2){
        img = ImageIO.read(ireland);
        json.put("name", "ireland");
    }else{
        img = ImageIO.read(southafrica);
        json.put("name", "southafrica");
    }
    byte[] bytes = null;
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      ImageIO.write(img, "png", out);
      bytes = out.toByteArray();
    }
    if (bytes != null) {
      Base64.Encoder encoder = Base64.getEncoder();
      json.put("data", encoder.encodeToString(bytes));
      return json;
    }
    return error("Unable to save image to byte array");
  }

  public static JSONObject error(String err) {
    JSONObject json = new JSONObject();
    json.put("error", err);
    return json;
  }

  public static JSONObject guessLetter() {
    JSONObject json = new JSONObject();
    json.put("type","guessLetter");
    json.put("datatype", 10);
    return json;
  }

  public static JSONObject guessWord() {
    JSONObject json = new JSONObject();
    json.put("type","guessWord");
    json.put("datatype", 10);
    return json;
  }

  public static void main(String[] args) throws IOException {
    ServerSocket serv = null;
    HashMap<String,Integer> leaderboardTracker = new HashMap<String,Integer>();

    try {
      serv = new ServerSocket(Integer.parseInt(args[0]));
      String answer = "";
      // NOTE: SINGLE-THREADED, only one connection at a time
      while (true) {
        Socket sock = null;
        try {
          System.out.println("Ready for connection");
          sock = serv.accept(); // blocking wait
          OutputStream out = sock.getOutputStream();
          InputStream in = sock.getInputStream();
          while (true) {
            byte[] messageBytes = NetworkUtils.Receive(in);
            JSONObject message = JsonUtils.fromByteArray(messageBytes);
            JSONObject returnMessage;
            
            int point = 0;

            if (message.has("selected")) {
              String mess = message.getString("selected");
              System.out.println(mess);
              if (mess.equals("l")) {
                returnMessage = leaderboard();
                returnMessage.put("name", message.get("name"));
                leaderboardTracker.put(message.getString("name"),message.getInt("points"));
                String leaderboard = "";
                java.util.Iterator<Entry<String,Integer>> hmIterator = leaderboardTracker.entrySet().iterator();
                while(hmIterator.hasNext()){
                  Map.Entry mapElement = (Map.Entry)hmIterator.next();
                  leaderboard+= ("\nName: " + mapElement.getKey() + ", Points: " + mapElement.getValue());
                }

                returnMessage.put("data", leaderboard);
                
              }else if(mess.equals("ci")){
                returnMessage = cities();
                answer = returnMessage.getString("name");
                System.out.println(answer);
              }else if(mess.equals("co")){
                returnMessage = countries();
                answer = returnMessage.getString("name");
              }else if(mess.equals("Start")){
                returnMessage = messageStart();
              }else if(mess.equals("info")){
                returnMessage = messageOptions();
                //trackingUser.put("name", message.getString("Name"));
                leaderboardTracker.put(message.getString("Name"), message.getInt("points"));
                System.out.println(leaderboardTracker);
                returnMessage.put("data", "Hi "+ message.getString("Name")+"! Your choices: leaderboard(l), cities(ci), countries(co)");
              }else if(mess.equals("guessLetter")){
                String guess = message.getString("guess");
                char x = 'A';
                
                returnMessage = guessLetter();
                System.out.println(answer);
                
                for(int i = 0; i < guess.length(); i++){
                  x = guess.charAt(i);
                  System.out.println(x);
                  returnMessage.put("data", "Your guess is "+ x);
                  returnMessage.put("letter", guess);
                }
                for(int i = 0; i < answer.length(); i++){
                  if(x == (answer.charAt(i))){
                    System.out.println(answer.charAt(i));
                    point = 1;
                  }
                }
                System.out.println(point);
                returnMessage.put("points", point); 
              }else if(mess.equals("guessWord")){
                returnMessage = guessWord();
                String guess = message.getString("guess");
                returnMessage.put("data", "Your guess is "+ guess);
                returnMessage.put("word", guess);

                if(guess.equalsIgnoreCase(answer)){
                  point = 5;
                }else{
                  point = 0;
                }

                returnMessage.put("points", point); 

              }else {
              returnMessage = error("Invalid message received");
              }
            }else{
              returnMessage = error("Invalid message received");
            }

            // we are converting the JSON object we have to a byte[]
            byte[] output = JsonUtils.toByteArray(returnMessage);
            NetworkUtils.Send(out, output);
            break;
          }
        } catch (Exception e) {
          System.out.println("Client disconnect");
        } finally {
          if (sock != null) {
            sock.close();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (serv != null) {
        serv.close();
      }
    }
  }
}