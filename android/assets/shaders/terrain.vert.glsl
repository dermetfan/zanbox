uniform vec4 u_sunLightColor;
uniform vec3 u_sunLightDirection;

uniform vec4 u_ambientLight;

uniform mat3  u_normalMatrix;
uniform mat4  u_projViewTrans;
uniform mat4  u_worldTrans;

attribute vec3 a_position;
attribute vec2 a_texCoord0;
attribute vec2 a_shade;
attribute vec3 a_normal;

varying vec2   v_texCoords0;
varying float  v_shadeFactory;
varying vec3   v_normal;
varying vec3   v_lightDiffuse;

vec3 sunLightDiffuse(vec3 normal) {
  vec3 lightDir = -u_sunLightDirection;
  float NdotL   = clamp(dot(normal, lightDir), 0.0, 1.0);
  vec3 value    = u_sunLightColor.rgb * NdotL;
  return value;
}

void main() {
    v_texCoords0   = a_texCoord0;
    v_shadeFactory = a_shade.x;
    v_normal       = normalize(u_normalMatrix * a_normal);
    v_lightDiffuse = u_ambientLight.rgb + sunLightDiffuse(v_normal);

    gl_Position    = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}