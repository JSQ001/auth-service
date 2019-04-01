/**
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * <p>
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * <p>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.hand.hcf.app.workflow.brms.service;

import com.hand.hcf.app.workflow.brms.model.KieBaseEntity;
import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import liquibase.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Intention of this Class is to show how rules can get dynamically and
 * temporary injected to extend an existing kBase.
 *
 * @author <a href="mailto:clichybi@redhat.com">Carsten Lichy-Bittendorf</a>
 * @version $Revision$ $Date$: Date of last commit
 */
@Service
@Slf4j
public class KieSessionService {
    private static KieContainer kieContainer = null;
    private static KieServices kieServices = KieServices.Factory.get();
    private Map<String, StatelessKieSession> cache = new ConcurrentHashMap<String, StatelessKieSession>();
    private Message.Level[] error = new Message.Level[]{Message.Level.WARNING, Message.Level.ERROR};


    /**
     * Builds a <code>Drools</code> {@link KieContainer} from the resources
     * found on the classpath.
     *
     * @return a <code>Drools</code> {@link KieContainer}
     */
    public static synchronized KieContainer getKieContainer() {
        if (kieContainer == null) {
            kieContainer = kieServices.getKieClasspathContainer();
        }
        return kieContainer;
    }

    /**
     * Get a stateless kSession for a given kBase Name. Just used here as
     * reference.
     *
     * @param kBaseName name of the kBase as defined in the kModule.
     * @return a stateless kSession.
     */
    public StatelessKieSession getStatelessKieSession(String kBaseName) {
        return kieContainer.getKieBase(kBaseName).newStatelessKieSession();
    }

    /**
     * Create a stateless kSession for a given kBase extended by dynamically
     * injected rules. A current trade-off is that the GAV coordinates of the
     * kJar including the kBase need to get provided.
     * <p>
     * <br/>
     * <b>Note:</b> As we share the same ReleaseId and KieBaseModel name this
     * operation is synchronized to avoid race conditions.
     *
     * @param group        of GAV coordinates of the kJar including the kBase to used.
     * @param artifact     of GAV coordinates of the kJar including the kBase to used.
     * @param version      of GAV coordinates of the kJar including the kBase to used.
     * @param kBaseName    name of the kBase as defined in the kModule.
     * @param dynamicRules the dynamic rule to inject must be a complete DRL resource
     *                     incl. package and imports.
     * @return a stateless kSession, including rules of kJar and dynamicRules.
     */
    public synchronized StatelessKieSession getExtendedStatelessKieSession(
        String group, String artifact, String version, String kBaseName,
        String dynamicRules, String droolsRuleDetailOid) {

        KieModule baseKieModule = kieServices.getRepository().getKieModule(
            KieServices.Factory.get()
                .newReleaseId(group, artifact, version));

        // Note: this is an in-memory file-system
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        ReleaseId newRid = kieServices.newReleaseId(group, artifact + ".test",
            version);
        kieFileSystem.generateAndWritePomXML(newRid);

        KieModuleModel kModuleModel = kieServices.newKieModuleModel();

        KieBaseModel newKieBaseModel = kModuleModel.newKieBaseModel(
            "kiemodulemodel").setDefault(true).addInclude(kBaseName);

        // add all the packages of the KieModule we base on also to the new one
        Collection<KiePackage> kiePackages = getKieContainer(KieServices.Factory.get().newReleaseId(group, artifact, version)).getKieBase(kBaseName).getKiePackages();
//        Collection<KiePackage> kiePackages = kieContainer.getKieBase(kBaseName).getKiePackages();

        Iterator<KiePackage> kiePackagesIter = kiePackages.iterator();
        while (kiePackagesIter.hasNext()) {
            KiePackage kiePackage = (KiePackage) kiePackagesIter.next();
            newKieBaseModel.addPackage(kiePackage.getName());
        }
        newKieBaseModel.newKieSessionModel("test-session").setDefault(true)
            .setType(KieSessionType.STATELESS);

        log.info(kModuleModel.toXML());
        kieFileSystem.writeKModuleXML(kModuleModel.toXML());

        // even if a KieFileSystem is used the path need to follow rules
        // prefix: "src/main/resources/" extended by KieBaseModel name
        // path itself must be equal one of the package names added to the
        // KieBaseModel
        // postfix: a valid drl filename
        String drlFileName = "src/main/resources/kiemodulemodel/"
            + ((KiePackage) kiePackages.toArray()[0]).getName()
            + "/" + droolsRuleDetailOid + ".drl";
        kieFileSystem.write(drlFileName, dynamicRules);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);

