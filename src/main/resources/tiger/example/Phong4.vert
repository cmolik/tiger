#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

out vec3 N, L;

uniform int cameraId;
uniform float scale;
uniform float transX;
uniform float transY;
uniform float transZ;

//layout(location=0) in vec4 vertex;
//layout(location=1) in vec3 normal;

void main(void) {

   mat4 camera[6];

   camera[0] =  mat4(
       vec4(-84.184945,  437.49188, -0.32282419, 0),
       vec4(-107.84124, -386.87619, -0.88995278, 0),
       vec4( 618.49587,  116.23784,  0.32213131, 0),
       vec4( 1837.1505,  963.35436,  4.2114192,  0));

   camera[1] =  mat4(
       vec4(-150.42581,  353.04694, -0.53099859, 0),
       vec4(-475.91573, -423.17325, -0.77629365, 0),
       vec4( 391.87185, -207.82641, -0.33971852, 0),
       vec4( 871.88752,  360.19508,  2.9778192,  0));

   camera[2] =  mat4(
       vec4(-162.18175,  348.34503, -0.49850209, 0),
       vec4(-591.58617, -126.29777, -0.22469516, 0),
       vec4(-123.77342, -454.59996, -0.83726206, 0),
       vec4( 1219.6471,  1173.7565,  4.5491024,  0));

   //TODO
   camera[3] =  mat4(
       vec4(-107.11638,  415.33391, -0.34932588, 0),
       vec4( 48.704574,  364.85004,  0.84985206, 0),
       vec4(-605.55606, -161.99439, -0.39461742, 0),
       vec4( 1714.0388,  1090.5635,  4.8228408,  0));

   camera[4] =  mat4(
       vec4(-126.28403,  434.17378, -0.31963486, 0),
       vec4( 478.15781,  375.44937,  0.87296068, 0),
       vec4(-382.29008,  129.75790,  0.36847416, 0),
       vec4( 780.76562,  435.77948,  3.1131742,  0));

   camera[5] =  mat4(
       vec4(-150.75756,  427.93946, -0.32276102, 0),
       vec4( 603.90406,  213.76089,  0.40031281, 0),
       vec4( 32.410123,  337.79211,  0.85765668, 0),
       vec4( 1210.8315,  1000.7431,  4.2774367,  0));

   vec4 vert = gl_Vertex;
   vert.xyz *= scale;
   vert.xyz += vec3(transX, transY, transZ);
   gl_Position = camera[cameraId] * vert;
   gl_Position /= gl_Position.z;
   gl_Position.z = 0.0;
   gl_Position.w = 1.0;

   gl_Position.xy /= vec2(640, 480);

   gl_Position.xy = gl_Position.xy * 2.0 - 1.0;

   vec3 lightPos = vec3(0.0, 0.0, -1.0);
   vec4 V = gl_ModelViewMatrix * gl_Vertex;
   N = gl_NormalMatrix * gl_Normal;
   L = lightPos - V.xyz;

   gl_FrontColor = vec4(gl_Color.xyz, 1.0);
   gl_BackColor = vec4(gl_Color.xyz, 1.0);
}