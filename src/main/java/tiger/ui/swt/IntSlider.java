/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import tiger.core.GlslProgramFloatParameter;
import tiger.core.GlslProgramIntParameter;
import tiger.ui.TigerChangeEvent;
import tiger.ui.TigerChangeListener;

/**
 *
 * @author cmolikl
 */
public class IntSlider extends Scale {

    public class ParamChangeListener implements TigerChangeListener {
        public void stateChanged(TigerChangeEvent e) {
            GlslProgramIntParameter p = (GlslProgramIntParameter) e.getSource();
            int value = p.getValue();
            if(value != getSelection()) {
                setSelection(value);
            }
        }
    }

    public IntSlider(GlslProgramIntParameter param, Composite parent, int style, int min, int max) {
        super(parent, style);
        setMinimum(min);
        setMaximum(max);
        setPageIncrement(1);
        setSelection(param.getValue());

        addSelectionListener(new IntSliderListener(param));
        param.addChangeListener(new ParamChangeListener());
    }
}
