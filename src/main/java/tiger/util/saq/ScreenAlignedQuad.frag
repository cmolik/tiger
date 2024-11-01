#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

uniform sampler2D texture;

in vec2 texCoord;

void main(void) {
   gl_FragColor = texture(texture, texCoord);
   //gl_FragColor = vec4(texCoord, 0.0, 1.0);
}