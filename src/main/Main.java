package main;


import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particles.ParticleMaster;
import particles.ParticleSystem;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.WaterRenderer;
import shaders.WaterShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.ParticleTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		List<Entity> entities = new ArrayList<>();
		List<Light> lights = new ArrayList<>();
		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();

		// particles
		ParticleMaster.init(renderer.getProjectionMatrix());
		ParticleSystem fireworkParticles = new ParticleSystem(new ParticleTexture(Loader.loadTexture("particles/fireworks.png"), 4), 15, 50, 2, 10, -50f);
		fireworkParticles.setDirection(new Vector3f(0, 1, 0), 0.1f);
		ParticleSystem smokeParticles = new ParticleSystem(new ParticleTexture(Loader.loadTexture("particles/smoke.png"), 8), 20, 10, 1, 10, -7);
		smokeParticles.setDirection(new Vector3f(0, 1, 0), 0.3f);

		// terrain
		Terrain terrain = new Terrain(new TerrainTexturePack(
				new TerrainTexture(Loader.loadTexture("terrain/Grass.png")),
				new TerrainTexture(Loader.loadTexture("terrain/DirtyGrass.png")),
				new TerrainTexture(Loader.loadTexture("terrain/Dirt.png")),
				new TerrainTexture(Loader.loadTexture("terrain/Gravel.png"))),
				new TerrainTexture(Loader.loadTexture("terrain/blendMap.png")));

		MousePicker mouse = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

		// entities
		Entity car = generateEntity("models/car.obj", "textures/car.png", 3, 0.2f, "Car");
		car.setPosition(3, terrain, 2);

		Entity tree = generateEntity("models/tree2.obj", "textures/tree2.png", 1.8f, 0.1f, "Tree");
		tree.setPosition(-45, terrain, -2);
		tree.getPosition().y-=2.4f; // put roots underground

		entities.add(car);
		entities.add(tree);

		// lights
		Light sun = new Light(new Vector3f(0, 175, 0), new Vector3f(0.8f, 0.8f, 0.7f), new Vector3f(0.196f, 0.0013f, 3.8E-6f));
		lights.add(sun);
		Light waterLight = new Light(new Vector3f(0, 20, -250), new Vector3f(0.8f, 0.8f, 0.6f), new Vector3f(0.2f, 0.015f, 1.0E-5f));
		lights.add(waterLight);

		// water
		WaterFrameBuffers waterFrameBuffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(waterShader, renderer.getProjectionMatrix(), waterFrameBuffers);

		List<WaterTile> waterTiles = new ArrayList<>();
		waterTiles.add(new WaterTile(0, -225, WaterTile.WATER_HEIGHT));

		Entity selectedEntity = null; // used to interact with entities


		while (!Display.isCloseRequested()) {
			camera.move(terrain);

			ParticleMaster.update();
			mouse.update();

			// mouse interaction
			if (Mouse.isButtonDown(1)) {
				Vector3f collisionPoint = mouse.getCurrentTerrainPoint();
				if (collisionPoint != null) {
					if (selectedEntity == null) {
						for (Entity entity : entities) {
							if (hitEntity(entity, collisionPoint)) {
								selectedEntity = entity;
							}
						}
						fireworkParticles.generateParticles(new Vector3f(collisionPoint.x, collisionPoint.y + 3, collisionPoint.z));
					}
					if (selectedEntity != null) {
						selectedEntity.setPosition(collisionPoint);
						switch (selectedEntity.getName()) {
							case "Tree" -> selectedEntity.getPosition().y -= 2.4f;
							case "Car" -> smokeParticles.generateParticles(collisionPoint);
						}
					}
				}
			} else {
				selectedEntity = null;
			}


			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			// water
			float waterHeight = WaterTile.WATER_HEIGHT;
			waterFrameBuffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getViewPointPosition().y - waterHeight);
			camera.getViewPointPosition().y-=distance;
			camera.invertPitch();
			renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, 1, 0, -waterHeight+3f));
			camera.getViewPointPosition().y+=distance;
			camera.invertPitch();
			waterFrameBuffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, -1, 0, waterHeight+3f));
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			waterFrameBuffers.unbindCurrentFrameBuffer();


			renderer.renderScene(entities, terrain, lights, camera, new Vector4f(0, 0, 0, 0));
			waterRenderer.render(waterTiles, camera);

			ParticleMaster.renderParticles(camera);

			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		waterFrameBuffers.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();

	}

	private static boolean hitEntity(Entity entity, Vector3f vector3f) {
		float dx = entity.getPosition().x - vector3f.x;
		float dy = entity.getPosition().y - vector3f.y;
		float dz = entity.getPosition().z - vector3f.z;
		float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		return distance <= 10.0f; // error margin == 10
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


