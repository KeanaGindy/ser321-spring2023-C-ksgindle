# Assignment 4 Activity 1
## Description
Demonstrates simple multithreaded Client and Server communication using SocketServer and Socket Classes. The client will be playing a memory matching game.
In this game, the user has 3 options: See leaderboard, Play game, and quit. <br />
If multiple users are connected, they will help each other to play the same game. The leaderboard persists even if the server crashes.
The protocol for the communication uses protobuf. <br />
The response is also a protobuf.

## Protocol

The protocol is defined in the PROTOCOL.md file. <br />
Specific Protocol for requests and responses are listed in the proto folder. 

## How to run the program
```
    For Server, run "gradle runServer -Pport=9099" (port can be changed)
    Alternatively, For Server, run "gradle runServer"
```
```   
    For Server, run "gradle runServer -Phost='localhost'-Pport=9099" (Host and port can be changed)
    Alternatively, For Client, run "gradle runClient"
```   
``` 
    Defaults for gradle arguments are 'localhost' and port 9099.
```

## Screencast Link

https://drive.google.com/file/d/1ZFk1IytLiujwG3oY2Z-WChNGYvxkq7T8/view?usp=share_link

## Requirements Completed
All requirements were completed.
