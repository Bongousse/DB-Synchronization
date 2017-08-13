package exerd.utilizing.com.ui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;

public class FileReader extends Frame {
	private static final long serialVersionUID = 1L;

	private Label lab1;
	private TextArea ta1;

	public FileReader(String filePath) {
		lab1 = new Label("Complete path of the selected file");
		ta1 = new TextArea(40, 20);

		add(ta1, "Center");
		add(lab1, "North");

		lab1.setFont(new Font("돋움", 20, 20));
		lab1.setText("File: " + filePath);
		display(filePath);

		setTitle("Generate Query");
		setSize(700, 700);
		setVisible(true);
		// a shortcut to close the frame
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}

	@SuppressWarnings("resource")
	public void display(String fname) { // this method is for reading a file
		try {
			FileInputStream fis = new FileInputStream(fname);
			int fileSize = fis.available();
			byte buf1[] = new byte[fileSize];
			fis.read(buf1);
			String str1 = new String(buf1);
			ta1.setText(str1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
