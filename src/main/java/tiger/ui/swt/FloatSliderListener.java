/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.ui.swt;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Scale;
import tiger.core.GlslProgramFloatParameter;

/**
 *
 * @author cmolikl
 */
public class FloatSliderListener implements SelectionListener {
    GlslProgramFloatParameter param;
    float min;
    float max;

    public FloatSliderListener(GlslProgramFloatParameter param, float min, float max) {
        this.param = param;
        this.min = min;
        this.max = max;
    }

    @Override
    public void widgetSelected(SelectionEvent selectionEvent) {
        Scale scale = (Scale) selectionEvent.getSource();
        float value = scale.getSelection() / 100f;
        value = (1-value)*min + value*max;
        if(param.getValue() != value) {
            param.setValue(value);
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent selectionEvent) {
    }
}
