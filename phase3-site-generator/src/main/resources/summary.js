async function loadSummary() {
    let date = document.getElementById("dateInput").value;
    if (!date) {
        alert("Please enter a date.");
        return;
    }

    let response = await fetch("http://localhost:9080/ui/daily-summary?date=" + date);
    let json = await response.json();

    let html = `
        <h3>Date: ${json.date}</h3>
        <p><strong>Total Incidents:</strong> ${json.totalIncidents}</p>
        <h4>Perpetrators by Sex</h4>${formatMap(json.perpsBySex)}
        <h4>Victims by Sex</h4>${formatMap(json.vicsBySex)}
        <h4>Perpetrator Age Ranges</h4>${formatMap(json.perpsByAgeRange)}
        <h4>Victim Age Ranges</h4>${formatMap(json.vicsByAgeRange)}
        <h4>Temperature</h4><p><strong>${json.temperatureData.temperature}°C</strong></p>
    `;

    document.getElementById("output").innerHTML = html;
}

function formatMap(obj) {
    if (!obj || Object.keys(obj).length === 0) return "<p>No data.</p>";

    let list = "<ul>";
    for (const key in obj) list += `<li><strong>${key}</strong>: ${obj[key]}</li>`;
    return list + "</ul>";
}
