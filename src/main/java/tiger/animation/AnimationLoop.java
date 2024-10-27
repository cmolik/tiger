
package tiger.animation;

public abstract class AnimationLoop extends Thread {

    volatile protected boolean runFlag = false;
    protected long delta;
    private long prevFrameTime = 0;
   
    volatile boolean forceDisplay = false;
    volatile boolean runAsFastAsPosible = false;
    
    
    public AnimationLoop() {
        this(60);
    }

    public AnimationLoop(int fps) {
        delta = 1000 / fps;
    }

    @Override
    public void run() {
        runFlag = true;

        while(runFlag) {
            if(update() || forceDisplay) {
                display();
                forceDisplay = false;
            }
            if(!runAsFastAsPosible) {
                syncFrameRate(delta);
            }
        }
    }

    protected void syncFrameRate(long waitTime) {
        long nextFrameTime = prevFrameTime + waitTime;
        long currTime = System.currentTimeMillis();
        while (currTime < nextFrameTime) {
            Thread.yield();
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            currTime = System.currentTimeMillis();
        }
        prevFrameTime = currTime;
    }

    public void stopLoop() {
        runFlag = false;
    }
    
    /*public boolean isRunning() {
        return runFlag;
    }*/
    
    public void setRunAsFastAsPossible(boolean runAsFastAsPosible) {
        this.runAsFastAsPosible = runAsFastAsPosible;
    }
    
    public boolean getRunAsFastAsPosible() {
        return runAsFastAsPosible;
    }
    
    public void forceDisplay() {
        forceDisplay = true;
    }
    
    protected boolean update() {
        return true;
    }

    protected abstract void display();

}