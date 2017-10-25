#version 330

layout (location=0) in vec3 position;

uniform mat4 projModelMatrix;

void main() {
    gl_Position = projModelMatrix * vec4(position, 1);
}