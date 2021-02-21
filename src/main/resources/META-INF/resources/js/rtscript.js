const LOGGED_COOKIE = "quarkus-credential";
var OPTIONID;//for use with modification of existing options by clicking edit
var OPTIONTABLE;//for use with new options invoked by pressing add new
var MODIFY = false;
var ERRMSG = "";

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
    document.getElementById("errmsg").style.display = 'none';
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
                ERRMSG = data;
            },
            error: function (e, status, err) {
                console.log("error was encountered.".concat(err));
                ERRMSG = err;
            }
        }
    ).done(function () {
        if (output) {
            closeRtInput();
            location.reload();
            window.location.href = "#list-options";
        } else {
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
                ERRMSG = msg;
            },
            error: function (e, status, err) {
                console.log("error was encountered.".concat(err));
                ERRMSG = err;
            }
        }
    ).done(function () {
        if (output) {
            closeRtInput();
            location.reload();
            window.location.href = "#list-options";
        } else {
            var x = document.getElementById("errmsg");
            x.style.display = "block";
            $("#msg").html("There was error executing this command ".concat(ERRMSG));
        }
    })
}

function sendEmail(mail) {
    let msg = $("#txtmail").val();
    console.log("Sending email to client: " + mail);
    $.ajax({
        url: '/readymix/sendMail',
        data: JSON.stringify({"mailto": mail, "message": msg}),
        headers: {'Content-Type': 'application/json'},
        method: 'PUT',
        onscroll: function (r) {
            console.log("Message sent succesfully! " + r);
        },
        onerror: function (e, s, err) {
            console.log("Send failed, " + err);
        }
    });
}

function sendSMS(mobile) {
    let msg = $("#txtsms").val();
    console.log("Sending SMS to client: " + mobile);
    $.ajax({
        url: '/readymix/sendSMS',
        data: JSON.stringify({"mobile": mobile, "message": msg}),
        headers: {'Content-Type': 'application/json'},
        method: 'PUT',
        onscroll: function (r) {
            console.log("Message sent succesfully! " + r);
        },
        onerror: function (e, s, err) {
            console.log("Send failed, " + err);
        }
    });
}

function saveCall(email, mobile) {
    let msg = $("#txtcall").val();
    console.log("Saving conversation : " + mobile);
    $.ajax({
        url: '/readymix/saveCall',
        data: JSON.stringify({"mobile": mobile, "message": msg, 'clientid': email}),
        headers: {'Content-Type': 'application/json'},
        method: 'POST',
        onscroll: function (r) {
            console.log("Phone call saved succesfully !" + r);
        },
        onerror: function (e, s, err) {
            console.log("Send failed, " + err);
        }
    });
}

Date.prototype.addHours = function (h) {
    this.setTime(this.getTime() + (h * 60 * 60 * 1000));
    return this;
}

function getStatusColor(status) {
    let stcolor = 'Brown';
    switch (status) {
        case 'Processing':
            stcolor = "#2c76f6";
            break;
        case 'Created':
            stcolor = "#bf1d1d";
            break;
        case 'Rejected':
            stcolor = "#595c5f";
            break;
        case 'Delivered':
            stcolor = null;
            break;
        case 'Canceled':
            stcolor = "#333538";
            break;

        default:
            stcolor = null;
    }
    return stcolor;
}

function filter() {
    $("#progress").css({"display":"block"});
    console.log(location);
    setTimeout(function () {
        location.reload();
        console.log("reloading order page applying filters");
        $("#progress").css({"display":"none"});
    }, 2000);

}