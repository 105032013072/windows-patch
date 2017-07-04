package com.bosssoft.install.windows.patch.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.ButtonGroup;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class UpdateFinishedPanel extends AbstractSetupPanel implements ActionListener {
	private StepTitleLabel line = new StepTitleLabel();

	private BorderLayout borderLayout1 = new BorderLayout();

	private JPanel setupPane = new JPanel();

	private JTextArea logLabel = new JTextArea();

	private JScrollPane jScrollPane1 = new JScrollPane();

	private JEditorPane txtLog = new JEditorPane();
	
	private JLabel labelLog=new JLabel();

	public UpdateFinishedPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(this.borderLayout1);
		setOpaque(false);

		this.setupPane.setLayout(null);
		this.line.setText(I18nUtil.getString("STEP.LOG"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));
		this.jScrollPane1.getViewport().add(this.txtLog, null);
		this.logLabel.setOpaque(false);
		this.logLabel.setEditable(false);
		this.logLabel.setBounds(new Rectangle(37, 35, 373, 33));
		this.jScrollPane1.setBounds(new Rectangle(36, 68, 410, 260));
		this.jScrollPane1.setOpaque(false);
		this.txtLog.setEditable(false);
		this.txtLog.setOpaque(false);
		
		this.labelLog.setText("查看完整的操作日志");
		this.labelLog.setBounds(new Rectangle(330, 345, 373, 33));
		
		//this.labelLog.addMouseListener(l);
		
		add(this.setupPane, "Center");
		this.setupPane.setOpaque(false);
		this.setupPane.add(this.line, null);
		this.setupPane.add(this.logLabel, null);
		this.setupPane.add(this.jScrollPane1, null);
		this.setupPane.add(this.labelLog, null);
	}

	private String getLabelText() {
		String text = I18nUtil.getString("LOG.LABEL");
		return text;
	}

	public void afterShow() {
	}

	public void beforeNext() {
	}

	public void beforePrevious() {
	}

	
	public void beforeShow() {
		this.logLabel.setText(getLabelText());

		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", true);
		controlPane.setButtonVisible("help", false);
		controlPane.setButtonVisible("next", false);
		controlPane.setButtonVisible("cancel", false);
		controlPane.setButtonVisible("previous", false);
		controlPane.setDefaultButton("finish");
		
		this.txtLog.setText(getContext().getStringValue("PATCH_LOG"));
		/*String logPath=System.getProperty("WORKDIR")+File.separator+"logs"+File.separator+"app.log";
		try {
			this.txtLog.setPage(new File(logPath).toURL());
		} catch (Exception e) {
			throw new InstallException("load the log File error "+e );
		} */
		
		this.labelLog.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent arg0) {
			}
			public void mousePressed(MouseEvent arg0) {
			    String readmePath=System.getProperty("WORKDIR")+File.separator+"logs"+File.separator+"app.log";
				
			    String[] cmds = { "notepad", new File(readmePath).getPath() };
				try {
					Runtime.getRuntime().exec(cmds, null, null);
				} catch (IOException localIOException) {
				}
			}
			
			public void mouseExited(MouseEvent arg0) {
				labelLog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
			public void mouseEntered(MouseEvent arg0) {
				labelLog.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				
			}
			
			public void mouseClicked(MouseEvent arg0) {
				
			}
		});
	}

	public boolean checkInput() {
		return true;
	}

	public String getNextBranchID() {
		return "";
	}

	public void initialize(String[] parameters) {
	}

	public void actionPerformed(ActionEvent ae) {
	}

	public void afterActions() {
	}
}