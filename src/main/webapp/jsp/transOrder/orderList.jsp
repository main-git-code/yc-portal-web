<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <title>译员-我的订单</title>
    <%@ include file="/inc/inc.jsp" %>
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
	 					<li><a href="javaScript:void(0);"  class="current" state="">全部订单</a></li>
	 					<li><a href="javaScript:void(0);" state="21">已领取(${ReceivedCount})</a></li>
	 					<li><a href="javaScript:void(0);" state="23">翻译中(${TranteCount})</a></li>
	 				</ul>
	 			</div>
	 			<div id="table-da1">
	 				<form id="orderQuery">
		 			<div class="oder-form-lable mt-20">
		 				<ul>
		 					<li class="mb-20">
		 						<p>订单状态</p>
		 						<p>
		 							<select class="select select-small radius"  name="state" id="state">
		 								<option value="">全部</option>
		 								<option value="21">已领取</option>
		 								<!-- <option value="211">已分配</option> -->
		 								<option value="23">翻译中</option>
		 								<option value="52">待审核</option>
		 								<option value="50">待确认</option>
		 								<option value="90">已完成</option>
		 								<!-- <option value="53">已评价</option> -->
		 								<option value="25">修改中</option>
		 								<option value="92">已退款</option>
		 							</select>
		 						</p>
		 						<p>订单阶段</p>
		 						<p>
		 							<select class="select select-small radius"  name="stateListStr" id="stateListStr">
		 								<option value="">全部</option>
		 								<option value="['211','23']">翻译</option>
		 								<option value="['40','41','42']">审校</option>
		 							</select>
		 						</p>
		 						<p>翻译领域</p>
		 						<p>
		 							<select class="select select-small radius" name="fieldCode" id="fieldCode">
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
		 						<p>订单时间</p>
		 						<p><input id="orderTimeStart" name="orderTimeStartStr" type="text" class="int-text int-small radius" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'orderTimeEnd\')}'})" readonly="readonly"></p>
  								<p>－</p>
  								<p><input id="orderTimeEnd" name="orderTimeEndStr" type="text" class="int-text int-small radius" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'orderTimeStart\')}',onpicked:function(dp){endtime();}})" readonly="readonly"></p>
		 					</li>
		 					<li class="mb-20">
		 						<p>翻译用途</p>
		 						<p>
			 						<select class="select select-small radius" name="useCode" id="useCode">
			 							<c:forEach items="${purpostList}" var="purpose">
											<option value="${purpose.purposeId}">
												<c:choose>
													<c:when test="<%=Locale.SIMPLIFIED_CHINESE.equals(response.getLocale())%>">${purpose.purposeCn}</c:when>
													<c:otherwise>${purpose.purposeEn}</c:otherwise>
												</c:choose>
											</option>
										</c:forEach>
			 						</select>
		 						</p>
		 						<p class="iocn-oder right"><input id="translateName" name="translateName" type="text" class="int-text int-medium radius pr-30">
		 							<i id="submitQuery" class=" icon-search"></i></p>
		 					</li>
		 					
		 				</ul>
		 			</div>
		 			</form>
		 			
		 			<div class="right-list-table">
		 				<table class="table table-hover table-bg">
		                   <thead>
		                      <tr>
		                           <th width="16.666%">任务名称</th>
		                           <th width="16.666%">翻译语言</th>
		                           <th width="16.666%">金额(元)</th>
		                           <th width="16.666%">阶段</th>
		                           <th width="16.666%">状态</th>
		                           <th width="16.666%">操作</th>
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
		                           			<p>{{:~timesToFmatter(orderTime)}}</p>
		                           			<p name="orderId">订单号：<span>{{:orderId}}</span></p>
		                           			<p class="right">剩余2天23小时59分钟</p>
		                           		</div>
		                           </th>
		                     </tr>
		              		</thead>
		                   <tbody>
							<input type="hidden" name="orderId" value="{{:orderId}}">
							<input type="hidden" name="unit" value="{{:currencyUnit}}">
							<input type="hidden" name="state" value="{{:state}}">
							<tr class="width-16">
		                           <td name="translateName">{{:translateName}}</td>
		                           <td>
									{{for ordProdExtendList}}
										{{if #parent.parent.data.currentLan == 'zh_CN'}}
											{{:langungePairChName}}
										{{else}}
											{{:langungePairEnName}}
										{{/if}}
									{{/for}}
								   </td>
		                           <td>{{:~liToYuan(totalFee)}}
										{{if  currencyUnit == '1'}}
											<spring:message code="myOrder.rmb"/>
										{{else }}
											<spring:message code="myOrder.dollar"/>
										{{/if}}
								   </td>
		                           <td>
								   		{{if state  == '211' || state  == '23'}}
											翻译
										{{else state  == '40' || state  == '41' || state  == '42'}}
											审校
										{{/if}}
								   </td>
								   {{if  state  == '21'}}
								   		<td>已领取</td>
		                           		<td>
		                           			<!-- <input type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10" value="分 配"> -->
		                           			<input name="trans" type="button"  class="btn biu-btn btn-auto-25 btn-green radius10"  value="翻 译">
		                          		</td>
								   {{else state  == '221'}}
										<td>已分配</td>
		                           		<td>
		                           			<!-- <input name="assigne" type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10" value="分 配"> -->
		                          		</td>
								   {{else state  == '23'}}
									<!-- 翻译中 -->
										<td>翻译中</td>
		                           		<td>
		                           			<input name="submit" type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10" value="提交">
		                          		</td>
								   {{else state  == '40'}}
									<!-- 待审核 -->
										<td>待审核</td>
		                           		<td>
		                           			<input name="submit" type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10" value="提交">
		                          		</td>
								  {{else state  == '50'}}
									<!-- 待确认 -->
										<td>待确认</td>
		                           		<td>
		                          		</td>
								   {{else state  == '90'}}
									<!-- 已完成 -->
										<td>已完成</td>
		                           		<td>
		                          		</td>
								  {{else state  == '53'}}
									<!-- 已评价 -->
										<td>已评价</td>
		                           		<td>
											<!-- <input name="evaluated" type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10" value="已评价"> -->
		                          		</td>
								  {{else state  == '25'}}
									<!-- 修改中 -->
										<td>修改中</td>
		                           		<td>
											<input name="submit" type="button"  class="btn biu-btn btn-auto-25 btn-yellow radius10" value="提交">
		                          		</td>
								  {{else state  == '92'}}
									<!-- 已退款 -->
										<td>已退款</td>
		                           		<td>
		                          		</td>
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
</div>
</body>
<%@ include file="/inc/incJs.jsp" %>

<script type="text/javascript">
var pager;
(function () {
	seajs.use('app/jsp/transOrder/orderList', function(oderListPage) {
		pager = new oderListPage({element : document.body});
		pager.render();
	});
	
	//最上面 订单类型切换
	$(".oder-table ul li a").click(function () {
		$(".oder-table ul li a").each(function () {
		    $(this).removeClass("current");
		});
		$(this).addClass("current");
		pager._orderListByType($(this).attr('state'));
	});
	
	//订单详情 点击订单标题
	 $('#searchOrderData').delegate("td[name='translateName']", 'click', function () {
       	  window.location.href="${_base}/p/trans/order/"+$(this).parent().parent().find("input[name='orderId']").val();
     });
	   
     //订单详情 点击订单号
	$('#searchOrderData').delegate("p[name='orderId']", 'click', function () {
		  window.location.href="${_base}/p/trans/order/"+$(this).parents("table").find("input[name='orderId']").val();
	});
       
	//提交按钮
	$('#searchOrderData').delegate("input[name='translateName']", 'click', function () {
	 
	});
	
	//翻译按钮
	$('#searchOrderData').delegate("input[name='trans']", 'click', function () {
		 window.location.href="${_base}/p/trans/order/"+$(this).parents("table").find("input[name='orderId']").val();
	});
	
})();


  
</script>
</html>
