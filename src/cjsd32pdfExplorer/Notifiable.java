package cjsd32pdfExplorer;

import java.util.HashMap;

/**
 * @author Colin Stevens 
 */
public interface Notifiable {
    public void notify(String notification);
    public void reportMetadata(HashMap metadata);
}
