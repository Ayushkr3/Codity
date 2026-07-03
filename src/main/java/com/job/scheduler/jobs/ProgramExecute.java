package com.job.scheduler.jobs;

import org.springframework.stereotype.Component;

import com.job.scheduler.handler.JobHandler;

import jakarta.annotation.PreDestroy;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProgramExecute implements JobHandler {
    
    Process proc = null;
    
    @Override
    public void handle(String payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(payload);
        String p = node.get("path").asString();
        if(p==null){
            //log
            return;
        }
        try {
            proc = new ProcessBuilder(p).start();   
        } catch (Exception e) {
            throw e;   
        }
    }
    @PreDestroy
    public void destroy(){
        if(proc!=null){
            proc.destroy();
        }
    }
}
