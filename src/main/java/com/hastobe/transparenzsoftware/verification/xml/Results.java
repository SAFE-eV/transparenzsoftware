package com.hastobe.transparenzsoftware.verification.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "results")
@XmlAccessorType(XmlAccessType.FIELD)
public class Results {

    @XmlElement(name = "result")
    private List<Result> results;

    public List<Result> getResults() {
        if (results == null) {
            results = new ArrayList<>();
        }
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
