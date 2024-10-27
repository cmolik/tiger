#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

layout(triangles) in;
layout(triangle_strip, max_vertices = 6) out;

flat out int layer; 

void main(void){
    for(layer = 0; layer < 2; layer++) {
        gl_Layer = layer;
        for(int i = 0; i < 3; i++){
            gl_Position = gl_in[i].gl_Position;
            EmitVertex();
        }
        EndPrimitive();
    }
}