#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

uniform sampler2D texture;

uniform ivec2 jumpLength; 

in vec2 texCoord;

void main(void) {
   ivec2 texSize = textureSize(texture, 0);
   vec2 texOffset = jumpLength / float(texSize.x);
   vec2 offsetCoord1 = texCoord - texOffset;
   vec2 offsetCoord2 = texCoord - 2.0*texOffset;
   vec2 offsetCoord3 = texCoord - 3.0*texOffset;

   gl_FragColor  = texture2D(texture, texCoord);
   if(jumpLength.x > 0 ) {
        if(offsetCoord1.x > 0) gl_FragColor += texture2D(texture, vec2(offsetCoord1.x, texCoord.y));
        if(offsetCoord2.x > 0) gl_FragColor += texture2D(texture, vec2(offsetCoord2.x, texCoord.y));
        if(offsetCoord3.x > 0) gl_FragColor += texture2D(texture, vec2(offsetCoord3.x, texCoord.y));
   }
   if(jumpLength.y > 0) {
        if(offsetCoord1.y > 0) gl_FragColor += texture2D(texture, vec2(texCoord.x, offsetCoord1.y));
        if(offsetCoord2.y > 0) gl_FragColor += texture2D(texture, vec2(texCoord.x, offsetCoord2.y));
        if(offsetCoord3.y > 0) gl_FragColor += texture2D(texture, vec2(texCoord.x, offsetCoord3.y));
   }
}