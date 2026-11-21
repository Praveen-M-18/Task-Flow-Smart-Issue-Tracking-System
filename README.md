# 🚀 TaskFlow – Smart Issue Tracking and Management System

<p align="center">
  <strong>A Smart, Role-Based Project and Issue Tracking Platform</strong>
</p>

<p align="center">
  A centralized task management solution designed to simplify project coordination, task allocation, workload monitoring, and team collaboration.
</p>

---

## 📌 Project Overview

**TaskFlow** is a web-based Smart Issue Tracking and Management System developed using Java and Spring Boot. It provides a centralized platform for managing projects, assigning tasks, tracking progress, and improving collaboration among team members.

The application supports multiple user roles, including Admin, Project Manager, Developer, and Tester, with role-based access control to ensure that users can access functionalities according to their responsibilities.

TaskFlow also incorporates a rule-based workload recommendation mechanism that helps managers identify team members with fewer active tasks when assigning new tasks. It provides project dashboards, activity tracking, developer comments, and CSV report generation to improve project visibility and management efficiency.

## 🎯 Project Objectives

- Simplify project and task management.
- Provide secure role-based authentication and authorization.
- Improve task allocation based on team workload.
- Track task progress, priorities, and statuses.
- Maintain activity logs for better transparency.
- Improve communication through task comments.
- Provide dashboards and downloadable project reports.

## ✨ Key Features

### 🔐 1. Authentication and Authorization
- Secure user authentication using Spring Security.
- JWT-based authentication.
- Role-based access control.
- Password encryption using BCrypt.
- Restricted access based on user roles.

### 👥 2. Role-Based User Management

TaskFlow supports four major user roles:

| Role | Responsibilities |
|---|---|
| Admin | Manage users and oversee the system. |
| Project Manager | Create projects, assign tasks, and monitor progress. |
| Developer | View assigned tasks, update progress, and add comments. |
| Tester | Track and manage testing-related tasks. |

### 📁 3. Project Management
- Create and manage projects.
- Organize project-related tasks.
- Monitor overall project progress.
- Manage project team activities.

### 📋 4. Task Management
- Create, assign, and manage tasks.
- Set task priorities.
- Track task status.
- View assigned tasks.
- Update task progress.

### ⚖️ 5. Workload-Based Task Assignment
- Evaluates the number of active tasks assigned to team members.
- Recommends team members with comparatively lower active workloads.
- Helps managers distribute tasks more effectively.
- Supports balanced workload distribution using rule-based logic.

### 💬 6. Comments and Activity Tracking
- Developers can add comments to tasks.
- Maintain task-related communication.
- Record important task activities.
- Track changes and updates through activity logs.

### 📊 7. Dashboard and Reporting
- View project and task statistics.
- Monitor task progress.
- Track team activities.
- Generate and download project reports in CSV format.

---

## 🛠️ Technology Stack

| Category | Technologies |
|---|---|
| Programming Language | Java 21 |
| Backend Framework | Spring Boot |
| Backend Architecture | Spring MVC, REST APIs |
| ORM Framework | Hibernate |
| Data Access | Spring Data JPA |
| Security | Spring Security, JWT |
| Password Encryption | BCrypt |
| Database | H2 Database |
| Build Tool | Maven |
| API Communication | RESTful Web Services |
| Version Control | Git and GitHub |

---

## 🏗️ System Architecture

TaskFlow follows a layered application architecture to maintain separation of responsibilities and improve code organization.

```text
             ┌──────────────────────┐
             │     User Interface   │
             │    Web Application   │
             └──────────┬───────────┘
                        │
                        ▼
             ┌──────────────────────┐
             │     REST API Layer   │
             │     Controllers      │
             └──────────┬───────────┘
                        │
                        ▼
             ┌──────────────────────┐
             │    Service Layer     │
             │  Business Logic      │
             └──────────┬───────────┘
                        │
                        ▼
             ┌──────────────────────┐
             │   Repository Layer   │
             │   Spring Data JPA    │
             └──────────┬───────────┘
                        │
                        ▼
             ┌──────────────────────┐
             │     H2 Database      │
             │   Data Persistence   │
             └──────────────────────┘
```

