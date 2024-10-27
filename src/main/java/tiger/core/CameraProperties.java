/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import gleem.CameraParameters;
import gleem.ExaminerViewer;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import java.util.Properties;

/**
 *
 * @author cmolikl
 */
public class CameraProperties {
    public static void saveProperties(ExaminerViewer viewer, Properties properties) {
        CameraParameters cameraParameters = null;
        synchronized(viewer) {
            cameraParameters = (CameraParameters) viewer.getCameraParameters().clone();
        }
        float radius = 100;
        if(viewer instanceof OrthogonalExaminerViewer) {
            radius = ((OrthogonalExaminerViewer) viewer).getRadius();
            properties.setProperty("camera.radius", "" + radius);
        }
        else {
            float vertFOV = cameraParameters.getVertFOV();
            properties.setProperty("camera.fov", "" + vertFOV);
        }

        Vec3f position = cameraParameters.getPosition();
        properties.setProperty("camera.position.0", "" + position.get(0));
        properties.setProperty("camera.position.1", "" + position.get(1));
        properties.setProperty("camera.position.2", "" + position.get(2));
        Vec3f axis = new Vec3f();
        float angle = cameraParameters.getOrientation().get(axis);
        properties.setProperty("camera.orientation.angle", "" + angle);
        properties.setProperty("camera.orientation.axis.0", "" + axis.get(0));
        properties.setProperty("camera.orientation.axis.1", "" + axis.get(1));
        properties.setProperty("camera.orientation.axis.2", "" + axis.get(2));
    }

    public static void loadProperties(ExaminerViewer viewer, Properties properties) {
        
        String p;
        
        Vec3f position = new Vec3f();
        boolean positionLoaded = true;
        for(int i = 0; i < 3; i++) {
            p = properties.getProperty("camera.position." + i);
            if(p != null) {
                position.set(i, Float.parseFloat(p));
            }
            else {
                positionLoaded = false;
                break;
            }
        }
        Vec3f axis = new Vec3f();
        boolean axisLoaded = true;
        for(int i = 0; i < 3; i++) {
            p = properties.getProperty("camera.orientation.axis." + i);
            if(p != null) {
                axis.set(i, Float.parseFloat(p));
            }
            else {
                axisLoaded = false;
                break;
            }
        }
        float angle = 0f;
        boolean angleLoaded = true;
        p = properties.getProperty("camera.orientation.angle");
        if(p != null) {
            angle = Float.parseFloat(p);
        }
        else {
            angleLoaded = false;
        }
        
        float fov = 45f;
        p = properties.getProperty("camera.fov");
        if(p != null) fov = Float.parseFloat(p);
        
        
        synchronized(viewer) {
            if(positionLoaded) viewer.setPosition(position);
            if(axisLoaded && angleLoaded) viewer.setOrientation(new Rotf(axis, angle));
            viewer.setVertFOV(fov);

            if(viewer instanceof OrthogonalExaminerViewer) {
                float radius = 100;
                p = properties.getProperty("camera.radius");
                if(p != null) radius = Float.parseFloat(p);
                ((OrthogonalExaminerViewer) viewer).setRadius(radius);
            }
        }
            
    }
    
    public Vec3f position = new Vec3f();
    public Vec3f axis = new Vec3f();
    public float angle = 0;
    public float fov = 45;
    public float radius = 100;
    
    public void saveProperties(ExaminerViewer viewer) {
        CameraParameters cameraParameters = null;
        synchronized(viewer) {
            cameraParameters = (CameraParameters) viewer.getCameraParameters().clone();
        }
        radius = 100;
        fov = 45;
        if(viewer instanceof OrthogonalExaminerViewer) {
            radius = ((OrthogonalExaminerViewer) viewer).getRadius();
        }
        else {
            fov = cameraParameters.getVertFOV();
        }

        position.set(cameraParameters.getPosition());
        angle = cameraParameters.getOrientation().get(axis);
    }
    
    public void loadProperties(ExaminerViewer viewer) {
        synchronized(viewer) {
            viewer.setPosition(position);
            viewer.setOrientation(new Rotf(axis, angle));
            viewer.setVertFOV(fov);

            if(viewer instanceof OrthogonalExaminerViewer) {
                ((OrthogonalExaminerViewer) viewer).setRadius(radius);
            }
        }
    }

    public void loadProperties(Properties properties) {

        String p;

        Vec3f position = new Vec3f();
        boolean positionLoaded = true;
        for(int i = 0; i < 3; i++) {
            p = properties.getProperty("camera.position." + i);
            if(p != null) {
                position.set(i, Float.parseFloat(p));
            }
            else {
                positionLoaded = false;
                break;
            }
        }

        Vec3f axis = new Vec3f();
        boolean axisLoaded = true;
        for(int i = 0; i < 3; i++) {
            p = properties.getProperty("camera.orientation.axis." + i);
            if(p != null) {
                axis.set(i, Float.parseFloat(p));
            }
            else {
                axisLoaded = false;
                break;
            }
        }

        float angle = 0f;
        boolean angleLoaded = true;
        p = properties.getProperty("camera.orientation.angle");
        if(p != null) {
            angle = Float.parseFloat(p);
        }
        else {
            angleLoaded = false;
        }

        float fov = 45f;
        p = properties.getProperty("camera.fov");
        if(p != null) fov = Float.parseFloat(p);

    }
}
