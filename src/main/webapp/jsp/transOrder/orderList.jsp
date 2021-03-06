<%@ page import="com.ai.paas.ipaas.i18n.ZoneContextHolder" %>
<%@ page import="com.ai.yc.protal.web.constants.OrderConstants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <%@ include file="/inc/inc.jsp" %>
    <title><spring:message code="myOrder.myorders"/></title>
    <script src="${_base}/resources/spm_modules/my97DatePicker/WdatePicker.js"></script>
</head>
<body>
<!--头部-->
<%@include file="/inc/transTopMenu.jsp" %>
<!--二级主体-->
<!--外侧背景-->
<!--二级主体-->
<!--外侧背景-->
<div class="cloud-container">
 <!--内侧内容区域-->
	<div class="translate-cloud-wrapper">
 		<!--左侧菜单-->
 		<%@include file="/inc/transLeftmenu.jsp" %>
	 	<!--右侧内容-->
	 	<!--右侧大块-->
	 	<div class="right-wrapper">	
	 		<!--右侧第二块-->
			<div class="right-list mt-0">
	 			<div class="oder-table">
	 				<ul>
	 					<!--  全部订单 -->
	 					<li><a href="javaScript:void(0);"  class="current" state=""><spring:message code="myOrder.allOrder"/></a></li>
	 					<!-- 已领取  -->
	 					<li><a href="javaScript:void(0);" state="21"><spring:message code="myOrder.status.Claimed"/>(${ReceivedCount})</a></li>
	 					<!-- 翻译中 -->
	 					<li><a href="javaScript:void(0);" state="23"><spring:message code="myOrder.status.translating"/>(${TranteCount})</a></li>
	 				</ul>
	 			</div>
	 			<div id="table-da1">
					<input id="stateP" name="stateP" type="hidden" value="${state}">
	 				<form id="orderQuery">
	 				
	 				<input id="interperId" name="interperId" type="hidden" value="${interperInfo.userId}">
	 				<c:if test="${interperInfo.lspRole == '12' || interperInfo.lspRole == '11'}">
	 					<input id="lspId"  name="lspId" type="hidden" value="${interperInfo.lspId}">
	 				</c:if>
						<input id="lspRole" type="hidden" name="lspRole" value="${interperInfo.lspRole}">
	 				
		 			<div class="oder-form-lable mt-20">
		 				<ul>
		 					<li class="mb-20">
		 						<!-- 订单状态 -->
  								<p class="none-left"><spring:message code="myOrder.orderStatus"/></p>
		 						<p>
		 							<select class="select select-small radius"  name="state" id="state">
		 								<!-- 全部  -->
		 								<option value="" selected="selected"><spring:message code="myOrder.translatingContent.all"/></option>
		 								<!-- 已领取 -->
		 								<option value="21"><spring:message code="myOrder.status.Claimed"/></option>
		 								<!-- 已分配  -->
		 								<!-- <option value="211"><spring:message code="myOrder.status.Assigned"/></option> -->
		 								<!-- 翻译中 -->
		 								<option value="23"><spring:message code="myOrder.status.translating"/></option>
		 								<!-- 待审核 -->
		 								<option value="40"><spring:message code="myOrder.status.Review"/></option>
		 								<!-- 待确认 -->
		 								<option value="50"><spring:message code="myOrder.status.tobeConfirm"/></option>
		 								<!--  已完成-->
		 								<option value="90"><spring:message code="myOrder.status.Completed"/></option>
		 								<!--已评价  -->
		 								<!-- <option value="53"><spring:message code="myOrder.status.Evaluated"/></option> -->
		 								<!-- 修改中 -->
		 								<option value="25"><spring:message code="myOrder.status.Modification"/></option>
		 								<!-- 已退款 -->
		 								<option value="92"><spring:message code="myOrder.status.Refunded"/></option>
		 							</select>
		 						</p>
		 						<!-- 订单阶段 -->
		 						<p><spring:message code="myOrder.OrderStage"/></p>
		 						<p>
		 							<select class="select select-small radius"  name="stateListStr" id="stateListStr">
		 								<!-- 全部  -->
		 								<option value=""><spring:message code="myOrder.translatingContent.all"/></option>
		 								<!-- 翻译  -->
		 								<option value="['211','23','25']"><spring:message code="myOrder.Translate"/></option>
		 								<!-- 审校  -->
		 								<option value="['40','41','42']"><spring:message code="myOrder.Proofread"/></option>
		 							</select>
		 						</p>
		 						<!-- 翻译领域 -->
		 						<p><spring:message code="myOrder.field"/></p>
		 						<p>
		 							<select class="select select-small radius" name="fieldCode" id="fieldCode">
										<!--全部 -->
										<option value="" selected="selected"><spring:message code="myOrder.translatingContent.all"/></option>
		 								<c:forEach items="${domainList}" var="domain">
		 									<option value="${domain.domainId}">
											<c:choose>
												<c:when test="<%=Locale.SIMPLIFIED_CHINESE.equals(response.getLocale())%>">${domain.domainCn}</c:when>
												<c:otherwise>${domain.domainEn}</c:otherwise>
											</c:choose>
										</option>
		 								</c:forEach>
		 							</select>
		 						</p>
		 						<!-- 订单时间 -->
		 						<p><spring:message code="myOrder.orderTime"/></p>
		 						<p><input id="orderTimeStart" name="orderTimeStartStr" type="text" class="int-text int-small radius" onClick="WdatePicker({lang:'${my97Lang}',readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'orderTimeEnd\')||\'%y-%M-%d\'}',onpicked:function(dp){pager._timesearch();}})" ></p>
  								<p>－</p>
  								<p><input id="orderTimeEnd" name="orderTimeEndStr" type="text" class="int-text int-small radius" onClick="WdatePicker({lang:'${my97Lang}',readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'orderTimeStart\')}',maxDate:'%y-%M-%d',onpicked:function(dp){pager._timesearch();}})" ></p>
		 					</li>
		 					<li class="mb-20">
		 						<!--翻译用途  -->
		 						<p class="none-left"><spring:message code="myOrder.purpose"/></p>
		 						<p>
			 						<select class="select select-small radius" name="useCode" id="useCode">
										<!--全部 -->
										<option value="" selected="selected"><spring:message code="myOrder.translatingContent.all"/></option>
			 							<c:forEach items="${purpostList}" var="purpose">
											<option value="${purpose.purposeId}">
												<c:choose>
													<c:when test="<%=Locale.SIMPLIFIED_CHINESE.equals(response.getLocale())%>">${purpose.purposeCn}</c:when>
													<c:otherwise>${purpose.purposeEn}</c:otherwise>
												</c:choose>
											</option>
										</c:forEach>f
			 						</select>
		 						</p>
		 						<p class="iocn-oder right">
									<input id="translateName" name="translateName" type="text" class="int-text int-medium radius pr-30" placeholder="<spring:message code="myOrder.inputContent"/>">
		 							<i id="submitQuery" class=" icon-search"></i></p>
		 					</li>
		 					
		 				</ul>
		 			</div>
		 			</form>

	 			</div>


				<div class="right-list-table">
					<table class="table table-hover table-bg">
						<thead>
						<tr>
							<!-- 任务名称 -->
							<th width="16.666%"><spring:message code="myOrder.TaskName"/></th>
							<!-- 翻译语言 -->
							<th width="16.666%"><spring:message code="myOrder.Language"/></th>
							<!-- 金额(元) -->
							<th width="16.666%"><spring:message code="myOrder.Amount"/></th>
							<!-- 阶段 -->
							<th width="16.666%"><spring:message code="myOrder.Stage"/></th>
							<!-- 状态 -->
							<th width="16.666%"><spring:message code="myOrder.Status"/></th>
							<!-- 操作 -->
							<th width="16.666%"><spring:message code="myOrder.Operate"/></th>
						</tr>
						</thead>
					</table>
				</div>
				<!-- 订单列表 -->
				<div class="right-list-table" id="searchOrderData"></div>
				<script id="searchOrderTemple" type="text/template">
					<table class="table  table-bg tb-border mb-20">
						<thead>
						<tr>
							<th colspan="6" class="text-l">
								<div class="table-thdiv">
									<p>{{:~timestampToDate('yyyy-MM-dd hh:mm:ss',orderTime,'<%=ZoneContextHolder.getZone()%>')}}</p>
									<p name="orderId"><spring:message code="myOrder.Ordernumber"/>：<span style="cursor:pointer;">{{:orderId}}</span></p>
									<!-- 剩余2天23小时59分钟 -->
									{{if state =='20' || state=='21' || state=='211' || state=='23' || state=='25' || state=='40' }}
									<p class="right">
										{{if finishRemTime < 0}}
										<!-- 已超时 -->
										<spring:message	code="myOrder.timedout" />
										{{else}}
										<spring:message	code="myOrder.Remaining" arguments="{{:finishTakeDays}},{{:finishTakeHours}},{{:finishTakeMinutes}}"/>
										{{/if}}
									</p>
									{{/if}}
								</div>
							</th>
						</tr>
						</thead>
						<tbody>
						<input type="hidden" name="orderId" value="{{:orderId}}">
						<input type="hidden" name="unit" value="{{:currencyUnit}}">
						<input type="hidden" name="state" value="{{:state}}">
						<tr class="width-16">
							<td name="translateName" class="text-l pl-20" style="cursor: pointer;">{{:translateName}}</td>
							<td>
								{{for ordProdExtendList}}
								{{if #parent.parent.data.currentLan == 'zh_CN'}}
								{{:langungePairChName}}
								{{else}}
								{{:langungePairEnName}}
								{{/if}}
								{{/for}}
							</td>
							<td>
								{{if  currencyUnit == '1'}}
								<spring:message code="myOrder.rmbSame" arguments="{{:~liToYuan(totalFee)}}" />
								{{else }}
								<spring:message code="myOrder.dollarSame" arguments="{{:~liToYuan(totalFee)}}" />
								{{/if}}
							</td>
							<td>
								<!-- 翻译  审校-->
								{{if state  == '211' || state  == '23' || state  == '25'}}
								<spring:message code="myOrder.Translate"/>
								{{else state  == '40' || state  == '41' || state  == '42'}}
								<spring:message code="myOrder.Proofread"/>
								{{/if}}
							</td>
							{{if  state  == '21'}}
							<!-- 已领取 -->
							<td><spring:message code="myOrder.status.Claimed"/></td>
							<td>
								<%--分配--%>
								<input name="assigne" type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10"
								value="<spring:message code='myOrder.Assign'/>">
								<!-- 翻 译 -->
								<%--若是口译订单，暂时不显示操作--%>
								{{if translateType != '<%=OrderConstants.TranslateType.ORAL%>'}}
								<input name="trans" type="button"  class="btn biu-btn btn-auto-25 btn-green radius10"
									   value="<spring:message code="myOrder.Translate"/>">
								{{/if}}
							</td>
							{{else state  == '221'}}
							<!-- 已分配 -->
							<td><spring:message code="myOrder.status.Assigned"/></td>
							<td>
								<input name="assigne" type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10"
									   value="<spring:message code='myOrder.Assign'/>">
							</td>
							{{else state  == '23'}}
							<!-- 翻译中 -->
							<td><spring:message code="myOrder.status.translating"/></td>
							<td>
								<!-- 提交 -->
								{{if translateType != '2'}}
								<input name="submit" type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10"
									   value="<spring:message code="myOrder.Submit"/>">
								{{/if}}
							</td>
							{{else state  == '24'}}
							<%--已提交--%>
							<td><spring:message code="myOrder.status.submitted"/></td>
							<td></td>
							{{else state  == '40'}}
							<!-- 待审核 -->
							<td><spring:message code="myOrder.status.Review"/></td>
							<td>
								<!--<input name="submit" type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10" value="提交"> -->
							</td>
							{{else state  == '41'}}
							<%--审核通过--%>
							<td><spring:message code="myOrder.status.AuditSuccess"/></td>
							<td></td>
							{{else state  == '42'}}
							<!-- 审核失败 -->
							<td><spring:message code="myOrder.status.Auditfailure"/></td>
							<td></td>
							{{else state  == '50'}}
							<!-- 待确认 -->
							<td><spring:message code="myOrder.status.tobeConfirm"/></td>
							<td></td>
							{{else state  == '51'}}
							<!-- 已确认 -->
							<td><spring:message code="myOrder.status.confirmed"/></td>
							<td></td>
							{{else state  == '90'}}
							<!-- 已完成 -->
							<td><spring:message code="myOrder.status.Completed"/></td>
							<td></td>
							{{else state  == '52'}}
							<!-- 待评价-->
							<td><spring:message code="myOrder.status.tobeEvaluated"/></td>
							<td></td>
							{{else state  == '53'}}
							<!-- 已评价 -->
							{{else displayFlag == '53'}}
							<!-- 已评价  -->
							<td><spring:message code="myOrder.status.Evaluated"/></td>
							<td>
								<input name="checkEvaluated" class="btn biu-btn btn-auto-25 btn-green radius10" type="button" value="<spring:message code="myOrder.ViewEvaluation"/>">
							</td>
							{{else state  == '25'}}
							<!-- 修改中 -->
							<td><spring:message code="myOrder.status.Modification"/></td>
							<td>
								<!-- 提交 -->
								<input name="submit" type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10" value="<spring:message code="myOrder.Submit"/>">
							</td>
							{{else state  == '92'}}
							<!-- 已退款 -->
							<td><spring:message code="myOrder.status.Refunded"/></td>
							<td></td>
							{{else }}
							<td></td>
							<td></td>
							{{/if}}
						</tr>
						</tbody>
					</table>
				</script>
				<!-- 订单列表结束 -->
				<div id="showMessageDiv"></div>
				<!--分页-->
				<div class="biu-paging paging-large">
					<ul id="pagination-ul">
					</ul>
				</div>
				<!--分页结束-->
			</div>	
		</div>
 	</div>
</div>

<%@include file="/inc/userFoot.jsp"%>
</body>
<%@ include file="/inc/incJs.jsp" %>

<script type="text/javascript">
var pager, orderPager;
var current = "orderList";
(function () {
	seajs.use(['app/jsp/transOrder/orderList', 'app/jsp/customerOrder/order'], function(oderListPage, orderPage) {
		pager = new oderListPage({element : document.body});
		orderPager = new orderPage({element : document.body});
		pager.render();
		orderPager.render();
	});
	
	//最上面 订单类型切换
	$("a[state]").click(function () {
		$("a[state]").removeClass("current");
		$(this).addClass("current");
		pager._orderListByType($(this).attr('state'));
	});
	
	//订单详情 点击订单标题
	 $('#searchOrderData').delegate("td[name='translateName']", 'click', function () {
       	  window.location.href="${_base}/p/trans/order/"+$(this).parent().parent().find("input[name='orderId']").val();
     });
	   
     //订单详情 点击订单号
	$('#searchOrderData').delegate("p[name='orderId'] span", 'click', function () {
		  window.location.href="${_base}/p/trans/order/"+$(this).parents("table").find("input[name='orderId']").val();
	});
       
	//提交按钮
	$('#searchOrderData').delegate("input[name='submit']", 'click', function () {
		orderPager._orderSubmit($(this).parents("table").find("input[name='orderId']").val());
	});
	
	//翻译按钮
	$('#searchOrderData').delegate("input[name='trans']", 'click', function () {
		 window.location.href="${_base}/p/trans/order/"+$(this).parents("table").find("input[name='orderId']").val();
	});
	//分配按钮
    $('#searchOrderData').delegate("input[name='assigne']", 'click', function () {
        window.location.href="${_base}/p/trans/order/"+$(this).parents("table").find("input[name='orderId']").val()+
			"?operType=1";
    });
	<%-- 已评价 跳转评价--%>
	$("#searchOrderData").delegate("input[name='checkEvaluated']","click",function(){
		window.location.href="${_base}/p/customer/order/seeEvaluate/"+$(this).parents("table").find("input[name='orderId']").val();
	});
    $("input").placeholder();
})();

  
</script>
</html>
