/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cjsd32pdfExplorer;

import java.util.HashMap;

/**
 *
 * @author dale
 */
public interface Notifiable {
    public void notify(String notification);
    public void reportMetadata(HashMap metadata);
}
