package com.bosssoft.install.windows_patch;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.install.windows.patch.mate.Depend;
import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.install.windows.patch.util.PatchUtil;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class GetTest {
	
  public static void main(String[] args){
	      String file="d://log.txt";
	      String result="this is my love";
	     PatchUtil.wirteAppendFile(result, file);
	 
  }
}
