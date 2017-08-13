package exerd.utilizing.com.ui;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import exerd.utilizing.com.constants.IConstants;
import exerd.utilizing.com.domain.CompColumn;
import exerd.utilizing.com.domain.Table;
import exerd.utilizing.com.processor.ColumnComparer;
import exerd.utilizing.com.sqlwriter.ASqlWriter;
import exerd.utilizing.com.sqlwriter.SqlWriterFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class UiMain extends JFrame {
	private static final long serialVersionUID = 1L;

	TextField ddlPathTx = new TextField();
	Choice dbmsCh = new Choice();
	TextField ipTx = new TextField();
	TextField portTx = new TextField();
	TextField sidTx = new TextField();
	TextField userIdTx = new TextField();
	TextField passwordTx = new TextField();
	TextField ddlColumnTx = new TextField();
	TextField dbColumnTx = new TextField();
	JProgressBar pBar = new JProgressBar();
	JList tableList = new JList<>();
	JList columnList = new JList<>();

	Map<Table, java.util.List<CompColumn>> compTableMap;

	private final Map<String, ImageIcon> imageMap;

	public UiMain() {
		super("DB Synchronization");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});

		setLayout(null);

		Label ddlPathLb = new Label("DDL Path:");
		Label dbmsLb = new Label("DBMS:");
		Label ipLb = new Label("IP:");
		Label portLb = new Label("Port:");
		Label sidLb = new Label("SID:");
		Label userIdLb = new Label("User ID");
		Label passwordLb = new Label("Password:");
		Label tableListLb = new Label("Table List:");
		Label columnListLb = new Label("Column List:");
		Label ddlColumnLb = new Label("DDL Column:");
		Label dbColumnLb = new Label("DB Column:");

		dbmsCh.add("POSTGRESQL");
		dbmsCh.add("ORACLE");

		Button browseBtn = new Button("Browse...");
		browseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		Button compareBtn = new Button("Compare !");
		compareBtn.addActionListener(new CompareBtnActionListener());
		Button generateBtn = new Button("Generate Query !");
		generateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ASqlWriter sqlWriter = SqlWriterFactory.getSqlWriter(dbmsCh.getSelectedItem());
				String query = sqlWriter.generateSql(compTableMap);
				FileWriter fw = null;
				try {
					fw = new FileWriter("./result/query.sql");
					fw.write(query);
					fw.flush();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					try {
						fw.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		});

		pBar.setStringPainted(true);
		pBar.setMinimum(0);
		pBar.setMaximum(100);

		tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableList.addMouseListener(new TableListActionListener());
		columnList.addMouseListener(new ColumnListActionListener());

		Font f = new Font("돋움", 10, 20);
		ddlPathLb.setFont(f);
		dbmsLb.setFont(f);
		ipLb.setFont(f);
		portLb.setFont(f);
		sidLb.setFont(f);
		userIdLb.setFont(f);
		passwordLb.setFont(f);
		tableListLb.setFont(f);
		columnListLb.setFont(f);
		ddlColumnLb.setFont(f);
		dbColumnLb.setFont(f);

		ddlPathTx.setFont(f);
		dbmsCh.setFont(f);
		ipTx.setFont(f);
		portTx.setFont(f);
		sidTx.setFont(f);
		userIdTx.setFont(f);
		passwordTx.setFont(f);
		browseBtn.setFont(f);
		compareBtn.setFont(f);
		generateBtn.setFont(f);
		tableList.setFont(f);
		columnList.setFont(f);

		Font f2 = new Font("돋움", 10, 15);
		ddlColumnTx.setFont(f2);
		ddlColumnTx.setEditable(false);
		dbColumnTx.setFont(f2);
		dbColumnTx.setEditable(false);

		int xInit = 30;
		int xInterval = 300;
		int yInit = 70;
		int yInterval = 60;
		ddlPathLb.setBounds(xInit, yInit, 100, 20);
		dbmsLb.setBounds(xInit, yInit + yInterval, 100, 20);
		ipLb.setBounds(xInit, yInit + yInterval * 2, 100, 20);
		portLb.setBounds(xInit + xInterval, yInit + yInterval * 2, 100, 20);
		sidLb.setBounds(xInit + xInterval * 2, yInit + yInterval * 2, 50, 20);
		userIdLb.setBounds(xInit, yInit + yInterval * 3, 100, 20);
		passwordLb.setBounds(xInit + xInterval, yInit + yInterval * 3, 100, 20);
		tableListLb.setBounds(xInit, yInit + yInterval * 5 + 50, 150, 20);
		columnListLb.setBounds(xInit + xInterval + 70, yInit + yInterval * 5 + 50, 150, 20);
		ddlColumnLb.setBounds(xInit, yInit + yInterval * 10, 130, 20);
		dbColumnLb.setBounds(xInit, yInit + yInterval * 11 - 30, 130, 20);

		xInit += 100;
		yInit -= 4;
		ddlPathTx.setBounds(xInit, yInit, 500, 30);
		browseBtn.setBounds(xInit + xInterval * 2 - 80, yInit - 2, 130, 32);
		dbmsCh.setBounds(xInit, yInit + yInterval, 165, 30);
		ipTx.setBounds(xInit, yInit + yInterval * 2, 170, 30);
		portTx.setBounds(xInit + xInterval, yInit + yInterval * 2, 100, 30);
		sidTx.setBounds(xInit + xInterval * 2 - 50, yInit + yInterval * 2, 100, 30);
		userIdTx.setBounds(xInit, yInit + yInterval * 3, 170, 30);
		passwordTx.setBounds(xInit + xInterval, yInit + yInterval * 3, 150, 30);
		compareBtn.setBounds(xInit + xInterval - 100, yInit + yInterval * 4, 130, 32);
		pBar.setBounds(20, yInit + yInterval * 4 + 50, 750, 32);
		JScrollPane tableListScroll = new JScrollPane(tableList);
		tableListScroll.setBounds(xInit - 100, yInit + yInterval * 5 + 90, 360, 200);
		JScrollPane columnListScroll = new JScrollPane(columnList);
		columnListScroll.setBounds(xInit + xInterval - 30, yInit + yInterval * 5 + 90, 360, 200);
		ddlColumnTx.setBounds(xInit + 30, yInit + yInterval * 10, 600, 30);
		dbColumnTx.setBounds(xInit + 30, yInit + yInterval * 11 - 30, 600, 30);
		generateBtn.setBounds(xInit + xInterval - 160, yInit + yInterval * 10 + 100, 250, 32);

		tableList.setCellRenderer(new TableListRenderer());

		imageMap = createImageMap();
		columnList.setCellRenderer(new ColumnListRenderer());

		add(ddlPathLb);
		add(dbmsLb);
		add(ipLb);
		add(portLb);
		add(sidLb);
		add(userIdLb);
		add(passwordLb);
		add(tableListLb);
		add(columnListLb);
		add(tableListLb);
		add(columnListLb);
		add(ddlColumnLb);
		add(dbColumnLb);

		add(ddlPathTx);
		add(browseBtn);
		add(dbmsCh);
		add(ipTx);
		add(portTx);
		add(sidTx);
		add(userIdTx);
		add(passwordTx);
		add(compareBtn);
		add(pBar);
		getContentPane().add(tableListScroll);
		getContentPane().add(columnListScroll);
		add(ddlColumnTx);
		add(dbColumnTx);
		add(generateBtn);

		setSize(800, 850);
		setResizable(false);
		setVisible(true);

		ddlPathTx.setText("C:\\Users\\Yong\\git\\eclipse.utilizing\\exerd.utilizing\\bxm_ddl.txt");
		ipTx.setText("182.162.100.120");
		portTx.setText("10110");
		sidTx.setText("orcl");
		userIdTx.setText("kbpoc");
		passwordTx.setText("infrabxm0204");
	}

	public void updateProgressBar(int value) {
		pBar.setValue(value);
	}

	public void safeUpdateProgressBar(final int value) {
		if (SwingUtilities.isEventDispatchThread()) {
			updateProgressBar(value);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					updateProgressBar(value);
				}
			});
		}
	}

	public class ProgressUpdateWorker extends SwingWorker<Void, Integer> {

		private ColumnComparer c;

		public ProgressUpdateWorker(ColumnComparer c) {
			this.c = c;
		}

		@Override
		protected Void doInBackground() throws Exception {
			// More work was done
			int value = 0;
			while ((value = c.getCurrentProgressPercent()) < 100) {
				Thread.sleep(100); // Illustrating long-running code.
				publish(value);
			}
			publish(value);

			return null;
		}

		@Override
		protected void process(List<Integer> chunks) {
			int value = chunks.get(chunks.size() - 1);
			pBar.setValue(value);
		}
	}

	class CompareBtnActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			pBar.setValue(0);
			tableList.removeAll();

			String ddlPath = ddlPathTx.getText();
			String dbms = dbmsCh.getSelectedItem();
			String ip = ipTx.getText();
			String port = portTx.getText();
			String sid = sidTx.getText();
			String userId = userIdTx.getText();
			String password = passwordTx.getText();

			final ColumnComparer c = new ColumnComparer(ddlPath, dbms, ip, port, sid, userId, password);

			new ProgressUpdateWorker(c).execute();

			new Thread(new Runnable() {
				public void run() {
					compTableMap = c.process();
					DefaultListModel listModel = new DefaultListModel<>();

					for (Table table : compTableMap.keySet()) {
						listModel.addElement(table);
					}

					tableList.setModel(listModel);
				}
			}).start();

		}

	}

	class TableListActionListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			columnList.removeAll();

			Table table = (Table) tableList.getSelectedValue();
			java.util.List<CompColumn> compColumnList = compTableMap.get(table);

			DefaultListModel listModel = new DefaultListModel<>();
			if (compColumnList != null) {
				for (CompColumn column : compColumnList) {
					listModel.addElement(column);
				}
			}
			columnList.setModel(listModel);

		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	class ColumnListActionListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			CompColumn compColumn = (CompColumn) columnList.getSelectedValue();
			if (compColumn.getDdlColumn() != null) {
				ddlColumnTx.setText(compColumn.getDdlColumn().toString());
			} else {
				ddlColumnTx.setText("There is no column");
			}
			if (compColumn.getDbColumn() != null) {
				dbColumnTx.setText(compColumn.getDbColumn().toString());
			} else {
				dbColumnTx.setText("There is no column");
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	class GenerateSqlActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

		}
	}

	public class TableListRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 5138435182832745600L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			setBackground(Color.WHITE);
			if (isSelected) {
				setBackground(Color.LIGHT_GRAY);
			}
			Table table = (Table) value;

			if (table.isNoneExistent()) {
				setText(table.getTableName());
				setForeground(Color.RED);
			} else {
				setText(table.getTableName() + "(" + table.getDifferentColumnCount() + ")");
				if (table.getDifferentColumnCount() == 0) {
					setForeground(Color.BLACK);
				} else {
					setForeground(Color.BLUE);
				}
			}
			setFont(new Font("돋움", 10, 20));
			return this;
		}
	}

	public class ColumnListRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 5138435182732745600L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			CompColumn column = (CompColumn) value;
			setBackground(Color.WHITE);
			if (isSelected) {
				setBackground(Color.LIGHT_GRAY);
			}
			
			if (column.getDdlColumn() != null) {
				setText(column.getDdlColumn().getName());
			} else {
				setText(column.getDbColumn().getName());
			}
			setIcon(imageMap.get(column.getCompTypeCd()));
			setFont(new Font("돋움", 10, 20));
			return this;
		}
	}

	private Map<String, ImageIcon> createImageMap() {
		Map<String, ImageIcon> map = new HashMap<>();
		try {
			map.put(IConstants.COMP_TYPE_CD.EQUAL, resizeIcon(new ImageIcon("./icon/equal.png")));
			map.put(IConstants.COMP_TYPE_CD.DIFFERENT, resizeIcon(new ImageIcon("./icon/different.png")));
			map.put(IConstants.COMP_TYPE_CD.NONE_EXISTENT, resizeIcon(new ImageIcon("./icon/none_existent.png")));
			map.put(IConstants.COMP_TYPE_CD.UNNECESSARY, resizeIcon(new ImageIcon("./icon/unnecessary.png")));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return map;
	}

	private ImageIcon resizeIcon(ImageIcon originalIcon) {
		Image image = originalIcon.getImage(); // transform it
		Image newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		originalIcon = new ImageIcon(newimg);
		return originalIcon;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			pBar.setValue(progress);
		}
	}

	public static void main(String[] args) {
		new UiMain();
	}
}
