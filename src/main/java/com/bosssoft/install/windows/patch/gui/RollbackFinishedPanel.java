package com.bosssoft.install.windows.patch.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.runtime.InstallRuntime;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class RollbackFinishedPanel extends AbstractSetupPanel{
	Logger logger = Logger.getLogger(getClass());
	private StepTitleLabel line = new StepTitleLabel();
	private JTextArea taIntroduction1 = new JTextArea();
	private JCheckBox chkReadme;
	private JLabel lblRun = new JLabel();
	private JTextArea taRunValue = new JTextArea();
	private JTextArea taIntroduction2 = new JTextArea();
	private JLabel lblReadme;

	public RollbackFinishedPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);
		this.line.setText(I18nUtil.getString("STEP.ROLLBACKFINISH"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));

		this.taIntroduction1.setOpaque(false);
		this.taIntroduction1.setText("Loading introduction......");
		this.taIntroduction1.setRows(1);
		this.taIntroduction1.setLineWrap(true);
		this.taIntroduction1.setWrapStyleWord(true);
		this.taIntroduction1.setBounds(new Rectangle(37, 43, 375, 41));
		this.taIntroduction1.setEditable(false);
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("window") >= 0) {
			this.chkReadme = new JCheckBox();
			this.chkReadme.setText(I18nUtil.getString("FINISH.LABEL.REAME"));
			this.chkReadme.setOpaque(false);
			this.chkReadme.setBounds(new Rectangle(37, 300, 375, 25));
			if (isCluster())
				this.chkReadme.setSelected(false);
			else
				add(this.chkReadme, null);
		} else {
			this.lblReadme = new JLabel();
			this.lblReadme.setText(I18nUtil.getString("FINISH.LABEL.REAME2"));
			this.lblReadme.setOpaque(false);
			this.lblReadme.setBounds(new Rectangle(37, 300, 375, 25));
			add(this.lblReadme, null);
		}

		this.lblRun.setPreferredSize(new Dimension(50, 16));
		this.lblRun.setBounds(new Rectangle(37, 80, 370, 16));

		this.taRunValue.setLineWrap(true);
		this.taRunValue.setOpaque(false);
		this.taRunValue.setBounds(new Rectangle(37, 99, 370, 41));
		this.taRunValue.setEditable(false);
		this.taIntroduction2.setOpaque(false);
		this.taIntroduction2.setLineWrap(true);
		this.taIntroduction2.setWrapStyleWord(true);
		this.taIntroduction2.setBounds(new Rectangle(37, 137, 370, 125));
		this.taIntroduction2.setEditable(false);
		setOpaque(false);
		add(this.line, null);
		add(this.taIntroduction1, null);
		add(this.lblRun, null);
		add(this.taRunValue, null);
		add(this.taIntroduction2, null);
	}

	public void afterShow() {
	}

	public void beforeNext() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
	}

	public void beforePrevious() {
	}

	private boolean isCluster() {
		boolean r = false;
		String isCluster = InstallRuntime.INSTANCE.getContext().getStringValue("IS_CLUSTER");
		r = (isCluster != null) && (isCluster.equalsIgnoreCase("true"));
		return r;
	}

	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", true);
		controlPane.setButtonVisible("help", false);
		controlPane.setButtonVisible("next", false);
		controlPane.setButtonVisible("cancel", false);
		controlPane.setButtonVisible("previous", false);
		controlPane.setDefaultButton("finish");

		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("window") >= 0) {
			this.chkReadme.setVisible(true);
			this.taRunValue.setText(getContext().getStringValue("STARTMENU_PATH").replace('/', '\\') + getContext().getStringValue("SHORTCUT_GROUP").replace('/', '\\'));
			this.lblRun.setText(I18nUtil.getString("FINISH.LABEL.RUN"));
			this.lblRun.setVisible(true);
			this.taRunValue.setVisible(true);
		} else if (this.chkReadme != null) {
			this.chkReadme.setVisible(false);
		}

		this.taIntroduction1.setText(getFinishLabel());

		String addtionInfo = treatLicenseInfo();

		this.taIntroduction2.setText(addtionInfo);
	}

	private String getFinishLabel() {
		String introduction = I18nUtil.getString("ROLLBACK.LABEL");
		//String productName = I18nUtil.getString("PRODUCT." + getContext().getStringValue("EDITION").toUpperCase());

		return introduction;
	}

	public boolean checkInput() {
		return true;
	}

	public String getNextBranchID() {
		return "";
	}

	public void initialize(String[] parameters) {
	}

	public void afterActions() {
		if ((this.chkReadme != null) && (!this.chkReadme.isSelected())) {
			return;
		}
		String installDir = getContext().getStringValue("INSTALL_DIR");
		String readmePath = installDir;
		if (Locale.getDefault().toString().equals("zh_CN"))
			readmePath = readmePath + "/readme_cn.txt";
		else {
			readmePath = readmePath + "/readme.txt";
		}
		String[] cmds = { "notepad", readmePath };
		try {
			Runtime.getRuntime().exec(cmds, null, null);
		} catch (IOException localIOException) {
		}
	}

	private String treatLicenseInfo() {
		return null;
	}
}
