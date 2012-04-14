package dk.frv.enav.esd.layers.ais;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.omGraphics.OMPoly;

public class GraphicObjects extends OMGraphicList{
	
	public GraphicObjects(){
		super();
	}
	
	public void createGraphics(int[] xPoints,int[] yPoints){
		OMPoly polygon = new OMPoly(xPoints, yPoints);
		
	}
	
	public void createHeading(int[] xPoints, int[] yPoints, double heading){
		
	}
}
