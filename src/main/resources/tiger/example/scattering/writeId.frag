#version 330
#extension GL_EXT_gpu_shader4: enable

uniform uint ids;

in vec2 texCoord;

layout(location=0) out uvec4 fragColor;

void main() {
    if(texCoord.x < 0.5) discard;
    if(texCoord.x > 0.6) discard;

    if(texCoord.y < 0.9) discard;
    if(texCoord.y > 1.0) discard;

    fragColor = uvec4(ids, 0u, 0u, 0u);
}


