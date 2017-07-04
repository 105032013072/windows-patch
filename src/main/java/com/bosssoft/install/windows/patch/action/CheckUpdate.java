package com.bosssoft.install.windows.patch.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.runtime.directive.Foreach;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.install.windows.patch.mate.Depend;
import com.bosssoft.install.windows.patch.mate.PatchApp;
import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.I18nUtil;

public class CheckUpdate implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		List<Depend> depnetList=getdependList();
		for (Depend depend : depnetList) {
			String type=depend.getType();
			if("product".equals(type)) productCheck(depend,context);
			else if("app".equals(type)){
				List<PatchApp> patchApps=(List<PatchApp>) context.getValue("PATCH_APPS");
			    appCheck(depend,patchApps);
			}
		}
        logger.info("Check Update:pass");
	}

	private void appCheck(Depend depend, List<PatchApp> patchApps) {
		for (PatchApp patchApp : patchApps) {
			if(patchApp.getAppName().equals(depend.getName())){
				if(appVersionMatch(patchApp,depend)){
					platformCheck(patchApp,depend);
				}else{
					String msg=I18nUtil.getString("CHECK.FAILD.APP.VERSION").replace("${appName}", depend.getName());
					MainFrameController.showConfirmDialog(msg, I18nUtil.getString("DIALOG.TITLE.INFO"), JOptionPane.DEFAULT_OPTION, 2);
					logger.debug("Check Update: "+msg);
					System.exit(0);
				}
				return;
			}
		}
		
		logger.debug("Check Update: 没有找到对应的应用名");
	}

	//平台检查
	private void platformCheck(PatchApp patchApp, Depend depend) {
		if(depend.getPlatformName()!=null){
			if(patchApp.getPlatformName().equalsIgnoreCase(depend.getPlatformName())){
				if(!ArrayUtils.contains(depend.getPlatformVersions(),patchApp.getPlatformVersion())){//平台版本信息不匹配
					String msg=I18nUtil.getString("CHECK.FAILD.PLATFORM.VERSION").replace("${appName}", depend.getName());
					MainFrameController.showConfirmDialog(msg, I18nUtil.getString("DIALOG.TITLE.INFO"), JOptionPane.DEFAULT_OPTION, 2);
					logger.info("Check Update: "+msg);
					System.exit(0);
				}
			}else{//平台名称不匹配
				String msg=I18nUtil.getString("CHECK.FAILD.PLATFORM.NAME").replace("${appName}", depend.getName());
				MainFrameController.showConfirmDialog(msg, I18nUtil.getString("DIALOG.TITLE.INFO"), JOptionPane.DEFAULT_OPTION, 2);
				logger.info("Check Update: "+msg);
				System.exit(0);
			}
		}
		
	}


	private boolean appVersionMatch(PatchApp patchApp, Depend depend) {
		if(depend.getVersions()!=null){
			return ArrayUtils.contains(depend.getVersions(),patchApp.getAppversion());
		}
		return true;
	}

	private void productCheck(Depend depend, IContext context) {
		String name=depend.getName();
		if(!name.equalsIgnoreCase(context.getStringValue("PRODUCT_NAME"))){
			MainFrameController.showConfirmDialog(I18nUtil.getString("CHECK.FAILD.PRODUCT.NAME"), I18nUtil.getString("DIALOG.TITLE.INFO"), JOptionPane.DEFAULT_OPTION, 2);
			logger.info("Check Update: "+I18nUtil.getString("CHECK.FAILD.PRODUCT.NAME"));
			System.exit(0);
		}
		if(!ArrayUtils.contains(depend.getVersions(), context.getStringValue("PRODUCT_VERSION"))){
			MainFrameController.showConfirmDialog(I18nUtil.getString("CHECK.FAILD.PRODUCT.VERSION"), I18nUtil.getString("DIALOG.TITLE.INFO"), JOptionPane.DEFAULT_OPTION, 2);
			logger.info("Check Update: "+I18nUtil.getString("CHECK.FAILD.PRODUCT.VERSION"));
			System.exit(0);
		}
	}

	//加载depend.xml
	private List<Depend> getdependList() {
		List<Depend> list=new ArrayList<Depend>();
		try {
			SAXReader reader=new SAXReader();
			Document doc=reader.read(PatchFileManager.getPatchCheckFile());
			Element root=doc.getRootElement();
		Iterator<Element> it=root.elementIterator("dependency");
		while(it.hasNext()){
			Element dependency=it.next();
			Depend depend=new Depend();
			depend.setType(dependency.elementText("type"));
			depend.setName(dependency.elementText("name"));
			String[] versions=dependency.elementText("version")==null?
			         null:dependency.elementText("version").split(",");
			depend.setVersions(versions);

			Element platform=dependency.element("platform");
			if(platform!=null){
				depend.setPlatformName(platform.elementText("name"));
				depend.setPlatformVersions(platform.elementText("version").split(","));
			}
			list.add(depend);
		}
		} catch (Exception e) {
			throw new InstallException("faild to load dependency file "+PatchFileManager.getPatchCheckFile()+" because "+e);
		}
		return list;
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
