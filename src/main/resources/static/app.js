

$(document).ready(function() {
    $("#submitbutton").click(function() {
        $("#infoMessage").empty();
        $("#errorMessage").empty();
        $.ajax("/vedtak", {
            data: JSON.stringify({
                "aktorId": $("#aktorId").val(),
                "arbeidsgiverId": $("#arbeidsgiverId").val(),
                "fom": $("#fom").val(),
                "tom": $("#tom").val(),
                "dagsats": $("#dagsats").val()
            }),
            contentType: "application/json",
            type: "POST",
            success: function (result) {
                $("#infoMessage").html("OK - Sendt:<br>--------<br>" + JSON.stringify(result));
            },
            error: function (xhr, status, error) {
                $("#errorMessage").html("Feil: " + error);
            }
        });
    });
});
