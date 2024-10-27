/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.ui.swt;

import tiger.core.GlslProgramIntParameter;
import tiger.ui.TigerChangeEvent;
import tiger.ui.TigerChangeListener;

import javax.swing.*;

/**
 *
 * @author cmolikl
 */
public class BooleanMenuItem extends JCheckBoxMenuItem {

    private class ParamChangeListener implements TigerChangeListener {
        public void stateChanged(TigerChangeEvent e) {
            GlslProgramIntParameter p = (GlslProgramIntParameter) e.getSource();
            if(p.getValue() == 0) {
                setSelected(false);
            }
            else {
                setSelected(true);
            }
        }
    }

    public BooleanMenuItem(String label, GlslProgramIntParameter param) {
        super(label);
        addActionListener(new BooleanActionListener(param));
        if(param.getValue() == 0) {
            setSelected(false);
        }
        else {
            setSelected(true);
        }
        param.addChangeListener(new ParamChangeListener());
    }

}
