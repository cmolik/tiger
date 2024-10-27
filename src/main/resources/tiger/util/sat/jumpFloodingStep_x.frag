#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

uniform sampler2D texture;

uniform int jumpLength; 

in vec2 texCoord;

void main(void) {
   ivec2 texSize = textureSize(texture, 0);
   float texOffset = jumpLength / float(texSize.x);
   float offsetCoord1 = texCoord.x - texOffset;
   float offsetCoord2 = texCoord.x - 2.0*texOffset;
   float offsetCoord3 = texCoord.x - 3.0*texOffset;
   float offsetCoord4 = texCoord.x - 4.0*texOffset;
   float offsetCoord5 = texCoord.x - 5.0*texOffset;
   float offsetCoord6 = texCoord.x - 6.0*texOffset;
   float offsetCoord7 = texCoord.x - 7.0*texOffset;

   gl_FragColor  = texture2D(texture, texCoord);
   if(offsetCoord1 > 0) {
        gl_FragColor += texture2D(texture, vec2(offsetCoord1, texCoord.y));
   }
   if(offsetCoord2 > 0) {
        gl_FragColor += texture2D(texture, vec2(offsetCoord2, texCoord.y));
   }
   if(offsetCoord3 > 0) {
        gl_FragColor += texture2D(texture, vec2(offsetCoord3, texCoord.y));
   }
   if(offsetCoord4 > 0) {
        gl_FragColor += texture2D(texture, vec2(offsetCoord4, texCoord.y));
   }
   if(offsetCoord5 > 0) {
        gl_FragColor += texture2D(texture, vec2(offsetCoord5, texCoord.y));
   }
   if(offsetCoord6 > 0) {
        gl_FragColor += texture2D(texture, vec2(offsetCoord6, texCoord.y));
   }
   if(offsetCoord7 > 0) {
        gl_FragColor += texture2D(texture, vec2(offsetCoord7, texCoord.y));
   }
}