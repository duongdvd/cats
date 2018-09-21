package jp.co.willwave.aca.model.enums;

import org.springframework.web.bind.annotation.RequestMethod;

public enum HttpMethod {
    UNKNOWN(-1), POST(0), GET(1);

    private Integer method;

    HttpMethod(Integer method) {
        this.method = method;
    }

    public Integer getMethod() { return this.method; }

    public void setMethod(Integer method) {
        this.method = method;
    }

    public static HttpMethod getByName(String method) {
        switch (method) {
            case "POST": return HttpMethod.POST;
            case "GET": return HttpMethod.GET;
            default: return HttpMethod.UNKNOWN;
        }
    }
}
