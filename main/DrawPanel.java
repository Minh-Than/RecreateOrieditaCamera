package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import components.CPLine;
import components.Point;
import model.CPCamera;

public class DrawPanel extends JPanel{
    List<CPLine> cpLines = new ArrayList<>();
    CPCamera cpCamera = CPCamera.getInstance();
    Point transformShowPoint = new Point(); // Only for showcasing transform point

    public DrawPanel(List<CPLine> cpCoords){
        super();
        setFocusable(true);
        requestFocus();
        setDoubleBuffered(true);
        setBackground(Color.WHITE);

        this.cpLines = cpCoords;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                double transformAngle = cpCamera.getTransformAngle();

                // Handle left and right arrow keys
                if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    cpCamera.addAngle(22.5);
                } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (Math.abs(transformAngle - 0.0) < 1e-6) transformAngle = 360.0; 
                    cpCamera.addAngle(-22.5);
                } else {
                    return;
                }

                // Set to 0 if angle approaches 360
                transformAngle = cpCamera.getTransformAngle();
                if(Math.abs(Math.abs(transformAngle) - 360.0) < 1e-6) transformAngle = 0.0;
                cpCamera.setTransformAngle(transformAngle);
                cpCamera.setSinCosRad(transformAngle);

                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                double x = e.getX();
                double y = e.getY();

                Point p = cpCamera.Display2CPPoint(new Point(x, y));
                cpCamera.setTransformX(p.getX());
                cpCamera.setTransformY(p.getY());

                transformShowPoint.setX(x);
                transformShowPoint.setY(y);

                cpCamera.setCameraX(x);
                cpCamera.setCameraY(y);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e){
                cpCamera.setCameraX(e.getX());
                cpCamera.setCameraY(e.getY());
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                cpCamera.setCameraX(e.getX());
                cpCamera.setCameraY(e.getY());
                repaint();
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoom = cpCamera.getTransformZoom();
                double zoomFactor = 0.1; // Customize this value for a smoother experience
                double preciseNotches = e.getPreciseWheelRotation();
                cpCamera.setTransformZoom(zoom * (1.0 - preciseNotches * zoomFactor));
                
                repaint();
            }
        });
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle panelBound = this.getVisibleRect();
        Line2D.Double line = new Line2D.Double();

        double cameraX = cpCamera.getCameraX();
        double cameraY = cpCamera.getCameraY();
        double transformX = transformShowPoint.getX();
        double transformY = transformShowPoint.getY();

        // CP lines
        drawCPLines(g2, panelBound);

        // Camera and transform points & coords
        drawPoint(g2, cameraX, cameraY, Color.BLACK);
        drawPoint(g2, transformX, transformY, Color.MAGENTA);

        // Visual connecting line
        line = new Line2D.Double(transformX, transformY, cameraX, cameraY);
        g2.draw(line);

        // Radar for angle and zooming
        drawRadar(g2, cpCamera.getTransformZoom());
    }

    public void drawCPLines(Graphics2D g2, Rectangle2D panelBound){
        Line2D.Double line = new Line2D.Double();

        for(CPLine cpLine : cpLines){
            Point p1 = cpCamera.CP2DisplayPoint(cpLine.getP1());
            Point p2 = cpCamera.CP2DisplayPoint(cpLine.getP2());

            line.setLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

            if(!panelBound.intersectsLine(line)) continue;
            
            g2.setColor(cpLine.getLineType());
            g2.draw(line);
        }
    }

    public void drawPoint(Graphics2D g2, double x, double y, Color color){
        g2.setColor(color);
        String mouseStr = "(" + x + ", " + x + ")";
        g2.drawString(mouseStr, (int) x + 10, (int) y);
        g2.fillOval((int) x - 2, (int) y - 2, 4, 4);
    }

    public void drawRadar(Graphics2D g2, double zoomFactor){
        Line2D.Double line = new Line2D.Double();

        // radar body
        g2.setColor(Color.BLACK);
        g2.fillOval(10, 10, 150, 150);

        // angle arc and lateral grid 
        g2.setColor(Color.orange);
        g2.fill(FillArc(70.0, 70.0, 30.0, 30.0, 0.0, -cpCamera.getTransformAngle()));
        g2.setColor(Color.GRAY);
        line = new Line2D.Double(20.0, 85.0, 85.0 + 65.0, 85.0);
        g2.draw(line);
        line = new Line2D.Double(85.0, 20.0, 85.0, 85.0 + 65.0);
        g2.draw(line);

        // radar circles
        double interpolatedFactor = Math.log(zoomFactor + 1) % 1.0;
        double zoomTLPos = 85 - 65 * interpolatedFactor;
        double diameter = 130 * interpolatedFactor;
        Ellipse2D.Double zoomRadar = new Ellipse2D.Double(zoomTLPos, zoomTLPos, diameter, diameter);
        g2.draw(zoomRadar);

        // radar rim and angle line
        g2.setColor(Color.WHITE);
        g2.draw(new Ellipse2D.Double(20, 20, 130, 130));
        line = new Line2D.Double(85.0, 85.0, 85.0 + (65 * cpCamera.getCosRad()), 85.0 + (65 * cpCamera.getSinRad()));
        g2.draw(line);
        g2.fillOval(82, 82, 6, 6);
    }

    public Shape FillArc(double x, double y, double width, double height, double startAngle, double endAngle){
        Shape arc;
        Path2D path = new Path2D.Double();

        arc = new Arc2D.Double(x, y, width, height,startAngle, endAngle, Arc2D.PIE);
        path.append(arc, true);
        path.closePath();

        arc = path;

        return arc;
    }
}
