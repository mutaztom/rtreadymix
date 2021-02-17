var tmpfile;
var commmedia;
var actcurid;
var actuserid;

function showTemplate(fname, commtype) {
    tmpfile = fname;
    commmedia = commtype;
    $.ajax({
        url: '/rtmix/getTemplate/' + commmedia + "/" + tmpfile,
        type: 'GET',
        headers: {'Contet-Type': 'text/plain'},
        data: {},
        success: function (r) {
            // console.log("recieved from server:" + r);
            $("#rtemplate").val(r);
            $("#rtemplate").load

            $("#rtitle").text(tmpfile);
            $('.collapse').collapse('show');
            $('.collapse').focus;
            window.location.hash = '#rtemplate';
            document.getElementById("closetemplate").style.display = "block";
            console.log(r);
        },
        error: function (st, err) {
            console.log("got error ".concat(err));
        }
    });
}

function closeTemplate() {
    $(".collapse").collapse('hide');
    $("#rtemplate").text("");
    document.getElementById("closetemplate").style.display = "none";
}

function saveTemplate() {
    let fcontent = $("#rtemplate").val();
    console.log(fcontent);
    $.ajax({
        url: '/rtmix/saveTemplate/' + commmedia + "/" + tmpfile,
        type: 'POST',
        data: fcontent,
        headers: {'Content-Type': 'text/plain'},
        success: function (r) {
            // console.log("recieved from server:" + r);
            $("#rtmessage").text(r);
            $("#rtmessage").css('display:block;')
            closeTemplate();
            ///it may be wise to reload page
            location.reload();
        },
        error: function (st, err) {
            console.log("got error ".concat(err));
            $("#rterror").text(err);
            $("#rterror").css('display:block;')

        }
    });
}

function deleteTemplate(fname, mtype) {
    let conf = confirm("Are you sure you want to delete this template?");
    if (!conf)
        return;
    tmpfile = fname;
    commmedia = mtype;
    $.ajax({
        url: '/rtmix/deleteTemplate/' + commmedia + "/" + tmpfile,
        type: 'DELETE',
        data: {},
        headers: {'Content-Type': 'text/plain'},
        success: function (r) {
            // console.log("recieved from server:" + r);
            $("#rtmessage").text(r);
            $('.collapse').collapse('hide');
            $("#rtmessage").css('display:block;')
            location.reload();
        },
        error: function (st, err) {
            console.log("got error ".concat(err));
            $("#rterror").text(err);
            $("#rterror").css('display:block;')
        }
    });
}

function showCurrency(curid) {
    actcurid = curid;
    $("#act-curitem").val($("#curitem_" + curid).val());
    $("#act-curname").val($("#curname_" + curid).val());
    $("#act-cursymbol").val($("#cursymbol_" + curid).val());
    $("#act-curfraction").val($("#curfraction_" + curid).val());
    $("#act-curiso").val($("#curiso_" + curid).val());
    $('.collapse').collapse('show');
    $('.collapse').focus();
}

function saveCurrency() {
    let currency = {};
    currency['id'] = actcurid;
    currency['item'] = $("#act-curitem").val().trim();
    currency['name'] = $("#act-curname").val().trim();
    currency['symbol'] = $("#act-cursymbol").val().trim();
    currency['fraction'] = $("#act-curfraction").val().trim();
    currency['shortName'] = $("#act-curiso").val().trim();
    console.log(currency)
    $.ajax({
        type: 'POST',
        url: '/rtmix/saveCurrency',
        data: JSON.stringify(currency),
        headers: {'Content-Type': 'application/json'},
        success: function (result) {
            console.log(result);
            if (result === 'OK') {
                $('.collapse').collapse('hide');
                location.reload();
            }
        },
        error: function (e, st, error) {
            console.log(error);
            $("#rterror").text(error);
        }
    })

}

function deleteCurrency(curid) {
    let conf = confirm("Are you sure you want to delete this currency?");
    if (!conf)
        return;
    $.ajax({
        url: '/rtmix/deleteCurrency/' + curid,
        type: 'DELETE',
        data: {},
        headers: {'Content-Type': 'text/plain'},
        success: function (r) {
            console.log("recieved from server:" + r);
            if (r === 'OK') {
                document.getElementById("rtmessage").innerText = r;
                $('.collapse').collapse('hide');
                document.getElementById("rtmessage").style.display = 'block';
                location.reload();
            }
        },
        error: function (st, en, err) {
            console.log("got error ".concat(err));
            $("#rterror").text(err);
            document.getElementById("rterror").style.display = 'block';
        }
    });
}

