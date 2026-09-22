package com.example.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component("processOrderDelegate")
public class ProcessOrderDelegate implements JavaDelegate {
    
    private final Logger logger = Logger.getLogger(ProcessOrderDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String orderId = (String) execution.getVariable("orderId");
        String customerName = (String) execution.getVariable("customerName");
        
        // Giả lập xử lý đơn hàng
        logger.info("Processing order " + orderId + " for customer " + customerName);
        execution.setVariable("status", "PROCESSED");
    }
}
