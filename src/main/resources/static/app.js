
function sendTulleVedtak() {
    document.getElementById("infoMessage").innerHTML = "";
    document.getElementById("errorMessage").innerHTML = "";

    fetch("/vedtak", {
        method: 'POST',
        body: JSON.stringify({
            "aktorId": document.getElementById("aktorId").value,
            "arbeidsgiverId": document.getElementById("arbeidsgiverId").value,
            "fom": document.getElementById("fom").value,
            "tom": document.getElementById("tom").value,
            "dagsats": document.getElementById("dagsats").value,
            "spamPassord": document.getElementById("spamPassord").value,
            "soknadId": document.getElementById("soknadId").value
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(function(response) {
        if (response.ok) {
            response.json().then(function(jsonresp) {
                console.log(jsonresp);
                console.log('Success:', JSON.stringify(jsonresp));
                document.getElementById("infoMessage").innerHTML = "OK - Sendt:<br>--------<br>" + JSON.stringify(jsonresp);
            });
        } else {
            response.text().then(function(textResp) {
                document.getElementById("errorMessage").innerHTML = "Feil: " + response.status + " - " + response.statusText +
                    " - " + textResp;
            });

        }
    });
}

window.addEventListener('DOMContentLoaded', function() {
    console.log("DOMContentReady");
    document.getElementById("submitbutton").addEventListener("click", sendTulleVedtak);
});

