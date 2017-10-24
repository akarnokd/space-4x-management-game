package hu.akarnokd.s4xmg.tutorial;

import org.joml.Vector3f;

public class ViewItem {

    final Mesh mesh;

    final Vector3f position;

    float scale;

    final Vector3f rotation;

    public ViewItem(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0f, 0f, 0f);
        scale = 1f;
        rotation = new Vector3f(0f, 0f, 0f);
    }

    public Vector3f position() {
        return position;
    }

    public float scale() {
        return scale;
    }

    public Vector3f rotation() {
        return rotation;
    }

    public Mesh mesh() {
        return mesh;
    }

    public void position(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void scale(float f) {
        scale = f;
    }

    public void rotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }
}
