<%@page import="com.ai.yc.protal.web.utils.UserUtil"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
UserUtil.getUserPortraitImg();
%>
<c:set var="uedroot" value="${pageContext.request.contextPath}/resources/template"/>
<c:set var="_base" value="${pageContext.request.contextPath}"/>
<script src="${_base}/resources/spm_modules/jquery/1.9.1/jquery.min.js"></script>
<div class="left-subnav interpreter-subanav">
	<div class="left-title">
		<ul>
			<li class="user"><img id="ycUserPortraitImg" src="${userPortraitImg}" /></li>
			<li class="word">
				<p id="left_username">
					<c:choose>
						<c:when test="${fn:length(user_session_key.username)>=5}">
							${fn:substring(user_session_key.username,0,5)}...
						</c:when>
						<c:otherwise>
							${user_session_key.username}
						</c:otherwise>
					</c:choose>
				</p>
				<p class="vip1"></p>
			</li>
		</ul>
	</div>
	<div class="left-list">
		<ul>
			<li id="index">
				<a href="${_base}/p/security/interpreterIndex">
					<span><i class="icon iconfont">&#xe645;</i></span>
					<%--我的首页--%>
					<span><spring:message code="ycleftmenu.mymainpage"/></span>
				</a>
			</li>
			<li id="lookOrders">
				<a href="javaScript:void(0);">
					<span><i class="icon iconfont">&#xe641;</i></span>
					<%--发现订单--%>
					<span><spring:message code="ycleftmenu.look.orders"/> </span>
				</a>
			</li>
			<%--订单大厅--%>
			<div class="list-p" id="taskCenter"><a href="${_base}/p/taskcenter/view"><spring:message
					code="ycleftmenu.task.center"/><span><c:if test="${taskNum!=null}"> (${taskNum})</c:if></span></a>
			</div>
			<%--分配订单--%>
			<div class="list-p" id="assignTask"><a href="${_base}/p/assigntask/view"><spring:message
					code="ycleftmenu.assign.orders"/><span><c:if test="${assignNum!=null}"> (${assignNum})</c:if></span></a>
			</div>
			<li id="orderList">
				<a href="${_base}/p/trans/order/list/view">
					<span><i class="icon iconfont">&#xe64a;</i></span>
					<%--我的订单--%>
					<span><spring:message code="ycleftmenu.myorder"/></span>
				</a>
			</li>
			<li class="side-bar">
				<a href="#" class="side-title">
					<span><i class="icon iconfont">&#xe646;</i></span>
					<span>LSP管理</span>
				</a>
				<ul>
					<li class="list-p" id="lspBill"><a href="${_base}/p/lspbill/toLspBill">LSP账单</a></li>
				</ul>
			</li>
			<!-- <li>
				<a href="#">
					<span><i class="icon iconfont">&#xe647;</i></span>
					<span>译员级别</span>
				</a>
			</li> -->
			<!-- <li>
				<a href="#">
					<span><i class="icon iconfont">&#xe640;</i></span>
					<span>工作记录</span>
				</a>
			</li> 
			-->
			<%-- <li id="interpreterInfo">
				<a href="${_base}/p/interpreter/interpreterInfoPager?source=interpreter">
					<span><i class="icon iconfont">&#xe642;</i></span>
					<span><spring:message code="ycleftmenu.myinfo"/></span>
				</a>
			</li> --%>
			<li id="seccenterSettings">
				<a  href="${_base}/p/security/seccenter?source=interpreter">
					<span><i class="icon iconfont">&#xe63f;</i></span>
					<span><spring:message code="ycleftmenu.mysecurity"/></span>
				</a>
			</li>
			<!-- <li>
				<a href="#">
					<span><i class="icon iconfont">&#xe606;</i></span>
					<span>LSP管理</span>
				</a>
			</li> -->
		</ul>
	</div>
	<!--定位-->
	<div class="locationaaa">
		<div class="left-phone">
			<p><i class="icon iconfont">&#xe60d;</i></p>
			<p class="phone-word">
				<%--早9:00-晚7:00--%>
				<span><spring:message code="ycleftmenu.time"/></span>
				<span class="red">400-119-8080</span>
			</p>
		</div>
		<div class="left-tplist"><a hrel="#"><img src="${uedroot}/images/to${lTag}.png" /></a><i class="icon-remove-circle"></i></div>
	</div>
</div>
<script type="text/javascript">
    $(function () {
        //一级菜单定位
        var currentEle = $("#" + current);
        if (current != "" && currentEle) {
            $("#left_menu_list ul li").removeClass("current");
            currentEle.addClass("current");
        }
        if(divEleId!=null && divEleId!=""){
            //二级菜单
            var divEle = $("#" + divEleId);
            if (divEle) {
                $("#left_menu_list ul div").removeClass("current");
                divEle.addClass("current");
            }
		}
    });
</script>
