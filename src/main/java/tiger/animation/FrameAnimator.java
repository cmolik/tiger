/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.animation;

import java.util.Iterator;

/**
 *
 * @author cmolikl
 */
public class FrameAnimator extends Animator {
    int startFrame;
    int endFrame;
    int frame;
    
    boolean frameChanged = false;
    
    public FrameAnimator(int startFrame, int endFrame) {
        this(startFrame, endFrame, 25);
    }
    
    public FrameAnimator(int startFrame, int endFrame, int fps) {
        super(fps);
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.frame = startFrame;
        this.runAsFastAsPosible = false;
        this.updateOnlyOnChange = true;
    }
    
    public void addAnimation(Interpolable param, int startFrame, int endFrame, Interpolable startValue, Interpolable endValue) {
        long startTime = (frame - startFrame)*delta;
        long endTime = (frame - startFrame)*delta;
        
        super.addAnimation(param, startTime, endTime, startValue, endValue);
    }
    
    protected long getNowTime() {
        return frame*delta;
    }
    
    protected void processTimeStamps(long now) {
        animations.clear();
        Iterator<Animator.TimeStamp> it = timeStamps.iterator();
        while(it.hasNext()) {
            Animator.TimeStamp ts = it.next();
            long time = ts.getTime();
            if(time < now) {
                if(ts.getType() == Animator.TimeStamp.START) {
                    animations.add(ts.animation);
                }
                if(ts.getType() == Animator.TimeStamp.END) {
                    animations.remove(ts.animation);
                }
            }
            else {
                break;
            }
        }
    }
    
    protected boolean update() {
        if(sorted && !frameChanged) {
            return false;
        }
        else {
            return super.update();
        }
    }
    
}
