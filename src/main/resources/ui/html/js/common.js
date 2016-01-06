alert("command:inject");

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