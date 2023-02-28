package example.grpcclient;

import java.util.LinkedList;
import java.util.Random;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import service.*;
import com.google.protobuf.Empty;

public class RockPaperScissorsImpl extends RockPaperScissorsGrpc.RockPaperScissorsImplBase{

    LinkedList<LeaderboardEntry> leaderBoard = new LinkedList<LeaderboardEntry>();
    @Override
    public void play(PlayReq req, StreamObserver<PlayRes> responseObserver) {
        PlayRes.Builder response = PlayRes.newBuilder();
        Random random = new Random();
        int wins;
        LeaderboardEntry.Builder entry = LeaderboardEntry.newBuilder();
        entry.setName(req.getName());
        
        if(req.getPlayValue() == 0) {
            System.out.println("Received from client: " + req.getName());
            System.out.println("Received from client: " + req.getPlayValue());
            System.out.println("Received from client: " + "ROCK");
            response.setIsSuccess(true);
        }else if(req.getPlayValue() == 1) {
            System.out.println("Received from client: " + req.getName());
            System.out.println("Received from client: " + req.getPlayValue());
            System.out.println("Received from client: " + "PAPER");
            response.setIsSuccess(true);
        }else if(req.getPlayValue() == 2) {
            System.out.println("Received from client: " + req.getName());
            System.out.println("Received from client: " + req.getPlayValue());
            System.out.println("Received from client: " + "SCISSORS");
            response.setIsSuccess(true);
        }
        

        int computerChoice;
        if(req.getPlayValue() == 0) {
            computerChoice = random.nextInt(3);
            if(computerChoice == 0) {
                System.out.println("Computer played: ROCK");
                response.setMessage("Player played: ROCK. BUT so did computer.... DRAW");
            }else if(computerChoice == 1) {
                System.out.println("computer played: PAPER");
                response.setMessage("Player played: ROCK. BUT computer player: PAPER.... YOU LOSE");
                entry.setLost(1);
            }else if(computerChoice == 2) {
                System.out.println("computer played: SCISSORS");
                response.setMessage("Player played: ROCK. BUT computer player: SCISSORS.... YOU WIN");
                entry.setWins(1);
            }
        }else if(req.getPlayValue() == 1) {
            computerChoice = random.nextInt(3);
            if(computerChoice == 0) {
                System.out.println("Computer played: ROCK");
                response.setMessage("Player played: PAPER. BUT the computer player ROCK.... YOU WIN");
                entry.setWins(1);
            }else if(computerChoice == 1) {
                System.out.println("computer played: PAPER");
                response.setMessage("Player played: PAPER. BUT computer player: PAPER.... DRAW!!");
            }else if(computerChoice == 2) {
                System.out.println("computer played: SCISSORS");
                response.setMessage("Player played: PAPER. BUT computer player: SCISSORS.... YOU LOSE");
                entry.setLost(1);
            }
        }else if(req.getPlayValue() == 2) {
            computerChoice = random.nextInt(3);
            if(computerChoice == 0) {
                System.out.println("Computer played: ROCK");
                response.setMessage("Player played: SCISSORS. BUT the computer player ROCK.... YOU LOSE");
                entry.setLost(1);
            }else if(computerChoice == 1) {
                System.out.println("computer played: PAPER");
                response.setMessage("Player played: SCISSORS. BUT computer player: PAPER.... YOU WIN!!");
                entry.setWins(1);
                System.out.println("wins: " + entry.getWins());
            }else if(computerChoice == 2) {
                System.out.println("computer played: SCISSORS");
                response.setMessage("Player played: SCISSORS. BUT computer player: SCISSORS.... DRAW");
            }
        }

        LeaderboardEntry finalEntry = null;
        
        
        finalEntry = entry.build();
        leaderBoard.add(finalEntry);
        System.out.println("new player added");
        //newPlayer = false;
        
        
        
        PlayRes resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
        
    }
    
    @Override
    public void leaderboard(Empty req, StreamObserver<LeaderboardRes> responseObserver) {
        LeaderboardRes.Builder response = LeaderboardRes.newBuilder();
        //response.setLeaderboard(leaderBoard);
        int count = 0;
        for(LeaderboardEntry e : leaderBoard) {
            response.addLeaderboard(e);
            count++;
        }
        
        LeaderboardRes resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
        System.out.println("flaka");
    }
    
    
}
