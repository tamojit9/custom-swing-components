/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jcircularScroller;

import ComponentAccessor.ComponentAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
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
public class JDottedLine extends JComponent implements Serializable {
    
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    
    private String sampleProperty;
    
    private PropertyChangeSupport propertySupport;
    
    public JDottedLine() {
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
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    private int n, dx;   
    private int[] dots;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getDx() {
        return dx;
    }
    
    int curr = -1, shift = 0;

    public void setDx(int dx) {
        this.dx = dx;
        if(curr == -1) curr = n-1;
        int progress = dx/50;
        int center = getWidth()>>1, mid = (n-1)/2;
        if(dots[curr] <= shift + center + 20*(curr-mid)) dots[curr] += (shift + center + 20*(curr-mid))/10;
        else if(curr > 0) --curr;
        else {
            curr = n-1;
            if(shift <= 0)
                shift = getWidth()/2+n*20;
            else {
                for (int i = 0; i < dots.length; i++) {
                    dots[i] = shift = -10;
                }
            }
        }
        repaint();
    }
    
    private double progress;

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
        if(curr == -1) curr = n-1;
        int center =  getWidth()/2, mid = n/2, total = n*center<<1, toCover = (int) (progress*total), covered = 0;
        if(progress-0.5 >= 0) {
            toCover -= total/2;
        }
        for(int i = n-1; i >= curr; i--) covered += dots[i];
        toCover -= covered;
        for(int i = curr; i >= 0 && toCover > 0; i--) {
            int d = (center+dx*(i-mid))-dots[i];
            if(shift >  0) d = center-dots[i];
            if(d > toCover) {
                dots[i] += toCover;
                break;
            } else {
              toCover -= d;
              dots[curr--] += d;
            }
        }
        if(Math.abs(progress-0.5) < 0.01)  {
            shift = getWidth()/2;
            curr = n-1;
            for (int i = 0; i < dots.length; i++) {
                dots[i] = dx*(i-mid);
            }
        }
        repaint();
        //System.out.println(progress);
        if(Math.abs(progress-1.0) < 0.001) {
            curr = -1;
            for (int i = 0; i < dots.length; i++) dots[i]= 0;
            shift = 0;
        }
    }
    
    
    
    JDottedLine(int n, int dx) {
        this.n = n;
        this.dx = dx;
        dots = new int[n];
    }
    
    public void drawCircle(Graphics2D g2d, Point p, int r) {
        g2d.fillOval(p.x - r / 2, p.y - r / 2, r, r);
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
    
    @Override
    public void paint(Graphics grphcs) {
        super.paint(grphcs); //To change body of generated methods, choose Tools | Templates.
        Graphics2D g2d = (Graphics2D) grphcs;
        setHints(g2d);
        g2d.setColor(getForeground());
        for(int i : dots) drawCircle(g2d, new Point(shift+i, getHeight()>>1), 10);
    }
    
    private void animate() {
        PropertySetter sliderDx = new PropertySetter(this, "progress", 0.0, 1.0);
        Animator animate = new Animator(5000, sliderDx);
        animate.setAcceleration(0.5f);
        animate.setDeceleration(0.5f);
        animate.setEndBehavior(Animator.EndBehavior.RESET);
        animate.setRepeatBehavior(Animator.RepeatBehavior.LOOP);
        animate.setRepeatCount(10);
        animate.start();
    }
    
    private void animate1() {
       // Tween.to(this, ComponentAccessor.PROGRESS, 2000).target(1).ease(TweenEquations.easeInElastic).start(tweenManager);
    }
    
    public static void main(String[] args) {
        JDottedLine line = new JDottedLine(5, 20);
        createJFrame(new JComponent[]{line});
        line.animate();
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
