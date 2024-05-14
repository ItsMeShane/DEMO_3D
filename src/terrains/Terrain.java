package terrains;

import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;


public class Terrain {

    public final static int SIZE = 800;

    private final float x;
    private final float z;
    private final RawModel model;
    private final TerrainTexturePack texturePack;
    private final TerrainTexture blendMap;

    private float[][] terrainHeights;

    public Terrain(TerrainTexturePack texturePack, TerrainTexture blendMap) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.z = (float) -SIZE / 2;
        this.x = (float) -SIZE / 2;
        this.model = generateTerrain();
    }

    public float getHeightOfTerrain(float x, float z) {
        float terrainX = x - getX();
        float terrainZ = z - getZ();
        float gridSqr = SIZE / (float) (terrainHeights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSqr);
        int gridZ = (int) Math.floor(terrainZ / gridSqr);
        if (gridX >= terrainHeights.length - 1 || gridZ >= terrainHeights.length - 1 || gridX < 0 || gridZ < 0)
            return -100;
        float xCoord = (terrainX % gridSqr) / gridSqr;
        float zCoord = (terrainZ % gridSqr) / gridSqr;
        if (xCoord <= (1 - zCoord)) {
            return  Maths.barryCentric(new Vector3f(0, terrainHeights[gridX][gridZ], 0), new Vector3f(1,
                            terrainHeights[gridX + 1][gridZ], 0), new Vector3f(0,
                            terrainHeights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            return Maths.barryCentric(new Vector3f(1, terrainHeights[gridX + 1][gridZ], 0),
                    new Vector3f(1, terrainHeights[gridX + 1][gridZ + 1], 1),
                    new Vector3f(0, terrainHeights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
    }

    private RawModel generateTerrain(){

        final int VERTEX_COUNT = 100;

        HeightsGenerator generator = new HeightsGenerator((int) (getX() / SIZE), (int) (getZ() / SIZE), VERTEX_COUNT);

        terrainHeights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(j, i, generator);
                terrainHeights[j][i] = height;
                vertices[vertexPointer*3+1] = height; // terrain height
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, generator);
                normals[vertexPointer*3]   = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return Loader.loadToVAO(vertices, textureCoords, normals, indices);
    }



    private float getHeight(int x, int z, HeightsGenerator generator) {
        return generator.generateHeight(x, z);
    }

    private Vector3f calculateNormal(int x, int z, HeightsGenerator generator) {
        float heightU = getHeight(x, z + 1, generator);
        float heightD = getHeight(x, z - 1, generator);
        float heightL = getHeight(x - 1, z, generator);
        float heightR = getHeight(x + 1, z, generator);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }
}
