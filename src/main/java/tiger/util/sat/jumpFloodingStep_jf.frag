#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable

uniform sampler2D texture;

uniform int jumpLength; 

in vec2 texCoord;

void main(void) {
   ivec2 texSize = textureSize(texture, 0);
   vec2 texOffset = jumpLength / vec2(texSize);
   vec2 offsetCoord = texCoord - texOffset;

   gl_FragColor  = texture2D(texture, texCoord);
   if(offsetCoord.x > 0) {
        gl_FragColor += texture2D(texture, vec2(offsetCoord.x, texCoord.y));
   }
   if(offsetCoord.y > 0) {
        gl_FragColor += texture2D(texture, vec2(texCoord.x, offsetCoord.y));
   }
   if(offsetCoord.x > 0 && offsetCoord.y > 0) {
        gl_FragColor += texture2D(texture, offsetCoord);
   }
}