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
        //CmdParser()
            //do all the shit
            

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
    public static void help(){
        String help = "\nhttpfs is a simple file server.\n" 
                +"Usage: httpfs [-v] [-p PORT] [-d PATH-TO-DIR]\n\n\n"
                +"\t-v Prints debugging messages.\n"
                +"\t-p Specifies the port number that the server will listen and serve at."
                +"Default is 8080.\n"
                +"\t-d Specifies the directory that the server will use to read/write"
                +"requested files. Default is the current directory when launching the application.";

        String help_get = "\nhttpfs help get\n";
                // +"\nusage: httpc get [-v] [-h key:value] URL\n"
                // +"\nGet executes a HTTP GET request for a given URL.\n"
                // +"\n-v Prints the detail of the response such as protocol, status, and headers.\n"
                // +"-h key:value Associates headers to HTTP Request with the format 'key:value'.\n";

        String help_post = "\nhttfs help post\n";
                // +"\nusage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                // +"\nPost executes a HTTP POST request for a given URL with inline data or from file.\n"
                // +"\n-v Prints the detail of the response such as protocol, status, and headers.\n"
                // +"-h key:value Associates headers to HTTP Request with the format 'key:value'.\n"
                // +"-d string Associates an inline data to the body HTTP POST request.\n"
                // +"-f file Associates the content of a file to the body HTTP POST request.\n"
                // +"\nEither [-d] or [-f] can be used but not both.\n";

        if (isPostRequest){
            System.out.println(help_post);
            System.exit(0);
        }else if (isGetRequest){
            System.out.println(help_get);
            System.exit(0);
        }else{
            System.out.println(help);
            System.exit(0);
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
            //read the message
            //call messageParser() which calls the appropriate get() or post()

            socket.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    //this needs to be modified to parse the message received from the client
    public static void messageParser(String message){
      
    }
    /**
     * get method that returns the content of the file
     */
    public static void get(String pathToDir){

        //call secureAccess() 
        //if no parameter 
            //return content of the current directory
        //else:
            //call checkIfFileExist()
                //return appropriate error message
            //else:
                //check for multiple reader / synchronization shit
                //otherwise openFile()
        //terminate thread
        //keep waiting for another request
    }

    /**
     * post method that writes to the specified file
     */
    public static void post(String pathToDir){

        //call secureAccess() 
        //go to the directory
        //check if file exists, 
                //if file doesn't... create it, 
                //otherwise overwrite=true|false(need more discussion)
        //call openFileAndPerformOperation()
        //check for the multiple writers / synchronization shit
        //terminate thread
        //keep waiting for another request
    }

    /**
     * this method is to check if the pathToDir is not outside the file server
     * @param pathToDir
     */
    public static void secureAccess(String pathToDir){

        //check if pathToDir is outside of current directory scope
        //if yes
            //send error message and terminate thread in this method
        //else
            // continue in normal order of execution

    }

    /**
     * can be called by get() and post().Check if the file exisit and return true
     * @param pathToDir
     * @return
     */
    public static boolean checkIfFileExist(String pathToDir){
        //this is called after the secureAccess() method
        //we already know if it is a get/post request
        //if the request if post :
            // either create the file or override and return true
        //else:
            //just check (Find out what happens if file exist but there is nothing maybe!!!)
        //return true/false

        return true;

    }

    /**
     * this can be called by get() and post()
     * @param pathToDir
     */
    public void openFileAndPerformOperation(String pathToDir){
        //buffered reader/writer can be defined here only, need not be static variables

        //this is called after the checkIfFileExist()
        //we already know if it is a get/post request
        //based on the type of request open Buffered Reader/Writer and perform realted operations.
        //if post:
            // open file and write to it
        //else:
            //open file and read contents
        //close the Buffered Reader/Writer.
    }
}