package com.bosssoft.install.windows.patch.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
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
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class RollbackDigestPanel extends AbstractSetupPanel {
	Logger logger = Logger.getLogger(getClass());

	private StepTitleLabel line = new StepTitleLabel();

	private JTree treeDigest = new JTree();
	private DefaultTreeModel treeModel = null;
	private JTextArea introduction = new JTextArea();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private StringBuffer rollbackApps=new StringBuffer("");
	private StringBuffer deleteApps=new StringBuffer("");
	private String backupTime=new String();

	public RollbackDigestPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);
		this.line.setText(I18nUtil.getString("STEP.DIGEST"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));

		this.introduction.setBackground(Color.white);
		this.introduction.setOpaque(false);
		this.introduction.setText(I18nUtil.getString("DIGEST.LABEL"));
		this.introduction.setEditable(false);
		this.introduction.setLineWrap(true);
		this.introduction.setRows(1);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setBackground(Color.white);
		this.introduction.setBounds(new Rectangle(37, 43, 372, 54));
		this.jScrollPane1.setBounds(new Rectangle(37, 101, 372, 256));

		this.treeDigest.setShowsRootHandles(false);
		DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
		render.setLeafIcon(null);
		render.setOpenIcon(null);
		render.setClosedIcon(null);
		render.setDisabledIcon(null);
		render.setFont(new Font("Dialog", 0, 12));
		this.treeDigest.setRowHeight(20);
		this.treeDigest.setCellRenderer(render);

		setOpaque(false);
		add(this.line, null);
		add(this.introduction, null);
		add(this.jScrollPane1, null);
		this.jScrollPane1.getViewport().add(this.treeDigest, null);
	}

	public void afterShow() {
	}

	public void beforeNext() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonText("next", I18nUtil.getString("BUTTON_NEXT"));
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);

		controlPane.setButtonText("next", I18nUtil.getString("BUTTON_INSTALL"));

		loadTree();
		expandAllNodes(this.treeDigest);
	}

	private void loadTree() {
		
		getRollBackInfo();
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(I18nUtil.getString("ROOBACK.DIGEST.LABEL.ROOT"));
		this.treeModel = new DefaultTreeModel(root);
		this.treeDigest.setModel(this.treeModel);

		String versionLabel = I18nUtil.getString("ROOBACK.DIGEST.LABEL.VERSION");
		String AppsLabel = I18nUtil.getString("ROOBACK.DIGEST.LABEL.APPS");
		String raLabel = I18nUtil.getString("ROOBACK.DIGEST.LABEL.RA");
		String delLabel = I18nUtil.getString("ROOBACK.DIGEST.LABEL.DEL");

		DefaultMutableTreeNode labelNode = null;
		labelNode = new DefaultMutableTreeNode(versionLabel);
		this.treeModel.insertNodeInto(labelNode, root, root.getChildCount());
		this.treeModel.insertNodeInto(new DefaultMutableTreeNode(backupTime+" 备份版本"), labelNode, 0);

		labelNode = new DefaultMutableTreeNode(AppsLabel);
		this.treeModel.insertNodeInto(labelNode, root, root.getChildCount());
		
		labelNode = new DefaultMutableTreeNode(raLabel);
		this.treeModel.insertNodeInto(labelNode, root, root.getChildCount());
		this.treeModel.insertNodeInto(new DefaultMutableTreeNode(rollbackApps), labelNode, 0);

		labelNode = new DefaultMutableTreeNode(delLabel);
		this.treeModel.insertNodeInto(labelNode, root, root.getChildCount());
		this.treeModel.insertNodeInto(new DefaultMutableTreeNode(deleteApps), labelNode, 0);
	}

	private void getRollBackInfo()  {
		String path=PatchFileManager.getPatchRollBackFile();
		try{
			SAXReader reader=new SAXReader();
		    Document doc=reader.read(new File(path));
		    Element root=doc.getRootElement();
		    backupTime=root.attributeValue("backupTime");
		    Element eleReplace=root.element("replace");
		    if(eleReplace!=null) getReplace(eleReplace);
		    
		    Element eleDelete=root.element("delete");
		    if(eleDelete!=null) getDelete(eleDelete);
		}catch(Exception e){
			throw new InstallException(e);
		}
		
	}

	private void getDelete(Element eleDelete) {
		Iterator<Element> it=eleDelete.elementIterator();
		while(it.hasNext()){
			Element e=it.next();
			if("app".equals(e.getQualifiedName())) deleteApps.append(e.attributeValue("appName")+",");
		}
	}

	private void getReplace(Element eleReplace) {
		Iterator<Element> it=eleReplace.elementIterator();
		while(it.hasNext()){
			Element e=it.next();
			if("app".equals(e.getQualifiedName())) rollbackApps.append(e.attributeValue("appName")+",");
		}
		
	}

	private DefaultMutableTreeNode getChildByNameKey(DefaultMutableTreeNode parent, String nameKey) {
		DefaultMutableTreeNode child = null;
		Enumeration enume = parent.children();
		while (enume.hasMoreElements()) {
			child = (DefaultMutableTreeNode) enume.nextElement();
			if (((String) child.getUserObject()).equals(nameKey))
				return child;
		}
		return child;
	}

	public void expandAllNodes(JTree tree) {
		int old = 0;
		int now = 0;
		do {
			old = tree.getRowCount();
			for (int i = 0; i < old; i++) {
				tree.expandRow(i);
			}
			now = tree.getRowCount();
		} while (now > old);
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
		if (getContext().getStringValue("INSTALLED_PROJECT").equalsIgnoreCase("false"))
			MainFrameController.showMessageDialog(I18nUtil.getString("PROJECT.WEBAPP.EXIST"), I18nUtil.getString("DIALOG.TITLE.WARNING"), 0);
	}
}