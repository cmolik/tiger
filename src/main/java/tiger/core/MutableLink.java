/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

/**
 *
 * @author cmolikl
 */
public class MutableLink<T> implements Link<T> {
    T value;

    public MutableLink(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
