package br.com.gabrielferreira.evento.config;

import br.com.gabrielferreira.evento.exception.handler.ServiceHandlerAutenticacao;
import br.com.gabrielferreira.evento.exception.handler.ServiceHandlerPermissao;
import br.com.gabrielferreira.evento.filter.JWTValidatorTokenFilter;
import br.com.gabrielferreira.evento.service.security.TokenService;
import br.com.gabrielferreira.evento.service.security.UsuarioAutenticacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static br.com.gabrielferreira.evento.utils.ConstantesUtils.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UsuarioAutenticacaoService usuarioAutenticacaoService;

    private final ObjectMapper objectMapper;

    private final TokenService tokenService;

    //Config seguranca
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        return http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Não é pra criar sessão
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JWTValidatorTokenFilter(tokenService, usuarioAutenticacaoService), UsernamePasswordAuthenticationFilter.class) // Verificar se o token está valido cada requisição
                .authenticationProvider(new AppAuthenticationProvider(usuarioAutenticacaoService, passwordEncoder()))
                .authorizeHttpRequests(auth -> auth.requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, "/login/**")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, API_CIDADES)).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, API_EVENTOS)).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, API_CIDADES)).hasRole(ADMIN)
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, API_CIDADES)).hasRole(ADMIN)
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, API_CIDADES)).hasRole(ADMIN)
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST, API_EVENTOS)).hasAnyRole(ADMIN, CLIENT)
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.PUT, API_EVENTOS)).hasAnyRole(ADMIN, CLIENT)
                        .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.DELETE, API_EVENTOS)).hasAnyRole(ADMIN, CLIENT)
                        .requestMatchers(new MvcRequestMatcher(introspector, API_USUARIOS)).hasRole(ADMIN)
                        .requestMatchers(new MvcRequestMatcher(introspector, API_PERFIS)).hasRole(ADMIN)
                        .anyRequest().authenticated())
                .exceptionHandling(eh -> eh.authenticationEntryPoint(new ServiceHandlerAutenticacao(objectMapper)) // Mensagem personalizada quando não for autenticado
                .accessDeniedHandler(new ServiceHandlerPermissao(objectMapper))) // Mensagem personalizada quando não tiver permissão
                .build();
    }

    // Config a partir da autenticação
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Config de recurso estaticos
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"),
                new AntPathRequestMatcher("/**.html")));
    }

    // Config senha criptografada
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
