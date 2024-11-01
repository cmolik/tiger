#version 330
#extension GL_EXT_gpu_shader4: enable

uniform uint ids;

layout(location=0) out uvec4 fragColor;

void main() {
    fragColor = uvec4(9u, 0u, 0u, 0u);
}


