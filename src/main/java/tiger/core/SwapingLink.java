/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

/**
 *
 * @author cmolikl
 */
public class SwapingLink<T> implements Link<T> {
    
    T item1;
    T item2;
    int selected = 0;
    
    public SwapingLink(T item1, T item2) {
        this.item1 = item1;
        this.item2 = item2;
    }
    
    public T get() {
        return selected == 0 ? item1 : item2;
    }
    
    public void swap() {
        selected = (selected + 1) % 2;
    }
    
    public void restart() {
        selected = 0;
    }
}
