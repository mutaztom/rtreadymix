$('#addmore').click(function () {
    var index = $('#id_form-TOTAL_FORMS').val()
    var newTable = $('#id_form-__prefix__-itemid').parents('tr').clone()
    newTable.html(newTable.html().replace(/__prefix__/g, index));
    newTable.insertBefore($('#rtcol'))
    $('#id_form-TOTAL_FORMS').val(
        parseInt($('#id_form-TOTAL_FORMS').val()) + 1
    )
    newTable.slideDown();
    newTable.reload();
});


function resetForm(doctype, docid) {
    conf = confirm('Do you realy want to reset all items, all contents of this document will be removed?')
    if (!conf)
        return;
    let count = $('#id_form-TOTAL_FORMS').val();
    for (let i = count; i >= 0; i--) {
        $('#id_form-'.concat(i).concat('-itemid')).parents('tr').remove();
    }
    $(count.val = 1);
    //now sumbit to reset
    $.ajax({
        url: '/documentReset/'.concat(doctype).concat('/').concat(docid).concat('/'),
        data: {
            'doctype': doctype,
            'docid': docid,
            'csrfmiddlewaretoken': csrftoken
        },
        success: (function (data) {
            console.log(data.status)
        }),
        error: function (data) {
            alert(data.status);
        }
    })
}

function onlogout() {
    var conf = confirm('Are you sure you want to check out from the system?');
    if (conf)
        window.location.assign('/checkout/');
}
