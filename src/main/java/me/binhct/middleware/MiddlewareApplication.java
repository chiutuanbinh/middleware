package me.binhct.middleware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.binhct.middleware.article.ArticleModel;
import me.binhct.middleware.article.ArticleMongoRepository;
import me.binhct.middleware.cluster.ClusterModel;
import me.binhct.middleware.cluster.ClusterMongoRepository;

@SpringBootApplication
public class MiddlewareApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiddlewareApplication.class, args);
	}

}
