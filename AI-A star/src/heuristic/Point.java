package heuristic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Point {
	int x; // row
	int y; // col
	int wall = 0;
	
	ArrayList<Point> neighbors;
	public ArrayList<Point> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(ArrayList<Point> neighbors) {
		this.neighbors = neighbors;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	Point from;
	
	double f = 0;
	double g = 0;
	double h = 0;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Point end) {
		return (this.x == end.x && this.y == end.y);
	}
	
	double func() {
		return (1-Luoi.alpha) * g + (Luoi.alpha) * h; 
	}
	
	void initNeighbor(Point[][] spots) {
		neighbors = new ArrayList<>();
		int rows = spots.length;
		int cols = spots[0].length;
		
		if(x > 0) {
			if(spots[x-1][y].wall != 1)
				neighbors.add(spots[x-1][y]);
		}
		if(y > 0) {
			if(spots[x][y-1].wall != 1)
				neighbors.add(spots[x][y-1]);
		}
		if(x < rows-1) {
			if(spots[x+1][y].wall != 1)
				neighbors.add(spots[x+1][y]);
		}
		if(y < cols-1) {
			if(spots[x][y+1].wall != 1)
				neighbors.add(spots[x][y+1]);
		}
		if(Luoi.algoSelect != 0) {
			if(x > 0 && y > 0) {
				if(spots[x-1][y-1].wall != 1)
					neighbors.add(spots[x-1][y-1]);
			}
			if(x > 0 && y < cols-1) {
				if(spots[x-1][y+1].wall != 1)
					neighbors.add(spots[x-1][y+1]);
			}
			if(x < rows-1 && y > 0) {
				if(spots[x+1][y-1].wall != 1)
					neighbors.add(spots[x+1][y-1]);
			}
			if(x < rows-1 && y < cols-1) {
				if(spots[x+1][y+1].wall != 1)
					neighbors.add(spots[x+1][y+1]);
			}
		}
	}

	void draw(Graphics2D g2d, int size) {
		g2d.fillRect(size * y, size * x, size, size);
	}
	void drawDen(Graphics2D g2d, int size) {
		g2d.fillOval(size * y, size * x, size, size);
	}

	void drawPoint(Graphics2D g2d, int size, Color color) {
		BasicStroke bs1 = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
		g2d.setStroke(bs1);
		g2d.setColor(color);
		g2d.drawRect(size * y, size * x, size, size);
	}

}
