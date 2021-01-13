const LOGGED_COOKIE = "quarkus-credential";
function onlogout() {
    var conf = confirm('Are you sure you want to check out from the system?');
    if (conf) {
        console.log("logging out")
        document.cookie = LOGGED_COOKIE + '=; Max-Age=0'
        window.location.href = "/login.html";
    }
}
