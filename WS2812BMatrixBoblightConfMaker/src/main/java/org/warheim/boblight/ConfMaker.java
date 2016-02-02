/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.warheim.boblight;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * based on:
 * https://code.google.com/archive/p/boblight/wikis/boblightconf.wiki
 * http://www.tweaking4all.com/home-theatre/xbmc/xbmc-boblight-openelec-ws2811-ws2812/
 *
 * @author andy
 */
public class ConfMaker {

    Preferences p;

    public ConfMaker() throws IOException {
        p = new Preferences();
    }
    
    static class Light {
        int number, hscanStart, hscanEnd, vscanStart, vscanEnd;
        String internalCode;

        public Light(int number, String internalCode, int hscanStart, int hscanEnd, int vscanStart, int vscanEnd) {
            this.number = number;
            this.internalCode = internalCode;
            this.hscanStart = hscanStart;
            this.hscanEnd = hscanEnd;
            this.vscanStart = vscanStart;
            this.vscanEnd = vscanEnd;
        }
        
    }
    
    private void putColor(PrintWriter pw, String name, String hex) {
        pw.println("[color]");
        pw.printf("name\t%s\n", name);
        pw.printf("rgb\t%s\n", hex);
        pw.printf("adjust\t%s\n", p.getProperty("adjust"));
        pw.printf("blacklevel\t%s\n", p.getProperty("blacklevel"));
        pw.printf("gamma\t%s\n", p.getProperty("gamma"));
        pw.println();
    }

    private void putLight(PrintWriter pw, Light l) {
        putLight(pw, l.number, l.internalCode, l.hscanStart, l.hscanEnd, l.vscanStart, l.vscanEnd);
    }
    
    private void putLight(PrintWriter pw, int number, String internalCode, int hscanStart, int hscanEnd, int vscanStart, int vscanEnd) {
        pw.println("[light]");
        pw.printf("# internalCode %s\n", internalCode);
        pw.printf("name\t%s%d\n", p.getProperty("lightCodePrefix"), number);
        pw.printf("color\tred %s %d\n", p.getProperty("name"), (number*3-2));
        pw.printf("color\tgreen %s %d\n", p.getProperty("name"), (number*3-1));
        pw.printf("color\tblue %s %d\n", p.getProperty("name"), (number*3));
        pw.printf("hscan\t%d %d\n", hscanStart, hscanEnd);
        pw.printf("vscan\t%d %d\n", vscanStart, vscanEnd);
        pw.println();
    }

    public void run(String outfile) throws IOException {
        int width = Integer.parseInt(p.getProperty("width"));
        int height = Integer.parseInt(p.getProperty("height"));
        int marginX = Integer.parseInt(p.getProperty("marginX"));
        int marginY = Integer.parseInt(p.getProperty("marginY"));
        try (PrintWriter pw = new PrintWriter(outfile)) {
            pw.println("# config file created by " + this.getClass().getCanonicalName());
            pw.println("# Andrzej Maslowski (http://www.github.com/andy1024)");
            pw.println("# this is designed for matrix led zigzag-mode only, not for ambilight");
            pw.println("# " + new Date());
            pw.println();
            pw.println("[global]");
            pw.println("interface\t127.0.0.1");
            pw.println("port\t"+p.getProperty("port"));
            pw.println();
            pw.println("[device]");
            pw.printf("name\t%s\n", p.getProperty("name"));
            pw.printf("type\t%s\n", p.getProperty("type"));
            pw.printf("output\t%s\n", p.getProperty("outputDevice"));
            pw.printf("channels\t%d\n", width*height*3);
            pw.printf("prefix\t%s\n", p.getProperty("prefix"));
            pw.printf("interval\t%s\n", p.getProperty("interval"));
            pw.printf("rate\t%s\n", p.getProperty("rate"));
            pw.printf("debug\t%s\n", "off");
            pw.printf("delayafteropen\t%s\n", p.getProperty("delayafteropen"));
            pw.println();
            putColor(pw, "red", "FF0000");
            putColor(pw, "green", "00FF00");
            putColor(pw, "blue", "0000FF");
            int number = 1;
            int boxWidth = (100-marginX*2)/width;
            int boxHeight = (100-marginY*2)/height;
            int hscanStart;
            int hscanEnd;
            int vscanStart;
            int vscanEnd;
            
            boolean ydirection = true;
            for (int x=width;x>0;--x) {
                if (ydirection) {
                    for (int y=height;y>0;--y) {
                        hscanStart = marginX + (x-1)*boxWidth;
                        hscanEnd = marginX + (x)*boxWidth-1;
                        vscanStart = marginY + (height-y)*boxHeight;
                        vscanEnd = marginY + (height-y+1)*boxHeight-1;
                        Light l = new Light(number++, "["+x+","+y+"]", hscanStart, hscanEnd, vscanStart, vscanEnd);
                        putLight(pw, l);
                    }
                } else {
                    for (int y=1;y<=height;++y) {
                        hscanStart = marginX + (x-1)*boxWidth;
                        hscanEnd = marginX + (x)*boxWidth-1;
                        vscanStart = marginY + (height-y)*boxHeight;
                        vscanEnd = marginY + (height-y+1)*boxHeight-1;
                        Light l = new Light(number++, "["+x+","+y+"]", hscanStart, hscanEnd, vscanStart, vscanEnd);
                        putLight(pw, l);
                    }
                }
                ydirection = !ydirection;
            }
        }
        
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        new ConfMaker().run("/tmp/boblight.conf");
    }
    
}
