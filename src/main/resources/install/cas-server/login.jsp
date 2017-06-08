<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page import="com.bosssoft.platform.cas.server.configuration.CasConfig"%>
<% 
response.setHeader("Pragma","No-cache");    
response.setHeader("Cache-Control","no-cache");    
response.setDateHeader("Expires", -10);   
%>

<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<title>登录</title>
<meta
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"
	name="viewport" />
<script type="text/javascript" src="<%=request.getContextPath() %>/frame/js/jquery1.42.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/frame/js/Validform_v5.3.2.js"></script>

<!-- CSS -->
<script type="text/javascript" src="<%=request.getContextPath() %>/frame/js/MD5.js"></script>

<link rel="stylesheet"
	href="<%=request.getContextPath() %>/frame/themes/custom/css/login.css">

<%

boolean  isNeedVerity=CasConfig.isEnableVerifycode();

String uiLoginType=CasConfig.getCasUILoginType();

request.setAttribute("isNeedVerity", isNeedVerity);
request.setAttribute("uiLoginType", uiLoginType);
%>

<script>
$(function(){

$(".i-text").focus(function(){
$(this).addClass('h-light');
});

$(".i-text").focusout(function(){
$(this).removeClass('h-light');
});

$("#username").focus(function(){
 var username = $(this).val();
 if(username=='输入账号'){
 $(this).val('');
 }
});

$("#username").focusout(function(){
 var username = $(this).val();

});


$("#password").focus(function(){
 var username = $(this).val();
 if(username=='输入密码'){
 $(this).val('');
 }
});


$("#validateCode").focus(function(){
 var username = $(this).val();
 if(username=='输入验证码'){
 $(this).val('');
 }
});

$("#validateCode").focusout(function(){
 var username = $(this).val();
 if(username==''){

 }
});



$("#loginForm").Validform({
	tiptype:function(msg,o,cssctl){
		var objtip=$(".error-box");
		objtip.text("");
		cssctl(objtip,o.type);
		if (o.type==3){
		
	
		objtip.text(msg);
		}
		
	},
	beforeSubmit:function(curform){
	
		var username=$("#username").val().trim();
		var password=$("#password").val().trim();
		//var pswd = MD5(username +"#" + password);
		var pswd = MD5(password);
		$("#password").val(pswd);
		return true;
	},
	ajaxPost:false
});

});
<%
String num = "1234567890abcdefghijklmnopqrstopqrstuvwxyz";
int size = 6;
char[] charArray = num.toCharArray();
StringBuffer sb = new StringBuffer();
for (int i = 0; i < size; i++) {
	sb.append(charArray[((int) (Math.random() * 10000) % charArray.length)]);
}
request.getSession().setAttribute("original_data", sb.toString());

// 设置认证原文到页面，给页面程序提供参数，用于产生认证请求数据包
request.setAttribute("original", sb.toString());
%>


function jitLogin(){
	<%
	String original=null,certAuthen =null;
	original = request.getAttribute("original")==null?null:request.getAttribute("original").toString();
	%>
	var Auth_Content = '<%=original%>';
	var DSign_Subject = $("#RootCADN").val();
	if(Auth_Content==""){
		alert("认证原文不能为空!");
		return;
	}else{
		//控制证书为一个时，不弹出证书选择框
		JITDSignOcx.SetCertChooseType(1);
		JITDSignOcx.SetCert("SC","","","",DSign_Subject,"");
		if(JITDSignOcx.GetErrorCode()!=0){
			alert("错误码："+JITDSignOcx.GetErrorCode()+"　错误信息："+JITDSignOcx.GetErrorMessage(JITDSignOcx.GetErrorCode()));
			return;
		}else {
			 var temp_DSign_Result = JITDSignOcx.DetachSignStr("",Auth_Content);
			 if(JITDSignOcx.GetErrorCode()!=0){
					alert("错误码："+JITDSignOcx.GetErrorCode()+"　错误信息："+JITDSignOcx.GetErrorMessage(JITDSignOcx.GetErrorCode()));
					return;
			 }
		//如果Get请求，需要放开下面注释部分
		//	 while(temp_DSign_Result.indexOf('+')!=-1) {
		//		 temp_DSign_Result=temp_DSign_Result.replace("+","%2B");
		//	 }
			$("#signed_data").val(temp_DSign_Result);
		}
	}
	$("#original_jsp").val(Auth_Content);
	
	var postData = {};
	
	$("#jitLoginForm").submit();
	//postData["RootCADN"] = $("#RootCADN").val();
	//postData["signed_data"] = $("#signed_data").val();
	//postData["original_jsp"] = $("#original_jsp").val();

}


</script>
</head>

<body id="body">

<div class="header">
<h1 class="header-logo"></h1>
	<div class="header-nav">
		<span class="down"><a target="_blank" href="<%=request.getContextPath() %>/resources/bsnetfun/grbsctl5.exe">插件下载 </a></span>|
		<span class="down"><a target="_blank" href="<%=request.getContextPath() %>/resources/bsnetfun/czsc0928.zip">操作文档 </a></span>|
		<span class="tel">技术支持: <b class="redFont">010-68551832<b></span>
		
	</div>
</div>


