/*
Simple Web Server in Java which allows you to call 
localhost:9000/ and show you the root.html webpage from the www/root.html folder
You can also do some other simple GET requests:
1) /random shows you a random picture (well random from the set defined)
2) json shows you the response as JSON for /random instead the html page
3) /file/filename shows you the raw file (not as HTML)
4) /multiply?num1=3&num2=4 multiplies the two inputs and responses with the result
5) /github?query=users/amehlhase316/repos (or other GitHub repo owners) will lead to receiving
   JSON which will for now only be printed in the console. See the todo below

The reading of the request is done "manually", meaning no library that helps making things a 
little easier is used. This is done so you see exactly how to pars the request and 
write a response back
*/

package funHttpServer;

import java.io.*;
import org.json.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Map;
import java.util.LinkedHashMap;
import java.nio.charset.Charset;
class WebServer {
  public static void main(String args[]) {
    WebServer server = new WebServer(9000);
  }

  /**
   * Main thread
   * @param port to listen on
   */
  public WebServer(int port) {
    ServerSocket server = null;
    Socket sock = null;
    InputStream in = null;
    OutputStream out = null;

    try {
      server = new ServerSocket(port);
      while (true) {
        sock = server.accept();
        out = sock.getOutputStream();
        in = sock.getInputStream();
        byte[] response = createResponse(in);
        out.write(response);
        out.flush();
        in.close();
        out.close();
        sock.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (sock != null) {
        try {
          server.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Used in the "/random" endpoint
   */
  private final static HashMap<String, String> _images = new HashMap<>() {
    {
      put("streets", "https://iili.io/JV1pSV.jpg");
      put("bread", "https://iili.io/Jj9MWG.jpg");
    }
     
  };
   
   private final static HashMap<String, String> _imagesFishes = new HashMap<>() {
    {
      put("clownfish", "https://cdn.mos.cms.futurecdn.net/4UdEs7tTKwLJbxZPUYR3hF.jpg");
      put("betafish", "https://iili.io/HleE5Hg.md.jpg");
      put("guppy", "https://www.thesprucepets.com/thmb/QsK0cYtPWxywxG2jn02tyRlyWZg=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/guppy-fish-species-profile-5078901-hero-9095fa292246421b820d32d4731c991b.jpg");
      put("goldfish", "https://i.natgeofe.com/n/18708334-6fce-40b5-ade6-a7e2db7035f2/01-goldfish-nationalgeographic_1567132_4x3.jpg");
      put("angelfish", "https://cdn.shopify.com/s/files/1/0311/3149/articles/care-guide-for-freshwater-angelfish-the-feisty-angel-of-the-aquarium-770309.jpg?v=1659758851");
      put("goldfish", "https://i.natgeofe.com/n/18708334-6fce-40b5-ade6-a7e2db7035f2/01-goldfish-nationalgeographic_1567132_4x3.jpg");
    }
   };

  private Random random = new Random();

  /**
   * Reads in socket stream and generates a response
   * @param inStream HTTP input stream from socket
   * @return the byte encoded HTTP response
   */
  public byte[] createResponse(InputStream inStream) {

    byte[] response = null;
    BufferedReader in = null;

    try {

      // Read from socket's input stream. Must use an
      // InputStreamReader to bridge from streams to a reader
      in = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));

      // Get header and save the request from the GET line:
      // example GET format: GET /index.html HTTP/1.1

      String request = null;

      boolean done = false;
      while (!done) {
        String line = in.readLine();

        System.out.println("Received: " + line);

        // find end of header("\n\n")
        if (line == null || line.equals(""))
          done = true;
        // parse GET format ("GET <path> HTTP/1.1")
        else if (line.startsWith("GET")) {
          int firstSpace = line.indexOf(" ");
          int secondSpace = line.indexOf(" ", firstSpace + 1);

          // extract the request, basically everything after the GET up to HTTP/1.1
          request = line.substring(firstSpace + 2, secondSpace);
        }

      }
      System.out.println("FINISHED PARSING HEADER\n");

      // Generate an appropriate response to the user
      if (request == null) {
        response = "<html>Illegal request: no GET</html>".getBytes();
      } else {
        // create output buffer
        StringBuilder builder = new StringBuilder();
        // NOTE: output from buffer is at the end

        if (request.length() == 0) {
          // shows the default directory page

          // opens the root.html file
          String page = new String(readFileInBytes(new File("www/root.html")));
          // performs a template replacement in the page
          page = page.replace("${links}", buildFileList());

          // Generate response
          builder.append("HTTP/1.1 200 OK\n");
          builder.append("Content-Type: text/html; charset=utf-8\n");
          builder.append("\n");
          builder.append(page);

        } else if (request.equalsIgnoreCase("json")) {
          // shows the JSON of a random image and sets the header name for that image

          // pick a index from the map
          int index = random.nextInt(_images.size());

          // pull out the information
          String header = (String) _images.keySet().toArray()[index];
          String url = _images.get(header);

          // Generate response
          builder.append("HTTP/1.1 200 OK\n");
          builder.append("Content-Type: application/json; charset=utf-8\n");
          builder.append("\n");
          builder.append("{");
          builder.append("\"header\":\"").append(header).append("\",");
          builder.append("\"image\":\"").append(url).append("\"");
          builder.append("}");

        }else if (request.equalsIgnoreCase("json2")){
           int index = random.nextInt(_imagesFishes.size());

          // pull out the information
          String header = (String) _imagesFishes.keySet().toArray()[index];
          String url = _imagesFishes.get(header);

          // Generate response
          builder.append("HTTP/1.1 200 OK\n");
          builder.append("Content-Type: application/json; charset=utf-8\n");
          builder.append("\n");
          builder.append("{");
          builder.append("\"header\":\"").append(header).append("\",");
          builder.append("\"image\":\"").append(url).append("\"");
          builder.append("}");
           
           
        }else if (request.equalsIgnoreCase("random")) {
          // opens the random image page

          // open the index.html
          File file = new File("www/index.html");

          // Generate response
          builder.append("HTTP/1.1 200 OK\n");
          builder.append("Content-Type: text/html; charset=utf-8\n");
          builder.append("\n");
          builder.append(new String(readFileInBytes(file)));

        } else if (request.contains("file/")) {
          // tries to find the specified file and shows it or shows an error

          // take the path and clean it. try to open the file
          File file = new File(request.replace("file/", ""));

          // Generate response
          if (file.exists()) { // success
            builder.append("HTTP/1.1 200 OK\n");
            builder.append("Content-Type: text/html; charset=utf-8\n");
            builder.append("\n");
            builder.append(new String(readFileInBytes(file)));
          } else { // failure
            builder.append("HTTP/1.1 404 Not Found\n");
            builder.append("Content-Type: text/html; charset=utf-8\n");
            builder.append("\n");
            builder.append("File not found: " + file);
          }
        } else if (request.contains("multiply?")) {
          // This multiplies two numbers, there is NO error handling, so when
          // wrong data is given this just crashes
           
           try {
              
                 try {
                 Map<String, String> query_pairs = new LinkedHashMap<String, String>();
                 // extract path parameters
                 query_pairs = splitQuery(request.replace("multiply?", ""));
                 // extract required fields from parameters
                 Integer num1 = Integer.parseInt(query_pairs.get("num1"));
                 Integer num2 = Integer.parseInt(query_pairs.get("num2"));
                 // do math
                 Integer result = num1 * num2;
                 if (result != null) { // success
                    builder.append("HTTP/1.1 200 OK\n");
                    builder.append("Content-Type: text/html; charset=utf-8\n");
                    builder.append("\n");
                    builder.append("Result is: " + result);
                  }else if (num2 == null && (num1 >= 0 || num1 <=0)) { // failure
                     num2 = 0;
                     result = num1 * num2;
                     builder.append("HTTP/1.1 200 OK\n");
                     builder.append("Num2 not given, set to default.\n");
                 }else if (num1 == null && (num2 >= 0 || num2 <=0)) { // failure
                     num1 = 0;
                     result = num1 * num2;
                     builder.append("HTTP/1.1 200 OK\n");
                     builder.append("Num1 not given, set to default.\n");
                  }else if (num1 == null && num2 == null) { // failure
                     num1 = 0;
                     num2 = 0;
                     result = num1 * num2;
                     builder.append("HTTP/1.1 200 OK\n");
                     builder.append("Number values not given, default is 0.\n");
                  } else {
                     builder.append("HTTP/1.1 400 Bad Request\n");
                     builder.append("Content-Type: text/html; charset=utf-8\n");
                     builder.append("\n");
                     builder.append("I am not sure what you want me to do...");
                  }
                 }catch (NumberFormatException nfe) {
                    builder.append("HTTP/1.1 406 Not Acceptable\n");
                    builder.append("Content-Type: text/html; charset=utf-8\n");
                    builder.append("\n");
                    builder.append("Parameters filled out incorrectly.");
                    nfe.printStackTrace();
                 }
           }catch (Exception anything)
           {
                 builder.append("HTTP/1.1 400 Bad Request\n");
                 builder.append("Content-Type: text/html; charset=utf-8\n");
                 builder.append("\n");
                 builder.append("I am not sure what you want me to do...");
                 anything.printStackTrace();
           }
           

          // TODO: Include error handling here with a correct error code and
          // a response that makes sense

        } else if (request.contains("github?")) {
          // pulls the query from the request and runs it with GitHub's REST API
          // check out https://docs.github.com/rest/reference/
          //
          // HINT: REST is organized by nesting topics. Figure out the biggest one first,
          //     then drill down to what you care about
          // "Owner's repo is named RepoName. Example: find RepoName's contributors" translates to
          //     "/repos/OWNERNAME/REPONAME/contributors"
           try {

             Map<String, String> query_pairs = new LinkedHashMap<String, String>();
             query_pairs = splitQuery(request.replace("github?", ""));
             String json = fetchURL("https://api.github.com/" + query_pairs.get("query"));
             JSONArray repoArray = new JSONArray(json);

             JSONArray newJSON = new JSONArray();

             for(int i=0; i<repoArray.length(); i++){

               // now we have a JSON object, one repo 
               JSONObject repo = repoArray.getJSONObject(i);
               int id = repo.getInt("id"); 
               System.out.println(id);

               String repoName = repo.getString("full_name");
               System.out.println(repoName);
               // owner is a JSON object in the repo object, get it and save it in own variable then read the login name
               JSONObject owner = repo.getJSONObject("owner");
               String ownername = owner.getString("login");
               System.out.println(ownername);

               JSONObject newRepo = new JSONObject();


               newRepo.put("full_name",repoName);
               newRepo.put("id",id);
               newRepo.put("login",ownername);

               newJSON.put(newRepo);
             }

               PrintWriter out = new PrintWriter("repoShort.json");
               out.println(newJSON.toString());
               out.close();


             builder.append("HTTP/1.1 200 OK\n");
             builder.append("Content-Type: text/html; charset=utf-8\n");
             builder.append("\n");
             builder.append(newJSON.toString());
           }catch(Exception anything){
              anything.printStackTrace();
              builder.append("HTTP/1.1 404 Bad Request\n");
              builder.append("Content-Type: text/html; charset=utf-8\n");
              builder.append("\n");
              builder.append("Parameters filled out incorrectly.");
              anything.printStackTrace();
           }
          // TODO: Parse the JSON returned by your fetch and create an appropriate
          // response based on what the assignment document asks for

     
        } else if (request.contains("feedFishes?")){
           try{
              try{
              Map<String, String> query_pairs = new LinkedHashMap<String, String>();
              // extract path parameters
              query_pairs = splitQuery(request.replace("feedFishes?", ""));
              // extract required fields from parameters
              Integer fishes = Integer.parseInt(query_pairs.get("fishes"));
              Integer food = Integer.parseInt(query_pairs.get("food"));
              // do math
              Integer result = fishes * food;
             File file = new File("www/new.html");

             // Generate response
             builder.append("HTTP/1.1 200 OK\n");
             builder.append("Content-Type: text/html; charset=utf-8\n");
             builder.append("\n");
             if(food < 0 || fishes <0){
                builder.append("Food or fishes cannot be less than zero. Try again!");
             }else if(food > 0 && fishes != 0 && fishes <= 5) {
                builder.append("You fed: " + fishes + " fishes! You gave them " + food + " ounces of food. Here is the type of fish you fed!");
             }else if(food == 0 && fishes != 0){
                builder.append("You did not feed the fish! They are hungry still! :(");
             }else if(fishes == 0) {
                builder.append("You must choose a number of fish to feed!");
             }else if(fishes > 5){
                builder.append("You can only feed up to five fish! Try again!");
             }else if(food > 5){
                builder.append("You fed: " + fishes + " fishes! You gave them " + food + " ounces of food.  Here is the type of fish you fed!");
             }
             if (fishes != 0 && fishes <= 5){
                for(int i = 0; i < fishes && i < 5;i++) {
                   builder.append(new String(readFileInBytes(file)));
                }
             }
              }catch (NumberFormatException nfe) {
                    builder.append("HTTP/1.1 406 Not Acceptable\n");
                    builder.append("Content-Type: text/html; charset=utf-8\n");
                    builder.append("\n");
                    builder.append("Parameters filled out incorrectly.");
                    nfe.printStackTrace();
                 }
           }catch (Exception anything)
           {
                 builder.append("HTTP/1.1 400 Bad Request\n");
                 builder.append("Content-Type: text/html; charset=utf-8\n");
                 builder.append("\n");
                 builder.append("I am not sure what you want me to do...");
                 anything.printStackTrace();
           }
          
        } else if (request.contains("tax?")) {
          // This gives the sales tax of an item.
           try {
             
                 try {
                    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
                    // extract path parameters
                    query_pairs = splitQuery(request.replace("tax?", ""));
                    // extract required fields from parameters
                    Double itemPrice = Double.parseDouble(query_pairs.get("itemPrice"));
                    Double salesTax = Double.parseDouble(query_pairs.get("salesTax"));
                    // do math
                    Double taxonItem = itemPrice * (salesTax * .0100);
                    Double result = itemPrice + taxonItem;

                    System.out.println(itemPrice);
                    System.out.println(salesTax);
                    if(itemPrice < 0 || salesTax < 0){
                       builder.append("HTTP/1.1 406 Not Acceptable\n");
                      builder.append("Content-Type: text/html; charset=utf-8\n");
                       builder.append("\n");
                      builder.append("Number values are too low!");
                    }else if(itemPrice < 0.1 && salesTax < 0.1){
                      builder.append("HTTP/1.1 406 Not Acceptable\n");
                      builder.append("Content-Type: text/html; charset=utf-8\n");
                       builder.append("\n");
                      builder.append("Number values are too low!");
                    }else if (result != null && itemPrice > 0.0 && salesTax > 0.0) { // success
                       builder.append("HTTP/1.1 200 OK\n");
                       builder.append("Content-Type: text/html; charset=utf-8\n");
                       builder.append("\n");
                       builder.append("The tax on the item is: " + String.format("%.2f",taxonItem) +"\nThe total price of the item is: "+ String.format("%.2f",result));
                     }else if (salesTax == 0.0 & itemPrice > 0.0) { // failure
                        taxonItem = 0.0;
                        result = itemPrice;
                        builder.append("HTTP/1.1 200 OK\n");
                       builder.append("Content-Type: text/html; charset=utf-8\n");
                        builder.append("Tax on item not given, it is assumed there is no tax.\n");
                        builder.append("The tax on the item is: " + String.format("%.2f",taxonItem) +"\nThe total price of the item is: "+ String.format("%.2f",result));
                    }else if (itemPrice == 0.00 & salesTax > 0.0) { // failure
                        itemPrice = 0.0;
                        taxonItem = itemPrice * (salesTax * .0100);
                        result = itemPrice + taxonItem;
                        builder.append("HTTP/1.1 200 OK\n");
                       builder.append("Content-Type: text/html; charset=utf-8\n");
                        builder.append("Item price not given, it is assumed that the price is zero.\n");
                        builder.append("\nThe total price of the item is: "+ String.format("%.2f",result));
                     }else if ((itemPrice == null && salesTax == null) || (itemPrice == 0 && salesTax == 0)) { // failure
                        builder.append("HTTP/1.1 406 Not Acceptable\n");
                       builder.append("Content-Type: text/html; charset=utf-8\n");
                        builder.append("Number values not given\n");
                     } else {
                        builder.append("HTTP/1.1 400 Bad Request\n");
                        builder.append("Content-Type: text/html; charset=utf-8\n");
                        builder.append("\n");
                        builder.append("I am not sure what you want me to do...");
                     }
                 }catch (NumberFormatException nfe) {
                    builder.append("HTTP/1.1 406 Not Acceptable\n");
                    builder.append("Content-Type: text/html; charset=utf-8\n");
                    builder.append("\n");
                    builder.append("Parameters filled out incorrectly.");
                    nfe.printStackTrace();
                 }
           }catch (Exception anything)
           {
                 builder.append("HTTP/1.1 400 Bad Request\n");
                 builder.append("Content-Type: text/html; charset=utf-8\n");
                 builder.append("\n");
                 builder.append("I am not sure what you want me to do...");
                 anything.printStackTrace();
           }
        
        }else {
         // if the request is not recognized at all

          builder.append("HTTP/1.1 400 Bad Request\n");
          builder.append("Content-Type: text/html; charset=utf-8\n");
          builder.append("\n");
          builder.append("I am not sure what you want me to do...");
        
        }

        // Output
        response = builder.toString().getBytes();
      }
    } catch (IOException e) {
      e.printStackTrace();
      response = ("<html>ERROR: " + e.getMessage() + "</html>").getBytes();
    }
    return response;
  }
  /**
   * Method to read in a query and split it up correctly
   * @param query parameters on path
   * @return Map of all parameters and their specific values
   * @throws UnsupportedEncodingException If the URLs aren't encoded with UTF-8
   */
  public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
    // "q=hello+world%2Fme&bob=5"
    String[] pairs = query.split("&");
    // ["q=hello+world%2Fme", "bob=5"]
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
          URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
    }
    // {{"q", "hello world/me"}, {"bob","5"}}
    return query_pairs;
  }

  /**
   * Builds an HTML file list from the www directory
   * @return HTML string output of file list
   */
  public static String buildFileList() {
    ArrayList<String> filenames = new ArrayList<>();

    // Creating a File object for directory
    File directoryPath = new File("www/");
    filenames.addAll(Arrays.asList(directoryPath.list()));

    if (filenames.size() > 0) {
      StringBuilder builder = new StringBuilder();
      builder.append("<ul>\n");
      for (var filename : filenames) {
        builder.append("<li>" + filename + "</li>");
      }
      builder.append("</ul>\n");
      return builder.toString();
    } else {
      return "No files in directory";
    }
  }

  /**
   * Read bytes from a file and return them in the byte array. We read in blocks
   * of 512 bytes for efficiency.
   */
  public static byte[] readFileInBytes(File f) throws IOException {

    FileInputStream file = new FileInputStream(f);
    ByteArrayOutputStream data = new ByteArrayOutputStream(file.available());

    byte buffer[] = new byte[512];
    int numRead = file.read(buffer);
    while (numRead > 0) {
      data.write(buffer, 0, numRead);
      numRead = file.read(buffer);
    }
    file.close();

    byte[] result = data.toByteArray();
    data.close();

    return result;
  }

  /**
   *
   * a method to make a web request. Note that this method will block execution
   * for up to 20 seconds while the request is being satisfied. Better to use a
   * non-blocking request.
   * 
   * @param aUrl the String indicating the query url for the OMDb api search
   * @return the String result of the http request.
   *
   **/
  public String fetchURL(String aUrl) {
    StringBuilder sb = new StringBuilder();
    URLConnection conn = null;
    InputStreamReader in = null;
    try {
      URL url = new URL(aUrl);
      conn = url.openConnection();
      if (conn != null)
        conn.setReadTimeout(20 * 1000); // timeout in 20 seconds
      if (conn != null && conn.getInputStream() != null) {
        in = new InputStreamReader(conn.getInputStream(), Charset.defaultCharset());
        BufferedReader br = new BufferedReader(in);
        if (br != null) {
          int ch;
          // read the next character until end of reader
          while ((ch = br.read()) != -1) {
            sb.append((char) ch);
          }
          br.close();
        }
      }
      in.close();
    } catch (Exception ex) {
      System.out.println("Exception in url request:" + ex.getMessage());
    }
    return sb.toString();
  }
}
