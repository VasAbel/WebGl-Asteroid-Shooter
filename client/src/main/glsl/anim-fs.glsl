#version 300 es

precision highp float;

out vec4 fragmentColor; //#vec4# A four-element vector [r,g,b,a].; Alpha is opacity, we set it to 1 for opaque.; It will be useful later for transparency.
in vec2 tex;
uniform struct{ sampler2D colorTexture; } material;

void main(void) {
  fragmentColor = texture(material.colorTexture, tex); 
}
