/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.ui;

import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author cmolikl
 */
public class HiddenPanel extends JPanel {
    
    private static int hide = 0;
    private static int show = 1;
    
    int state = 0;
    
    @Override
    public void paint(Graphics g) {
        if(state == hide) {
            if(super.getSize().width > 5) {
                System.out.println("width = " + super.getSize().width);
                super.setSize(super.getSize().width - 5, super.getHeight());
            }
            else {
                state = show;
                System.out.println("show");
            }
        }
        else {
            if(super.getSize().width < 150) {
                System.out.println("width = " + super.getSize().width);
                super.setSize(super.getSize().width + 5, super.getHeight());
            }
            else {
                state = hide;
                System.out.println("hide");
            }
        }
        super.paint(g);
    }
}
