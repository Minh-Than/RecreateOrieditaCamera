package model;

import components.Point;
import modelInterface.Camera;

/**
 * A Singleton subclass of Camera to display the crease pattern.
 */
public class CPCamera extends Camera {

    private static CPCamera cpCamera = new CPCamera();

    private CPCamera(){ initialize(); }

    public static CPCamera getInstance(){ return cpCamera; }

    public void initialize(){
        cameraX = 300.0;
        cameraY = 300.0;
        transformX = 0.0;
        transformY = 0.0;
        transformAngle = 0.0;
        setSinCosRad(transformAngle);
        transformZoom = 1.0;
    }

    /**
     *Convert a coordinate into a point in a transformable screen
    - {x, y} can be rewritten as <rcosθ, rsinθ> from trigonometry (1)
    - The rotated vector about an origin includes the change in angle(α), written as {rcos(θ+α), rsin(θ+α)}
    - Use Ptolemy’s identities (https://www2.clarku.edu/faculty/djoyce/trig/identities.html)
      to rewrite it to {rcosθcosα - rsinθsinα, rsinθcosα + rcosθsinα}
    - Substitute rcosθ and rsinθ with x and y respectively from (1) to get the resulting vector expression:
      {xcosα - ysinα, ycosα + xsinα}
     * @param p object point
     * @return display point
     */
    public Point CP2DisplayPoint(Point p){

        // - Get vector from camera to p, with camera as origin
        double x1 = p.getX() - transformX; 
        double y1 = p.getY() - transformY;

        double x2 = x1 * cosRad - y1 * sinRad;
        double y2 = y1 * cosRad + x1 * sinRad;

        x2 *= transformZoom;
        y2 *= transformZoom;
    
        return new Point(x2 + cameraX, y2 + cameraY);
    }

    // and vice versa
    public Point Display2CPPoint(Point p){
        double x1 = p.getX();
        double y1 = p.getY();
        x1 -= cameraX;
        y1 -= cameraY;

        x1 /= transformZoom;
        y1 /= transformZoom;

        // - Same deal, just with negative α
        // <xcosα + ysinα, ycosα - xsinα>
        double x2 = x1 * cosRad + y1 * sinRad;
        double y2 = y1 * cosRad - x1 * sinRad;

        return new Point(x2 + transformX, y2 + transformY);
    }
}
