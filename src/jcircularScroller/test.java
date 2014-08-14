/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jcircularScroller;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;   
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.*;
import java.io.Serializable;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 *
 * @author tamojit
 */
public class test extends JComponent implements Serializable {
    
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    
    private String sampleProperty;
    
    private final PropertyChangeSupport propertySupport;
    
    public test() {
        propertySupport = new PropertyChangeSupport(this);
        addTriger();
    }
    
    public String getSampleProperty() {
        return sampleProperty;
    }
    
    public void setSampleProperty(String value) {
        String oldValue = sampleProperty;
        sampleProperty = value;
        propertySupport.firePropertyChange(PROP_SAMPLE_PROPERTY, oldValue, sampleProperty);
    }
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    LinearGradientPaint getVerticalGlowingShade(Rectangle r, Color bg) {
        //bg = bg.brighter().brighter().brighter().brighter().brighter();
        return new LinearGradientPaint(new Point(r.x, r.y), new Point(r.x, r.y + r.height+1),
                new float[]{0, 1.0f},
                new Color[]{bg.brighter().brighter().brighter().brighter().brighter(), bg});
    }
    
    int height = 100;
    
    @Override
    public void paint(Graphics g) {
        paintChildren(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver.derive(0.1f));
        g2d.setPaint(getVerticalGlowingShade(new Rectangle(0, getHeight()-height, getWidth(), height), getBackground()));
        g2d.fill(new Rectangle(0, getHeight()-height, getWidth(), height));
    }
    
    private void addTriger() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                height = 0;
                animate();
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                height = 0;
                repaint();
            }
        });
    }
    
    public static void main(String[] args) {
        JFrame jf = new JFrame();
        final test jcs = new test();
        jcs.setLayout(new GridLayout(1, 1));
        jcs.add(new JLabel("THIS IS A TEST"));
        jf.setLocation(0, 200);
        jf.setSize(1367, 500);
        jcs.setBackground(Color.black);
        jcs.setVisible(true);
        jf.setContentPane(jcs);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
    Timer t;
    private void animate() {
        t = new Timer(1, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(getHeight()/2 == 0) return;
                height += (getHeight()-height)/5;
                if((getHeight()-height)/5 == 0) {
                    height = getHeight();
                    t.stop();
                }
                repaint();
            }
        });
        t.start();
    }
}
