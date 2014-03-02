/* 
* Name: WebServer.java
* Class: CSC 360_
* Date: 03/01/2014
* Written By: Gary Patricelli                      
*/

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

        // Process incoming connection requests
        while (true) 
        {
            // Accept the incoming request
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

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception
    {
        // Construct a 1K buffer to hold bytes on their way to the socket
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copy requested file into the socket's output stream
        while((bytes = fis.read(buffer)) != -1 ) 
        {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName)
    {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html"))
        {
            return "text/html";
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
        {
            return "image/jpeg";
        }
        if (fileName.endsWith(".gif"))
        {
            return "image/gif";
        }
        return "application/octet-stream";
    }
    
    private void processRequest() throws Exception
    {
        // Get a reference to the socket's input and output streams
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

        // Extract the filename from the request line
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken(); // Skip over request, assuming it is GET
        String fileName = tokens.nextToken(); 
        
        // Prepend a '.' indicating the current directory
        fileName = "." + fileName;
 
        // Open the requested file
        FileInputStream fis = null; 
        boolean fileExists = true;
        try
        {
            fis = new FileInputStream(fileName);
        }
        catch (FileNotFoundException e)
        {
            fileExists = false;
        }

        // Construct response
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody =  null;
        if (fileExists) 
        {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
        }
        else
        {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "Content-type: " + "text/html" + CRLF;
            entityBody = "<HTML><HEAD><TITLE>Not Found</TITLE></HEAD>" +
                         "<BODY>Not Found</BODY></HTML>";
        }

        // Send the status line
        os.writeBytes(statusLine);
 
        // Send the content type line
        os.writeBytes(contentTypeLine);

        // Send a blank line indicating the end of the header
        os.writeBytes(CRLF);
 
        // Send the entity body
        if (fileExists)
        {
            sendBytes(fis, os);
            fis.close();
        }
        else
        {
            os.writeBytes(entityBody);
        }

        // Close streams and socket
        os.close();
        br.close();
        socket.close();
    }
}
