package com.bosssoft.install.windows.patch.action;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.install.windows.patch.mate.PatchApp;
import com.bosssoft.install.windows.patch.mate.ResourceType;
import com.bosssoft.install.windows.patch.mate.WarType;
import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.thoughtworks.xstream.io.xml.SaxWriter;

public class LoadConfig implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		String patchConfig=PatchFileManager.getPatchConfig();
	    parserConfig(context,patchConfig);	
	}

	private void parserConfig(IContext context, String patchConfig)  {
		try {
			//根据补丁配置文件中的信息构建对应的对象，放到context中
			List<PatchApp> list=new ArrayList<PatchApp>();
			SAXReader reader=new SAXReader();
			Document doc=reader.read(new File(patchConfig));
			Element prodcut = doc.getRootElement();
			context.setValue("PRODUCT_NAME", prodcut.attributeValue("name"));
			Map<String,String> appInfo=InitProducConf(context);//加载该产品的安装版本信息
			Iterator<Element> it=prodcut.elementIterator("app");
			while(it.hasNext()){
				Element appLabel=it.next();
				PatchApp app=constructApp(appLabel,context,appInfo);
				list.add(app);
			}
			context.setValue("PATCH_APPS", list);
			
		} catch (Exception e) {
			throw new InstallException("faile to parser patch config "+patchConfig,e);
		}
		
		
	}
  private Map<String,String> InitProducConf(IContext context) {
		String filepath=PatchFileManager.getPatchProdcutVersionFile(context);
		File file=new File(filepath);
		Map<String,String> appInfo=new HashMap<String, String>();
		try{
			SAXReader reader=new SAXReader();
			Document doc= reader.read(file);
			Element prodcut = doc.getRootElement();
			String version=prodcut.elementText("version");
			String installDir=prodcut.elementText("installDir");
			context.setValue("IP", getIP());
			
			//加载每个应用的部署路径和端口号
			Element applications=prodcut.element("applications");
			Iterator<Element> it=applications.elementIterator("application");
			
			while(it.hasNext()){
				Element application=it.next();
				String info=application.elementText("deployDir")+","+application.elementText("serverPort");
				appInfo.put(application.elementText("appName"), info);
			}
			
		}catch(Exception e){
			String message="cannot get the information of version about already installed product,please make sure the external path is correct ";
			throw new InstallException(message);
		}
		return appInfo;
		
	}

      private String getIP() {
          String ipaddress=null;
  		try {
  			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
  	
  			while(netInterfaces.hasMoreElements()){
  				NetworkInterface ni = netInterfaces.nextElement();
  				Enumeration<InetAddress> netAddresses = ni.getInetAddresses();
  				while(netAddresses.hasMoreElements()){
  					InetAddress ip =  netAddresses.nextElement();
  					if ((ip != null) && (!ip.isLoopbackAddress()) && (ip.getHostAddress().indexOf(":") == -1)) {
  						String address = ip.getHostAddress();
  						ipaddress=address;
  					}
  				}
  			}
  		} catch (Exception localException) {
  		 throw new InstallException("faild to get IP because "+localException);
  		}
  		if(ipaddress==null)ipaddress="127.0.0.1";
  		return ipaddress;
     }

/**
 * 构建需要更新的应用对象
 * @param appLabel
 * @param context 
 * @param appInfo 
 * @return
 */
	private PatchApp constructApp(Element appLabel, IContext context, Map<String, String> appInfo) {
		PatchApp app=new PatchApp();
		parserExitVersion(appLabel,context,app);//获取已安装版本信息
		
		//解析补丁文件信息
		String appName=appLabel.attributeValue("name");
	    app.setAppName(appName);
	    
	    String info=appInfo.get(appName);
	    if(info==null){//新增的应用无部署信息
	    	
	    }else{
	    	String []ai=info.split(",");
	  	    app.setServerDeployDir(ai[0]);
	  	    app.setServerPort(ai[1]);
	    }
	    
	    Iterator<Element> it =appLabel.elementIterator();
	    while(it.hasNext()){
			Element r=it.next();
			String tagName=r.getQualifiedName();
			if("war".equals(tagName)){
				WarType warType=new WarType();
				warType.setIsInstalled(app.getIsInstalled());
				warType.setName(r.attributeValue("name"));
				warType.setSourcePath(PatchFileManager.getPathResourceDir(appName)+File.separator+r.attributeValue("name"));
				warType.setDestPath(context.getStringValue("APP_DEPLOY_DIR")+File.separator);
				warType.setAppName(appName);
				app.addPatchFile(warType);
			}else{
				ResourceType resourceType=new ResourceType();
				resourceType.setSourcePath(PatchFileManager.getPathResourceDir(appName)+File.separator+r.attributeValue("name"));
			    resourceType.setDestPath(context.getStringValue("APP_DEPLOY_DIR")+r.attributeValue("file")+File.separator+r.attributeValue("name"));
			   if(new File(resourceType.getDestPath()).exists()) resourceType.setIsInstalled(true);
			   else resourceType.setIsInstalled(false);
			    resourceType.setAppName(appName);
			    app.addPatchFile(resourceType);
			}
		}
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
