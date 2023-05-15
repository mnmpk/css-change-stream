package com.example.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

import lombok.var;

@Component
public class AsyncService {
	private Logger logger = LoggerFactory.getLogger(getClass());
    
	@Autowired
	private MongoTemplate mongoTemplate;
    
	@Async
	public CompletableFuture<Void> insertOne(String collection) throws InterruptedException {
		logger.info(Thread.currentThread().getName() + " start at: " + LocalDateTime.now().toString());
		var c = mongoTemplate.getCollection(collection);
		Document doc = new Document();
		doc.put("i", Thread.currentThread().getName());
		doc.put("t", new Date());
		c.insertOne(doc);
		return CompletableFuture.completedFuture(null);
	}
}
