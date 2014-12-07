/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcircularScroller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author tamojit
 */
public class JColorLoaderMonoChrome extends JComponent implements Serializable {

    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";

    private String sampleProperty;

    private final PropertyChangeSupport propertySupport;
    Rectangle[] rects;
    Color[] colors;
    private int n;

    public void setColors(Color[] colors) throws Exception {
        if(colors.length != n) throw new Exception("no of colors specified is not equal to the no of colors provided");
        this.colors = colors;
    }

    public JColorLoaderMonoChrome(int n) {
        propertySupport = new PropertyChangeSupport(this);
        this.n = n;
        colors = new Color[n];
    }

    private void iniRects() {
        rects = new Rectangle[n];
        int cx = getWidth() >> 1, cy = getHeight() >> 1;
        for (int i = 0; i < rects.length; i++) {
            rects[i] = getRectangle(cx, cy, 0, getHeight());
        }
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

    public void setHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    Rectangle getRectangle(int cx, int cy, int w, int h) {
        return new Rectangle(cx - (w / 2), cy - (h / 2), w, h);
    }

    int i = 0;

    @Override
    public void paint(Graphics g) {
        if (rects == null) {
            iniRects();
        }
        Graphics2D g2d = (Graphics2D) g;
        setHints(g2d);
        if(i > 0) {
            g2d.setColor(colors[i-1]);
            g2d.fill(rects[i-1]);
        }
        g2d.setColor(colors[i]);
        g2d.fill(rects[i]);
    }

    void updateRect(int dx) {
        if (rects == null) {
            iniRects();
        }
        int cx = getWidth() >> 1, cy = getHeight() >> 1;
        if (rects[i].width + dx <= getWidth()) {
            rects[i] = getRectangle(cx, cy, rects[i].width + (getWidth()-rects[i].width)/dx, rects[i].height);
        } 
        if((getWidth()-rects[i].width)/dx <= 5) {
            rects[i] = getRectangle(cx, cy, getWidth(), rects[i].height);
            i++;
        }
        if (i == rects.length) {
            i = 0;
            iniRects();
        }
        repaint();
    }
    void animate(final int speed) {
        new Timer(10, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateRect(speed);
            }
        }).start();
    }

    public static void main(String[] argv) throws InterruptedException, Exception {
        JFrame jf = new JFrame();
        final JColorLoaderMonoChrome jcs = new JColorLoaderMonoChrome(4);
        jf.setSize(1366, 10);
        jf.setUndecorated(true);
        jcs.setVisible(true);
        jf.setContentPane(jcs);
        jcs.setColors(new Color[]{Color.BLACK, Color.RED, Color.BLUE, Color.GREEN});
        jcs.animate(12);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}
