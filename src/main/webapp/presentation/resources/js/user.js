function getMark(mark){
	if(mark == ' ')
		return ' ';
	else if(mark == '-')
		return '<p style="padding:0px; margin:0px;">-</p>';
	return '<p style="padding:0px; margin:0px;">'+mark+'</p>';
}

function getStringDate(date){
	var month = date.getMonth() + 1;
	if(month < 10)
		month = '0'+month;
	
	var day = date.getDate();
	if(day < 10)
		day = '0'+day;
	
    return date.getFullYear() + '-' + month + '-' + day;
}

function createMentorProjectsInfo(userId, divInside){
	$.ajax({
	    'url' : '/ajaxmentorprojects',
	    'type' : 'GET',
	    'data' : {'user' : userId},
	    
	    'success' : function(data) {
	    	var s = '';
	    	$.each( data, function( key, value ) {
	    		var currentDate = new Date();
	    		var startDate = new Date(value.startDate);
	    		var finishDate = new Date(value.finishDate);
	    		var divClass = 'default';
	    		
	    		if(startDate.getTime() < currentDate.getTime() && currentDate.getTime() < finishDate.getTime()){
	    			divClass = 'current';
	    		}else if(startDate.getTime() > currentDate.getTime()){
	    			divClass = 'primary';
	    		}else{
	    			divClass = 'finished';
	    		}
	    		
	    		s+= '<h2>Mentor projects: </h2><hr/>';
	    		s+= '<div class="panel panel-'+divClass+'">';
	    		s+= '<div id="mpr'+value.id+'" class="panel-body"><a href="/certainProject/'+value.id+'">' + value.name + '</a>';
	    		s+= '<div class="pull-right">'+getStringDate(new Date(value.startDate))+' - '+getStringDate(new Date(value.finishDate))+'</div>';
 	    		s+= '</div></div>';
	    	
	    	});

	    	if(jQuery.isEmptyObject(data))
	    		s= '<br/><h2>'+lang.user_mentor_projects+'</h2><hr/><h4>'+lang.user_no_projects+'</h4>';
	    		
	    	$(divInside).html(s);
	    }
	});
}

