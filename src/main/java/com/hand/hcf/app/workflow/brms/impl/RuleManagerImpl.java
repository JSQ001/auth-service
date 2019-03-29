package com.hand.hcf.app.workflow.brms.impl;

import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import liquibase.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cuikexiang on 2017/3/15.
 */
@Component
@Slf4j
public class RuleManagerImpl {
    private Map<String, StatelessKieSession> cache = new ConcurrentHashMap<String, StatelessKieSession>();

    private Message.Level[] error = new Message.Level[]{Message.Level.WARNING, Message.Level.ERROR};

    private static final String DEFAULT_RULE_NAME = "DEFAULT_RULE_NAME";

    // modify by mh.z 20190227 包名变了，否则cache.get(drlMd5).getKieBase().getKiePackage(DEFAULT_PACKAGE_NAME)返回null
    //private static final String DEFAULT_PACKAGE_NAME = "com.hand.hcf.app.brms.web.rest.dto";
    private static final String DEFAULT_PACKAGE_NAME = "com.hand.hcf.app.workflow.brms.dto";
    // END modify by mh.z

    private static final String DEFAULT_PACKAGE_NAME_NULL = "DEFAULT_PACKAGE_NAME_NULL";


    public StatelessKieSession createKieSessionFromDRL(final String drlContent) {

        String ruleName = DEFAULT_RULE_NAME;
        try {
            DrlParser parser = new DrlParser();
            PackageDescr p = parser.parse(false, drlContent);
            ruleName = p == null || CollectionUtils.isEmpty(p.getRules()) ? DEFAULT_RULE_NAME : p.getRules().get(0).getName();
        } catch (DroolsParserException e) {
            log.error("转化DRL规则异常", e);
            e.printStackTrace();
        }

        String drlMd5 = MD5Util.computeMD5(drlContent);
        if (cache.containsKey(drlMd5)) {
            log.info("CASHE RULE NAME:{},AND MAPPING RULE INFO IN KIESESSION:{}", ruleName, cache.get(drlMd5).getKieBase().getKiePackage(DEFAULT_PACKAGE_NAME) != null ? cache.get(drlMd5).getKieBase().getKiePackage(DEFAULT_PACKAGE_NAME).getRules().toString() : DEFAULT_PACKAGE_NAME_NULL);
            if(StringUtils.indexOf(cache.get(drlMd5).getKieBase().getKiePackage(DEFAULT_PACKAGE_NAME).getRules().toString(), ruleName) == -1){
                log.warn("RULE NAME:{} GET A DIFFERENT RULE FROM CACHE", ruleName);
            }
            return cache.get(drlMd5);
        }

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drlContent, ResourceType.DRL);
        verifyRule(kieHelper);
        StatelessKieSession kieSession = kieHelper.build().newStatelessKieSession();
        cache.put(drlMd5, kieSession);
        log.info("RULE NAME:{},AND MAPPING RULE INFO IN KIESESSION:{}", ruleName, cache.get(drlMd5).getKieBase().getKiePackage(DEFAULT_PACKAGE_NAME) != null ? cache.get(drlMd5).getKieBase().getKiePackage(DEFAULT_PACKAGE_NAME).getRules().toString() : DEFAULT_PACKAGE_NAME_NULL);
        return kieSession;
    }

    private StatelessKieSession createKieSessionCachable(final String drlContent) {
        return null;
    }

    public void verifyRuleFromDRL(final String drlContent) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drlContent, ResourceType.DRL);
        verifyRule(kieHelper);
    }


    private void verifyRule(KieHelper kieHelper) {
        Results results = kieHelper.verify();
        if (results.hasMessages(error)) {
            List<Message> messages = results.getMessages(error);
            StringBuffer sb = new StringBuffer();
            for (Message message : messages) {
                String error = "Error: " + message.getText();
                log.error(error);
                sb.append(error).append("\r\n");
            }
            throw new ValidationException(new ValidationError("rule", "rule content is not valid:" + sb));
        }
    }

    public <T> void excuteInsertObject(StatelessKieSession ksession, T... object) {
        ksession.execute(Arrays.asList(object));
    }
}
