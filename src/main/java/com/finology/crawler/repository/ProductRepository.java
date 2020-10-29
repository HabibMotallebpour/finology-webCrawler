package com.finology.crawler.repository;

import com.finology.crawler.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author H.Motallebpour
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
