package entities;

import org.lwjgl.util.vector.Vector3f;

public class Light {

    private Vector3f position;
    private Vector3f color;
    private Vector3f attenuation = new Vector3f(1, 0, 0);
    private String name;

    public Light(Vector3f position, Vector3f color) {
        this.setPosition(position);
        this.setColor(color);
    }

    public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
        this.attenuation = attenuation;
        this.setPosition(position);
        this.setColor(color);
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return getName();
    }

}
