package ShipStuff;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import Entities.Entity;
import Physics.Vector3;

public class ShipEntity {
	
	public Chunk center;
	
	public Vector3f position;
	public Vector3f rotation;
	
	public ShipEntity(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
		center = new Chunk(0, 0, 0, null, null);
		
		center.setCellValue(0, 0, 0, 1);
		
		System.out.println("Infinity: " + Float.POSITIVE_INFINITY + ", " + Float.NEGATIVE_INFINITY);
	}
	
	public Chunk raycastChunk(Vector3f position, Vector3f direction) {
		int xCoord = (int) (position.x / Chunk.CHUNK_LENGTH_X);
		int yCoord = (int) (position.y / Chunk.CHUNK_LENGTH_Y);
		int zCoord = (int) (position.z / Chunk.CHUNK_LENGTH_Z);
		
		if(position.x < 0) xCoord--;
		if(position.y < 0) yCoord--;
		if(position.z < 0) zCoord--;
		
		//System.out.println("Chunk coord: [" + xCoord + ", " + yCoord + ", " + zCoord + "]");
		System.out.println("Direction: [" + direction.x + ", " + direction.y + ", " + direction.z + "]");
		
		Chunk chunk = getChunkAtCoord(xCoord, yCoord, zCoord);
		
		if(chunk == null) {
			//System.out.println("Position not inside chunk. Checking for intersection...");
			ArrayList <Chunk> chunks = getAllChunks();
			for(Chunk chonk : chunks) {
				float tMin = Float.NEGATIVE_INFINITY;
				float tMax = Float.POSITIVE_INFINITY;
				
				float tx1 = (chonk.position.x - position.x) / direction.x;
				float tx2 = (chonk.position.x + Chunk.CHUNK_LENGTH_X - position.x) / direction.x;
				float ty1 = (chonk.position.y - position.y) / direction.y;
				float ty2 = (chonk.position.y + Chunk.CHUNK_LENGTH_Y - position.y) / direction.y;
				float tz1 = (chonk.position.z - position.z) / direction.z;
				float tz2 = (chonk.position.z + Chunk.CHUNK_LENGTH_Z - position.z) / direction.z;
				
				tMin = Math.max(tMin, Math.min(tx1, tx2));
				tMax = Math.min(tMax, Math.max(tx1, tx2));
				tMin = Math.max(tMin, Math.min(ty1, ty2));
				tMax = Math.min(tMax, Math.max(ty1, ty2));
				tMin = Math.max(tMin, Math.min(tz1, tz2));
				tMax = Math.min(tMax, Math.max(tz1, tz2));
				
				if(tMin < tMax) {
					System.out.println("Collision found!");
					return chonk;
				}
			}
		}
		
		return chunk;
	}
	