function createStudentProjectsInfo(userId, divInside){
	$.ajax({
	    'url' : '/ajaxstudentprojects',
	    'type' : 'GET',
	    'data' : {'user' : userId},
	    
	    'success' : function(data) {
	    	var s = '<h2>'+lang.user_student_projects+'</h2><hr/>';
	    	$.each( data, function( key, value ) {
	    		var currentDate = new Date();
	    		var startDate = new Date(value.startDate);
	    		var finishDate = new Date(value.finishDate);
	    		var divClass = 'default';
	    		
	    		try{
	    			getStringDate(new Date(value.startDate));
	    		}catch(error){
	    			alert(error);
	    		}
	    		
	    		if(startDate.getTime() < currentDate.getTime() && currentDate.getTime() < finishDate.getTime()){
	    			divClass = 'current';
	    		}else if(startDate.getTime() > currentDate.getTime()){
	    			divClass = 'primary';
	    		}else{
	    			divClass = 'finished';
	    		}
	    		s+= '<div class="panel panel-'+divClass+'">';
	    		s+= '<div id="pr'+value.id+'" class="panel-body clickable">' + value.name;
	    		s+= '<div class="pull-right">'+getStringDate(new Date(value.startDate))+' - '+getStringDate(new Date(value.finishDate))+'</div>';
 	    		s+= '</div>';
 	    		s+= '</div>';
 	    		
 	    		s+= '<div id="sub'+value.id+'">';
 	    		s+= '<div class="row">';
 	    		s+= '<div class="col-sm-12">';
 	    		s+= '<div class="col-sm-12">';
 	    		s+= '<div id="stinf-'+value.id+'"></div>';
 	    		s+= '<br/><h4 class="project-description">'+value.description+'</h4>';
 	    		s+= '<div class="col-sm-8 col-sm-offset-2 charts-wrappere" id="chart'+value.id+'">'+lang.user_loading+'...<br/><br/></div>';
 	    		s+= '</div></div></div></div>';
	    	});

	    	if(jQuery.isEmptyObject(data))
	    		s= '<br/><h2>'+lang.user_student_projects+'</h2><hr/><h4>'+lang.user_no_projects+'</h4>';
	    	
	    	$(divInside).html(s);
                
	    	$.each(data, function(key, value) {
	    		$('#sub'+value.id).hide();
	    		$('#pr'+value.id).click(function() {
	    			$('#sub'+value.id).slideToggle();
	    			$('#pr'+value.id).unbind( "click" );
	    			$('#pr'+value.id).click(function() {
	    				$('#sub'+value.id).slideToggle();
	    			});
	    			
    	 			$.ajax({
    	 	    	    'url' : '/ajaxstudentprofile',
    	 	    	    'type' : 'GET',
    	 	    	    'data' : {
    	 	    	      'student' : userId,
    	 	    	      'project' : value.id
    	 	    	    },
    	 	    	   
    	 	    	    'success' : function(data) {
    	 	    	    	$('#chart'+value.id).html('');
    	 	    	    	
    	 	    	    	$('#stinf-'+value.id).html('<h4><b>'+lang.user_status_title+'</b>'+data.studentStatuses[data.studentStatuses.length - 1].statusTitle+'</h4>');
//                            createCharts(data.chartInfo, value.id);
                            radarChart(data.chartInfo, value.id);

    	 	    	    	
                            var reviews = '<br/><div class="row"><div class="col-sm-12"> <div class="col-sm-12">';
    	 	    	    	if(data.technicalReview !== null || data.generalReview !== null){
    	 	    	    		reviews+= '<br/><div class="row"><h2>'+lang.user_final_reviews+'</h2>';
    	 	    	    		reviews+= getFinalReviews(data)+'</div>';
    	 	    	    	}
    	 	    	    	var table= '';
    	 	    	    	
    	 	    	    	if(data.markTableDto.tableData.length>0){
    	 	    	    		table+= '<br/>';
    	 	    	    		table+= '<div class="row"><div class = "col-sm-10"><h2>'+lang.user_grades_title+'</h2></div>';
    	 	    	    		table+= '<div class = "col-sm-2"><h2>';
    	 	    	    		table+=	'</h2></div></div>';
                            
    	 	    	    		table+= '<div class="panel panel-default"><div class="panel-heading"><div class="row">';
    	 	    	    		table+= '<div class = "col-sm-5">';
    	 	    	    		table+= getSelectCategories(data.markTableDto.projectCategories, value.id);
    	 	    	    		table+= '</div>';
    	 	    	    		table+= '<div class = "col-sm-5">';
    	 	    	    		table+= getSelectCriteria(data.markTableDto.projectCriteria, value.id);
    	 	    	    		table+= '</div>';
    	 	    	    		table+= '<div class = "col-sm-2">';
    	 	    	    		table+= '<button onclick="search('+value.id+')" class="form-control btn btn-default">'+lang.user_marks_search+'</button>';
    	 	    	    		table+= '</div>';
    	 	    	    		table+= '</div></div>';
    	 	    	    		table+= getMarksTable(data.markTableDto, value.id);
    	 	    	    		table+= '</div>';
    	 	    	    	}
    	 	    	    	
                            table+= '<br/></div></div></div>';
    	 	    	    	$('#sub'+value.id).append(reviews);
    	 	    	    	$('#sub'+value.id).append(table);
    	 	    	    	
    	 	    	    	$('.criteria-select-'+value.id).select2({
    	 	    	    		  placeholder: lang.user_select_criteria
    	 	    	    	});
    	 	    	    	$('.categories-select-'+value.id).select2({
    	 	    	    		  placeholder: lang.user_select_categories
    	 	    	    	});

    	 	    	       var $table = $('#marks-'+value.id);
    	 	    	       var $fixedColumn = $table.clone().insertBefore($table).addClass('fixed-column');

    	 	    	       $fixedColumn.find('th:not(:first-child),td:not(:first-child)').remove();

    	 	    	       $fixedColumn.find('tr').each(function (i, elem) {
    	 	    	           $(this).height($table.find('tr:eq(' + i + ')').height());
    	 	    	           $(this).width($table.find('tr:eq(' + i + ')').width());
    	 	    	       });
    	 	    	    }
    	 	    	  });
    			});
	    	});
	    }
	  });
}

