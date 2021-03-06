package me.binhct.middleware.article;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleController {
    @GetMapping(value = "/article/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Article> getArticles(@RequestParam(name = "limit", defaultValue = "10") long count) {
        return ArticleModel.INSTANCE.getLatest(count);
    }

    @GetMapping(value = "/article/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Article getArticle(@PathVariable String id){
        return ArticleModel.INSTANCE.get(id);
    }
}
