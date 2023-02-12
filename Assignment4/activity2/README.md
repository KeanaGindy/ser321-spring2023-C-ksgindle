# Assignment 4 Activity 2
## Description
Demonstrates simple multithreaded Client and Server communication using SocketServer and Socket Classes. The client will be playing a memory matching game.
In this game, the user has 3 options: See leaderboard, Play game, and quit. <br />
If multiple users are connected, they will help each other to play the same game. The leaderboard persists even if the server crashes.
The protocol for the communication uses protobuf. <br />
The response is also a protobuf.
<br />
<br />

The program will indicate what responses it expects from the user. <br />
Moreover, for user input, when guessing tiles, the program is expecting a response like this: 'a-1' or 'b-2'. <br />
The program expects a dash in between the row/column guess. <br />
When the user is prompted with the main menu, the console is expecting an integer response for the menu indicated.
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
All requirements were completed, error handling might be lacking.
