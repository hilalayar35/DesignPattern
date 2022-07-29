import java.io.*;
import java.util.*;

public class AdapterPatternTest {
	public static void main(String[] args) {
		List<Geometry> geometries = new ArrayList<Geometry>();
		
		// cube
		double width = 10.0;
		Geometry cube = new Cube(width);
		cube.setFileName("cube.obj");
		geometries.add(cube);
		
		// sphere
		double radius = 10.0;
		int divisions = 20;
		Geometry sphere = new Sphere(radius, divisions);
		sphere.setFileName("sphere.obj");
		geometries.add(sphere);
				
		// torus
		double innerRadius = 1.0;
		double outerRadius = 3.0;
		int subdivisionCount = 30;
		Geometry torus = new TorusAdapter(new Torus3D(), innerRadius, outerRadius, subdivisionCount);
		torus.setFileName("torus.obj");
		geometries.add(torus);
		
		for (Geometry geometry : geometries) {
			geometry.save();
			
			System.out.println(geometry.getClass().getSimpleName() + " is saved as " + geometry.getFileName());
		}
	}
}


// well-designed 3D geometry library
class Vertex {
	double x;
	double y;
	double z;
	
	Vertex() { }
	
	Vertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}

class Face {
	final List<Integer> vertexIndices = new ArrayList<Integer>();
	
	Face() { }
	
	Face(int[] indices) {
		for (int index : indices) {
			vertexIndices.add(index);
		}
	}
}

abstract class Geometry {
	final public List<Vertex> vertices = new ArrayList<Vertex>();
	final public List<Face> faces = new ArrayList<Face>();
	private String fileName;
	
	void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	String getFileName() {
		return fileName;
	}
	
	void save() {
		try {
			FileWriter writer = new FileWriter(fileName);		
			
			for (Vertex vertex : vertices) {
				writer.write("v " + vertex.x + " " + vertex.y + " " + vertex.z + "\n");
			}

			for (Face face : faces) {
				writer.write("f");
				for (int index : face.vertexIndices) {
					writer.write(" " + (index + 1));
				}
				writer.write("\n");
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}	
}


class Cube extends Geometry {
	Cube(double size) {
		vertices.add(new Vertex(-size/2.0, -size/2.0, size/2.0));
		vertices.add(new Vertex(size/2.0, -size/2.0, size/2.0));
		vertices.add(new Vertex(size/2.0, size/2.0, size/2.0));
		vertices.add(new Vertex(-size/2.0, size/2.0, size/2.0));

		vertices.add(new Vertex(-size/2.0, -size/2.0, -size/2.0));
		vertices.add(new Vertex(size/2.0, -size/2.0, -size/2.0));
		vertices.add(new Vertex(size/2.0, size/2.0, -size/2.0));
		vertices.add(new Vertex(-size/2.0, size/2.0, -size/2.0));
				
		faces.add(new Face(new int[] {0, 1, 2}));
		faces.add(new Face(new int[] {2, 3, 0}));
		faces.add(new Face(new int[] {1, 5, 6}));
		faces.add(new Face(new int[] {6, 2, 1}));
		faces.add(new Face(new int[] {7, 6, 5,}));
		faces.add(new Face(new int[] {5, 4, 7}));
		faces.add(new Face(new int[] {4, 0, 3}));
		faces.add(new Face(new int[] {3, 7, 4}));
		faces.add(new Face(new int[] {4, 5, 1}));
		faces.add(new Face(new int[] {1, 0, 4}));
		faces.add(new Face(new int[] {3, 2, 6}));
		faces.add(new Face(new int[] {6, 7, 3}));
	}
}

class Sphere extends Geometry {
	Sphere(double radius, int subdivisionCount) {
		double pitchInc = (Math.PI / (subdivisionCount - 1));
		double rotInc   = (2.0 * Math.PI / subdivisionCount);
		  
		for (int p=0; p<subdivisionCount; p++) {
			double out = Math.abs(radius * Math.sin(p * pitchInc));
			double y = radius * Math.cos(p * pitchInc);

			for (int s=0; s<subdivisionCount; s++) {
				double x = out * Math.cos(s * rotInc);
				double z = out * Math.sin(s * rotInc);
				
				vertices.add(new Vertex(x, y ,z));
			}
		}		

		for (int p=0; p<subdivisionCount; p++) {
			int po = p % subdivisionCount;
			int pn = (p + 1) % subdivisionCount;
			
			for (int s=0; s<subdivisionCount; s++) {
				int sn = (s + 1) % subdivisionCount;

				int i1 = po * subdivisionCount + s;
				int i2 = po * subdivisionCount + sn;
				int i3 = pn * subdivisionCount + sn;
				int i4 = pn * subdivisionCount + s;
				
				faces.add(new Face(new int[] {i1, i2, i3, i4}));
			}
		}		
	}
}


// TorusAdapter class
// since you can construct the geometry in TorusAdapter you do not need to keep private copy of Torus3D torus3D (Adaptee)
// but, generally you need to take a private copy of Adeptee in the constructor
class TorusAdapter extends Geometry {
	
	TorusAdapter(Torus3D torus3D, double innerRadius, double outerRadius, int subdivisionCount) {
		for(double[] vertice : torus3D.createVertices(innerRadius, outerRadius, subdivisionCount)){
			vertices.add(new Vertex(vertice[0], vertice[1], vertice[2]));
		}
		
		for(int[] face : torus3D.createFaces(subdivisionCount)){
			faces.add(new Face(face));
		}
	}
}

// third-party 3D geometry library (spaghetti code)
class Torus3D {
	List<double[]> createVertices(double innerRadius, double outerRadius, int subdivisionCount) {
		List<double[]> vertices = new ArrayList<double[]>();
		
		double dp = (2.0 * Math.PI) / subdivisionCount;
		double dt = (2.0 * Math.PI) / subdivisionCount;

		for (int stack = 0; stack < subdivisionCount; stack++) {
			double theta = dt * stack;

			for (int slice = 0; slice < subdivisionCount; slice++) {
			    double phi = dp * slice;
			    double v[] = new double[3];
			    v[0] = Math.cos(theta) * (outerRadius + Math.cos(phi) * innerRadius);
			    v[1] = Math.sin(theta) * (outerRadius + Math.cos(phi) * innerRadius);
			    v[2] = Math.sin(phi) * innerRadius;
			    vertices.add(v);
			}
		}
		
		return vertices;
	}
	
	List<int[]> createFaces(int subdivisionCount) {
		List<int[]> faces = new ArrayList<int[]>();
		
		for (int p=0; p<subdivisionCount; p++) {
			int pn = (p + 1) % subdivisionCount;
			
			for (int s=0; s<subdivisionCount; s++) {
				int sn = (s + 1) % subdivisionCount;

				int i1 = p * subdivisionCount + s;
				int i2 = p * subdivisionCount + sn;
				int i3 = pn * subdivisionCount + sn;
				int i4 = pn * subdivisionCount + s;
				
				faces.add(new int[] {i4, i3, i2, i1});
			}
		}	
		
		return faces;
	}
	
}	

