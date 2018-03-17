package simplestestestestestest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class ServerCommunication {
    /**
     * The socket connection into this
     */
    private BufferedReader in;
    
    /**
     * The socket connection out of this
     */
    private PrintWriter out;
    
    public ServerCommunication() throws IOException {
        run();
    }
    
    private void run() throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.print("Server address: ");
        String serverAddress = input.nextLine();
        Socket socket;
        try {
            socket = new Socket(serverAddress, 9001);
        } catch (ConnectException cexp) {
            System.out.println("Connection error: " + cexp.getMessage());
            System.exit(0);
            return;
        }
        
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        GUI gui = GUI.run(out);
        
        while(true) {
            String line = in.readLine();
            if(line == null) {
                return;
            }
            gui.println("SERVER: " + line);
        }
    }
}
