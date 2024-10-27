/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.ui;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import tiger.core.GlslProgramIntParameter;

/**
 *
 * @author Jakub
 */
public class OneOfNGroup {

    private final ButtonGroup group;

    public OneOfNGroup(JMenu jm, int num, String[] names) {
        group = new ButtonGroup();
        int value = 1;
        for (int i = 0; i < num; i++) {
            final GlslProgramIntParameter ip = new GlslProgramIntParameter("parameter " + i, value);
            OneOfNMenuItem rbMenuItem = new OneOfNMenuItem(names[i], group, ip);
            group.add(rbMenuItem);
            jm.add(rbMenuItem);
            value = 0;
        }
    }

    public ButtonGroup getGroup() {
        return group;
    }

}
