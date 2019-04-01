package com.hand.hcf.app.workflow.config;

import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.event.DebugProcessEventListener;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.hand.hcf.app.workflow.brms"})
public class DroolsConfiguration {

    private Logger logger = LoggerFactory.getLogger(DroolsConfiguration.class);

    @Autowired
    KieContainer kieContainer;

    @Bean
    public KieContainer kieContainer() {
        return KieServices.Factory.get().getKieClasspathContainer();
    }

    @Bean
    public KieSession kieSession() {
        return kieContainer.newKieSession("ksession-rules");
    }

    @Bean
    public StatelessKieSession statelessKieSession() {
        StatelessKieSession statelessKieSession = kieContainer.getKieBase("rules").newStatelessKieSession();
        if (logger.isDebugEnabled()) {
            statelessKieSession.addEventListener(new DebugRuleRuntimeEventListener());
            statelessKieSession.addEventListener(new org.drools.core.event.DebugRuleRuntimeEventListener());
            statelessKieSession.addEventListener(new DebugProcessEventListener());
            statelessKieSession.addEventListener(new DebugAgendaEventListener());
            statelessKieSession.addEventListener(new DebugRuleRuntimeEventListener());
        }
        return statelessKieSession;
    }

}
