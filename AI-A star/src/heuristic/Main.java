package heuristic;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Main extends JFrame {
	Panel panel;

	public Main() {
		initUI();
	}

	private void initUI() {
		panel = new Panel();
		add(panel);

		setTitle("Tìm đường đi ngắn nhất trong mê cung");
		setSize(panel.frameW + 150, panel.frameH + 22);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
