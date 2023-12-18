package models.requestModels;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequest {
    private String login;
    private String password;
}
