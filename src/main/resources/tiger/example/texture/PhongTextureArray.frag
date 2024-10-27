#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

in vec3 N, L;

const float specularExp = 128.0;
const float ambientLight = 0.2;
const vec3 lightCol = vec3(1.0, 1.0, 1.0);

uniform float darknes;

layout(location = 0) out vec4 fragColor0;
layout(location = 1) out vec4 fragColor1;
layout(location = 2) out vec4 fragColor2;

void main(void) {
    
    // Init fragment color
    vec4 color = vec4(0.0, 0.0, 0.0, 1.0);

    // Ambient component
    color.rgb += gl_Color.rgb * ambientLight;

    // Diffuse component

     // Normalize normal vector
    vec3 NN = normalize(N);

    // Normalize light vector
    vec3 NL = vec3(0.0, 0.0, 1.0); //normalize(L);
    //vec3 NH = normalize(NL + vec3(0.0, 0.0, 1.0));

    // Sin of angle between normal and light vector
    float NdotL = abs(dot(NN, NL));
    
    color.rgb += color.rgb * lightCol * NdotL;

    // Specular component
    //if(NdotL > 0.0) {
    //    gl_FragColor.rgb += lightCol * pow(max(0.0, dot(NN, NH)), specularExp);
    //}

    //color.rgb -= darknes;

    fragColor0 = vec4(color.r, 0.0, 0.0, 1.0);
    fragColor1 = vec4(0.0, color.g, 0.0, 1.0);
    fragColor2 = color;

    //gl_FragColor.a = 0.5;
}