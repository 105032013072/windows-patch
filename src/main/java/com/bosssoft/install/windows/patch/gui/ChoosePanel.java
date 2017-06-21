package com.bosssoft.install.windows.patch.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class ChoosePanel extends AbstractSetupPanel implements ActionListener {
	private static final String LICENSE_FILE_PREFIX = "Bosssoft License_";
	private StepTitleLabel line = new StepTitleLabel();

	private BorderLayout borderLayout1 = new BorderLayout();

	private JPanel setupPane = new JPanel();

	private JTextArea txtExplain=new JTextArea();
	
	private JRadioButton rbtnUpdate = new JRadioButton();

	private JRadioButton rbtnRollBack = new JRadioButton();

	private ButtonGroup buttonGroup = new ButtonGroup();

	public ChoosePanel() {
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
		this.line.setText(I18nUtil.getString("STEP.CHOOSE"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));

		this.txtExplain.setText(I18nUtil.getString("CHOOSE.EXPLAIN"));
		this.txtExplain.setBounds(new Rectangle(37, 50, 360, 25));
		this.txtExplain.setEditable(false);
		this.txtExplain.setOpaque(false);
		
		this.rbtnUpdate.setText(I18nUtil.getString("CHOOSE.UPDATE"));
		this.rbtnUpdate.setOpaque(false);
		this.rbtnUpdate.setBounds(new Rectangle(37, 100, 372, 25));
		this.rbtnRollBack.setBounds(new Rectangle(37, 150, 372, 25));
		this.rbtnUpdate.setSelected(true);
		this.rbtnRollBack.setText(I18nUtil.getString("CHOOSE.ROLLBACK"));
		this.rbtnRollBack.setOpaque(false);
		this.buttonGroup.add(this.rbtnUpdate);
		this.buttonGroup.add(this.rbtnRollBack);
		add(this.setupPane, "Center");
		this.setupPane.setOpaque(false);
		this.setupPane.add(this.line, null);
		this.setupPane.add(this.rbtnRollBack, null);
		this.setupPane.add(this.rbtnUpdate, null);
        this.setupPane.add(this.txtExplain, null);
		
		this.rbtnUpdate.addActionListener(this);
		this.rbtnRollBack.addActionListener(this);
	}

	private String getLabelText() {
		String text = I18nUtil.getString("LICENSE.LABEL");
		return text;
	}

	public void afterShow() {
	}

	public void beforeNext() {
	}

	public void beforePrevious() {
	}

	public void beforeShow() {

		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
		controlPane.setButtonVisible("previous", false);
		if (this.rbtnUpdate.isSelected())
			getContext().setValue("IS_UPDATE", true);
		else
			getContext().setValue("IS_UPDATE", false);
		controlPane = null;

	}

	//检查是否可回滚
	public boolean checkInput() {
		if(this.rbtnRollBack.isSelected()){
			File rollBackFile=new File(PatchFileManager.getPatchRollBackFile());
			if(rollBackFile.exists()) return true;
			else{
				String msg=I18nUtil.getString("CHOOSE.ERROR");
				showError(MessageFormat.format(msg,null));
				return false;
			} 
			
		}else return true;
	}

	public String getNextBranchID() {
		return "";
	}

	public void initialize(String[] parameters) {
	}

	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == this.rbtnUpdate) {
			getContext().setValue("IS_UPDATE", true);
		} else if (source == this.rbtnRollBack) {
			getContext().setValue("IS_UPDATE", false);
		}
	}

	public void afterActions() {
	}
}