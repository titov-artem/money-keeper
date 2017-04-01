jQuery.ajaxSetup({async:false});

function getQueryParameters(str) {
    return decodeURI((str || document.location.search)).replace(/(^\?)/, '').split("&").map(function (n) {
        return n = n.split("="), this[n[0]] = n[1], this
    }.bind({}))[0];
}

/**
 * @param alertSelector
 * @param time
 * @deprecated use showAlert2
 */
function showAlert(alertSelector, time) {
    var alert = $(alertSelector);

    var oldTimer = alert.prop('timer');
    if (oldTimer !== undefined && oldTimer != null) {
        clearTimeout(oldTimer);
    }

    alert.removeClass('hidden');
    var timer = setTimeout(function () {
        $(alertSelector).addClass('hidden');
        alert.prop('timer', null);
    }, time);
    alert.prop('timer', timer);
}

/**
 * Display alert for specified time period
 * @param container container which will hold an alert
 * @param time duration of time while which alert should be displayed
 * @param type alert type - one of { success, info, warning, danger }
 * @param text text that will be displayed in alert
 * @param alertId alert id if needed to prevent duplicating of alert if second alert acquires
 * while first alert is on the screen
 */
function showAlert2(container, time, type, text, alertId) {
    var alertContainer = $(container);
    var alert = null;
    if (alertId !== undefined) {
        alert = $('#' + alertId);
    }
    if (alert == null || (alert.length != undefined && alert.length == 0)) {
        alert = $('<div ' + (alertId === undefined ? '' : 'id="' + alertId + '" ') + 'class="alert alert-' + type + '" role="alert">' + text + '</div>');
    }

    var oldTimer = alert.prop('timer');
    if (oldTimer !== undefined && oldTimer != null) {
        clearTimeout(oldTimer);
    }

    alertContainer.append(alert);
    var timer = setTimeout(function () {
        alert.remove();
    }, time);
    alert.prop('timer', timer);
}

$(document).ready(function () {
    var navigation = $('<div id="navigation-container">' +
        '<ul class="nav nav-pills">' +
        '<li role="presentation" class="navigation-link"><a href="/static">Home</a></li>' +
        '<li role="presentation" class="navigation-link"><a href="/static/reports/reports.html">Reports</a></li>' +
        '<li role="presentation" class="navigation-link"><a href="/static/transactions/transactions.html">Transactions</a></li>' +
        '<li role="presentation" class="navigation-link"><a href="/static/category/category-editor.html">Category editor</a></li>' +
        '<li role="presentation" class="navigation-link"><a href="/static/store/stores-editor.html">Store editor</a></li>' +
        '</ul>' +
        '</div>');
    $('body').prepend(navigation);
    $('.navigation-link a').each(function () {
        // if ($(this).attr('href') === source) {
        //     $(this).parent().addClass('active');
        // }
    })
});

function reloadPage() {
    endpoint.post("/application/switch", [source]);
}

var endpoint = {
    apiPath: '/services/api',
    get: function (path, params) {
        var finalPath = this.apiPath + path + '?';
        for (var property in params) {
            if (params.hasOwnProperty(property)) {
                finalPath = finalPath + property + '=' + encodeURIComponent(params[property]) + '&';
            }
        }
        var out;
        $.get(finalPath, function(data) { out = data; });
        console.log(out);
        return out;
    },
    post: function (path, params) {
        return $.post(this.apiPath + path, params);
    }
};