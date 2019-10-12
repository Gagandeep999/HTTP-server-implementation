import java.net.*;
import java.io.*;

/*
httpfs acts as the continously listening serverSocket which accepts any connection and then creates a seperate thread
to take care of that request. (thus we enable the feature of simultaniously managing multiple requests *bonus feature)
*/

public class httpfs {

    /* 
    main where we create our ServerSocket and listen for requests, creating threads for each request
    */
    public static void main(String[] args) throws IOException {

    if (args.length >=4){
        System.err.println
            ("Usage: "+
             "\n     httpfs [-v] [-p PORT] [-d PATH-TO-DIR]");
        System.exit(1);
    }
        //
        // check the modes that are activated here leaving only the port number left in the args
        //

        //if the port number is not specified, we use the default port
        if(args.lenght=0){
            int portNumber = 8080;
        }
        else{
            int portNumber = Integer.parseInt(args[0]);
        }

        boolean active = true;
        //here we try and connect our serverSocket to the port, we use a catch try blocks, because the port might be occupied
        //and this would throw an exception
        try (ServerSocket serverSocket = new ServerSocket(portNumber)){ 
            while (active) {
	            new httpfsThread(serverSocket.accept()).start();
	        }
        } 
        catch (IOException e){
            System.err.println("Could not connect to the port: " + portNumber);
            System.exit(-1);
        }
    }
}

/*
httpfsThread is a thread created by httpfs when a connection is accepted. In the thread we will 
*/
public class httpfsThread extends Thread {

    private Socket socket = null;

    /* 
    Calls the super() to allocate a new thread and direct its private socket.
    */
    public httpfsThread(Socket socket) {
        super("httpfsThread");
        this.socket = socket;
    }
    /*
    run function is called when hhtpfsThreads are created and start() is used on them.
    */
    public void run() {
        try( PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) 
        {

            //do everything. call all the methods.

            socket.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}