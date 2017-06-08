package com.bosssoft.install.windows_patch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.bosssoft.install.windows.patch.mate.PatchApp;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.wizard.util.XmlUtil;

public class Parsertest {

	public static void main(String[] args) {
		String patchConfig="d://test//info.xml";
		try {
			//根据补丁配置文件中的信息构建对应的对象，放到context中
			
			List<PatchApp> list=new ArrayList<PatchApp>();
			Document doc= XmlUtil.getDocument(new File(patchConfig));
			Elements prodcut = XmlUtil.findElements(doc, "product");
			Iterator<Element> it=prodcut.select("app").iterator();
			while(it.hasNext()){
				Element appLabel=it.next();
				PatchApp app=constructApp(appLabel);
				list.add(app);
			}
		} catch (IOException e) {
			throw new InstallException("faile to parser patch config "+patchConfig,e);
		}
	}

	private static PatchApp constructApp(Element appLabel) {
		PatchApp app=new PatchApp();
	    app.setAppName(appLabel.attr("name"));
	    Iterator<Element> it =appLabel.children().iterator();
	    while(it.hasNext()){
			Element r=it.next();
			System.out.println(r.tagName());
		}
		return null;
	}

}
