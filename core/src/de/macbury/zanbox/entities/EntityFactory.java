package de.macbury.zanbox.entities;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;

import de.macbury.zanbox.Zanbox;
import de.macbury.zanbox.entities.components.AnimatedSpriteComponent;
import de.macbury.zanbox.entities.components.BoundingBoxComponent;
import de.macbury.zanbox.entities.components.MovementComponent;
import de.macbury.zanbox.entities.components.PositionComponent;
import de.macbury.zanbox.entities.components.SpriteComponent;
import de.macbury.zanbox.entities.components.VisibleComponent;
import de.macbury.zanbox.entities.managers.Tags;
import de.macbury.zanbox.level.GameLevel;
import de.macbury.zanbox.level.terrain.tiles.Tile;
import de.macbury.zanbox.managers.Assets;

/**
 * Created by macbury on 28.05.14.
 */
public class EntityFactory {
  private TextureAtlas characterAtlas;
  private GameLevel level;
  private final static Vector3 defaultOffset = new Vector3(0.5f, Tile.GROUND_HEIGHT + 0.1f, 0.5f);
  public EntityFactory(GameLevel gameLevel) {
    this.level          = gameLevel;
    this.characterAtlas = Zanbox.assets.get(Assets.CHARSET_TEXTURE);
  }

  public Entity player() {
    Entity e = level.createEntity();
    e.addComponent(new PositionComponent(0,0,0));
    e.addComponent(new MovementComponent(4f));
    e.addComponent(new VisibleComponent());
    e.addComponent(new BoundingBoxComponent());
    Animation animation = new Animation(0.15f, characterAtlas.findRegions("dummy"), Animation.PlayMode.LOOP);

    AnimatedSpriteComponent spriteComponent = new AnimatedSpriteComponent(level.modelBatch.build(animation, false, true));

    spriteComponent.offset.set(defaultOffset);
    e.addComponent(spriteComponent);
    level.getManager(Tags.class).register(Tags.PLAYER,e);
    return e;
  }

  public Entity sign() {
    Entity e = level.createEntity();
    e.addComponent(new BoundingBoxComponent());
    e.addComponent(new PositionComponent(2,0,2));
    e.addComponent(new VisibleComponent());
    SpriteComponent spriteComponent = new SpriteComponent(level.modelBatch.build(characterAtlas.findRegion("sign"), true, true));
    spriteComponent.offset.set(defaultOffset);
    e.addComponent(spriteComponent);
    return e;
  }
}