function createCharts(data, projectId) {
    var chart = $("#chart" + projectId);
    var typesDropdown = "<div class='dropdown chart-dropdown' id='type-menu'>" +
            "<button class='btn btn-link dropdown-toggle'" + 
            "type='button' data-toggle='dropdown'>" +
            "Show<span class='caret'></span></button>" +
            "<ul class='dropdown-menu'>" +
      "<li><a id='all-criteria'>All criteria</a></li>" +
      "<li><a id='categories'>Categories</a></li>" + 
      "<li><a id='grouped-criteria'>Grouped criteria</a></li></ul>";
    chart.append(typesDropdown);
    var sortDropdown = "<div class='dropdown chart-dropdown' id='sort-menu'>" +
            "<button class='btn btn-link dropdown-toggle'" + 
            "type='button' data-toggle='dropdown'>" +
            "Sort by<span class='caret'></span></button>" +
            "<ul class='dropdown-menu'>" +
      "<li><a id='increase'>Increase</a></li>" +
      "<li><a id='decrease'>Decrease</a></li></ul>";
    chart.append(sortDropdown);
    var criteriaValues = [];
    $.each(data, function (index, value) {
        $.each(value, function (index, value) {
            criteriaValues.push(value.averageValue);
        });
    });
    var maxYAxisValue = Math.max.apply(null, criteriaValues);
//    $('.chart-dropdown ul li').click(function(e) {
//        console.log($(this).find('a').attr('id'));
//    });
    drawCriteriaChart(data, '#chart' + projectId, 6, 'title');
}

function getMarksTable(markTableDto, projectId){
	var table = '';
 	table+= '<div class="table-responsive">';
 	table+= '<table class="table table-bordered user-table" id="marks-'+projectId+'">';
 	table+= '<tr><th>#</th>';
 	
 	$.each(markTableDto.meetings, function( key, value ) {
 			table+= '<th align="center">'+getModal(value.id, 'M', value.name, value)+'</th>';
 	});
 	
 	if(markTableDto.finalReview == null)
 		table+= '<th align="center"><center>F</center></th>';
 	else
 		table+= '<th align="center">'+getModal('f'+markTableDto.finalReview.id, 'F', lang.user_final_review, markTableDto.finalReview)+'</th>';
 	
 	table+= '</tr>';

 	$.each(markTableDto.tableData, function(key, categoryResult ) {
 		table+= '<tr id="row-'+projectId+'-'+categoryResult.categoryDto.id+'"><td><b>' + categoryResult.categoryDto.name + '</b></td><td colspan="'+ markTableDto.meetings.length*15+'"></td></tr>';
 		
 		$.each(categoryResult.criteriaResults, function( i, criterionResult ) {
 			table+= '<tr class="active" id="row-'+projectId+'-'+categoryResult.categoryDto.id+'-'+criterionResult.criterionId+'"><td style="padding-left:30px;">' + criterionResult.criterionName + '</td>';
 			$.each(criterionResult.marks, function(index, mark) {
 					if(mark.commentary == '')
 						table+= '<td align="center"><span class="clickable" title="'+mark.description+''+mark.commentary+'">' + getMark(mark.value) + '</span></td>';
 					else
 						table+= '<td align="center"><span class="clickable" title="'+mark.description+': '+mark.commentary+'">' + getMark(mark.value) + '</span></td>';
	    		});
 			table+= '</tr>';
 		});
 			
 	});
 	
 	table+= '</table></div>';
 	
 	return table;
}

