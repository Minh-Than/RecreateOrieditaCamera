package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
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

                int notches = e.getWheelRotation();
                if (notches < 0) {
                    cpCamera.setTransformZoom(zoom / 0.7);
                } else {
                    cpCamera.setTransformZoom(zoom * 0.7);
                }

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

        // CP lines
        Line2D.Double line = new Line2D.Double();
        for(CPLine cpLine : cpLines){
            Point p1 = cpCamera.CP2DisplayPoint(cpLine.getP1());
            Point p2 = cpCamera.CP2DisplayPoint(cpLine.getP2());

            line.setLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

            if(!panelBound.intersectsLine(line)) continue;
            
            g2.setColor(cpLine.getLineType());
            g2.draw(line);
        }

        // Camera point & coords
        double cameraX = cpCamera.getCameraX();
        double cameraY = cpCamera.getCameraY();

        g2.setColor(Color.BLACK);
        String mouseStr = "(" + cameraX + ", " + cameraY + ")";
        g2.drawString(mouseStr, (int) cameraX + 10, (int) cameraY);
        g2.fillOval((int) cameraX - 2, (int) cameraY - 2, 4, 4);

        // Transform point & coords
        double transformX = transformShowPoint.getX();
        double transformY = transformShowPoint.getY();

        g2.setColor(Color.MAGENTA);
        mouseStr = "(" + transformX + ", " + transformY + ")";
        g2.drawString(mouseStr, (int) transformX + 10, (int) transformY);
        g2.fillOval((int) transformX - 2, (int) transformY - 2, 4, 4);

        // Visual connecting line
        g2.draw(new Line2D.Double(transformX, transformY, cameraX, cameraY));
    }
}
