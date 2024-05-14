package io.zeebe.monitor.repository;

import io.zeebe.monitor.entity.UserTask;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTaskRepository extends CrudRepository<UserTask, Long> {

    List<UserTask> findByProcessInstanceKey(long processInstanceKey);
}


