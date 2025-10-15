let current_folder_id;

function select_folder(address, folder){
    if (current_folder_id != null) document.getElementById(current_folder_id).classList.remove("current_item")
    current_folder_id = folder
    document.getElementById(folder).classList.add("current_item")

    let url = new URL("http://" + window.location.host + "/mail-list" )
    url.searchParams.set("address", address)
    url.searchParams.set("folder", folder)

    document.getElementById("frame").src = url.href;
}