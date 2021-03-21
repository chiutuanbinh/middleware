package me.binhct.middleware.article;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import me.binhct.middleware.common.Response;

@RestController
public class ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @GetMapping(value = "/article/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getArticles(@RequestParam(name = "limit", defaultValue = "10") int count) {
        Response response = new Response();
        try {
            List<Article> result = ArticleModel.INSTANCE.getLatest(count);
            response.setValue(result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }

    @GetMapping(value = "/article/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getArticle(@PathVariable String id) {
        Response response = new Response();
        try {
            response.setValue(ArticleModel.INSTANCE.get(id));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }

    @GetMapping(value = "/publisher/{publisher}/article", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getByPublisher(@RequestParam(name = "limit", defaultValue = "10") int count,
            @PathVariable String publisher) {
        Response response = new Response();
        try {
            response.setValue(ArticleModel.INSTANCE.getByPubisher(publisher, count));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/publisher", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getAllPublisher() {
        Response response = new Response();
        try {
            response.setValue(ArticleModel.INSTANCE.getAllPublisher());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }
}
