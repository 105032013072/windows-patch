package com.bosssoft.install.windows.patch;

import java.io.File;

import com.bosssoft.install.windows.patch.util.PatchFileManager;
import com.bosssoft.platform.installer.core.Constants;
import com.bosssoft.platform.installer.core.launch.LaunchException;
import com.bosssoft.platform.installer.core.launch.Launcher;



public class DemoLauncher implements Constants {


	public static void main(String[] args) {
		try {
			//设置log4j的相对路径
			String workDir=PatchFileManager.getPatchWordDir()+"logging";
			System.setProperty ("WORKDIR", workDir);
			
			Launcher launcher = new Launcher();
			launcher.run(args);
		} catch (LaunchException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}

}