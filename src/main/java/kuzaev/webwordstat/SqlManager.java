package kuzaev.webwordstat;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

import static kuzaev.webwordstat.WebWordStat.LOG;

public class SqlManager {

    private static Connection con = null;

    public static final int WORD_MAX_LENGTH = 30;

    static String createTableSQL =  "CREATE TABLE IF NOT EXISTS word_stat (" +
                                    "   date TIMESTAMP NOT NULL," +
                                    "   host VARCHAR(30) NOT NULL," +
                                    "   url VARCHAR(150) NOT NULL," +
                                    "   word VARCHAR(" + WORD_MAX_LENGTH + ") NOT NULL," +
                                    "   count BIGINT NOT NULL DEFAULT 0," +
                                    "   PRIMARY KEY (url, word)" +
                                    ");";

    static String deleteTableSQL = "DROP TABLE IF EXISTS word_stat;";

    static String deleteSiteWordStatSQL = "DELETE FROM word_stat WHERE url=?;";

    static String insertAndUpdateRowSQL = "INSERT INTO word_stat VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE count=count+VALUES(count);";

    static String querySiteWordStatSQL = "SELECT * FROM word_stat WHERE url=?;";

    static {
        con = createConnection("jdbc:h2:./db/word_stat_db;MODE=MYSQL;DATABASE_TO_LOWER=TRUE", "sa", "");
    }

    public static Connection createConnection(String dbUrl, String dbUser, String dbPassword) {
        Connection con = null;
        try {
            con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            LOG.severe("Could not connect to the database " + dbUrl + ": " + e);
            System.err.println("Could not connect to the database " + dbUrl + ": " + e);
            System.exit(-1);
        }
        return con;
    }

    public static Connection getConnection() {
        return con;
    }

    public static void createDB() {
        try (Statement s = getConnection().createStatement()) {
            s.execute(deleteTableSQL);
            s.execute(createTableSQL);
        } catch (SQLException e) {
            LOG.severe("Error by initializing database : " + e);
            System.err.println("Error by initializing database : " + e);
            System.exit(-1);
        }
    }

    /* universal method for querying data from DB in sorted manner */
    //TODO implement this method or delete
    public static ResultSet querySiteStat(String url, String wordsSortedOrder, String countsSortedOrder) throws IOException {
        throw new UnsupportedOperationException();
//        try (PreparedStatement ps = getConnection().prepareStatement(querySiteWordStatSQL)) {
//            ps.setString(1, url);
//            return ps.executeQuery();
//        } catch (SQLException e) {
//            LOG.severe(e.toString());
//        }
//        return null;
    }

    /* writes SiteWordStat into database, if there are allready entries, they will be summed with the new ones */
    /* @pre - before successive invoking this method DB must be erased for the particular site */
    public static void insertAndUpdateIntoDB(SiteWordStat sws) {
        Timestamp timestamp = sws.getTimestamp();
        String host = sws.getHost();
        String urlString = sws.getUrlString();
        try (PreparedStatement ps = getConnection().prepareStatement(insertAndUpdateRowSQL)) {
            for (Map.Entry<String, Long> entry : sws.getMapCounter().entrySet()) {
                ps.setTimestamp(1, timestamp);
                ps.setString(2, host);
                ps.setString(3, urlString);
                ps.setString(4, entry.getKey());
                ps.setLong(5, entry.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            if (e.getErrorCode() != 22001) { //database error 22001: value too long
                LOG.severe(e.toString());
            } else {
                LOG.info("Some word in the stream is too long for the database and will be omitted.");
            }
        }
    }

    public static void deleteSiteWordStat(SiteWordStat sws) {
        deleteSiteWordStat(sws.getUrlString());
    }

    public static void deleteSiteWordStat(String urlString) {
        try (PreparedStatement ps = getConnection().prepareStatement(deleteSiteWordStatSQL)) {
            ps.setString(1, urlString);
            ps.execute();
        } catch (SQLException e) {
            LOG.severe(e.toString());
        }
    }

    /* outputs the full site stat by whole table rows */
    public static void print(String siteName) {
        try (PreparedStatement ps = getConnection().prepareStatement(querySiteWordStatSQL)) {
            ps.setString(1, siteName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getTimestamp(1) + ", " + rs.getString(2) + ", " + rs.getString(3) + ", " + rs.getString(4) + ", " + rs.getLong(5));
            }
        } catch (SQLException e) {
            LOG.severe(e.toString());
        }
    }

    /* outputs the full site stat by word-count pairs */
    public static void printBriefly(String siteName) {
        try (PreparedStatement ps = getConnection().prepareStatement(querySiteWordStatSQL)) {
            ps.setString(1, siteName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(4) + " - " + rs.getLong(5));
            }
        } catch (SQLException e) {
            LOG.severe(e.toString());
        }
    }

}
