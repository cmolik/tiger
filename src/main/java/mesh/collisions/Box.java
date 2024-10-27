/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mesh.collisions;

import javax.vecmath.Point4d;

/**
 *
 * @author cmolikl
 */
public class Box {
    public Point4d min;
    public Point4d max;

    public Box(Point4d min, Point4d max) {
        this.min = min;
        this.max = max;
    }
    
    public boolean intersect(Box b) {
        double maxLeft = Math.max(min.x, b.min.x);
        double minRight = Math.min(max.x, b.max.x);
        double maxBottom = Math.max(min.y, b.min.y);
        double minTop = Math.min(max.y, b.max.y);
        double maxFront = Math.max(min.z, b.min.z);
        double minBack = Math.min(max.z, b.max.z);
        if(maxLeft < minRight && maxBottom < minTop && maxFront < minBack) {
            return true;
        }
        return false;
    }
}
