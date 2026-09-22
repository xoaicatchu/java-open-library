package com.example.sentinel.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelConfig {

    // IMPORTANT: Required to enable @SentinelResource
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @PostConstruct
    public void initRules() {
        initFlowRules();
        initDegradeRules();
    }

    private void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        
        FlowRule getProductRule = new FlowRule();
        getProductRule.setResource("getProduct"); // Tên resource khớp với value trong @SentinelResource
        getProductRule.setGrade(RuleConstant.FLOW_GRADE_QPS); // Giới hạn theo QPS
        getProductRule.setCount(2); // Cho phép tối đa 2 request/giây
        
        rules.add(getProductRule);
        FlowRuleManager.loadRules(rules);
    }

    private void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();
        
        DegradeRule createProductRule = new DegradeRule();
        createProductRule.setResource("createProduct");
        createProductRule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType()); // Ngắt mạch theo tỷ lệ lỗi
        createProductRule.setCount(0.5); // Nếu 50% request lỗi
        createProductRule.setMinRequestAmount(2); // Cần ít nhất 2 request trong khoảng thời gian để kích hoạt
        createProductRule.setStatIntervalMs(10000); // Khoảng thời gian thống kê (10s)
        createProductRule.setTimeWindow(5); // Thời gian ngắt mạch (5s) sau đó thử lại
        
        rules.add(createProductRule);
        DegradeRuleManager.loadRules(rules);
    }
}
