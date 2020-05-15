package org.imintel.mbtiles4j;

public class Tile {
    private final int zoom;
    private final int column;
    private final int row;
    private final byte[] data;

    public Tile(int zoom, int column, int row, byte[] data) {
        this.zoom = zoom;
        this.column = column;
        this.row = row;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public int getZoom() {
        return zoom;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }
}