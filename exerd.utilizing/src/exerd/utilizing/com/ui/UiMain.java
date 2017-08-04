package exerd.utilizing.com.ui;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import exerd.utilizing.com.domain.Column;
import exerd.utilizing.com.processor.ColumnComparer;

public class UiMain extends Frame {
	private static final long serialVersionUID = 1L;

	TextField ddlPathTx = new TextField();
	Choice dbmsCh = new Choice();
	TextField ipTx = new TextField();
	TextField portTx = new TextField();
	TextField sidTx = new TextField();
	TextField userIdTx = new TextField();
	TextField passwordTx = new TextField();
	List tableList = new List();
	List columnList = new List();

	Map<String, java.util.List<Column>> compTableMap;

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
				// TODO
			}
		});

		tableList.addActionListener(new TableListActionListener());

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
		tableListLb.setBounds(xInit, yInit + yInterval * 5, 150, 20);
		columnListLb.setBounds(xInit + xInterval + 70, yInit + yInterval * 5, 150, 20);

		xInit += 100;
		yInit -= 4;
		ddlPathTx.setBounds(xInit, yInit, 500, 30);
		browseBtn.setBounds(xInit + xInterval * 2 - 80, yInit - 2, 130, 32);
		dbmsCh.setBounds(xInit, yInit + yInterval, 165, 30);
		ipTx.setBounds(xInit, yInit + yInterval * 2, 150, 30);
		portTx.setBounds(xInit + xInterval, yInit + yInterval * 2, 100, 30);
		sidTx.setBounds(xInit + xInterval * 2 - 50, yInit + yInterval * 2, 100, 30);
		userIdTx.setBounds(xInit, yInit + yInterval * 3, 150, 30);
		passwordTx.setBounds(xInit + xInterval, yInit + yInterval * 3, 150, 30);
		compareBtn.setBounds(xInit + xInterval - 100, yInit + yInterval * 4, 130, 32);
		tableList.setBounds(xInit - 100, yInit + yInterval * 5 + 50, 360, 200);
		columnList.setBounds(xInit + xInterval - 30, yInit + yInterval * 5 + 50, 360, 200);
		generateBtn.setBounds(xInit + xInterval - 160, yInit + yInterval * 10, 250, 32);

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

		add(ddlPathTx);
		add(browseBtn);
		add(dbmsCh);
		add(ipTx);
		add(portTx);
		add(sidTx);
		add(userIdTx);
		add(passwordTx);
		add(compareBtn);
		add(tableList);
		add(columnList);
		add(generateBtn);

		setSize(800, 750);
		setResizable(false);
		setVisible(true);
	}

	class CompareBtnActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			tableList.removeAll();

			String ddlPath = ddlPathTx.getText();
			String dbms = dbmsCh.getSelectedItem();
			String ip = ipTx.getText();
			String port = portTx.getText();
			String sid = sidTx.getText();
			String userId = userIdTx.getText();
			String password = passwordTx.getText();

			ColumnComparer c = new ColumnComparer(ddlPath, dbms, ip, port, sid, userId, password);
			compTableMap = c.process();

			for (String tableName : compTableMap.keySet()) {
				tableList.add(tableName);
			}
		}
	}

	class TableListActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			columnList.removeAll();

			String tableName = e.getActionCommand();
			java.util.List<Column> compColumnList = compTableMap.get(tableName);
			for (Column column : compColumnList) {
				columnList.add(column.getName());
			}
		}
	}

	public static void main(String[] args) {
		new UiMain();
	}
}
