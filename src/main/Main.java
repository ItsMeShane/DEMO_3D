package main;


import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		List<Entity> entities = new ArrayList<>();
		List<Light> lights = new ArrayList<>();
		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();


		Entity car = generateEntity("models/car.obj", "textures/car.png", 3, 0, 0, 2, 0.2f, "Car");
		Entity tree = generateEntity("models/tree2.obj", "textures/tree2.png", -10, 0, 0, 0.5f, 0.2f, "Tree");
		entities.add(car);
		entities.add(tree);

		Light light = new Light(new Vector3f(0, 6, 0), new Vector3f(0.6f, 0.6f, 0), new Vector3f(0.5f, 8.0E-4f, 3.2E-6f));
		lights.add(light);


		while (!Display.isCloseRequested()) {
			camera.move();
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);


			renderer.renderScene(entities, lights, camera, new Vector4f(0, 0, 0, 0));
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();

	}


	private static Entity generateEntity(String objPath, String texturePath, float xPos, float yPos, float zPos, float scale, float reflectivity, String name) {
		ModelData modelData = OBJFileLoader.loadOBJ(objPath);
		RawModel rawModel = Loader.loadToVAO(modelData.vertices(), modelData.textureCoords(), modelData.normals(), modelData.indices());
		TexturedModel texturedModel = new TexturedModel(rawModel, new ModelTexture(Loader.loadTexture(texturePath)));
		ModelTexture modelTexture = texturedModel.texture();
		modelTexture.setShineDamper(10);
//		modelTexture.setUseFakeLighting(true);
		modelTexture.setReflectivity(reflectivity);
		Entity entity = new Entity(texturedModel, new Vector3f(xPos, yPos, zPos), 0, 0, 0, scale, name);
		entity.setObjPath(objPath);
		entity.setTexturePath(texturePath, false);
		return entity;
	}

}


