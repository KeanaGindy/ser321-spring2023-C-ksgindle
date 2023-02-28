package example.grpcclient;

import java.security.PublicKey;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.UserDataHandler;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;

public class BlackBookImpl extends BlackBookGrpc.BlackBookImplBase{
    
    
    public LinkedList<Address> bookOfAddresses = new LinkedList<Address>();

    @Override
    public void add(BlackBookWriteRequest req, StreamObserver<BlackBookResponse> responseObserver) {
        System.out.println("Received from client: " + req.getAddress().getName() + " " + req.getAddress().getCity() + " " + req.getAddress().getState());
        BlackBookResponse.Builder response = BlackBookResponse.newBuilder();
        
        Address.Builder addBuilder = Address.newBuilder();
        addBuilder.setName(req.getAddress().getName());
        addBuilder.setCity(req.getAddress().getCity());
        addBuilder.setState(req.getAddress().getState());
        addBuilder.setStreet(req.getAddress().getStreet());
        addBuilder.setPhone(req.getAddress().getPhone());
        
        Address finalAddress = addBuilder.build();
        bookOfAddresses.add(finalAddress);
        System.out.println("Users address has been saved");
        response.setMessage("Users address has been saved");
        response.setIsSuccess(true);
        
        
        BlackBookResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
    
    
    @Override
    public void find(BlackBookSearchRequest req, StreamObserver<BlackBookResponse> responseObserver) {
        System.out.println("Received from client: " + req.getName());
        BlackBookResponse.Builder response = BlackBookResponse.newBuilder();
        
        for(Address e : bookOfAddresses) {
            if(e.getName().equals(req.getName())) {
                System.out.println("user found");
                response.setAddress(e);
            }
        }
        
        
        BlackBookResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
    
    @Override
    public void list(Empty req, StreamObserver<BlackBookResponse> responseObserver) {
        System.out.println("Received from client: request for the list of addresses");
        BlackBookResponse.Builder response = BlackBookResponse.newBuilder();
        
        for(Address z : bookOfAddresses) {
            response.addBook(z);
        }
        
        BlackBookResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
        
    }

        
    
}