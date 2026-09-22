package com.example.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component("rejectOrderDelegate")
public class RejectOrderDelegate implements JavaDelegate {

    private final Logger logger = Logger.getLogger(RejectOrderDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String orderId = (String) execution.getVariable("orderId");
        
        // Giả lập từ chối đơn hàng
        logger.info("Rejecting order " + orderId);
        execution.setVariable("status", "REJECTED");
    }
}
