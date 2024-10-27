/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.animation;

import java.util.Collection;

/**
 *
 * @author cmolikl
 */
public class InterpolableArray<T extends Interpolable> implements Interpolable<T[]> {
    
    private T[] array;
    
    public InterpolableArray(T[] array) {
        this.array = array;
    }

    @Override
    public void interpolate(T[] value1, T[] value2, float t) {
        int length = Math.min(array.length, value1.length);
        length = Math.min(length, value2.length);
        for(int i = 0; i < length; i++) {
            array[i].interpolate(value1[i], value2[i], t);
        }
    }
    
}
