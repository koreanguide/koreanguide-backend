package kr.yuns.mail.task;

import kr.yuns.mail.data.enums.MailType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MailTask {
    private MailType mailType;
    private String email;

    public String toJson() {
        return "{" + "\"mailType\": " + mailType + ", \"email\": \"" + email + "\"}";
    }
}