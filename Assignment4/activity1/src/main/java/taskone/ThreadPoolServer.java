package taskone;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolServer implements Runnable{

    StringList strings = new StringList();
    private Socket conn;
    private int id;
    protected Thread runningThread = null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(2);
    protected int          serverPort   = 8000;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    
    

    public ThreadPoolServer(int port){
      this.serverPort = port;
    }

    public void run() {
      synchronized(this){
        this.runningThread = Thread.currentThread();
      }
      openServerSocket();
        while(! isStopped()){
          Socket clientSocket = null;
          try {
              clientSocket = this.serverSocket.accept();
          } catch (IOException e) {
              if(isStopped()) {
                  System.out.println("Server Stopped.") ;
                  break;
              }
              throw new RuntimeException(
                  "Error accepting client connection", e);
          }
          this.threadPool.execute(
              new Performer(clientSocket, strings));
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;

      }
      private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8000", e);
        }
      }

      private synchronized boolean isStopped() {
        return this.isStopped;
      }

      public synchronized void stop(){
          this.isStopped = true;
          try {
              this.serverSocket.close();
          } catch (IOException e) {
              throw new RuntimeException("Error closing server", e);
          }
      }
    
      public static void main(String[] args) throws Exception {
        ThreadPoolServer server = new ThreadPoolServer(8000);
        new Thread(server).start();
      }
    
}

