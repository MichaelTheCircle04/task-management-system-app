package com.mtrifonov.task.management.system.app.repositories;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mtrifonov.task.management.system.app.entities.TaskComment;

@Profile(value = "prod")
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
	
	Page<TaskComment> findAllByTask(Long id, Pageable pageable);
	Page<TaskComment> findAllByTaskAndAuthor(Long id, String email, Pageable pageable);
	
	
}
