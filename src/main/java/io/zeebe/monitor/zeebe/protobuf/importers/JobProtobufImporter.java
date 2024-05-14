package io.zeebe.monitor.zeebe.protobuf.importers;

import com.google.protobuf.Value;
import io.camunda.zeebe.protocol.Protocol;
import io.camunda.zeebe.protocol.record.intent.JobIntent;
import io.zeebe.exporter.proto.Schema;
import io.zeebe.monitor.entity.JobEntity;
import io.zeebe.monitor.entity.UserTask;
import io.zeebe.monitor.entity.UserTaskState;
import io.zeebe.monitor.repository.JobRepository;
import io.zeebe.monitor.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class JobProtobufImporter {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    public void importJob(final Schema.JobRecord record) {
        if (isJobForUserTask(record)) {
            importUserTask(record);
        }

        final JobIntent intent = JobIntent.valueOf(record.getMetadata().getIntent());
        final long key = record.getMetadata().getKey();
        final long timestamp = record.getMetadata().getTimestamp();

        final JobEntity entity = jobRepository.findById(key).orElseGet(() -> {
            final JobEntity newEntity = new JobEntity();
            newEntity.setKey(key);
            newEntity.setProcessInstanceKey(record.getProcessInstanceKey());
            newEntity.setElementInstanceKey(record.getElementInstanceKey());
            newEntity.setJobType(record.getType());
            return newEntity;
        });

        entity.setState(intent.name().toLowerCase());
        entity.setTimestamp(timestamp);
        entity.setWorker(record.getWorker());
        entity.setRetries(record.getRetries());
        jobRepository.save(entity);
    }

    private Boolean isJobForUserTask(Schema.JobRecord record) {
        return record.getType().equals(Protocol.USER_TASK_JOB_TYPE);
    }

    private void importUserTask(Schema.JobRecord record) {
        Optional<UserTask> entityOptional = userTaskRepository.findById(record.getMetadata().getKey());
        UserTask entity;
        entity = entityOptional.orElseGet(() -> createUserTask(record));

        switch (record.getMetadata().getIntent()) {
            case "CREATED":
                entity.setStartTime(record.getMetadata().getTimestamp());
                break;
            case "COMPLETED":
                entity.setState(UserTaskState.COMPLETED);
                entity.setEndTime(record.getMetadata().getTimestamp());
                break;
            case "CANCELED":
                entity.setState(UserTaskState.CANCELED);
                entity.setEndTime(record.getMetadata().getTimestamp());
                break;
        }

        entity.setTimestamp(record.getMetadata().getTimestamp());

        userTaskRepository.save(entity);
    }

    private UserTask createUserTask(Schema.JobRecord record) {
        Map<String, Value> customHeaders = record.getCustomHeaders().getFieldsMap();
        String assignee = customHeaders.get(Protocol.USER_TASK_ASSIGNEE_HEADER_NAME) != null ? customHeaders.get(Protocol.USER_TASK_ASSIGNEE_HEADER_NAME).getStringValue() : null;
        String candidateGroups = customHeaders.get(Protocol.USER_TASK_CANDIDATE_GROUPS_HEADER_NAME) != null ? customHeaders.get(Protocol.USER_TASK_CANDIDATE_GROUPS_HEADER_NAME).getStringValue() : null;
        String formKey = customHeaders.get(Protocol.USER_TASK_FORM_KEY_HEADER_NAME) != null ? customHeaders.get(Protocol.USER_TASK_FORM_KEY_HEADER_NAME).getStringValue() : null;
        return new UserTask(
                record.getMetadata().getKey(),
                record.getMetadata().getPosition(),
                record.getProcessInstanceKey(),
                record.getProcessDefinitionKey(),
                record.getElementInstanceKey(),
                assignee,
                candidateGroups,
                formKey
        );
    }
}