**Security Flow:**

1. Users authenticate using their credentials.
2. The application validates the credentials.
3. A JWT token is generated after successful authentication.
4. The token is included in subsequent protected API requests.
5. Spring Security validates the token and enforces role-based access.

---

## 🗂️ Database Design

TaskFlow uses Hibernate and Spring Data JPA for database operations.

The main entities include:

| Entity | Description |
|---|---|
| User | Stores user information and roles. |
| Project | Stores project details. |
| Task | Stores task information, assignments, priorities, and statuses. |
| Comment | Stores task-related comments. |
| ActivityLog | Maintains task activity history. |

### Task Status

Tasks are managed using different status values, such as:

- TODO
- IN_PROGRESS
- COMPLETED

### Task Priority

Tasks can be categorized based on their priority to help teams organize their work effectively.

---

## ⚙️ Installation and Setup

Follow these steps to run TaskFlow on your local machine.

### Prerequisites

Make sure the following tools are installed:

- Java Development Kit (JDK 21)
- Apache Maven
- Git
- IDE such as Eclipse, IntelliJ IDEA, or VS Code

### Step 1: Clone the Repository

```bash
git clone https://github.com/Praveen-M-18/Task-Flow---Smart-Issue-Tracking-System.git
```

### Step 2: Navigate to the Project Directory

```bash
cd Task-Flow---Smart-Issue-Tracking-System
```

### Step 3: Build the Application

On Windows:

```bash
mvnw.cmd clean install
```

Alternatively, if Maven is installed globally:

```bash
mvn clean install
```

### Step 4: Run the Application

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Alternatively:

```bash
mvn spring-boot:run
```

### Step 5: Access the Application

Once the application starts successfully, access it at:

```text
http://localhost:8085
```

The H2 Database Console can be accessed at:

```text
http://localhost:8085/h2-console
```

Use the JDBC URL and credentials configured in your application properties.

---

## 🔌 API Integration

TaskFlow uses RESTful APIs to handle communication between the frontend and backend.

The API layer supports operations related to:

- User authentication.
- Project management.
- Task creation and assignment.
- Task status updates.
- Comments and activity tracking.
- Dashboard statistics.
- Report generation.

Protected API requests use JWT-based authentication.

---

## 📸 Application Screenshots

Add your actual application screenshots in the `screenshots` directory.

Example:

```text
screenshots/
├── login.png
├── dashboard.png
├── project-management.png
├── task-management.png
├── task-details.png
└── reports.png
```

Display them in the README using:

```markdown
### Login Page
![Login Page](screenshots/login.png)

### Dashboard
![Dashboard](screenshots/dashboard.png)

### Task Management
![Task Management](screenshots/task-management.png)

### Project Management
![Project Management](screenshots/project-management.png)

### Reports
![Reports](screenshots/reports.png)
```

---

## 🔒 Security Considerations

- JWT-based authentication.
- Role-based authorization.
- BCrypt password hashing.
- Protected application endpoints.
- Stateless authentication architecture.

**Note:** Keep production credentials and JWT secrets outside the source repository. Configure appropriate production security settings before deployment.

---

## 🚀 Future Enhancements

The following improvements can be considered for future versions:

- Integration with MySQL or PostgreSQL.
- Email notifications for task assignments and updates.
- Real-time notifications.
- Advanced project analytics and visual reports.
- Docker-based application deployment.
- Microservices architecture.
- Integration with CI/CD pipelines.
- Cloud deployment.

---

## 👨‍💻 Developer

**Praveen M**

Master of Computer Applications (MCA)

Coimbatore, Tamil Nadu, India

- GitHub: [Praveen-M-18](https://github.com/Praveen-M-18)

---
