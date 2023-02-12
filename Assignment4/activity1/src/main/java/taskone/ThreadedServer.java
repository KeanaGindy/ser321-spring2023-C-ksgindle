package taskone;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadedServer extends Thread{
    StringList strings = new StringList();
    private Socket conn;
    private int id;
    
    public ThreadedServer(Socket sock, int id, StringList strings) {
      this.conn = sock;
      this.id = id;
      this.strings = strings;
    }
  
    public void run() {
      try {

        // while client hasn't ended

        Performer performer = new Performer(this.conn, strings);
        performer.run();

        // on close, clean up 
        System.out.println("Client " + id + " closed connection.");

        conn.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public static void main(String[] args) throws Exception {
        Socket sock = null;
        int port;
        int id = 0;
        StringList strings = new StringList();

        if (args.length != 1) {
            // gradle runServer -Pport=9099 -q --console=plain
            System.out.println("Usage: gradle runServer -Pport=9099 -q --console=plain");
            System.exit(1);
        }
        port = -1;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be an integer");
            System.exit(2);
        }
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server Started...");
        while (true) {
            System.out.println("Accepting a Request...");
            sock = server.accept();

            ThreadedServer myServerThread = new ThreadedServer(sock, id++, strings);
            myServerThread.start();

            
        }
    }
}
