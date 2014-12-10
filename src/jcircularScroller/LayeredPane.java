/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jcircularScroller;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

/**
 *
 * @author Tamojit9
 */
public class LayeredPane {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        JLayeredPane layeredPane = new JLayeredPane();
        frame.setContentPane(layeredPane);
        frame.setSize(1000, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JExpandingList jExpandingList = new JExpandingList();
        jExpandingList.setSize(100, 100);
        jExpandingList.setLocation(100, 100);
        jExpandingList.add("test1");
        jExpandingList.add("test2");
        layeredPane.add(jExpandingList, 0);
        final JColorLoaderMonoChrome jColorLoaderMonoChrome = new JColorLoaderMonoChrome(4);
        jColorLoaderMonoChrome.setColors(new Color[]{Color.BLACK, Color.RED, Color.BLUE, Color.GREEN});
        jColorLoaderMonoChrome.setSize(1000, 10);
        jColorLoaderMonoChrome.setLocation(00, 100);
        layeredPane.add(jColorLoaderMonoChrome, 2);
        jColorLoaderMonoChrome.animate(5);
        
        JHiddenPanel jHiddenPanel = new JHiddenPanel();
        jHiddenPanel.setTabText("testing this shit");
        jHiddenPanel.setLocation(500, 00);
        jHiddenPanel.setSize(480, 500);
        JCircularScroller jCircularScroller = new JCircularScroller();
        jCircularScroller.setSize(300,500);
        jCircularScroller.setTextColor(Color.yellow);
        jCircularScroller.setBackground(Color.GREEN.darker());
        jCircularScroller.setForeground(Color.GREEN.darker().darker());
        jHiddenPanel.setHiddenComponent(jCircularScroller);
        jHiddenPanel.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20));
        layeredPane.add(jHiddenPanel, 1);
    }
}
