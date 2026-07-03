// ---------------------------------------------------------------------
// Shared helpers used by every page. Backend is the Spring Boot job
// scheduler API; all endpoints are under BASE_URL below.
// ---------------------------------------------------------------------

const BASE_URL = "http://localhost:8080/api";

// ---------- auth / token ----------
function getToken() { return localStorage.getItem("jwt"); }
function getUserName() { return localStorage.getItem("userName") || ""; }

function saveAuth(data) {
    localStorage.setItem("jwt", data.token);
    localStorage.setItem("userName", data.name || "");
}

function logout() {
    localStorage.removeItem("jwt");
    localStorage.removeItem("userName");
    window.location.href = "index.html";
}

// Call at the top of every page except index.html / register.html.
function requireAuth() {
    if (!getToken()) {
        window.location.href = "index.html";
    }
}

// ---------- fetch wrapper ----------
// Returns parsed JSON (or null for 204s). Throws an Error with a readable
// message on failure so callers can show it in an .error-box.
async function apiFetch(path, method, body) {
    const opts = { method: method || "GET", headers: {} };
    const token = getToken();
    if (token) opts.headers["Authorization"] = "Bearer " + token;
    if (body !== undefined && body !== null) {
        opts.headers["Content-Type"] = "application/json";
        opts.body = JSON.stringify(body);
    }

    let res;
    try {
        res = await fetch(BASE_URL + path, opts);
    } catch (e) {
        throw new Error("Could not reach the API at " + BASE_URL + " - is the server running?");
    }

    if (res.status === 401 || res.status === 403) {
        // Token missing/expired/invalid, or user doesn't own this resource.
        if (res.status === 401) {
            logout();
            return null;
        }
        const text = await res.text();
        throw new Error(parseErrorMessage(text) || "You don't have access to this resource.");
    }

    if (res.status === 204) return null;

    const text = await res.text();
    if (!res.ok) {
        throw new Error(parseErrorMessage(text) || ("Request failed with status " + res.status));
    }
    return text ? JSON.parse(text) : null;
}

function parseErrorMessage(text) {
    try {
        const parsed = JSON.parse(text);
        return parsed.message || null;
    } catch (e) {
        return null;
    }
}

// ---------- UI helpers ----------
function showError(el, err) {
    el.textContent = err.message || String(err);
    el.className = "error-box";
    el.style.display = "block";
}

function showSuccess(el, msg) {
    el.textContent = msg;
    el.className = "success-box";
    el.style.display = "block";
}

function hideBox(el) {
    el.style.display = "none";
}

function fmtDate(iso) {
    if (!iso) return "-";
    return iso.replace("T", " ").slice(0, 19);
}

function qs(name) {
    return new URLSearchParams(window.location.search).get(name);
}

// ---------- nav bar ----------
function renderNav(active) {
    const items = [
        ["dashboard.html", "Dashboard"],
        ["projects.html", "Projects"],
        ["workers.html", "Workers"],
        ["dlq.html", "Dead Letter Queue"]
    ];
    const links = items.map(([href, label]) =>
        `<a href="${href}" class="${active === href ? 'active' : ''}">${label}</a>`
    ).join("");

    const nav = document.createElement("header");
    nav.className = "topnav";
    nav.innerHTML = `
        <div class="brand">Job Scheduler</div>
        <nav>${links}
            <button onclick="logout()">Log out (${getUserName()})</button>
        </nav>`;
    document.body.insertBefore(nav, document.body.firstChild);
}

// A handful of ready-made cron expressions for the recurring-jobs form,
// since hand-writing 6-field cron syntax is the main friction point there.
const CRON_PRESETS = [
    ["", "Custom..."],
    ["0 * * * * *", "Every minute"],
    ["0 0/5 * * * *", "Every 5 minutes"],
    ["0 0/15 * * * *", "Every 15 minutes"],
    ["0 0 * * * *", "Every hour"],
    ["0 0 0 * * *", "Every day at midnight"],
    ["0 0 2 * * *", "Every day at 2 AM"],
    ["0 0 9 * * MON-FRI", "Weekdays at 9 AM"],
    ["0 0 0 1 * *", "First of every month"]
];
