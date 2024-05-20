#version 300 es
in vec4 vertexPosition;
in vec4 vertexTexCoord;
out vec4 tex;
out vec4 modelPosition;

uniform struct {
    mat4 modelMatrix;
} gameObject;

// Camera struct with the view projection matrix
uniform struct {
    mat4 viewProjMatrix;
} camera;

void main(void) {
    tex = vertexTexCoord;
    modelPosition = vertexPosition;
    
    // Transform the vertex position to world space
    vec4 worldPosition = vertexPosition * gameObject.modelMatrix;

    // Then transform to camera space
    gl_Position = worldPosition *  camera.viewProjMatrix;
}