	public RaycastPackage raycast(Vector3f position, Vector3f direction) {
		System.out.println("Position: [" + position.x + ", " + position.y + ", " + position.z + "]");
		int xChunkCoord = (int) (position.x / Chunk.CHUNK_LENGTH_X);
		int yChunkCoord = (int) (position.y / Chunk.CHUNK_LENGTH_Y);
		int zChunkCoord = (int) (position.z / Chunk.CHUNK_LENGTH_Z);
		
		if(position.x < 0) xChunkCoord--;
		if(position.y < 0) yChunkCoord--;
		if(position.z < 0) zChunkCoord--;
		
		Chunk chunk = getChunkAtCoord(xChunkCoord, yChunkCoord, zChunkCoord);
		
		Vector3f start = position;
		Vector3f face = new Vector3f(0, 0, 0);
		
		if(chunk == null) {
			System.out.println("Position not inside chunk, finding closest along ray");
			start = new Vector3f(position.x + 1000 * direction.x, position.y + 1000 * direction.y, position.z + 1000 * direction.z);
			ArrayList <Chunk> chunks = getAllChunks();
			System.out.println("Number of chunks returned: " + chunks.size());
			for(Chunk chonk : chunks) {
				//Top face
				Vector3f intersect1 = rayPlaneIntersection(position, direction, chonk.verts[0], chonk.verts[1], chonk.verts[3]);
				if(intersect1 != null && subtract(intersect1, position).length() < subtract(start, position).length()) {
					System.out.println("Intersection with top face of chunk!");
					start = intersect1;
					chunk = chonk;
					face.set(0, 1, 0);
				}
				
				//Bottom face
				Vector3f intersect2 = rayPlaneIntersection(position, direction, chonk.verts[7], chonk.verts[6], chonk.verts[4]);
				if(intersect2 != null && subtract(intersect2, position).length() < subtract(start, position).length()) {
					System.out.println("Intersection with bottom face of chunk!");
					start = intersect2;
					chunk = chonk;
					face.set(0, -1, 0);
				}
				
				//Right face
				Vector3f intersect3 = rayPlaneIntersection(position, direction, chonk.verts[4], chonk.verts[5], chonk.verts[0]);
				if(intersect3 != null && subtract(intersect3, position).length() < subtract(start, position).length()) {
					System.out.println("Intersection with right face of chunk!");
					start = intersect3;
					chunk = chonk;
					face.set(1, 0, 0);
				}
				
				//Left face
				Vector3f intersect4 = rayPlaneIntersection(position, direction, chonk.verts[3], chonk.verts[2], chonk.verts[7]);
				if(intersect4 != null && subtract(intersect4, position).length() < subtract(start, position).length()) {
					System.out.println("Intersection with left face of chunk!");
					start = intersect4;
					chunk = chonk;
					face.set(-1, 0, 0);
				}
				
				//Front face
				Vector3f intersect5 = rayPlaneIntersection(position, direction, chonk.verts[0], chonk.verts[3], chonk.verts[4]);
				if(intersect5 != null && subtract(intersect5, position).length() < subtract(start, position).length()) {
					System.out.println("Intersection with front face of chunk!");
					start = intersect5;
					chunk = chonk;
					face.set(0, 0, -1); //Might want to set to -1 since it's technically the back face
				}
				
				//Back face
				Vector3f intersect6 = rayPlaneIntersection(position, direction, chonk.verts[0], chonk.verts[3], chonk.verts[4]);
				if(intersect6 != null && subtract(intersect6, position).length() < subtract(start, position).length()) {
					System.out.println("Intersection with back face of chunk!");
					start = intersect6;
					chunk = chonk;
					face.set(0, 0, 1);
				}
			}
			
			if(chunk == null) {
				System.out.println("NO CHUNK FOUND!");
				return null;
			}
		}
		
		System.out.println("Start: [" + start.x + ", " + start.y + ", " + start.z + "]");
		System.out.println("Chunk position: [" + chunk.position.x + ", " + chunk.position.y + ", " + chunk.position.z + "]");
		
		int xBlockCoord = (int) ((start.x - chunk.position.x) / Chunk.CELL_SIZE_X);
		int yBlockCoord = (int) ((start.y - chunk.position.y) / Chunk.CELL_SIZE_Y);
		int zBlockCoord = (int) ((start.z - chunk.position.z) / Chunk.CELL_SIZE_Z);
		
		System.out.println("Cell coordinate: [" + xBlockCoord + ", " + yBlockCoord + ", " + zBlockCoord + "]");
		
		int value = chunk.getCellValue(xBlockCoord, yBlockCoord, zBlockCoord);
		
		while(value == 0) {
			Vector3f[] verts = chunk.getCellVerts(xBlockCoord, yBlockCoord, zBlockCoord);
			
			System.out.println("Checking cell: [" + xBlockCoord + ", " + yBlockCoord + ", " + zBlockCoord + "]");
			
			//Top intersection (bottom of next cell)
			if(direction.y > 0) {
				Vector3f intersect = rayPlaneIntersection(start, direction, verts[0], verts[1], verts[3]);
				if(intersect != null) {
					start = intersect;
					face.set(0, -1, 0); //This is negative because I am hitting the bottom face of the cell above this one
					yBlockCoord++;
					if(yBlockCoord >= Chunk.GRID_SIZE_Y) {
						yBlockCoord = 0;
						if(chunk.topChunk != null) {
							chunk = chunk.topChunk;
						}else {
							return raycast(new Vector3f(start.x + 0.01f * direction.x, start.y + 0.01f * direction.y, start.z + 0.01f * direction.z), direction);
						}
					}
				}
			}
			//Bottom intersection (top of next cell)
			if(direction.y < 0) {
				Vector3f intersect = rayPlaneIntersection(start, direction, verts[4], verts[7], verts[5]);
				if(intersect != null) {
					start = intersect;
					face.set(0, 1, 0); //This is negative because I am hitting the bottom face of the cell above this one
					yBlockCoord--;
					if(yBlockCoord < 0) {
						yBlockCoord = Chunk.GRID_SIZE_Y - 1;
						if(chunk.bottomChunk != null) {
							chunk = chunk.bottomChunk;
						}else {
							return raycast(new Vector3f(start.x + 0.01f * direction.x, start.y + 0.01f * direction.y, start.z + 0.01f * direction.z), direction);
						}
					}
				}
			}
			//Right intersection (left of next cell)
			if(direction.x > 0) {
				Vector3f intersect = rayPlaneIntersection(start, direction, verts[4], verts[5], verts[0]);
				if(intersect != null) {
					start = intersect;
					face.set(-1, 0, 0);
					xBlockCoord++;
					if(xBlockCoord >= Chunk.GRID_SIZE_X) {
						xBlockCoord = 0;
						if(chunk.leftChunk != null) {
							chunk = chunk.leftChunk;
						}else {
							return raycast(new Vector3f(start.x + 0.01f * direction.x, start.y + 0.01f * direction.y, start.z + 0.01f * direction.z), direction);
						}
					}
				}
			}
			//Left intersection (right of next cell)
			if(direction.x < 0) {
				Vector3f intersect = rayPlaneIntersection(start, direction, verts[3], verts[2], verts[7]);
				if(intersect != null) {
					System.out.println("Cell left intersection found!");
					start = intersect;
					face.set(1, 0, 0);
					xBlockCoord--;
					if(xBlockCoord < 0) {
						xBlockCoord = Chunk.GRID_SIZE_X - 1;
						if(chunk.rightChunk != null) {
							chunk = chunk.rightChunk;
						}else {
							return raycast(new Vector3f(start.x + 0.01f * direction.x, start.y + 0.01f * direction.y, start.z + 0.01f * direction.z), direction);
						}
					}
				}else {
					System.out.println("Cell left intersection was null");
				}
			}
			//Front intersection (back of next cell)
			if(direction.z < 0) {
				Vector3f intersect = rayPlaneIntersection(start, direction, verts[0], verts[3], verts[4]);
				if(intersect != null) {
					System.out.println("Cell front intersection found!");
					start = intersect;
					face.set(0, 0, 1);
					zBlockCoord--;
					if(zBlockCoord < 0) {
						zBlockCoord = Chunk.GRID_SIZE_Z - 1;
						if(chunk.backChunk != null) {
							chunk = chunk.backChunk;
						}else {
							return raycast(new Vector3f(start.x + 0.01f * direction.x, start.y + 0.01f * direction.y, start.z + 0.01f * direction.z), direction);
						}
					}
				}else {
					System.out.println("Cell front intersect was null");
				}
			}
			//Back intersection (front of next cell)
			if(direction.z > 0) {
				Vector3f intersect = rayPlaneIntersection(start, direction, verts[5], verts[6], verts[1]);
				if(intersect != null) {
					System.out.println("Cell back intersection found!");
					start = intersect;
					face.set(0, 0, -1);
					zBlockCoord++;
					if(zBlockCoord >= Chunk.GRID_SIZE_Z) {
						zBlockCoord = 0;
						if(chunk.frontChunk != null) {
							chunk = chunk.frontChunk;
						}else {
							return raycast(new Vector3f(start.x + 0.01f * direction.x, start.y + 0.01f * direction.y, start.z + 0.01f * direction.z), direction);
						}
					}
				}else {
					System.out.println("Cell back intersection was null");
				}
			}
			
			value = chunk.getCellValue(xBlockCoord, yBlockCoord, zBlockCoord);
		}
		
		return new RaycastPackage(chunk, xBlockCoord, yBlockCoord, zBlockCoord, face);
	}
	
