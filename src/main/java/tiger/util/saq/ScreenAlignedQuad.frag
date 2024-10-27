#version 120
#extension GL_EXT_gpu_shader4: enable

uniform sampler2D texture;

varying vec2 texCoord;

void main(void) {
   gl_FragColor = texture2D(texture, texCoord);
   //gl_FragColor = vec4(texCoord, 0.0, 1.0);
}