function createTemplate(comtype) {
    let tname = prompt("Enter template name (Must be unique)");
    let exist = false;

    //make sure it doesn't exist
    exist = checkTemplateExist(tname, comtype);
    commmedia = comtype;
    if (exist) {
        alert("File already exists");
    } else {
        tmpfile = tname;
        $(".collapse").collapse('show');
        $('.collapse').focus;
        $("#rtemplate").val(null);
        $("#rtitle").val('New Template: ' + tname);
        document.getElementById("closetemplate").style.display = "block";
    }
}

function createCurrency() {
    actcurid = -1;
    $("#act-curitem").val("");
    $("#act-curname").val("");
    $("#act-cursymbol").val("");
    $("#act-curfraction").val("");
    $("#act-curiso").val("");
    $("#curcol").collapse('show');
}

function checkTemplateExist(tfile, comtype) {
    let elist = document.getElementsByName(comtype === 'email' ? 'emailtname' : 'smstname');
    let found = false;
    elist.forEach(
        function (currentValue, currentIndex, listObj) {
            if (currentValue.innerText === tfile) {
                found = true;
                return true;
            }
        },
        // 'myThisArg'
    );
    return found;
}

function saveProps() {
    let plist = document.getElementsByName('propval');
    let pdata = {'p': 'val'};
    plist.forEach(
        function (curval, curindex, nodeList) {
            pdata[nodeList.item(curindex).id] = curval.value;
            // console.log(nodeList.item(curindex).id + " : " + curval.value);
        },);
    $.ajax(
        {
            url: '/rtmix/saveProp',
            data: JSON.stringify(pdata),
            headers: {'Content-Type': 'application/json'},
            type: 'POST',
            success: function (result) {
                console.log(result);
                if (result === 'OK') {
                    alert("Preoperties were saved successfully");
                }
            },
            error: function (e, s, err) {
                console.log(err);
            }
        }
    )

    function saveNotify() {
        $.ajax({
            url: 'read'
        })
    }
}

function addNotifySettings(ntype) {
    let newitem = prompt("Please enter new admin " + ntype);
    let allmail = null;
    let props = {};
    if (ntype === 'adminmail') {
        allmail = "{rtutil:prop('adminmail')}";
        props = {"adminmail": allmail.concat(",").concat(newitem)};
    } else if (ntype === "adminmobile") {
        allmail = "{rtutil:prop('adminmobile')}";
        props = {"adminmobile": allmail.concat(",").concat(newitem)};
    }
    updateNotify(props);
}

function saveNotifyMethod() {
    let sms = $('#cksms').is(":checked") ? true : false;
    let email = $('#ckemail').is(":checked") ? true : false;
    let props = {"notify.sms": sms, "notify.email": email};
    updateNotify(props);
}

function updateNotify(props) {
    $.ajax({
        url: "/readymix/setNotification",
        data: JSON.stringify(props),
        type: "post",
        headers: {"Content-Type": "application/json"},
        success: function (data) {
            console.log(data);
            location.reload();
            $('#list-notification').load(document.URL + ' #list-notification');
            $('#list-tab a[href="#list-notification-list"]').tab('show');
        },
        error: function (e, st, err) {
            console.log(err);
        }
    });
}

function addProp() {
    let prop = prompt("Please enter key=value pairs to prop");
    $.ajax({
        url: '/rtmix/addprop/' + prop,
        type: 'POST',
        headers: {"Content-Type": "Application/json"},
        data: null,
        onsuccess: function (dat) {
            console.log("properties processed, with result:" + dat);
            $('#list-settings').load(document.URL + '#list-settings');
        },
        onerror: function (e, s, err) {
            console.log("Error encountered: " + err);
        }
    });
}

$('#list-tab a').on('click', function (e) {
    e.preventDefault()
    $(this).tab('show')
});

function adduser() {
    actuserid = -1;
    $("#curuid").val("");
    $("#curuname").val("");
    $("#cururole").val("");
    $("#curumobile").val("");
    $("#curuemail").val("");
    $("#curupassword").val();
    $("#curuconpassword").val();
    $("#usercol").show();
}

function showuser(userid) {
    actuserid = userid;
    $("#curuid").val(actuserid);
    $("#curuname").val($("#curuname_" + actuserid).val());
    $("#cururole").val($("#curole_" + actuserid).val());
    $("#curumobile").val($("#curphone_" + actuserid).val());
    $("#curuemail").val($("#curemail_" + actuserid).val());
    $("#curupassword").val("onlytesting");
    $("#curuconpassword").val();
    $("#usercol").show();
}

function onshowpwd(contid) {
    let shown = document.getElementById(contid).type === 'text';
    document.getElementById(contid).type = shown ? 'password' : 'text';
}

function onUserman(dat) {
    // location.reload();
    $("#usercol").hide();
}