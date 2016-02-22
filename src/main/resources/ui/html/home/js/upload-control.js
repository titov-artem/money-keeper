/**
 * Created by scorpion on 22.02.16.
 */

$(document).ready(function () {
    $('#accounts').on('click', '.account-upload', uploadBankStatement);
    $('#duplicates-container').on('click', '.remove-transaction', removeTransaction);
    $('#deduplicate').click(deduplicate);
    $('#deduplicate-close').click(closeDeduplicate);
});

function uploadBankStatement() {
    var account = $(this).closest('.account').prop('source');
    var uploadResult = JSON.parse(endpoint.post('/bank/statement/upload', [account.id]));
    if (uploadResult.result === 'SUCCESS') {
        if (uploadResult.duplicates.length == 0) {
            showAlert2('#upload-alerts-container', 3000, 'success', 'Bank statement uploaded successfully', 'file-uploaded-alert');
        } else {
            showAlert2('#upload-alerts-container', 5000, 'info', 'Bank statement uploaded successfully, but duplicates found', 'file-uploaded-alert');
            showDuplicates(uploadResult);
        }
    } else if (uploadResult.result === 'FAILED') {
        showAlert2('#upload-alerts-container', 3000, 'danger', 'Failed to upload bank statement file due to exception', 'failed-to-upload-file-alert');
    } else if (uploadResult.result === 'NO_FILE_CHOSEN') {
        showAlert2('#upload-alerts-container', 3000, 'warning', 'No file was selected', 'no-file-selected-alert');
    }
}

function removeTransaction() {
    var transaction = $(this).parent('.transaction');
    var source = transaction.prop('source');
    endpoint.delete('/transactions', [source.id]);
    transaction.remove();
}

function deduplicate() {
    var container = $('#duplicates-container');
    var removedDuplicates = JSON.parse(endpoint.post('/transactions/deduplicate', [
        '"' + container.prop('from') + '"',
        '"' + container.prop('to') + '"' // todo think about this
    ]));
    hideDuplicates();
    showAlert2('#upload-alerts-container', 3000, 'success', 'Removed ' + removedDuplicates.length + ' duplicates', 'duplicates-removed-alert');
}

function closeDeduplicate() {
    hideDuplicates();
    showAlert2('#upload-alerts-container', 3000, 'warning', 'Duplicates left intact!', 'duplicates-left-intact-alert');
}

function showDuplicates(uploadResult) {
    var container = $('#duplicates-container');
    container.removeClass('hidden');
    container.prop('from', uploadResult.from);
    container.prop('to', uploadResult.to);
    var list = $('#duplicates-list');
    list.children().each(function (i, e) {
        e.remove()
    });
    uploadResult.duplicates.forEach(function (transaction) {
        var row = $(templates.applyTemplate('editable-transaction-row.ftl', JSON.stringify(transaction)));
        row.prop('source', transaction);
        list.append(row);
    });
}

function hideDuplicates() {
    var container = $('#duplicates-container');
    container.addClass('hidden');
    var list = $('#duplicates-list');
    list.children().each(function (i, e) {
        e.remove()
    });
}