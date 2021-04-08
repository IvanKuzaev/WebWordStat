package kuzaev.webwordstat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebWordStat {

    public static final int MINIMUM_HEAP_SIZE = 15 * 1_048_576; //practical minimum to run this program

    public static final Logger LOG = Logger.getLogger(WebWordStat.class.getName());

    static {
        String logFileName = WebWordStat.class.getSimpleName() + ".log";
        try {
            LOG.addHandler(new FileHandler(logFileName));
        } catch (IOException e) {
            System.err.println(e + ": cannot create log file " + logFileName + ".");
        }
    }

    private static void processBatchFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String siteName;
                while ((siteName = br.readLine()) != null) {
                    if (!(siteName = siteName.trim()).equals("")) {
                        new SiteWordStat(siteName);
                        System.out.println("Word statistic from the site " + siteName + " written to database.");
                        LOG.fine("Word statistic from the site " + siteName + " written to database.");
                    }
                }
            } catch (IOException e) {
                LOG.severe("Error while reading file " + fileName);
                System.err.println("Error while reading file " + fileName);
            }
        } else {
            LOG.severe("File " + fileName + " does not exist.");
            System.err.println("File " + fileName + " does not exist.");
        }
    }

    private static String getArgN(String[] args, int n) {
        if (n < args.length) {
            return args[n];
        } else {
            return "";
        }
    }

    public static void main(String[] args) {
        LOG.setLevel(Level.SEVERE); //change level for debug
        LOG.info("Working directory = " + System.getProperty("user.dir"));
        String urlString;
        switch (getArgN(args, 0)) {
            case "/batchMode" :
                processBatchFile(getArgN(args, 1));
                break;
            case "/query" :
                urlString = getArgN(args, 1);
                //TODO implement or delete
//                String arg2 = getArgN(args, 1);
//                String arg3;
//                switch (arg2) {
//                    case "/sortedWords+":
//                        break;
//                    case "/sortedWords-":
//                        break;
//                    case "/sortedCounts+":
//                        break;
//                    case "/sortedCounts-":
//                        break;
//                    case "/limit":
//                        arg3 = getArgN(args, 2);
//                        break;
//                }
                System.out.println("Word statistic from the database for the site " + urlString + " :");
                SqlManager.print(urlString);
                break;
            case "/resetDB" :
                SqlManager.createDB();
                System.out.println("The previous database was deleted and a new one was created.");
                break;
            case "/help" :
                System.out.println( "Usage:\n" +
                                    "\tkuzaev.webwordstat.WebWordStat <site url> - prints word statistis of one site to console\n" +
                                    "\tkuzaev.webwordstat.WebWordStat /batchMode <text_file> - process all sites in <text_file> (one url per line) and writes statistics to DB\n" +
                                    "\tkuzaev.webwordstat.WebWordStat /query <site url> - prints all statistic to the site <site url> from DB to console (if there's any)\n" +
                                    "\tkuzaev.webwordstat.WebWordStat /resetDB - deletes the current DB and creates a new empty one\n" +
                                    "\tkuzaev.webwordstat.WebWordStat /help - outputs this help information\n" +
                                    "");
                break;
            default: //process a single web site, write statistic to DB and print it to console
                urlString = getArgN(args, 0);
                System.out.println("Word statistic for the content from " + urlString + " :");
                new SiteWordStat(urlString);
                SqlManager.printBriefly(urlString);
                break;
        }
        System.exit(0);
    }

}
