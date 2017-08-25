package exerd.utilizing.com.ui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
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
import java.io.FileInputStream;
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
import javax.swing.SwingWorker;

import org.apache.log4j.xml.DOMConfigurator;

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
	JList dbList = new JList<>();
	String dbInfo;
	TextField ddlColumnTx = new TextField();
	TextField dbColumnTx = new TextField();
	JProgressBar pBar = new JProgressBar();
	JList tableList = new JList<>();
	JList columnList = new JList<>();

	String generateQueryPath = "./result/sync.sql";

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
		Label dbListLb = new Label("DB List:");
		Label tableListLb = new Label("Table List:");
		Label columnListLb = new Label("Column List:");
		Label ddlColumnLb = new Label("DDL Column:");
		Label dbColumnLb = new Label("DB Column:");

		Button browseBtn = new Button("Browse...");
		browseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDialog fileBrowseDialog = new FileDialog(UiMain.this, "Select DDL File");
				fileBrowseDialog.setVisible(true);
				ddlPathTx.setText(fileBrowseDialog.getDirectory() + fileBrowseDialog.getFile());
			}
		});
		Button compareBtn = new Button("Compare !");
		compareBtn.addActionListener(new CompareBtnActionListener());
		Button generateBtn = new Button("Generate Query !");
		generateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String dbms = dbInfo.split("\t")[1];
				ASqlWriter sqlWriter = SqlWriterFactory.getSqlWriter(dbms);
				String query = sqlWriter.generateSql(compTableMap);
				FileWriter fw = null;
				try {
					fw = new FileWriter(generateQueryPath);
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

				new FileReader(generateQueryPath);
			}
		});

		Button newBtn = new Button("New");
		newBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DbPopup dbPopup = new DbPopup();
				dbPopup.setVisible(true);
				getDbList();

			}
		});
		Button detailBtn = new Button("Detail");
		detailBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String dbInfoTmp = dbInfo + " ";
				String name = dbInfoTmp.split("\t")[0];
				String dbms = dbInfoTmp.split("\t")[1];
				String ip = dbInfoTmp.split("\t")[2];
				String port = dbInfoTmp.split("\t")[3];
				String sid = dbInfoTmp.split("\t")[4];
				String userId = dbInfoTmp.split("\t")[5];
				String password = dbInfoTmp.split("\t")[6];

				DbPopup dbPopup = new DbPopup(name, dbms, ip, port, sid, userId, password);
				dbPopup.setVisible(true);
			}
		});
		Button deleteBtn = new Button("Delete");
		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteDbInfo(dbInfo);
				getDbList();
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
		dbListLb.setFont(f);
		tableListLb.setFont(f);
		columnListLb.setFont(f);
		ddlColumnLb.setFont(f);
		dbColumnLb.setFont(f);

		ddlPathTx.setFont(f);
		newBtn.setFont(f);
		detailBtn.setFont(f);
		deleteBtn.setFont(f);
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
		dbListLb.setBounds(xInit, yInit + yInterval - 20, 100, 20);
		tableListLb.setBounds(xInit, yInit + yInterval * 5 + 50, 150, 20);
		columnListLb.setBounds(xInit + xInterval + 70, yInit + yInterval * 5 + 50, 150, 20);
		ddlColumnLb.setBounds(xInit, yInit + yInterval * 10, 130, 20);
		dbColumnLb.setBounds(xInit, yInit + yInterval * 11 - 30, 130, 20);

		xInit += 100;
		yInit -= 4;
		ddlPathTx.setBounds(xInit, yInit, 500, 30);
		browseBtn.setBounds(xInit + xInterval * 2 - 80, yInit - 2, 130, 32);
		JScrollPane dbListScroll = new JScrollPane(dbList);
		dbListScroll.setBounds(xInit - 100, yInit + yInterval + 20, 600, 155);
		newBtn.setBounds(xInit + xInterval * 2 - 80, yInit + yInterval + 18, 130, 32);
		detailBtn.setBounds(xInit + xInterval * 2 - 80, yInit + yInterval + 78, 130, 32);
		deleteBtn.setBounds(xInit + xInterval * 2 - 80, yInit + yInterval + 138, 130, 32);

		compareBtn.setBounds(xInit + xInterval - 100, yInit + yInterval * 4, 130, 32);
		pBar.setBounds(20, yInit + yInterval * 4 + 50, 750, 32);
		JScrollPane tableListScroll = new JScrollPane(tableList);
		tableListScroll.setBounds(xInit - 100, yInit + yInterval * 5 + 90, 360, 200);
		JScrollPane columnListScroll = new JScrollPane(columnList);
		columnListScroll.setBounds(xInit + xInterval - 30, yInit + yInterval * 5 + 90, 360, 200);
		ddlColumnTx.setBounds(xInit + 30, yInit + yInterval * 10, 600, 30);
		dbColumnTx.setBounds(xInit + 30, yInit + yInterval * 11 - 30, 600, 30);
		generateBtn.setBounds(xInit + xInterval - 160, yInit + yInterval * 10 + 100, 250, 32);

		dbList.setCellRenderer(new DbListRenderer());

		tableList.setCellRenderer(new TableListRenderer());

		imageMap = createImageMap();
		columnList.setCellRenderer(new ColumnListRenderer());

		add(ddlPathLb);
		add(dbListLb);
		add(tableListLb);
		add(columnListLb);
		add(tableListLb);
		add(columnListLb);
		add(ddlColumnLb);
		add(dbColumnLb);

		add(ddlPathTx);
		add(browseBtn);
		getContentPane().add(dbListScroll);
		add(newBtn);
		add(detailBtn);
		add(deleteBtn);
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

		ddlPathTx.setText("C:\\Users\\Yong\\git\\DB-Synchronization\\db.sync\\ddl\\bxm_ddl.txt");

		getDbList();
	}

	private void deleteDbInfo(String dbInfo) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("./config/dbList.txt");
			int fileSize = fis.available();
			byte buf[] = new byte[fileSize];
			fis.read(buf);
			String str = new String(buf);
			str = str.replaceAll(dbInfo + "\n", "");

			FileWriter fw = null;
			try {
				fw = new FileWriter("./config/dbList.txt", false);
				fw.write(str);
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
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private void getDbList() {
		DefaultListModel listModel = new DefaultListModel<>();

		FileInputStream fis;
		try {
			fis = new FileInputStream("./config/dbList.txt");
			int fileSize = fis.available();
			byte buf[] = new byte[fileSize];
			fis.read(buf);
			String str = new String(buf);
			String[] dbStrList = str.split("\n");

			for (String dbInfo : dbStrList) {
				listModel.addElement(dbInfo);
			}

			dbList.setModel(listModel);

		} catch (IOException e) {
			e.printStackTrace();
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

			String dbInfoTmp = dbInfo + " ";
			String dbms = dbInfoTmp.split("\t")[1].trim();
			String ip = dbInfoTmp.split("\t")[2].trim();
			String port = dbInfoTmp.split("\t")[3].trim();
			String sid = dbInfoTmp.split("\t")[4].trim();
			String userId = dbInfoTmp.split("\t")[5].trim();
			String password = dbInfoTmp.split("\t")[6].trim();

			final ColumnComparer c = new ColumnComparer(ddlPath, dbms, ip, port, sid, userId, password);

			new ProgressUpdateWorker(c).execute();

			new Thread(new Runnable() {
				public void run() {
					try {
						compTableMap = c.process();

						DefaultListModel listModel = new DefaultListModel<>();

						for (Table table : compTableMap.keySet()) {
							listModel.addElement(table);
						}

						tableList.setModel(listModel);
					} catch (Exception e) {
						e.printStackTrace();
					}
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

	private class DbListRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 5238435182832745600L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			setBackground(Color.WHITE);
			if (isSelected) {
				setBackground(Color.LIGHT_GRAY);
			}
			dbInfo = (String) value;

			String name = dbInfo.split("\t")[0];

			setText(name);
			setFont(new Font("돋움", 10, 20));
			return this;
		}
	}

	private class TableListRenderer extends DefaultListCellRenderer {

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

	private class ColumnListRenderer extends DefaultListCellRenderer {

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
		String log4jXml = "./config/log4j.xml";
		DOMConfigurator.configureAndWatch(log4jXml);
		new UiMain();
	}
}
