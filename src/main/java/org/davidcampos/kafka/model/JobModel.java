package org.davidcampos.kafka.model;

import java.io.IOException;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class JobModel implements ToXContent {
    public String company_name;
    public String post_link;
    public String job;
    public String salary;
    public String location;
    public String deadline;
    public JobModel(String company_name,String post_link,String job,String salary,String location,String deadline){
        this.company_name= company_name;
        this.post_link= post_link;
        this.job= job;
        this.salary= salary;
        this.location= location;
        this.deadline= deadline;
    }
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        return builder.startObject()
          .field("conpany_name", company_name)
          .field("post_link", post_link)
          .field("job", job)
          .field("salary", salary)
          .field("location", location)
          .field("deadline", deadline)
          .endObject();
    }
}
