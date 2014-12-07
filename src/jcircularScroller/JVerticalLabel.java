/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jcircularScroller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.*;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

/**
 *
 * @author tamojit
 */
public class JVerticalLabel extends JComponent implements Serializable {
    
    
        
    public enum Rotate {
        south, east, north, west
    }
    
    Rotate r = Rotate.west;
    double angle = getNumberOfRotations(r);
    boolean set;
    int cx, cy;

    public void setRotation(Rotate r) {
        this.r = r;
    }

    public Rotate getRotation() {
        return r;
    }
    
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    
    private String sampleProperty;
    
    private final PropertyChangeSupport propertySupport;
    
    public JVerticalLabel() {
        propertySupport = new PropertyChangeSupport(this);
        addTrigger();
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
    
    
    public double getNumberOfRotations(Rotate r) {
        switch (r) {
            case south:
               return 0;
            case east :
                return 90;
            case west :
                return (90*3);
            case north :
                return (90*2);
            default:
                throw new AssertionError();
        }
    }
    
    void drawCenteredTextWithRotations(String s, Graphics2D g2d, int cx, int cy, double angle) {
        g2d.setFont(getFont());
        g2d.transform(AffineTransform.getRotateInstance(angle, cx, cy));
        int h = g2d.getFontMetrics().getDescent();
        Rectangle2D stringBounds = g2d.getFontMetrics().getStringBounds(s, g2d);
        g2d.drawString(s, (int) (cx - stringBounds.getWidth() / 2), cy + h);
        g2d.transform(AffineTransform.getRotateInstance(-1*angle, cx, cy));
    }

    
    void setHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    void paintActualComponent(Graphics2D g2d) {
        if(!set) {
            set = true;
            cx = getWidth()>>1; cy = getHeight()>>1;
        }
        drawCenteredTextWithRotations("testing", g2d, cx, cy, Math.toRadians(angle));
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        Graphics2D g2d = (Graphics2D) g;
        //g2d.setPaint(getHorizontalGlowingShade(new Rectangle(0, 0, getWidth(), getHeight()), getBackground()));
        setHints(g2d);
        paintActualComponent(g2d);
    }
    Timer t;
    public void animateFlip() {
        t = new Timer(10, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(angle < getNumberOfRotations(r)) {
                    angle++;
                    setSize(getWidth()  - 1, getHeight());
                }
                if(angle >  getNumberOfRotations(r))  {
                    setSize(getWidth() + 1, getHeight());
                    angle--;
                }
                if(angle ==  getNumberOfRotations(r)) t.stop();
                repaint();
            }
        });
        t.start();
    }
    
    LinearGradientPaint getHorizontalGlowingShade(Rectangle r, Color bg) {
        return new LinearGradientPaint(new Point(r.x, r.y), new Point(r.x + r.width, r.y), 
                new float[]{0, 0.25f, 0.5f, 0.75f, 1.0f}, 
                new Color[]{bg, bg.brighter().brighter().brighter(), Color.WHITE, bg.brighter().brighter().brighter(), bg});
    }
    
    static void createJFrame(JComponent[] jcs) {
        jcs[0].setBorder(new LineBorder(Color.yellow));
        jcs[0].setFont(new java.awt.Font("Segoe UI Semibold", 0, 200));
        JFrame jf = new JFrame();
        jf.setLayout(new GridLayout(jcs.length, 1));
        jf.setSize(600, 600);
        for (JComponent jComponent : jcs) {
            jf.add(jComponent);
        }
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
    
    final void addTrigger() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent me) {
                r = Rotate.south;
                animateFlip();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                r = Rotate.west;
                animateFlip();
            }
        });
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        createJFrame(new JComponent[]{new JVerticalLabel()});
    }
}
