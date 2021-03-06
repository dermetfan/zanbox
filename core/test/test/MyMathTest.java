package test;

import com.badlogic.gdx.math.Vector3;
import de.macbury.zanbox.level.terrain.chunks.Chunk;
import de.macbury.zanbox.utils.MyMath;
import org.junit.Assert;

/**
 * Created by macbury on 03.06.14.
 */
public class MyMathTest {

  public static void itShouldTransformFromWorldToLocalPosition(Vector3 fromWorldPosition, Vector3 toLocalPosition) {
    Vector3 out  = new Vector3();
    MyMath.worldToLocalTilePosition(fromWorldPosition, out);
    TestHelper.assertEqual(toLocalPosition, out);
  }

  @org.junit.Test
  public void testWorldToLocalTilePosition() throws Exception {
    Vector3 out  = new Vector3();
    Vector3 in   = new Vector3();

    itShouldTransformFromWorldToLocalPosition(new Vector3(10, 0, 10), new Vector3(10, 0, 10));
    itShouldTransformFromWorldToLocalPosition(new Vector3(-10, 0, 5), new Vector3(22, 0, 5));
    itShouldTransformFromWorldToLocalPosition(new Vector3(-10, 0, -10), new Vector3(22, 0, 22));
    itShouldTransformFromWorldToLocalPosition(new Vector3(Chunk.TILE_SIZE, 0,Chunk.TILE_SIZE), new Vector3(0, 0, 0));
    itShouldTransformFromWorldToLocalPosition(new Vector3(Chunk.TILE_SIZE-1, 0,Chunk.TILE_SIZE-1), new Vector3(31, 0, 31));
    itShouldTransformFromWorldToLocalPosition(new Vector3(Chunk.TILE_SIZE+1, 0,Chunk.TILE_SIZE+1), new Vector3(1, 0,1));
    itShouldTransformFromWorldToLocalPosition(new Vector3((Chunk.TILE_SIZE * 2 + 1), 0, (Chunk.TILE_SIZE * 2 + 1)), new Vector3(1, 0,1));
    itShouldTransformFromWorldToLocalPosition(new Vector3(-(Chunk.TILE_SIZE * 2 + 1), 0, -(Chunk.TILE_SIZE * 2 + 1)), new Vector3(31, 0,31));

    MyMath.worldToLocalTilePosition(in.set((Chunk.TILE_SIZE), 0, (Chunk.TILE_SIZE)), out);
    TestHelper.assertEqual(out, new Vector3(0, 0, 0));

    MyMath.worldToLocalTilePosition(in.set(-(Chunk.TILE_SIZE), 0, -(Chunk.TILE_SIZE)), out);
    TestHelper.assertEqual(out, new Vector3(0, 0, 0));
  }

  @org.junit.Test
  public void testLocalToWorldTilePosition() throws Exception {
    Vector3 out  = new Vector3();
    Vector3 in   = new Vector3();
    Chunk chunkA = new Chunk(0,0);

    MyMath.localToWorldTilePosition(chunkA, new Vector3(0, 0, 0), out);
    TestHelper.assertEqual(out, Vector3.Zero);

    MyMath.localToWorldTilePosition(chunkA, new Vector3(10,0,10), out);
    TestHelper.assertEqual(out, new Vector3(10,0,10));

    Chunk chunkB = new Chunk(1,1);
    MyMath.localToWorldTilePosition(chunkB, in.set(0, 0, 0), out);
    TestHelper.assertEqual(out, in.add(Chunk.TILE_SIZE, 0, Chunk.TILE_SIZE));

    MyMath.localToWorldTilePosition(chunkB, in.set(5, 0, 5), out);
    TestHelper.assertEqual(out, in.add(Chunk.TILE_SIZE, 0, Chunk.TILE_SIZE));

    Chunk chunkC = new Chunk(-1,-1);
    MyMath.localToWorldTilePosition(chunkC, new Vector3(0, 0, 0), out);
    TestHelper.assertEqual(out, new Vector3(-Chunk.TILE_SIZE, 0, -Chunk.TILE_SIZE));

    Vector3 worldTilePosition = new Vector3();
    Vector3 localTilePosition = new Vector3(5,0,5);
    MyMath.localToWorldTilePosition(chunkC, localTilePosition, worldTilePosition);
    MyMath.worldToLocalTilePosition(worldTilePosition, out);
    TestHelper.assertEqual(out, localTilePosition);

    MyMath.localToWorldTilePosition(chunkC, in.set(5, 0, 5), out);

    TestHelper.assertEqual(out, in.add(-Chunk.TILE_SIZE, 0, -Chunk.TILE_SIZE));

    int offsetX = -1;
    int offsetY = 2;
    Chunk chunkD = new Chunk(offsetX,offsetY);

    MyMath.localToWorldTilePosition(chunkD, new Vector3(0, 0, 0), out);
    TestHelper.assertEqual(out, new Vector3(offsetX * Chunk.TILE_SIZE, 0, Chunk.TILE_SIZE*offsetY));

    MyMath.localToWorldTilePosition(chunkD, in.set(5, 0, 5), out);
    in.add(offsetX * Chunk.TILE_SIZE, 0, offsetY * Chunk.TILE_SIZE);
    TestHelper.assertEqual(out, in);
  }
}
