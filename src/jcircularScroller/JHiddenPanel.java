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
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.*;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.border.LineBorder;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

/**
 *
 * @author Tamojit9
 */
public class JHiddenPanel extends JComponent implements Serializable {
    
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    
    private String sampleProperty, tabText;
    Color tabColor = Color.lightGray;
    JComponent hiddenComponent;
    int hw, hh;

    public JComponent getHiddenComponent() {
        return hiddenComponent;
    }

    public void setHiddenComponent(JComponent hiddenComponent) {
        this.hiddenComponent = hiddenComponent;
        hw = hiddenComponent.getWidth();
        hh = hiddenComponent.getHeight();
        if(hw == 0) hw = getWidth();
        if(hh == 0) hh = getHeight();
        hiddenComponent.setSize(0, hh);
        hiddenComponent.setLocation(getWidth(), 0);
        add(hiddenComponent);
        addTrigger();
    }
    
    

    public Color getTabColor() {
        return tabColor;
    }

    public void setTabColor(Color tabColor) {
        this.tabColor = tabColor;
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
    
    public String getTabText() {
        return tabText;
    }

    public void setTabText(String tabText) {
        this.tabText = tabText;
    }
    
    private PropertyChangeSupport propertySupport;
    
    public JHiddenPanel() {
        propertySupport = new PropertyChangeSupport(this);
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

    Rectangle2D stringBounds, effective;
    private boolean open, busy;
    int dx;
    
    void paintTheTab(Graphics2D g2d) {
        stringBounds = g2d.getFontMetrics(getFont()).getStringBounds(tabText, g2d);
        effective = new Rectangle2D.Double(getWidth()-stringBounds.getHeight()-dx, 0, stringBounds.getHeight(), stringBounds.getWidth());
        g2d.setColor(tabColor);
        g2d.fillRect(getWidth()-(int)stringBounds.getHeight()-dx, 0, (int)stringBounds.getHeight(), (int)stringBounds.getWidth());
        g2d.setColor(getForeground());
        drawCenteredTextWithRotations(tabText, g2d, (int) (getWidth()-stringBounds.getHeight()/2)-dx, (int) (stringBounds.getWidth()/2), Math.toRadians(90*3));
    }
    
    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        //System.out.println(dx);
        this.dx = dx;
        hiddenComponent.setSize(dx, hh);
        hiddenComponent.setLocation(getWidth()-dx, 0);
        repaint();
    }
    
    void addTrigger() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                //System.out.println("busy = "+busy);
                //if(busy) return;
                busy = true;
                //System.out.println("effective = "+effective.contains(me.getPoint()));
                if(effective.contains(me.getPoint())) {
                    open = !open;
                    animate();
                }
            }
        }); 
    }
 
    
    private void animate() {
        //System.out.println("open = "+open);
        PropertySetter sliderDx = new PropertySetter(this, "dx", open ? 0 : hw, open ? hw : 0);
        Animator animate = new Animator(1000, sliderDx);
        animate.setAcceleration(0.7f);
        animate.setDeceleration(0.3f);
        animate.start();
        busy = false;
    }
    
    @Override
    public void paint(Graphics grphcs) {
        Graphics2D g2d = (Graphics2D) grphcs;
        paintTheTab(g2d);
        super.paint(grphcs);
    }
    
    
    public static void main(String[] args) {
        JHiddenPanel jHiddenPanel = new JHiddenPanel();
        jHiddenPanel.setTabText("testing this shit");
        JCircularScroller jCircularScroller = new JCircularScroller();
        jCircularScroller.setSize(300,500);
        jCircularScroller.setTextColor(Color.yellow);
        jCircularScroller.setBackground(Color.GREEN.darker());
        jCircularScroller.setForeground(Color.GREEN.darker().darker());
        jHiddenPanel.setHiddenComponent(jCircularScroller);
        createJFrame(new JComponent[]{jHiddenPanel});
    }
    
    static void createJFrame(JComponent[] jcs) {
        jcs[0].setBorder(new LineBorder(Color.yellow));
        jcs[0].setFont(new java.awt.Font("Segoe UI Semibold", 0, 50));
        JFrame jf = new JFrame();
        jf.setLayout(new GridLayout(jcs.length, 1));
        jf.setSize(600, 600);
        for (JComponent jComponent : jcs) {
            jf.add(jComponent);
        }
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}
