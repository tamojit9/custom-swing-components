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
import java.awt.List;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.beans.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author tamojit
 */
public class JExpandingList extends JComponent implements Serializable {

    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";

    private String sampleProperty;

    private final PropertyChangeSupport propertySupport;

    public JExpandingList() {
        propertySupport = new PropertyChangeSupport(this);
        setBackground(Color.BLACK);
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

    //=======================================================================variables==========================================================
    ArrayList<Object> items = new ArrayList<>();
    double angle;
    Color selectionColor = Color.GREEN;
    ArrayList<Shape> shapes;
    double limit;
    Timer t;
    boolean reverse;
    int selectedIndex = -1;
    //=======================================================================variables==========================================================

    //=======================================================================setters=============================================================
    public void add(Object o) {
        items.add(o);
    }

    public void addAll(List items1) {
        items.addAll((Collection<? extends Object>) items1);
    }
    
    public Object getSelectedItem() {
        return items.get(selectedIndex);
    }

    public void setSelectionColor(Color selectionColor) {
        this.selectionColor = selectionColor;
    }

    public Color getSelectionColor() {
        return selectionColor;
    }
    
    //=======================================================================setters=============================================================

    //=======================================================================painting functions===================================================
    void precomputeShape() {
        if(items.isEmpty()) return;
        shapes = new ArrayList<>();
        int w = getWidth() / items.size(), h = Math.min(getHeight()>>1, getWidth()>>1);
        w = Math.min(getWidth()>>1, w);
        Area a = new Area();
        for (Object elem : items) {
            shapes.add(new Rectangle(getWidth() >> 1, getHeight()/2-h, w, h));
        }
    }

    void paintRectangularStrips(Graphics2D g2d) {
        g2d.setColor(Color.black);
        int i = 0;        
        double totAngle = -angle;
        if (shapes == null) {
            precomputeShape();
        }
        ArrayList<Color> colors = getColorBands(getBackground(), items.size());
        for (Shape shape : shapes) {
            Area a = new Area(shape);
            totAngle +=  angle;
            a.transform(AffineTransform.getRotateInstance(Math.toRadians(totAngle), getWidth() >> 1, getHeight() / 2));
            g2d.setColor(colors.get(i++));
            g2d.fill(a);
        }
    }
    
    void paintText(Graphics2D g2d) {
        int w = getWidth() / items.size(), h = Math.min(getHeight()>>1, getWidth()>>1);
        w = Math.min(getWidth()>>1, w);
        for (int j = items.size()-1; j >= 0 ; j--) {
            if (angle == 0 && j != 0) {
                continue;
            }
            g2d.setColor(Color.red);
            if(j ==  selectedIndex) {
                g2d.setColor(selectionColor);
            }
            drawCenteredText(items.get(j).toString(), g2d, getWidth() / 2 + w / 2, getHeight()/2 - h / 2);
            g2d.transform(AffineTransform.getRotateInstance(Math.toRadians(angle), getWidth() >> 1, getHeight() / 2));
        }
    }
    
    void paintHolderPin(Graphics2D g2d) {
        g2d.setTransform(AffineTransform.getRotateInstance(0));
        draw3DShpere(g2d, getWidth()/2, getHeight()/2, Math.max(20, getWidth()/20), 1);
    }
    
    public void drawCenteredText(String s, Graphics2D g2d, int cx, int cy) {
        g2d.setFont(getFont());
        g2d.transform(AffineTransform.getQuadrantRotateInstance(-1, cx, cy));
        int w = g2d.getFontMetrics().charWidth(s.charAt(0)) * s.length(), h = g2d.getFontMetrics().getDescent();
        g2d.drawString(s, cx - w / 2, cy + h);
        g2d.transform(AffineTransform.getQuadrantRotateInstance(+1, cx, cy));;
    }
    
    public static Color darken(Color color, double fraction) {
        fraction = 1 - fraction;
        int red = (int) Math.round(Math.max(0, color.getRed() - 255 * fraction));
        int green = (int) Math.round(Math.max(0, color.getGreen() - 255 * fraction));
        int blue = (int) Math.round(Math.max(0, color.getBlue() - 255 * fraction));
        return new Color(red, green, blue);
    }

    public void fillCircle(Graphics2D g2d, Point p, int r) {
        g2d.fillOval(p.x - r / 2, p.y - r / 2, r, r);
    }
    
    public void draw3DShpere(Graphics2D g2d, int cx, int cy, int r, int dir) {
        int radialX = cx, radialY = cy, radius = r / 2;
        if (dir == 2) {
            radialX -= r / 4;
            radialY -= r / 4;
            radius = 3 * r / 4;
        } else if (dir == 4) {
            radialX += r / 4;
            radialY += r / 4;
            radius = 3 * r / 4;
        } else if (dir == 1) {
            radialX += r / 4;
            radialY -= r / 4;
            radius = 3 * r / 4;
        } else if (dir == 3) {
            radialX -= r / 4;
            radialY += r / 4;
            radius = 3 * r / 4;
        }
        g2d.setPaint(new RadialGradientPaint(new Point(radialX, radialY), (float) (r / 2),
                new float[]{0.0f, 0.6f, 1f},
                new Color[]{Color.WHITE, lighten(getForeground(), 0.6f), darken(getForeground(), 1f)}));
        fillCircle(g2d, new Point(cx, cy), r);
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

    void paintActualComponent(Graphics2D g2d) {
        if(items == null || items.isEmpty()) return;
        paintRectangularStrips(g2d);
        paintText(g2d);
        paintHolderPin(g2d);
    }

    void animateExpansion() {
        t = new Timer(items.size() + 1, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean stop = true;
                if (!reverse && angle < limit) {
                    stop = false;
                    angle++;
                }
                if (reverse && angle > limit) {
                    stop = false;
                    --angle;
                }
                if (stop) {
                    t.stop();
                }
                repaint();
            }
        });
        t.start();
    }
    
