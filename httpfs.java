import java.net.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.io.*;
import java.lang.Integer;

/*
httpfs acts as the continously listening serverSocket which accepts any connection and then creates a seperate thread
to take care of that request. (thus we enable the feature of simultaniously managing multiple requests *bonus feature)
*/
public class httpfs {

    private static boolean isVerbose = false;
    private static boolean isGetRequest = false;
    private static boolean isPostRequest = false;
    private static String portNumber = "8080";
    private static String pathToDir = ".";

    /* 
    main where we create our ServerSocket and listen for requests, creating threads for each request
    */
    public static void main(String[] args) throws IOException {

        if (args.length >=4 || args.length == 0){
            System.err.println("\nEnter \"httpfs help\" to get more information.\n");
            System.exit(1);
        }else{
            cmdParser(args);
        }            

        //here we try and connect our serverSocket to the port, we use a catch try blocks, because the port might be occupied
        //and this would throw an exception
        int portNum = Integer.parseInt(portNumber);
        try (ServerSocket serverSocket = new ServerSocket(portNum)){ 
            System.out.println("Server has been instantiated at port " + portNum);
            while (true) {
                new httpfsThread(serverSocket.accept()).start();
	        }
        } 
        catch (IOException e){
            System.err.println("Could not connect to the port: " + portNumber);
            System.exit(-1);
        }
    }        

    /**
    * This method takes the cmd args and parses them according to the different conditions of the application.
    * @param args an array of the command line arguments.
    */
    public static void cmdParser(String[] args){
        for (int i =0; i<args.length; i++){
            if (args[i].equalsIgnoreCase("-v")){
                isVerbose = true;
            }else if (args[i].equalsIgnoreCase("-p")){
                portNumber = args[i+1];
                i++;
            }else if (args[i].equalsIgnoreCase("-d")){
                pathToDir = (args[i+1]);
                i++;
            }else if (args[i].equalsIgnoreCase("help")){
                help();
            }
        }
    }

    /**
    * Prints the help menu.
    */
    public static void help(){
        String help = "\nhttpfs is a simple file server.\n" 
                +"\nUsage: httpfs [-v] [-p PORT] [-d PATH-TO-DIR]\n\n"
                +"  -v  Prints debugging messages.\n"
                +"  -p  Specifies the port number that the server will listen and serve at."
                +"Default is 8080.\n"
                +"  -d  Specifies the directory that the server will use to read/write"
                +"requested files. Default is the current directory when launching the application.\n";

        System.out.println(help);
        System.exit(0);
    }

    /*
    httpfsThread is a thread created by httpfs when a connection is accepted. In the thread we will 
    */
    private static class httpfsThread extends Thread {

        private Socket socket = null;
        static List<String> fileData = new ArrayList<>();
        static List<String> filePath = new ArrayList<>();
        static PrintWriter out=null;
        static BufferedReader in=null;

        /* 
        Calls the super() to allocate a new thread and direct its private socket.
        */
        public httpfsThread(Socket socket) {
            super();
            this.socket = socket;
            try{
                out = new PrintWriter(this.socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            }
            catch( Exception e){
                System.out.println("error creating the thread");
            }
        }
        /*
        run function is called when hhtpfsThreads are created and start() is used on them.
        */
        public void run() {
            try{
                System.out.println("inside a new thread");
                messageParser();
                socket.close();
                System.out.println("    thread completed\n");
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }


        //this needs to be modified to parse the message received from the client
        public void messageParser(){
            //gives us what the first lign of the message is
            //determines if its a get or post
            try{
                String line = in.readLine();
                String temp ="";
                //System.out.println(line);
                if(line.contains("GET ")){
                    String[] tokens=line.split(" ");
                    String path=tokens[1];
                    //System.out.println(path);
                    String[] tokens_2=path.split("/");
                    for(int i = 0; i<tokens_2.length;i++){
                        filePath.add(tokens_2[i]);
                    }
                    get();
                  //System.out.println("get done");
                }
                else if(line.contains("POST ")){
                    
                }
                else{
                    System.out.println("wrong format .... request dropped");
                }
                socket .close();
            } 
            catch(Exception e){
                System.out.println("error in the message parser");
            }
        
        }
        /**
         * get method that returns the content of the file
         */
        public void get(){

            //call secureAccess() 

            //if no parameter 
            if(filePath.size()==0){
                //return content of the current directory
                File directory = new File("./");
                File[] contentOfDirectory = directory.listFiles();
                int size=contentOfDirectory.length;
                out.write("HTTP/1.0 200 OK\r\n");
                out.write("Content-Length: "+size+"\r\n");
                out.write("\r\n");
                for (File object : contentOfDirectory) {
                    if(object.isFile()){
                        out.write("File name : "+object.getName()+"\n");
                    }
                    else if(object.isDirectory()){
                        out.write("Directory name : "+object.getName()+"\n");
                    }
                }
                out.flush();
            }
            else{
                //call checkIfFileExist()
                    //return appropriate error message
                //else:
                    //check for multiple reader / synchronization shit
                    //otherwise openFile()
            }
                
            //terminate thread
            //keep waiting for another request
        }

        /**
         * post method that writes to the specified file
         */
        public void post(String pathToDir){

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
        public void secureAccess(String pathToDir){

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
        public boolean checkIfFileExist(String pathToDir){
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

}

