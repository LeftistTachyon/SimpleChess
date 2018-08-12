package simplechessserver;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import simplechessserver.ClientCommunication.Handler;

/**
 * The Main class
 * @author Jed Wang
 */
public class SimpleChessServerMain {
    /**
     * The Main Method
     * @param args the command line arguments
     * @throws java.io.IOException if something goes wrong
     */
    public static void main(String[] args) throws IOException {
        System.out.println("The chess server is running.");
        
        MainWindow mw = new MainWindow();
        Handler.setMainWindow(mw);
        
        try(ServerSocket listener = new ServerSocket(ClientCommunication.PORT)) {
            while(true) {
                Handler h = new Handler(listener.accept());
                h.start();
                mw.addHandler(h);
            }
        } catch(BindException be) {
            System.err.println("Cannot start server: " + be.getMessage());
        }
    }
}
