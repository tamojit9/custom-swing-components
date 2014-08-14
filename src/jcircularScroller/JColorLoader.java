/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcircularScroller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;
import java.io.Serializable;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author tamojit
 */
public class JColorLoader extends JComponent implements Serializable {

    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";

    private String sampleProperty;

    private final PropertyChangeSupport propertySupport;

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

    //=======================================status variables=====================================================
    Rectangle[] rects;
    Color[] colors;
    Random r = new Random(System.currentTimeMillis());
    private final int n;
    Timer t;
    //=======================================status variables=====================================================

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    public JColorLoader(int n) {
        propertySupport = new PropertyChangeSupport(this);
        this.n = n;
        colors = new Color[n];
    }

    //=======================================painting helper functions=====================================================
    private void iniRects() {
        boolean alt = false;
        rects = new Rectangle[n];
        int cx = getWidth() >> 1, cy = getHeight() >> 1, w = getWidth(), h = getHeight(), d = getWidth() / n;
        for (int i = 0; i < rects.length; i++) {
            rects[i] = getRectangle(cx, cy, w, h);
            w -= 2 * d;
            colors[i] = new Color(alt ? r.nextInt() : Integer.MIN_VALUE - r.nextInt());
            alt = !alt;
        }
    }

    private void setHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private Rectangle getRectangle(int cx, int cy, int w, int h) {
        return new Rectangle(cx - (w / 2), cy - (h / 2), w, h);
    }

    //=======================================painting helper functions=====================================================
    
    
    //=======================================actual painting functions=====================================================
    @Override
    public void paint(Graphics g) {
        if (rects == null) {
            iniRects();
        }
        Graphics2D g2d = (Graphics2D) g;
        setHints(g2d);
        int i = 0;
        for (Shape shape : rects) {
            g2d.setColor(colors[i++]);
            g2d.fill(shape);
        }
    }
    //=======================================actual painting functions=====================================================
    
    //=======================================updating functions=====================================================

    private void updateRect(int dx) {
        int c = 0;
        if (rects == null) {
            return;
        }
        int cx = getWidth() >> 1, cy = getHeight() >> 1;
        for (int i = 0; i < rects.length; i++) {
            if (rects[i].width + dx <= getWidth()) {
                rects[i] = getRectangle(cx, cy, rects[i].width + dx + i, rects[i].height);
            } else {
                c++;
            }
        }
        if (c == rects.length) {
            iniRects();
        }
        repaint();
    }

    public void animate() {
        t = new Timer(10, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateRect(3);
            }
        });
        t.start();
    }

    public void stop() {
        t.stop();
    }
    
    //=======================================updating functions=====================================================

    public static void main(String[] argv) throws InterruptedException {
        JFrame jf = new JFrame();
        final JColorLoader jcs = new JColorLoader(15);
        jf.setUndecorated(true);
        jf.setLocation(0, 00);
        jf.setSize(1367, 10);
        jcs.setVisible(true);
        jf.setContentPane(jcs);
        jcs.animate();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}
