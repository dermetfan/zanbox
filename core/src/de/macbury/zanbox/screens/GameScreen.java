package de.macbury.zanbox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import de.macbury.zanbox.Zanbox;
import de.macbury.zanbox.graphics.sprites.animated.AnimatedSprite3D;
import de.macbury.zanbox.graphics.sprites.BaseSprite3D;
import de.macbury.zanbox.graphics.sprites.ModelAndSpriteBatch;
import de.macbury.zanbox.graphics.sprites.normal.Sprite3D;
import de.macbury.zanbox.graphics.geometry.MeshAssembler;
import de.macbury.zanbox.managers.Assets;
import de.macbury.zanbox.terrain.World;
import de.macbury.zanbox.terrain.chunk.Chunk;
import de.macbury.zanbox.terrain.chunk.ChunkRenderable;

/**
 * Created by macbury on 27.05.14.
 */
public class GameScreen extends BaseScreen {
  private final Renderable chunkRenderable;
  private final AnimatedSprite3D animatedSprite3D;
  private RenderContext renderContext;
  private CameraInputController cameraController;
  private Array<BaseSprite3D> sprites;
  private World world;
  private ModelAndSpriteBatch modelBatch;
  private PerspectiveCamera camera;

  public GameScreen() {
    this.renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED));
    this.modelBatch    = new ModelAndSpriteBatch(renderContext);
    this.world         = new World(1234);

    TextureAtlas terrainAtlas   = Zanbox.assets.get(Assets.TERRAIN_TEXTURE);
    TextureRegion regionA       = terrainAtlas.findRegion("light_grass");
    TextureRegion regionB       = terrainAtlas.findRegion("dirt");
    TextureRegion regionC       = terrainAtlas.findRegion("sand");

    TextureAtlas charAtlas   = Zanbox.assets.get(Assets.CHARSET_TEXTURE);
    TextureRegion[] regions = {regionA, regionB, regionC, charAtlas.findRegion("dummy")};

    Animation animation = new Animation(0.1f, regionA, regionB, regionC);
    animation.setPlayMode(Animation.PlayMode.LOOP);
    this.animatedSprite3D = modelBatch.build(animation, true, true);
    this.sprites = new Array<BaseSprite3D>();
    sprites.add(animatedSprite3D);
    for(int i = 0; i < 300; i++) {
      TextureRegion region = regions[i % regions.length];

      Sprite3D sprite = modelBatch.build(region, true, true);
      sprite.set((float)Math.random() * 10,(float)Math.random() * 10,(float) Math.random() * 10);
      sprite.scale(1 + (float) Math.random() * 5, 1 + (float) Math.random() * 5);
      //sprite.setTransparent(true);
      sprites.add(sprite);
    }

    modelBatch.debug();


    MeshAssembler meshAssembler = new MeshAssembler();
    TextureRegion region        = terrainAtlas.findRegion("light_grass");

    meshAssembler.begin(); {
      for(int x = 0; x < 20; x++) {
        for(int y = 0; y < 20; y++) {
          meshAssembler.topFace(x,0,y, 1,1,1, region.getU(), region.getV(), region.getU2(), region.getV2());
        }
      }

    } meshAssembler.end();

    Mesh mesh = new Mesh(false, meshAssembler.getVerties().length, meshAssembler.getIndices().length, meshAssembler.getVertexAttributes());
    mesh.setIndices(meshAssembler.getIndices());
    mesh.setVertices(meshAssembler.getVerties());


    chunkRenderable = new ChunkRenderable();
    chunkRenderable.mesh = mesh;
    chunkRenderable.primitiveType = GL30.GL_TRIANGLES;
    chunkRenderable.meshPartSize   = mesh.getNumIndices();
    chunkRenderable.meshPartOffset = 0;
    chunkRenderable.material = new Material(TextureAttribute.createDiffuse(region.getTexture()));


    this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

    camera.position.set(0,5,0);
    camera.lookAt(Vector3.Zero);
    camera.update(true);

    cameraController = new CameraInputController(camera);
    Gdx.input.setInputProcessor(cameraController);

  }

  @Override
  public void onEnter() {

  }

  @Override
  public void onExit() {

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0f, 0f, 0f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    cameraController.update();
    camera.update();

    animatedSprite3D.incStateTime(delta);
    renderContext.begin(); {
      modelBatch.begin(camera); {
        for(Chunk chunk : world.chunks) {
          chunk.render(modelBatch);
        }
        modelBatch.render(chunkRenderable);

        for(BaseSprite3D sprite : sprites)
          modelBatch.render(sprite);
      } modelBatch.end();
    } renderContext.end();
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void show() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {
    modelBatch.dispose();
  }
}
