
const state = {
    token: localStorage.getItem('token'),
    projectId: null,
}


const $ = (sel) => document.querySelector(sel);

async function api(path, options = {}) {
    const headers = {'Content-Type': 'application/json'};
    if (state.token) headers.Authorization = `Bearer ${state.token}`;
    const res = await fetch(`/api${path}`, {...options, headers});
    if (res.status === 401 && state.token) {
        logout()
        throw new Error('Session expired, please log in again');
    }
    if (!res.ok) {
        const problem = await res.json().catch(() => ({}));
        throw new Error(problem.detail || problem.title || `Request failed (${res.status})`);
    }
    return res.status === 204 ? null : res.json();
}

function toast(message) {
    const el = $('#toast');
    el.textContent = message
    el.hidden = false
    clearTimeout(toast.timer);
    toast.timer = setTimeout(() => (el.hidden = true), 4000);
}

async function run(fn) {
    try {
        await fn();
    } catch (e) {
        toast(e.message)
    }
}

// auth

$('#auth-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const form = new FormData(e.target);
    const action = e.submitter.dataset.action
    run(async () => {
        if (action === 'register') {
            await api('/auth/register', {
                method: 'POST',
                body: JSON.stringify({
                    email: form.get('email'),
                    displayName: form.get('displayName') || form.get('email'),
                    password: form.get('password'),
                }),
            })
        }
        const {accessToken} = await api('/auth/login', {
            method: 'POST',
            body: JSON.stringify({email: form.get('email'), password: form.get('password')}),
        })
        state.token = accessToken;
        localStorage.setItem('token', accessToken);
        await start();
    })
})


$('#logout').addEventListener('click', logout);


function logout() {
    state.token = null;
    localStorage.removeItem('token');
    $('#app').hidden = true
    $('#user-bar').hidden = true;
    $('#auth').hidden = false;
}

// projects

async function loadProjects(selectId) {
    const projects = await api('/projects');
    const select = $('#project-select');
    select.innerHTML = '';
    for (const p of projects) {
        select.append(new Option(p.name, p.id));
    }
    state.projectId = selectId ?? projects[0]?.id ?? null;
    if (state.projectId) select.value = state.projectId;
    await loadBoard();
}


$('#project-select').addEventListener('change', (e) => {
    state.projectId = e.target.value;
    run(loadBoard)
})

$('#new-project').addEventListener('submit', (e) => {
    e.preventDefault();
    const name = e.target.name.value;
    run(async () => {
        const project = await api('/projects', {method: 'POST', body: JSON.stringify({name})});
        // default colums
        for (const column of ['To do', 'In progress', 'Done']) {
            await api(`/projects/${project.id}/columns`, {method: 'POST', body: JSON.stringify({name: column})});
        }
        e.target.reset();
        await loadProjects(project.id);
    })
})
$('#new-column').addEventListener('submit', (e) => {
    e.preventDefault();
    if (!state.projectId) return toast('Create a project first');
    const body = {name: e.target.name.value, wipLimit: e.target.wipLimit.value || null};
    run(async () => {
        await api(`/projects/${state.projectId}/columns`, {method: 'POST', body: JSON.stringify(body)});
        e.target.reset();
        await loadBoard();
    })
});


// board

async function loadBoard() {
    const boardEl = $('#board');
    boardEl.innerHTML = '';
    if (!state.projectId) return;

    const board = await api(`/projects/${state.projectId}/board`)
    for (const column of board.columns) {
        boardEl.append(renderColumn(column));
    }
}

function renderColumn(column) {
    const el = document.createElement('div');
    el.className = 'column'
    const limit = column.wipLimit ? ` / ${column.wipLimit}` : '';
    if (column.wipLimit && column.tasks.length >= column.wipLimit) el.classList.add('over-limit');

    const title = document.createElement('h3');
    title.textContent = column.name;
    const count = document.createElement('small')
    count.textContent = `${column.tasks.length}${limit}`
    title.append(count);

    const list = document.createElement('div');
    list.className = 'tasks'
    for (const task of column.tasks) list.append(renderTask(task));
    makeDropTarget(list, column.id)

    const add = document.createElement('form');
    add.className = 'add-task';
    add.innerHTML = '<input name="title" placeholder="Add a task" required><button>+</button>';
    add.addEventListener('submit', (e) => {
        e.preventDefault();
        run(async () => {
            await api(`/columns/${column.id}/tasks`, {
                method: 'POST',
                body: JSON.stringify({title: add.title.value}),
            })
            await loadBoard();
        })
    })


    el.append(title, list, add);
    return el;
}

function renderTask(task) {
    const el = document.createElement('div');
    el.className = `task ${task.priority}`;
    el.draggable = true;
    el.dataset.id = task.id;

    const title = document.createElement('div');
    title.textContent = task.title;
    const meta = document.createElement('div')
    meta.className = 'meta';
    meta.textContent = [task.priority, task.assignee?.displayName, task.dueDate && `due ${task.dueDate}`]
        .filter(Boolean)
        .join(' · ')
    el.append(title, meta);


    el.addEventListener('dragstart', (e) => e.dataTransfer.setData('text/plain', task.id));
    return el;
}

function makeDropTarget(list, columnId) {
    list.addEventListener('dragover', (e) => {
        e.preventDefault();
        list.classList.add('drag-over')
    })
    list.addEventListener('dragleave', () => list.classList.remove('drag-over'));
    list.addEventListener('drop', (e) => {
        e.preventDefault();
        list.classList.remove('drag-over')
        const taskId = e.dataTransfer.getData('text/plain');
        const position = dropPosition(list, e.clientY, taskId);
        run(async () => {
            try {
                await api(`/tasks/${taskId}/move`, {
                    method: 'PATCH',
                    body: JSON.stringify({columnId, position}),
                })
            } finally {
                await loadBoard();
            }
        })
    })
}

// drop index
function dropPosition(list, y, draggedId) {
    const cards = [...list.querySelectorAll('.task')].filter((c) => c.dataset.id !== draggedId);
    const index = cards.findIndex((card) => {
        const box = card.getBoundingClientRect();
        return y < box.top + box.height / 2;
    })
    return index === -1 ? cards.length : index;
}

// init


async function start() {
    const me = await api('/auth/me');
    $('#user-name').textContent = me.displayName
    $('#auth').hidden = true;
    $('#app').hidden = false;
    $('#user-bar').hidden = false;
    await loadProjects();
}

if (state.token) run(start);
