package heuristic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

public class Panel extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Luoi here;
	private Timer clock;

	public int frameW = 10;
	public int frameH = 10;
	public static final int size = 20;

	// MOUSE

	int moX, moY;
	private boolean mPress = false;
	private boolean mDensity = false;
	private boolean mStart = false;
	private static boolean mEnd[];

	// JBUTTON

	private boolean searchFlag = false;
	private JButton btn_search;
	private JButton btn_resetPath;
	private JButton btn_resetWall;

	// JBOX

	private ButtonGroup bg_algo;

	private JRadioButton rb_manhattan;
	private JRadioButton rb_euclide;
	private JRadioButton rb_ultimate;
	private JComboBox<Float> alphaComboBox;

	private void initPane() {
		setLayout(null);
		
		// ALPHA

				alphaComboBox = new JComboBox<Float>();
				alphaComboBox.setModel(new DefaultComboBoxModel<Float>(new Float[] { (float) 0, (float) 0.5, (float) 1 }));
				alphaComboBox.setSelectedIndex(1);
				alphaComboBox.setBounds(frameW + 20, 27, 107, 22);
				add(alphaComboBox);
				alphaComboBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Luoi.alpha = (float) alphaComboBox.getSelectedItem();
					}
				});
				
		// JBUTTON

		btn_search = new JButton("Search");
		btn_resetPath = new JButton("reset Path");
		btn_resetWall = new JButton("reset All");

		btn_search.setBounds(frameW + 17, frameH - 141, 100, 40);
		btn_resetPath.setBounds(frameW + 17, frameH - 91, 100, 30);
		btn_resetWall.setBounds(frameW + 17, frameH - 50, 100, 30);

		btn_search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (searchFlag == false) {
					searchFlag = true;
					here.delay1 = System.currentTimeMillis();
				}
			}
		});
		btn_resetWall.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				here.resetPath();
				here.resetWall();
				searchFlag = false;

			}
		});
		btn_resetPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (here.stop) {
					here.resetPath();
					searchFlag = false;
				}
			}
		});

		add(btn_search);
		add(btn_resetPath);
		add(btn_resetWall);

		// JBOX

		rb_manhattan = new JRadioButton("Manhattan");
		rb_euclide = new JRadioButton("Euclide");
		rb_ultimate = new JRadioButton("Diagonal", true);

		rb_manhattan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Luoi.algoSelect = 0;
			}
		});
		rb_euclide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Luoi.algoSelect = 1;
			}
		});
		rb_ultimate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Luoi.algoSelect = 2;
			}
		});

		rb_manhattan.setBounds(frameW + 17, frameH - 206, 100, 16);
		rb_euclide.setBounds(frameW + 17, frameH - 188, 100, 16);
		rb_ultimate.setBounds(frameW + 17, frameH - 170, 100, 16);

		bg_algo = new ButtonGroup();
		bg_algo.add(rb_manhattan);
		bg_algo.add(rb_euclide);
		bg_algo.add(rb_ultimate);

		add(rb_manhattan);
		add(rb_euclide);
		add(rb_ultimate);

		// MOUSE

		addMouseListener(new MAdapter());
		addMouseMotionListener(new MAdapter());
	}

	private void initPanel() {
		here = new Luoi();
		frameH = here.ROWS * size;
		frameW = here.COLS * size;
		Color bg = new Color(255, 236, 139);
		setBackground(bg);
		mEnd = new boolean[100];
		for (int i = 0; i < 100; i++)
			mEnd[i] = false;
	}

	private void initTime() {
		clock = new Timer(1, this);
		clock.start();
	}

	public Panel() {
		initPanel();
		initPane();
		initTime();
	}

	void doDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		here.draw(g2d, searchFlag);

		// DRAW ALPHA
		g2d.setColor(Color.black);

		String func = "F = (1-a) * G + a * H";
		g2d.drawString(func, frameW + 5, 15);

		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(1);
		df.setMaximumFractionDigits(5);

		String alphaConf = "a:";
		g2d.drawString(alphaConf, frameW + 5, 42);

		// DRAW RESULT
		g2d.setColor(Color.white);
		g2d.fillRect(frameW + 9, 55, 120, 47);

		g2d.setColor(new Color(0x009966));

		if (here.stop) {
			if (!here.found) {
				g2d.drawString("KHÔNG CÓ ĐƯỜNG!", frameW + 10, 83);
			} else {
				df.setMinimumFractionDigits(2);
				df.setMaximumFractionDigits(3);

				String st = "time (ms): " + (here.delay2 - here.delay1);
				g2d.drawString(st, frameW + 17, 82);

				for (int i = 0; i < Luoi.s; i++) {
					if (here.end[i].g > 0) {
						String pathLength = "cost (dv): " + df.format((here.end[i].g));
						g2d.drawString(pathLength, frameW + 17, 96);
					}
				}

				String oSize = "close:" + here.close.size();
				g2d.drawString(oSize, frameW + 17, 69);
			}
		}
		// END DRAWING
		g2d.dispose();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int j = 0;
		if (!searchFlag) {
			if (mStart) {
				int x = moY / size;
				int y = moX / size;
				here.start = here.points[x][y];
				here.start.wall = 0;
			} else
				for (int i = 0; i < Luoi.s; i++) {
					if (mEnd[i]) {
						int x = moY / size;
						int y = moX / size;
						here.end[i] = here.points[x][y];
						here.end[i].wall = 0;
						j++;
					}
				}
			if (j == 0)
				if (mPress) {
					here.makeWall(moX, moY);
				} else if (mDensity) {
					here.makeDensity(moX, moY);
				}
		}
		repaint();
	}

	class MAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			moX = e.getX();
			moY = e.getY();
			int j = 0;
			if (inner(here.start))
				mStart = true;
			else {
				for (int i = 0; i < Luoi.s; i++) {
					if (inner(here.end[i]))
						mEnd[i] = true;
					j++;
				}
			}
			if (j > 0)
				if (moX >= 0 && moX < frameW && moY >= 0 && moY < frameH)
					if (e.getButton() == MouseEvent.BUTTON1)
						mPress = true;
					else if (e.getButton() == MouseEvent.BUTTON3)
						mDensity = true;
					else if (e.getButton() == MouseEvent.BUTTON1)
						mPress = false;
					else if (e.getButton() == MouseEvent.BUTTON3)
						mDensity = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			moX = e.getX();
			moY = e.getY();
			if (moX < 0 || moX >= frameW || moY < 0 || moY >= frameH) {
				mPress = false;
				mDensity = false;
				mStart = false;
				for (int i = 0; i < Luoi.s; i++)
					mEnd[i] = false;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mPress = false;
			mDensity = false;
			mStart = false;
			for (int i = 0; i < Luoi.s; i++)
				mEnd[i] = false;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			moX = e.getX();
			moY = e.getY();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			moX = e.getX();
			moY = e.getY();
			if (e.getClickCount() == 2) {
				here.makeEnd(moX, moY);
				here.invertWall(moX, moY);
			}
			if (e.getClickCount() == 1) {
				here.invertWall(moX, moY);
			}
		}

		boolean inner(Point z) {
			return (moX >= z.y * size) && (moX <= (z.y + 1) * size) && (moY >= z.x * size) && (moY <= (z.x + 1) * size);
		}
	}
}
