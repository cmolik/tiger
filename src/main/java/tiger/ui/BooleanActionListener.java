/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import tiger.core.GlslProgramIntParameter;

/**
 *
 * @author cmolikl
 */
public class BooleanActionListener implements ActionListener {

    GlslProgramIntParameter param;
    
    public BooleanActionListener(GlslProgramIntParameter param) {
        this.param = param;
    }

    public void actionPerformed(ActionEvent e) {
        AbstractButton aButton = (AbstractButton) e.getSource();
        if(param.getValue() == 0) {
            param.setValue(1);
            aButton.getModel().setSelected(true);
        }
        else {
            param.setValue(0);
            aButton.getModel().setSelected(false);
        }
    }

}