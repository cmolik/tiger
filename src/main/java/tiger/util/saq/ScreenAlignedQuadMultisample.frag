
#version 330 compatibility
#extension GL_NV_explicit_multisample : enable

uniform sampler2DMS texture;

in ivec2 texCoord;

void main(void) {
   gl_FragColor = texelFetch(texture, texCoord, 0);
}
