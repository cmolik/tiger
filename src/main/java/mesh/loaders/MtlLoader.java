/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mesh.loaders;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import scene.surface.Appearance;

/**
 *
 * @author cmolikl
 */
public class MtlLoader {

    public static Map<String, Appearance> loadFile(String filename) throws FileNotFoundException, IOException {

        BufferedReader br = new BufferedReader(new FileReader(filename));
        HashMap<String, Appearance> appearances = new HashMap<String, Appearance>();

        Appearance appearance = null;

        Pattern commentpat = Pattern.compile("^#");
        Pattern newmtlpat = Pattern.compile("^newmtl\\s+(\\S*)$");
        Pattern kapat = Pattern.compile("^Ka\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)$");
        Pattern kdpat = Pattern.compile("^Kd\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)$");
        Pattern kspat = Pattern.compile("^Ks\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)$");
        Pattern dpat = Pattern.compile("^d\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)$");
        Pattern trpat = Pattern.compile("^Tr\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)$");
        Pattern nspat = Pattern.compile("^Ns\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)$");
        Pattern illumpat = Pattern.compile("^illum\\s+(\\d+)$");

        String line;
        while ((line = br.readLine()) != null) {
            //System.out.println("line: " + line);
            Matcher commentm = commentpat.matcher(line);
            Matcher newmtlm = newmtlpat.matcher(line);
            Matcher kam = kapat.matcher(line);
            Matcher kdm = kdpat.matcher(line);
            Matcher ksm = kspat.matcher(line);
            Matcher dm = dpat.matcher(line);
            Matcher trm = trpat.matcher(line);
            Matcher nsm = nspat.matcher(line);
            Matcher illumm = illumpat.matcher(line);


            if (commentm.find()) {
                //System.out.println("Comment line.");
            } else if (newmtlm.find()) {
                if (appearance != null) {
                    appearances.put(appearance.getName(), appearance);
                }
                appearance = new Appearance(newmtlm.group(1));
            } else if (kam.find()) {
                if (appearance != null) {
                    appearance.setAmbient(Float.parseFloat(kam.group(1)), Float.parseFloat(kam.group(2)), Float.parseFloat(kam.group(3)));
                }
            } else if (kdm.find()) {
                if (appearance != null) {
                    appearance.setDiffuse(Float.parseFloat(kdm.group(1)), Float.parseFloat(kdm.group(2)), Float.parseFloat(kdm.group(3)));
                }
            } else if (ksm.find()) {
                if (appearance != null) {
                    appearance.setSpecular(Float.parseFloat(ksm.group(1)), Float.parseFloat(ksm.group(2)), Float.parseFloat(ksm.group(3)));
                }
            } else if (dm.find()) {
                if (appearance != null) {
                    appearance.setAlpha(Float.parseFloat(dm.group(1)));
                }
            } else if (trm.find()) {
                if (appearance != null) {
                    appearance.setAlpha(Float.parseFloat(trm.group(1)));
                }
            } else if (nsm.find()) {
                if (appearance != null) {
                    appearance.setShines(Float.parseFloat(nsm.group(1)));
                }
            } else if (illumm.find()) {
                if (appearance != null) {
                    appearance.setModel(Integer.parseInt(illumm.group(1)));
                }
            }
        }
        appearances.put(appearance.getName(), appearance);
        return appearances;
    }
}
