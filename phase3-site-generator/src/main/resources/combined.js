async function loadCombined() {
    let date = document.getElementById("dateInput").value;
    if (!date) {
        alert("Please enter a date.");
        return;
    }

    let response = await fetch("http://localhost:9080/ui/combined?date=" + date);
    let data = await response.json();

    let html = "<h3>Results for " + date + "</h3>";

    if (!Array.isArray(data) || data.length === 0) {
        html += "<p>No incidents found.</p>";
        document.getElementById("output").innerHTML = html;
        return;
    }

    html += "<ul>";
    for (let item of data) {
        let inc = item.incident;
        let temp = item.temperature;

        html += `
          <li>
            <strong>Incident:</strong><br>
            Victim Sex: ${inc.victim.sex}<br>
            Victim Age Range: ${inc.victim.ageRange}<br>
            Perp Sex: ${inc.perpetrator.sex}<br>
            Perp Age Range: ${inc.perpetrator.ageRange}<br>
            Borough: ${inc.borough.name}<br>
            Date: ${inc.occurDate}<br>
            <strong>Temperature:</strong> ${temp.temperature}°C (${temp.date})
            <br><br>
          </li>
        `;
    }
    html += "</ul>";

    document.getElementById("output").innerHTML = html;
}
