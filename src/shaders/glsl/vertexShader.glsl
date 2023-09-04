#version 400

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 pass_textureCoordinates;
out vec3 surfaceNormal;
out vec3 toLightVector[3];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[3];

uniform float useFakeLighting;

uniform float numberOfRows;
uniform vec2 offset;

// fog
const float density = 0.0; // 0.003
const float gradient = 5;

uniform vec4 plane;

void main(void){

    vec4 worldPosition = transformationMatrix * vec4(position,1.0);

    gl_ClipDistance[0] = dot(worldPosition, plane);

	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_textureCoordinates = (textureCoordinates / numberOfRows + offset);

    vec3 actualNormal = normal;
    if (useFakeLighting > 0.5) {
        actualNormal = vec3(0.0, 1, 0.0);
    }

	surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
    for (int i = 0; i < 3; i++) {
        toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }
	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    float distance = length(positionRelativeToCamera.xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}