package de.macbury.zanbox.terrain.chunk;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.zanbox.terrain.World;

/**
 * Created by macbury on 27.05.14.
 */
public class Chunks extends Array<Chunk> implements Disposable {
  public ChunkGeometryBuilder chunksBuilder;
  public World world;

  public Chunks(World world) {
    this.world = world;
    this.chunksBuilder = new ChunkGeometryBuilder(world);
  }

  public Chunk getByTilePosition(int tx, int tz, int layer) {
    return get(tx / Chunk.SIZE, tz / Chunk.SIZE, layer);
  }

  // Get chunk by chunk position
  public Chunk get(int x, int z, int layer) {
    for(Chunk chunk : this) {
      if (chunk.at(x,z,layer)) {
        return chunk;
      }
    }
    //TODO: check if can load chunk from db
    Chunk chunk = new Chunk(x,z,layer);
    chunk.setChunks(this);
    chunk.rebuild();
    this.add(chunk);
    return chunk;
  }

  @Override
  public void dispose() {
    clear();
  }

  public void update(float delta) {
    for(Chunk chunk : this) {
      chunk.update(delta);
    }
  }
}
