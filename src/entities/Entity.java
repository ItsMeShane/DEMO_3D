package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;


public class Entity {

	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private String name;
	private int textureIndex = 0;

	//
	private String objPath;
	private String texturePath;

	public Entity(TexturedModel model, float scale, String name) {
		setModel(model);
		setName(name);
		setScale(scale);
		setPosition(new Vector3f());
	}

	public Entity(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.textureIndex = textureIndex;
		setModel(model);
		setPosition(position);
		setRotX(rotX);
		setRotY(rotY);
		setRotZ(rotZ);
		setScale(scale);
	}

	public float getTextureXOffset() {
		int col = textureIndex % model.texture().getNumberOfRows();
		return (float) col / model.texture().getNumberOfRows();
	}

	public float getTextureYOffset() {
		int row = textureIndex / model.texture().getNumberOfRows();
		return (float) row / model.texture().getNumberOfRows();
	}

	public void increasePosition(float dx, float dy, float dz) {
//		this.position.x += dx;
//		this.position.y += dy;
//		this.position.z += dz;
		position.translate(dx, dy, dz);
	}

	public void increaseRotation(float dx, float dy, float dz) {
		setRotX(getRotX() + dx);
		setRotY(getRotY() + dy);
		setRotZ(getRotZ() + dz);
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public void setPosition(float x, Terrain terrain, float z) {
		this.position = new Vector3f(x, terrain.getHeightOfTerrain(x, z), z);
	}

	public void setRotation(Vector3f rot) {
		setRotX(rot.getX());
		setRotY(rot.getY());
		setRotZ(rot.getZ());
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObjPath() {
		return objPath;
	}

	public void setObjPath(String objPath) {
		this.objPath = objPath;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public void setTexturePath(String texturePath, boolean shouldUpdateModel) {
		this.texturePath = texturePath;
	}
}
