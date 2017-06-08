package com.bosssoft.install.windows_patch;

import java.net.URL;

public class GetTest {
  public static void main(String[] args){
	  URL url=Thread.currentThread().getContextClassLoader().getResource("install");
	  System.out.println(url.toString());
  }
}
