package ru.marthastudios.coinjoiner.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("oauth.vk")
@Getter
@Setter
public class VkOAuthProperty {
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String scopes;
}
