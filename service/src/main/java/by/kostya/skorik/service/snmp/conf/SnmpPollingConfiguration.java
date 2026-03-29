package by.kostya.skorik.service.snmp.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Executor;

@Configuration
public class SnmpPollingConfiguration {

    @Bean
    public RestClient restClient(){
        return RestClient.builder().build();
    }

    @Bean(name = "SnmpExecutor")
    public Executor executor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("SNMP-Async-");
        executor.initialize();
        return executor;
    }
}