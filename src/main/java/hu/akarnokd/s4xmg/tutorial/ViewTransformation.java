package hu.akarnokd.s4xmg.tutorial;

import org.joml.*;

import java.lang.Math;

public class ViewTransformation {

    final Matrix4f projectionMatrix;

    final Matrix4f worldMatrix;

    final Matrix4f orthoMatrix;

    public ViewTransformation() {
        projectionMatrix = new Matrix4f();
        worldMatrix = new Matrix4f();
        orthoMatrix = new Matrix4f();
    }

    public Matrix4f getProjectionMatrix(float fov, int width, int height, float zNear, float zFar) {
        float aspect = width / (float)height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspect, zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f getWorldMatrix(Vector3f position, float scale, Vector3f rotation) {
        worldMatrix.identity()
                .translate(position)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(scale)
        ;
        return worldMatrix;
    }

    public Matrix4f getOrthoProjectionMatrix(int left, int right, int bottom, int top) {
        orthoMatrix.identity()
                .ortho2D(left, right, bottom, top);
        return orthoMatrix;
    }

    public Matrix4f getOrthoProjectionMatrix(int left, int right, int bottom, int top, float correction) {
        orthoMatrix.identity()
                .ortho2D(left, right, bottom, top)
                .translate(correction, correction, 0f);
        return orthoMatrix;
    }

    public Matrix4f getOrthoProjectModelMatrix(float x, float y, float scale, float rotation, Matrix4f ortho) {
        return ortho.translate(x, y, 0f)
                .rotateZ((float) Math.toRadians(rotation))
                .scale(scale);
    }
}
