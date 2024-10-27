#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable
#extension GL_EXT_texture_array : enable

uniform sampler2DArray texture;  
in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

void main(void) {
    fragColor = texture2DArray(texture, vec3(texCoord, texCoord.x));
}