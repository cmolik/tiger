
uniform sampler2D texture1;
uniform sampler2D texture2;
const float widthInversed = 1.0/512.0;
const float heightInversed = 1.0/512.0;

//const mat3 k = mat3(-0.125, -0.125, -0.125,  -0.125, 1.0, -0.125,  -0.125, -0.125, -0.125);
//const int channel = 0;

varying vec2 texCoord;

void main(void) {
   
    vec4 X = texture2D(texture1, texCoord); 
    //vec4 A = texture2D(texture, texCoord + vec2(-widthInversed, -heightInversed));
    vec4 B = texture2D(texture1, texCoord + vec2(0, -heightInversed));
    //vec4 C = texture2D(texture, texCoord + vec2(widthInversed, -heightInversed));
    vec4 D = texture2D(texture1, texCoord + vec2(-widthInversed, 0));
    vec4 E = texture2D(texture1, texCoord + vec2(widthInversed, 0));
    //vec4 F = texture2D(texture, texCoord + vec2(-widthInversed, heightInversed));
    vec4 G = texture2D(texture1, texCoord + vec2(0, heightInversed));
    //vec4 H = texture2D(texture, texCoord + vec2(widthInversed, heightInversed));

    //vec4 result = abs(k[1][1]*X + k[0][0]*A + k[1][0]*B + k[2][0]*C + k[0][1]*D + k[2][1]*E + k[0][2]*F + k[1][2]*G + k[2][2]*H);
    
    if(X.r != B.r || X.r != D.r || X.r != E.r || X.r != G.r) {
        gl_FragData[0] = vec4(texCoord, 0.0, X.r);
    }
    else {
        gl_FragData[0] = vec4(0.0, 0.0, 1.0, X.r);
    }

    X = texture2D(texture2, texCoord); 
    //vec4 A = texture2D(texture, texCoord + vec2(-widthInversed, -heightInversed));
    B = texture2D(texture2, texCoord + vec2(0, -heightInversed));
    //vec4 C = texture2D(texture, texCoord + vec2(widthInversed, -heightInversed));
    D = texture2D(texture2, texCoord + vec2(-widthInversed, 0));
    E = texture2D(texture2, texCoord + vec2(widthInversed, 0));
    //vec4 F = texture2D(texture, texCoord + vec2(-widthInversed, heightInversed));
    G = texture2D(texture2, texCoord + vec2(0, heightInversed));

    if(X.r != 0.0 && (B.r == 0.0 || D.r == 0.0 || E.r == 0.0 || G.r == 0.0)) {
        gl_FragData[1] = vec4(texCoord, 0.0, X.r);
    }
    else {
        gl_FragData[1] = vec4(0.0, 0.0, 1.0, X.r);
    }
}