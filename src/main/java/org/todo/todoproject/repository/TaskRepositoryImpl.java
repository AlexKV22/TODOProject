package org.todo.todoproject.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.todo.todoproject.dto.request.TaskChangeStatusRequest;
import org.todo.todoproject.entity.Task;
import org.todo.todoproject.util.TaskStatus;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Slf4j
public class TaskRepositoryImpl implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TaskRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Task save(Task task) {
        String sql = "INSERT INTO todo_project.tasks (title, created_at, expire_at, status) VALUES (?, ?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.query(con -> {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, task.getTitle());
            statement.setTimestamp(2, Timestamp.from(task.getCreateAt()));
            if (task.getExpireAt() != null) {
                statement.setDate(3, Date.valueOf(task.getExpireAt()));
            } else {
                statement.setNull(3, Types.DATE);
            }
            statement.setString(4, task.getStatus().name());
            return statement;
        }, rs -> rs.next() ? rs.getLong("id") : null);
        createLog("Create", id);
        task.setId(id);
        return task;
    }

    @Override
    public Page<Task> findAll(Pageable pageable) {
        String sql = "SELECT * FROM todo_project.tasks ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<Task> result = jdbcTemplate.query(con -> {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, pageable.getPageSize());
            statement.setLong(2, pageable.getOffset());
            return statement;
        }, (rs, rowNum) -> mapRow(rs));
        String sqlForSize = "SELECT COUNT(*) FROM todo_project.tasks";
        Long count = jdbcTemplate.queryForObject(sqlForSize, Long.class);
        return new PageImpl<>(result, pageable, count);
    }

    @Override
    public Optional<Task> findById(Long id) {
        Task result = null;
        String sql = "SELECT * FROM todo_project.tasks WHERE id = ?";
        try {
            result = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRow(rs), id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        createLog("FindById", id);
        return Optional.of(result);
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM todo_project.tasks WHERE id = ?";
        int update = jdbcTemplate.update(sql, id);
        createLog("Delete",id);
        return update >= 1;
    }

    @Override
    public Optional<Task> update(Task task, Long id) {
        String sql = "UPDATE todo_project.tasks SET title = ?, expire_at = ?, status = ? WHERE id = ? RETURNING id, title, created_at, expire_at, status";
        Task updateTask = jdbcTemplate.query(con -> {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, task.getTitle());
            if (task.getExpireAt() != null) {
                statement.setDate(2, Date.valueOf(task.getExpireAt()));
            } else {
                statement.setNull(2, Types.DATE);
            }
            statement.setString(3, task.getStatus().name());
            statement.setLong(4, id);
            return statement;
        }, rs -> rs.next() ? mapRow(rs) : null);
        createLog("Update", id);
        return Optional.ofNullable(updateTask);
    }

    @Override
    public Optional<Task> changeStatus(TaskChangeStatusRequest taskChangeStatusRequest, Long id) {
        String sql = "UPDATE todo_project.tasks SET status = ? WHERE id = ? RETURNING id, title, created_at, expire_at, status";
        Task task = jdbcTemplate.query(con -> {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, taskChangeStatusRequest.status().name());
            statement.setLong(2, id);
            return statement;
        }, rs -> rs.next() ? mapRow(rs) : null);
        createLog("UpdateStatus", id);
        return Optional.ofNullable(task);
    }

    private Task mapRow(ResultSet rs) throws SQLException {
        LocalDate expire = null;
        Date expireAt = rs.getDate("expire_at");
        if (expireAt != null) {
            expire = expireAt.toLocalDate();
        }
        return new Task(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getTimestamp("created_at").toInstant(),
                expire,
                TaskStatus.valueOf(rs.getString("status"))
        );
    }

    private void createLog(String action, Long id) {
        log.info("Action: {}, id: {}", action, id);
    }
}
