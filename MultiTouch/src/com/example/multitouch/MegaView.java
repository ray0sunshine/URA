package com.example.multitouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

public class MegaView extends View {
	//should aggregate a drag line to one object
	
	private SparseArray<PointF> activePointers;
	private SparseArray<Float> activePressure;
	private SparseArray<Path> activePath;
	private SparseArray<Integer> pLength;
	
	private Paint viewPainter;
	private Paint textPainter;
	private Paint pathPainter;
	
	private Paint bmPainter;
	private Bitmap bm;
	private Canvas saveCanvas;
	
	private static final int fullSize = 100;
	private static final int maxLine = 20; //writes line to bitmap every 20 segments to keep up performance
	
	public MegaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initMegaView();
	}

	private void initMegaView() {
		activePointers = new SparseArray<PointF>();
		activePressure = new SparseArray<Float>();
		activePath = new SparseArray<Path>();
		pLength = new SparseArray<Integer>();
		
		viewPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
		viewPainter.setColor(Color.RED);
		viewPainter.setStyle(Paint.Style.FILL);
		
		textPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPainter.setTextSize(20);
		
		pathPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
		pathPainter.setColor(Color.BLUE);
		pathPainter.setStyle(Paint.Style.STROKE);
		pathPainter.setStrokeWidth(1);
		pathPainter.setStrokeJoin(Paint.Join.ROUND);
		
		bmPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e){
		int ptrIdx = e.getActionIndex();
		int ptrId = e.getPointerId(ptrIdx);
		int mAction = e.getActionMasked();
		
		//need to standardize and clean up pointer id and indexing
		
		//using cubic to create curves
		//need 4 points, either record the current segment in an array or something
		//use the the 2 end segments to calculate the anchor points by inverting the the difference of the 2 outer points to the 2 inner ends...then divide difference by 3
		//(it's a same assumption so the 2 anchors don't cross over...in most cases...hopefully)
		//first line to, once we have the points, rewind and cubic to
		//need some custom class(s) to make this neater/easier
		//custom path class that extends path, and has a curveTo that deals with the inner stuff....SmoothPath.curveTo(x,y); calls to super.cubicTo, super.lineTo...etc
		//needs to only curve lines that are longer than a certain length...cuz small segments don't matter
		
		switch(mAction){
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				activePointers.put(ptrId, new PointF(e.getX(ptrIdx), e.getY(ptrIdx)));
				activePressure.put(ptrId, e.getPressure(ptrIdx));
				
				Path p = new Path();
				p.moveTo(e.getX(ptrIdx), e.getY(ptrIdx));
				activePath.put(ptrId, p);
				pLength.put(ptrId, 0);
				
				break;
			case MotionEvent.ACTION_MOVE:
				int size = e.getPointerCount();
				for(int i=0; i<size; i++){
					PointF pt = activePointers.get(e.getPointerId(i));
					if(pt != null){
						pt.x = e.getX(i);
						pt.y = e.getY(i);
						activePressure.setValueAt(e.getPointerId(i), e.getPressure(i));
						activePath.get(e.getPointerId(i)).lineTo(pt.x, pt.y);
						pLength.setValueAt(i, pLength.get(e.getPointerId(i)) + 1); //need more efficient way of incrementing
						
						//purge path after certain length and save to bitmap
						if(pLength.get(e.getPointerId(i)) > maxLine){
							saveCanvas.drawPath(activePath.get(e.getPointerId(i)), pathPainter);
							activePath.get(e.getPointerId(i)).reset();
							activePath.get(e.getPointerId(i)).moveTo(pt.x, pt.y);
							pLength.setValueAt(i,0);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:
				saveCanvas.drawPath(activePath.get(ptrId), pathPainter);
				
				activePointers.remove(ptrId);
				activePressure.remove(ptrId);
				activePath.remove(ptrId);
				pLength.remove(ptrId);
				break;
		}
		
		invalidate();
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		canvas.drawBitmap(bm, 0, 0, bmPainter);
		
		int size = activePointers.size();
		for(int i=0; i<size; i++){
			PointF pt = activePointers.valueAt(i);
			float press = activePressure.valueAt(i);
			viewPainter.setAlpha((int) Math.min(255.0*press,255.0));
			if(pt != null){
				int drawSize = (int) (fullSize*press);
				canvas.drawCircle(pt.x, pt.y, drawSize, viewPainter);
				canvas.drawText(Integer.toString(activePointers.keyAt(i)), pt.x-drawSize+10, pt.y-drawSize+30, textPainter);
				canvas.drawPath(activePath.valueAt(i), pathPainter);
			}
		}
		canvas.drawText("Total Points: " + size, 10, 30, textPainter);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		bm = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
		bm.eraseColor(Color.WHITE);
		saveCanvas = new Canvas(bm);
	}
}
