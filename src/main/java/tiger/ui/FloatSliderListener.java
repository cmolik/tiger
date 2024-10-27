/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.ui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import tiger.core.GlslProgramFloatParameter;

/**
 *
 * @author cmolikl
 */
public class FloatSliderListener implements ChangeListener {
     GlslProgramFloatParameter param;
     boolean normalize;

     public FloatSliderListener(GlslProgramFloatParameter param, boolean normalize) {
         this.param = param;
         this.normalize = normalize;
     }

     public void stateChanged(ChangeEvent e) {
        FloatSlider slider = (FloatSlider) e.getSource();
        float value = slider.getValue() / 100f;
        if(!normalize) {
            float min = slider.min;
            float max = slider.max;
            value = (1-value)*min + value*max;
        }
        if(param.getValue() != value) {
            param.setValue(value);
        }
    }
}
