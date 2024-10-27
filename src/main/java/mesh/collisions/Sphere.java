/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mesh.collisions;

import javax.vecmath.Point4d;
import scene.surface.mesh.Face;

/**
 *
 * @author cmolikl
 */
public class Sphere {

    public Face associatedFace;
    public Point4d center;
    public double radius;

    public Sphere(Point4d center, double radius) {
        this.center = center;
        this.radius = radius;
    }
    
    public boolean intersect(Sphere s) {
        if(center.distance(s.center) < radius + s.radius) {
            return true;
        }
        return false;
    }
}
