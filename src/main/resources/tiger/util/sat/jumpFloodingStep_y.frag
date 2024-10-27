#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

uniform sampler2D texture;

uniform int jumpLength; 

in vec2 texCoord;

void main(void) {
   ivec2 texSize = textureSize(texture, 0);
   float texOffset = jumpLength / float(texSize.y);
   float offsetCoord1 = texCoord.y - texOffset;
   float offsetCoord2 = texCoord.y - 2.0*texOffset;
   float offsetCoord3 = texCoord.y - 3.0*texOffset;
   float offsetCoord4 = texCoord.y - 4.0*texOffset;
   float offsetCoord5 = texCoord.y - 5.0*texOffset;
   float offsetCoord6 = texCoord.y - 6.0*texOffset;
   float offsetCoord7 = texCoord.y - 7.0*texOffset;

   gl_FragColor  = texture2D(texture, texCoord);
   if(offsetCoord1 > 0) {
        gl_FragColor += texture2D(texture, vec2(texCoord.x, offsetCoord1));
   }
   if(offsetCoord2 > 0) {
        gl_FragColor += texture2D(texture, vec2(texCoord.y, offsetCoord2));
   }
   if(offsetCoord3 > 0) {
        gl_FragColor += texture2D(texture, vec2(texCoord.y, offsetCoord3));
   }
   if(offsetCoord4 > 0) {
        gl_FragColor += texture2D(texture, vec2(texCoord.y, offsetCoord4));
   }
   if(offsetCoord5 > 0) {
        gl_FragColor += texture2D(texture, vec2(texCoord.y, offsetCoord5));
   }
   if(offsetCoord6 > 0) {
        gl_FragColor += texture2D(texture, vec2(texCoord.y, offsetCoord6));
   }
   if(offsetCoord7 > 0) {
        gl_FragColor += texture2D(texture, vec2(texCoord.y, offsetCoord7));
   }
}