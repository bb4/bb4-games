package com.barrybecker4.game.twoplayer.hex.ui;

import java.awt.*;

public class HexagonRenderer {

    public static void fillHexagon(Graphics2D g2, Point point, double radius, Color color) {
        drawHexagonAux(g2, point, radius, true, color, null);
    }

    public static void drawHexagon(Graphics2D g2, Point point, double radius, Color color, Stroke stroke) {
        drawHexagonAux(g2, point, radius, false, color, stroke);
    }

    private static void drawHexagonAux(Graphics2D g2, Point point, double radius,
                                       boolean fill, Color color, Stroke stroke) {

        int numPoints = 7;
        int[] xpoints = new int[numPoints];
        int[] ypoints = new int[numPoints];

        for (int i = 0; i <= 6; i++) {
            double angStart = HexUtil.rad(30 + 60 * i);
            xpoints[i] = (int)(point.getX() + radius * Math.cos(angStart));
            ypoints[i] = (int)(point.getY() + radius * Math.sin(angStart));
        }

        Polygon poly = new Polygon(xpoints, ypoints, numPoints);
        g2.setColor(color);

        if (fill) {
            g2.fillPolygon(poly);
        }
        else {
            g2.setStroke(stroke);
            g2.drawPolygon(poly);
        }
    }
}
