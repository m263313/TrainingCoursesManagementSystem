<%@ page language="java" contentType="text/html;charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/presentation/header.jsp"%>
<div class = "row">
	<div class = "col-sm-12">
		<div class = "text-center">
			<h2><spring:message code="error.404.description"/></h2>
		</div>
	</div>
</div>
<br/>

<div class = "row">
	<div class = "col-sm-12">
		<div class="text-center">
			<h2><a href="/"><spring:message code="error.404.action.description"/></a></h2>
		</div>
	</div>
</div>
<%@include file="/presentation/footer.jsp"%>