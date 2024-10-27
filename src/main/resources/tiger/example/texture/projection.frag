#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

uniform uint index;
uniform uint bit;

layout(location=0) out uvec4 fragData0;

void main() {
    uint bitmask = 1u << bit;
    fragData0 = uvec4(0u);
    fragData0[index] = bitmask;
}