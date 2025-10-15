let current_mail_id;

function select_mail(id){
    if (current_mail_id != null) document.getElementById(current_mail_id).classList.remove("current_mail")
    document.getElementById(id).classList.add("current_mail")
    current_mail_id = id

    let current_mail_url = new URL("http://" + window.location.host + "/mail")
    current_mail_url.searchParams.set("id", current_mail_id)

    document.getElementById("frame").src = current_mail_url.href;
}