#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

uniform sampler2DArray texture;

uniform int layer;

in vec2 texCoord;

void main(void) {
   gl_FragColor = texture(texture, vec3(texCoord, layer));
}