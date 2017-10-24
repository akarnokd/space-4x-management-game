#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec3 inColor;

out vec3 outColor;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main() {
    gl_Position = projectionMatrix * worldMatrix * vec4(position, 1.0);

    outColor = inColor;
}