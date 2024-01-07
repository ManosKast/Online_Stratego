package Controller;

public class Client {
    /*
    static Socket socket;
    static Packet message;
    static AtomicBoolean waitingResponse = new AtomicBoolean(false);

    public Client(String address, Packet packet, MouseListener gameHandler, MouseListener reviveHandler) {
        try {
            message = packet;
            socket = new Socket(address, 1234);
            Controller.Client.UserThread userThread = new Controller.Client.UserThread();
            Controller.Client.ServerThread serverThread = new Controller.Client.ServerThread();

            userThread.start();
            serverThread.start();

            userThread.join();
            serverThread.join();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e){
            System.err.println("Interrupted: " + e.getMessage());
            System.exit(1);
        }
    }
    static class UserThread extends Thread {
        Queue<String> messagesQueue = new LinkedList<>();
        BufferedReader stdIn;
        PrintWriter out;
        String header;
        String userInput;
        MouseListener gameHandler;
        MouseListener reviveHandler;

        UserThread(MouseListener gameHandler, MouseListener reviveHandler){
            super();
            //TODO: Error handling for null.
            this.gameHandler = gameHandler;
            this.reviveHandler = reviveHandler;
        }

        public void run() {
            try {
                stdIn = new BufferedReader(new InputStreamReader(System.in));
                out = new PrintWriter(Client.socket.getOutputStream(), true);
                while (waitingUserInput()) {
                    if (canRequest()) {
                        // TODO: Send to server game command.
                        if (userInput.equals("1")){
                            header = Client.message.generatePacket((byte) 1, false);
                            //out.println(header);
                            //continue;
                        }
                        else if (userInput.equals("2")){
                            header = Client.message.generatePacket((byte) 2, false);
                            //out.println(header);
                            //continue;
                        }
                        else if (userInput.equalsIgnoreCase("exit")) {
                            header = Client.message.generatePacket((byte) 9, false);
                            //out.println(header);
                            //continue;
                        }
                        out.println(header);
                        Client.waitingResponse.set(true);
                    } else {
                        // TODO: Add in queue. Also if can request and queue contains items, then query.
                        System.out.println("Waiting for server response...");
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading user input: " + e.getMessage());
                System.exit(1);
            }
        }

        private boolean waitingUserInput() throws IOException {return (userInput = stdIn.readLine()) != null;}
        private boolean canRequest() {return !Client.waitingResponse.get();}
    }

    static class ServerThread extends Thread {
        BufferedReader in;
        String serverInput;
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(Client.socket.getInputStream()));
                while(waitingServerInput()){
                    // TODO: Act accordingly on View package.
                    System.out.println("Echo: " + serverInput);
                    Packet server = Packet.convertMessageToPacket(serverInput);

                    switch(server.getProtocol()) {
                        case Protocols.WELCOME:
                            if(server.serverAssignedID()) message.setPlayerID(server); break;
                    }
                    Client.waitingResponse.set(false);
                }
            } catch (IOException e) {
                System.err.println("Error reading server input: " + e.getMessage());
                System.exit(1);
            }
        }

        private boolean waitingServerInput() throws IOException {return (serverInput = in.readLine()) != null;}

    }*/
}
