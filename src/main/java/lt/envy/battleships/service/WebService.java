package lt.envy.battleships.service;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public abstract class WebService {

    protected String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        return writer.toString();
    }
}
