/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import scene.surface.mesh.Mesh;
import scene.Scene;

/**
 *
 * @author cmolikl
 */
public class ParametersLoader {
    
    public ParametersLoader() {};
    
    public void loadParameters(Scene<Mesh> scene, File file) throws FileNotFoundException {
        try {
            FileReader fr = new FileReader(file.getAbsolutePath());
            BufferedReader reader = new BufferedReader(fr);
            
            String line = reader.readLine();
            long lineNumber = 0;
            
            while(line != null) {
                lineNumber++;
                if(line.startsWith("#")) {
                    line = reader.readLine(); 
                    continue;
                }
                if(line.startsWith("C ")) {
                    String[] tokens = line.split(" ");
                    if(tokens.length != 3) {
                        System.err.println("Wrong number of tokens on line number " + lineNumber);
                        line = reader.readLine(); 
                        continue;
                    }
                    Mesh exteriorMesh = scene.getMesh(tokens[1]);
                    if(exteriorMesh == null) {
                        System.err.print("Error on line number " + lineNumber +". ");
                        System.err.println("Mesh with name " + tokens[1] + " does not exist.");
                        line = reader.readLine(); 
                        continue;
                    }
                    Mesh interiorMesh = scene.getMesh(tokens[2]);
                    if(interiorMesh == null) {
                        System.err.print("Error on line number " + lineNumber +". ");
                        System.err.println("Mesh with name " + tokens[2] + " does not exist.");
                        line = reader.readLine(); 
                        continue;
                    }
                    if(exteriorMesh.equals(interiorMesh)) {
                        System.err.print("Error on line number " + lineNumber +". ");
                        System.err.println("Meshes have same names.");
                        line = reader.readLine(); 
                        continue;
                    }
                    exteriorMesh.addChild(interiorMesh);
                }
                if(line.startsWith("B ")) {
                    String[] tokens = line.split(" ");
                    if(tokens.length != 3) {
                        System.err.println("Wrong number of tokens on line number " + lineNumber);
                        line = reader.readLine(); 
                        continue;
                    }
                    Mesh mesh = scene.getMesh(tokens[1]);
                    if(mesh == null) {
                        System.err.print("Error on line number " + lineNumber +". ");
                        System.err.println("Mesh with name " + tokens[1] + " does not exist.");
                        line = reader.readLine(); 
                        continue;
                    }
                    /*Mesh blockingMesh = scene.getMesh(tokens[2]);
                    if(blockingMesh == null) {
                        System.err.print("Error on line number " + lineNumber +". ");
                        System.err.println("Mesh with name " + tokens[2] + " does not exist.");
                        line = reader.readLine(); 
                        continue;
                    }
                    if(mesh.equals(blockingMesh)) {
                        System.err.print("Error on line number " + lineNumber +". ");
                        System.err.println("Meshes have same names.");
                        line = reader.readLine(); 
                        continue;
                    }
                    mesh.addHelper(blockingMesh);*/
                }
                line = reader.readLine(); 
            }
            reader.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }   
    }
    
    public void clearParameters(Scene<Mesh> scene) {
        for(Mesh mesh : scene.getAllMeshes()) {
            mesh.getChildren().clear();
            //mesh.getHelpers().clear();
        }
    }
}
