package com.example.multitouch;

import android.graphics.PointF;

public class Ops {
	private static final float extRatio = 1/3;
	
	//calculates the distance between 2 points
	public static float dist(PointF p1, PointF p2){
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		return (float)Math.sqrt(dx*dx + dy*dy);
	}
	
	//calculates the extended anchor point based on 2 ends of a segment for cubic curves
	//returns the point reached but tracing a line from p1 to p2 and going beyond by extRatio*dist(p1,p2)
	public static PointF extend(PointF p1, PointF p2){
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;
		return new PointF(p2.x + dx*extRatio, p2.y + dy*extRatio);
	}
}