        // add the KieModule we extend as dependency
        kieBuilder.setDependencies(baseKieModule);

        kieBuilder.buildAll(); // kieModule is automatically deployed to
        // KieRepository if successfully built.
        if (kieBuilder.getResults().hasMessages(
            org.kie.api.builder.Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n"
                + kieBuilder.getResults().toString());
        }

        // return a stateless kSession based on the definitions above.
        return kieServices.newKieContainer(newRid).newStatelessKieSession(
            "test-session");
    }

    /**
     * Builds a <code>Drools</code> {@link KieContainer} from {@KieBaseEntity
     * }
     *
     * @return a <code>Drools</code> {@link KieContainer}
     */
    public synchronized KieContainer getKieContainer(KieBaseEntity kieBaseEntity) {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        ReleaseId newRid = kieServices.newReleaseId(kieBaseEntity.getGroup(),
            kieBaseEntity.getArtifact(), kieBaseEntity.getVersion());

        // check if we need to build or already exists
        if (kieBaseEntity.getLastBuild() == null
            || kieBaseEntity.getLastBuild().before(
            kieBaseEntity.getLastUpdated())) {

            kieFileSystem.generateAndWritePomXML(newRid);

            KieModuleModel kModuleModel = kieServices.newKieModuleModel();

            KieBaseModel newKieBaseModel = kModuleModel
                .newKieBaseModel(kieBaseEntity.getKieBaseName());
            newKieBaseModel.addPackage(kieBaseEntity.getPackageName()).setDefault(true);

            newKieBaseModel.newKieSessionModel("stateless-session")
                .setDefault(true).setType(KieSessionType.STATELESS);
            newKieBaseModel.newKieSessionModel("stateful-session")
                .setDefault(true).setType(KieSessionType.STATEFUL);

            log.info(kModuleModel.toXML());
            kieFileSystem.writeKModuleXML(kModuleModel.toXML());

            // even if a KieFileSystem is used the path need to follow rules
            // prefix: "src/main/resources/" extended by KieBaseModel name
            // path itself must be equal one of the package names added to the
            // KieBaseModel
            // postfix: a valid drl filename
            String drlFileName = "src/main/resources/"
                + kieBaseEntity.getKieBaseName() + "/"
                + kieBaseEntity.getPackageName() + "/"
                + kieBaseEntity.getKieBaseName() + ".drl";

            kieFileSystem.write(drlFileName, kieBaseEntity.toString());

            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);

            kieBuilder.buildAll(); // kieModule is automatically deployed to
            // KieRepository if successfully built.
            if (kieBuilder.getResults().hasMessages(
                org.kie.api.builder.Message.Level.ERROR)) {
                throw new RuntimeException("Build Errors:\n"
                    + kieBuilder.getResults().toString());
            }
            kieBaseEntity.setLastBuild(new Date());
        }

        // return a KieContainer based on the definitions above.
        return kieServices.newKieContainer(newRid);
    }

    public KieContainer getKieContainer(ReleaseId rid) {
        return kieServices.newKieContainer(rid);
    }

    public static void logRules(KieSession kieSession) {
        logRules(kieSession.getKieBase().getKiePackages());
    }

    public static void logRules(StatelessKieSession kieSession) {
        logRules(kieSession.getKieBase().getKiePackages());
    }

    private static void logRules(Collection<KiePackage> kiePackages) {
        for (KiePackage nextPackage : kiePackages) {
            Collection<Rule> rules = nextPackage.getRules();
            for (Rule nextRule : rules) {
                log.info("Rule: " + nextRule.getName());
            }
        }
    }

    public static StatelessKieSession createKSession(String kbaseName, String sessionName) {
        KieServices ks = KieServices.Factory.get();

        KieContainer kContainer = ks.getKieClasspathContainer();

        StatelessKieSession kSession = kContainer.newStatelessKieSession(sessionName);

        return kSession;
    }

    public static StatelessKieSession createKSession(String sessionName) {
        return createKSession("separated_rules", sessionName);
    }

    public static StatelessKieSession createKSession() {

        return createKSession("all_rules", "ksession-All");
    }


    public StatelessKieSession createKieSessionFromDRL(final String drlContent) {
        String drlMd5 = MD5Util.computeMD5(drlContent);
        if (cache.containsKey(drlMd5)) {
            return cache.get(drlMd5);
        }

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drlContent, ResourceType.DRL);

        verifyRule(kieHelper);

        StatelessKieSession kieSession = kieHelper.build().newStatelessKieSession();
        cache.put(drlMd5, kieSession);
        return kieSession;
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
}