    public ArrayList<Color> getColorBands(Color color, int bands) {

        ArrayList<Color> colorBands = new ArrayList<>();
        for (int index = 0; index < bands; index++) {
            colorBands.add(lighten(color, 1-(double) index / (double) bands));
        }
        return colorBands;

    }

    public static Color lighten(Color color, double fraction) {
        fraction = 1 - fraction;
        int red = (int) Math.round(Math.min(255, color.getRed() + 255 * fraction));
        int green = (int) Math.round(Math.min(255, color.getGreen() + 255 * fraction));
        int blue = (int) Math.round(Math.min(255, color.getBlue() + 255 * fraction));
        return new Color(red, green, blue);
    }
    //=======================================================================painting functions===================================================

    
    
    //=======================================================================update functions======================================================
     final void addTrigger() {
        addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent me) {
                double totAngle = -angle, r = Math.max(20, getWidth()/20);
                Shape s = new Ellipse2D.Double(getWidth()/2-r, getHeight()/2-r, r*2, r*2);
                for (int i = shapes.size()-1; i >= 0; i--) {
                    Shape shape = shapes.get(i);
                    Area a = new Area(shape);
                    totAngle += angle;
                    a.transform(AffineTransform.getRotateInstance(Math.toRadians(totAngle), getWidth() >> 1, getHeight() / 2));
                    if (a.contains(me.getPoint()) && !s.contains(me.getPoint())) {
                        selectedIndex = i;
                    }
                }
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent me) {
                limit = 360 * 1.0 / (items.size());
                reverse = false;
                animateExpansion();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                limit = 0;
                reverse = true;
                animateExpansion();
            }
        });
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                precomputeShape();
            }
        });
    }
    //=======================================================================update functions======================================================

    
    
    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        Graphics2D g2d = (Graphics2D) g;
        setHints(g2d);
        paintActualComponent(g2d);
    }

    static void createJFrame(JComponent[] jcs) {
        jcs[0].setBackground(Color.black);
        ((JExpandingList) jcs[0]).add("fuck1");
        ((JExpandingList) jcs[0]).add("fuck2");
        ((JExpandingList) jcs[0]).add("fuck2as");
        ((JExpandingList) jcs[0]).add("fuck2asa");
        ((JExpandingList) jcs[0]).add("fuck2a");
       // ((JExpandingList) jcs[0]).add("fuckzxc2");
      //  ((JExpandingList) jcs[0]).add("fuck2s");((JExpandingList) jcs[0]).add("fuckasdd2");((JExpandingList) jcs[0]).add("fuckasd2");
        
        

        jcs[0].setFont(new java.awt.Font("Segoe UI Semibold", 0, 30));
        JFrame jf = new JFrame();
        jf.setLayout(new GridLayout(jcs.length, 1));
        jf.setSize(450, 450);
        for (JComponent jComponent : jcs) {
            jf.add(jComponent);
        }
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }

    public static void main(String[] args) {
        createJFrame(new JComponent[]{new JExpandingList()});
    }
}
