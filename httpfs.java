import java.net.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.io.*;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;

/*
httpfs acts as the continously listening serverSocket which accepts any connection and then creates a seperate thread
to take care of that request. (thus we enable the feature of simultaniously managing multiple requests *bonus feature)
*/
public class httpfs {

    private static boolean isDebugging = false;
    private static String portNumber = "8080";
    private static String pathToDir = ".";

    /* 
    main where we create our ServerSocket and listen for requests, creating threads for each request
    */
    public static void main(String[] args) throws IOException {

        if (args.length >=5){
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
    private static class httpfsThread extends Thread{

        private Socket socket = null;
        static List<String> fileData = new ArrayList<>();
        static List<String> filePath = new ArrayList<>();
        static PrintWriter out=null;
        static BufferedReader in=null;
        private static boolean isGetRequest = false;
        private static boolean isPostRequest = false;

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
            try{
                String line = in.readLine();
                //System.out.println(line);
                if(line.contains("GET ")){
                    isGetRequest=true;
                    String[] tokens=line.split(" ");
                    String path="."+tokens[1];
                    checkIfFileExist(path);
                    get(path);
                }
                else if(line.contains("POST ")){
                    isPostRequest=true;
                    String[] tokens=line.split(" ");
                    String path="."+tokens[1];
                    String body="";
                    while(true) {
                        //do nothing until we hit the body delimiter (assignment doesnt really care for that
                        line = in.readLine();
                        System.out.println("line is: "+line);
                        if(line.equals("")){ //hit the delimiter
                            System.out.println("before break");
                            break;
                        }
                    }
                    while(true) {
                        line = in.readLine();
                        if(line.equals("")){
                            // System.out.println("hit the selimiter"); //hit the delimiter
                            break;
                        } 
                        body = body.concat(line+"\n"); 
                    }
                    //System.out.println(body);
                    checkIfFileExist(path);
                    post(path,body);
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
        public void get(String path){
            //call secureAccess() 
            //if no parameter 
            //System.out.println(path);
            if(path.equals("./")){
                //return content of the current directory
                File directory = new File(pathToDir);
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
                    //check for multiple reader / synchronization shit
                    openFileAndPerformOperation(path);
            }
        }

        /**
         * post method that writes to the specified file
         */
        public void post(String path, String body){

            //call secureAccess() 
            openFileAndPerformOperation(path,body);
            //check for the multiple writers / synchronization shit
        }

        /**
         * this method is to check if the pathToDir is not outside the file server
         * @param pathToDir
         */
        //do this by checking of th epath is a child to the current directory.
        public void secureAccess(String path){

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
        //
        //create also the folders
        //
        public void checkIfFileExist(String path){
            //this is called after the secureAccess() method
            //we already know if it is a get/post request
            //if the request if post :
            if(isGetRequest){
                File tempFile = new File(path);
                boolean exists = tempFile.exists();
                if(!exists){
                    out.write("HTTP/1.0 404 Not Found\r\n\r\n");
                    out.flush();
                   try{
                       socket.close();
                   }catch(Exception e){
                        System.out.println("error in the checkFileExist");
                   }
                }
            }
            if(isPostRequest){
                // either create the file or override and return true
                int index = path.lastIndexOf("/");
                String directories = path.substring(0, index);
                File file= new File(directories);
                boolean exists = file.exists();
                //create them if one of the folders doesnt exist.
                if(!exists){
                    file.mkdirs();
                }
                File textFile = new File(path);
                //Create the file
                try{
                 textFile.createNewFile();    
                }catch (Exception e){
                    System.out.println("error in the checkIfFileExists");
                }
            }
        }

        /**
         * this can be called by get()
         * @param path
         */
        public void openFileAndPerformOperation(String path){
        
            //we already know if it is a get/post request
            //based on the type of request open Buffered Reader/Writer and perform realted operations.
            //if post:
            
                //open file and read contents
            try{
                File file = new File(path);
                BufferedReader input_file = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line="";
                String line_ = "";
                while((line = input_file.readLine()) != null) {
                    line_ =line_.concat(line)+"\n";
                }
                int size=line_.length();
                out.write("HTTP/1.0 200 OK\r\n");
                out.write("Content-Length: "+size+"\r\n");
                out.write("Content-Type: text/html\r\n");
                out.write("\r\n");
                out.write(line_);
                out.flush();
                //the data
                input_file.close();
            } catch (Exception e){
                System.out.println("error in the openFileAndPerformOperation get");
            }
        }
            
        /**
        * this can be called by post()
        * @param path @param body
        */
        public void openFileAndPerformOperation(String path,String body){
            try{
                File textFile = new File(path);
                FileWriter writer = new FileWriter(textFile);
                writer.write(body);
                writer.close();
            } catch (Exception e){
                System.out.println("error in the openFileAndPerformOperation post");
            }
        }
    }
}