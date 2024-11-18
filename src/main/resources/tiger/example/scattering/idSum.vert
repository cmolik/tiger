#version 330
#extension GL_EXT_gpu_shader4: enable

uniform usampler2D idTexture;

in layout(location=0) vec2 position;
in layout(location=1) vec2 texCoord;

flat out uvec4 id;

void main() {
    id = texture(idTexture, texCoord);
    gl_Position = vec4(position, 0.0, 1.0);
}
