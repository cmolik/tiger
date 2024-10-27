/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.ui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import tiger.core.GlslProgramIntParameter;

/**
 *
 * @author cmolikl
 */
public class IntSliderListener implements ChangeListener {
     GlslProgramIntParameter param;

     public IntSliderListener(GlslProgramIntParameter param) {
         this.param = param;
     }

     public void stateChanged(ChangeEvent e) {
        IntSlider slider = (IntSlider) e.getSource();
        param.setValue(slider.getValue());
    }
}
