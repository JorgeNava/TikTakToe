TikTakToe

This is a Tic Tac Toe game implemented over a distributed system.
The system is intended to work with two servers (player screens) and two clients (remotes).
The way the communication is stablished is via Sockets and ServerSockets, making use of extra threads for the listening and acception of new clients.
Once an operation is selected from the remotes the order is sent to the correspondant server, this server then procces the data and re-sends the order
to the oppononets server to synch players.
Servers need to be configured with its own port (where theyre going to be listening to the client ports) and with the port of the opponents server so it can
send the data.
Remotes need to be configured with the port of the player server.
