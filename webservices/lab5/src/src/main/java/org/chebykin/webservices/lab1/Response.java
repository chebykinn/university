package org.chebykin.webservices.lab1;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response {
    int id;
    boolean status;

    public Response() {
        this.id = 0;
        this.status = false;
    }

    public Response(int id, boolean status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