function getFinalReviews(studentProfile){
	var reviews = '';
	
	reviews += '<table class="table table-bordered">';
	reviews += '<tr>';
	reviews += '<th style="width:50%">'+lang.user_technical_review+'</th>';
	reviews += '<th style="width:50%">'+lang.user_general_review+'</th>';
	reviews += '</tr>';

	if (studentProfile.technicalReview !== null) {
		reviews += '<td><b>On ' + studentProfile.technicalReview.date + ' by '
				+ studentProfile.technicalReview.lastName + ' '
				+ studentProfile.technicalReview.firstName + '</b><br/>'
				+ studentProfile.technicalReview.commentary + '</td>';
	} else
		reviews += '<td>'+lang.user_no_technical_review+'</td>';

	if (studentProfile.generalReview !== null) {
		reviews += '<td><b>On ' + studentProfile.generalReview.date + ' by '
				+ studentProfile.generalReview.lastName + ' '
				+ studentProfile.generalReview.firstName + '</b><br/>'
				+ studentProfile.generalReview.commentary + '</td>';

		reviews += '</tr>';
	} else 
		reviews += '<td>'+user.user_no_general_review+'</td>';
	
	return reviews;
}

function getSelectCategories(projectCategories, projectId){
	var select = '<select style="width:90%;" multiple="true" class="categories-select-'+projectId+'">';
	
	$.each(projectCategories, function(key, value ) {
		select += '<option value="' + value.id + '">' + value.name + '</option>\n';
	});
	
	select += '</select>';
	
	return select;
}

function search(projectId){
	var emptyCategoties = [];
	var selectedCriteria = []; 
	var selectedCategories = []; 
	
	$('.criteria-select-'+projectId+' :selected').each(function(){
		selectedCriteria.push($(this).val()); 
    });
	
	$('.categories-select-'+projectId+' :selected').each(function(){
        selectedCategories.push($(this).val()); 
    });
    
	$('#marks-'+projectId+' > tbody  > tr').each(function(index, value) {
		if($(value).css('display') == 'none')
			$(value).show();
	});
	
	if(selectedCriteria.length != 0 || selectedCategories.length != 0){
		var currentCategory;
		var open = 0;
    
		$('#marks-'+projectId+' > tbody  > tr').each(function(index, value) {
			var id = value.id;
			var keys = id.split("-");
 		
			if(keys.length === 3){
				if(currentCategory != null && open === 0){
					$(currentCategory).hide();
				}
 			
				open = 0;
				currentCategory = value;	
			}
 		
			if(keys.length === 4){
				if(($.inArray(keys[2], selectedCategories)) == -1 && ($.inArray(keys[3], selectedCriteria)) == -1)
					$(value).hide();
				else
					open = open + 1;
			}
		});
 	
		if(currentCategory != null && open === 0){
			$(currentCategory).hide();
		}
	}
}

function getSelectCriteria(projectCriteria, projectId){
	var select = '<select style="width:90%;" multiple="true" class="criteria-select-'+projectId+'">';
	
	$.each(projectCriteria, function(key, value ) {
		select += '<option value="' + value.id + '">' + value.title + '</option>\n';
	});
	
	select += '</select>';
	
	return select;
}

function getReviewForm(userId, projectId) {
	$('#addFinReview').modal('toggle');
	$('#finReviewProject').modal('toggle');
	$.ajax({
		'url' : '/ajax/get/final_review_form',
		'type' : 'GET',
		'data' : {'user' : userId, 'projectId' : (projectId)?projectId:$('#fin-rev-proj-switch').val()},
		'success' : function(resp) {
			var s = '';
			$.each( resp.data, function( key, value ) {
				s+='<tr class="fin-rev-res-item"><td><label>'+value.criterion.title+'</label></td><td><select id="sel'
				+value.criterion.id+'">';
				for(var i=0; i<6; ++i){
					s+='<option val='+i;
					if(value.mark&&i==value.mark.value)
						s+=' selected';
					if(i==0)
						s+=' title = "NeZnayu"'
					if(i==1)
						s+=' title = "Slabovat"'
					if(i==2)
						s+=' title = "NeOchen"'
					if(i==3)
						s+=' title = "Srednenko"'
					if(i==4)
						s+=' title = "NePloh"'
					if(i==5)
						s+=' title = "Krasavchik"'
					s+='>'+i+'</option>';
				}
				s+='</select></td><td><input maxlength="255" required type="text" id="'
					+value.criterion.id+'"';
				if(value.commentary)
					s+=' value="'+value.commentary+'"';
				s+='></td></tr>';
			});
			s+='<tr><td colspan="3"><label>'+lang.final_review_comment_general+'</label><textarea maxlength="255" class="form-control" id="fin-rev-com" rows="5">';
			if(resp.comment)
				s+=resp.comment;
			s+='</textarea></td></tr>';
			if(jQuery.isEmptyObject(resp.data))
				s= '<br/><h4>'+lang.final_review_error_no_criteria+'</h4>';
			else
				$('#addFinReview').find('.btn').removeClass('hidden');
			$('#final-review-form-list').html(s);
		},
		'error': function (data) {
			console.warn(data);
		}
	});
}

