package saturday.config;

import com.restfb.DefaultFacebookClient;
import com.restfb.Version;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class SocialConfig {

    @Bean
    @Scope(value="request", proxyMode= ScopedProxyMode.TARGET_CLASS)
    public DefaultFacebookClient facebook() {
       return new DefaultFacebookClient(Version.VERSION_2_5);
    }
}
