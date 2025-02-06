package com.example.demo;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.StringConfiguration;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Component
public class CacheManagerComponent {
        Random random;
        RemoteCacheManager remoteCacheManager;
        RemoteCache<String, Customer> remoteCache;

    //Essendo questo un @Component il suo costruttore verrà chiamato in fase di startup ed invierà al server lo schema protobuf della cache
    public CacheManagerComponent( RemoteCacheManager remoteCacheManager) throws IOException {
        random = new Random();
        this.remoteCacheManager = remoteCacheManager;
        // Upload the generated schema in the server
        RemoteCache<String, String> metadataCache = this.remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
        GeneratedSchema schema = new CustomerSchemaBuilderImpl();
        metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());
        System.out.println("Invio lo schema protobuf al server");

        // Recupera la cache esistente
        RemoteCache<String, Customer> cache = remoteCacheManager.getCache("customers");

        if (cache == null) {
            // Se non esiste, la crea usando customers.xml
            System.out.println(" Cache 'customers' non trovata, creando da XML...");
            String xml = Files.readString(Paths.get(CacheManagerComponent.class.getClassLoader().getResource("customers.xml").getPath()));
            cache = remoteCacheManager.administration().getOrCreateCache("customers", new StringConfiguration(xml));

            if (cache == null) {
                throw new IllegalStateException(" Errore nella creazione della cache 'customers'.");
            }
            System.out.println(" Cache 'customers' creata con successo!");
        } else {
            System.out.println(" Cache 'customers' già esistente.");
        }
        this.remoteCache = this.remoteCacheManager.getCache(Data.CACHE_NAME);

    }

    @Scheduled(fixedDelay = 1000)
    public void createOne() {
        int cacheId = this.random.nextInt();
        String customerId = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        String customerName = UUID.randomUUID().toString().replace("-", "").substring(0, 10);


        //testiamo l'inserimento della cache
        Customer v = new Customer(customerId, customerName);
        remoteCache.put(Integer.toString(cacheId), v);
        System.out.println("Inserimento: "+v);


    }

    @Scheduled(fixedDelay = 5000)
    public void printSize() {

        System.out.println(Data.CACHE_NAME + " size is: "+ remoteCache.size());


    }
}
