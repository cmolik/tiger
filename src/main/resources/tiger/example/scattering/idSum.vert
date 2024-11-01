#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

uniform usampler2D idTexture;

flat out uvec4 id;

void main() {
    id = texture(idTexture, gl_MultiTexCoord0.xy);
    gl_Position = gl_Vertex * 2.0 - 1.0;
}
