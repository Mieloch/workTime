package topworker.config;


import org.jdal.auth.AuthService;
import org.jdal.vaadin.auth.SpringAuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@ComponentScan("topworker.service.impl")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       // http.authorizeRequests().antMatchers("/").hasRole("USER").and().formLogin().loginPage("/login").defaultSuccessUrl("/#!calendar", true).usernameParameter("login").passwordParameter("password").and().csrf().disable();
            http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        try {
            auth.userDetailsService(userDetailsService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public AuthService getAuthService(){
        return new SpringAuthManager();
    }


}
