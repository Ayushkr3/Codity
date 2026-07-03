const queueId = localStorage.getItem("queueId");

document.getElementById("queueId").textContent = queueId;

async function createRecurring() {

    const body = {

        name: document.getElementById("name").value,

        cron: document.getElementById("cron").value,

        payload: JSON.parse(
            document.getElementById("payload").value
        )

    };

    const result = await request(
        "/queues/" + queueId + "/recurring-jobs",
        "POST",
        body
    );

    output(result);

    loadRecurring();

}

async function loadRecurring() {

    const result = await request(
        "/queues/" + queueId + "/recurring-jobs"
    );

    output(result);

    const table = document.getElementById("table");

    table.innerHTML = "";

    result.forEach(job => {

        table.innerHTML += `

        <tr>

        <td>${job.id}</td>

        <td>${job.name}</td>

        <td>${job.cron}</td>

        <td>

        <button onclick="pauseRecurring('${job.id}')">

        Pause

        </button>

        <button onclick="resumeRecurring('${job.id}')">

        Resume

        </button>

        </td>

        </tr>

        `;

    });

}

async function pauseRecurring(id) {

    const result = await request(
        "/queues/" + queueId + "/recurring-jobs/" + id + "/pause",
        "POST"
    );

    output(result);

    loadRecurring();

}

async function resumeRecurring(id) {

    const result = await request(
        "/queues/" + queueId + "/recurring-jobs/" + id + "/resume",
        "POST"
    );

    output(result);

    loadRecurring();

}

function output(data) {

    document.getElementById("output").textContent =
        JSON.stringify(data, null, 2);

}

loadRecurring();