/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scene;

import gleem.BSphere;
import gleem.BSphereProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import scene.surface.Surface;

/**
 *
 * @author cmolikl
 */
public class Scene<T extends Surface> implements BSphereProvider {
    
    private HashMap<String, T> meshes;
    private HashMap<String, Integer> meshesNameConflicts;
    private HashMap<Integer, T> id2mesh;
    private BSphere boundingSphere;
    public String path;
    public String fileName;
    public String extension;

    
    public Scene() {
        super();
        meshes = new HashMap<>();
        meshesNameConflicts = new HashMap<>();
        id2mesh = new HashMap<>();
    }
    
    public void addMesh(T mesh) {
        if(meshes.get(mesh.getName()) != null) {
            int conflicts = meshesNameConflicts.getOrDefault(mesh.getName(), 0);
            conflicts++;
            meshesNameConflicts.put(mesh.getName(), conflicts);
            mesh.name = mesh.name + "." + conflicts;
        }
        meshes.put(mesh.getName(), mesh);
        id2mesh.put(mesh.id, mesh);
    }
    
    public T getMesh(String name) {
        return meshes.get(name);
    }
    
    public T getMesh(Integer id) {
        return id2mesh.get(id);
    }

    public T removeMesh(String meshName) {
        T mesh = meshes.remove(meshName);
        if(mesh != null) {
            id2mesh.remove(mesh.id);
        }
        return mesh;
    }
    
    public void changeMeshIds(HashMap<Integer, T> id2mesh) {
        this.id2mesh = id2mesh;
    }
    
    public Collection<T> getAllMeshes() {
        return meshes.values();
    }
    
    public Collection<String> getAllMeshesNames() {
        return meshes.keySet();
    }
    
    public Collection<Integer> getAllMeshesIds() {
        return id2mesh.keySet();
    }
    
    public int getSize() {
        return meshes.size();
    }

    public void replaceWith(Scene<T> scene) {
        meshes = scene.meshes;
        id2mesh = scene.id2mesh;
        boundingSphere = scene.boundingSphere;
        path = scene.path;
        fileName = scene.fileName;
        extension = scene.extension;
    }

    public void clear() {
        meshes.clear();
        id2mesh.clear();
    }
    
    public void setBoundingSphere(BSphere sphere) {
        boundingSphere = sphere;
    }
    
    public BSphere getBoundingSphere() {
        return boundingSphere;
    }    

}
