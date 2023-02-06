package Solution.udp;
import java.awt.Dimension;

import org.json.*;

import Solution.tcp.PicturePanel;
import Solution.tcp.OutputPanel.EventHandlers;
import Solution.tcp.PicturePanel.InvalidCoordinateException;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.WindowConstants;


public class ClientGui implements OutputPanel.EventHandlers {
	JDialog frame;
	PicturePanel picturePanel;
	OutputPanel outputPanel;
	boolean gameStarted = false;
	String currentMessage;
	DatagramSocket sock;
	OutputStream out;
	InputStream in;
	ObjectOutputStream os;
	BufferedReader bufferedReader;
	boolean gameEntry = false;
	int points = 0;
	String answer = null;
	ArrayList<String> blanks = new ArrayList<String>();
	String username;


	/**
	 * Construct dialog
	 * @throws IOException 
	 */
	public ClientGui() throws IOException {
		frame = new JDialog();
		frame.setLayout(new GridBagLayout());
		frame.setMinimumSize(new Dimension(500, 500));
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


		// setup the top picture frame
		picturePanel = new PicturePanel();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.25;
		frame.add(picturePanel, c);

		// setup the input, button, and output area
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.75;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		outputPanel = new OutputPanel();
		outputPanel.addEventHandlers(this);
		frame.add(outputPanel, c);

		picturePanel.newGame(1);
		insertImage("img/hi.png", 0, 0);
		outputPanel.appendOutput("Enter 'Start' to input name and see options.");

	}

	/**
	 * Shows the current state in the GUI
	 * @param makeModal - true to make a modal window, false disables modal behavior
	 */
	public void show(boolean makeModal) {
		frame.pack();
		frame.setModal(makeModal);
		frame.setVisible(true);
	}

	/**
	 * Insert an image into the grid at position (col, row)
	 * 
	 * @param filename - filename relative to the root directory
	 * @param row - the row to insert into
	 * @param col - the column to insert into
	 * @return true if successful, false if an invalid coordinate was provided
	 * @throws IOException An error occured with your image file
	 */
	public boolean insertImage(String filename, int row, int col) throws IOException {
		System.out.println("Image insert");
		String error = "";
		try {
			// insert the image
			if (picturePanel.insertImage(filename, row, col)) {
				// put status in output
				return true;
			}
			error = "File(\"" + filename + "\") not found.";
		} catch(PicturePanel.InvalidCoordinateException e) {
			// put error in output
			error = e.toString();
		}
		outputPanel.appendOutput(error);
		return false;
	}


	/*
	 * JSON Objects/Protocol for sending to server
	 */

	  
	public static JSONObject leaderboard() {
		JSONObject request = new JSONObject();
		request.put("selected", "l");
		return request;
	}
	
	public static JSONObject cities() {
		JSONObject request = new JSONObject();
		request.put("selected", "ci");
		return request;
	}
	
	public static JSONObject countries() {
		JSONObject request = new JSONObject();
		request.put("selected", "co");
		return request;
	}

	public static JSONObject start() {
		JSONObject request = new JSONObject();
		request.put("selected", "Start");
		return request;
	}

	public static JSONObject info() {
		JSONObject request = new JSONObject();
		request.put("selected", "info");
		return request;
	}

	public static JSONObject guessLetter() {
		JSONObject request = new JSONObject();
		request.put("selected", "guessLetter");
		return request;
	}

	public static JSONObject guessWord() {
		JSONObject request = new JSONObject();
		request.put("selected", "guessWord");
		return request;
	}

