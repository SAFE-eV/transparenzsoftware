package com.hastobe.transparenzsoftware;

import com.hastobe.transparenzsoftware.verification.xml.Results;
import com.hastobe.transparenzsoftware.verification.xml.Values;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class XsdGenerator {

    public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext jaxbContextValues = JAXBContext.newInstance(Values.class);
        MySchemaOutputResolver sorValues = new MySchemaOutputResolver();
        sorValues.filename = "values.xsd";
        jaxbContextValues.generateSchema(sorValues);

        JAXBContext jaxbContextResult = JAXBContext.newInstance(Results.class);
        MySchemaOutputResolver sorResult = new MySchemaOutputResolver();
        sorResult.filename = "results.xsd";
        jaxbContextResult.generateSchema(sorResult);
    }


}

class MySchemaOutputResolver extends SchemaOutputResolver {
    public String filename;

    public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
        File file = new File(filename);
        StreamResult result = new StreamResult(file);
        result.setSystemId(file.toURI().toURL().toString());
        return result;
    }

}
