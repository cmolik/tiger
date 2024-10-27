/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.ui;

import java.util.Dictionary;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import tiger.core.GlslProgramFloatParameter;
import tiger.core.GlslProgramIntParameter;

/**
 *
 * @author cmolikl
 */
public class IntSlider extends JSlider {

    private class ParamChangeListener implements TigerChangeListener {
        public void stateChanged(TigerChangeEvent e) {
            GlslProgramIntParameter p = (GlslProgramIntParameter) e.getSource();
            setValue(p.getValue());
        }
    }

    public IntSlider(GlslProgramIntParameter param, int orientation, int min, int max) {
        super(orientation, min, max, param.getValue());
        setMajorTickSpacing(max);
        setMinorTickSpacing(1);
        setPaintTicks(true);
        setPaintLabels(true);

        addChangeListener(new IntSliderListener(param));
        param.addChangeListener(new ParamChangeListener());
    }
}
