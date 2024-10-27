/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

/**
 *
 * @author cmolikl
 */
public class GeneralLink<T> implements Link<T> {
    
    T item;
    
    public GeneralLink(T item) {
        this.item = item;
    }
    
    public T get() {
        return item;
    }

    public void set(T item) {
        this.item = item;
    }
}
