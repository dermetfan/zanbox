package de.macbury.zanbox.level.terrain.chunks.layers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import de.macbury.zanbox.level.terrain.biome.Biome;
import de.macbury.zanbox.level.terrain.biome.WorldBiomeProvider;
import de.macbury.zanbox.level.terrain.chunks.Chunk;
import de.macbury.zanbox.level.terrain.chunks.ChunksProvider;
import de.macbury.zanbox.level.terrain.tiles.Tile;
import de.macbury.zanbox.level.terrain.tiles.TileBuilder;
import de.macbury.zanbox.utils.MyMath;

/**
 * Created by macbury on 29.05.14.
 */
public class GroundLayer extends Layer {

  private static final String TAG = "GroundLayer";

  public GroundLayer() {
    super();
    setIndex(Layer.BASE_INDEX);
  }

  @Override
  protected void buildRenderables(int tileStartX, int tileStartZ, boolean onlyBorder) {
    for(int x = 0; x < ChunkLayerPartRenderable.ROW_COUNT; x++) {
      for(int z = 0; z < ChunkLayerPartRenderable.ROW_COUNT; z++) {
        boolean border = (x == 0 || z == 0 || x == ChunkLayerPartRenderable.ROW_COUNT -1 || z == ChunkLayerPartRenderable.ROW_COUNT -1);

        int sx = x * ChunkLayerPartRenderable.SIZE_IN_TILES; //Local tile position in tiles table
        int sz = z * ChunkLayerPartRenderable.SIZE_IN_TILES;

        if (onlyBorder) {
          if (border) {
            buildPart(sx,sz, tileStartX, tileStartZ, border);
          }
        } else {
          buildPart(sx,sz, tileStartX, tileStartZ, border);
        }
      }
    }
  }

  @Override
  public void generateTiles(int tileStartX, int tileStartZ) {
    WorldBiomeProvider provider = chunk.chunksProvider.level.biomeProvider;

    for(int x = 0; x < Chunk.TILE_SIZE; x++) {
      for(int z = 0; z < Chunk.TILE_SIZE; z++) {
        Biome biome   = provider.getBiomeAt(tileStartX + x, tileStartZ + z);

        switch (biome) {
          case PLAINS:
            tiles[x][z] = Tile.LIGHT_GRASS;
          break;

          case FOREST:
            tiles[x][z] = Tile.DARK_GRASS;
          break;

          case DESERT:
            tiles[x][z] = Tile.SAND;
          break;

          case SNOW:
            tiles[x][z] = Tile.SNOW;
          break;

          case MOUNTAINS:
            tiles[x][z] = Tile.ROCK;
          break;

          case DEEP_WATER:
            tiles[x][z] = Tile.DEEP_WATER;
          break;

          case SHALLOW_WATER:
            tiles[x][z] = Tile.SHALLOW_WATER;
          break;

          case LAVA:
            tiles[x][z] = Tile.LAVA;
          break;

          default:
            throw new GdxRuntimeException("Undefined biome: " +biome.toString());
        }
      }
    }
  }

  public void buildPart(int sx, int sz, int tileStartX, int tileStartZ, boolean border) {
    ChunksProvider provider = chunk.chunksProvider;
    TileBuilder builder     = provider.level.tileBuilder;
    builder.begin(); {
      for(int x = 0; x < ChunkLayerPartRenderable.SIZE_IN_TILES; x++) {
        for(int z = 0; z < ChunkLayerPartRenderable.SIZE_IN_TILES; z++) {
          //MyMath.localToWorldTilePosition(this.chunk, tempB.set(x+sx, 0, z+sz), tempA);

          byte tileID = getTileByLocalTilePosition(x+sx, z+sz);
         // byte tileID2 = getTileByLocalTilePosition(x+sx, z+sz);
         // if (tileID != tileID2) {
        //    throw new GdxRuntimeException("Not working");
        //  }
          builder.topFace(x,index,z, tileID);
        }
      }
    } builder.end();

    MyMath.tilePositionToMeters(tempA.set(sx, 0, sz), tempB);
    MyMath.chunkPositionToMeters(chunk.position, tempA);
    tempB.add(tempA);

    ChunkLayerPartRenderable renderable = builder.getRenderable();
    renderable.border                   = border;
    renderable.x                        = sx;
    renderable.z                        = sz;

    renderable.worldTransform.translate(tempB);
    //Gdx.app.log(TAG, "World location " + tempB.toString());

    renderable.mesh.calculateBoundingBox(renderable.boundingBox);
    renderable.boundingBox.min.add(tempB).y = -Tile.SIZE/2;
    renderable.boundingBox.max.add(tempB).y = Tile.SIZE;
    renderables.add(renderable);

    //Gdx.app.log(TAG, "Builded part: " +renderable.toString() + " tileStartXZ = " + tileStartX + "x" + tileStartZ);
  }
}
