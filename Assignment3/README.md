## Description & Purpose:

  Picture guessing game similar to hangamn that utilization a server/client model that adheres to the TCP protocl. Upon entering, the user inputs their name
  and is given three options:
  - Show leaderboard
  - Guess city
  - Guess country
  
  To guess, the user can input a single letter or a whole word guess. The server will supply the user with points accordingly. The user has the option to 
  execute the server with a designated port value. If no port is chosen, a default port of 8000 will be used. Simiarly, the user can execute the client with
  custom host and port arguments or the default port and host of 8000 and 'localhost' will be chosen.

## Requirements Checklist:
- [x] i.
- [x] ii.
- [x] iii.
- [x] iv.
- [ ] v.
- [x] vi.
- [x] vii.
- [x] viii.
- [x] ix.
- [x] x
- [x] xi.
- [x] xii.
- [x] xiii.
- [x] xiv.
- [x] xv.
- [x] xvi.
- [x] xvii.
- [x] xviii.
- [x] xix.
- [x] xx.
- [ ] xxi.

## How to run (Can run w/o args):
- gradle runServerTCP -Pport=8000 OR runServerTCP
- gradle runClientTCP -Pport=8000 -Phost='localhost' OR runClientTCP
- gradle runServerUDP -Pport=8000 OR runServerUDP
- gradle runClientUDP -Pport=8000 -Phost='localhost' OR runClientUDP


## UML Diagram:
  Uploaded in github repo, showcases sequence diagram for when the user is given menu options.

## Protocol Description:
### Client asks server.
Request:
- {"selected": <String>} (String can be 'l', 'ci', or 'co')
 
 Response (if image):
- {"type": <String>, "datatype": int, "name": <String>, "data": byte[]}
 
 Response (if string):
- {"datatype": int, "type": <String>, "data": <String>}
 
 Error Response:
- {"error", err}
### Client guessing.
Request:
- {"selected": <String>} (String can be 'guessLetter' or 'guessWord')
 
Response for letter guess: (data is user's guess) (letter is also user's guess)
- {"datatype": int, "type": <String>, "data": <String>, "letter": <String>, "points": int}
 
Response for word guess: (data is user's guess) (letter is also user's guess)
- {"datatype": int, "type": <String>, "data": <String>, "word": <String>, "points": int}
 
 Error Response:
- {"error", err}

## Video Demonstration:
  https://drive.google.com/file/d/1AoJyLQb5izjoYO7Tg4uXpeKpLHVL8s9t/view?usp=sharing
  


## How the program is robust:
  
  The program is made robust from the client/server execution. If no arguments are inputted, default arguments are used.
  During execution, if a user inputs the incorrect input, the server will response accordingly with error protocl or the
  client will respond. The protocol uses headers accordingly. FOr instance, the header for errorr is simply 'error' to 
  aid readibility. Lastly, try/catch blocks with correct error handling are implemented in the best-suited areas of code.
  
  
  
  
