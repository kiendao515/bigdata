package org.davidcampos.kafka.producer;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import org.davidcampos.kafka.model.Job;

import com.google.gson.Gson;

public class main {
    public static void main(String[] args) {
        Gson gson = new Gson();

        try {
            String data="";
//            URL url = new URL("https://dl.dropboxusercontent.com/s/kx753tfonwsn45y/topcv.json?dl=0");
//            Scanner s = new Scanner(url.openStream());

            URL url = new URL("https://dl.dropboxusercontent.com/s/kx753tfonwsn45y/topcv.json?dl=0");
            StringBuilder builder = new StringBuilder();

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    builder.append(str);
                }
            }
            String jsonStr = builder.toString();
            System.out.println(jsonStr);
            ObjectMapper mapper = new ObjectMapper();
            List<Job> jobs = mapper.readValue(jsonStr, new TypeReference<List<Job>>(){});
            System.out.println(jobs.size());
            //FileReader reader = new FileReader("https://dl.dropboxusercontent.com/s/kx753tfonwsn45y/topcv.json?dl=0");

            // convert JSON array to list of users
//            ObjectMapper mapper = new ObjectMapper();
//            List<Job> jobs = mapper.readValue(s.nextLine().toString(), new TypeReference<List<Job>>(){});
            //List<Job> jobs = new Gson().fromJson(s.nextLine().toString(), new TypeToken<List<Job>>() {}.getType());

             //print users
//            for (int i = 0; i < jobs.size(); i++) {
//                System.out.println(jobs.get(i).company_name);
//            }
//            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
