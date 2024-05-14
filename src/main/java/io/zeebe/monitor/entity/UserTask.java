package io.zeebe.monitor.entity;


import jakarta.persistence.*;

@Entity
public class UserTask {
    @Id
    @Column(name = "key_")
    private long key;
    private long position;
    private long processInstanceKey;
    private long processDefinitionKey;
    private long elementInstanceKey;
    private String assignee;
    private String candidateGroups;
    private String formKey;

    @Enumerated(EnumType.STRING)
    private UserTaskState state = UserTaskState.CREATED;
    private long timestamp = -1;

    private Long startTime;
    private Long endTime;

    public UserTask() {
    }

    public UserTask(long key, long position, long processInstanceKey, long processDefinitionKey, long elementInstanceKey, String assignee, String candidateGroups, String formKey) {
        this.key = key;
        this.position = position;
        this.processInstanceKey = processInstanceKey;
        this.processDefinitionKey = processDefinitionKey;
        this.elementInstanceKey = elementInstanceKey;
        this.assignee = assignee;
        this.candidateGroups = candidateGroups;
        this.formKey = formKey;
    }

    public long getKey() {
        return key;
    }

    public long getPosition() {
        return position;
    }

    public long getProcessInstanceKey() {
        return processInstanceKey;
    }

    public long getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public long getElementInstanceKey() {
        return elementInstanceKey;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getCandidateGroups() {
        return candidateGroups;
    }

    public String getFormKey() {
        return formKey;
    }

    public UserTaskState getState() {
        return state;
    }

    public void setState(UserTaskState state) {
        this.state = state;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}

