/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.animation;

import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author cmolikl
 */
public class MeasurementAnimator extends Animator {
    
    private int measurements;
    private int frame;
    private ArrayList<ChangeListener> startListeners = new ArrayList<>();
    private ArrayList<ChangeListener> updateListeners = new ArrayList<>();
    private ArrayList<ChangeListener> endListeners = new ArrayList<>();
    
    public MeasurementAnimator(int measurements) {
        super(REAL_TIME);
        this.runAsFastAsPosible = true;
        this.measurements = measurements;
        this.frame = 0;
    }
    
    @Override
    protected boolean update() {
        if(frame < measurements) {
            for(ChangeListener listener : updateListeners) {
                listener.stateChanged(new ChangeEvent(this));
            }
            frame++;
            return true;
        }
        else {
            runFlag = false;
            return false;
        }
    }
    
    @Override
    public void run() {
        for(ChangeListener listener : startListeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
        
        super.run();
        
        for(ChangeListener listener : endListeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }
    
    public void addStartListener(ChangeListener listener) {
        startListeners.add(listener);
    }
    
    public void addUpdateListener(ChangeListener listener) {
        updateListeners.add(listener);
    }
    
    public void addEndListener(ChangeListener listener) {
        endListeners.add(listener);
    }
    
}
