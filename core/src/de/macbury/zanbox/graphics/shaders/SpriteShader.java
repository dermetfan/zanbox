package de.macbury.zanbox.graphics.shaders;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import de.macbury.zanbox.Zanbox;
import de.macbury.zanbox.graphics.sprites.SpriteRenderable;
import de.macbury.zanbox.managers.Shaders;

/**
 * Created by macbury on 28.05.14.
 */
public class SpriteShader extends CoreShader {
  private final int u_opacity;
  private final int u_alphaTest;
  private Material currentMaterial;

  public static class Inputs {
    public final static Uniform opacity         = new Uniform("u_opacity", BlendingAttribute.Type);
    public final static Uniform alphaTest       = new Uniform("u_alphaTest", FloatAttribute.AlphaTest);
  }

  public static class Setters {
    public final static Setter diffuseTexture = new Setter() {
      @Override
      public boolean isGlobal (BaseShader shader, int inputID) {
        return false;
      }

      @Override
      public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
        final int unit = shader.context.textureBinder.bind(((TextureAttribute)(combinedAttributes
                .get(TextureAttribute.Diffuse))).textureDescription);
        shader.set(inputID, unit);
      }
    };
  }

  public SpriteShader() {
    super(Zanbox.shaders.get(Shaders.SHADER_SPRITES));
    u_opacity        = register(Inputs.opacity);
    u_alphaTest      = register(Inputs.alphaTest);
  }


  protected void bindMaterial (final Renderable renderable) {
    if (currentMaterial == renderable.material) return;

    int depthFunc = GL20.GL_LESS;
    float depthRangeNear = 0f;
    float depthRangeFar = 1f;
    boolean depthMask = true;

    currentMaterial = renderable.material;
    for (final Attribute attr : currentMaterial) {
      final long t = attr.type;
      if (BlendingAttribute.is(t)) {
        context.setBlending(true, ((BlendingAttribute)attr).sourceFunction, ((BlendingAttribute)attr).destFunction);
        set(u_opacity, ((BlendingAttribute)attr).opacity);
      } if ((t & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest)
        set(u_alphaTest, ((FloatAttribute)attr).value);
      else if ((t & DepthTestAttribute.Type) == DepthTestAttribute.Type) {
        DepthTestAttribute dta = (DepthTestAttribute)attr;
        depthFunc = dta.depthFunc;
        depthRangeNear = dta.depthRangeNear;
        depthRangeFar = dta.depthRangeFar;
        depthMask = dta.depthMask;
      }
    }

    context.setDepthTest(depthFunc, depthRangeNear, depthRangeFar);
    context.setDepthMask(depthMask);
    context.setCullFace(GL20.GL_NONE);
  }

  @Override
  public void render(Renderable renderable) {
    if (!renderable.material.has(BlendingAttribute.Type))
      context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    bindMaterial(renderable);
    super.render(renderable);
  }

  @Override
  public int compareTo(Shader other) {
    if (other == null) return -1;
    if (other == this) return 0;
    return 0;
  }

  @Override
  public boolean canRender(Renderable instance) {
    return SpriteRenderable.class.isInstance(instance);
  }
}
