package server;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.*;
import java.lang.*;
import java.lang.reflect.Array;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Logs;
import buffers.RequestProtos.Message;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;

class SockBaseServer implements Runnable {
    static String logFilename = "logs.txt";

    static String leaderBoardFilename = "leader_log.txt";
    static Response.Builder leaderBoard = Response.newBuilder().setResponseType(Response.ResponseType.LEADER);
    Game game;

    Socket clientSocket;
    InputStream in = null;
    OutputStream out = null;

    String[] rowCol = new String[4];
    int[] rowColFinal = new int[4];

    public SockBaseServer(Socket sock, Game game){
        this.clientSocket = sock;
        this.game = game;
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
        } catch (Exception e) {
            System.out.println("Error in constructor: " + e);
        }
    }

    public static void main(String[] args){
        Game game = new Game();
        initLeaderFile();
        initLeaderboard();
        int port = 9099; 
        if (argsCheck(args)) {
            port = getPort(args);
        }
        System.out.println("ThreadServer Started.............");
        ServerSocket serv = connectServerSocket(port);
        while (true) {
            try {
                Socket clientSocket = serv.accept();
                System.out.println("Thread Created.............");
                Runnable serverRunnable = new SockBaseServer(clientSocket, game);
                Thread serverThread = new Thread(serverRunnable);
                serverThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initLeaderFile() {
        try {
            File leaderFile = new File(leaderBoardFilename);
            if (leaderFile.createNewFile()) {
                System.out.println("File created: " + leaderFile.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void initLeaderboard() {
        String line;
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(leaderBoardFilename)); BufferedReader br = new BufferedReader(isr)) {
            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    String[] lineArray = line.split(",");
                    String element1 = lineArray[0];
                    String element2 = lineArray[1];

                    int wins = 0;
                    try {
                        wins = Integer.parseInt(element2);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    addEntry(element1, wins);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static boolean argsCheck(String[] args) {
        if (args.length != 1) {
            System.out.println("Expected arguments: <port(int)>");
            System.exit(1);
        }
        return true;
    }

    private static int getPort(String[] args) {
        int port = -1;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }
        return port;
    }

    private static ServerSocket connectServerSocket(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
        return serverSocket;
    }

    private synchronized static void addEntry(String name, int wins) {
        Entry leader = Entry.newBuilder().setName(name).setWins(wins).build();
        leaderBoard.addLeader(leader);
    }

    // Handles the communication right now it just accepts one input and then is done you should make sure the server stays open
    // can handle multiple requests and does not crash when the server crashes
    // you can use this server as based or start a new one if you prefer. 
    public void run() {
        String name = "";


        System.out.println("Ready...");

        try {
            boolean quit = false;
            while (!quit) {

                Request request;
                Response response;

                if (clientSocket.isConnected()) {
                    request = Request.parseDelimitedFrom(in);
                } else {
                    break;
                }

                switch (request.getOperationType()) {
                    case NAME:
                        System.out.println("operationType: " + request.getOperationType());
                        name = request.getName();
                        writeToLog(name, Message.CONNECT);
                        response = buildGreetingRes(name);
                        break;
                    case LEADER:
                        System.out.println("operationType: " + request.getOperationType());
                        leaderBoard.getLeaderList();
                        response = buildLeaderRes();
                        break;
                    case NEW:
                        System.out.println("operationType: " + request.getOperationType());
                        game.newGame(); 
                        response = buildPlayRes(false, false);
                        break;
                    case TILE1:
                        System.out.println("operationType: " + request.getOperationType());
                        System.out.println("tile: " + request.getTile());
                        String flippedBoard;
                        if (request.hasTile()) {    
                            rowCol = request.getTile().split("-");   
                            if(rowCol[0].equals("a")){
                                rowColFinal[0] = 1;
                            } else if(rowCol[0].equals("b")) {
                                rowColFinal[0] = 2;
                            } else if(rowCol[0].equals("c")) {
                                rowColFinal[0] = 3;
                            } else if(rowCol[0].equals("d")) {
                                rowColFinal[0] = 4;
                            }

                            if(rowCol[1].equals("1")){
                                rowColFinal[1] = 2;
                            } else if(rowCol[1].equals("2")) {
                                rowColFinal[1] = 4;
                            } else if(rowCol[1].equals("3")) {
                                rowColFinal[1] = 6;
                            } else if(rowCol[1].equals("4")) {
                                rowColFinal[1] = 8;
                            }

                            flippedBoard = game.tempFlipWrongTiles(rowColFinal[0],rowColFinal[1]);
                        } else {
                            flippedBoard = game.getBoard();
                        }

                        char tile1, tile2;
                        tile1 = game.getTile(rowColFinal[0], rowColFinal[1]);
                        tile2 = game.getTile(rowColFinal[2], rowColFinal[3]);
                        System.out.println("tile1" + tile1);
                        System.out.println("tile2" + tile2);

                        response = buildPlayRes(flippedBoard,false,true);
                        System.out.println(game.showBoard());

                        break;
                    case TILE2:
                        System.out.println("operationType: " + request.getOperationType());
                        System.out.println("tile: " + request.getTile());

                        if (request.hasTile()) {    
                            rowCol = request.getTile().split("-");   
                            if(rowCol[0].equals("a")){
                                rowColFinal[2] = 1;
                            } else if(rowCol[0].equals("b")) {
                                rowColFinal[2] = 2;
                            } else if(rowCol[0].equals("c")) {
                                rowColFinal[2] = 3;
                            }

                            if(rowCol[1].equals("1")){
                                rowColFinal[3] = 2;
                            } else if(rowCol[1].equals("2")) {
                                rowColFinal[3] = 4;
                            } else if(rowCol[1].equals("3")) {
                                rowColFinal[3] = 6;
                            } else if(rowCol[1].equals("4")) {
                                rowColFinal[3] = 8;
                            }


                            char tile11, tile22;
                            tile11 = game.getTile(rowColFinal[0], rowColFinal[1]);
                            tile22 = game.getTile(rowColFinal[2], rowColFinal[3]);
                            System.out.println("tile1" + tile11);
                            System.out.println("tile2" + tile22);

                        

                            if (tile11 == tile22){
                                game.replaceOneCharacter(rowColFinal[0], rowColFinal[1]);
                                game.replaceOneCharacter(rowColFinal[2], rowColFinal[3]);
                            }

                            game.checkWin();
                            System.out.println(game.getWon());

                            //WIN CASE
                            if(game.getWon() == true){
                                boolean nameExists = false;
                                    List<Entry> leaderList = leaderBoard.getLeaderList();
                                    if (!leaderList.isEmpty()) {                              
                                        for (Entry entry : leaderList) {
                                            if (entry.getName().equalsIgnoreCase(name)) {
                                                String entryName = entry.getName();
                                                int entryWins = entry.getWins();
                                                entryWins += 1;
                                                addEntry(entryName, entryWins);
                                                leaderBoard.removeLeader(leaderList.indexOf(entry)).build();   
                                                writeToLeaderLog(name);
                                                nameExists = true;
                                                break;
                                            }
                                        }
                                        if (!nameExists || leaderList.isEmpty()) {
                                            addEntry(name, 1);
                                            writeToLeaderLog(name);
                                        }
                                    }
                                showLeaderboard();
                                response = buildWonRes();
                                System.out.println("END OF CHECK WIN");
                                writeToLog(name, Message.WIN);
                            //MATCHED TILE
                            } else if (tile11 == tile22){
                                String flippedBoard2 = game.getBoard();
                                response = buildPlayRes(flippedBoard2,true,false);
                            //TILE IS NOT MATCHED
                            } else {
                                String flippedBoard2 = game.tempFlipWrongTiles(rowColFinal[0],rowColFinal[1], rowColFinal[2],rowColFinal[3]);
                                response = buildPlayRes(flippedBoard2,false,false);
                            }

                            System.out.println(game.showBoard());

                        
                        } else {
                            response = buildErrorRes("Error: Row | Column must be inputted like example. \n e.g 'a-1' \n e.g 'b-3'");
                        }
                            break;
                        case QUIT:
                            response = buildByeRes(name);
                            quit = true;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + request);
                    }

                if (response.getResponseType().equals(Response.ResponseType.WON)) {
                    game.setWon();
                }
                response.writeDelimitedTo(out);
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            System.out.println("Client " + name + " connection has been terminated.");
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.out.println("Client connection has been closed");
            }
        }
    }  


    /**
     * Writing a new entry to our log
     * @param name - Name of the person logging in
     * @param message - type Message from Protobuf which is the message to be written in the log (e.g. Connect) 
     * @return String of the new hidden image
     */
    public static void writeToLog(String name, Message message){
        try {
            // read old log file 
            Logs.Builder logs = readLogFile();

            // get current time and data
            Date date = java.util.Calendar.getInstance().getTime();
            System.out.println(date);

            // we are writing a new log entry to our log
            // add a new log entry to the log list of the Protobuf object
            logs.addLog(date.toString() + ": " +  name + " - " + message);

            // open log file
            FileOutputStream output = new FileOutputStream(logFilename);
            Logs logsObj = logs.build();

            // This is only to show how you can iterate through a Logs object which is a protobuf object
            // which has a repeated field "log"

            for (String log: logsObj.getLogList()){

                System.out.println(log);
            }

            // write to log file
            logsObj.writeTo(output);
        }catch(Exception e){
            System.out.println("Issue while trying to save");
        }
    }


    /**
     * Reading the current log file
     * @return Logs.Builder a builder of a logs entry from protobuf
     */
    public static Logs.Builder readLogFile() throws Exception{
        Logs.Builder logs = Logs.newBuilder();

        try {
            // just read the file and put what is in it into the logs object
            return logs.mergeFrom(new FileInputStream(logFilename));
        } catch (FileNotFoundException e) {
            System.out.println(logFilename + ": File not found.  Creating a new file.");
            return logs;
        }
    }


    private synchronized Response buildGreetingRes(String name) {
        return Response.newBuilder().setResponseType(Response.ResponseType.GREETING).setMessage("Hello " + name + " and welcome. Welcome to a simple game of memory matching. ").build();
    }

    private synchronized Response buildLeaderRes() {
        return leaderBoard.build();
    }

    private synchronized Response buildPlayRes(String flippedBoard,boolean eval, boolean tile2request) {
        return Response.newBuilder().setResponseType(Response.ResponseType.PLAY)
        .setBoard(game.getBoard()) // gets the hidden board
        .setFlippedBoard(flippedBoard)
        .setEval(eval)
        .setSecond(tile2request)
        .build();
    }

    private synchronized Response buildPlayRes(boolean eval, boolean tile2request) {
        return Response.newBuilder().setResponseType(Response.ResponseType.PLAY)
        .setBoard(game.getBoard()) // gets the hidden board
        .setEval(eval)
        .setSecond(tile2request)
        .build();
    }

    public synchronized static void writeToLeaderLog(String name) {

        try {
            BufferedReader file = new BufferedReader(new FileReader(leaderBoardFilename));
            StringBuilder inputBuffer = new StringBuilder();
            String line;

            boolean found = false;
            while ((line = file.readLine()) != null) {
                if (line.contains(name)) {
                    String[] contents = line.split(",");
                    int currWins = Integer.parseInt(contents[1]);
                    currWins += 1;
                    line = contents[0] + "," + currWins;
                    found = true;
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }

            if (!found) {
                while ((line = file.readLine()) != null) {
                    inputBuffer.append(line);
                    inputBuffer.append('\n');
                }
                inputBuffer.append(name).append(",1");
            }
            file.close();
            FileOutputStream fileOut = new FileOutputStream(leaderBoardFilename);
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }

    }

    private synchronized void showLeaderboard() {
        for (Entry lead : leaderBoard.getLeaderList()) {
            System.out.println(lead.getName() + ": " + lead.getWins());
        }
    }

    private synchronized Response buildWonRes() {
        return Response.newBuilder().setResponseType(Response.ResponseType.WON).setMessage("All tiles have been matched!!!").build();
    }

    private synchronized Response buildErrorRes(String errorMessage) {
        return Response.newBuilder().setResponseType(Response.ResponseType.ERROR).setMessage(errorMessage).build();
    }

    private synchronized Response buildByeRes(String name) {
        return Response.newBuilder().setResponseType(Response.ResponseType.BYE).setMessage("Goodbye " + name).build();
    }


    
}

