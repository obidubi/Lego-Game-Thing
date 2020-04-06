package ShipStuff;

import org.lwjgl.util.vector.Vector3f;

public class Chunk {
	
	static final int GRID_SIZE_X = 16;
	static final int GRID_SIZE_Y = 16;
	static final int GRID_SIZE_Z = 16;
	
	static final float CELL_SIZE_X = 1.0f;
	static final float CELL_SIZE_Y = 1.0f;
	static final float CELL_SIZE_Z = 1.0f;
	
	static final float CHUNK_LENGTH_X = CELL_SIZE_X * GRID_SIZE_X;
	static final float CHUNK_LENGTH_Y = CELL_SIZE_Y * GRID_SIZE_Y;
	static final float CHUNK_LENGTH_Z = CELL_SIZE_Z * GRID_SIZE_Z;
	
	int[] grid = new int[GRID_SIZE_X * GRID_SIZE_Y * GRID_SIZE_Z];
	
	Vector3f position;
	
	Chunk topChunk;
	Chunk bottomChunk;
	Chunk rightChunk;
	Chunk leftChunk;
	Chunk frontChunk;
	Chunk backChunk;
	
	int x;
	int y;
	int z;
	
	boolean visited = false;
	
	Vector3f[] verts;
	
	Chunk(int x, int y, int z, Chunk parent, Vector3f direction) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		position = new Vector3f(x * CHUNK_LENGTH_X, y * CHUNK_LENGTH_Y, z * CHUNK_LENGTH_Z);
		
		verts = new Vector3f[] {new Vector3f(position.x + CHUNK_LENGTH_X, position.y + CHUNK_LENGTH_Y, position.z), 
								new Vector3f(position.x + CHUNK_LENGTH_X, position.y + CHUNK_LENGTH_Y, position.z + CHUNK_LENGTH_Z), 
								new Vector3f(position.x, position.y + CHUNK_LENGTH_Y, position.z + CHUNK_LENGTH_Z), 
								new Vector3f(position.x, position.y + CHUNK_LENGTH_Y, position.z), 
								new Vector3f(position.x + CHUNK_LENGTH_X, position.y, position.z), 
								new Vector3f(position.x + CHUNK_LENGTH_X, position.y, position.z + CHUNK_LENGTH_Z), 
								new Vector3f(position.x, position.y, position.z + CHUNK_LENGTH_Z), 
								new Vector3f(position.x, position.y, position.z)};
		
		if(parent != null) {
			if(direction.y == 1) {
				topChunk = parent;
			}else if(direction.y == -1) {
				bottomChunk = parent;
			}else if(direction.x == 1) {
				rightChunk = parent;
			}else if(direction.x == -1) {
				leftChunk = parent;
			}else if(direction.z == 1) {
				frontChunk = parent;
			}else if(direction.z == -1) {
				backChunk = parent;
			}
		}
	}
	
	public int getCellValue(int x, int y, int z) {
		return grid[x + y * GRID_SIZE_X + z * GRID_SIZE_X * GRID_SIZE_Y];
	}
	
	public void setCellValue(int x, int y, int z, int value) {
		grid[x + y * GRID_SIZE_X + z * GRID_SIZE_X * GRID_SIZE_Y] = value;
	}
	
	public Vector3f[] getCellVerts(int x, int y, int z) {
		Vector3f[] cellVerts = new Vector3f[] {new Vector3f(position.x + (float) x * CELL_SIZE_X + CELL_SIZE_X, position.y + (float) y * CELL_SIZE_Y + CELL_SIZE_Y, position.z + (float) z * CELL_SIZE_Z), 
											   new Vector3f(position.x + (float) x * CELL_SIZE_X + CELL_SIZE_X, position.y + (float) y * CELL_SIZE_Y + CELL_SIZE_Y, position.z + (float) z * CELL_SIZE_Z + CELL_SIZE_Z), 
											   new Vector3f(position.x + (float) x * CELL_SIZE_X, 				position.y + (float) y * CELL_SIZE_Y + CELL_SIZE_Y, position.z + (float) z * CELL_SIZE_Z + CELL_SIZE_Z), 
											   new Vector3f(position.x + (float) x * CELL_SIZE_X, 				position.y + (float) y * CELL_SIZE_Y + CELL_SIZE_Y, position.z + (float) z * CELL_SIZE_Z),
											   
											   new Vector3f(position.x + (float) x * CELL_SIZE_X + CELL_SIZE_X, position.y + (float) y * CELL_SIZE_Y, 				position.z + (float) z * CELL_SIZE_Z), 
											   new Vector3f(position.x + (float) x * CELL_SIZE_X + CELL_SIZE_X, position.y + (float) y * CELL_SIZE_Y, 				position.z + (float) z * CELL_SIZE_Z + CELL_SIZE_Z), 
											   new Vector3f(position.x + (float) x * CELL_SIZE_X, 				position.y + (float) y * CELL_SIZE_Y, 				position.z + (float) z * CELL_SIZE_Z + CELL_SIZE_Z), 
											   new Vector3f(position.x + (float) x * CELL_SIZE_X, 				position.y + (float) y * CELL_SIZE_Y, 				position.z + (float) z * CELL_SIZE_Z)};
		
		return cellVerts;
	}

}
