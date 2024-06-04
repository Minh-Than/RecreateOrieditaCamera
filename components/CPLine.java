package components;

import java.awt.Color;

public class CPLine {
    private int lineType;
    private Point p1, p2;

    public CPLine(int lineType, double x1, double y1, double x2, double y2){
        this.lineType = lineType;
        this.p1 = new Point(x1, y1);
        this.p2 = new Point(x2, y2);
    }

    public Point getP1(){ return p1; }

    public Point getP2(){ return p2; }

    public double getX1(){ return p1.getX(); }

    public double getY1(){ return p1.getY(); }

    public double getX2(){ return p2.getX(); }

    public double getY2(){ return p2.getY(); }

    public Color getLineType(){
        return switch(lineType){
            case 1 -> Color.BLACK;
            case 2 -> Color.RED;
            case 3 -> Color.BLUE;
            case 4 -> Color.CYAN;
            default -> throw new IllegalArgumentException("Unexpected value: " + lineType);
        };
    }
}
