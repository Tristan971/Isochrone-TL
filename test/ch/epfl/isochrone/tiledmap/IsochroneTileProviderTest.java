package ch.epfl.isochrone.tiledmap;

import org.junit.Test;

import java.io.IOException;

public class IsochroneTileProviderTest {

    @Test
    public void test() throws IOException {
        new IsochroneTileProvider(ch.epfl.isochrone.RandomStuffGenerator.generateFastestPathTree(), ch.epfl.isochrone.RandomStuffGenerator.generateColorTable(), 10);
    }

    @Test
    public void testTileAt() throws Exception {

    }
}