/**
 * TaskFlow - Interactive Single Page REST API Client
 */

const API_BASE_URL = 'http://localhost:8085';

document.addEventListener('DOMContentLoaded', () => {
  // State
  let currentUser = null;
  let jwtToken = localStorage.getItem('taskflow_token');
  let activeTab = 'dashboard-tab';
  let activeTaskFilter = 'ALL';
  let selectedTaskIdForComments = null;

  // DOM Elements
  const authModal = document.getElementById('auth-modal');
  const loginForm = document.getElementById('login-form');
  const loginEmail = document.getElementById('login-email');
  const loginPassword = document.getElementById('login-password');

  const profileName = document.getElementById('profile-name');
  const profileRole = document.getElementById('profile-role');
  const avatarCircle = document.getElementById('avatar-circle');
  const btnLogout = document.getElementById('btn-logout-trigger');
  const btnOpenLogin = document.getElementById('btn-open-login');

  const btnNewProject = document.getElementById('btn-new-project');
  const btnNewTask = document.getElementById('btn-new-task');
  const btnDownloadReport = document.getElementById('btn-download-report');
  const navProjects = document.getElementById('nav-projects');
  const navSmartAlloc = document.getElementById('nav-smart-alloc');
  const navReports = document.getElementById('nav-reports');
  const navUsers = document.getElementById('nav-users');

  const activeTabHeading = document.getElementById('active-tab-heading');
  const themeToggleBtn = document.getElementById('theme-toggle-btn');
  const toastContainer = document.getElementById('toast-container');

  // Initialization
  initTheme();
  checkAuthStatus();

  // --- Theme Handler ---
  function initTheme() {
    const savedTheme = localStorage.getItem('taskflow_theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);
  }

  themeToggleBtn.addEventListener('click', () => {
    const current = document.documentElement.getAttribute('data-theme');
    const next = current === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', next);
    localStorage.setItem('taskflow_theme', next);
    updateThemeIcon(next);
  });

  function updateThemeIcon(theme) {
    themeToggleBtn.innerHTML = theme === 'dark'
      ? '<i class="fa-solid fa-moon"></i>'
      : '<i class="fa-solid fa-sun"></i>';
  }

  // --- Auth Helper (Fetch Wrapper) ---
  async function authFetch(url, options = {}) {
    options.headers = options.headers || {};
    if (jwtToken) {
      options.headers['Authorization'] = `Bearer ${jwtToken}`;
    }

    // Prepend API_BASE_URL if url is a relative path
    const fullUrl = url.startsWith('http') ? url : `${API_BASE_URL}${url}`;

    try {
      const res = await fetch(fullUrl, options);

      if (res.status === 401) {
        showToast('Session expired or unauthorized. Please login.', 'error');
        logout();
        throw new Error('Unauthorized');
      }

      if (!res.ok) {
        const errorData = await res.json().catch(() => ({ message: res.statusText }));
        throw new Error(errorData.message || errorData.error || 'API Request Failed');
      }

      return await res.json();
    } catch (err) {
      console.error('API Error:', err);
      throw err;
    }
  }

  // --- Auth Status Check & User Setup ---
  async function checkAuthStatus() {
    if (!jwtToken) {
      showAuthModal();
      return;
    }

    try {
      currentUser = await authFetch('/api/users/me');
      updateUserUI();
      hideAuthModal();
      loadActiveTabData();
    } catch (err) {
      logout();
    }
  }

  function updateUserUI() {
    if (!currentUser) return;
    profileName.textContent = currentUser.name;
    profileRole.textContent = currentUser.role;
    profileRole.className = `user-role-badge role-${currentUser.role.toLowerCase()}`;
    avatarCircle.textContent = currentUser.name.charAt(0).toUpperCase();

    // Role-based visibility
    const isAdmin = currentUser.role === 'ADMIN';
    const isManager = currentUser.role === 'MANAGER';

    if (navProjects) {
      navProjects.style.display = (isAdmin || isManager) ? 'flex' : 'none';
    }
    btnNewProject.style.display = isAdmin ? 'inline-flex' : 'none';
    btnNewTask.style.display = (isAdmin || isManager) ? 'inline-flex' : 'none';
    navSmartAlloc.style.display = (isAdmin || isManager) ? 'flex' : 'none';
    navReports.style.display = (isAdmin || isManager) ? 'flex' : 'none';
    navUsers.style.display = isAdmin ? 'flex' : 'none';
    btnDownloadReport.style.display = (isAdmin || isManager) ? 'inline-flex' : 'none';
    btnOpenLogin.style.display = 'none';
  }

  function showAuthModal() {
    authModal.classList.add('active');
  }

  function hideAuthModal() {
    authModal.classList.remove('active');
  }

  function logout() {
    jwtToken = null;
    currentUser = null;
    localStorage.removeItem('taskflow_token');
    profileName.textContent = 'Not Logged In';
    profileRole.textContent = 'GUEST';
    profileRole.className = 'user-role-badge role-developer';
    avatarCircle.textContent = 'G';
    showAuthModal();
  }

  btnLogout.addEventListener('click', logout);
  btnOpenLogin.addEventListener('click', showAuthModal);

  // Login Form Submit
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const email = loginEmail.value ? loginEmail.value.trim() : '';
      const password = loginPassword.value ? loginPassword.value.trim() : '';

      const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: email,
          password: password
        })
      });

      if (!res.ok) throw new Error('Invalid email or password');
      const data = await res.json();

      jwtToken = data.token || data.accessToken;
      localStorage.setItem('taskflow_token', jwtToken);
      currentUser = data.user;

      showToast(`Welcome back, ${currentUser.name}!`, 'success');
      updateUserUI();
      hideAuthModal();
      loadActiveTabData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // --- Navigation & Tabs ---
  document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', (e) => {
      e.preventDefault();
      document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));

      item.classList.add('active');
      activeTab = item.getAttribute('data-tab');
      document.getElementById(activeTab).classList.add('active');

      const tabTitles = {
        'dashboard-tab': 'Dashboard Analytics',
        'projects-tab': 'Projects Management',
        'tasks-tab': 'Issue & Task Board',
        'smart-alloc-tab': 'Smart Developer Allocation',
        'reports-tab': 'Developer Performance Reports',
        'users-tab': 'User Management & Roles'
      };
      activeTabHeading.textContent = tabTitles[activeTab] || 'Dashboard';

      loadActiveTabData();
    });
  });

  function loadActiveTabData() {
    if (!currentUser) return;
    if (activeTab === 'dashboard-tab') loadDashboardData();
    else if (activeTab === 'projects-tab') loadProjectsData();
    else if (activeTab === 'tasks-tab') loadTasksData();
    else if (activeTab === 'smart-alloc-tab') loadSmartAllocationData();
    else if (activeTab === 'reports-tab') loadReportsData();
    else if (activeTab === 'users-tab') loadUsersData();
  }

  // --- Dashboard Data ---
  async function loadDashboardData() {
    try {
      const data = await authFetch('/api/dashboard');
      document.getElementById('metric-projects').textContent = data.totalProjects;
      document.getElementById('metric-tasks').textContent = data.totalTasks;
      document.getElementById('metric-completed').textContent = data.completedTasks;
      document.getElementById('metric-pending').textContent = data.pendingTasks;
      document.getElementById('metric-testing').textContent = data.testingTasks;
      document.getElementById('metric-critical').textContent = data.criticalTasks;

      // Render task preview
      const tasks = await authFetch('/api/tasks');
      renderTaskGrid(tasks.slice(0, 4), document.getElementById('dashboard-task-preview'));
    } catch (err) {
      console.warn('Dashboard fetch error:', err);
    }
  }

  // --- Projects Data ---
  async function loadProjectsData() {
    try {
      const projects = await authFetch('/api/projects');
      const container = document.getElementById('projects-grid');
      container.innerHTML = '';

      if (projects.length === 0) {
        container.innerHTML = '<p style="color: var(--text-muted);">No projects found.</p>';
        return;
      }

      projects.forEach(p => {
        const card = document.createElement('div');
        card.className = 'task-card';
        card.innerHTML = `
          <div class="task-card-header">
            <div class="task-title"><i class="fa-solid fa-folder-open" style="color: var(--accent-primary);"></i> ${escapeHtml(p.title)}</div>
            <span class="status-badge status-DONE">Active</span>
          </div>
          <div class="task-desc">${escapeHtml(p.description || 'No description provided.')}</div>
          <div class="task-meta">
            <div class="task-assignee"><i class="fa-solid fa-user-tie"></i> Manager: ${escapeHtml(p.manager ? p.manager.name : 'Unassigned')}</div>
            <div>Created: ${new Date(p.createdAt).toLocaleDateString()}</div>
          </div>
        `;
        container.appendChild(card);
      });
    } catch (err) {
      showToast(err.message, 'error');
    }
  }

  // --- Tasks Data ---
  async function loadTasksData() {
    try {
      let endpoint = '/api/tasks';
      if (activeTaskFilter === 'MY_TASKS') {
        endpoint = '/api/tasks/my-tasks';
      }

      let tasks = await authFetch(endpoint);
      if (activeTaskFilter === 'CRITICAL') {
        tasks = tasks.filter(t => t.priority === 'CRITICAL');
      }

      renderTaskGrid(tasks, document.getElementById('tasks-grid'));
    } catch (err) {
      showToast(err.message, 'error');
    }
  }

  // Task Filter Buttons
  document.querySelectorAll('.task-filter-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.task-filter-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      activeTaskFilter = btn.getAttribute('data-filter');
      loadTasksData();
    });
  });

  function renderTaskGrid(tasks, container) {
    container.innerHTML = '';
    if (tasks.length === 0) {
      container.innerHTML = '<p style="color: var(--text-muted); grid-column: 1/-1;">No tasks found.</p>';
      return;
    }

    const isManagerOrAdmin = currentUser.role === 'ADMIN' || currentUser.role === 'MANAGER';

    tasks.forEach(t => {
      const card = document.createElement('div');
      card.className = 'task-card';

      const isAssignedToMe = t.assignee && t.assignee.id === currentUser.id;
      const canUpdateStatus = isManagerOrAdmin || isAssignedToMe;

      card.innerHTML = `
        <div class="task-card-header">
          <div class="task-title">${escapeHtml(t.title)}</div>
          <span class="priority-pill priority-${t.priority}">${t.priority}</span>
        </div>
        <div class="task-desc">${escapeHtml(t.description || 'No description.')}</div>

        <div style="display: flex; gap: 8px; align-items: center; justify-content: space-between;">
          <span class="status-badge status-${t.status}">${t.status}</span>
        </div>

        <div class="task-actions" style="justify-content: space-between; border-top: 1px solid var(--border-glass); padding-top: 10px;">
          <div>
            ${canUpdateStatus ? `
              <select class="select-input task-status-change" data-id="${t.id}">
                <option value="TODO" ${t.status === 'TODO' ? 'selected' : ''}>TODO</option>
                <option value="IN_PROGRESS" ${t.status === 'IN_PROGRESS' ? 'selected' : ''}>IN_PROGRESS</option>
                <option value="TESTING" ${t.status === 'TESTING' ? 'selected' : ''}>TESTING</option>
                <option value="DONE" ${t.status === 'DONE' ? 'selected' : ''}>DONE</option>
                <option value="CLOSED" ${t.status === 'CLOSED' ? 'selected' : ''}>CLOSED</option>
              </select>
            ` : ''}
          </div>

          <button class="btn btn-secondary btn-task-comments" data-id="${t.id}" data-title="${escapeHtml(t.title)}" style="padding: 6px 12px; font-size: 12px;">
            <i class="fa-solid fa-comments"></i> Logs & Comments
          </button>
        </div>
        <div class="task-metadata-footer">
          <div><i class="fa-solid fa-folder-open" aria-hidden="true"></i> Project: ${escapeHtml(t.projectName || (t.project && t.project.title) || 'Unknown project')}</div>
          <div><i class="fa-solid fa-user-gear" aria-hidden="true"></i> Assigned to: ${escapeHtml(t.assignedToName || (t.assignee && t.assignee.name) || 'Unassigned')}</div>
        </div>
      `;
      container.appendChild(card);
    });

    // Attach Event Listeners to status changes
    container.querySelectorAll('.task-status-change').forEach(select => {
      select.addEventListener('change', async (e) => {
        const taskId = e.target.getAttribute('data-id');
        const newStatus = e.target.value;
        try {
          await authFetch(`/api/tasks/${taskId}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
          });
          showToast(`Task status updated to ${newStatus}`, 'success');
          loadActiveTabData();
        } catch (err) {
          showToast(err.message, 'error');
        }
      });
    });

    // Attach Event Listeners to Comments modal trigger
    container.querySelectorAll('.btn-task-comments').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = btn.getAttribute('data-id');
        const title = btn.getAttribute('data-title');
        openCommentsModal(id, title);
      });
    });
  }

  // --- Smart Allocation Data ---
  async function loadSmartAllocationData() {
    try {
      const devAlloc = await authFetch('/api/tasks/smart-allocation');
      const container = document.getElementById('smart-alloc-grid');
      container.innerHTML = '';

      if (devAlloc.length === 0) {
        container.innerHTML = '<p style="color: var(--text-muted);">No eligible developers found with active tasks < 5.</p>';
        return;
      }

      devAlloc.forEach(item => {
        const card = document.createElement('div');
        card.className = 'dev-card';
        card.innerHTML = `
          <div style="display: flex; align-items: center; gap: 12px;">
            <div class="user-avatar" style="width: 44px; height: 44px;">${item.developer.name.charAt(0)}</div>
            <div>
              <div style="font-weight: 700; font-size: 15px;">${escapeHtml(item.developer.name)}</div>
              <div style="font-size: 12px; color: var(--text-muted);">${escapeHtml(item.developer.email)}</div>
            </div>
          </div>
          <div style="text-align: right;">
            <div class="dev-workload-badge">${item.activeTasksCount} Active Tasks</div>
            <div style="font-size: 11px; color: var(--status-done); margin-top: 4px; font-weight: 600;">Optimal Workload</div>
          </div>
        `;
        container.appendChild(card);
      });
    } catch (err) {
      showToast(err.message, 'error');
    }
  }

  // --- Users Data (Admin Only) ---
  async function loadUsersData() {
    try {
      const users = await authFetch('/api/users');
      const container = document.getElementById('users-grid');
      container.innerHTML = '';

      users.forEach(u => {
        const card = document.createElement('div');
        card.className = 'dev-card';
        card.innerHTML = `
          <div style="display: flex; align-items: center; gap: 12px;">
            <div class="user-avatar" style="width: 44px; height: 44px;">${u.name.charAt(0)}</div>
            <div>
              <div style="font-weight: 700; font-size: 15px;">${escapeHtml(u.name)}</div>
              <div style="font-size: 12px; color: var(--text-muted);">${escapeHtml(u.email)}</div>
            </div>
          </div>
          <span class="user-role-badge role-${u.role.toLowerCase()}">${u.role}</span>
        `;
        container.appendChild(card);
      });
    } catch (err) {
      showToast(err.message, 'error');
    }
  }

  // --- Developer Performance Reports ---
  async function loadReportsData() {
    try {
      const reports = await authFetch('/api/reports/developers');
      const tbody = document.getElementById('reports-table-body');
      const emptyMsg = document.getElementById('reports-empty-msg');
      tbody.innerHTML = '';

      if (!reports || reports.length === 0) {
        emptyMsg.style.display = 'block';
        return;
      }
      emptyMsg.style.display = 'none';

      reports.forEach(r => {
        const completionColor = r.completionRatePercent >= 75
          ? '#10b981'
          : r.completionRatePercent >= 40
            ? '#f59e0b'
            : '#ef4444';

        const tr = document.createElement('tr');
        tr.style.cssText = 'border-bottom: 1px solid var(--border-glass); transition: background 0.2s;';
        tr.onmouseenter = () => tr.style.background = 'var(--bg-primary)';
        tr.onmouseleave = () => tr.style.background = '';

        tr.innerHTML = `
          <td style="padding: 12px 16px; font-weight: 600; color: var(--text-muted);">#${r.developerId}</td>
          <td style="padding: 12px 16px; font-weight: 700;">${escapeHtml(r.developerName)}</td>
          <td style="padding: 12px 16px; color: var(--text-muted); font-size: 12px;">${escapeHtml(r.developerEmail)}</td>
          <td style="padding: 12px 16px; text-align: center; font-weight: 700; color: var(--accent-primary);">${r.totalTasksAssigned}</td>
          <td style="padding: 12px 16px; text-align: center; color: #f59e0b;">${r.inProgressTasks}</td>
          <td style="padding: 12px 16px; text-align: center; color: #10b981;">${r.completedTasks}</td>
          <td style="padding: 12px 16px; text-align: center;">
            <span style="font-weight: 700; color: ${completionColor};">${r.completionRatePercent.toFixed(1)}%</span>
          </td>
          <td style="padding: 12px 16px; text-align: center; color: var(--text-muted);">${r.avgResolutionTimeHours.toFixed(2)}h</td>
          <td style="padding: 12px 16px; text-align: center; color: var(--text-muted);">${r.avgResolutionTimeDays.toFixed(2)}d</td>
        `;
        tbody.appendChild(tr);
      });
    } catch (err) {
      showToast('Failed to load reports: ' + err.message, 'error');
    }
  }

  /**
   * Triggers an authenticated CSV download by fetching the blob
   * and creating a temporary anchor element.
   */
  async function downloadReportCsv() {
    try {
      showToast('Preparing report download...', 'success');
      const fullUrl = `${API_BASE_URL}/api/reports/developers/export`;
      const res = await fetch(fullUrl, {
        headers: { 'Authorization': `Bearer ${jwtToken}` }
      });

      if (!res.ok) {
        const err = await res.json().catch(() => ({ message: res.statusText }));
        throw new Error(err.message || 'Download failed');
      }

      // Extract filename from Content-Disposition header if available
      const disposition = res.headers.get('Content-Disposition') || '';
      let filename = 'taskflow_developer_report.csv';
      const match = disposition.match(/filename=([^;]+)/);
      if (match) filename = match[1].trim().replace(/"/g, '');

      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);

      showToast('Report downloaded successfully!', 'success');
    } catch (err) {
      showToast('Report download failed: ' + err.message, 'error');
    }
  }

  // Wire Download Report button (top-bar header)
  if (btnDownloadReport) {
    btnDownloadReport.addEventListener('click', downloadReportCsv);
  }

  // Wire Export CSV button inside Reports tab (set up after DOM loads)
  document.addEventListener('click', (e) => {
    if (e.target && (e.target.id === 'btn-export-report-csv' || e.target.closest('#btn-export-report-csv'))) {
      downloadReportCsv();
    }
  });

  // --- Modals Setup & Actions ---

  // Create User (Admin Only)
  const createUserModal = document.getElementById('create-user-modal');
  const createUserForm = document.getElementById('create-user-form');
  const btnCreateUserModal = document.getElementById('btn-create-user-modal');
  const userCreateSubmit = document.getElementById('user-create-submit');

  btnCreateUserModal.addEventListener('click', () => {
    createUserForm.reset();
    createUserModal.classList.add('active');
  });

  createUserForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    userCreateSubmit.disabled = true;

    try {
      await authFetch('/api/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: document.getElementById('user-name').value.trim(),
          email: document.getElementById('user-email').value.trim(),
          password: document.getElementById('user-password').value,
          role: document.getElementById('user-role').value
        })
      });

      showToast('User created successfully.', 'success');
      createUserModal.classList.remove('active');
      createUserForm.reset();
      await loadUsersData();
    } catch (err) {
      showToast(`Could not create user: ${err.message}`, 'error');
    } finally {
      userCreateSubmit.disabled = false;
    }
  });

  // 1. Create Project Modal
  const createProjectModal = document.getElementById('create-project-modal');
  const createProjectForm = document.getElementById('create-project-form');

  btnNewProject.addEventListener('click', async () => {
    // Populate managers
    const users = await authFetch('/api/users');
    const select = document.getElementById('proj-manager-select');
    select.innerHTML = '';
    users.filter(u => u.role === 'MANAGER' || u.role === 'ADMIN').forEach(m => {
      select.innerHTML += `<option value="${m.id}">${escapeHtml(m.name)} (${m.role})</option>`;
    });
    createProjectModal.classList.add('active');
  });

  createProjectForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      await authFetch('/api/projects', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          title: document.getElementById('proj-title').value,
          description: document.getElementById('proj-desc').value,
          managerId: document.getElementById('proj-manager-select').value
        })
      });
      showToast('Project created successfully!', 'success');
      createProjectModal.classList.remove('active');
      createProjectForm.reset();
      loadProjectsData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // 2. Create Task Modal
  const createTaskModal = document.getElementById('create-task-modal');
  const createTaskForm = document.getElementById('create-task-form');

  btnNewTask.addEventListener('click', async () => {
    // Populate Projects & Assignees
    const projects = await authFetch('/api/projects');
    const users = await authFetch('/api/users');

    const projSelect = document.getElementById('task-project-select');
    projSelect.innerHTML = projects.map(p => `<option value="${p.id}">${escapeHtml(p.title)}</option>`).join('');

    const assignSelect = document.getElementById('task-assignee-select');
    assignSelect.innerHTML = '<option value="">-- Unassigned --</option>' +
      users.filter(u => u.role === 'DEVELOPER').map(d => `<option value="${d.id}">${escapeHtml(d.name)}</option>`).join('');

    createTaskModal.classList.add('active');
  });

  createTaskForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      const assigneeId = document.getElementById('task-assignee-select').value;
      await authFetch('/api/tasks', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          title: document.getElementById('task-title').value,
          description: document.getElementById('task-desc').value,
          priority: document.getElementById('task-priority-select').value,
          projectId: document.getElementById('task-project-select').value,
          assigneeId: assigneeId ? parseInt(assigneeId) : null,
          dueDate: document.getElementById('task-duedate').value || null
        })
      });
      showToast('Task created successfully!', 'success');
      createTaskModal.classList.remove('active');
      createTaskForm.reset();
      loadActiveTabData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // Close modals
  document.querySelectorAll('.close-modal-btn, .modal-overlay').forEach(el => {
    el.addEventListener('click', (e) => {
      if (e.target === el || e.target.classList.contains('close-btn')) {
        document.querySelectorAll('.modal-overlay').forEach(m => m.classList.remove('active'));
      }
    });
  });

  // 3. Comments & Activity Modal
  const commentsModal = document.getElementById('comments-modal');
  const addCommentForm = document.getElementById('add-comment-form');

  async function openCommentsModal(taskId, taskTitle) {
    selectedTaskIdForComments = taskId;
    document.getElementById('comments-modal-title').textContent = `Task #${taskId}: ${taskTitle}`;

    loadCommentsAndLogs(taskId);
    commentsModal.classList.add('active');
  }

  async function loadCommentsAndLogs(taskId) {
    try {
      const comments = await authFetch(`/api/tasks/${taskId}/comments`);
      const logs = await authFetch(`/api/tasks/${taskId}/logs`);

      const commentsContainer = document.getElementById('comments-list');
      commentsContainer.innerHTML = '';

      if (comments.length === 0) {
        commentsContainer.innerHTML = '<p style="font-size: 12px; color: var(--text-dim);">No comments yet.</p>';
      } else {
        comments.forEach(c => {
          commentsContainer.innerHTML += `
            <div style="background: var(--bg-primary); padding: 10px; border-radius: var(--radius-sm); border: 1px solid var(--border-glass);">
              <div style="font-size: 12px; font-weight: 700; color: var(--accent-primary);">${escapeHtml(c.user ? c.user.name : 'Unknown')}</div>
              <div style="font-size: 13px; margin: 4px 0;">${escapeHtml(c.text)}</div>
              <div style="font-size: 10px; color: var(--text-dim);">${new Date(c.createdAt).toLocaleString()}</div>
            </div>
          `;
        });
      }

      const logsContainer = document.getElementById('activity-logs-list');
      logsContainer.innerHTML = '';
      logs.forEach(l => {
        logsContainer.innerHTML += `
          <div style="color: var(--text-muted);">
            <i class="fa-solid fa-clock-rotate-left" style="color: var(--text-dim);"></i> 
            <strong>[${new Date(l.timestamp).toLocaleTimeString()}]</strong> ${escapeHtml(l.message)}
          </div>
        `;
      });
    } catch (err) {
      showToast(err.message, 'error');
    }
  }

  addCommentForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!selectedTaskIdForComments) return;

    const textInput = document.getElementById('comment-text-input');
    try {
      await authFetch(`/api/tasks/${selectedTaskIdForComments}/comments`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text: textInput.value })
      });
      textInput.value = '';
      showToast('Comment posted', 'success');
      loadCommentsAndLogs(selectedTaskIdForComments);
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // Utility Toast Function
  function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
      <i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation'}"></i>
      <span>${escapeHtml(message)}</span>
    `;
    toastContainer.appendChild(toast);
    setTimeout(() => {
      toast.remove();
    }, 4000);
  }

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"']/g, function (m) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[m];
    });
  }
});