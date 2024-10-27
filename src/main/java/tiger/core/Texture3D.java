/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ShortBuffer;
import javax.imageio.ImageIO;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import static tiger.core.Texture2D.imageTypes;

/**
 *
 * @author grolljak
 */
public class Texture3D extends Texture {

    private short max, min;

    public Texture3D() {

        internalFormat = GL2.GL_LUMINANCE16;
        format = GL.GL_RGBA;
        type = GL.GL_UNSIGNED_BYTE;
        textureType = GL2.GL_TEXTURE_3D;

        params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);
    }

    public Texture3D(int width, int height, int depth) {

        this.width = width;
        this.height = height;
        this.depth = depth;
        internalFormat = GL2.GL_LUMINANCE16;
        format = GL.GL_RGBA;
        //type = GL.GL_UNSIGNED_BYTE;
        type = GL.GL_UNSIGNED_SHORT;
        textureType = GL2.GL_TEXTURE_3D;

        params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);
    }

    public Texture3D(int width, int height, int depth, int type, int format, int internalFormat) {
              
        this.width = width;
        this.height = height;
        this.depth = depth;

        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
        textureType = GL2.GL_TEXTURE_3D;
        params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);
    }
    
//    public Texture3D(int width, int height, int depth, short min, short max){
//        this(width, height, depth, GL.GL_UNSIGNED_SHORT, GL.GL_RGBA, GL2.GL_LUMINANCE16, min, max, GL.GL_LINEAR);
//    }
    
    public Texture3D(int width, int height, int depth, int type, int format, int internalFormat, short min, short max, int filter) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.min = min;
        this.max = max;

        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
        textureType = GL2.GL_TEXTURE_3D;
        
        params.put(GL.GL_TEXTURE_MIN_FILTER, filter);
        params.put(GL.GL_TEXTURE_MAG_FILTER, filter);
        params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);
    }

    public static void bindNothing(GL gl) {
        gl.glBindTexture(GL2.GL_TEXTURE_3D, 0);
    }

    @Override
    public void init(GLAutoDrawable drawable) {

        GL gl = drawable.getGL();
        allocate(gl);
        bind(gl);
        setParams(gl);

        if (imageData == null) {
            System.out.println("null image data..");
            //loadSubImages(gl, internalFormat, format, type, 0, width, height, depth);
        } else {
            if (!resizable) {
                loadImage(gl, 0, internalFormat, format, type, 0, width, height, depth, imageData);
            } else {
                loadImage(gl, 0, internalFormat, format, type, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight(), depth, imageData);
            }
        }
    }

    /*
    public void loadDicom(String path) throws FileNotFoundException, IOException {

        max = Short.MIN_VALUE;
        min = Short.MAX_VALUE;

        short[] data;

        int ukazatel = 0;
        BufferedInputStream bis = null;

        //zjistit jestli sequence ci nikoliv
        try {
            File file = new File(path);
            bis = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.err.println("File was not found!");
        }
        DICOM d = new DICOM(bis);
        d.open(path);
        
        if (d.getStackSize() == 1) {
            
            File dir = new File(path.split("/I")[0]);
            File[] directoryListing = dir.listFiles();
            data = new short[width * height * depth];
            
            for (int i = 1; i < directoryListing.length; i++) { //number of files in directory
                String s = path.split("/I10")[0] + "/I";

                s += i * 10;

                try {
                    File file = new File(s);
                    bis = new BufferedInputStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    continue; //ignore missing files in sequence
                }

                d = new DICOM();
                d.open(s);

                ImageProcessor ip = d.ip;
                short[] tmp = (short[]) ip.getPixels();

                for (int j = 0; j < tmp.length; j++) {
                    data[ukazatel] = tmp[j];
                    if (data[ukazatel] > max) {
                        max = data[ukazatel];
                    }
                    if (data[ukazatel] < min) {
                        min = data[ukazatel];
                    }
                    ukazatel++;
                }
            }

            resizable = false;
            type = GL.GL_UNSIGNED_SHORT;
            format = GL.GL_LUMINANCE;

            //System.out.println(type);
            params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
            params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
            params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);

            imageData = ShortBuffer.wrap(data);
        } else {
            
            loadSingleDicomFile(path, d);
        }
    }

    public void loadSingleDicomFile(String path, DICOM d) {

        byte[] data = new byte[width * height * depth];
        int ukazatel = 0;

        HashSet distinctValues = new HashSet();

        int num_of_slices = d.getNSlices();
        
        
        for (int i = 1; i < num_of_slices+1; i++) {
            d.setSlice(i);
            ImageProcessor ip = d.ip;
            short[] tmp = (short[]) ip.getPixels();

            for (int j = 0; j < tmp.length; j++) {
                distinctValues.add(tmp[j]);
            }
        }

        List sortedList = new ArrayList(distinctValues);
        Collections.sort(sortedList);
        
        for (int i = 1; i < num_of_slices; i++) {

            d.setSlice(i);

            ImageProcessor ip = d.ip;
            short[] tmp = (short[]) ip.getPixels();

            for (int j = 0; j < tmp.length; j++) {

                if (sortedList.size() > 1) {
                    data[ukazatel] = (byte) (sortedList.indexOf(tmp[j]) * (250 / (sortedList.size() - 1)));
                } else {
                    data[ukazatel] = (byte) tmp[j];
                }

                if (data[ukazatel] > max) {
                    max = data[ukazatel];
                }
                if (data[ukazatel] < min) {
                    min = data[ukazatel];
                }

                ukazatel++;
            }
        }

        resizable = false;

        type = GL.GL_UNSIGNED_BYTE;
        format = GL.GL_LUMINANCE;

        params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);

        imageData = ByteBuffer.wrap(data);
    }

    public void loadDat(String path) throws FileNotFoundException, IOException {

        max = Short.MIN_VALUE;
        min = Short.MAX_VALUE;

        DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));

        int i = 0;

        DataBufferUShort buffer = new DataBufferUShort(width * height * depth * 16);

        //System.out.println("uSizeX: " + input.readUnsignedShort());
        //System.out.println("uSizeY: " + input.readUnsignedShort());
        //System.out.println("uSizeZ: " + input.readUnsignedShort());
        System.out.println("Načítám texturu ze souboru ...");

        while (input.available() > 0) {
            buffer.setElem(i, input.readUnsignedShort());

            if (buffer.getElem(i) > max) {
                max = (short) buffer.getElem(i);
            }
            if (buffer.getElem(i) < min) {
                min = (short) buffer.getElem(i);
            }
            i = i + 4;

        }

        input.close();

        System.out.println(i);
        resizable = false;

        params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);

        imageData = ShortBuffer.wrap((buffer.getData()));
    }

    public void loadData(String path, String name, int start, int end) throws IOException {

        max = Short.MIN_VALUE;
        min = Short.MAX_VALUE;

        short[] pole = new short[width * height * depth];
        int imageType = 0;
        int ukazatel = 0;

        try {

            for (int i = start; i < end + 1; i++) {
                String s = path + name;
                if (i > 0 && i < 10) {
                    s += "00" + i + ".png";
                }
                if (i >= 10 && i < 100) {
                    s += "0" + i + ".png";
                }
                if (i >= 100 && i < 1000) {
                    s += +i + ".png";
                }

                File file = new File(s);
                BufferedImage image = ImageIO.read(file);

                imageType = image.getType();
                System.out.println("Image type: " + imageTypes[imageType]);

                WritableRaster raster = image.getRaster();

                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {

                        int value = raster.getSample(y, x, 0);

                        pole[ukazatel] = (short) value;

                        if (pole[ukazatel] > max) {
                            max = pole[ukazatel];
                        }
                        if (pole[ukazatel] < min) {
                            min = pole[ukazatel];
                        }

                        ukazatel++;
                    }
                }
            }

        } catch (IOException ex) {
            System.err.printf("Error while loading texture data.");
        }

        resizable = false;
        type = Texture2D.pixelTypes[imageType];
        format = Texture2D.pixelFormats[imageType];

        params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);

        //pro USHORT data
        //internalFormat = GL2.GL_LUMINANCE16;
        imageData = ShortBuffer.wrap(pole);

        //imageData = ShortBuffer.wrap(((DataBufferUShort) dataBuffer).getData());
    }

    public void loadData() throws IOException {
        System.out.println("texture 3d - loadData-null");

        resizable = false;
        params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);

        imageData = null;
    }
    */

    @Override
    public void loadImage(GL gl, int mipLevel, int internalFormat, int format, int type, int border, int width, int height, int depth, Buffer data) {
        System.out.println("Texture 3D - loadImage");
        GL2 gl2 = gl.getGL2();
        bind(gl2);

        gl2.glTexImage3D(textureType, mipLevel, internalFormat, width, height, depth, border, format, type, data);

    }

    private void loadSubImages(GL gl, int internalFormat, int format, int type, int border, int width, int height, int depth) {

        GL2 gl2 = gl.getGL2();
        bind(gl2);
        //v cyklu loadim subimage
        int start = 1;
        int end = 50;
        String path = "./files/cthead/";
        int imageType = 0;

        try {
            for (int i = start; i < end + 1; i++) {
                String s = path + "cthead-16bit";
                if (i > 0 && i < 10) {
                    s += "00" + i + ".png";
                }
                if (i >= 10 && i < 100) {
                    s += "0" + i + ".png";
                }
                if (i >= 100 && i < 1000) {
                    s += +i + ".png";
                }

                System.out.println("Načítám texturu: " + s);
                File file = new File(s);
                BufferedImage image = ImageIO.read(file);

                imageType = image.getType();
                System.out.println("Image type: " + imageTypes[imageType]);

                //DataBuffer dataBuffer = image.getRaster().getDataBuffer();
                //Buffer data = ShortBuffer.wrap(((DataBufferUShort) dataBuffer).getData());
                //System.out.println("size:" + dataBuffer.getSize());
                //System.out.println("size:" + data.capacity());
                short[] pole = new short[14000000];
                int ukazatel = 0;
                WritableRaster raster = image.getRaster();

                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {

                        // Obrázek je ve stupních šedi (16 bitů na pixel), 
                        // proto bereme pouze první kanál (třetí parametr je 0).
                        int value = raster.getSample(y, x, 0);
                        pole[ukazatel] = (short) value;
                        ukazatel++;
                    }
                }

                Buffer data = ShortBuffer.wrap(pole);
                gl2.glTexSubImage3D(textureType, 0, 0, 0, i - 1, width, height, depth, format, type, data);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.printf("Error while loading texture data.");
        }
    }

    public short getMax() {
        return max;
    }

    public short getMin() {
        return min;
    }
    
    public void setResizable(boolean resizable){
        this.resizable = resizable;
    }


}
