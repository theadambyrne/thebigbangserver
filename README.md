# The Big Bang Server

## Description
This project is a command based groupchat application that uses Sockets, Threads and Pipes. It includes many small feature such as /joke and /panic. 

## Architecture
This project uses a Client-Server architecture with a single server and multiple clients. The server is responsible for handling all the clients and their requests. The server uses a thread pool to handle multiple clients at once. The server also uses a pipe to communicate with the client. The client is responsible for sending requests to the server and receiving responses. The client uses a thread to listen for responses from the server. The client also uses a pipe to communicate with the server.

## Features
- Clients are free to join server, provided they know the server's IP address and port number.
- Clients can send messages to the server, which will be broadcasted to all other clients.
- Clients are identified by a nickname associated with their socket.
- Clients can send commands to the server, which will be executed by the server.

## Commands
- /joke - The server will send a random joke to the client using an API.
- /panic - The server will clear all client's output.
- /list - The server will send a list of all the clients connected to the server.
- /clear - The server will clear the client's output.
- /quit - The client will disconnect from the server.
- /help - The server will send a list of all the commands to the client.

## Installation
```bash
git clone https://github.com/theadambyrne/thebigbangserver.git
cd thebigbangserver

# install dependencies
mvn clean install

# run tests
mvn test
```

## Usage
```bash
# generate executable jar files
mvn clean package

# run server and client
java -jar target/TBBS-server-1.jar
java -jar target/TBBS-client-1.jar
```