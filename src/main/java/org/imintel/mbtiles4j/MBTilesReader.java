package org.imintel.mbtiles4j;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.imintel.mbtiles4j.model.MetadataEntry;

public class MBTilesReader implements AutoCloseable {

    private Connection connection;

    public MBTilesReader(File f) throws MBTilesReadException {
        try {
            connection = SQLHelper.establishConnection(f);
        } catch (MBTilesException e) {
            throw new MBTilesReadException("Establish Connection to " + f.getAbsolutePath() + " failed", e);
        }
    }

    @Override
	public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
        		//
        }
    }

    public MetadataEntry getMetadata() throws MBTilesReadException {
        String sql = "SELECT * from metadata;";
        try {
            ResultSet resultSet = SQLHelper.executeQuery(connection, sql);
            MetadataEntry ent = new MetadataEntry();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String value = resultSet.getString("value");
                ent.addKeyValue(name, value);
            }
            return ent;
        } catch (MBTilesException | SQLException e) {
            throw new MBTilesReadException("Get Metadata failed", e);
        }
    }

    public TileIterator getTiles() throws MBTilesReadException {
        String sql = "SELECT * from tiles;";
        try {
            ResultSet resultSet = SQLHelper.executeQuery(connection, sql);
            return new TileIterator(resultSet);
        } catch (MBTilesException e) {
            throw new MBTilesReadException("Access Tiles failed", e);
        }
    }
    
    public Tile getTile(int zoom, int column, int row) throws MBTilesReadException {
    	String sql = String.format("SELECT tile_data FROM tiles WHERE zoom_level = %d AND tile_column = %d AND tile_row = %d", zoom, column, row);
    	
    	try {
			ResultSet resultSet = SQLHelper.executeQuery(connection, sql);
			if (resultSet.next()) {
	            return new Tile(zoom, column, row, resultSet.getBytes("tile_data"));
			} else {
				throw new MBTilesReadException(String.format("Could not get Tile for z:%d, column:%d, row:%d", zoom, column, row));
			}
		} catch (MBTilesException | SQLException e) {
			throw new MBTilesReadException(String.format("Could not get Tile for z:%d, column:%d, row:%d", zoom, column, row), e);
		}
    }
    
    public int getMaxZoom() throws MBTilesReadException {
    	String sql = "SELECT MAX(zoom_level) FROM tiles";
    	
    	try {
			ResultSet resultSet = SQLHelper.executeQuery(connection, sql);
			if (resultSet.next()) {
				return resultSet.getInt(1);
			} else {
				throw new MBTilesReadException("Could not get max zoom");
			}
		} catch (Exception e) {
			throw new MBTilesReadException("Could not get max zoom", e);
		}
    }
    
    public int getMinZoom() throws MBTilesReadException {
    	String sql = "SELECT MIN(zoom_level) FROM tiles";
    	
    	try {
			ResultSet resultSet = SQLHelper.executeQuery(connection, sql);
			if (resultSet.next()) {
				return resultSet.getInt(1);
			} else {
				throw new MBTilesReadException("Could not get min zoom");
			}
		} catch (Exception e) {
			throw new MBTilesReadException("Could not get min zoom", e);
		}
    }
}
