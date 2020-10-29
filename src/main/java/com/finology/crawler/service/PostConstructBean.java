package com.finology.crawler.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author H.Motallebpour
 */
@Component
@Log4j2
public class PostConstructBean {
    @Autowired
    private CrawlerService crawlerService;

    @PostConstruct
    public void init() {
        log.info("Starting........");
        crawlerService.getPageLinks("http://magento-test.finology.com.my/breathe-easy-tank.html");
    }
}