function getMentorStudentProjects(userId){
	$.ajax({
		'url': '/ajax/get/projects_final_review',
		'type': 'GET',
		'data': {'studentId': userId},
		'success': function (resp) {
			if(resp.length==1){
				getReviewForm(userId, resp[0].id);
				return;
			}
			var s = '<br/><tr><td colspan="3"><select class="form-control" id="fin-rev-proj-switch">';
			$.each(resp, function (key, value) {
				s+='<option value="'+value.id+'">'+value.name+'</option>';
			});
			s+='</select></td></tr>';
			$('#final-review-project-list').html(s);
			if(resp.length>1)
				$('#finReviewProject').find('.btn').removeClass('hidden');
		},
		'error': function (resp) {
			var s = '<tr><td colspan="3"><label>'+lang.review_no_projects+'</label></td></tr>';
			$('#final-review-hr-project-list').html(s);
		}
	});
}

function doFinalReview(userId) {
	function sendAjax(data, comment) {
		console.log(comment);
		$.ajax({
			url: '/ajax/post/final_review_form/'+userId,
			type: 'POST',
			contentType: "application/json",
			dataType: 'json',
			data: JSON.stringify({data:data,comment:comment}),
			success: function (data) {
				console.log('success');
			},
			error: function (error) {
				console.log(error.responseText);
			}
		});
	};
	var error = null;
	var data = [];
	var comment = $('#fin-rev-com').val();
	if(!comment){
		error = lang.final_review_error_input_required;
		$('#fin-rev-com').addClass('error');
	}
	else
		if($('#fin-rev-com').hasClass('error'))
			$('#fin-rev-com').removeClass('error');
	$('#final-review-form-list').find($('.fin-rev-res-item')).each(function () {
		var finReview = {"mark": {"value": $(this).find('select').val()}, "criterion": {"id": $(this).find('input').attr('id')}, "commentary": $(this).find('input').val()};
		if(!$(this).find('input').val()) {
			error = lang.final_review_error_input_required;
			$(this).addClass('error');
		}
		else if($(this).hasClass('error'))
			$(this).removeClass('error');

		data.push(finReview);
	});
	if(!error){
		$('#fin-rev-err').addClass('hidden');
		$('#addFinReview').modal('toggle');
		sendAjax(data, comment);
	}
	else{
		$('#fin-rev-err').text(error);
		$('#fin-rev-err').removeClass('hidden');
	}
};

function report(studentId){
	$.ajax({
	    'url' : '/ajaxstudentprojects',
	    'type' : 'GET',
	    'data' : {'user' : studentId},
	    'success' : function(data) {
	    	var select = '<div class="row">';
	    	select += '<div class="col-sm-12">';
	    	select += lang.user_select_projects;
	    	select += '</div>';
	    	select += '</div>'
	    	select += '<div class="row">';
	    	select += '<div class="col-sm-12">';
	    	select += '<select style="width:100%;" multiple="true" class="select-project-report">';
	    	
	    	$.each(data, function(key, value ) {
	    		select += '<option value="' + value.id + '">' + value.name + '</option>\n';
	    	});
	    	
	    	select += '</select>';
	    	select += '</div>';
	    	select += '</div><hr/>';
	    	select += '* '+lang.user_note_projects+'<br/>';
	    	select += '* '+lang.user_note_projects2+'<br/><br/>';
	    	
	    	select += '<div class="row"><div class="col-sm-12">';
	    	select += '<button onclick="loadCriteria('+studentId+')" class="btn btn-primary pull-right">'+lang.user_next_step+'</button>';
	    	select += '</div></div>';
	    	if(data.length == 0)
	    		$('#project-report-back').html(lang.user_no_projects);
	    	else{
	    		$('#project-report-back').html(select);
	 	    	$('.select-project-report').select2();
	    	}

	    }
	});
}

