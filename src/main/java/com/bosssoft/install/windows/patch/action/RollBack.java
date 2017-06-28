package com.bosssoft.install.windows.patch.action;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class RollBack implements IAction{

	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map params) throws InstallException {
		
	   try {
	    	String path=PatchFileManager.getPatchRollBackFile();
		    SAXReader reader=new SAXReader();
		    Document doc=reader.read(new File(path));
		    Element root=doc.getRootElement();
		    
		    Element eleReplace=root.element("replace");
		    if(eleReplace!=null) doReplace(eleReplace);
		    
		    Element eleDelete=root.element("delete");
		    if(eleDelete!=null) doDelete(eleDelete);
		} catch (Exception e) {
			throw new InstallException("faild to rollback  "+e);
		}
		
	}

	private void doDelete(Element eleDelete) {
		Iterator<Element> it=eleDelete.elementIterator();
		while(it.hasNext()){
			Element e=it.next();
			try {
				FileUtils.delete(new File(e.attributeValue("path")), null, null);
				logger.info("rollback: delete file "+e.attributeValue("path"));
			} catch (OperationException e1) {
				throw new InstallException("faild to rollback  "+e);
			}
		}
		
	}

	private void doReplace(Element eleReplace) {
		Iterator<Element> it=eleReplace.elementIterator();
		while(it.hasNext()){
			Element e=it.next();
			try {
				File sourceFile=new File(e.attributeValue("source"));
				File destFile=new File(e.attributeValue("dest"));
				FileUtils.copy(sourceFile, destFile, null, null);
				logger.info("rollback: copy "+sourceFile+" to "+destFile);
			} catch (OperationException e1) {
				throw new InstallException("faild to rollback  "+e);
			}
		}
		
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}
	
}
