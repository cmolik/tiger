/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.ui.swt;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Scale;
import tiger.core.GlslProgramFloatParameter;
import tiger.core.GlslProgramIntParameter;

/**
 *
 * @author cmolikl
 */
public class IntSliderListener implements SelectionListener {
    GlslProgramIntParameter param;

    public IntSliderListener(GlslProgramIntParameter param) {
        this.param = param;
    }

    @Override
    public void widgetSelected(SelectionEvent selectionEvent) {
        Scale scale = (Scale) selectionEvent.getSource();
        int value = scale.getSelection();
        if(param.getValue() != value) {
            param.setValue(value);
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent selectionEvent) {
    }
}
