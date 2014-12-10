/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Tamojit9
 */
package ComponentAccessor;

import aurelienribon.tweenengine.TweenAccessor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import jcircularScroller.JDottedLine;

public class ComponentAccessor implements TweenAccessor<JDottedLine> {
       public static final int POS_XY = 1;
	public static final int CPOS_XY = 2;
	public static final int SCALE_XY = 3;
	public static final int OPACITY = 5;
	public static final int TINT = 6;
        public static final int SCALE_CXY = 7;
        public static final int EXT_XR = 8, EXT_XL = 9;
        public static final int EXT_YU = 10, EXT_YD = 11, PROGRESS = 12;
        
	@Override
	public int getValues(JDottedLine target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POS_XY:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;

			case CPOS_XY:
				returnValues[0] = target.getX() + target.getWidth()/2;
				returnValues[1] = target.getY() + target.getHeight()/2;
				return 2;

			case SCALE_XY:
				returnValues[0] = (float) target.getWidth();
				returnValues[1] = (float) target.getHeight();
				return 2;
                            
			case OPACITY: 
                            returnValues[0] = target.getBackground().getAlpha()/255; 
                            return 1;
                            
			case TINT:
				returnValues[0] = target.getBackground().getRed();
				returnValues[1] = target.getBackground().getGreen();
				returnValues[2] = target.getBackground().getBlue();
				return 3;
                            
                        case SCALE_CXY :
                            returnValues[0] = (float) target.getWidth();
                            returnValues[1] = (float) target.getHeight();
                            return 2;
                            
                        case EXT_XL :
                            returnValues[0] = target.getWidth();
                            return 1;
                            
                        case EXT_XR :
                            returnValues[0] = target.getWidth();
                            return 1;
                            
                        case EXT_YU :
                            returnValues[0] = target.getHeight();
                            return 1;
                            
                        case EXT_YD :
                            returnValues[0] = target.getHeight();
                            return 1;
                        case PROGRESS :
                            returnValues[0] = (float) target.getProgress();
			default: 
                            assert false; 
                            return -1;
		}
	}
        int cx = -1, cy = -1;
	@Override
	public void setValues(JDottedLine target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POS_XY: 
                            target.setLocation((int)newValues[0], (int)newValues[1]);
                            break;
			case CPOS_XY: 
                            target.setLocation((int)newValues[0] - target.getWidth()/2, (int)newValues[1] - target.getHeight()/2); 
                            break;
			case SCALE_XY: 
                            target.setSize((int)newValues[0], (int)newValues[1]);  
                            break;
			case OPACITY:
				Color c = target.getBackground();
				target.setBackground(new Color(c.getRed()/255, c.getGreen()/255, c.getBlue()/255, newValues[0]));
                                target.repaint();
                                target.revalidate();
				break;
			case TINT:
				c = target.getBackground();
				target.setBackground(new Color(newValues[0]/255, newValues[1]/255, newValues[2]/255, c.getAlpha()/255));
				break;
                        case SCALE_CXY :
                            Point p = target.getLocation();
                            if(cx == -1) {
                                cx = (int)p.x + target.getWidth()/2;
                                cy = (int)p.y + target.getHeight()/2;
                            }
                            target.setLocation(cx-(target.getWidth()/2), cy-(target.getHeight()/2));
                            target.setSize((int)newValues[0], (int)newValues[1]);
                        
                        case EXT_XL :
                            int rx = target.getLocation().x + target.getWidth(), ry = target.getLocation().y;
                            target.setSize((int) newValues[0], target.getHeight());
                            target.setLocation(rx-target.getWidth(), ry);
                            break;
                        case EXT_XR :
                            target.setSize((int) newValues[0], target.getHeight());
                            break;
                        case EXT_YD :
                            target.setSize(target.getWidth(), (int) newValues[0]);
                            break;
                        case EXT_YU :
                            int dx = target.getLocation().x + target.getWidth(), dy = target.getLocation().y + target.getHeight();
                            target.setSize(target.getWidth(), (int) newValues[0]);
                             target.setLocation(dx-target.getWidth(), dy-target.getHeight());
                            break;
                        case PROGRESS :
                            target.setProgress(newValues[0]);
                        default: 
                            assert false;
		}
	}
}