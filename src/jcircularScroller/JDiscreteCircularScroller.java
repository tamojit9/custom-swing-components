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
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.beans.*;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author tamojit
 */
public class JDiscreteCircularScroller extends JComponent implements Serializable {
    
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    
    private String sampleProperty;
    
    private final PropertyChangeSupport propertySupport;
    
    public JDiscreteCircularScroller() {
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
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
       //=======================================status variables=====================================================
    int progress = 0, upperLimit = 100;
    boolean entered;
    Area baseCircle, LoadingArc;
    //=======================================status variables=====================================================
    
    
    //=======================================getters and setters=====================================================
    
    public void setProgress(int progress) {
        this.progress = progress;
        if(this.progress <= 0) this.progress = 0;
        if(this.progress*100/360 >= upperLimit) this.progress = upperLimit*360/100;
        repaint();
    }
    
    public int getProgress() {
        return progress*100/360;
    }
    
    public void incrementProgress(int progress) {
        this.progress += progress;
        if(this.progress <= 0) this.progress = 0;
        if(this.progress*100/360 >= upperLimit) this.progress = upperLimit*360/100;
        repaint();
    }
    
    public int getUpperLimit() {
        return upperLimit;
    }
   
    public void setUpperLimit(int upperLimit) {
        this.upperLimit = upperLimit;
    }

    //=======================================getters and setters=====================================================
    
    
    //=======================================painting helper functions=====================================================
    Area getArcShape(int x, int y, int majorAxis, int minorAxis, int startAngle, int angle, int width) {
        Area a;
        a = new Area(new Arc2D.Double(x, y, majorAxis, minorAxis, startAngle+1, angle-2, Arc2D.PIE));
        int w = majorAxis - width, h = minorAxis - width;
        a.subtract(new Area(new Arc2D.Double(x + majorAxis / 2 - w / 2, y + minorAxis / 2 - h / 2, w, h, startAngle-10, angle+20, Arc2D.PIE)));
        return a;
    }

    void clearBackGround(Graphics2D g2d) {
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public double cos(double degree) {
        return Math.cos(Math.toRadians(degree));
    }

    public double sin(double degree) {
        return Math.sin(Math.toRadians(degree));
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

    public Area getCircleShape(Point p, int r) {
        return new Area(new Ellipse2D.Double(p.x - r, p.y - r, r << 1, r << 1));
    }

    public void mark(Graphics2D g2d, int x, int y) {
        Color c = g2d.getColor();
        g2d.setColor(getBackground().equals(Color.black) ? Color.white : Color.black);
        g2d.fill(getCircleShape(new Point(x, y), 3));
        g2d.setColor(c);
    }
    
    public void drawCenteredText(String s, Graphics2D g2d, int cx, int cy) {
        g2d.setFont(getFont());
        int w = g2d.getFontMetrics().charWidth(s.charAt(0))*s.length(), h = g2d.getFontMetrics().getDescent();
        g2d.drawString(s, cx-w/2, cy+h);
    }
    //=======================================painting helper functions=====================================================
    
    //=======================================actual painting functions=====================================================
    void drawBackGround(Graphics2D g2d) {
        //g2d.setColor(Color.YELLOW);
        //g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    void drawBackGroundRing(Graphics2D g2d, int radius, int cx, int cy) {
        baseCircle = getCircleShape(new Point(cx, cy), radius);
        g2d.setColor(getBackground());
        g2d.fill(baseCircle);
    }
    
    void drawLoadingArc(Graphics2D g2d, int radius, int cx, int cy) {
        radius += radius>>1;
        LoadingArc = new Area();
        for(int a = 0; a < progress; a+=23) {
            LoadingArc.add(getArcShape(cx - radius, cy - radius, radius << 1, radius << 1, a, 18, radius>>1));
        }
        g2d.setColor(getForeground());
        g2d.fill(LoadingArc);
    }
    
    void drawProgress(Graphics2D g2d, int cx, int cy) {
        drawCenteredText(progress*100/360+"", g2d, cx, cy);
    }

    void drawActualComponent(Graphics2D g2d) {
        int radius = Math.min(getWidth() >> 1, getHeight() >> 1);
        radius = radius - (radius>>1);
        Point p = getLocation();
        int cx = getWidth() >> 1, cy = (getHeight() >> 1);
        drawBackGroundRing(g2d, radius, cx, cy);
        drawLoadingArc(g2d, radius, cx, cy);
        drawProgress(g2d, cx, cy);
    }

    //=======================================actual painting functions=====================================================
    
    
    
    //=======================================updating functions=====================================================
    @Override
    public boolean contains(Point point) {
        if(baseCircle == null) return false;
        return baseCircle.contains(point) || LoadingArc.contains(point);
    }
    

    @Override
    public boolean contains(int i, int i1) {
        return contains(new Point(i, i1)); //To change body of generated methods, choose Tools | Templates.
    }
    
    void updateTriggeringMechanism(Point now) {
        incrementProgress((int) Angle.getAngle(new Point(getWidth(), getHeight()>>1), now, new Point(getWidth()>>1, getHeight()>>1)) - (progress%360));
    }
    
    final void addTrigger() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                updateTriggeringMechanism(me.getPoint());
            }
        });
        addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseEntered(MouseEvent me) {
                entered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                entered = false;
                repaint();
            }
        });
    }
    
    //=======================================updating functions=====================================================
    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        Graphics2D  g2d = (Graphics2D) g;
        setHints(g2d);
        drawBackGround(g2d);
        drawActualComponent(g2d);
    }
    
    public static void main(String[] argv) throws InterruptedException {
        JFrame jf = new JFrame();
        jf.setLayout(new GridLayout(1, 1));
        final JDiscreteCircularScroller jcs = new JDiscreteCircularScroller();
        jcs.setSize(300, 300);
        jcs.setLocation(200, 100);
        jcs.setVisible(true);
        //jcs.setUpperLimit(200);
        jf.add(jcs);
        jf.setSize(600, 600);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        jcs.setFont(new java.awt.Font("Segoe UI Semibold", 0, 30));
        int angle = 1;
        jcs.setProgress(270);
    }
}
