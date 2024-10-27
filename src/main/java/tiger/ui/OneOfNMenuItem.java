/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.ui;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import tiger.core.GlslProgramIntParameter;

/**
 *
 * @author Jakub
 */
public class OneOfNMenuItem extends JRadioButtonMenuItem {

    private GlslProgramIntParameter param;

    public OneOfNMenuItem(String label, ButtonGroup group, GlslProgramIntParameter param) {
        super(label);
        this.param = param;
        addActionListener(new OneOfNActionListener(param, group));
        if (param.getValue() == 0) {
            setSelected(false);
        } else {
            setSelected(true);
        }
    }

    public GlslProgramIntParameter getParam() {
        return param;
    }
}
