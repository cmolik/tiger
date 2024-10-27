/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import tiger.core.GlslProgramFloatParameter;
import tiger.ui.TigerChangeEvent;
import tiger.ui.TigerChangeListener;

/**
 *
 * @author cmolikl
 */
public class FloatSlider extends Scale {

    public class ParamChangeListener implements TigerChangeListener {
        public void stateChanged(TigerChangeEvent e) {
            GlslProgramFloatParameter p = (GlslProgramFloatParameter) e.getSource();
            int value = (int)(100f*(p.getValue()-min)/(max - min));
            if(value != getSelection()) {
                setSelection(value);
            }
        }
    }

    public float min;
    public float max;

    public FloatSlider(GlslProgramFloatParameter param, Composite parent, int style, float min, float max) {
        super(parent, style);
        setMinimum(0);
        setMaximum(100);
        setPageIncrement(5);
        setSelection((int)(100f*(param.getValue()-min)/(max - min)));

        this.min = min;
        this.max = max;

        addSelectionListener(new FloatSliderListener(param, min, max));
        param.addChangeListener(new ParamChangeListener());
    }
}
