#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

out vec3 N, L;

layout(location=0) in vec4 vertex;
layout(location=1) in vec3 normal;

void main(void) {
   gl_Position = gl_ModelViewProjectionMatrix * vertex;

   vec3 lightPos = vec3(0.0, 0.0, -1.0);
   vec4 V = gl_ModelViewMatrix * vertex;
   N = gl_NormalMatrix * normal;
   L = lightPos - V.xyz;

   gl_FrontColor = vec4(gl_Color.xyz, 1.0);
   gl_BackColor = vec4(gl_Color.xyz, 1.0);
}