<div class="authcenter">
<div class='authcenter-body fn-clear  <c:if test="${isNeedVerity==true}">need-verity</c:if>'>
<div class="authcenter-body-login ">
<ul class="ui-nav" id="J-loginMethod-tabs">

                			<li data-status="J-login" id="tab-userLogin" style="display: none">账密登录</li>

                            <li data-status="ca-login"id="tab-caLogin"  style="display: none">CA登陆</li>
 
                            <br class="clear-float">
            </ul>
            
 <div class="login login-modern" id="J-login" style="display: none;">
 <div class="error-box"></div>
	<form:form name="loginForm" id="loginForm" method="post">
	   <div class="fm-item">
		   <label for="logonId" class="form-label">登陆用户：</label>
		   <input type="text" value="" placeholder="输入账号" maxlength="100"  name="username" id="username" class="i-text"  datatype="s3-18" nullmsg="请输入输入账号！"  errormsg="用户名至少3个字符,最多18个字符！"  >    
	       <div class="ui-form-explain"></div>
	  </div>
	  
	  <div class="fm-item">
		   <label for="logonId" class="form-label">登陆密码：</label>
		   <input type="password" placeholder="输入密码" value="" maxlength="100" id="password" name="password" class="i-text" nullmsg="请设置密码！" datatype="*" errormsg="密码范围在6~16位之间！">    
	       <div class="ui-form-explain"></div>
	  </div>
	  <c:if test="${isNeedVerity==true}"> 
	  <div class="fm-item pos-r">
		   <label for="logonId" class="form-label">验证码</label>
		   <input type="text" placeholder="输入验证码" maxlength="100" id="validateCode"  name="validateCode" class="i-text yzm" datatype="*" nullmsg="请输入验证码！" >    
	       <div class="ui-form-explain"> <img id="yzmimg" src="captcha.htm"  class="yzm-img" align="absmiddle"
													title="验证码" /></div>
	  </div>
	  </c:if>
	  <div class="fm-item">
		   <label for="logonId" class="form-label"></label>
		   <input type="submit" value="登  陆" tabindex="4" id="send-btn" class="btn-login"> 
	       <div class="ui-form-explain"></div>
	  </div>
	  <input type="hidden" name="lt" value="${loginTicket}" />
				<input type="hidden" name="execution" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" />
				<input type="hidden"  name="loginType" value='1' /> 
				
	  </form:form>
 </div>
 


<div class="login login-modern" id="ca-login" style="display: none;">
 			<div class="error-box"></div>
			<object classid="clsid:707C7D52-85A8-4584-8954-573EFCE77488" id="JITDSignOcx" width="0"  height="0" codebase="<%=request.getContextPath() %>/resources/bsnetfun/JITComVCTK.cab#version=2,0,24,42"></object>
			<form:form id="jitLoginForm" name="jitLoginForm" method="post"  class="jitLoginForm">
				<div>
					<%--
					颁发者DN：
					<select id="RootCADN" style="width:140px;">
						<option value="">CN=Certificate Authority Of MOF,O=MOF,C=CN</option>
					</select>
					 --%>
					<input type="hidden" id="RootCADN" value="" style="width:100px;" />
					<input type="hidden" id="signed_data" name="signed_data" /> 
					<input type="hidden" id="original_jsp" name="original_jsp" /> 
				</div>
			
				<div style="padding-top:45%;">
		   		<input type="button" value="登  陆" tabindex="4" id="ca-send-btn" onclick="jitLogin()" class="btn-login"> 
				<input type="hidden" name="lt" value="${loginTicket}" />
				<input type="hidden" name="execution" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" />
				<input type="hidden"name="loginType" value='2' /> 
				<input type="hidden" name="password"  value='1' /> 
				<input type="hidden" name="username"  value='1' /> 
				</div>
			</form:form>
</div>
</div>
</div>



<div class="footer">
  <div class="logo_f" style="float:left"><img src="<%=request.getContextPath() %>/frame/themes/custom/images/czb.png" width="251" height="30" /></div>
    	<div class="gybs" style="float:right">版本号:1.0.2</div>
</div>
<script>
	<c:forEach var="error" items="${messages}">
			
					var objtip=$(".error-box");
					objtip.text('${error.text}');
				</c:forEach>
	$("#J-loginMethod-tabs li").click(function(){
	$(".error-box").html("");
	$("#J-loginMethod-tabs li").removeClass("active");
	$(this).addClass("active");
	
	var id=$(this).attr("data-status");
	$(".login-modern").hide();
	
	$("#"+id).show();
	})
	
	
	var uiLoginType='${uiLoginType}';
	
	var LOGIN_TYPE_MAP={
			"1":"tab-userLogin",
			"2":"tab-caLogin"
	}
	if (LOGIN_TYPE_MAP[uiLoginType]){
		
		$("#"+LOGIN_TYPE_MAP[uiLoginType]).trigger("click");
		
		for(key in LOGIN_TYPE_MAP){
			if (key!=uiLoginType){
				var contentId=$("#"+LOGIN_TYPE_MAP[key]).attr("data-status");
				$("#"+LOGIN_TYPE_MAP[key]).remove();
				$("#"+contentId).remove();
			}else{
				$("#"+LOGIN_TYPE_MAP[key]).css("width","100%");
				$("#"+LOGIN_TYPE_MAP[key]).show();
			}
			
		}
		
	}else{
		for(key in LOGIN_TYPE_MAP){
			
				$("#"+LOGIN_TYPE_MAP[key]).show();
		
			
		}
		$("#"+LOGIN_TYPE_MAP["1"]).trigger("click");

		
	}
	
	</script>
	
	</body>
</html>
<%

org.springframework.webflow.execution.RequestContextHolder.getRequestContext().getMessageContext().clearMessages();
request.setAttribute("messages", null);
%>

<script>
var imgName='banner02.png'
$(document).ready(function(){
setInterval(function(){
	$(".authcenter").css("background-image","url(<%=request.getContextPath() %>/frame/themes/custom/images/"+imgName+")");
	if (imgName=='banner02.png'){
	imgName='banner01.png';
	}else{
		imgName='banner02.png';
	}
	
},5000);//1000为1秒钟

	});
</script>
