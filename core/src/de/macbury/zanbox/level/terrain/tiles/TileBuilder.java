package de.macbury.zanbox.level.terrain.tiles;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import de.macbury.zanbox.Zanbox;
import de.macbury.zanbox.graphics.geometry.MeshAssembler;
import de.macbury.zanbox.graphics.geometry.MeshVertexData;
import de.macbury.zanbox.level.terrain.chunk.layers.ChunkLayerPartRenderable;
import de.macbury.zanbox.managers.Assets;
import de.macbury.zanbox.utils.MyMath;

/**
 * Created by macbury on 29.05.14.
 */
public class TileBuilder extends MeshAssembler {
  private final TextureAtlas terrainAtlas;
  private final Material tileMaterial;
  private final Vector3 tempA = new Vector3();
  private final Vector3 tempB = new Vector3();

  protected static class RenderablePool extends Pool<ChunkLayerPartRenderable> {
    protected Array<ChunkLayerPartRenderable> obtained = new Array<ChunkLayerPartRenderable>();

    @Override
    protected ChunkLayerPartRenderable newObject () {
      return new ChunkLayerPartRenderable();
    }

    @Override
    public ChunkLayerPartRenderable obtain () {
      ChunkLayerPartRenderable renderable = super.obtain();
      renderable.environment = null;
      renderable.material = null;
      renderable.mesh = null;
      renderable.shader = null;
      renderable.worldTransform.idt();
      obtained.add(renderable);
      return renderable;
    }

    public void flush () {
      super.freeAll(obtained);
      obtained.clear();
    }
  }

  private RenderablePool pool;

  public TileBuilder() {
    super();
    this.pool           = new RenderablePool();
    this.terrainAtlas   = Zanbox.assets.get(Assets.TERRAIN_TEXTURE);

    if (terrainAtlas.getTextures().size > 1)
      throw new GdxRuntimeException("Only supports one texture for terrain!!!!");

    Texture terrainTexture = terrainAtlas.getTextures().first();
    this.tileMaterial      = new Material(TextureAttribute.createDiffuse(terrainTexture));
  }

  @Override
  public void begin() {
    super.begin();
    this.using(MeshVertexData.AttributeType.Position);
    this.using(MeshVertexData.AttributeType.TextureCord);
  }

  public ChunkLayerPartRenderable getRenderable() {
    Mesh mesh = new Mesh(true,verties.length, this.indices.length, this.getVertexAttributes());
    mesh.setIndices(indices);
    mesh.setVertices(verties);

    ChunkLayerPartRenderable renderable = pool.obtain();
    renderable.mesh                     = mesh;
    renderable.primitiveType            = GL30.GL_TRIANGLES;
    renderable.meshPartSize             = mesh.getNumIndices();
    renderable.meshPartOffset           = 0;
    renderable.material                 = tileMaterial;
    return renderable;
  }

  public void free(ChunkLayerPartRenderable layerPartRenderable) {
    pool.free(layerPartRenderable);
  }

  public TextureRegion regionForTile(byte tileID) {
    switch (tileID) {
      case Tile.SAND:
        return terrainAtlas.findRegion("sand");
      case Tile.SNOW:
        return terrainAtlas.findRegion("water"); //TODO: change this
      case Tile.ROCK:
        return terrainAtlas.findRegion("rock");
      case Tile.LIGHT_GRASS:
        return terrainAtlas.findRegion("light_grass");
      case Tile.DARK_GRASS:
        return terrainAtlas.findRegion("grass");
      default:
        throw new GdxRuntimeException("Undefined tile id: " + tileID);
    }
  }

  public void topFace(int x, int y, int z, byte tileID) {
    TextureRegion region = regionForTile(tileID);
    topFace(x, y, z, Tile.SIZE, Tile.SIZE, Tile.SIZE, region.getU(), region.getV(), region.getU2(), region.getV2());
  }

  @Override
  public void dispose() {
    super.dispose();
    pool.flush();
    pool.clear();
  }
}
