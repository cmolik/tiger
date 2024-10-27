/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.interaction;

import gleem.BSphereProvider;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import tiger.core.Camera;
import tiger.core.GlslProgramFloatArrayParameter;

/**
 *
 * @author cmolikl
 */
public class OrbitManipulator {
    
    public class OrbitManipulatorMouseListener extends MouseAdapter implements MouseMotionListener {
        
        float x;
        float y;
        
        GlslProgramFloatArrayParameter cameraPosition;

        @Override
        public void mouseDragged(MouseEvent e) {
            float currentX = e.getX();
            float currentY = e.getY();
            
            float deltaX = currentX - x;
            float deltaY = currentY - y;
            
            camera.position.getValue()[0] = cameraPosition.getValue()[0] - camera.center.getValue()[0];
            camera.position.getValue()[1] = cameraPosition.getValue()[1] - camera.center.getValue()[1];
            camera.position.getValue()[2] = cameraPosition.getValue()[2] - camera.center.getValue()[2];
            
            
            
            camera.position.getValue()[0] = cameraPosition.getValue()[0] + camera.center.getValue()[0];
            camera.position.getValue()[1] = cameraPosition.getValue()[1] + camera.center.getValue()[1];
            camera.position.getValue()[2] = cameraPosition.getValue()[2] + camera.center.getValue()[2];
        }

        @Override
        public void mouseMoved(MouseEvent e) {      
        }

        @Override
        public void mousePressed(MouseEvent e) {
            x = e.getX();
            y = e.getY();
            cameraPosition = camera.position;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        } 
    }
    
    protected Camera camera;
    protected BSphereProvider boundingSphereProvider;
    
    public OrbitManipulator(Camera camera, BSphereProvider boundingSphereProvider) {
        this.camera = camera;
        this.boundingSphereProvider = boundingSphereProvider;
    }
    
}