	public Vector3f rayPlaneIntersection(Vector3f V0, Vector3f Vd, Vector3f p0, Vector3f p1, Vector3f p3) {
		Vector3f normal = normalize(crossProduct(subtract(p1, p0), subtract(p3, p0)));
		float numerator = dotProduct(p0, normal) - dotProduct(V0, normal);
		float denominator = dotProduct(Vd, normal);
		if(denominator == 0) return null;
		float t = numerator / denominator;
		//if(t < 0) return null;
		Vector3f intersect = new Vector3f(V0.x + t * Vd.x, V0.y + t * Vd.y, V0.z + t * Vd.z);
		
		float area = dotProduct(normal, crossProduct(subtract(p1, p0), subtract(p3, p0)));
		float c1 = dotProduct(normal, crossProduct(subtract(p1, p0), subtract(intersect, p0))) / area;
		float c2 = dotProduct(normal, crossProduct(subtract(p3, p0), subtract(intersect, p3))) / area;
		
		if(c1 >= 0 && c1 <= 1 && c2 >= 0 && c2 <= 1) {
			return intersect;
		}
		
		return null;
	}
	
	public float dotProduct(Vector3f a, Vector3f b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}
	
	public Vector3f crossProduct(Vector3f a, Vector3f b) {
		return new Vector3f(a.y * b.z - a.z * b.y, 
						  a.z * b.x - a.x * b.z, 
						  a.x * b.y - a.y * b.x);
	}
	
