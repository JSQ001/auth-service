package com.hand.hcf.app.workflow.brms.dto;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("restriction")
@XmlRootElement(name = "messages")
public class CustomMessagesDTO {
    private final List<CustomMessageDTO> messages = Collections.synchronizedList(new ArrayList<CustomMessageDTO>());

    public Collection<CustomMessageDTO> getMessages() {
        return Collections.unmodifiableCollection(messages);
    }

    public void addMessage(String ruleFiredResult, String message) {
        this.messages.add(new CustomMessageDTO(ruleFiredResult, message));
    }

    public void addMessage(CustomMessageDTO customMessage) {
        this.messages.add(customMessage);
    }

    public boolean hasRuleFired() {
        long cnt = this.messages.stream().filter(c -> c.getFiredRuleFlg().equals(Boolean.TRUE)).count();
        if (cnt > 0) {
            return true;
        }
        return false;
    }
}
