<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <%@ include file="/inc/inc.jsp" %>
    <title>联系人</title>
    <c:choose>
        <c:when test="${TransType == '2'}">
            <c:set var="order" value="${sessionScope.oralOrderInfo}" scope="session" />
            <c:set var="orderSummary" value="${sessionScope.oralOrderSummary}" scope="session" />
        </c:when>
        <c:otherwise>
            <c:set var="order" value="${sessionScope.writeOrderInfo}" scope="session" />
            <c:set var="orderSummary" value="${sessionScope.writeOrderSummary}" scope="session" />
        </c:otherwise>
    </c:choose>

    <c:set var="transType" value="${order.baseInfo.translateType}" />
</head>
<body>
    <!--面包屑导航-->
    <%@ include file="/inc/topHead.jsp" %>
    <%@ include file="/inc/topMenu.jsp" %>
    <!--主体-->
    <!-- 联系人 -->
    <div class="placeorder-container" id="contactPage">
        <div class="placeorder-wrapper">
            <!--步骤-->
            <div class="place-bj">
                <div class="place-step">
                    <!--进行的状态-->
                    <div class="place-step-none adopt-wathet-bj">
                        <ul>
                            <li class="circle"><i class="icon iconfont">&#xe60f;</i></li>
                            <li class="word"><spring:message code="order.translateLan"/></li>
                        </ul>
                        <p class="line"></p>
                    </div>
                    <!--未进行的状态-->
                    <div class="place-step-none adopt-blue-bj">
                        <ul>
                            <li class="circle"><i class="icon iconfont">&#xe60d;</i></li>
                            <li class="word"><spring:message code="order.contact"/></li>
                        </ul>
                        <p class="line"></p>
                    </div>
                    <!--未进行的状态-->
                    <div class="place-step-none adopt-ash-bj">
                        <ul>
                            <li class="circle"><i class="icon iconfont">&#xe608;</i></li>
                            <li class="word"><spring:message code="order.payment"/></li>
                        </ul>
                        <p class="line"></p>
                    </div>

                </div>
            </div>
            <!--白色背景-->
            <div class="white-bj">
                <div class="right-list-title pl-20 none-border">
                    <p><spring:message code="order.ContactInfo"/></p>
                </div>

                <input type="hidden"  name="gnCountryId" value="${Contact.gnCountryId}" />
                <form id="contactForm">
                <div class="oral-form" id="saveContactDiv">
                    <ul>
                        <li>
                            <input type="hidden" name="contactId" value="${Contact.contactId}"/>
                            <p><input id="userName" name="userName" value="${Contact.userName}" maxlength="10" type="text" class="int-text int-in-bi radius" placeholder="<spring:message code="order.EnterName"/>"></p>
                            <p><select id="globalRome" name="gnCountryId" class="select select-in radius"></select></p>
                            <p><input id="mobilePhone" name="mobilePhone"  value="${Contact.mobilePhone}" pattern="^1\d{10}$" type="text" class="int-text int-in-bi radius" placeholder="<spring:message code="order.EnterPhone"/>"></p>
                            <p class="mr-0"><input id="email" name="email" value="${Contact.email}"  maxlength="64" type="text" class="int-text int-large-mail radius" placeholder="<spring:message code="order.EnterEmail"/>"></p>
                        </li>
                        <li class="right-btn"><input id="saveContact" maxlength="18" type="button" class="btn radius20 border-blue btn-50" value="<spring:message code="order.Save"/>"></li>

                    </ul>
                </div>
                </form>

                <div class="oral-form" id="editContactDiv" style="display: none;">
                    <ul>
                        <li>
                            <p></p>
                            <p></p>
                            <p></p>
                            <p class="right"><a href="javaScript:void(0);" id="editContact"><i class="icon-edit"></i></a></p>
                        </li>
                    </ul>
                </div>
            </div>
            <!--白色背景-->
            <div class="white-bj">
                <input type="hidden" id="transType" value="${transType}">
                <div class="right-list-title pb-10 ">
                    <p><spring:message code="order.ConfirmOrderInfo"/></p>
                </div>
                <div class="right-list-table">
                    <c:if test="${transType != '2'}">
                        <table id="textOrderTable" class="table table-th-color">
                            <thead>
                            <tr>
                                <th width="32%"><spring:message code="order.Subject"/></th>
                                <th width="17%"><spring:message code="order.Language"/></th>
                                <th width="17%"><spring:message code="order.Fields"/></th>
                                <th width="17%"><spring:message code="order.purpose"/></th>
                                <th width="17%"><spring:message code="order.TranslationGrade"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>${order.baseInfo.translateName}</td>
                                <td>${orderSummary.duadName}</td>
                                <td>${orderSummary.purposeName}</td>
                                <td>${orderSummary.domainName}</td>
                                <td>${orderSummary.translevel}</td>
                            </tr>
                            </tbody>
                        </table>
                    </c:if>
                    <%--口译订单--%>
                    <c:if test="${transType == '2'}">
                        <table class="table table-th-color" id="oralOrderTable">
                            <thead>
                            <tr>
                                <th width="24%"><spring:message code="order.Subject"/></th>
                                <th width="12%"><spring:message code="order.translateLan"/></th>
                                <th width="12%"><spring:message code="order.InterpretationType"/></th>
                                <th width="12%"><spring:message code="order.StartingTime"/></th>
                                <th width="12%"><spring:message code="order.EngdingTime"/></th>
                                <th width="12%"><spring:message code="order.Place"/></th>
                                <th width="12%"><spring:message code="order.MeetingAmount"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>${order.baseInfo.translateName}</td>
                                <td>${orderSummary.duadName}</td>
                                <td>${orderSummary.translevel}</td>
                                <td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss"
                                                    value="${order.productInfo.startTime}"/></td>
                                <td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss"
                                                    value="${order.productInfo.endTime}"/></td>
                                <td>${order.productInfo.meetingAddress}</td>
                                <td>${order.productInfo.meetingSum}</td>
                            </tr>
                            </tbody>
                        </table>
                    </c:if>
                </div>

                <c:if test="${transType != '2'}">
                    <div class="right-list-title pb-10 mt-20 none-border">
                        <!-- 给译员留言 -->
                        <p><spring:message code="order.LeaveMessage"/></p>
                    </div>
                    <div class="lx-textarea">
                        <p><textarea id="remark"
                                 onkeyup="textLimit(this,50);" onkeydown="textLimit(this,50);"
                                 oninput="textLimit(this,50);"  onpropertychange="textLimit(this,50);"
                                class="int-text textarea-xlarge-text radius">${order.baseInfo.remark}</textarea></p>
                    </div>
                </c:if>
            </div>
            <!--白色背景-->
            <div class="white-bj">
                <div class="right-list-title pb-10 none-border">
                    <p><spring:message code="order.TotalPirce"/></p>
                </div>
                <div class="urgent">
                    <ul>

                        <li id="price">
                            <c:if test="${transType == '0' && order.feeInfo.currencyUnit == '1'}">
                                <span><fmt:formatNumber value="${order.feeInfo.totalFee/1000}" pattern="#,##0.00#"/></span><spring:message code="order.yuan"/>
                            </c:if>
                            <c:if test="${transType == '0' && order.feeInfo.currencyUnit == '2'}">
                                $<span><fmt:formatNumber value="${order.feeInfo.totalFee/1000}" pattern="#,##0.00#"/></span>
                            </c:if>
                            <c:if test="${transType != '0'}">
                                <spring:message code="order.waitPatiently" />
                            </c:if>
                        </li>
                    </ul>
                </div>
            </div>


            <div class="recharge-btn order-btn placeorder-btn">
                <input type="button" id="toCreateOrder" skip="${skip}" class="btn btn-yellow btn-xxxlarge radius10" value="<spring:message code="order.Back"/>">
                <input type="button" id="submitOrder" class="btn btn-green btn-xxxlarge radius10" value="<spring:message code="order.SubmitOrder"/>">
            </div>
        </div>
    </div>

    <!--底部-->
    <%@include file="/inc/indexFoot.jsp"%>
</body>
<%@ include file="/inc/incJs.jsp" %>
<script type="text/javascript">
    (function () {
        var pager;
        seajs.use(['app/jsp/order/orderContact'], function(orderContactPage) {
            pager = new orderContactPage({element : document.body});
            pager.render();
        });
        //IE8的输入框提示信息兼容
        $("input,textarea").placeholder();
    })();

    function textLimit(field, maxlimit) {
        // 函数，3个参数，表单名字，表单域元素名，限制字符；
        if (field.value.length > maxlimit){
            //如果元素区字符数大于最大字符数，按照最大字符数截断；
            field.value = field.value.substring(0, maxlimit);
        }
    }
</script>
</html>