	public Vector3f subtract(Vector3f a, Vector3f b) {
		return new Vector3f(a.x - b.x, a.y - b.y, a.z - b.z);
	}
	
	public Vector3f normalize(Vector3f v) {
		float length = (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
		if(length == 0) return null;
		v.x /= length;
		v.y /= length;
		v.z /= length;
		return v;
	}
	
	public Chunk getChunkAtCoord(int xCoord, int yCoord, int zCoord) {
		//System.out.println("Getting chunk at coordinate: [" + xCoord + ", " + yCoord + ", " + zCoord + "]");
		ArrayList <Chunk> chunks = new ArrayList<Chunk>();
		chunks.add(center);
		while(chunks.size() > 0) {
			Chunk chunk = chunks.get(0);
			if(chunk.x == xCoord && chunk.y == yCoord && chunk.z == zCoord) {
				chunks.clear();
				chunks.add(center);
				while(chunks.size() > 0) {
					Chunk chunk2 = chunks.get(0);
					chunk2.visited = false;
					if(chunk2.topChunk != null && chunk2.topChunk.visited) chunks.add(chunk2.topChunk);
					if(chunk2.bottomChunk != null && chunk2.bottomChunk.visited) chunks.add(chunk2.bottomChunk);
					if(chunk2.rightChunk != null && chunk2.rightChunk.visited) chunks.add(chunk2.rightChunk);
					if(chunk2.leftChunk != null && chunk2.leftChunk.visited) chunks.add(chunk2.leftChunk);
					if(chunk2.frontChunk != null && chunk2.frontChunk.visited) chunks.add(chunk2.frontChunk);
					if(chunk2.backChunk != null && chunk2.backChunk.visited) chunks.add(chunk2.backChunk);
					chunks.remove(0);
				}
				//System.out.println("Returning discovered chunk");
				return chunk;
			}
			chunk.visited = true;
			if(chunk.topChunk != null && !chunk.topChunk.visited) chunks.add(chunk.topChunk);
			if(chunk.bottomChunk != null && !chunk.bottomChunk.visited) chunks.add(chunk.bottomChunk);
			if(chunk.rightChunk != null && !chunk.rightChunk.visited) chunks.add(chunk.rightChunk);
			if(chunk.leftChunk != null && !chunk.leftChunk.visited) chunks.add(chunk.leftChunk);
			if(chunk.frontChunk != null && !chunk.frontChunk.visited) chunks.add(chunk.frontChunk);
			if(chunk.backChunk != null && !chunk.backChunk.visited) chunks.add(chunk.backChunk);
			chunks.remove(0);
		}
		//System.out.println("No chunk found at location");
		return null;
	}
	
	public ArrayList<Chunk> getAllChunks() {
		//System.out.println("Getting all chunks...");
		ArrayList <Chunk> chunks = new ArrayList<Chunk>();
		ArrayList <Chunk> temp = new ArrayList<Chunk>();
		temp.add(center);
		while(temp.size() > 0) {
			Chunk chunk = temp.get(0);
			chunks.add(chunk);
			chunk.visited = true;
			if(chunk.topChunk != null && !chunk.topChunk.visited) temp.add(chunk.topChunk);
			if(chunk.bottomChunk != null && !chunk.bottomChunk.visited) temp.add(chunk.bottomChunk);
			if(chunk.rightChunk != null && !chunk.rightChunk.visited) temp.add(chunk.rightChunk);
			if(chunk.leftChunk != null && !chunk.leftChunk.visited) temp.add(chunk.leftChunk);
			if(chunk.frontChunk != null && !chunk.frontChunk.visited) temp.add(chunk.frontChunk);
			if(chunk.backChunk != null && !chunk.backChunk.visited) temp.add(chunk.backChunk);
			temp.remove(0);
		}
		temp.add(center);
		while(temp.size() > 0) {
			Chunk chunk = temp.get(0);
			chunk.visited = false;
			if(chunk.topChunk != null && chunk.topChunk.visited) temp.add(chunk.topChunk);
			if(chunk.bottomChunk != null && chunk.bottomChunk.visited) temp.add(chunk.bottomChunk);
			if(chunk.rightChunk != null && chunk.rightChunk.visited) temp.add(chunk.rightChunk);
			if(chunk.leftChunk != null && chunk.leftChunk.visited) temp.add(chunk.leftChunk);
			if(chunk.frontChunk != null && chunk.frontChunk.visited) temp.add(chunk.frontChunk);
			if(chunk.backChunk != null && chunk.backChunk.visited) temp.add(chunk.backChunk);
			temp.remove(0);
		}
		//System.out.println("Returning discovered chunks");
		return chunks;
	}

}
