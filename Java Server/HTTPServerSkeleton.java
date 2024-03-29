import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class HTTPServerSkeleton {
    static final int PORT = 5075;
    
    public static void main(String[] args) throws IOException {
        
        ServerSocket serverConnect = new ServerSocket(PORT);
        System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

        while(true)
        {
            Socket s = serverConnect.accept();
            Thread requestHandler = new RequestHandler(s, 1);
            requestHandler.start();
        }
    }
    
}
