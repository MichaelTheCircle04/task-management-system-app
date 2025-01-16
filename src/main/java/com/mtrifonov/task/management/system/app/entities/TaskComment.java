package com.mtrifonov.task.management.system.app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
*
* @Mikhail Trifonov
*/
@Entity
@Table(name = "task_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"task"})
@EqualsAndHashCode(exclude = {"task"})
public class TaskComment {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="task_comments_seq")
    @SequenceGenerator(name="task_comments_seq", sequenceName="task_comments_task_comment_id_seq")
    @Column(name = "task_comment_id")
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id")
	private Task task;
	
	private String author;
	
	private String text;
}
