async function loadDLQ() {

    const result = await request("/dlq");

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    const table = document.getElementById("table");

    table.innerHTML = "";

    result.forEach(job => {

        table.innerHTML += `

        <tr>

        <td>${job.id}</td>

        <td>${job.jobName}</td>

        <td>${job.reason}</td>

        <td>

        <button onclick="requeue('${job.id}')">

        Requeue

        </button>

        </td>

        </tr>

        `;

    });

}

async function requeue(id) {

    const result = await request(

        "/dlq/" + id + "/requeue",

        "POST"

    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    loadDLQ();

}

loadDLQ();