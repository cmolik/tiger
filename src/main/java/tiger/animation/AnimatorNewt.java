package tiger.animation;

import com.jogamp.newt.opengl.GLWindow;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import com.jogamp.opengl.awt.GLCanvas;

/**
 * Game that creates a window and handles input.
 * @author Eric
 */
public class AnimatorNewt extends AnimationLoop {

    private class TimeStamp implements Comparable<TimeStamp> {
        public static final int START = 0;
        public static final int END = 1;

        private int type;
        private long time;
        private Animation animation;

        public TimeStamp(long time, int type) {
            this.time = time;
            this.type = type;
        }

        public long getTime() {
            return time;
        }

        public int getType() {
            return type;
        }

        public int compareTo(TimeStamp t) {
            return (int)(time - t.getTime());
        }

        public String toString() {
            return "" + time;
        }
    }

    private class Animation {
        private Interpolable param;
        private long startTime;
        private long endTime;
        private float startValue;
        private float endValue;
        private float duration;

        public Animation(Interpolable param, TimeStamp startTime, TimeStamp endTime, float startValue, float endValue) {
            this.param = param;
            this.startTime = startTime.getTime();
            this.endTime = endTime.getTime();
            duration = (float) (this.endTime - this.startTime);
            this.startValue = startValue;
            this.endValue = endValue;
        }

        public void update(long now) {
            long fromStart = now - startTime;
            float t = fromStart / duration;
            param.interpolate(startValue, endValue, t);
        }
    }
    
    private HashSet<Animation> animations; 
    private LinkedList<TimeStamp> timeStamps;
    
    private LinkedList<GLWindow> canvases;
            
    private boolean sorted = true;
    private boolean updateOnlyOnChange = false;
    
    public AnimatorNewt() {
        animations = new HashSet<>();
        timeStamps = new LinkedList<>();
        canvases = new LinkedList<>();
    }
    
    public void addCanvas(GLWindow canvas) {
        canvases.add(canvas);
    }
    
    public void addParameter(Interpolable param, long startTime, long endTime, float startValue, float endValue) {
        TimeStamp startStamp = new TimeStamp(startTime, TimeStamp.START);
        TimeStamp endStamp = new TimeStamp(endTime, TimeStamp.END);
        Animation a = new Animation(param, startStamp, endStamp, startValue, endValue);
        startStamp.animation = a;
        endStamp.animation = a;
        timeStamps.add(startStamp);
        timeStamps.add(endStamp);
        sorted = false;
    }
    
    @Override
    protected boolean update() {
        if(!updateOnlyOnChange) {
            return true;
        }
        if(timeStamps.isEmpty()) {
            return false;
        }
        if(!sorted) {
            Collections.sort(timeStamps);
            sorted = true;
        }
        
        long now = System.currentTimeMillis();
        Iterator<TimeStamp> it = timeStamps.iterator();
        while(it.hasNext()) {
            TimeStamp ts = it.next();
            long time = ts.getTime();
            if(time < now) {
                if(ts.getType() == TimeStamp.START) {
                    animations.add(ts.animation);
                }
                else {
                    animations.remove(ts.animation);
                }
                it.remove();
            }
            else {
                break;
            }
        }

        if(animations.isEmpty()) {
            return false;
        }

        for(Animation a : animations) {
            a.update(now);
        }
        return true;
    }
    
    public void display() {
        for(GLWindow c : canvases) {
            c.display();
        }
    }
    
    public void setUpdateOnlyOnChange(boolean value) {
        this.updateOnlyOnChange = value;
    }
    
    public boolean getUpdateOnlyOnChange() {
        return updateOnlyOnChange;
    }
}
