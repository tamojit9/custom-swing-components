/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jcircularScroller;

import java.awt.Point;

/**
 *
 * @author tamojit
 */
public class Angle {
    
    public static enum Quadrant {
        first, second, third, fourth
    };
    
    static Quadrant getQuadrant(Point p) {
        if(p.x >= 0) {
            if(p.y >= 0) return Quadrant.first;
            if(p.y < 0) return Quadrant.fourth;
        } else {
            if(p.y >= 0) return Quadrant.second;
            if(p.y < 0) return Quadrant.third;
        }
        return null;
    }
    
    static double adjustAngle(Point p, double angle) {
        Quadrant q = getQuadrant(p);
        switch(q) {
            case first : return angle;
            case second : return angle;
            case third : return 360-angle;
            case fourth : return 360-angle;
        }
        return 0.0;   
    }
    
    static double getAngle(Point a, Point b, Point center) {
        int x1, y1, x2, y2;
        x1 = (int)(a.x-center.x); y1 = (int) (-a.y+center.y);
        x2 = (int) (b.x-center.x); y2 = (int) (-b.y+center.y);
        double s = Math.abs(Math.toDegrees(Math.atan2(y1, x1)));
        double angle1 = Math.abs(Math.toDegrees(Math.atan2(y2, x2)));
        s = adjustAngle(new Point(x1, y1), s);
        angle1 = adjustAngle(new Point(x2, y2), angle1);
        angle1 -= s;
        if(angle1 < 0) angle1 += 360;
        return angle1;
    }
}
