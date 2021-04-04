package me.binhct.middleware.cluster;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import me.binhct.middleware.common.Response;
import me.binhct.middleware.common.Response.Error;

@RestController
public class ClusterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterController.class);

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/cluster/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getLatestCluster(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        Response response = new Response();

        try {
            List<Cluster> clusters = ClusterModel.INSTANCE.getLatest(limit);
            if (clusters != null) {
                response.setValue(clusters);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping(value = "/cluster/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response setCluster(@RequestBody Cluster cluster, @PathVariable String id) {
        Response response = new Response();
        try {
            long res = ClusterModel.INSTANCE.addCluster(cluster);
            Error error = new Error();
            error.setCode(String.valueOf(res));
            response.setError(error);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }
}
