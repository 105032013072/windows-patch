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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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

	private JTextArea finishLabel= new JTextArea();
	
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
		
		this.finishLabel.setOpaque(false);
		this.finishLabel.setEditable(false);
		this.finishLabel.setBounds(new Rectangle(37, 43, 375, 41));
		
		this.labelLog.setText("查看完整的操作日志");
		this.labelLog.setBounds(new Rectangle(37, 300, 375, 25));
		
		add(this.setupPane, "Center");
		this.setupPane.setOpaque(false);
		this.setupPane.add(this.line, null);
		this.setupPane.add(this.finishLabel, null);
		this.setupPane.add(this.labelLog, null);
		
		
	}



	private String getshowContent()  {
		String msg=null;
		if("true".equals(getContext().getStringValue("IS_ROLLBACK"))){
			msg=I18nUtil.getString("FINISH.UPDATE.ROLLBACK.SHOW");
		}else{
			msg=I18nUtil.getString("FINISH.UPDATE.SUCCESS.SHOW").replace("${version}", getProductVersion());
		}
		return msg;
	}

	//获取产品新版本
	private String getProductVersion() {
		String version=null;
		try {
			 String versionFile=PatchFileManager.getPatchProdcutInfoFile(getContext());
			 SAXReader reader=new SAXReader();
			 Document doc =reader.read(new File(versionFile));
			 Element root=doc.getRootElement();
			 version=root.elementText("version");
		} catch (Exception e) {
			throw new InstallException(e);
		}
	  return version;
	}

	public void afterShow() {
	}

	public void beforeNext() {
	}

	public void beforePrevious() {
	}

	
	public void beforeShow() {

		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", true);
		controlPane.setButtonVisible("help", false);
		controlPane.setButtonVisible("next", false);
		controlPane.setButtonVisible("cancel", false);
		controlPane.setButtonVisible("previous", false);
		controlPane.setDefaultButton("finish");
	
		this.finishLabel.setText(getshowContent());
		
		this.labelLog.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent arg0) {
			}
			public void mousePressed(MouseEvent arg0) {
			    String readmePath=getContext().getStringValue("INSTALL_LOGFILE_PATH");
				
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