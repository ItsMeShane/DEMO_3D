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
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		List<Entity> entities = new ArrayList<>();
		List<Light> lights = new ArrayList<>();
		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();

		Terrain terrain = new Terrain(new TerrainTexturePack(
				new TerrainTexture(Loader.loadTexture("terrain/Grass.png")),
				new TerrainTexture(Loader.loadTexture("terrain/DirtyGrass.png")),
				new TerrainTexture(Loader.loadTexture("terrain/Dirt.png")),
				new TerrainTexture(Loader.loadTexture("terrain/Gravel.png"))),
				new TerrainTexture(Loader.loadTexture("terrain/blendMap.png")));

		Entity car = generateEntity("models/car.obj", "textures/car.png", 3, 0.2f, "Car");
		car.setPosition(3, terrain, 2);

		Entity tree = generateEntity("models/tree2.obj", "textures/tree2.png", 1.8f, 0.1f, "Tree");
		tree.setPosition(-10, terrain, 10);

		entities.add(car);
		entities.add(tree);


		Light sun = new Light(new Vector3f(0, 175, 0), new Vector3f(0.8f, 0.8f, 0.7f), new Vector3f(0.196f, 0.0013f, 3.8E-6f));
		lights.add(sun);


		while (!Display.isCloseRequested()) {
			camera.move(terrain);
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);


			renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, 0, 0, 0));
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();

	}


	private static Entity generateEntity(String objPath, String texturePath, float scale, float reflectivity, String name) {
		ModelData modelData = OBJFileLoader.loadOBJ(objPath);
		RawModel rawModel = Loader.loadToVAO(modelData.vertices(), modelData.textureCoords(), modelData.normals(), modelData.indices());
		TexturedModel texturedModel = new TexturedModel(rawModel, new ModelTexture(Loader.loadTexture(texturePath)));
		ModelTexture modelTexture = texturedModel.texture();
		modelTexture.setShineDamper(10);
//		modelTexture.setUseFakeLighting(true);
		modelTexture.setReflectivity(reflectivity);
		Entity entity = new Entity(texturedModel, scale, name);
		entity.setObjPath(objPath);
		entity.setTexturePath(texturePath, false);
		return entity;
	}

}


