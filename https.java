public class https{

    private static boolean isVerbose = false;
    private static boolean isGetRequest = false;
    private static boolean isPostRequest = false;
    private static boolean needHelp = false;
    private static String portNumber = "8080";
    private static String pathToDir = "";


    public static void main(String args[]) {
        if ( args.length == 0){
            System.out.println("\nEnter https help to get more information.\n");
        }else{
            cmdParser(args);
        }
        if (needHelp) {
            help();
        }else if(isGetRequest){
            get(pathToDir);
        }else if (isPostRequest){
            post(pathToDir);
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
                // hasHeaderData = true;
                portNumber = args[i+1];
                i++;
            }else if (args[i].equalsIgnoreCase("-d")){
                // hasInLineData = true;
                pathToDir = (args[i+1]);
                i++;
            // }else if (args[i].equalsIgnoreCase("-f")){
            //     readFromFile = true;
            //     filePath = (args[i+1]);
            //     i++;
            }else if (args[i].equalsIgnoreCase("get")){
                isGetRequest = true;
            }else if (args[i].equalsIgnoreCase("post")){
                isPostRequest = true;
            }else if (args[i].equalsIgnoreCase("help")){
                needHelp = true;
            }else{
                url = (args[i]);
            }
       }
    }

    /**
     * Prints the help menu.
     */
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