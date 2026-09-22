package dev.perfectbogus.mongo.svc.repository;

import dev.perfectbogus.mongo.svc.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByStatus(String status);
    List<Task> findByTagsContaining(String tag);
}
