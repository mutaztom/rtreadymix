const LOGGED_COOKIE = "quarkus-credential";

function onlogout() {
    var conf = confirm('Are you sure you want to check out from the system?');
    if (conf) {
        console.log("logging out")
        document.cookie = LOGGED_COOKIE + '=; Max-Age=0'
        window.location.href = "/login.html";
    }
}

// $(".rtinput").click(function () {
//     console.log("showing new popup box:");
//     var dlg = $('#modalinput').modal({show: false, focus: false});
//     dlg.modal('show');
// });

function editOption(oldval, oldarval, optionid) {
    var dlg = $('#modalinput').modal({show: false, focus: false});
    $("#rtnewval").val(oldval);
    $("#rtarnewval").val(oldarval);
    dlg.modal('show');
}

function createOption(optionid) {
    console.log("showing new popup box: " + optionid);
    var dlg = $('#modalinput').modal({show: false, focus: false});
    $("#rtnewval").val('');
    $("#rtarnewval").val('');
    dlg.modal('show');
}

function closeRtInput() {
    $('#modalinput').modal('hide');
}

$('#modalinput').on('shown.bs.modal', function () {
    $('#rtnewval').trigger('focus')
})

function removeOption(optionid,tblname) {
    let tbl="tbl".concat(tblname);
    var ok=confirm("Are you sure you want to delete ".concat(optionid).concat(" from ").concat(tbl));
    if(!ok)
        return;
    $.ajax(
        {
            dataType:'text',
            type: "POST",
            headers: {
                'Content-Type':'application/json',
                'consumes':'text/plain'
            },
            url: '/readymix/removeOption',
            data: {"itemid": optionid,"table":tbl},
            success: function (data) {
                console.log("Received this message from server ".concat(data));
            },
            error: function (e) {
                console.log("error was encountered.".concat(e));
            }
        }
    );
}
function modifyOption(optionid,tblname,item,aritem) {
    let tbl="tbl".concat(tblname);
    var ok=confirm("Are you sure you want to delete ".concat(tbl));
    if(!ok)
        return;
    $.ajax(
        {
            type:'POST',
            method:'POST',
            dataType:'text',
            url: '/readymix/modifyOption',
            data: {'itemid': optionid,"table":tbl,"item":item,"aritem":aritem},
            success: function (data) {
                console.log("Received this message from server ".concat(data));
            },
            error: function (e) {
                console.log("error was encountered.".concat(e));

            }
        }
    )
}


