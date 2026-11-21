package com.taskflow.api.initializer;

import com.taskflow.api.entity.*;
import com.taskflow.api.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final ProjectRepository projectRepository;
        private final TaskRepository taskRepository;
        private final CommentRepository commentRepository;
        private final ActivityLogRepository activityLogRepository;
        private final PasswordEncoder passwordEncoder;

        public DataInitializer(UserRepository userRepository,
                        ProjectRepository projectRepository,
                        TaskRepository taskRepository,
                        CommentRepository commentRepository,
                        ActivityLogRepository activityLogRepository,
                        PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
                this.projectRepository = projectRepository;
                this.taskRepository = taskRepository;
                this.commentRepository = commentRepository;
                this.activityLogRepository = activityLogRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                commentRepository.deleteAll();
                activityLogRepository.deleteAll();
                taskRepository.deleteAll();
                projectRepository.deleteAll();
                userRepository.deleteAll();

                // 2. Re-seed clean BCrypt encrypted users
                User admin = new User(null, "System Admin", "admin@taskflow.com", passwordEncoder.encode("admin123"),
                                Role.ADMIN, LocalDateTime.now());
                User manager = new User(null, "Project Manager", "manager@taskflow.com",
                                passwordEncoder.encode("manager123"), Role.MANAGER, LocalDateTime.now());
                User dev = new User(null, "Developer", "dev@taskflow.com", passwordEncoder.encode("dev123"),
                                Role.DEVELOPER, LocalDateTime.now());

                userRepository.save(admin);
                userRepository.save(manager);
                userRepository.save(dev);

                // 2. Seed Projects
                Project p1 = new Project(null, "TaskFlow Core Engine",
                                "Core backend REST API modules, security, and smart tracking algorithm", manager,
                                LocalDateTime.now());
                Project p2 = new Project(null, "Mobile Companion App",
                                "Cross-platform mobile application for issue tracking",
                                manager, LocalDateTime.now());

                p1 = projectRepository.save(p1);
                p2 = projectRepository.save(p2);

                // 3. Seed Tasks
                Task t1 = new Task(null, "JWT Security & Authentication",
                                "Implement BCrypt password hashing and stateless JWT filters", Priority.CRITICAL,
                                Status.IN_PROGRESS,
                                dev, p1, LocalDate.now().plusDays(7));
                Task t2 = new Task(null, "Smart Allocation Algorithm",
                                "Implement developer workload balance query (activeTasks < 5)", Priority.HIGH,
                                Status.TODO, dev, p1,
                                LocalDate.now().plusDays(10));
                Task t3 = new Task(null, "Dashboard Metrics Endpoint",
                                "Aggregate project, task, testing, and critical issue totals", Priority.MEDIUM,
                                Status.DONE, dev, p1,
                                LocalDate.now().plusDays(3));
                Task t4 = new Task(null, "H2 Console Configuration",
                                "Configure frame options and stateless security for in-memory DB", Priority.CRITICAL,
                                Status.TESTING,
                                dev, p1, LocalDate.now().plusDays(2));

                t1 = taskRepository.save(t1);
                t2 = taskRepository.save(t2);
                t3 = taskRepository.save(t3);
                t4 = taskRepository.save(t4);

                // 4. Seed Comments
                Comment c1 = new Comment(null, t1, dev,
                                "JWT filters and SecurityConfig have been implemented and tested.",
                                LocalDateTime.now());
                Comment c2 = new Comment(null, t3, manager, "Great work completing the dashboard metrics endpoint!",
                                LocalDateTime.now());

                commentRepository.save(c1);
                commentRepository.save(c2);

                // 5. Seed Activity Logs
                activityLogRepository
                                .save(new ActivityLog(t1,
                                                "Task created by manager@taskflow.com and assigned to Developer"));
                activityLogRepository.save(
                                new ActivityLog(t1, "Status changed from TODO to IN_PROGRESS by dev@taskflow.com"));
                activityLogRepository
                                .save(new ActivityLog(t2,
                                                "Task created by manager@taskflow.com and assigned to Developer"));
                activityLogRepository.save(new ActivityLog(t3, "Status changed to DONE by dev@taskflow.com"));
                activityLogRepository.save(new ActivityLog(t4, "Status changed to TESTING by dev@taskflow.com"));

                System.out.println(">>> TaskFlow API Seed Data successfully loaded!");
        }
}
