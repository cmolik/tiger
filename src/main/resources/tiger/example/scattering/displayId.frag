#version 450
#extension GL_EXT_gpu_shader4: enable

uniform usampler2D texture;

uniform uint ids;

in vec2 texCoord;

void main(void) {
    uint textureId = texture(texture, texCoord).r;
    if((ids & textureId) == 0u) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
    else {
        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
    }
}


