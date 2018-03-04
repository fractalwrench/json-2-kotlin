function validateForm() {
    var json = document.forms["jsonForm"]["json"].value;
    try {
        JSON.parse(json);
    } catch (e) {
        alert("Input was not interpreted as valid JSON.");
        return false;
    }
}

function copyToClipboard() {
    var kotlinSource = document.getElementById("kotlinText");
    kotlinSource.select();
    document.execCommand("Copy");
    disableCopyBtn();
}

function disableCopyBtn() {
    var btn = document.getElementById("copyBtn");
    btn.disabled = true;
    btn.value = "Copied";
    setTimeout(enableCopyBtn, 1000);
}

function enableCopyBtn() {
    var btn = document.getElementById("copyBtn");
    btn.disabled = false;
    btn.value = "Copy to Clipboard";
}

See: https://stackoverflow.com/a/18197341/5144991
function downloadAsFile() {
    var filename = document.getElementById("filename").value;
    var text = document.getElementById("kotlinText").value;

    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
}
