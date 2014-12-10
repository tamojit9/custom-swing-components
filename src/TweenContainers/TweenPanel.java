/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TweenContainers;

import ComponentAccessor.ComponentAccessor;
import TweenContainers.DrawingCanvas.Callback;
import aurelienribon.tweenengine.TweenManager;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/**
 *
 * @author Tamojit9
 */
public class TweenPanel extends JPanel{
    protected TweenManager tweenManager;
    protected DrawingCanvas canvas;
    public void setCallback(Callback cb) {
        canvas.setCallback(cb);
    }
    public TweenPanel() {
        tweenManager = new TweenManager();
        aurelienribon.tweenengine.Tween.registerAccessor(Component.class, new ComponentAccessor());
        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if(canvas == null) {
                    canvas = new DrawingCanvas() {
                        @Override
                        protected void update(int elapsedMillis) {
                            tweenManager.update(elapsedMillis);
                        }
                    }.start();
                }
            }
        });
    }
}
