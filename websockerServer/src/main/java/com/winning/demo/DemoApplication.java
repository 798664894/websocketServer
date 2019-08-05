package com.winning.demo;

import com.winning.demo.webSocket.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DemoApplication.class);
        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);
        //解决WebSocket不能注入的问题
        WebSocketServer.setApplicationContext(configurableApplicationContext);
	}


}
