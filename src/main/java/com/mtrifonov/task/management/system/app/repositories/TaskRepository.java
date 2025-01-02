package com.mtrifonov.task.management.system.app.repositories;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mtrifonov.task.management.system.app.entities.Task;

@Profile(value = "prod")
public interface TaskRepository extends JpaRepository<Task, Long> {

	Page<Task> findAllByAuthor(String email, Pageable pageable);
	Page<Task> findAllByExecutor(String email, Pageable pageable);
}
