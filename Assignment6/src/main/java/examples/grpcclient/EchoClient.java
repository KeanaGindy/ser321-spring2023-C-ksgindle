package example.grpcclient;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import service.*;
import test.TestProtobuf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.protobuf.Empty; // needed to use Empty

// just to show how to use the empty in the protobuf protocol
    // Empty empt = Empty.newBuilder().build();

/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class EchoClient {
  private final EchoGrpc.EchoBlockingStub blockingStub;
  private final JokeGrpc.JokeBlockingStub blockingStub2;
  private final RegistryGrpc.RegistryBlockingStub blockingStub3;

  private final TimerGrpc.TimerBlockingStub timerStub;
  private final RockPaperScissorsGrpc.RockPaperScissorsBlockingStub rpsStub;
  private final BlackBookGrpc.BlackBookBlockingStub addStub;

  /** Construct client for accessing server using the existing channel. */
  public EchoClient(Channel channel, Channel regChannel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's
    // responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to
    // reuse Channels.
    blockingStub = EchoGrpc.newBlockingStub(channel);
    blockingStub2 = JokeGrpc.newBlockingStub(channel);
    blockingStub3 = RegistryGrpc.newBlockingStub(regChannel);
    timerStub = TimerGrpc.newBlockingStub(channel);
    rpsStub = RockPaperScissorsGrpc.newBlockingStub(channel);
    addStub = BlackBookGrpc.newBlockingStub(channel);
  }

  public void askServerToParrot(String message) {
    ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
    ServerResponse response;
    try {
      response = blockingStub.parrot(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }
    System.out.println("Received from server: " + response.getMessage());
  }

  //Timer Implementation
    public void askServerToStartTimer(String message) {
      TimerRequest request = TimerRequest.newBuilder().setName(message).build();
      TimerResponse response;
      
          try {
            response = timerStub.start(request);
          } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
          }
          System.out.println("Received from server: " + response.getIsSuccess());
          System.out.println(response.getError());
        }

  public void askServerToCheckTimer(String message) {
      TimerRequest request = TimerRequest.newBuilder().setName(message).build();
      TimerResponse response;
      
          try {
            response = timerStub.check(request);
          } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
          }
          System.out.println("Received from server: " + response.getIsSuccess());
          System.out.println("Time elapsed: " + response.getTimer().getSecondsPassed());
        }

  public void askServerToCloseTimer(String message) {
      TimerRequest request = TimerRequest.newBuilder().setName(message).build();
      TimerResponse response;
      
      
          try {
            response = timerStub.close(request);
          } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
          }
          System.out.println("Received from server: " + response.getIsSuccess());
          //System.out.println("User eliminated: " + response.getTimerList());
        }

  public void askServerToListTimers() {
      Empty empt = Empty.newBuilder().build();
      TimerList response;
      
          try {
            response = timerStub.list(empt);
          
          } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
          }
          for(Time z : response.getTimersList()) {
              System.out.println("User: " + z.getName() + " Time: " + z.getSecondsPassed());
          }
          

  }

