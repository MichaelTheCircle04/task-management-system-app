package com.mtrifonov.task.management.system.app.stubs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Repository;

import com.mtrifonov.task.management.system.app.entities.Task;
import com.mtrifonov.task.management.system.app.entities.TaskComment;
import com.mtrifonov.task.management.system.app.repositories.TaskCommentRepository;

@Repository
@Profile(value = "test")
public class StubTaskCommentRepository implements TaskCommentRepository {
	
	AtomicInteger seq = new AtomicInteger(1);
	
	List<TaskComment> comments = new ArrayList<>(List.of(
			TaskComment.builder()
			.id(seq.getAndIncrement())
			.task(Task.builder().id(1).build())
			.text("comment1")
			.author("s.spiegel@example.com").build(),
			TaskComment.builder()
			.id(seq.getAndIncrement())
			.task(Task.builder().id(1).build())
			.text("comment2")
			.author("b.baggins@example.com").build(),
			TaskComment.builder()
			.id(seq.getAndIncrement())
			.task(Task.builder().id(1).build())
			.text("comment3")
			.author("b.baggins@example.com").build(),
			TaskComment.builder()
			.id(seq.getAndIncrement())
			.task(Task.builder().id(1).build())
			.text("comment4")
			.author("b.baggins@example.com").build(),
			TaskComment.builder()
			.id(seq.getAndIncrement())
			.task(Task.builder().id(1).build())
			.text("comment5")
			.author("m.circle@example.com").build()
			));
	

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public <S extends TaskComment> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends TaskComment> List<S> saveAllAndFlush(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<TaskComment> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> ids) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllInBatch() {
		// TODO Auto-generated method stub

	}

	@Override
	public TaskComment getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskComment getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskComment getReferenceById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends TaskComment> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends TaskComment> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends TaskComment> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TaskComment> findAll() {
		return Collections.unmodifiableList(comments);
	}

	@Override
	public List<TaskComment> findAllById(Iterable<Long> ids) {
		Set<Long> set = new HashSet<>();
		var itr = ids.iterator();
		while(itr.hasNext()) {
			set.add(itr.next());
		}
		return comments.stream().filter(c -> set.contains(c.getId())).toList();
	}

	@Override
	public <S extends TaskComment> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<TaskComment> findById(Long id) {
		return comments.stream().filter(c -> id.equals(c.getId())).findFirst();
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long count() {
		return comments.size();
	}

	@Override
	public void deleteById(Long id) {
		var task = comments.stream().filter(c -> id.equals(c.getId())).findFirst().orElseThrow();
		comments.remove(task);

	}

	@Override
	public void delete(TaskComment entity) {
		deleteById(entity.getId());
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		Set<Long> set = new HashSet<>();
		var itr = ids.iterator();
		while(itr.hasNext()) {
			set.add(itr.next());
		}
		comments.stream().filter(c -> set.contains(c.getId())).forEach(c -> delete(c));

	}

	@Override
	public void deleteAll(Iterable<? extends TaskComment> entities) {
		var itr = entities.iterator();
		while(itr.hasNext()) {
			delete(itr.next());
		}

	}

	@Override
	public void deleteAll() {
		comments.stream().forEach(c -> delete(c));

	}

	@Override
	public List<TaskComment> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<TaskComment> findAll(Pageable pageable) {
		return new PageImpl<>(comments, pageable, comments.size());
	}

	@Override
	public <S extends TaskComment> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public <S extends TaskComment> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends TaskComment> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends TaskComment> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <S extends TaskComment, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<TaskComment> findAllByTask(Long id, Pageable pageable) {
		
		List<TaskComment> data;
		var orders = pageable.getSort().get().toList();
		
		if (orders.isEmpty()) {
			data = comments.stream().filter(c -> id.equals(c.getTask().getId())).toList();
		} else {
			data = comments.stream().filter(c -> id.equals(c.getTask().getId()))
					.sorted((Comparator<TaskComment>) StubTaskRepository.getComparator(orders, TaskComment.class))
					.toList();
		}
		
		long start = pageable.getOffset();
		long end = Math.min((start + pageable.getPageSize()), data.size());
		var page = data.subList((int) start, (int) end);
		
		return new PageImpl<>(page, pageable, data.size());
	}

	@Override
	public Page<TaskComment> findAllByTaskAndAuthor(Long id, String email, Pageable pageable) {
		
		List<TaskComment> data;
		var orders = pageable.getSort().get().toList();
		
		if (orders.isEmpty()) {
			data = comments.stream()
					.filter(c -> 
						id.equals(c.getTask().getId()) && 
						email.equals(c.getAuthor()))
					.toList();
		} else {
			data = comments.stream()
					.filter(c -> 
						id.equals(c.getTask().getId()) && 
						email.equals(c.getAuthor()))
					.sorted((Comparator<TaskComment>) StubTaskRepository.getComparator(orders, TaskComment.class))
					.toList();
		}
		
		long start = pageable.getOffset();
		long end = Math.min((start + pageable.getPageSize()), data.size());
		var page = data.subList((int) start, (int) end);
		
		return new PageImpl<>(page, pageable, data.size());
	}

}
