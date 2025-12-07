async function loadGraph() {

    if (window.tempChartInstance) {
        window.tempChartInstance.destroy();
    }

    console.log("Loading graph")

    let response = await fetch("http://localhost:9080/ui/daily-summary/all");
    let data = await response.json();

    let points = [];

    for (let day of data) {
        if (!day.temperatureData) continue;

        let temp = Number(day.temperatureData.temperature);
        let count = Number(day.totalIncidents);

        if (isNaN(temp) || isNaN(count)) continue;

        points.push({
            x: temp,
            y: count,
            date: day.date
        });
    }


    function computeRegression(points) {
        let n = points.length;
        let sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (let p of points) {
            sumX += p.x;
            sumY += p.y;
            sumXY += p.x * p.y;
            sumX2 += p.x * p.x;
        }

        let denominator = (n * sumX2 - sumX * sumX);
        if (denominator === 0) return null;

        let m = (n * sumXY - sumX * sumY) / denominator;
        let b = (sumY - m * sumX) / n;

        let minX = Math.min(...points.map(p => p.x));
        let maxX = Math.max(...points.map(p => p.x));

        return [
            { x: minX, y: m * minX + b },
            { x: maxX, y: m * maxX + b }
        ];
    }

    let regressionLine = computeRegression(points);

    // 2d context to prevent infininte resizing
    const canvas = document.getElementById("tempChart");
    const ctx = canvas.getContext("2d");

    canvas.height = 500;
    canvas.style.height = "500px";

    const datasets = [
        {
            label: "Incidents vs Temperature",
            data: points,
            backgroundColor: "rgba(80,130,255,0.35)", 
            pointRadius: 3,                            
            hoverRadius: 7,
            order: 0
        }
    ];

    if (regressionLine) {
        datasets.push({
            label: "Regression Line",
            data: regressionLine,
            type: "line",
            borderColor: "rgba(255,0,0,1)", 
            borderWidth: 4,                  
            pointRadius: 0,
            fill: false,
            tension: 0,
            order: 999
        });
    }

    window.tempChartInstance = new Chart(ctx, {
        type: "scatter",
        data: { datasets },
        options: {
            responsive: false,            
            maintainAspectRatio: false,   

            datasets: {
                scatter: { order: 0 },
                line: { order: 999 }
            },

            scales: {
                x: { title: { display: true, text: "Temperature (°C)" }},
                y: { title: { display: true, text: "Total Incidents" }}
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: ctx => {
                            const p = ctx.raw;
                            return `Date: ${p.date} | Temp: ${p.x}°C | Incidents: ${p.y}`;
                        }
                    }
                }
            }
        }
    });
}

window.onload = loadGraph;
