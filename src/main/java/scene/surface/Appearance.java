/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scene.surface;

import gleem.linalg.Vec3f;

/**
 *
 * @author cmolikl
 */
public class Appearance {
    
    public static final int DIFFUSE_MODEL = 1;
    public static final int SPECULAR_MODEL = 2;
    
    private String name;
    private Vec3f ambient = new Vec3f(0.2f, 0.2f, 0.2f);
    private Vec3f diffuse = new Vec3f(0.8f ,0.8f ,0.8f);
    private Vec3f specular = new Vec3f(1.0f ,1.0f ,1.0f);
    private float alpha = 1f;
    private float shines = 0f;
    private int model = SPECULAR_MODEL;
    
    public Appearance(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public Vec3f getAmbient() {
        return ambient;
    }

    public void setAmbient(float r, float g, float b) {
        this.ambient.set(r, g, b);
    }

    public Vec3f getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(float r, float g, float b) {
        this.diffuse.set(r, g, b);
    }

    public Vec3f getSpecular() {
        return specular;
    }

    public void setSpecular(float r, float g, float b) {
        this.specular.set(r, g, b);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getShines() {
        return shines;
    }

    public void setShines(float shines) {
        this.shines = shines;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }
    
}
