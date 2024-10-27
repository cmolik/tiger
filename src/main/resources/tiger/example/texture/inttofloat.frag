#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

uniform usampler2D texture;

in vec2 texCoord;

void main() {
    uvec4 X = texture2D(texture, texCoord);
    if(X == uvec4(0u)) {
        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
    }
    else {
        gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);
    }
}
