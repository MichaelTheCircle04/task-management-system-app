package com.mtrifonov.task.management.system.app.repositories;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import com.mtrifonov.task.management.system.app.entities.TaskComment;

/**
*
* @Mikhail Trifonov
*/
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
	
	@Query(value = 
		"""
		SELECT
			c, t
		FROM TaskComment c
			JOIN FETCH Task t ON c.task.id = t.id
		WHERE 
			c.id = ?1
		"""
	)
	Optional<TaskComment> findById(Long id);
	@NativeQuery(value =
		"""
		SELECT 
			t.task_id, header, description, status, 
			priority, t.author, executor, task_comment_id,
			c.author c_author, text 
		FROM task_comments c 
			JOIN tasks t USING (task_id)
		WHERE 
			t.task_id = ?1
		""", countQuery = 
		"""
		SELECT
			count(task_comment_id)
		FROM task_comments c
		WHERE
			c.task_id = ?1 	
		""", sqlResultSetMapping = "CommentsWithTask")
	Page<Object[]> findByTaskId(Long id, Pageable pageable);
	@NativeQuery(value = 
		"""
		SELECT
			t.task_id, header, description, status, 
			priority, t.author, executor, task_comment_id,
			c.author c_author, text	
		FROM task_comments c 
			JOIN tasks t USING (task_id)
		WHERE 
			t.task_id = ?1 AND
			c.author = ?2
		""", countQuery = 
		"""
		SELECT
			count(task_comment_id)
		FROM task_comments c
		WHERE
			c.task_id = ?1 AND 
			c.author = ?2		
		""", sqlResultSetMapping = "CommentsWithTask"
	)
	Page<Object[]> findByTaskIdAndAuthor(Long id, String email, Pageable pageable);
}
