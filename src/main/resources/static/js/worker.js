async function loadWorkers() {

    const result = await request("/workers");

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    const table = document.getElementById("table");

    table.innerHTML = "";

    result.forEach(worker => {

        table.innerHTML += `

        <tr>

        <td>${worker.id}</td>

        <td>${worker.name}</td>

        <td>${worker.status}</td>

        <td>${worker.queueName}</td>

        </tr>

        `;

    });

}

loadWorkers();