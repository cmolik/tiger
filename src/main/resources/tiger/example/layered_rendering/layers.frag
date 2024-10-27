#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

flat in int layer;

layout(location = 0) out vec4 fragColor;

void main(void) {   
    if(layer == 0) {
        fragColor = vec4(1.0, 0.0, 0.0, 1.0);
        gl_FragDepth = gl_FragCoord.z;
    }
    else {
        fragColor = vec4(0.0, 1.0, 0.0, 1.0);
        gl_FragDepth = gl_FragCoord.z - 0.5;
    }
}