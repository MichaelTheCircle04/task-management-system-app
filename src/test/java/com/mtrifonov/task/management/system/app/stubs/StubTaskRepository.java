package com.mtrifonov.task.management.system.app.stubs;

import java.lang.reflect.InvocationTargetException;
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
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.mtrifonov.task.management.system.app.entities.Task;
import com.mtrifonov.task.management.system.app.entities.Task.Priority;
import com.mtrifonov.task.management.system.app.entities.Task.Status;
import com.mtrifonov.task.management.system.app.repositories.TaskRepository;

@Repository
@Profile(value = "test")
public class StubTaskRepository implements TaskRepository {
	
	AtomicInteger seq = new AtomicInteger(1);

	List<Task> tasks = new ArrayList<>(List.of(
			Task.builder()
			.id(this.seq.getAndIncrement())
			.header("header")
			.description("task1")
			.status(Status.COMPLETED)
			.priority(Priority.MIDDLE)
			.author("s.spiegel@example.com")
			.executor("b.baggins@example.com")
			.build(), 
			Task.builder()
			.id(this.seq.getAndIncrement())
			.header("header")
			.description("task2")
			.status(Status.IN_PROGRESS)
			.priority(Priority.MIDDLE)
			.author("s.spiegel@example.com")
			.executor("m.circle@example.com")
			.build(),
			Task.builder()
			.id(this.seq.getAndIncrement())
			.header("header")
			.description("task3")
			.status(Status.COMPLETED)
			.priority(Priority.LOW)
			.author("s.spiegel@example.com")
			.executor("m.circle@example.com")
			.build()));
	
	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public <S extends Task> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Task> List<S> saveAllAndFlush(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAllInBatch(Iterable<Task> entities) {
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
	public Task getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task getReferenceById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Task> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Task> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Task> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Task> findAll() {
		return Collections.unmodifiableList(tasks);
	}

	@Override
	public List<Task> findAllById(Iterable<Long> ids) {
		Set<Long> set = new HashSet<>();
		var itr = ids.iterator();
		while(itr.hasNext()) {
			set.add(itr.next());
		}
		return tasks.stream().filter(t -> set.contains(t.getId())).toList();
	}

	@Override
	public <S extends Task> S save(S entity) {
		entity.setId(this.seq.getAndIncrement());
		this.tasks.add(entity);
		return entity;
	}

	@Override
	public Optional<Task> findById(Long id) {		
		return tasks.stream().filter(t -> id.equals(t.getId())).findFirst();
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long count() {
		return tasks.size();
	}

	@Override
	public void deleteById(Long id) {
		
		var task = tasks.stream().filter(t -> id.equals(t.getId())).findFirst().orElseThrow();
		tasks.remove(task);
	}

	@Override
	public void delete(Task entity) {
		
		deleteById(entity.getId());
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		Set<Long> set = new HashSet<>();
		var itr = ids.iterator();
		while(itr.hasNext()) {
			set.add(itr.next());
		}
		tasks.stream().filter(t -> set.contains(t.getId())).forEach(t -> delete(t));
	}

	@Override
	public void deleteAll(Iterable<? extends Task> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Task> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Task> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Task> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public <S extends Task> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Task> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends Task> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <S extends Task, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Task> findAllByAuthor(String email, Pageable pageable) {
		
		List<Task> data;
		var orders = pageable.getSort().get().toList();
		System.out.println("1");
		if (orders.isEmpty()) {
			data = tasks.stream().filter(t -> t.getAuthor().equals(email)).toList();
		} else {
			data = tasks.stream().filter(t -> t.getAuthor().equals(email))
					.sorted((Comparator<Task>) getComparator(orders, Task.class)).toList();
		}
		System.out.println("2");
		long start = pageable.getOffset();
		long end = Math.min((start + pageable.getPageSize()), data.size());
		System.out.println("3");
		System.out.println(start);
		System.out.println(end);
		var page = data.subList((int) start, (int) end);
		System.out.println("4");
		return new PageImpl<>(page, pageable, data.size());
	}

	@Override
	public Page<Task> findAllByExecutor(String email, Pageable pageable) {
		
		List<Task> data;
		var orders = pageable.getSort().get().toList();
		
		if (orders.isEmpty()) {
			data = tasks.stream().filter(t -> t.getExecutor().equals(email)).toList();
		} else {
			data = tasks.stream().filter(t -> t.getExecutor().equals(email))
					.sorted((Comparator<Task>) getComparator(orders, Task.class)).toList();
		}
				
		long start = pageable.getOffset();
		long end = Math.min((start + pageable.getPageSize()), data.size());
		var page = data.subList((int) start, (int) end);
		
		return new PageImpl<>(page, pageable, data.size());
	}
	
	public static Comparator<?> getComparator(List<Order> orders, Class<?> targetType) {
		
		Assert.notEmpty(orders, "Orders cannot be empty");
		
		return (f, s) -> {
			
			int res = 0;
			var curOrder = orders.get(0);
			boolean asc = curOrder.isAscending();
			for (var order : orders) {

				try {
					var method = targetType.getMethod("get" + StringUtils.capitalize(order.getProperty()));
					var val1 = method.invoke(f);
					var val2 = method.invoke(s);
					
					if (val1 instanceof Comparable && val2 instanceof Comparable) {
						Comparable c1 = (Comparable) val1;
						Comparable c2 = (Comparable) val2;
						res = c1.compareTo(c2);
					}
					
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) { System.out.println(e.getMessage()); }
				
				if (res != 0) {
					break;
				}
			}			
			return asc ? res : res * -1;
		};
	}

}
