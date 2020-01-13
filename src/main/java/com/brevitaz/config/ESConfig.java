package com.brevitaz.config;

import com.brevitaz.elasticsearch.ElasticOperations;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ESConfig {
    final static Logger LOGGER = LoggerFactory.getLogger(ESConfig.class);

    @Value("${app.elasticsearch.cluster-name}")
    private String esClusterName;

    @Value("${app.elasticsearch.cluster-nodes}")
    private String nodes;

    @Bean
    /*@Profile("prod")*/
    public Client client() {

        Settings settings = Settings.builder()
                .put("cluster.name", esClusterName)
                .build();

        String nodesList[] = nodes.trim().split(",");

        TransportClient client = new PreBuiltTransportClient(settings);

        for (String node : nodesList) {
            String hostAndPort[] = node.trim().split(":");
            try {
                String esHost = hostAndPort[0];
                Integer esPort = Integer.parseInt(hostAndPort[1]);

                if (!esHost.isEmpty()) {
                    client.addTransportAddress(new TransportAddress(InetAddress.getByName(esHost),esPort));
                }
                    //client.addTransportAddress(new TransportAddress(InetAddress.getByName(esHost), esPort));

            } catch (Exception e) {
                LOGGER.error("Invalid node  configuration. Expected format is host:port");
            }
        }

        return client;
    }

}