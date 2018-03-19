package simplestestestestestest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

/**
 * The class which communicates between this and the server
 * @author Jed Wang
 */
public class ServerCommunication {
    /**
     * The socket connection into this
     */
    private BufferedReader in;
    
    /**
     * The socket connection out of this
     */
    private PrintWriter out;
    
    /**
     * Creates a new ServerCommunication
     * @throws IOException if something goes wrong
     */
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
