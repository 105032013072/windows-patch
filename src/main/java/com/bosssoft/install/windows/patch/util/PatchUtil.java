package com.bosssoft.install.windows.patch.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.io.FileUtils;

public class PatchUtil {
	public static String readFile(String sourceFile){
		StringBuilder result = new StringBuilder();
        try{
            //BufferedReader br = new BufferedReader(new FileReader(new File(sourceFile)));//构造一个BufferedReader类来读取文件
        	BufferedReader br = new BufferedReader(new FileReader(new File(sourceFile)));
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
	}
	
	public static String readFile(String sourceFile,String charSet){
		StringBuilder result = new StringBuilder();
        try{
        	InputStreamReader isr=new InputStreamReader(new FileInputStream(new File(sourceFile)), charSet);
        	BufferedReader br=new BufferedReader(isr);
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
	}
	
	public static void writeToFile(String result,String filePath){
		
		try {
	           BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(filePath)));
				bw.write (result);
				bw.close();
			} catch (IOException e) {
				throw new InstallException("Failed to ConfigComponents", e);
			}
	}
	
	public static void writeToFile(String result,String filePath,String charSet){
		try {
	           BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(filePath),charSet));
				bw.write (result);
				bw.close();
			} catch (IOException e) {
				throw new InstallException("Failed to ConfigComponents", e);
			}
	}
	
	public static int showConfirmMessage(String msg, String title){
		JPanel jpanel = new JPanel();
		JTextArea textArea = new JTextArea(4, 40);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText(msg);
		textArea.setEditable(false);
		jpanel.add(new JScrollPane(textArea));
		return MainFrameController.showConfirmDialog(msg, title, JOptionPane.YES_NO_OPTION, 2);
	}
}