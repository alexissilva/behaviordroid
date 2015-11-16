package behaviordroid.file;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Alexis on 03-06-15.
 */
public abstract class FileReader {

    protected File file;
    protected DefaultHandler documentHandler = generateDocumentHandler();


    public FileReader(String filePath) throws IOException {

        file = new File(filePath);
        if (!file.exists()) {
            throw new IOException(filePath + " doesn't exist.");
        }
    }

    protected abstract DefaultHandler generateDocumentHandler();

    public abstract Object read() throws Exception;

    protected void parseFile() throws IOException, SAXException, ParserConfigurationException {

        // Basic SAX parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(file, documentHandler);

    }

    public File getFile() {
        return file;
    }
}
