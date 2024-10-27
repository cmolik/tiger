/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.animation;

/**
 *
 * @author cmolikl
 */
public interface Interpolable<T> {
    public void interpolate(T value1, T value2, float t);
}
