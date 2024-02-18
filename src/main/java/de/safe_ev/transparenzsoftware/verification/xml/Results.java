package de.safe_ev.transparenzsoftware.verification.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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
