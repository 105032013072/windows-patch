package com.bosssoft.install.windows.patch.gui;

import java.io.File;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.wizard.gui.BossHomeChoosePanel;

public class PatchBossHomePanel extends BossHomeChoosePanel{
	
	public void beforeNext() {
		super.beforeNext();
		
		Logger logger = Logger.getLogger(getClass());
		logger.info("set Bosssoft_Home: "+getContext().getStringValue("BOSSSOFT_HOME"));
	}
}
