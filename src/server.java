import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer
{
    public static void main(String argv[]) throws Exception
    {
        // Set port number
        int port = 3333;

        // Establish the listen socket
        ServerSocket serverSocket = new ServerSocket(port);

        // Process incomming connection requests
        while (true) 
        {
            Socket clientSocket = serverSocket.accept();
             
            // Construct an object to process the HTTP request message.
            HttpRequest = request = new HttpRequest(clientSocket);

            // Create a new thread to handle this request
            Thread thread = new Thread(request);

            // Start the thread
            thread.start();
        }
    }
}

final class HttpRequest implements Runnable
{
    final static String CRLF = "\r\n";
    Socket socket;

    // Constructor
    public HttpRequest(Socket socket) throws Exception
    {
        this.socket = socket;
    }

    // Implement the run() method of the Runnable interface
    public void run()
    {

    }
    
    private void processRequest() throws Exception
    {

    }
}
