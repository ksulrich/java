/**
 * $Id: Element.java,v 1.11 2007/12/29 18:47:46 klaus Exp $
 */
package com.danet.ulrich.pictures;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Element implements Comparable<Object> {
    // ------------------------------ FIELDS ------------------------------

    private String title;
    private BufferedImage image;
    private String desc;
    //private File baseDir;
    //private String jpgFileName;
    private File jpgFile;
    private boolean mark;

    // -------------------------- STATIC METHODS --------------------------

    public final static Element[] readElements(File imageDir) throws IOException {
        if (!imageDir.exists()) {
            throw new FileNotFoundException("Directory " + imageDir.getName() + " does not exist");
        }
        List<String> jpgFiles = new ArrayList<String>();
        getJpgFileNames(jpgFiles, imageDir);

        Set<Element> elements = new TreeSet<Element>();
        for (int i = 0; i < jpgFiles.size(); i++) {
            Element element = new Element(new File(jpgFiles.get(i).toString()));
            elements.add(element);
        }
        return (Element[]) elements.toArray(new Element[elements.size()]);
    }

    private static void getJpgFileNames(List<String> fileNames, File imageDir) {
        String[] allFileNames = imageDir.list();
        for (int i = 0; i < allFileNames.length; i++) {
            File file = new File(imageDir.getPath() + File.separator + allFileNames[i]);
            if (file.isDirectory()) {
                getJpgFileNames(fileNames, file);
            }
        }
        String[] files = imageDir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.endsWith("-thumb.jpg")) {
                    return false;
                }
                if (name.endsWith(".jpg")) {
                    return true;
                }
                if (name.endsWith(".JPG")) {
                	return true;
                }
                return false;
            }
        });
        for (int i = 0; i < files.length; i++) {
            fileNames.add(imageDir.getPath() + File.separator + files[i]);
        }
    }

    // --------------------------- CONSTRUCTORS ---------------------------

    public Element(File jpgFile) {
        this.jpgFile = jpgFile;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    //    public File getBaseDir() {
    //        return baseDir;
    //    }

    public File getJpgFile() {
        return jpgFile;
    }

    public String getDesc() throws IOException {
        if (desc == null) {
            readText();
        }
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private void readText() throws IOException {
        String text = read(getDescriptionFile());

        this.title = extractTitle(text);
        this.desc = extractDesc(text);
    }

    private static String read(File descFile) throws IOException {
        StringBuffer sb = new StringBuffer();
        if (descFile.exists()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(descFile)));
            String input;
            while ((input = br.readLine()) != null) {
                sb.append(input + "\n");
            }
        }
        return sb.toString();
    }

    private File getDescriptionFile() {
        String jpgFileName = jpgFile.getName();
        String fileName = jpgFileName.substring(0, jpgFileName.lastIndexOf("."));
        return new File(getDescriptionDir().getPath() + File.separator + fileName + ".txt");
    }

    private String extractTitle(String text) {
        int nlPos = 0;
        if (text != null) {
            nlPos = text.indexOf('\n');
            if (nlPos >= 0) {
                nlPos++;
            } else {
                nlPos = text.length();
            }
        }
        return text.substring(0, nlPos).trim();
    }

    private String extractDesc(String text) {
        return text.substring(text.indexOf('\n') + 1).trim();
    }

    public BufferedImage getImage() {
        return this.image;
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getTitle() throws IOException {
        if (title == null) {
            readText();
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return jpgFile.toString();
    }

    // ------------------------ CANONICAL METHODS ------------------------

    public String toString() {
        String parent = jpgFile.getParent();
        int index = parent.lastIndexOf(File.separatorChar);
	if (index > 0) parent = parent.substring(index + 1);
        return parent + File.separatorChar + jpgFile.getName();
    }

    // ------------------------ INTERFACE METHODS ------------------------


    // --------------------- Interface Comparable ---------------------

    public int compareTo(Object o) {
        Element e1 = (Element) o;
        String name1 = e1.jpgFile.toString();
        return (jpgFile.toString().compareTo(name1));
    }

    // -------------------------- OTHER METHODS --------------------------

    private File getDescriptionDir() {
        File descDir = new File(jpgFile.getParentFile().getPath() + File.separator + "description");
        if (!descDir.exists()) {
            descDir.mkdirs();
        }
        return descDir;
    }

    public void save(String title, String desc) throws IOException {
        
        if ((title.equals(getTitle())) && desc.equals(getDesc())) {
            return;
        }
        System.out.println("Save: " + toStringDebug());
        this.title = title;
        this.desc = desc;

        File descFile = getDescriptionFile();
        BufferedWriter bout = null;
        try {
            bout = new BufferedWriter(new FileWriter(descFile));
            bout.write(title);
            bout.newLine();
            bout.newLine();
            bout.write(desc);
            bout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toStringDebug() {
        return "com.danet.ulrich.pictures.Element{" +
            "title='" + title + "'" +
            ", desc='" + desc + "'" +
            ", jpgFile=" + jpgFile.toString() +
            ", image=" + image +
            "}";
    }

    public boolean deleteJpgFile() {
        String oldName = jpgFile.toString();
        String newName = oldName + ".deleted";
        return new File(oldName).renameTo(new File(newName));

    }

    public void flushImage() {
        if (image != null) image.flush();
        image = null;
    }

    public void mark() {
        this.mark = !this.mark;
    }

    public boolean isMark() {
        return mark;
    }

    public void copy(File dir) throws IOException {
        if (isMark()) {
            byte[] buf = new byte[4096];
            String parent = jpgFile.getParent();
            String year = parent.substring(parent.lastIndexOf(File.separator)+1);
            FileInputStream fis = new FileInputStream(jpgFile);
            File outFile = new File(dir.toString() + File.separator + year + "-" + jpgFile.getName());
            outFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outFile);
            int len = -1;
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
        }
    }
}