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
    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getPost_link() {
        return post_link;
    }

    public void setPost_link(String post_link) {
        this.post_link = post_link;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
