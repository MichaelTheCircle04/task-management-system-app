package com.mtrifonov.task.management.system.app.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"comments"})
@EqualsAndHashCode(exclude = {"comments"})
@JsonIgnoreProperties(value = {"id", "comments"})
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="tasks_seq")
    @SequenceGenerator(name="tasks_seq", sequenceName="tasks_task_id_seq", initialValue = 6)
    @Column(name = "task_id")
    private Long id;
    @NotBlank(message = "Header must be present")
    private String header;
    @NotBlank(message = "Description must be present")
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @NotNull(message = "Priority must be present")
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private String author;
    private String executor;

    @OneToMany(mappedBy = "task")
    private List<TaskComment> comments;
    
    public enum Status {
        WAITING,
        IN_PROGRESS,
        COMPLETED
    }
    
    public enum Priority {
    	LOW,
    	MIDDLE,
    	HIGH
    }
}
