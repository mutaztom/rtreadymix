const LOGGED_COOKIE = "quarkus-credential";
var OPTIONID;//for use with modification of existing options by clicking edit
var OPTIONTABLE;//for use with new options invoked by pressing add new
var MODIFY = false;
var ERRMSG="";
function onlogout() {
    var conf = confirm('Are you sure you want to check out from the system?');
    if (conf) {
        console.log("logging out")
        document.cookie = LOGGED_COOKIE + '=; Max-Age=0'
        window.location.href = "/login.html";
    }
}


function editOption(oldval, oldarval, optionid) {
    var dlg = $('#modalinput').modal({show: false, focus: false});
    $("#rtnewval").val(oldval);
    $("#rtarnewval").val(oldarval);
    OPTIONID = optionid;
    MODIFY = true;
    dlg.modal('show');
}

function createOption(tblname) {
    console.log("showing new popup box: " + tblname);
    var dlg = $('#modalinput').modal({show: false, focus: false});
    $("#rtnewval").val('');
    $("#rtarnewval").val('');
    $("#tblname").val(tblname);
    OPTIONTABLE = tblname;
    MODIFY = false;
    dlg.modal('show');
}

function closeRtInput() {
    $('#modalinput').modal('hide');
}

$('#modalinput').on('shown.bs.modal', function () {
    $('#rtnewval').trigger('focus');
    $("#msg").html("");
    document.getElementById("errmsg").style.display='none';
})

function removeOption(optionid, tblname) {
    const tbl = "tbl".concat(tblname);
    const ok = confirm("Are you sure you want to delete ".concat(optionid).concat(" from ").concat(tbl));
    if (!ok)
        return;
    var output = false;
    $.ajax(
        {
            type: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            url: '/readymix/removeOption',
            data: JSON.stringify({table: tbl, itemid: optionid}),
            success: function (result) {
                console.log("Received this message from server ".concat(result));
                output = (result === 'OK');
            },
            error: function (t, st, err) {
                console.log("error was encountered.".concat(st).concat(" ").concat(err));
            }
        }
    )
        .done(function () {
            if (output) {
                location.reload();
            }
        });
}

function modifyOption() {
    let conid = OPTIONID;
    let tbl = "tbl" + conid.split("_")[0]
    let optionid = conid.split("_")[1];
    var ok = confirm("Are you sure you modify ".concat(tbl).concat(" itemid: ").concat(optionid));
    if (!ok)
        return;
    let item = $("#rtnewval").val();
    let aritem = $("#rtarnewval").val();
    let output = false;
    $.ajax(
        {
            type: 'POST',
            dataType: 'text',
            headers: {
                'Content-Type': 'application/json'
            },
            url: '/readymix/modifyOption',
            data: JSON.stringify({itemid: parseInt(optionid), table: tbl, item: item, aritem: aritem}),
            success: function (data) {
                console.log("Received this message from server ".concat(data));
                output = (data === 'OK');
                ERRMSG=data;
            },
            error: function (e, status, err) {
                console.log("error was encountered.".concat(err));
                ERRMSG=err;
            }
        }
    ).done(function () {
        if(output) {
            closeRtInput();
            location.reload();
            window.location.href = "#list-options";
        }else{
            var x = document.getElementById("errmsg");
            x.style.display = "block";
            $("#msg").html("There was error executing this command ".concat(ERRMSG));
        }
    })
}

function addOption() {
    let tbl = "tbl" + OPTIONTABLE;
    let item = $("#rtnewval").val();
    let aritem = $("#rtarnewval").val();
    let output = false;
    $.ajax(
        {
            type: 'POST',
            dataType: 'text',
            headers: {
                'Content-Type': 'application/json'
            },
            url: '/readymix/addOption',
            data: JSON.stringify({table: tbl, item: item, aritem: aritem}),
            success: function (msg) {
                console.log("Received this message from server ".concat(msg));
                output = (msg === 'OK');
                ERRMSG=msg;
            },
            error: function (e, status, err) {
                console.log("error was encountered.".concat(err));
                ERRMSG=err;
            }
        }
    ).done(function () {
        if (output) {
            closeRtInput();
            location.reload();
            window.location.href = "#list-options";
        }else{
            var x = document.getElementById("errmsg");
            x.style.display = "block";
            $("#msg").html("There was error executing this command ".concat(ERRMSG));
        }
    })
}


