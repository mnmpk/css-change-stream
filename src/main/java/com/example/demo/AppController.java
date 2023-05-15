package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private AsyncService asyncService;

    @RequestMapping("/insert")
    public void insert() throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			asyncService.insertOne("inventory");
		}
    }
}
