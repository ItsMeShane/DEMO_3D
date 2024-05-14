#version 400

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector[3];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColor[3];
uniform vec3 attenuation[3];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {

    vec4 blendMapColor = texture(blendMap, pass_textureCoordinates);

    float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
    vec2 tiledCoordinates = pass_textureCoordinates * 20;
    vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoordinates) * backTextureAmount;
    vec4 rTextureColor = texture(rTexture, tiledCoordinates) * blendMapColor.r;
    vec4 gTextureColor = texture(gTexture, tiledCoordinates) * blendMapColor.g;
    vec4 bTextureColor = texture(bTexture, tiledCoordinates) * blendMapColor.b;

    vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0; i < 3; i++) {
        float distance = length(toLightVector[i]);
        float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDot1 = dot(unitNormal, unitLightVector);
        float brightness = max(nDot1, 0.0);
        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        totalDiffuse += (brightness * lightColor[i] / attenuationFactor);
        totalSpecular += (dampedFactor * reflectivity * lightColor[i]) / attenuationFactor;
    }
    totalDiffuse = max(totalDiffuse, 0.7); // min brightness 0-1


    out_Color = vec4(totalDiffuse, 1.0) * totalColor + vec4(totalSpecular, 1.0);
    out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);

}