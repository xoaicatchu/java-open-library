package com.example.camunda;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CamundaApplicationTests {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Test
    void testProcessDeployment() {
        assertNotNull(runtimeService);
        assertNotNull(taskService);
    }

    @Test
    void testStartProcessAndCheckVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", "ORD-123");
        variables.put("customerName", "Alice");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("order-approval", variables);
        
        assertNotNull(processInstance);
        
        Object processStarted = runtimeService.getVariable(processInstance.getId(), "processStarted");
        assertEquals(true, processStarted, "ExecutionListener should have set this variable");
    }

    @Test
    void testUserTaskCreation() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", "ORD-456");
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("order-approval", variables);
        
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertNotNull(task);
        assertEquals("Review Order", task.getName());
        assertEquals("admin", task.getAssignee());
    }

    @Test
    void testGatewayRoutingApproved() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", "ORD-789");
        variables.put("customerName", "Bob");
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("order-approval", variables);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        
        // Approve order
        taskService.complete(task.getId(), Map.of("approved", true));
        
        // Since it's approved, it should hit processOrderDelegate and end
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
        assertNull(pi, "Process should be completed and removed from runtime");
    }

    @Test
    void testGatewayRoutingRejected() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", "ORD-999");
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("order-approval", variables);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        
        // Reject order
        taskService.complete(task.getId(), Map.of("approved", false));
        
        // Since it's rejected, it should hit rejectOrderDelegate and end
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
        assertNull(pi, "Process should be completed and removed from runtime");
    }
}
