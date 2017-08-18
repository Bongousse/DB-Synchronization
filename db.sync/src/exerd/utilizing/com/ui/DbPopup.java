package exerd.utilizing.com.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import exerd.utilizing.com.constants.IConstants;

class DbPopup extends JDialog {

	private static final long serialVersionUID = 1L;

	private String dbListPath = "./config/dblist.txt";

	final TextField nameTx = new TextField();
	final JComboBox<String> dbmsCh = new JComboBox<String>();
	final TextField ipTx = new TextField();
	final TextField portTx = new TextField();
	final TextField sidTx = new TextField();
	final TextField userIdTx = new TextField();
	final TextField passwordTx = new TextField();

	JButton saveBtn = new JButton("Save");

	public DbPopup(String name, String dbms, String ip, String port, String sid, String userId, String password) {
		this();
		nameTx.setText(name);
		dbmsCh.setSelectedItem(dbms);
		dbmsCh.setName(dbms);
		ipTx.setText(ip);
		portTx.setText(port);
		sidTx.setText(sid);
		userIdTx.setText(userId);
		passwordTx.setText(password);

		nameTx.setEditable(false);
		dbmsCh.setEditable(false);
		dbmsCh.setEnabled(false);
		ipTx.setEditable(false);
		portTx.setEditable(false);
		sidTx.setEditable(false);
		userIdTx.setEditable(false);
		passwordTx.setEditable(false);

		saveBtn.setEnabled(false);
	}

	public DbPopup() {

		JPanel labelFields = new JPanel(new BorderLayout(3, 2));
		labelFields.setBorder(new TitledBorder("Database"));

		JPanel informationPanel = new JPanel(new GridLayout(0, 2, 1, 1));
		informationPanel.setBorder(new TitledBorder("Information"));

		JLabel nameLb = new JLabel("Name:");

		informationPanel.add(nameLb);
		informationPanel.add(nameTx);

		JPanel generalPanel = new JPanel(new BorderLayout());
		generalPanel.setBorder(new TitledBorder("General"));

		JPanel dbFieldPanel = new JPanel(new GridLayout(0, 2, 1, 1));

		JLabel dbmsLb = new JLabel("DBMS:");
		JLabel ipLb = new JLabel("IP:");
		JLabel portLb = new JLabel("Port:");
		JLabel sidLb = new JLabel("SID:");
		JLabel userIdLb = new JLabel("User ID:");
		JLabel passwordLb = new JLabel("Password:");

		dbmsCh.setBackground(Color.white);
		dbmsCh.addItem(IConstants.DBMS.ORACLE);
		dbmsCh.addItem(IConstants.DBMS.POSTGRESQL);

		dbFieldPanel.add(dbmsLb);
		dbFieldPanel.add(dbmsCh);
		dbFieldPanel.add(ipLb);
		dbFieldPanel.add(ipTx);
		dbFieldPanel.add(portLb);
		dbFieldPanel.add(portTx);
		dbFieldPanel.add(sidLb);
		dbFieldPanel.add(sidTx);
		dbFieldPanel.add(userIdLb);
		dbFieldPanel.add(userIdTx);
		dbFieldPanel.add(passwordLb);
		dbFieldPanel.add(passwordTx);

		JPanel buttonConstrain = new JPanel();
		buttonConstrain.add(new JButton("Ping Test"));

		generalPanel.add(dbFieldPanel);
		generalPanel.add(buttonConstrain, BorderLayout.SOUTH);

		JPanel saveCancelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		saveBtn.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				saveEvent(nameTx.getText(), (String) dbmsCh.getSelectedItem(), ipTx.getText(), portTx.getText(),
						sidTx.getText(), userIdTx.getText(), passwordTx.getText());
				String message = "Save Complete";
				JOptionPane pane = new JOptionPane(message);
				JDialog dialog = pane.createDialog(new JFrame(), "Dilaog");
				dialog.show();
				dispose();
			}
		});
		saveCancelPanel.add(saveBtn);
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		saveCancelPanel.add(cancelBtn);

		labelFields.add(informationPanel, BorderLayout.NORTH);
		labelFields.add(generalPanel, BorderLayout.CENTER);
		labelFields.add(saveCancelPanel, BorderLayout.SOUTH);

		add(labelFields);
		setTitle("DB INFO");
		setSize(400, 350);
		setResizable(false);
		pack();
		setModal(true);
	}

	private void saveEvent(String name, String dbms, String ip, String port, String sid, String userId,
			String password) {

		String dbInfo = name + "\t" + dbms + "\t" + ip + "\t" + port + "\t" + sid + "\t" + userId + "\t" + password
				+ "\n";

		FileWriter fw = null;
		try {
			fw = new FileWriter(dbListPath, true);
			fw.write(dbInfo);
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

	public static void main(String[] args) throws Exception {
		// Create the GUI on the event dispatching thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new DbPopup();
			}
		});
	}
}
