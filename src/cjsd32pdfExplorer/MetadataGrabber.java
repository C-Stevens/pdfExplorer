package cjsd32pdfExplorer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javafx.application.Platform;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.xml.DomXmpParser;
import org.apache.xmpbox.xml.XmpParsingException;

/**
 * @author Colin Stevens
 */
public class MetadataGrabber extends Thread {
    Notifiable parent;
    private File sourceFile;
    public Boolean stop = false;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("E, y-M-d h:m:s a z");
    HashMap<String, String> data = new HashMap<>();
    
    PDDocument document = null;
    XMPMetadata metadata = null;
    
    public MetadataGrabber(File sourceFile, Notifiable parent) {
        this.sourceFile = sourceFile;
        this.parent = parent;
    }
    
    @Override
    public void run() {
        try {
            try {
                document = PDDocument.load(sourceFile);
            } catch (IOException | NullPointerException e) {
                doNotify("Error: Could not read PDF.");
                return;
            }

            PDDocumentCatalog catalog = document.getDocumentCatalog();
            PDMetadata meta = catalog.getMetadata();
            if (meta != null) {
                DomXmpParser xmpParser = null;
                try {
                    xmpParser = new DomXmpParser();
                } catch (XmpParsingException ex) {
                    doNotify("Error: Failed to initialize XML parser.");
                }

                try {
                    metadata = xmpParser.parse(meta.createInputStream());
                } catch (IOException | XmpParsingException ex) {
                    doNotify("Error: Failed to parse XML metadata.");
                }


                DublinCoreSchema dc = metadata.getDublinCoreSchema();
                if (dc != null) {
                    data.put("title", dc.getTitle());
                    data.put("description", dc.getDescription());
                    try { // These tend to throw exceptions for some reason
                        data.put("creators", dc.getCreators().toString());
                        data.put("subjects", dc.getSubjects().toString());
                    } catch (NullPointerException e) {
                        data.put("creators", null);
                        data.put("subjects", null);
                    }
                }

                XMPBasicSchema basic = metadata.getXMPBasicSchema();
                if (basic != null) {
                    data.put("create_date", dateFormatter.format(basic.getCreateDate().getTime()));
                    data.put("modify_date", dateFormatter.format(basic.getModifyDate().getTime()));
                    data.put("creator_tool", basic.getCreatorTool().toString());
                }
            } else {
                PDDocumentInformation information = document.getDocumentInformation();
                data.put("title", information.getTitle());
                data.put("subject", information.getSubject());
                data.put("author", information.getAuthor());
                data.put("creator", information.getCreator());
                data.put("producer", information.getProducer());
            }
        } finally {
            if(document != null) {
                try {
                    document.close();
                } catch (IOException ex) {
                   doNotify("Error: Failed to close file.");
                   return;
                }
                doNotify("Metadata succesfully extracted.");
                reportMetadata(data);
            }
        }
    }
    
    public void end() {
        stop = true;
    }
    
    public void doNotify(String message) {
        if (parent != null) {
            Platform.runLater(() -> {
                parent.notify(message);
            });
        }
    }
    
    public void reportMetadata(HashMap data) {
        if (parent != null) { // No need for a runLater here since this will be called when everything is finished.
            parent.reportMetadata(data);
        }
    }
}
