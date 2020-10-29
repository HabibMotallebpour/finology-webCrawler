package com.finology.crawler.repository;

import com.finology.crawler.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author H.Motallebpour
 */
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
}