	/**
	 * Submit button handling
	 * 
	 * TODO: This is where your logic will go or where you will call appropriate methods you write. 
	 * Right now this method opens and closes the connection after every interaction, if you want to keep that or not is up to you. 
	 */
	@Override
	public void submitClicked(){
		System.out.println("submit clicked ");

		// Pulls the input box text and converts to bytes
		String input = outputPanel.getInputText();

		currentMessage = "{'type': 'name', 'value' : '"+input+"'}";

		outputPanel.setInputText("");
		
		
		try {

			InetAddress address = InetAddress.getByName("localhost");
      		int port = 8000;
      		sock = new DatagramSocket();



			do {
				 //needes NFE error https://www.freecodecamp.org/news/java-string-to-int-how-to-convert-a-string-to-an-integer/#:~:text=Use%20Integer.parseInt()%20to,it%20will%20throw%20a%20NumberFormatException.
				
				outputPanel.setInputText("");
				JSONObject request = null;
				if(input.equalsIgnoreCase("l") && gameStarted == false){
					outputPanel.setInputText("");
					request = leaderboard();
					request.put("data", input);
					request.put("name", username);
					request.put("points", points);
				}else if(input.equalsIgnoreCase("ci") && gameStarted == false){
					request = cities();	
					outputPanel.setInputText("");
					gameStarted = true;
				}else if(input.equalsIgnoreCase("co") && gameStarted == false){
					request = countries();
					outputPanel.setInputText("");
					gameStarted = true;
				}else if(input.equalsIgnoreCase("quit")){
					sock.close();
					out.close();
					in.close();
					System.exit(0);
				}else if(gameEntry == false){
					request = start();
					outputPanel.setInputText("");
					gameEntry = true;
				}else if(gameStarted == false){
					outputPanel.setInputText("");
					request = info();
					request.put("Name", input);
					request.put("points",points);
					username = input;
					points = 0;
					outputPanel.setPoints(0);
				}else if(input.length() > 1 && gameStarted == true){
					outputPanel.setInputText("");
					request = guessWord();
					request.put("guess",input);
					System.out.println(request.get("guess"));
				}else if (gameStarted == true){
					outputPanel.setInputText("");
					request = guessLetter();
					request.put("guess",input);
				}else{
					outputPanel.appendOutput("Error: Input is not valid. Try again!");
				}


				//Reading in from server
				if (request != null) {
					NetworkUtils.Send(sock, address, port, JsonUtils.toByteArray(request));
          			NetworkUtils.Tuple responseTuple = NetworkUtils.Receive(sock);
					JSONObject response = JsonUtils.fromByteArray(responseTuple.Payload);

					if (response.has("error")) {
					  System.out.println(response.getString("error"));
					} else {
						String type = response.getString("type");
					  switch (response.getInt("datatype")) {
						case (11): { //if response is an image
							Base64.Decoder decoder = Base64.getDecoder();
							byte[] bytes = decoder.decode(response.getString("data"));
							
							try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
							  picturePanel.insertImageBytes(bais, 0, 0);
							  System.out.println("Your image");
							}

							if(type.equalsIgnoreCase("imageCity")){
								answer = response.getString("name");
							}else if(type.equalsIgnoreCase("imageCountry")){
								answer = response.getString("name");
							}
							for(int i = 0; i < answer.length(); i++){
								blanks.add("_");
							}	

							String blanksString = "";
							for(int i = 0; i < blanks.size(); i++){
								String x = blanks.get(i);
								blanksString += x;
							}
							outputPanel.setBlanks(blanksString);
							outputPanel.appendOutput("You can guess with one letter, guess with a word, or type 'quit'");
							break;
						  }
						case (10): //if response is a string
							System.out.println("Your type is " + response.getString("type"));
							System.out.println(response.getString("data"));
							outputPanel.appendOutput("");
							

							int newPoint;

							//Setting UI if leaderboard is chosen,
							if(type.equalsIgnoreCase("leaderboard")){
								outputPanel.appendOutput(response.getString("data"));
								outputPanel.appendOutput(response.getString("Your choices: leaderboard(l), cities(ci), countries(co), (quit)"));
							
							//Setting UI for blanks and recieving evaluation of input from server. Server provides points.
							} else if(type.equalsIgnoreCase("guessLetter")){
								
								String blanksString = "";
								newPoint = response.getInt("points");
								if(newPoint == 1){
									points+=1;
									char x = 'A';
									String guess = response.getString("letter");
									for(int i = 0; i < guess.length(); i++){
										x = guess.charAt(i);
									}
									for(int i = 0; i < answer.length(); i++){
										if(x == (answer.charAt(i))){
											System.out.println(answer.charAt(i));
											blanks.set(i, guess);
										}
									}
								}else if(newPoint == 0){
									points-=1;
								}
								if(points <= 0){
									outputPanel.appendOutput("You Lost! Your choices: leaderboard(l), cities(ci), countries(co), (quit)");
									insertImage("img/lose.jpg", 0, 0);
									gameEntry = false;
									gameStarted = false;
									blanksString = "";
									blanks.removeAll(blanks);
								}
									for(int i = 0; i < blanks.size(); i++){
										String x = blanks.get(i);
										blanksString += x;
									}

								if(answer.equalsIgnoreCase(blanksString)){
									outputPanel.appendOutput("You Won! Your choices: leaderboard(l), cities(ci), countries(co), (quit)");
									insertImage("img/win.jpg", 0, 0);
									gameEntry = false;
									gameStarted = false;
									blanksString = "";
									blanks.removeAll(blanks);
				
								}

								outputPanel.setBlanks(blanksString);
							} else if(type.equalsIgnoreCase("guessWord")){
								String blanksString = response.getString("word");
								newPoint = response.getInt("points");
								if(newPoint == 5){
									points+=5;
									outputPanel.appendOutput("You Won! Your choices: leaderboard(l), cities(ci), countries(co), (quit)");
									insertImage("img/win.jpg", 0, 0);
									gameEntry = false;
									gameStarted = false;
									outputPanel.setBlanks(blanksString);
									blanks.removeAll(blanks);
								}else if(newPoint == 0){
									points-=5;
									outputPanel.appendOutput("Incorrect guess!");
								}
								if(points <= 0){
									outputPanel.appendOutput("Incorrect guess, You Lost! Your choices: leaderboard(l), cities(ci), countries(co), (quit)");
									insertImage("img/lose.jpg", 0, 0);
									gameEntry = false;
									gameStarted = false;
									blanks.removeAll(blanks);
								}

								System.out.println(points);
							} else {
								outputPanel.appendOutput(response.getString("data"));
							}
							
							outputPanel.setPoints(points);
							
							break;
					  }
					  
					}
				  }
				} while (true);
			
		} catch (Exception e){
			System.out.println(e);
		}

	}



	/**
	 * Key listener for the input text box
	 * 
	 * Change the behavior to whatever you need
	 */
	@Override
	public void inputUpdated(String input) {
		if (input.equals("surprise")) {
			outputPanel.appendOutput("You found me!");
		}
	}


	public static void main(String[] args) throws IOException {
		// create the frame

		try {
			ClientGui main = new ClientGui();
			main.show(true);

		} catch (Exception e) {e.printStackTrace();}



	}
}
