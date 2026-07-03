const projectId = localStorage.getItem("projectId");

document.getElementById("currentProject").textContent = projectId;

async function createQueue() {

    const body = {

        name: document.getElementById("queueName").value

    };

    const result = await request(

        "/projects/" + projectId + "/queues",

        "POST",

        body

    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    loadQueues();

}

async function loadQueues() {

    const result = await request(

        "/projects/" + projectId + "/queues"

    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    const table = document.getElementById("queueTable");

    table.innerHTML = "";

    result.forEach(queue => {

        table.innerHTML += `

        <tr>

        <td>${queue.id}</td>

        <td>${queue.name}</td>

        <td>${queue.status}</td>

        <td>

        <button onclick="pauseQueue('${queue.id}')">

        Pause

        </button>

        <button onclick="resumeQueue('${queue.id}')">

        Resume

        </button>

        <button onclick="stats('${queue.id}')">

        Stats

        </button>

        <button onclick="selectQueue('${queue.id}')">

        Select

        </button>

        </td>

        </tr>

        `;

    });

}

async function pauseQueue(id) {

    const result = await request(

        "/queues/" + id + "/pause",

        "POST"

    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    loadQueues();

}

async function resumeQueue(id) {

    const result = await request(

        "/queues/" + id + "/resume",

        "POST"

    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    loadQueues();

}

async function stats(id) {

    const result = await request(

        "/queues/" + id + "/stats"

    );

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

}

function selectQueue(id) {

    localStorage.setItem("queueId", id);

    alert("Queue Selected");

}

loadQueues();