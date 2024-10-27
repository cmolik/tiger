#version 120
#extension GL_EXT_gpu_shader4: enable

uniform sampler2D texture;

varying vec2 texCoord;

void main(void) {
   gl_FragColor = texture2D(texture, vec2(texCoord.x, 1.0 - texCoord.y));
}