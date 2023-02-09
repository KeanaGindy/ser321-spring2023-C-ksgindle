# Assignment 4 Activity 1
## Description
The Performer has 6 functions, 
```
add: adding strings to an array, 
clear: clearing the entire array,
find: finding a string at a specified index,
display: displaying the entire list,
sort: sorting the list alphabetically,
prepend: adding a string to the beginning of a string at the specified index
```
Additionally, the user can quit. 
When running the server, it can be decided whether the server is single threaded, multithreaded and unbounded, or multithreaded and bounded. The bounded server defaults to bound two threads.

The program will specify to the user what type of input it is expecting. To choose one
of the performer's functions, the user should input a number 0-6 depending on what they want to do. From there, the performer will indicate the input necessary for that task.

## Protocol

### Requests
request: { "selected": <int: 1=add, 2=clear, 3=find, 4=display, 5=sort, 6=prepend
0=quit>, "data": <thing to send>}

  add: data <string> -- a string to add to the list, this should already mostly work
  clear: data <> -- no data given, clears the whole list
  find: data <string> -- display index of string if found, else -1
  display: data <> -- no data given, displays the whole list
  sort: data <> -- no data given, sorts the list
  prepend: data <int> <string> -- index and string, prepends given string to string at index

### Responses

success response: {"ok" : true, type": <String> "data": <thing to return> }


type <String>: echoes original selected from request
datatyoe<int>: echoes original inputted number from request
data <string>: 
    add: return current list
    clear: return empty list
    find: return integer value of index where that string was found or -1
    display: return current
    sort: return current list
    prepend: return current list


error response: {"ok" : false, "message"": <error string> }
error string: Should give good error message of what went wrong


## How to run the program
### Terminal
Please use the following commands:
```
    For Server, run "gradle runServer -Pport=9099 -q --console=plain"
    For Server (single thread), run "gradle runTask1"
    For Server (multithreaded, unbounded), run "gradle runTask2"
    For Server (multithreaded, bounded), run "gradle runTask3"
```
```   
    For Client, run "gradle runClient"
```   
## Screencast Link

## Requirements Completed
All requirements were completed.

