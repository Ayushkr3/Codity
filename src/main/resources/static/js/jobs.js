const queueId = localStorage.getItem("queueId");

document.getElementById("queueId").textContent = queueId;

async function createJob() {

    const body = {

        name: document.getElementById("jobName").value,

        payload: JSON.parse(
            document.getElementById("payload").value
        ),

        priority: Number(
            document.getElementById("priority").value
        ),

        delay: Number(
            document.getElementById("delay").value
        )

    };

    const result = await request(
        "/queues/" + queueId + "/jobs",
        "POST",
        body
    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    loadJobs();

}

async function createBatch() {

    const body = JSON.parse(
        document.getElementById("batchPayload").value
    );

    const result = await request(
        "/queues/" + queueId + "/jobs/batch",
        "POST",
        body
    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    loadJobs();

}

async function loadJobs() {

    const result = await request(
        "/queues/" + queueId + "/jobs"
    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    const table = document.getElementById("jobsTable");

    table.innerHTML = "";

    result.forEach(job => {

        table.innerHTML += `

        <tr>

        <td>${job.id}</td>

        <td>${job.name}</td>

        <td>${job.status}</td>

        <td>${job.priority}</td>

        <td>

        <button onclick="viewJob('${job.id}')">

        View

        </button>

        <button onclick="logs('${job.id}')">

        Logs

        </button>

        <button onclick="executions('${job.id}')">

        Executions

        </button>

        <button onclick="retryJob('${job.id}')">

        Retry

        </button>

        <button onclick="deleteJob('${job.id}')">

        Delete

        </button>

        </td>

        </tr>

        `;

    });

}

async function viewJob(id) {

    const result = await request(
        "/queues/" + queueId + "/jobs/" + id
    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

}

async function logs(id) {

    const result = await request(
        "/queues/" + queueId + "/jobs/" + id + "/logs"
    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

}

async function executions(id) {

    const result = await request(
        "/queues/" + queueId + "/jobs/" + id + "/executions"
    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

}

async function retryJob(id) {

    const result = await request(
        "/queues/" + queueId + "/jobs/" + id + "/retry",
        "POST"
    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    loadJobs();

}

async function deleteJob(id) {

    const result = await request(
        "/queues/" + queueId + "/jobs/" + id,
        "DELETE"
    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    loadJobs();

}

loadJobs();