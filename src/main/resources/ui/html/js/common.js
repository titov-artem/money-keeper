alert("command:inject"); // for synchronization between view and backend

function getQueryParameters(str) {
    return decodeURI((str || document.location.search)).replace(/(^\?)/, '').split("&").map(function (n) {
        return n = n.split("="), this[n[0]] = n[1], this
    }.bind({}))[0];
}

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