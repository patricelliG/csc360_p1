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
            // Accept the incomming request
            Socket clientSocket = serverSocket.accept();
             
            // Construct an object to process the HTTP request message.
            HttpRequest request = new HttpRequest(clientSocket);

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
        try
        {
            processRequest();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }
    
    private void processRequest() throws Exception
    {
        // Get a referece to the socket's input and output streams
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream()); 

        // Set up input stream filters
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        // Get the request line of the HTTP request message
        String requestLine = br.readLine();

        // Display the request line
        System.out.println();
        System.out.println(requestLine);

        // Get and display header lines
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0 ) 
        {
            System.out.println(headerLine);
        }
        
        // Close streams and socket
        os.close();
        br.close();
        socket.close();
    }
}