//Rock Paper Scissors Implementation
  public void askServerToplayeRPS(String name, int move) {
    PlayReq request = PlayReq.newBuilder().build();
    PlayRes response;
    
    if(move == 0) {
        request = PlayReq.newBuilder().setName(name).setPlay(PlayReq.Played.ROCK).build();
    }else if(move == 1) {
        request = PlayReq.newBuilder().setName(name).setPlay(PlayReq.Played.PAPER).build();
    }else if(move == 2) {
        request = PlayReq.newBuilder().setName(name).setPlay(PlayReq.Played.SCISSORS).build();
    }
    try {
        response = rpsStub.play(request);
      } catch (Exception e) {
        System.err.println("RPC failed: " + e.getMessage());
        return;
      }
      System.out.println(response.getMessage());
      System.out.println(response.getError());
  }

  public void askServerToListLeaders() {
    Empty empt = Empty.newBuilder().build();
    LeaderboardRes response;

        try {
          response = rpsStub.leaderboard(empt);
          
        } catch (Exception e) {
          System.err.println("RPC failed: " + e.getMessage());
          return;
        }
        
        for(LeaderboardEntry z : response.getLeaderboardList()) {
            System.out.println("User: " + z.getName() + " \n Wins: " + z.getWins() + " \n Losses: " + z.getLost());
        }
  }

  //Black Book Implementation
    public void askServerToAddAddress(String name, String city, String state, String street, String phone) {
      Address.Builder address = Address.newBuilder();
      address.setName(name).setCity(city).setState(state).setStreet(street).setPhone(phone);
      
      BlackBookWriteRequest request = BlackBookWriteRequest.newBuilder().setAddress(address).build();
      
      BlackBookResponse response;
      
          try {
            response = addStub.add(request);
          } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            return;
          }
          System.out.println("Received from server: " + response.getIsSuccess());
          System.out.println("Received from server: " + response.getMessage());
        }

  public void askServerTofindAddress(String name) {
      BlackBookSearchRequest request = BlackBookSearchRequest.newBuilder().setName(name).build();
      BlackBookResponse response;
      
      try {
          response = addStub.find(request);
        } catch (Exception e) {
          System.err.println("RPC failed: " + e.getMessage());
          return;
        }
      System.out.println("Received from server: ");
      System.out.println(response.getAddress());
      
  }

  public void askServerToListAddress() {
      Empty empt = Empty.newBuilder().build();
      BlackBookResponse response;
      
      try {
        response = addStub.list(empt);
        
        
        
      } catch (Exception e) {
        System.err.println("RPC failed: " + e.getMessage());
        return;
      }
      for(Address z : response.getBookList()) {
          System.out.println("Name: " + z.getName() + " -->City: " + z.getCity() + " -->State: " + z.getState() + " -->Street: " + z.getStreet() + " -->Phone: " + z.getPhone());
      }
  }


  //Part of Given Code

  public void askForJokes(int num) {
    JokeReq request = JokeReq.newBuilder().setNumber(num).build();
    JokeRes response;


    try {
      response = blockingStub2.getJoke(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    System.out.println("Your jokes: ");
    for (String joke : response.getJokeList()) {
      System.out.println("--- " + joke);
    }
  }

  public void setJoke(String joke) {
    JokeSetReq request = JokeSetReq.newBuilder().setJoke(joke).build();
    JokeSetRes response;

    try {
      response = blockingStub2.setJoke(request);
      System.out.println(response.getOk());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void getServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      response = blockingStub3.getServices(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void findServer(String name) {
    FindServerReq request = FindServerReq.newBuilder().setServiceName(name).build();
    SingleServerRes response;
    try {
      response = blockingStub3.findServer(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void findServers(String name) {
    FindServersReq request = FindServersReq.newBuilder().setServiceName(name).build();
    ServerListRes response;
    try {
      response = blockingStub3.findServers(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 7) {
      System.out
          .println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)> <regOn(bool)>");
      System.exit(1);
    }
    int port = 9099;
    int regPort = 9003;
    String host = args[0];
    String regHost = args[2];
    String message = args[4];
    String autoString = args[6];
    try {
      port = Integer.parseInt(args[1]);
      regPort = Integer.parseInt(args[3]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }

    // Create a communication channel to the server, known as a Channel. Channels
    // are thread-safe
    // and reusable. It is common to create channels at the beginning of your
    // application and reuse
    // them until the application shuts down.
    String target = host + ":" + port;
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS
        // to avoid
        // needing certificates.
        .usePlaintext().build();

    String regTarget = regHost + ":" + regPort;
    ManagedChannel regChannel = ManagedChannelBuilder.forTarget(regTarget).usePlaintext().build();
    try {
      EchoClient client = new EchoClient(channel, regChannel);
        
        
        
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      if(autoString.equals("0")) {
          while(true) {
              System.out.println("What service would you like to use today?"); // NO ERROR handling of wrong input here.
              System.out.println("Choose you Services: Timer = 1 || Rock Paper Scissors = 2 || Black Book = 3 || Exit = 4");
              String choice = reader.readLine();
              
              switch (choice) {
              case "1": {
                  System.out.println("Start = a, Check = b, Close = c, List = d, User input e.g 'd'");
                  String choice2 = reader.readLine();
                  
                  switch (choice2) {
                      case "a": {
                          
                          System.out.println("Name of timer to start?");
                          String name = reader.readLine();
                          client.askServerToStartTimer(name);
                          break;
                      }
                      case "b": {
                          
                          System.out.println("Name of timer to check?");
                          String name = reader.readLine();
                          client.askServerToCheckTimer(name);
                          break;
                      }
                      case "c": {
                          
                          System.out.println("Name of timer to close?");
                          String name = reader.readLine();
                          client.askServerToCloseTimer(name);
                          break;
                      }
                      case "d": {
                          client.askServerToListTimers();
                          System.out.println("List returned: ");
                          break;
                      }
                  }
                  break;
              }
              case "2": {
                  System.out.println("Play = a, Leaderboard = b");
                  String choice2 = reader.readLine();
                  
                  switch (choice2) {
                      case "a": {
                          System.out.println("whats your name?");
                          String nameString = reader.readLine();
                          
                          
                          
                          boolean correct = false;
                          String move = "";
                          int count = 0;
                          while(correct == false) {
                              System.out.println("Enter: 0 = rock, 1 = paper, 2 = scissor");
                              move = reader.readLine();
                              count = move.length();
                              
                              for(int i = 0; i < move.length();i++) {
                                  Character temp = move.charAt(i);
                                  if(Character.isDigit(temp)) {
                                      correct = true;
                                  }else {
                                      System.out.println("not a digit");
                                  }
                              }
                          }
                          
                          
                          try {
                              int played = Integer.parseInt(move);
                              client.askServerToplayeRPS(nameString,played);
                              break;
                          }catch(NumberFormatException e) {
                              e.printStackTrace();
                          }
                          break;
                      }
                      case "b": {
                          System.out.println("Leaderboard: ");
                          client.askServerToListLeaders();
                          break;
                      }
                      
                  }
                  break;
              }
              case "3": {
                  System.out.println("Add to Black book = a || Find Entry = b || List Entries = c, User input e.g 'd'");
                  String choice3 = reader.readLine();
                  
                  switch (choice3) {
                      case "a": {
                          System.out.println("Enter name: ");
                          String addNameString = reader.readLine();
                          System.out.println("Enter city: ");
                          String addCityString = reader.readLine();
                          System.out.println("Enter state: ");
                          String addStateString = reader.readLine();
                          System.out.println("Enter street: ");
                          String addStreetString = reader.readLine();
                          System.out.println("Enter phone: ");
                          String addphoneString = reader.readLine();
                          client.askServerToAddAddress(addNameString,addCityString,addStateString,addStreetString,addphoneString);
                          break;
                      }
                      case "b": {
                          System.out.println("Enter the name that you want to find: ");
                          String nameToFind = reader.readLine();
                          client.askServerTofindAddress(nameToFind);
                          break;
                      }
                      case "c": {
                          System.out.println("List of Entries: ");
                          client.askServerToListAddress();
                          break;
                      }
                      
                  }
                  break;
              }
              case "4": {
                  System.exit(0);
                  break;
              }
              }
              
              
              
          }
      }else if(autoString.equals("1")) {
          int count = 0;
          
              
              System.out.println("What service would you like to use today?"); // NO ERROR handling of wrong input here.
              System.out.println("Choose you Services: Timer = 1 || Rock, Paper, Scissors = 2");
              System.out.println("===================================================");
              System.out.println("Timer has been chosen");
              Thread.sleep(5000);
              
              String name = "Ex. Student 1";
              System.out.println("Starting timer for: " + name);
              client.askServerToStartTimer(name);
              Thread.sleep(5000);
              
              String name2 = "Ex. Student 2";
              System.out.println("Starting timer for: " + name2);
              client.askServerToStartTimer(name2);
              Thread.sleep(5000);
              
              System.out.println("Checking timer for " + name);
              client.askServerToCheckTimer(name);
              System.out.println("Checking timer for " + name2);
              client.askServerToCheckTimer(name2);
              
              System.out.println("\n");
              System.out.println("Closing timer for: " + name);
              client.askServerToCloseTimer(name);
              Thread.sleep(10000);
              System.out.println("\n");
              
              System.out.println("Returning list....");
              Thread.sleep(5000);
              System.out.println("List returned: ");
              client.askServerToListTimers();
              Thread.sleep(5000);
              System.out.println("===================================================");
              System.out.println("===================================================");
              System.out.println("===================================================");
              System.out.println("===================================================");
              System.out.println("===================================================");
              System.out.println("Rock Paper Scissors has been chosen");
              Thread.sleep(5000);
              
              String nameString = "Ex. Student 3";
              System.out.println("Playing RPS with " + nameString);
              System.out.println(nameString + " is Playing rock");
              int played = 0;
              client.askServerToplayeRPS(nameString,played);
              Thread.sleep(1000);
              
              String nameString2 = "Ex. Student 4";
              System.out.println(nameString2 + " is Playing paper");
              played = 1;
              client.askServerToplayeRPS(nameString2,played);
              Thread.sleep(1000);
              
              String nameString3 = "Ex. Student 5";
              System.out.println(nameString3 + " is Playing scissors");
              played = 2;
              client.askServerToplayeRPS(nameString3,played);
              Thread.sleep(3000);
              
              System.out.println("Leaderboard: ");
              client.askServerToListLeaders();
              Thread.sleep(3000);
              
              System.out.println("===================================================");
              System.out.println("===================================================");
              System.out.println("===================================================");
              System.out.println("===================================================");
              System.out.println("===================================================");
              Thread.sleep(3000);

              System.out.println("Adding 1 entry.....");
              Thread.sleep(3000);
              System.out.println("Adding name: Ex. Student 1");
              String addNameString = "Ex. Student 1";
              System.out.println("Adding city: Phoenix ");
              String addCityString = "Phoenix";
              System.out.println("Adding state: Arizona ");
              String addStateString = "Arizona";
              System.out.println("Adding street: Flower Field St");
              String addStreetString = "Flower Field St";
              System.out.println("Adding phone: 111-222-3333");
              String addphoneString = "111-222-3333";
              client.askServerToAddAddress(addNameString,addCityString,addStateString,addStreetString,addphoneString);
              Thread.sleep(3000);
              
              System.out.println("Adding 2nd entry.....");
              Thread.sleep(3000);
              System.out.println("Adding name: Ex. Student 2");
              addNameString = "Ex. Student 2";
              System.out.println("Adding city: Laguna Beach");
              addCityString = "Laguna Beach";
              System.out.println("Adding state: California");
              addStateString = "California";
              System.out.println("Adding street: Pacific Coast Highway");
              addStreetString = "Pacific Coast Highway";
              System.out.println("Adding phone: 123-456-78904");
              addphoneString = "123-456-7890";
              client.askServerToAddAddress(addNameString,addCityString,addStateString,addStreetString,addphoneString);
              Thread.sleep(3000);
              
              System.out.println("Searching for : Ex. Student 2 ");
              Thread.sleep(3000);
              String nameToFind = "Ex. Student 2";
              client.askServerTofindAddress(nameToFind);
              Thread.sleep(3000);
              
              System.out.println("Listing all Entries: ");
              Thread.sleep(3000);
              client.askServerToListAddress();
              Thread.sleep(3000);
      }

    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent
      // leaking these
      // resources the channel should be shut down when it will no longer be used. If
      // it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      regChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
