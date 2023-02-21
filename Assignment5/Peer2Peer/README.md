<h1>PROJECT DESCRIPTION</h1>
 This is a basic peer-2-peer for a chat in which all peers can communicate with each other. 
 In order to start the peer-2-peer, the leader should be started first and they are put in charge of the network; however, the peers can still communicate to each other.

<h1>SCREENCAST</h1>

https://drive.google.com/file/d/1potlk1JKBUgRbn1LXUCRL8Aj7kRZox2A/view?usp=share_link
 

<h1>HOW TO RUN</h1>

*To run the leader (on default ports): gradle runPeer -PisLeader=true -q --console=plain
*To run a peer: gradle runPeer -PpeerName=Keana -Ppeer="localhost:9000" -Pleader="localhost:8000" -q --console=plain
*Running another peer: gradle runPeer -PpeerName=KeanaClone -Ppeer="localhost:1234" -Pleader="localhost:8000" -q --console=plain
