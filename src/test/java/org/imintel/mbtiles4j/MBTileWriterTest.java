package org.imintel.mbtiles4j;

import org.imintel.mbtiles4j.model.MetadataBounds;
import org.imintel.mbtiles4j.model.MetadataEntry;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MBTileWriterTest {

    private File f = new File("src/test/resources/example.mbtiles");

    @Test
    public void test() throws Exception {
        MBTilesWriter writer = new MBTilesWriter("done");
        MBTilesReader reader = new MBTilesReader(f);
        MetadataEntry metadata = reader.getMetadata();
        writer.addMetadataEntry(metadata);
        TileIterator tiles = reader.getTiles();
        while (tiles.hasNext()) {
            Tile next = tiles.next();
            writer.addTile(next.getData(), next.getZoom(), next.getColumn(), next.getRow());
        }
        tiles.close();
        reader.close();
        writer.close();
        reader = new MBTilesReader(writer.getFile());
        metadata = reader.getMetadata();
        assertEquals(metadata.getTilesetName(), "control_room");
        assertEquals(metadata.getTilesetType(), MetadataEntry.TileSetType.BASE_LAYER);
        assertEquals(metadata.getTilesetVersion(), "0.2.0");
        assertEquals(metadata.getTilesetDescription(), "");
        assertEquals(metadata.getTileMimeType(), MetadataEntry.TileMimeType.JPG);
        assertEquals(metadata.getTilesetBounds(), new MetadataBounds(-180, -85, 180, 85));
        assertEquals(metadata.getAttribution(), "");
        tiles = reader.getTiles();
        while (tiles.hasNext()) {
            Tile next = tiles.next();
            assertNotNull(next.getData());
        }
        tiles.close();
        reader.close();

    }
}
