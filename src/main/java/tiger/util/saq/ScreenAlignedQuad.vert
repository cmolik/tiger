#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

out vec2 texCoord;

void main(void) {
   gl_Position = vec4(gl_Vertex.xy, 0.0, 1.0 );
    
   // Texture coordinate for screen aligned (in correct range):
   texCoord = 0.5*gl_Vertex.xy + 0.5;
   //texCoord = gl_Position.xy; 
}