async function createProject() {

    const body = {

        name: document.getElementById("projectName").value,

        description: document.getElementById("projectDescription").value

    };

    const result = await request("/projects", "POST", body);

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    loadProjects();

}

async function loadProjects() {

    const result = await request("/projects");

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    const table = document.getElementById("projectsTable");

    table.innerHTML = "";

    result.forEach(project => {

        table.innerHTML += `

        <tr>

        <td>${project.id}</td>

        <td>${project.name}</td>

        <td>${project.description}</td>

        <td>

        <button onclick="selectProject('${project.id}')">

        Select

        </button>

        </td>

        </tr>

        `;

    });

}

function selectProject(id) {

    localStorage.setItem("projectId", id);

    alert("Project Selected");

}

loadProjects();