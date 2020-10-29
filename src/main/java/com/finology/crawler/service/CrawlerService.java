package com.finology.crawler.service;

import com.finology.crawler.model.Product;
import com.finology.crawler.model.Url;
import com.finology.crawler.repository.ProductRepository;
import com.finology.crawler.repository.UrlRepository;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author H.Motallebpour
 */
@Service
@Log4j2
public class CrawlerService {
    private final Set<String> links;
    private final ProductRepository productRepository;
    private final UrlRepository urlRepository;
    private final Set<String> exploredUrls = new HashSet<>();

    @PostConstruct
    public void loadExploredLinks() {
        List<Url> urls = urlRepository.findAll();
        urls.forEach(u -> exploredUrls.add(u.getUrl()));
    }

    @Autowired
    public CrawlerService(ProductRepository productRepository, UrlRepository urlRepository) {
        this.productRepository = productRepository;
        this.urlRepository = urlRepository;
        this.links = new HashSet<>();
    }

    public void getPageLinks(String url) {
        try {
            if (links.add(url)) {
                Url checkedUrl = new Url();
                checkedUrl.setUrl(url);
                if (!exploredUrls.contains(url)) {
                    urlRepository.save(checkedUrl);
                }
                log.info("Checking URL : {}", url);
            }

            Document document = Jsoup.connect(url).get();
            final Elements elements = document.select("a[href]");
            getProductParameters(document, url);
            elements.stream()
                    .filter(e -> !e.attr("abs:href").contains("#"))
                    .filter(e -> e.attr("abs:href").endsWith(".html"))
                    .filter(e -> !links.contains(e.attr("abs:href")))
                    .forEach(e -> getPageLinks(e.attr("abs:href")));

        } catch (IOException e) {
            log.error("can not get page {} document ", url, e);
        }
    }

    private void getProductParameters(Document document, String url) {
        if (exploredUrls.contains(url)) {
            log.info("url {} already been explored", url);
            return;
        }

        Elements productTitle = document.select("div.product-info-main");
        String title;
        String price;
        Product product;

        if (productTitle != null && !productTitle.isEmpty()) {
            product = new Product();
            title = productTitle.get(0).select("h1.page-title").text();
            log.info("title : {}", title);
            product.setTitle(title);
            price = productTitle.get(0).select("span.price").text();
            product.setPrice(price);
            log.info("price : {} ", price);

            Elements productInfo = document.select("div[class = product data items]");
            if (productInfo != null && !productInfo.isEmpty()) {
                String description = productInfo.get(0).getElementById("description").text();
                log.info("Description : {} ", description);
                product.setDescription(description);
                Element additionalElement = productInfo.get(0).getElementById("additional");

                if (additionalElement != null) {
                    StringBuilder extra = new StringBuilder();
                    Elements tr = additionalElement.select("tr");
                    for (Element element : tr) {
                        String th = element.select("th").text();
                        String td = element.select("td").text();
                        extra.append(th).append(" : ").append(td).append(" | ");
                    }
                    extra.delete(extra.lastIndexOf("|"), extra.lastIndexOf(" "));
                    log.info("Extra Information : {}", extra);
                    product.setExtraInformation(extra.toString());
                }
            }
            productRepository.save(product);
        }
    }

}
