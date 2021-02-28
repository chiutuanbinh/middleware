package me.binhct.middleware.goldprice;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoldPriceController {

    @GetMapping(value = "/price/{gType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public GoldPrice getGoldPrice(@PathVariable String gType){
        return GoldPriceModel.Instance.getPrice(gType);
    }
}
