# GRPC Services and Registry

The following folder contains a Registry.jar which includes a Registering service where Nodes can register to allow clients to find them and use their implemented GRPC services. 

- The first service is a timer. Within the timer, the client can create new timers, check timers, stop timers, and list all the timers.
- The second service is a rock, paper, and scissors game. This game opeartes with the user of a computer player. The user can play a game and then check a leaderboard.
- The last service is a blackbook service. This service allows a user to create a new entry into their personal blackbook. An entry contains the name of an entry, the
address of entry, and the phone number. The user can create new entries, and look at all the entries in their 'blackbook'.

Before starting do a "gradle generateProto".

### gradle runRegistryServer
Will run the Registry node on localhost (arguments are possible see gradle). This node will run and allows nodes to register themselves. 

The Server allows Protobuf, JSON and gRPC. We will only be using gRPC

### gradle runNode
Will run a node with a Timer, Rock Paper Scissors, and BlackBook services. The node registers itself on the Registry. You can change the host and port the node runs on and this will register accordingly with the Registry. However, the command to run the node with default values looks like so:

- gradle runNode --Console=plain -q

### gradle runClient
Will run a client which will call the services from the node, it talks to the node directly not through the registry. At the end the client does some calls to the Registry to pull the services, this will be needed later.
The command to run the client with default values and allowing user interaction is:

- gradle runClient -Pauto=0 --console=plain -q

For the command to run with hardcoded data input, no user interaction:

- gradle runClient -Pauto=1 --console=plain -q

### gradle runDiscovery
Will create a couple of threads with each running a node with services in JSON and Protobuf. This is just an example and not needed for assignment 6. 

### gradle testProtobufRegistration
Registers the protobuf nodes from runDiscovery and do some calls. 

### gradle testJSONRegistration
Registers the json nodes from runDiscovery and do some calls. 
