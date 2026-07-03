async function login() {

    const result = await request("/auth/login", "POST", {

        email: document.getElementById("email").value,

        password: document.getElementById("password").value

    });

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

    if (result.token) {
        localStorage.setItem("token", result.token);
        alert("Logged in");
    }

}

async function register() {

    const result = await request("/auth/register", "POST", {

        name: document.getElementById("name").value,

        email: document.getElementById("email").value,

        password: document.getElementById("password").value

    });

    document.getElementById("output").textContent =
        JSON.stringify(result, null, 2);

}