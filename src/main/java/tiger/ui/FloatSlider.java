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

/**
 *
 * @author cmolikl
 */
public class FloatSlider extends JSlider {

    private class ParamChangeListener implements TigerChangeListener {
        public void stateChanged(TigerChangeEvent e) {
            GlslProgramFloatParameter p = (GlslProgramFloatParameter) e.getSource();
            int value = (int)(100f*(p.getValue()-min)/(max - min));
            if(value != getValue()) {
                setValue(value);
            }
        }
    }

    private int defaultMajorTickSpacing = 25;
    public float min;
    public float max;
    public FloatSlider(GlslProgramFloatParameter param, boolean normalize, int orientation, float min, float max) {
        super(orientation, 0, 100, (int)(100*param.getValue()/(max - min)));
        setMajorTickSpacing(defaultMajorTickSpacing);
        setMinorTickSpacing(5);
        setPaintTicks(true);
        setPaintLabels(true);

        Dictionary dict = new Hashtable();
        for(int i = 0; i <= 100; i += defaultMajorTickSpacing) {
            float f = ((float) i)/100f;
            dict.put(i, new JLabel(Float.toString((1-f)*min+f*max)));
        }
        setLabelTable(dict);

        this.min = min;
        this.max = max;
        addChangeListener(new FloatSliderListener(param, normalize));
        param.addChangeListener(new ParamChangeListener());
    }
}
