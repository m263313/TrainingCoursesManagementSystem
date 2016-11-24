/**
 * Created by Alex_Frankiv on 14.11.2016.
 */

$(document).ready(function () {
    $("#showAvailableCriteria").click(function () {
        $.ajax({
            url: "/getAvailableMeetingCriteria",
            type: "GET",
            data: {meetingId: meetingId},
            success: function (data) {
                console.log(data);
                appendTableRows(data);
            },
            error: function (textStatus) {
                console.log(textStatus);
            }
        });
    });

    $(document).on('click', '.addButton', function () {
        var a = $(this);
        $.ajax({
            url: "/addMeetingCriteria",
            type: "POST",
            data: {meetingId: meetingId, criteriaTitle: a.closest('tr').find('td:first').text()},
            success: function (data) {
                console.log(data);
                $('#collapseIn').append(buildResponseCriteria(data));
                a.parent().parent().remove();
            },
            error: function (textStatus) {
                console.log(textStatus);
            }
        });
    });

    $(document).on('click', '.rmv-cr-btn', function () {
        var criteria = $.parseJSON($(this).attr('data-button'));
        $.ajax({
            url: "/deleteMeetingCriteria",
            type: "POST",
            data: {meetingId: meetingId, criteriaTitle: criteria.title},
            success: function (data) {
                console.log(data);
                $('#criteriaId-' + criteria.id).remove();
            },
            error: function (textStatus) {
                console.log(textStatus);
                $('#criteriaDeleteErrorModal').modal('show');

            }
        });
    });


    function appendTableRows(data) {
        $("#criterionTable > tbody:last").children().remove();
        $.each(data, function (key, value) {
            $('#criterionTable > tbody:last-child').append("<tr>" +
                "<td>" + value.title + "</td>" +
                "<td><button class='addButton btn-primary btn-sm'><span class='glyphicon glyphicon-plus'></span></button></td>" +
                "</tr>");
        });
    }

    $("#search").keyup(function () {
        var value = this.value.toLowerCase();

        $("table").find("tr").each(function (index) {
            if (!index)
                return;
            var id = $(this).find("td").first().text().toLowerCase();
            $(this).toggle(id.indexOf(value) !== -1);
        });
    });
});

var setAttendance = function (meetingId, studentId) {
    $.ajax({
        type: 'POST',
        data: {meetingId: meetingId, studentId: studentId},
        url: '/meeting/set_no_attendance',
        statusCode: {
            404: function (response) {
                console.log('404');
            },
            200: function (response) {
                console.log(response);
            }
        }
    });
};

var do_evaluate = function (reviewId) {
    var reviewComment = $('#review-comment').val();
    var results = [];
    $('ul').find('li').each(function (index) {
        if ($(this).find('.result-criterion-id').val())
            results.push({
                mark: {value: $(this).find('.result-mark').val()}, commentary: $(this).find('.result-comment').val(),
                criterion: {id: $(this).find('.result-criterion-id').val()}
            });
    });
    console.log(results);
    $.ajax({
        type: 'POST',
        data: JSON.stringify({results: results, reviewId: reviewId, reviewComment: reviewComment}),
        url: '/meeting/do_evaluate',
        dataType: 'json',
        contentType:'application/json',
        statusCode: {
            500: function (response) {
                console.log(response.responseText);
            },
            400: function (response) {
                console.log(response);
            },
            404: function (response) {
                console.log('404');
            },
            200: function (response) {
                console.log(response);
            }
        }
    });
};

function changeSpan() {
    if ($("#spanId").hasClass("glyphicon-chevron-down")) {
        $("#spanId").removeClass('glyphicon-chevron-down');
        $("#spanId").addClass('glyphicon-chevron-up');
    } else {
        $("#spanId").removeClass('glyphicon-chevron-up');
        $("#spanId").addClass('glyphicon-chevron-down');
    }
}

function buildResponseCriteria(data) {
    return "<div class='panel-body' id='criteriaId-" + data.id + "'>" +
        "<div class='col-md-11'>" + data.title + "</div>" +
        "<c:if test='" + data.rated + "'>" +
        "<div class='btn rmv-cr-btn col-md-1' type='button'" +
        " data-button='{\"id\":\"" + data.id + "\", \"title\": \"" + data.title + "\"}'>" +
        "<span class='glyphicon glyphicon-remove'></span>" +
        "</div>" +
        "</c:if>" +
        "</div>";
}
;