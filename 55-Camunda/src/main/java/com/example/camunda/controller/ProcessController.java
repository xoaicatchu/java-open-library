package com.example.camunda.controller;

import com.example.camunda.dto.StartProcessRequest;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class ProcessController {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public ProcessController(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startProcess(@RequestBody StartProcessRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", request.orderId());
        variables.put("customerName", request.customerName());

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("order-approval", variables);
        
        return ResponseEntity.ok(Map.of(
                "processInstanceId", instance.getId(),
                "status", "Process started successfully"
        ));
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Map<String, Object>>> getTasks() {
        List<Task> tasks = taskService.createTaskQuery().processDefinitionKey("order-approval").list();
        
        List<Map<String, Object>> response = tasks.stream().map(task -> {
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("taskId", task.getId());
            taskInfo.put("taskName", task.getName());
            
            Map<String, Object> variables = taskService.getVariables(task.getId());
            taskInfo.put("variables", variables);
            return taskInfo;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Map<String, String>> completeTask(@PathVariable String taskId, @RequestParam boolean approved) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approved);
        
        taskService.complete(taskId, variables);
        
        return ResponseEntity.ok(Map.of("status", "Task completed", "taskId", taskId, "approved", String.valueOf(approved)));
    }
}
