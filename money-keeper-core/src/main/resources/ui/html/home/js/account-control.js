/**
 * Created by scorpion on 22.02.16.
 */

$(document).ready(function () {
    var accounts = JSON.parse(endpoint.get('/account', []));
    accounts.forEach(function (account) {
        appendAccountItem(buildAccountItem(account));
    });

    $('#create-account').click(createAccount);
    $('#remove-account').click(removeAccount);
    $('#accounts').on('click', '.account-remove', openRemoveAccountConfirmation);
});

function createAccount() {
    var name = $('#account-name');
    if (!name.val().trim()) {
        showAlert2('#account-create-alerts-container', 3000, 'danger', 'Please enter name', 'account-bad-name-alert');
        return;
    }
    var account = JSON.parse(endpoint.post('/account', [JSON.stringify({
        name: name.val(),
        parserType: $('#account-type').val()
    })]));
    if (account == undefined) {
        showAlert2('#account-create-alerts-container', 3000, 'danger', 'Failed to create account', 'account-create-failed-alert');
        return;
    }
    appendAccountItem(buildAccountItem(account));
    name.val('');
}

function openRemoveAccountConfirmation() {
    var accountItem = $(this).closest('.account');
    $('#remove-account').prop('target', accountItem);
    $('#account-remove-confirm-modal').modal();
}

function removeAccount() {
    var accountItem = $(this).prop('target');
    var account = accountItem.prop('source');
    endpoint.delete('/account', [account.id]);
    accountItem.remove();
    $('#account-remove-confirm-modal').modal('hide');
}

function buildAccountItem(account) {
    var item = $(templates.applyTemplate('account-item.ftl', JSON.stringify(account)));
    item.prop('source', account);
    return item;
}

function appendAccountItem(item) {
    $('#accounts').append(item);
}