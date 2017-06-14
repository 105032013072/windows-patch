package com.bosssoft.install.windows.patch.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.bosssoft.install.windows.patch.mate.PatchApp;
import com.bosssoft.install.windows.patch.mate.ResourceType;
import com.bosssoft.install.windows.patch.mate.WarType;
import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.wizard.util.XmlUtil;

public class InitConfig implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
	  String patchConfig=PatchFileManager.getPatchConfig();
	  parserConfig(context,patchConfig);	
	}

	private void parserConfig(IContext context, String patchConfig) {
		try {
			//根据补丁配置文件中的信息构建对应的对象，放到context中
			List<PatchApp> list=new ArrayList<PatchApp>();
			Document doc= XmlUtil.getDocument(new File(patchConfig));
			Elements prodcut = XmlUtil.findElements(doc, "product");
			context.setValue("PRODUCT_NAME", prodcut.attr("name"));
			InitProducConf(context);//加载该产品的安装版本信息
			Iterator<Element> it=prodcut.select("app").iterator();
			while(it.hasNext()){
				Element appLabel=it.next();
				PatchApp app=constructApp(appLabel,context);
				list.add(app);
			}
			context.setValue("PATCH_APPS", list);
			
		} catch (IOException e) {
			throw new InstallException("faile to parser patch config "+patchConfig,e);
		}
		
		
	}
  private void InitProducConf(IContext context) {
		String filepath=context.getStringValue("BOSSSOFT_HOME")+File.separator+context.getStringValue("PRODUCT_NAME")+"_version.xml";
		File file=new File(filepath);
		try{
			Document doc= XmlUtil.getDocument(file);
			Elements prodcut = XmlUtil.findElements(doc, "product");
			String version=prodcut.select("version").text();
			String installDir=prodcut.select("installDir").text();
			String deployDir=prodcut.select("deployDir").text();
			String serverPort=prodcut.select("serverPort").text();
			context.setValue("PRODUCT_VERSION", version);
			context.setValue("PRODUCT_INSTALL_DIR", installDir);
			context.setValue("APP_DEPLOY_DIR", deployDir);
			context.setValue("SERVER_PORT", serverPort);
		}catch(Exception e){
			throw new InstallException("cannot get the information of version about already installed product",e);
		}
		
		
	}

/**
 * 构建需要更新的应用对象
 * @param appLabel
 * @param context 
 * @return
 */
	private PatchApp constructApp(Element appLabel, IContext context) {
		PatchApp app=new PatchApp();
		parserExitVersion(appLabel,context,app);//获取已安装版本信息
		
		//解析补丁文件信息
		String appName=appLabel.attr("name");
	    
	    app.setAppName(appName);
	    Iterator<Element> it =appLabel.children().iterator();
	    while(it.hasNext()){
			Element r=it.next();
			String tagName=r.tagName();
			if("war".equals(tagName)){
				WarType warType=new WarType();
				warType.setIsInstalled(app.getIsInstalled());
				warType.setName(r.attr("name"));
				warType.setSourcePath(PatchFileManager.getPathResourceDir(appName)+File.separator+r.attr("name"));
				warType.setDestPath(context.getStringValue("APP_DEPLOY_DIR")+File.separator);
				app.addPatchFile(warType);
			}else{
				ResourceType resourceType=new ResourceType();
				resourceType.setSourcePath(PatchFileManager.getPathResourceDir(appName)+File.separator+r.attr("name"));
			    resourceType.setDestPath(context.getStringValue("APP_DEPLOY_DIR")+r.attr("file")+File.separator+r.attr("name"));
			    app.addPatchFile(resourceType);
			}
		}
		//设置检查文件目录
	    app.setCheckFileDir(PatchFileManager.getPathCheckDir(appName));
	    return app;
	    
	}

	private void parserExitVersion(Element appLabel, IContext context, PatchApp app) {
	String filePath=context.getStringValue("BOSSSOFT_HOME")+File.separator+appLabel.attr("name")+File.separator+"version.xml";
	File versionFile=new File(filePath);
	if(!versionFile.exists()){//对应的版本信息文件不存在说明该应用是新增
       app.setIsInstalled(false);
       return;
 	}else{
 		app.setIsInstalled(true);
 	}
	try{
		Document doc = XmlUtil.getDocument(new File(filePath));
		Element eleApp = XmlUtil.findElements(doc, "app").get(0);
		String appVersion=eleApp.attr("version");
		Element platform=eleApp.select("platform").get(0);
		String platformName=platform.attr("name");
		String platformVersion=platform.attr("version");
		app.setExitAppInfo(appVersion, platformName, platformVersion);
	    
	}catch(Exception e){
		throw new InstallException("cannot get the information of version about already installed product",e);
	} 
	
}

	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}

}
