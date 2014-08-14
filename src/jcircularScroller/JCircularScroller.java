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
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.beans.*;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author tamojit
 */
public class JCircularScroller extends JComponent implements Serializable {

    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";

    private String sampleProperty;

    private final PropertyChangeSupport propertySupport;

    public JCircularScroller() {
        propertySupport = new PropertyChangeSupport(this);
        setBackground(Color.LIGHT_GRAY);
        setForeground(Color.DARK_GRAY);
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

    //=======================================status variables=====================================================
    int progress, upperLimit = 100;
    double angle = 0, alpha = 0;
    boolean entered;
    Area baseRing, LoadingArc, indicator;
    int r;
    int radius;
    Color textColor = Color.BLACK;
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
   
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
    //=======================================getters and setters=====================================================


    
    
    //=======================================painting helper functions=====================================================
    Area getArcShape(int x, int y, int majorAxis, int minorAxis, int startAngle, int angle, int width) {
        Area a;
        a = new Area(new Arc2D.Double(x, y, majorAxis, minorAxis, startAngle, angle, Arc2D.PIE));
        int w = majorAxis - width, h = minorAxis - width;
        a.subtract(new Area(new Arc2D.Double(x + majorAxis / 2 - w / 2, y + minorAxis / 2 - h / 2, w, h, startAngle-1, angle+1, Arc2D.PIE)));
        return a;
    }

    Area getRingShape(int cx, int cy, int r, int d) {
        Area a;
        Shape s = new Ellipse2D.Double(cx - (r), cy - (r), (r) << 1, (r) << 1);
        a = new Area(s);
        a.subtract(new Area(new Ellipse2D.Double(cx - d, cy - d, d << 1, d << 1)));
        return a;
    }

    Area getTriangleShape(int angle, int[] x2Points, int[] y2Points) {
        GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x2Points.length);
        polyline.moveTo(x2Points[0], y2Points[0]);

        for (int index = 1; index < x2Points.length; index++) {
            polyline.lineTo(x2Points[index], y2Points[index]);
        };
        Area a = new Area(polyline);
        a.transform(AffineTransform.getRotateInstance(Math.toRadians(angle), x2Points[0], y2Points[0]));
        return a;
    }

    Area getEquiLateralTriangleShape(int angle, Point p, int a) {
        int x2Points[] = {p.x, (int) (p.x - a * Math.sin(Math.toRadians(30))),
            (int) (p.x + a * Math.sin(Math.toRadians(30)))};
        int y2Points[] = {p.y, (int) (p.y + a * Math.cos(Math.toRadians(30))),
            (int) (p.y + a * Math.cos(Math.toRadians(30)))};
        return new Area(getTriangleShape(angle, x2Points, y2Points));
    }

    Area getGoogleMapIndicatorShape(int cx, int y, int r, double angle) {
        int cy = (int) (y - (r >> 1) - (r * 2 * Math.sqrt(2)) * (Math.sqrt(3) / 2));
        Area a = new Area(getRingShape(cx, cy, 3 * r >> 1, r >> 1));
        int or = r;
        r = (int) (r * 2 * Math.sqrt(2));
        r /= Math.sqrt(2);
        int  x1 = cx;
        a.add(getEquiLateralTriangleShape(180, new Point(x1, y), r));
        a.transform(AffineTransform.getRotateInstance(Math.toRadians(angle), x1, y));
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
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    void drawBackGroundRing(Graphics2D g2d, int radius, int cx, int cy) {
        baseRing = getRingShape(cx, cy, r, Math.max(r - 15, 5));
        g2d.setColor(getBackground());
        g2d.fill(baseRing);
    }

    void drawLoadingArc(Graphics2D g2d, int radius, int cx, int cy) {
        LoadingArc = getArcShape(cx - r, cy - r, r << 1, r << 1, 0, progress, 30);
        g2d.setColor(getForeground());
        g2d.fill(LoadingArc);
    }
    
    void drawIndicator(Graphics2D g2d, int cx, int cy, int radius) {
        g2d.setComposite(AlphaComposite.SrcOver.derive((float)alpha));
        Color c = g2d.getColor();
        if(entered) {
            if(!c.equals(Color.white))
                g2d.setColor(c.brighter());
            else 
                g2d.setColor(c.darker());
        }
        indicator = getGoogleMapIndicatorShape(cx + (int) (r * sin(90 + progress)), cy + (int) (r * cos(90 + progress)), (r>>2)-1, 90 - progress);
        g2d.fill(indicator);
        g2d.setColor(c);
    }
    
    void drawProgress(Graphics2D g2d, int cx, int cy) {
        g2d.setColor(textColor);
        drawCenteredText(progress*100/360+"", g2d, cx, cy);
    }

    void drawActualComponent(Graphics2D g2d) {
        radius = Math.min(getWidth() >> 1, getHeight() >> 1);
        radius = radius - (radius>>1);
        if(!entered) {
            radius >>= 1;
            if(r == 0)
                r = radius;
        }
        Point p = getLocation();
        int cx = getWidth() >> 1, cy = (getHeight() >> 1);
        drawBackGroundRing(g2d, radius, cx, cy);
        drawLoadingArc(g2d, radius, cx, cy);
        drawIndicator(g2d, cx, cy, radius);
        drawProgress(g2d, cx, cy);
    }
    
    Timer t; boolean busy;
    
    void animateProgress() {
       if(busy) return;
       t = new Timer(10, new ActionListener() {

           @Override
           public void actionPerformed(ActionEvent e) {
               busy = true;
                if(r < radius)  {
                    r++;
                    alpha = r*1.0/radius;
                }
                else if(r > radius)  {
                    r--;
                    alpha = Math.min(1, (r>>1)*1.0/radius - 0.5);
                }
                else  {
                    busy = false;
                    t.stop();
                }
                repaint();
           }
       });
       t.start();
    }
    //=======================================actual painting functions=====================================================
    
    
    
    
    
    //=======================================updating functions=====================================================
    @Override
    public boolean contains(Point point) {
        if(baseRing == null) return false;
        int radius = Math.max(getWidth() >> 1, getHeight() >> 1);
        if(!entered)
            radius >>= 2;
        return getCircleShape(new Point(getWidth()>>1, getHeight()>>1), radius).contains(point) || (indicator != null && indicator.contains(point));
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
                radius <<= 1;
                animateProgress();
            }
            
            @Override
            public void mouseExited(MouseEvent me) {
                entered = false;
                radius >>= 1;
                animateProgress();
            }
        });
    }
    
    //=======================================updating functions=====================================================
    
    
    
    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        Graphics2D g2d = (Graphics2D) g;
        setHints(g2d);
        drawBackGround(g2d);
        drawActualComponent(g2d);
    }

    public static void main(String[] argv) {
        JFrame jf = new JFrame();
        jf.setLayout(new GridLayout(1, 1));
        final JCircularScroller jcs = new JCircularScroller();
        jcs.setSize(300, 300);
        jcs.setLocation(200, 100);
        jcs.setVisible(true);
        jcs.setTextColor(Color.yellow);
        jcs.setBackground(Color.GREEN.darker());
        jcs.setForeground(Color.GREEN.darker().darker());
        //jcs.setUpperLimit(200);
        jf.add(jcs);
        jf.setSize(600, 600);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        jcs.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20));
        int angle = 0;
    }
    
}
