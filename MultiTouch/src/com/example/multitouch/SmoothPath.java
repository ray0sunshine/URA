package com.example.multitouch;

import android.graphics.Path;
import android.graphics.PointF;

//this needs to be able to differentiate between commited and vector lines

public class SmoothPath extends Path {
	private int length;//number of line segments in the path
	private static final float minLen = 32;//minimum length for curving the segment
	
	//holds 4 points at any time to do curves
	private PointF p0;//point used with p1 to calculate the first anchor of the final segment
	private PointF p1;//start of segment to be final curved
	private PointF p2;//current end position
	private PointF p3;//latest coordinate
	
	public SmoothPath() {
		initSmoothPath();
	}
	
	public SmoothPath(float x, float y) {
		initSmoothPath();
		moveTo(x,y);
		p3.set(x,y);
	}
	
	private void initSmoothPath(){
		length = 0;
		p0 = new PointF();
		p1 = new PointF();
		p2 = new PointF();
		p3 = new PointF();
	}
	
	public void curveTo(float x, float y) {
		//need to do 2 things: re-curve the previous segment if needed, pre-curve or line the latest segment depending on length
		//make sure segment is at least a certain length, otherwise just lineTo if it's too short
		//once we have at least 2 segements we can rewind and cubicTo the 2nd last one using either no first anchor (on top of 1st point)
		//or use the extension of the previous segment end and the last segement end
		//for the latest segment, just use cubic to with the 2nd anchor being the 2nd end
		
		//if only 1 segment, just line too
		//else do more complex stuff
		//update points
		
		p0.set(p1);
		p1.set(p2);
		p2.set(p3);
		p3.set(x,y);
		
		length++;
		switch(length){
		case 1:
			lineTo(p3.x, p3.y);
			break;
			
		case 2:
			if(Ops.dist(p1, p2) > minLen){
				rewind();
				PointF ap = Ops.extend(p3, p2);
				quadTo(ap.x, ap.y, p2.x, p2.y);
			}
			
			if(Ops.dist(p2, p3) > minLen){
				PointF ap = Ops.extend(p1, p2);
				quadTo(ap.x, ap.y, p3.x, p3.y);
			}else{
				lineTo(p3.x, p3.y);
			}
			break;
			
		default:
			if(Ops.dist(p1, p2) > minLen){
				rewind();
				PointF ap1 = Ops.extend(p0, p1);
				PointF ap2 = Ops.extend(p3, p2);
				cubicTo(ap1.x, ap1.y, ap2.x, ap2.y, p2.x, p2.y);
			}
			
			if(Ops.dist(p2, p3) > minLen){
				PointF ap = Ops.extend(p1, p2);
				quadTo(ap.x, ap.y, p3.x, p3.y);
			}else{
				lineTo(p3.x, p3.y);
			}
			break;
		}
	}
}
