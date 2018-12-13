package heuristic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Luoi {
	public final static int countEnd = 1;
	final int ROWS = 30;
	final int COLS = 50;
	Point[][] points;

	private ArrayList<Point> open;
	public ArrayList<Point> close;
	private ArrayList<Point> paths;

	Point start;
	public static Point end[];
	public static int s = countEnd;

	boolean stop = false;
	boolean found = false;
	private int startCount = 1;
	private int pathCount = 0;

	// DISTANCE COST

	int densityValue = 2;
	double normDist = 1; //Chi phí để di chuyển ngang dọc
	double diagDist = Math.sqrt(2); // CHi phí để di chuyển chéo

	// HEURISTIC
	static double alpha = 0.5;
	double p = 0.001; //Trọng số trong Breaking Ties: p < (Chi phí để di chuyển 1 bước) / (Chi phí tối đa để đi đến đích)
	

	// TIMING

	long delay1 = 0;
	long delay2 = 0;

	public static byte algoSelect = 2;
	/*
	 * 0 : manhattan 1: euclide 2: diagon
	 */

	public Luoi() {
		int i, j;

		points = new Point[ROWS][COLS];
		for (i = 0; i < ROWS; ++i) {
			for (j = 0; j < COLS; ++j)
				points[i][j] = new Point(i, j);
		}
		end = new Point[100];
		start = points[5][7];

		end[0] = points[20][44];
		end[1] = points[18][8];
		end[2] = points[6][8];

		open = new ArrayList<>();
		close = new ArrayList<>();

		int expo = (int) Math.log10(COLS * ROWS) + 1;
		System.out.println(expo);
	}

	private Point getMin() {
		int iMin = open.size() - 1;
		for (int i = open.size() - 1; i >= 0; --i) {
			if (open.get(i).f < open.get(iMin).f)
				iMin = i;
		}
		Point n = open.get(iMin);
		open.remove(iMin);
		return n;
	}

	private void addOpen(Point n, byte algoSelect) {
		for(Point neighbor: n.neighbors) {
			if(close.contains(neighbor) == true) 
				continue;
			int a = neighbor.getX() - n.getX();
			int b = neighbor.getY() - n.getY();
			if (points[n.getX() + a][n.getY()].wall == 1 && points[n.getX()][n.getY() + b].wall == 1) {
				continue;
			}
			if(open.contains(neighbor) == false)
				open.add(neighbor);
			
			double tempG;
			double singleCost;
			if(n.x != neighbor.x && n.y != neighbor.y)
				singleCost = diagDist;
			else 
				singleCost = normDist;
			
			if(n.wall == densityValue)
				singleCost = singleCost * densityValue;
			tempG = n.g + singleCost;
			
			if(neighbor.g != 0 && tempG >= neighbor.g) 
				continue;
			neighbor.from = n;
			
			neighbor.g = tempG;
			neighbor.h = heuristic(neighbor, end, algoSelect);
			neighbor.f = neighbor.func();
		}
	}

	private void solution(Point n) {
		delay2 = System.currentTimeMillis();
		stop = true;
		found = true;
		paths = new ArrayList<>();
		Point t = n;

		paths.add(t);
		while (true) {
			t = t.from;
			paths.add(t);
			if (t == start)
				break;
		}
		pathCount = paths.size() - 1;
	}

	// ALGORITHM

	void draw(Graphics2D g2d, boolean searchFlag) {
		if (searchFlag) {
			if (startCount == 1) {
				open.add(start);
				start.g = 0;
				start.f = heuristic(start, end, algoSelect);
				startCount = 0;
			}
			if (!stop) {
				if (open.isEmpty() == false) {
					Point n = getMin();
					for (int i = 0; i < s; i++)
						if (n.equals(end[i])) {
							solution(n);
							return;
						}
					close.add(n);
					n.initNeighbor(points);
					if (n.neighbors.isEmpty() == false)
						addOpen(n, algoSelect);
				} else {
					stop = true;
					return;
				}
			}
		}
		// DRAW HERE
		drawGridlines(g2d, Panel.size);
		drawPoints(g2d, Panel.size);
		if (found)
			drawPath(g2d, Panel.size);
		// END DRAWING
	}

	// DRAWING FUNC

	void drawPoints(Graphics2D g2d, int size) {
		int i, j;
		for (i = 0; i < COLS; ++i) {
			points[0][i].wall = 1;
			points[ROWS-1][i].wall = 1;
		}
		for (i = 0; i < ROWS; ++i) {
			points[i][0].wall = 1;
			points[i][COLS-1].wall = 1;
		}
		
		for (i = 0; i < ROWS; ++i)
			for (j = 0; j < COLS; ++j) {
				if (points[i][j].wall == 1) {
					g2d.setColor(Color.gray);
					points[i][j].draw(g2d, size);
				} else if (points[i][j].wall == densityValue) {
					g2d.setColor(new Color(0xFF99FF));
					points[i][j].draw(g2d, size);
				}
			}
		for (Point n : open) {
			if (n.wall == densityValue)
				g2d.setColor(new Color(0xFFCCFF));
			else
				g2d.setColor(new Color(0xFFFF66));
			n.drawDen(g2d, size);
		}
		for (Point n : close) {
			if (n.wall == densityValue)
				g2d.setColor(new Color(0xFFCC99));
			else
				g2d.setColor(new Color(0xFFCC33));
			n.drawDen(g2d, size);
		}

		g2d.setColor(new Color(0xccffcc));
		start.draw(g2d, size);
		start.drawPoint(g2d, size, Color.green);

		for (i = 0; i < s; i++) {
			g2d.setColor(new Color(0xFF6600));
			end[i].draw(g2d, size);
			end[i].drawPoint(g2d, size, Color.red);
		}
	}

	void drawGridlines(Graphics2D g2d, int size) {
		int i;
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, COLS * size, ROWS * size);

		g2d.setColor(new Color(197, 196, 197));
		for (i = 0; i < ROWS; ++i)
			g2d.drawLine(0, size * i, size * COLS, size * i);
		for (i = 0; i < COLS; ++i)
			g2d.drawLine(size * i, 0, size * i, size * ROWS);
	}

	void drawPath(Graphics2D g2d, int size) {
		g2d.setColor(Color.black);
		if (pathCount > 0)
			pathCount--;
		BasicStroke bs2 = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
		g2d.setStroke(bs2);
		for (int i = paths.size() - 1; i > pathCount; --i) {
			g2d.drawLine(size * paths.get(i).y + size / 2, size * paths.get(i).x + size / 2,
					size * paths.get(i - 1).y + size / 2, size * paths.get(i - 1).x + size / 2);
		}

	}

	// MOUSE GESTURE

	void invertWall(int moX, int moY) {
		int y = moX / Panel.size;
		int x = moY / Panel.size;
		if (y >= 0 && y < COLS && x >= 0 && x < ROWS)
			points[x][y].wall = 0;
	}

	void makeWall(int moX, int moY) {
		int y = moX / Panel.size;
		int x = moY / Panel.size;
		if (y >= 0 && y < COLS && x >= 0 && x < ROWS)
			points[x][y].wall = 1;
	}

	static Point[] remove(Point[] a, int i, int size) {
		for (int j = i; j < size - 1; j++) {
			a[j] = a[j + 1];
		}
		return a;
	}
	
	void makeEnd(int moX, int moY) {
		boolean change = false;
		int y = moX / Panel.size;
		int x = moY / Panel.size;
		if (points[x][y].equals(start)) return;
		for (int i = 0; i < s; i++) {
			if (end[i].equals(points[x][y])) {
				change = true;
				end = remove(end, i, s);
				s--;
			}
		}
		if (!change) {
			if (y >= 0 && y < COLS && x >= 0 && x < ROWS)
				end[s] = points[x][y];
			s++;
			
			for (int j = 0; j < s; j++)
				System.out.println(end[j]);
			System.out.println(s);
		}
	}

	void makeDensity(int moX, int moY) {
		int y = moX / Panel.size;
		int x = moY / Panel.size;
		if (y >= 0 && y < COLS && x >= 0 && x < ROWS)
			points[x][y].wall = densityValue;
	}

	// RESET BUTTON

	void resetWall() {
		int i, j;
		for (i = 0; i < ROWS; ++i)
			for (j = 0; j < COLS; ++j)
				points[i][j].wall = 0;
		stop = false;
		s = countEnd;
	}

	void resetPath() {
		int i, j;
		for (i = 0; i < ROWS; ++i)
			for (j = 0; j < COLS; ++j) {
				points[i][j].g = 0;
				points[i][j].h = 0;
				points[i][j].f = 0;
				points[i][j].from = null;
			}

		open.removeAll(open);
		close.removeAll(close);
		if (found) {
			paths.removeAll(paths);
			found = false;
		}

		startCount = 1;
		stop = false;
	}

	// ALPHA BUTTON
	void alphaSet(float x) {
		alpha = x;
		System.out.println("alpha = " + alpha);
	}

	// HEURISTIC HERE

	double heuristic(Point a, Point b[], byte algoSelect) {
		double h[];

		h = new double[100];
		if (algoSelect == 0)
			for (int i = 0; i < s; i++)
				h[i] = manhattanDistance(a, b[i]);
		else if (algoSelect == 1)
			for (int i = 0; i < s; i++)
				h[i] = euclideDistance(a, b[i]);
		else
			for (int i = 0; i < s; i++)
				h[i] = diagonDistance(a, b[i]);
		double min = h[0];
		for (int i = 1; i < s; i++)
			if (h[i] < min)
				min = h[i];
		return min;

	}

	double manhattanDistance(Point a, Point b) {
		double dx1 = a.x - b.x;
		double dy1 = a.y - b.y;
		double dx2 = start.x - b.x;
		double dy2 = start.y - b.y;
		double cross = Math.abs(dx1 * dy2 - dx2 * dy1);
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + cross * p;
	}

	double euclideDistance(Point a, Point b) {
		double dx1 = a.x - b.x;
		double dy1 = a.y - b.y;
		double dx2 = start.x - b.x;
		double dy2 = start.y - b.y;
		double cross = Math.abs(dx1 * dy2 - dx2 * dy1);
		return Math.hypot(dx1, dy1) + cross * p;
	}

	double diagonDistance(Point a, Point b) {
		double dx1 = a.x - b.x;
		double dy1 = a.y - b.y;
		double dx2 = start.x - b.x;
		double dy2 = start.y - b.y;
		double cross = Math.abs(dx1 * dy2 - dx2 * dy1);
		int dx = Math.abs(a.x - b.x);
		int dy = Math.abs(a.y - b.y);
		double h;
		if (dx >= dy)
			h = normDist * (dx - dy) + diagDist * dy;
		else
			h = normDist * (dy - dx) + diagDist * dx;
		return h + cross * p;
	}
}
