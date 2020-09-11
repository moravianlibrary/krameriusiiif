package cz.rumanek.kramerius.krameriusiiif.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * Default http security config is disabled
     */
    public SecurityConfig(){
        super(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .anonymous().and()
                .exceptionHandling().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST).denyAll()
                .antMatchers(HttpMethod.PUT).denyAll()
                .antMatchers(HttpMethod.DELETE).denyAll()
                .antMatchers(HttpMethod.GET, "/**")
                .permitAll();
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.httpFirewall(new DefaultHttpFirewall());
    }

}
