package org.imintel.mbtiles4j;

import java.io.File;
import java.sql.*;

public class SQLHelper {
	
	public final static String DEFAULT_SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
	
    public static Connection establishConnection(File file) throws MBTilesException {
        Connection c;
        try {
        	Class.forName(System.getProperty("org.imintel.mbtiles4j.sqlite_jdbc_driver", DEFAULT_SQLITE_JDBC_DRIVER));
            c = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        } catch (ClassNotFoundException | SQLException e) {
            throw new MBTilesException("Establish Connection failed.", e);
        }
        return c;
    }

    public static void createTable(Connection connection, String tableName, String schema, String... onSuccess) throws MBTilesException {
        if (!tableExists(connection, tableName)) {
            String sql = "CREATE TABLE  " + tableName + schema + ";";
            execute(connection, sql);
            for (String cmd : onSuccess) {
                execute(connection, cmd);
            }
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws MBTilesException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "';";
        try (ResultSet resultSet = executeQuery(connection, sql)) {
            boolean tableExists = resultSet.next();
            return tableExists;
        } catch (SQLException e) {
            throw new MBTilesException("Close Result Set", e);
        }
    }

    public static ResultSet executeQuery(Connection connection, String sql) throws MBTilesException {
        Statement stmt = createStatement(connection);
        try {
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            throw new MBTilesException("Execute statement on connection failed. (" + sql + ")", e);
        }
    }

    private static Statement createStatement(Connection connection) throws MBTilesException {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            throw new MBTilesException("Create a statement on connection failed.", e);
        }
    }

    public static void execute(Connection connection, String sql) throws MBTilesException {
        Statement stmt = createStatement(connection);
        try {
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            throw new MBTilesException("Execute statement on connection failed. (" + sql + ")", e);
        }
    }

    public static void insert(Connection connection, String tableName, String schema, String values) throws MBTilesException {
        execute(connection, "INSERT INTO " + tableName + " " + schema + " " + values + ";");
    }

    public static void addTile(Connection connection, byte[] bytes, long zoom, long column, long row) throws MBTilesException {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO tiles (zoom_level,tile_column,tile_row,tile_data) VALUES(?,?,?,?)")) {
            stmt.setInt(1, (int) zoom);
            stmt.setInt(2, (int) column);
            stmt.setInt(3, (int) row);
            stmt.setBytes(4, bytes);
            stmt.execute();
        } catch (SQLException e) {
            throw new MBTilesException("Add Tile failed.", e);
        }
    }
}
