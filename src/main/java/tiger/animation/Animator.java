package tiger.animation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.awt.GLCanvas;

/**
 * Game that creates a window and handles input.
 * @author Eric
 */
public class Animator extends AnimationLoop {
    
    public static final int REAL_TIME = 0;
    public static final int TIME_LINE = 1;
    
    protected int mode = REAL_TIME;

    protected class TimeStamp implements Comparable<TimeStamp> {
        public static final int START = 0;
        public static final int END = 1;

        private int type;
        private long time;
        protected Animation animation;

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

    protected class Animation {
        private Interpolable param;
        private long startTime;
        private long endTime;
        private Interpolable startValue;
        private Interpolable endValue;
        private float duration;

        public Animation(Interpolable param, TimeStamp startTime, TimeStamp endTime, Interpolable startValue, Interpolable endValue) {
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
    
    protected HashSet<Animation> animations; 
    protected LinkedList<TimeStamp> timeStamps;
    
    protected LinkedList<GLAutoDrawable> canvases;
            
    protected boolean sorted = true;
    protected boolean updateOnlyOnChange = false;
    protected volatile boolean pause = false;
    protected long pauseStartTime;
    protected long pauseTime = 0L;
    
    public Animator(int type) {
        this(60, type);
    }
    
    public Animator(int fps, int type) {
        super(fps);
        animations = new HashSet<>();
        timeStamps = new LinkedList<>();
        canvases = new LinkedList<>();
    }
    
    public void addCanvas(GLAutoDrawable canvas) {
        canvases.add(canvas);
    }
    
    public void addAnimation(Interpolable param, long startTime, long endTime, Interpolable startValue, Interpolable endValue) {
        TimeStamp startStamp = new TimeStamp(startTime, TimeStamp.START);
        TimeStamp endStamp = new TimeStamp(endTime, TimeStamp.END);
        Animation a = new Animation(param, startStamp, endStamp, startValue, endValue);
        startStamp.animation = a;
        endStamp.animation = a;
        timeStamps.add(startStamp);
        timeStamps.add(endStamp);
        sorted = false;
    }
    
    protected long getNowTime() {
        return System.currentTimeMillis() - pauseTime;
    }
    
    protected void processTimeStampsRealTime(long now) {
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
    }
    
    protected void processTimeStampsTimeLine(long now) {
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
    
    @Override
    protected boolean update() {
        boolean update = false;
        if(!timeStamps.isEmpty()) {
            if(!sorted) {
                Collections.sort(timeStamps);
                sorted = true;
            }

            long now = getNowTime();
            
            switch(mode) {
                case REAL_TIME:
                    processTimeStampsRealTime(now);
                    break;
                case TIME_LINE:
                    processTimeStampsTimeLine(now);
                    break;
            }
            

            if(!animations.isEmpty()) {
                update = true;
                for(Animation a : animations) {
                    a.update(now);
                }
            }
        }
        
        if(!updateOnlyOnChange) {
            update = true;
        }
        
        return update;
    }
    
    public void display() {
        for(GLAutoDrawable c : canvases) {
            c.display();
        }
    }
    
    public void setUpdateOnlyOnChange(boolean value) {
        this.updateOnlyOnChange = value;
    }
    
    public boolean getUpdateOnlyOnChange() {
        return updateOnlyOnChange;
    }
    
    public void pause() {
        pauseStartTime = System.currentTimeMillis();
        pause = true;
    }
    
    public void play() {
        pauseTime += System.currentTimeMillis() - pauseStartTime;
        pause = false;
    }
}
