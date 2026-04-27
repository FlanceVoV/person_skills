package com.flance.framework.modules.webflux_web.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.reactor.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ContextPathCompositeHandler;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Map;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class WebContainerConfig {

    private Integer nettyPort;
    private Integer tomcatPort;
    private String[] controllerPackages;
    private String[] webfluxPackages;
    private String[] mvcDispatcherServletMapping;
    private String[] webfluxDispatcherServletMapping;

    @Value("${server.servlet.context-path:}")
    private String servletContextPath;

    @Value("${server.webflux.context-path:}")
    private String webfluxContextPath;

    @Bean
    public WebServer tomcatMvcServer(ApplicationContext parentContext) {
        var ctx = new AnnotationConfigWebApplicationContext();
        ctx.setParent(parentContext);
        ctx.scan(controllerPackages);
        ctx.register(MvcConfig.class);
        ctx.refresh();

        var factory = new TomcatServletWebServerFactory(tomcatPort);
        factory.setContextPath(servletContextPath);
        var dispatcher = new DispatcherServlet(ctx);

        var webServer = factory.getWebServer(servletContext -> {
            var registration = servletContext.addServlet("dispatcherServlet", dispatcher);
            registration.addMapping(mvcDispatcherServletMapping);
            registration.setLoadOnStartup(1);
        });
        webServer.start();
        return webServer;
    }

//    @Bean
//    public HttpHandler httpHandler(ApplicationContext parentContext) {
//        var ctx = new AnnotationConfigWebApplicationContext();
//        ctx.setParent(parentContext);
//        ctx.register(WebFluxConfig.class);
//        ctx.scan(webfluxPackages);
//        ctx.refresh();
//
//        var dispatcherHandler = new DispatcherHandler();
//        dispatcherHandler.setApplicationContext(ctx);
//        return new HttpWebHandlerAdapter(dispatcherHandler);
//    }

    @Bean
    public HttpHandler httpHandler(ApplicationContext parentContext) {
        var ctx = new AnnotationConfigWebApplicationContext();
        ctx.setParent(parentContext);
        ctx.register(WebFluxConfig.class);
        ctx.scan(webfluxPackages);
        ctx.refresh();

        var dispatcherHandler = new DispatcherHandler();
        dispatcherHandler.setApplicationContext(ctx);
        HttpHandler originalHandler = new HttpWebHandlerAdapter(dispatcherHandler);

        if (webfluxContextPath != null && !webfluxContextPath.isBlank()) {
            return new ContextPathCompositeHandler(
                    Map.of(webfluxContextPath, originalHandler)
            );
        }
        return originalHandler;
    }

    @Bean
    public WebServer nettyWebServer(HttpHandler httpHandler) {
        var factory = new NettyReactiveWebServerFactory(nettyPort);
        var webServer = factory.getWebServer(httpHandler);
        webServer.start();
        log.info("Netty started on port {} (http) with context path '{}'", nettyPort, webfluxContextPath);
        return webServer;
    }

}
