package hu.akarnokd.s4xmg.tutorial;

import org.joml.*;

import java.lang.Math;

public class ViewTransformation {

    final Matrix4f projectionMatrix;

    final Matrix4f worldMatrix;

    public ViewTransformation() {
        projectionMatrix = new Matrix4f();
        worldMatrix = new Matrix4f();
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
}
