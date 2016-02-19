alert("command:inject"); // for synchronization between view and backend

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
    var navigation = templates.applyTemplate('navigation.ftl', '{}');
    $('body').prepend(navigation);
    $('.navigation-link a').each(function () {
        if ($(this).attr('href') === source) {
            $(this).parent().addClass('active');
        }
    }).click(function () {
        endpoint.post("/application/switch", [$(this).attr('href')]);
    });
});