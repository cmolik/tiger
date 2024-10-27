#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable


layout(location=0) in vec4 vertex;
layout(location=1) in vec3 normal;

void main(void) {
   gl_Position = gl_ModelViewProjectionMatrix * vertex;
}