function loadCriteria(studentId){
	var projects = [];
	var selectedProjects = $('.select-project-report').val();
	
	if(selectedProjects != null)
		projects = selectedProjects;
	
	$.ajax({
	    'url' : '/ajaxcriteria?projects='+projects+'&student='+studentId,
	    'type' : 'GET',
	    'success' : function(data) {

	    	select = '<hr/><div class="row">';
	    	select += '<div class="col-sm-12">';
	    	select += lang.user_select_categories + ':';
	    	select += '</div>';
	    	select += '</div>';
	    	select += '<div class="row">';
	    	select += '<div class="col-sm-12">';
	    	select += '<select style="width:100%;" multiple="true" class="select-category-report">';
	    	
	    	$.each(data.categories, function(key, value ) {
	    		select += '<option value="' + value.id + '">' + value.name + '</option>\n';
	    	});
	    	
	    	select += '</select>';
	    	select += '</div>';
	    	select += '</div>';
	    	$('#criteria-report-back').html(select);
 	    	$('.select-category-report').select2();
 	    	select = '<hr/><div class="row">';
 	    	select += '<div class="col-sm-12">';
 	    	select += lang.user_select_criteria + ':';
 	    	select += '</div>';
 	    	select += '</div>';
 	    	select += '<div class="row">';
 	    	select += '<div class="col-sm-12">';
	    	select += '<select style="width:100%;" multiple="true" class="select-criterion-report">';
	    	
	    	$.each(data.criteria, function(key, value ) {
	    		select += '<option value="' + value.id + '">' + value.title + '</option>\n';
	    	});
	    	
	    	select += '</select>';
	    	select += '</div>';

	    	select += '</div>';
	    	select += '<hr/>';
	    	select += '* '+lang.user_note_criteria+'<br/><br/>';
	    	select += '<div class="row><div class="col-sm-12">';
	    	select += '<button onclick="sendRequest('+studentId+')" class="btn btn-primary pull-right">'+lang.user_download+'</button>';
	    	select += '</div></div><br/><br/>';
	    	$('#criteria-report-back').show();
	    	$('#criteria-report-back').append(select);
 	    	$('.select-criterion-report').select2();
	    }
	});
}

function sendRequest(studentId){
	var projects = [];
	var selectedProjects = $('.select-project-report').val();
	
	if(selectedProjects != null)
		projects = selectedProjects;
	
	var selectedCategories = $('.select-category-report').val();
	var categories = [];
	
	if(selectedCategories != null)
		categories = selectedCategories;
	
	var criteria = [];
	var selectedCriteria = $('.select-criterion-report').val();
	
	if(selectedCriteria != null)
		criteria = selectedCriteria;

	var url = '/studentMarks.xls?studentId='+studentId+'&projects=';
	url +=projects+'&categories='+categories;
	url += '&criteria='+criteria;
	window.location = url;
	
	$('#projects-report-modal').modal('hide');
}

