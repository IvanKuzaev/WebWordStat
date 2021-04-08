package kuzaev.webwordstat;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;

import static kuzaev.webwordstat.WebWordStat.LOG;

public class SiteWordStat {

    public static final int AVERAGE_SITE_SIZE = 5 * 1_048_576; //take 5 Mb average web site size for 2021

    private String urlString;
    private URL url;
    private String host;
    private Timestamp timestamp;
    private URLConnection urlConnection;
    private int contentLength;
    private long maxMemory;

    /*
        This boolean flag was initially introduced for two modes of operating:
        1) we load the whole web site into memory and process it in one go
        2) we load the web site by chunks into buffer and process it chunk by chunk (now it's the main mode)
        Temporarily left for possible further development.
     */
    private boolean enoughMemoryForWholeSite; //TODO maybe to delete

    private MapCounter<String> mapCounter;

    public SiteWordStat(String urlString) {
        if (urlIsValidAndSet(urlString)) {
            update();
        }
    }

    public String getHost() {
        return host;
    }

    public String getUrlString() {
        return urlString;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public MapCounter<String> getMapCounter() {
        return mapCounter;
    }

    private boolean urlIsValidAndSet(String urlString) {
        this.urlString = urlString;
        try {
            url = new URL(urlString);
            host = url.getHost();
            return true;
        } catch (MalformedURLException e) {
            LOG.severe(e + ": invalid url syntax - " + urlString + ".");
            return false;
        }
    }

    private void setUrlConnection(URL url) {
        try {
            urlConnection = url.openConnection();
        } catch (FileNotFoundException e) {
            LOG.severe(e + ": invalid url path - " + urlString + ".");
            System.err.println("Invalid url path - " + urlString + ".");
        } catch (UnknownHostException e) {
            LOG.severe("Error in host adress.");
            System.err.println("Please specify an existing host to process.");
        } catch (IOException e) {
            LOG.severe(e + ": error trying to open connection from - " + urlString + ".");
        }
        contentLength = urlConnection.getContentLength();
        LOG.info("contentLength = " + contentLength + " bytes");
    }

    private void determineAndCheckMemory() {
        maxMemory = Runtime.getRuntime().maxMemory();
        LOG.info("Runtime.getRuntime().maxMemory() = " + Runtime.getRuntime().maxMemory() / 1024 + " Kb");
        if (5 * AVERAGE_SITE_SIZE + WebWordStat.MINIMUM_HEAP_SIZE <= maxMemory) { //multiplier 5 is an approximation
            enoughMemoryForWholeSite = true;
            LOG.info("Enough memory available. The site can be completely loaded into memory");
        } else if (5 * HyperTextProcessor.DEFAULT_BUFFER_SIZE + WebWordStat.MINIMUM_HEAP_SIZE <= maxMemory) { //multiplier 5 is an approximation
            enoughMemoryForWholeSite = false;
            LOG.info("Not enough memory available. The site can be processed by chunks.");
        } else {
            System.err.println("Not enough memory to run a program. Please increase the heap size or expand your RAM.");
            LOG.severe("Not enough memory to run a program. Please increase the heap size or expand your RAM.");
            System.exit(-1);
        }
    }

    private void processByStream(Reader reader) throws IOException {
        SqlManager.deleteSiteWordStat(this);
        mapCounter = new MapCounter<>();
        HyperTextProcessor htp = new HyperTextProcessor(reader);
        htp.addBufferBeginListener(mapCounter::clear);
        htp.addBufferEndListener(() -> SqlManager.insertAndUpdateIntoDB(this));
        htp.getWordsStream().forEachOrdered(mapCounter::put); //process a stream word by word and collect statistic
    }

    private void update() {
        timestamp = new Timestamp(new Date().getTime());
        setUrlConnection(url);
        determineAndCheckMemory();
        try(InputStream is = urlConnection.getInputStream()) {
            processByStream(new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            LOG.severe(e + ": invalid url path - " + urlString + ".");
            System.err.println("Invalid url path - " + urlString + ".");
        } catch (IOException e) {
            LOG.severe(e + ": Check your internet connection.");
            System.exit(-1);
        }
    }

}
