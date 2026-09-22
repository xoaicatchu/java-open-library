package com.example.camunda.listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component("orderStartedListener")
public class OrderStartedListener implements ExecutionListener {

    private final Logger logger = Logger.getLogger(OrderStartedListener.class.getName());

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        logger.info("Process instance started: " + processInstanceId);
        execution.setVariable("processStarted", true);
    }
}
