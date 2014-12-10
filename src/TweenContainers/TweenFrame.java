/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TweenContainers;

import ComponentAccessor.ComponentAccessor;
import aurelienribon.tweenengine.TweenManager;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
 *
 * @author Tamojit9
 */
public class TweenFrame extends javax.swing.JFrame{
    protected TweenManager tweenManager;
    protected DrawingCanvas canvas = new DrawingCanvas() {

        @Override
        protected void update(int elapsedMillis) {
            tweenManager.update(elapsedMillis);
        }
    };
    public TweenFrame() {
        tweenManager = new TweenManager();
        aurelienribon.tweenengine.Tween.registerAccessor(Component.class, new ComponentAccessor());
        addWindowListener(new WindowAdapter() {
                        @Override public void windowOpened(WindowEvent e) {
                                canvas.start();
                        }
        });
    }
}
