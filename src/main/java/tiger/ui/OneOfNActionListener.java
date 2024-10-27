/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import tiger.core.GlslProgramIntParameter;

/**
 *
 * @author Jakub
 */
public class OneOfNActionListener implements ActionListener {

    GlslProgramIntParameter param;
    ButtonGroup group;

    public OneOfNActionListener(GlslProgramIntParameter param, ButtonGroup group) {
        this.param = param;
        this.group = group;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Enumeration<AbstractButton> enumeration = group.getElements();
        for (int i = 0; i < group.getButtonCount(); i++) {
            OneOfNMenuItem button = (OneOfNMenuItem) enumeration.nextElement();
            button.getParam().setValue(0);
        }
        param.setValue(1);
    }

}
