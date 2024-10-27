/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.ui;

import javax.swing.JLabel;
import tiger.core.GlslProgramIntParameter;
import tiger.core.GlslProgramParameter;

/**
 *
 * @author cmolikl
 */
public class ParameterLabel extends JLabel {
    private class ParamChangeListener implements TigerChangeListener {
        public void stateChanged(TigerChangeEvent e) {
            GlslProgramIntParameter p = (GlslProgramIntParameter) e.getSource();
            setText(p.name + " = " + p.getValue());
        }
    }

    public ParameterLabel(GlslProgramParameter p) {
        super(p.name + " = " + p);
        p.addChangeListener(new ParamChangeListener());
    }
}
