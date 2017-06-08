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
			Iterator<Element> it=prodcut.select("app").iterator();
			while(it.hasNext()){
				Element appLabel=it.next();
				PatchApp app=constructApp(appLabel);
				list.add(app);
			}
			context.setValue("PATCH_APPS", list);
			System.out.println("tets ");
			
		} catch (IOException e) {
			throw new InstallException("faile to parser patch config "+patchConfig,e);
		}
		
		
	}
/**
 * 构建需要更新的应用对象
 * @param appLabel
 * @return
 */
	private PatchApp constructApp(Element appLabel) {
		//解析补丁文件信息
		String appName=appLabel.attr("name");
	    PatchApp app=new PatchApp();
	    app.setAppName(appName);
	    Iterator<Element> it =appLabel.children().iterator();
	    while(it.hasNext()){
			Element r=it.next();
			String tagName=r.tagName();
			if("war".equals(tagName)){
				WarType warType=new WarType();
				warType.setName(r.attr("name"));
				warType.setSourcePath(PatchFileManager.getPathResourceDir(appName)+File.separator+r.attr("name"));
				app.addPatchFile(warType);
			}else{
				ResourceType resourceType=new ResourceType();
				resourceType.setSourcePath(PatchFileManager.getPathResourceDir(appName)+File.separator+r.attr("name"));
			    resourceType.setDestPath(r.attr("file")+File.separator+r.attr("name"));
			    app.addPatchFile(resourceType);
			}
		}
		//设置检查文件目录
	    app.setCheckFileDir(PatchFileManager.getPathCheckDir(appName));
	    return app;
	    
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}

}
