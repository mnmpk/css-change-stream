package com.example.demo;

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;

@Component
public class AppService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    private void startChangeStream() {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                logger.info("Start watching inventory");
                ChangeStreamIterable<Document> changeStream = mongoTemplate.getCollection("inventory").watch();

                changeStream.forEach(event -> {
                    try {
                        logger.info(event.toString());
                        switch (event.getOperationType()) {
                            case INSERT:
                                logger.info("New insert come in, log the record");
                                MongoCollection<Document> logColl = mongoTemplate.getCollection("logs");
                                Document d = logColl.findOneAndUpdate(Filters.empty(),
                                        Updates.combine(Updates.inc("seq", 1), Updates.set("resumeToken", event.getResumeToken())),
                                        new FindOneAndUpdateOptions().upsert(true));
                                int count = 0;
                                if (d != null) {
                                    count = d.getInteger("seq");
                                }

                                // log events
                                // long count = logColl.countDocuments()+1;
                                // Document d = new Document();
                                // d.put("resumeToken", event.getResumeToken());
                                // logger.info(logColl.insertOne(d).toString());
                                logger.info("Update inventory " + event.getDocumentKey() + " seq no. to " + count);
                                logger.info(mongoTemplate.getCollection("inventory").updateOne(event.getDocumentKey(), Updates.set("seq", count)).toString());

                                break;
                            case UPDATE:
                                logger.info(event.getUpdateDescription().toString());
                                break;
                            default:
                                break;
                        }
                    } catch (Exception ex) {
                        logger.error("error", ex);
                    }
                });
            }
        });
    }
}