function getModal(modalId, modalName, modalTitle, reviewDto){
	
	var modal = '<center><font class="clickable" color="blue" data-toggle="modal" data-target="#'+modalId+'"><b>'+modalName+'</b></font></center>';

	modal += '<div id="'+modalId+'" class="modal fade" role="dialog">';
	modal += '<div class="modal-dialog">';
	modal += '<div class="modal-content">';
	modal += '<div class="modal-header">';
	modal += '<button type="button" class="close" data-dismiss="modal">&times;</button>';
	modal += '<h4 class="modal-title">'+modalTitle+'</h4>';
	modal += '</div>'
	modal += '<div style="max-height:80vh;overflow-y:auto;" class="modal-body text-left">';
	modal += '<div class="remove-all-styles">';
	modal += '<b>'+lang.user_date+': </b>'+reviewDto.date;
	
	if(reviewDto.type === '-')
		modal += '<br/>'+lang.user_no_meeting_info;
	else {
		
		if(reviewDto.commentary){
			modal += '<br/><b>'+lang.user_general_comment+'</b> '+reviewDto.commentary + '<br/><hr/>';
		}else{
			modal += '<br/><hr/>';
		}
		
    	$.each(reviewDto.marks, function(key, value ) {
    		modal += '<b>'+lang.user_criterion_name+':</b> ' + value.criterionName + '<br/>';
    		modal += '<b>'+lang.user_mark+':</b> ' + value.value + ' ('+value.description+')<br/>';
    		modal += '<b>'+lang.user_commentary+':</b> ' + value.commentary + '<hr/>';
    	});
	}

	modal += '</div>';
	modal += '</div>';
	modal += '</div>';
	modal += '</div>';
	modal += '</div>';
	
	return modal;
}

function getHRReviewProjects(userId) {
	$('#finReviewHRProject').modal('toggle');
	$.ajax({
		'url': '/ajax/get/hr_review_projects',
		'type': 'GET',
		'data': {'studentId': userId},
		'success': function (resp) {
			var s = '';
			if(resp.length>0) {
				s += '<br/><tr><td colspan="3"><select class="form-control" id="fin-review-hr-proj-switch">';
				$.each(resp, function (key, value) {
					s += '<option value="' + value.id + '">' + value.name + '</option>';
				});
				s += '</select></td></tr>';
				$('#finReviewHRProject').find('.btn').removeClass('hidden');
			}else{
				s = '<tr><td colspan="3"><label>'+lang.review_no_projects+'</label></td></tr>';
			}
				$('#final-review-hr-project-list').html(s);
		},
		'error': function (resp) {
			var s = '<tr><td colspan="3"><label>'+lang.review_no_projects+'</label></td></tr>';
			$('#final-review-hr-project-list').html(s);
		}
	});
}

function getHRReviewForm(userId) {
	$('#finReviewHRProject').modal('toggle');
	$('#createHRreviewModal').modal('toggle');
	$.ajax({
		'url': '/ajax/get/hr_review',
		'type': 'GET',
		'data': {'studentId': userId, 'projectId': $('#fin-review-hr-proj-switch').val()},
		'success': function (resp) {
			var s = '';
			switch (resp.available){
				case 'B':
					s+='<option value="G">General</option><option value="T">Technical</option>';
					break;
				case 'G':
					s+='<option value="G">General</option>';
					break;
				case 'T':
					s+='<option value="T">Technical</option>';
					break;
				default:
					break;
			}
			$('#reviewtype').html(s);
			$('#project-id-hr-rev').val(resp.project.id);
		},
		'error': function (resp) {
			console.warn(resp);
		}
	});
};

function doHRRequest(userId) {
	if(!$('#hr-rev-commentary').val()){
		$('#hr-rev-err').removeClass('hidden');
		$('#hr-rev-commentary').addClass('error');
		return;
	}
	$.ajax({
		'url': '/ajax/post/hr_review',
		'type': 'POST',
		'data': {'studentId': userId, 'projectId': $('#project-id-hr-rev').val(),
			'comment': $('#hr-rev-commentary').val(), 'type': $('#reviewtype').val()},
		'success': function (resp) {
			console.warn(resp);
			$('#hr-rev-commentary').val('');
		},
		'error': function (resp) {
			console.warn(resp);
		}
	});
	if($('#hr-rev-commentary').hasClass('error')){
		$('#hr-rev-err').addClass('hidden');
		$('#hr-rev-commentary').removeClass('error');
	}
	$('#createHRreviewModal').modal('toggle');
	$('#finReviewHRProject').find('.btn').addClass('hidden');
};