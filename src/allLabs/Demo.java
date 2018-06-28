package allLabs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
 
public class Demo extends Component implements ActionListener {

	//************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************
  
    String descs[] = {
        "Original", 
        "Negative",
        "Rescale",
        "Shift",
        "Random Rescale & Shift",
        "Image Addition",
        "Image Subtraction",
        "Image Multiplication",
        "Image Division",
        "NOT Operation",
        "AND Operation",
        "OR Operation",
        "XOR Operation",
        "ROI",
        "Log Transformation",
        "Power Law",
        "Random LUT Transformation",
        "Bit Plane Slicing",
        "Generate Histogram",
        "Histogram Normalisation",
        "Histogram Equalization",
        "Image Convolution",
        "Salt and Pepper",
        "Min Filtering",
        "Max Filtering",
        "Mid-Point Filtering",
        "Median Filtering",
        "Mean and Standard Deviation",
        "Simple Thresholding",
        "Automated Thresholding",
        "Reset menu function",
    };
    
    public static boolean readingRawImage = true;
    int opIndex;  //option index for 
    int lastOp;

    private BufferedImage biOriginal, biFilteredOriginal, bi, biFiltered;   // the input image saved as bi;//
    private BufferedImage ROIBi = null;
    static int w;
	static int h;
	private ArrayList<BufferedImage> storedImages = new ArrayList<>();
    //public boolean inROIMode = false;
    public Demo() {
        try {
        	
        	if(readingRawImage){
        		Path imagePath = Paths.get("src/allLabs/Goldhill.raw");
        		
        		byte[] imageBytes = Files.readAllBytes(imagePath);
        		int [] imageInts = new int[imageBytes.length];
        		
        		int width = w = 512;
        		int height = 512;
        		int[][][] result = new int[width][height][4];
        		
        		int counterX = 0;
        		int counterY = 0;
        		for(int i = 0; i<imageBytes.length; i++){
        			result[counterX][counterY][0] = (int)(imageBytes[i])&0xff;
        			result[counterX][counterY][1] = (int)(imageBytes[i])&0xff;
        			result[counterX][counterY][2] = (int)(imageBytes[i])&0xff;
        			result[counterX][counterY][3] = (int)(imageBytes[i])&0xff;
        			
        			counterX++;
        			if(counterX == width){
        				counterX = 0;
        				counterY++;
        			}
        		}
        		
        		biOriginal = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        		bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        		
        		 for(int row=0; row<height; row++){
                     for(int col =0; col<width; col++){
                         int a = result[row][col][0];
                         int r = result[row][col][1];
                         int g = result[row][col][2];
                         int b = result[row][col][3];

                         int p = (a<<24) | (r<<16) | (g<<8) | b;
                         biOriginal.setRGB(row, col, p);
                         bi.setRGB(row, col, p);

                     }
                 }
        	}
        	else{
        		  biOriginal = ImageIO.read(new File("src/allLabs/Cameraman.bmp"));
        		  bi = ImageIO.read(new File("src/allLabs/Cameraman.bmp"));
        	}
        	
            w = biOriginal.getWidth(null);
            h = biOriginal.getHeight(null);
            System.out.println(biOriginal.getType());
            if (biOriginal.getType() != BufferedImage.TYPE_INT_RGB && bi.getType() !=BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2Original = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                
                Graphics bigOriginal = bi2Original.getGraphics();
                Graphics big = bi2.getGraphics();
                
                bigOriginal.drawImage(biOriginal, 0, 0, null);
                big.drawImage(bi, 0, 0, null);
                
                biFilteredOriginal = biOriginal = bi2Original;
                biFiltered = bi = bi2;
                storedImages.add(biFiltered);
            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }                         
 
    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }
 

    String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }
 
 

    void setOpIndex(int i) {
        opIndex = i;
    }
 
    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();  
        
        g.drawImage(biFilteredOriginal, 0, 0, null);
        if(ROIBi != null){
        	g.drawImage(ROIBi, w*2, 0, null);
        }
        g.drawImage(biFiltered, w, 0, null);
        
    }
 

    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
      int width = image.getWidth();
      int height = image.getHeight();

      int[][][] result = new int[width][height][4];

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int p = image.getRGB(x,y);
            int a = (p>>24)&0xff;
            int r = (p>>16)&0xff;
            int g = (p>>8)&0xff;
            int b = p&0xff;

            result[x][y][0]=a;
            result[x][y][1]=r;
            result[x][y][2]=g;
            result[x][y][3]=b;
         }
      }
      return result;
    }

    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
            	//System.out.println("a");
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];
                
                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);
            }
        }
        return tmpimg;
    }


    //************************************
    //  Example:  Image Negative
    //************************************
    public BufferedImage ImageNegative(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
        		ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
            }
        }
        
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //Testing purposes only for ROI
    //************************************
    public BufferedImage SetOpacityToZero(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			if(!(x >=50 && x<=100 && y>=50 && y<100))
    				ImageArray[x][y][0] = 0;
    		}
    	}
    	
    	return convertToBimage(ImageArray);
    }
    public BufferedImage SetOpacityTo255(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray[x][y][0] = 255;
    		}
    	}
    	
    	return convertToBimage(ImageArray);
    }
    //************************************
    //  Your turn now:  Add more function below
    //************************************
    //************************************
    //  Image Re-scaling
    //************************************
    public BufferedImage ImageRescale(BufferedImage timg, float scaleValue){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			int r = (int) Math.ceil((scaleValue * ImageArray[x][y][1]));
    			int g = (int) Math.ceil((scaleValue * ImageArray[x][y][2]));
    			int b = (int) Math.ceil((scaleValue * ImageArray[x][y][3]));
    			
    			ImageArray[x][y][1] = boundsChecker(r);
    			ImageArray[x][y][2] = boundsChecker(g);
    			ImageArray[x][y][3] = boundsChecker(b);
    			
    		}
    	}
    	return convertToBimage(ImageArray);	
    }
    
    //************************************
    //  Image Shifting
    //************************************
    public BufferedImage ImageShifting(BufferedImage timg, int shiftValue){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			int r = ImageArray[x][y][1]+shiftValue;
    			int g = ImageArray[x][y][2]+shiftValue;
    			int b = ImageArray[x][y][3]+shiftValue;
    			
    			ImageArray[x][y][1] = boundsChecker(r);
    			ImageArray[x][y][2] = boundsChecker(g);
    			ImageArray[x][y][3] = boundsChecker(b);
    		}
    	}
    	return convertToBimage(ImageArray);
    }
    
    //************************************
    //  Image Re-scaling and Shifting with random value
    //************************************
    public BufferedImage ImageShiftingAndRescaling(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	//int rand = (int)(Math.random()*100+1);
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			int rand = (int)(Math.random()*255+1);
    			
    			int r = ImageArray[x][y][1]+rand;
    			int g = ImageArray[x][y][2]+rand;
    			int b = ImageArray[x][y][3]+rand;
    			//ImageArray[x][y][0] = 255;
    			ImageArray[x][y][1] = boundsChecker(r);
    			ImageArray[x][y][2] = boundsChecker(g);
    			ImageArray[x][y][3] = boundsChecker(b);
    		}
    	}
		return randomRescaleAndShift(convertToBimage(ImageArray));
    }

    private BufferedImage randomRescaleAndShift(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int rmin = Integer.MAX_VALUE;
    	int rmax = Integer.MIN_VALUE;
    	int gmin = Integer.MAX_VALUE;
    	int gmax = Integer.MIN_VALUE;
    	int bmin = Integer.MAX_VALUE;
    	int bmax = Integer.MIN_VALUE;
    	int [][][] ImageArray = convertToArray(timg);
    	Scanner input = new Scanner(System.in);
    	System.out.println("Please enter a value to scale by i.e 0.5, 0.7, 1, 2 etc.");
    	float scaleValue = input.nextFloat();
    	System.out.println("PLease enter a value to shift by i.e 25, 50 etc.");
    	int shiftValue = input.nextInt();
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray[x][y][1] = boundsChecker((int)(Math.ceil((scaleValue * ImageArray[x][y][1])+shiftValue)));
    			ImageArray[x][y][2] = boundsChecker((int)(Math.ceil((scaleValue * ImageArray[x][y][2])+shiftValue)));
    			ImageArray[x][y][3] = boundsChecker((int)(Math.ceil((scaleValue * ImageArray[x][y][3])+shiftValue)));
    		}
    	}
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			int r = ImageArray[x][y][1];
    			int g = ImageArray[x][y][2];
    			int b = ImageArray[x][y][3];
    			
       			if(r < rmin){
    				rmin = r;
    			}
       			else if(g < gmin){
       				gmin = g;
       			}
       			else if(b < bmin){
       				bmin = b;
       			}
    			
    			if(r > rmax){
    				rmax = r;
    			}
    			else if(g > gmax){
    				gmax = g;
    			}
    			else if(b > bmax){
    				bmax = b;
    			}
    		}
    	}
    	
    	for(int y = 0; y<width; y++){
    		for(int x = 0; x<height; x++){
    			int r =255*(ImageArray[x][y][1]-rmin)/(rmax-rmin);
    			int g =255*(ImageArray[x][y][2]-gmin)/(gmax-gmin);
    			int b =255*(ImageArray[x][y][1]-bmin)/(bmax-bmin);
 
    			ImageArray[x][y][1] = customBoundsChecker(r, rmin, rmax);
    			ImageArray[x][y][2] = customBoundsChecker(g, gmin, gmax);
    			ImageArray[x][y][3] = customBoundsChecker(b, bmin, bmax);
    			
    		}
    	}
    	return convertToBimage(ImageArray);	
    }
    
    private int boundsChecker(int colorValue){
    	if(colorValue > 255){
    		return 255;
    	}
    	else if(colorValue < 0){
    		return 0;
    	}
    	else{
    		return colorValue;
    	}
    }
    
    private int customBoundsChecker(int value, int min, int max){
    	if(value < min){
    		return min;
    	}
    	else if(value > max){
    		return max;
    	}
    	else{
    		return value;
    	}
    }
    
    //************************************
    //  Image Addition
    //************************************
    public BufferedImage ImageAddition(BufferedImage timg1, BufferedImage timg2){
    	int width = timg1.getWidth();
    	int height = timg1.getHeight();
    	
    	int [][][] ImageArray1 = convertToArray(timg1);
    	int ImageArray2[][][] = convertToArray(timg2);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray1[x][y][1] = boundsChecker(ImageArray1[x][y][1] + ImageArray2[x][y][1]);
    			ImageArray1[x][y][2] = boundsChecker(ImageArray1[x][y][2] + ImageArray2[x][y][2]);
    			ImageArray1[x][y][3] = boundsChecker(ImageArray1[x][y][3] + ImageArray2[x][y][3]);
    		}
    	}
    	
    	return convertToBimage(ImageArray1);
    }
    
    //************************************
    //  Image subtraction
    //************************************
    public BufferedImage ImageSubtraction(BufferedImage timg1, BufferedImage timg2){
    	int width = timg1.getWidth();
    	int height = timg1.getHeight();
    	
    	int [][][] ImageArray1 = convertToArray(timg1);
    	int [][][] ImageArray2 = convertToArray(timg2);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray1[x][y][1] = boundsChecker(ImageArray1[x][y][1]- ImageArray2[x][y][1]);
    			ImageArray1[x][y][2] = boundsChecker(ImageArray1[x][y][2]- ImageArray2[x][y][2]);
    			ImageArray1[x][y][3] = boundsChecker(ImageArray1[x][y][3]- ImageArray2[x][y][3]);
    		}
    	}
    	
    	return convertToBimage(ImageArray1);
    }
    //************************************
    //  Image Multiplication
    //************************************
    public BufferedImage ImageMultiplication(BufferedImage timg1, BufferedImage timg2){
    	int width = timg1.getWidth();
    	int height = timg1.getHeight();
    	
    	int [][][] ImageArray1 = convertToArray(timg1);
    	int [][][] ImageArray2 = convertToArray(timg2);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray1[x][y][1] = boundsChecker(ImageArray1[x][y][1]* ImageArray2[x][y][1]);
    			ImageArray1[x][y][2] = boundsChecker(ImageArray1[x][y][2]* ImageArray2[x][y][2]);
    			ImageArray1[x][y][3] = boundsChecker(ImageArray1[x][y][3]* ImageArray2[x][y][3]);
    		}
    	}
    	
    	return convertToBimage(ImageArray1);
    }
    //************************************
    //  Image Division
    //************************************
    public BufferedImage ImageDivision(BufferedImage timg1, BufferedImage timg2){
    	int width = timg1.getWidth();
    	int height = timg2.getHeight();
    	int [][][] ImageArray1 = convertToArray(timg1);
    	int [][][] ImageArray2 = convertToArray(timg2);
    
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			if(ImageArray2[x][y][1] != 0 && ImageArray2[x][y][2] !=0 && ImageArray2[x][y][3]!=0){
	    			ImageArray1[x][y][1] = boundsChecker(ImageArray1[x][y][1]/ ImageArray2[x][y][1]);
	    			ImageArray1[x][y][2] = boundsChecker(ImageArray1[x][y][2]/ ImageArray2[x][y][2]);
	    			ImageArray1[x][y][3] = boundsChecker(ImageArray1[x][y][3]/ ImageArray2[x][y][3]);
    			}
    		}
    	}
    	
    	return convertToBimage(ImageArray1);
    	
    }
    //************************************
    // Bitwise NOT
    //************************************
    public BufferedImage NOTOperation(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	int [][][] ImageArray = convertToArray(timg);
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
//    			if(ROIBi != null){
//    				ImageArray[x][y][0] = boundsChecker(~ImageArray[x][y][0]);
//    				
//    			}
				ImageArray[x][y][1] = boundsChecker(~ImageArray[x][y][1]&0xff);
    			ImageArray[x][y][2] = boundsChecker(~ImageArray[x][y][2]&0xff);
    			ImageArray[x][y][3] = boundsChecker(~ImageArray[x][y][3]&0xff);
    			
    		}
    	}
    	
    	return convertToBimage(ImageArray);
    }
    
    //************************************
    // Bitwise AND
    //************************************
    public BufferedImage ANDOperation(BufferedImage timg1, BufferedImage timg2){
    	int width = timg1.getWidth();
    	int height = timg1.getHeight();
    	
    	int [][][] ImageArray1 = convertToArray(timg1);
    	
    	int [][][] ImageArray2 = convertToArray(timg2);
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
  				ImageArray2[x][y][1] = boundsChecker(ImageArray1[x][y][1]&ImageArray2[x][y][1]);
				ImageArray2[x][y][2] = boundsChecker(ImageArray1[x][y][2]&ImageArray2[x][y][2]);
				ImageArray2[x][y][3] = boundsChecker(ImageArray1[x][y][3]&ImageArray2[x][y][3]);
    		}
    	}
    	
    	return convertToBimage(ImageArray2);
    }
    //************************************
    // Bitwise OR
    //************************************
    public BufferedImage OROperation(BufferedImage timg1, BufferedImage timg2){
    	int width = timg1.getWidth();
    	int height = timg2.getHeight();
    	
    	int [][][] ImageArray1 = convertToArray(timg1);
    	int [][][] ImageArray2 = convertToArray(timg2);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray1[x][y][1] = boundsChecker(ImageArray1[x][y][1]|ImageArray2[x][y][1]);
    			ImageArray1[x][y][2] = boundsChecker(ImageArray1[x][y][2]|ImageArray2[x][y][2]);
    			ImageArray1[x][y][3] = boundsChecker(ImageArray1[x][y][3]|ImageArray2[x][y][3]);
    		}
    	}
    	
    	return convertToBimage(ImageArray1);
    }
    //************************************
    // Bitwise XOR
    //************************************
    public BufferedImage XOROperation(BufferedImage timg1, BufferedImage timg2){
    	int width = timg1.getWidth();
    	int height = timg2.getHeight();
    	
    	int [][][] ImageArray1 = convertToArray(timg1);
    	int [][][] ImageArray2 = convertToArray(timg2);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray1[x][y][1] = boundsChecker(ImageArray1[x][y][1]^ImageArray2[x][y][1]);
    			ImageArray1[x][y][2] = boundsChecker(ImageArray1[x][y][2]^ImageArray2[x][y][2]);
    			ImageArray1[x][y][3] = boundsChecker(ImageArray1[x][y][3]^ImageArray2[x][y][3]);
    		}
    	}
    	
    	return convertToBimage(ImageArray1);
    }
    
    //************************************
    // Bitwise ROI GENERATION
    //************************************
    //need to check if this is correct with the lecturer lab3 ex4
    public BufferedImage GenerateROIWithANDOperation(BufferedImage timg, int x1, int y1, int horizontalLength, int verticleLength){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	//int [][][] ImageArray = convertToArray(timg);
    	int rangeX = x1+horizontalLength;
    	int rangeY = y1+verticleLength;
    	int [][][] MaskImageArray = new int [width][height][4];
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			if(!(x >=x1 && x<=rangeX && y>=y1 && y<rangeY)){
    				MaskImageArray[x][y][0] = 0; // to get the black surrounding set this array position to 255
    				MaskImageArray[x][y][1] = 0;
    				MaskImageArray[x][y][2] = 0;
    				MaskImageArray[x][y][3] = 0;    				
    			}
    			else{
    				MaskImageArray[x][y][0] = 255;
    				MaskImageArray[x][y][1] = 255;
    				MaskImageArray[x][y][2] = 255;
    				MaskImageArray[x][y][3] = 255;
    			}
    		}
    	}
    	return ROIANDOperation(timg, convertToBimage(MaskImageArray));
    }
    
    private BufferedImage ROIANDOperation(BufferedImage timg1, BufferedImage timg2){
    	int width = timg1.getWidth();
    	int height = timg1.getWidth();
    	
    	int [][][] ImageArray1 = convertToArray(timg1);
    	int [][][] ImageArray2 = convertToArray(timg2);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray1[x][y][0] = boundsChecker(ImageArray1[x][y][0] & ImageArray2[x][y][0]);
    			ImageArray1[x][y][1] = boundsChecker(ImageArray1[x][y][1] & ImageArray2[x][y][1]);
    			ImageArray1[x][y][2] = boundsChecker(ImageArray1[x][y][2] & ImageArray2[x][y][2]);
    			ImageArray1[x][y][3] = boundsChecker(ImageArray1[x][y][3] & ImageArray2[x][y][3]);
    		}
    	}
    	ROIBi = timg2;
    	//storedImages.add(ROIBi);
    	return convertToBimage(ImageArray1);
    }
    
    //***********************************************
    //	Log Operation
    //***********************************************
    public BufferedImage LogTransformation(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	for(int y = 0; y< height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray[x][y][1] = boundsChecker((int) (Math.log(1+ImageArray[x][y][1])*255/Math.log(256)));
    			ImageArray[x][y][2] = boundsChecker((int) (Math.log(1+ImageArray[x][y][2])*255/Math.log(256)));
    			ImageArray[x][y][3] = boundsChecker((int) (Math.log(1+ImageArray[x][y][3])*255/Math.log(256)));
    		}
    	}
    	
    	return convertToBimage(ImageArray);
    }
    
    //***********************************************
    //	Power Law
    //***********************************************
    public BufferedImage PowerLaw(BufferedImage timg, float powerValue){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray[x][y][1] = boundsChecker((int)(Math.pow(255,1-powerValue)*Math.pow(ImageArray[x][y][1],powerValue)));
    			ImageArray[x][y][2] = boundsChecker((int)(Math.pow(255,1-powerValue)*Math.pow(ImageArray[x][y][2],powerValue)));
    			ImageArray[x][y][3] = boundsChecker((int)(Math.pow(255,1-powerValue)*Math.pow(ImageArray[x][y][3],powerValue)));
    		}
    	}
    	
    	return convertToBimage(ImageArray);
    }
    
    //***********************************************
    //	Random Look Up Table
    //***********************************************
    public BufferedImage RandomLUTTransformation(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	int [] LUT = new int[256];
    	for(int k = 0; k<256; k++){
    		LUT[k] = (int)(Math.random()*255+1);
    	}
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray[x][y][1] = boundsChecker(LUT[ImageArray[x][y][1]]);
    			ImageArray[x][y][2] = boundsChecker(LUT[ImageArray[x][y][2]]);
    			ImageArray[x][y][3] = boundsChecker(LUT[ImageArray[x][y][3]]);
    		}
    	}
    	
    	return convertToBimage(ImageArray);
    }
    
    //***********************************************
    //	Bit plane slicing
    //***********************************************
    public BufferedImage BitPlaneSlicing(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	Scanner bitPlane = new Scanner(System.in);
    	System.out.println("Select a bit plane between 0 to 7");
    	int k = bitPlane.nextInt();
    	
    	for(int y = 0 ; y<height; y++){
    		for(int  x= 0; x<width; x++){
				 ImageArray[x][y][1] = (ImageArray[x][y][1]>>k)&1; //r
				 ImageArray[x][y][2] = (ImageArray[x][y][2]>>k)&1; //g
				 ImageArray[x][y][3] = (ImageArray[x][y][3]>>k)&1; //b
				 
				 if(ImageArray[x][y][1] == 1){
					 ImageArray[x][y][1] = 255;
				 }
				 
				 if(ImageArray[x][y][2] == 1){
					 ImageArray[x][y][2] = 255;
				 }
				 
				 if(ImageArray[x][y][3] == 1){
					 ImageArray[x][y][3] = 255;
				 }
    		}
    	}
    	
    	return convertToBimage(ImageArray);
    }
    
    //***********************************************
    //	Histogram Generation
    //***********************************************
    public void ImageHistogram(BufferedImage timg){
    	
    	int [] histogramArray = GenerateImageHistogram(timg);
    	for(int k = 0; k<histogramArray.length; k++){
    		System.out.println(k+" : "+histogramArray[k]);
    	}
    }
    
    private int[] GenerateImageHistogram(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	int [] histogramArray = new int[256];
    	int [][][] ImageArray = convertToArray(timg);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			int r = ImageArray[x][y][1];
    			int g = ImageArray[x][y][2];
    			int b = ImageArray[x][y][3];
    			
    			histogramArray[r]++;
    			histogramArray[g]++;
    			histogramArray[b]++;
    		}
    	}
    	return histogramArray;
    }
    
    //***********************************************
    //	Histogram Normalisation
    //***********************************************
    public void ImageHistogramNormalization(BufferedImage timg){
    	
    	double [] normalisedHistogram = GenerateNormalisedHistogram(timg);
    	
    	for(int i = 0; i<normalisedHistogram.length; i++){
    		System.out.println(i+" : "+normalisedHistogram[i]);
    	}
    }
    
    public double[] GenerateNormalisedHistogram(BufferedImage timg){
    	double numberOfPixels = 0;
    	
    	int [] histogramArray = GenerateImageHistogram(timg);
    	
    	//System.out.println("A");
    	for(int i = 0; i<histogramArray.length; i++){
    		System.out.println(histogramArray[i]);
    	}
    	//System.out.println("A");
    	double [] normalisedHistogram = new double[256];
    	
    	for(int k = 0; k< histogramArray.length; k++){
    		numberOfPixels+= histogramArray[k];
    	}
    	
    	System.out.println(numberOfPixels);
    	for(int i = 0; i< normalisedHistogram.length; i++){
    		normalisedHistogram[i] = histogramArray[i]/numberOfPixels;
    	}
    	
    	return normalisedHistogram;
    }
    
    //***********************************************
    //	Histogram Equalisation
    //***********************************************
    public BufferedImage ImageHistogramEqualization(BufferedImage timg){
    	//int numberOfPixels = 0;
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	int pixelCount = 0;
    	int [][][] ImageArray = convertToArray(timg);
    	
    	double [] rHistogram = new double[256];
    	double [] gHistogram = new double[256];
    	double [] bHistogram = new double[256];
    	
    	double [] rNormalizedHistogram = new double[256];
    	double [] gNormalizedHistogram = new double[256];
    	double [] bNormalizedHistogram = new double[256];
    	
    	double [] rCumulativeHistogram = new double[256];
    	double [] gCumulativeHistogram = new double[256];
    	double [] bCumulativeHistogram = new double[256];
    	
    	int [] rMultipliedIntensity = new int [256];
    	int [] gMultipliedIntensity = new int [256];
    	int [] bMultipliedIntensity = new int [256];
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			rHistogram[ImageArray[x][y][1]]++;
    			gHistogram[ImageArray[x][y][2]]++;
    			bHistogram[ImageArray[x][y][3]]++;
    			pixelCount++;
    		}
    	}
    	
    	for(int i = 0; i<256; i++){
    		rNormalizedHistogram[i] = (double) rHistogram[i]/pixelCount;
    		gNormalizedHistogram[i] = (double) gHistogram[i]/pixelCount;
    		bNormalizedHistogram[i] = (double) bHistogram[i]/pixelCount;
    	}
    	
    	rCumulativeHistogram[0] = rNormalizedHistogram[0];
    	gCumulativeHistogram[0] = gNormalizedHistogram[0];
    	bCumulativeHistogram[0] = bNormalizedHistogram[0];
    	for(int i = 0; i<255; i++){
    		rCumulativeHistogram[i+1] = rCumulativeHistogram[i]+rNormalizedHistogram[i+1];
    		gCumulativeHistogram[i+1] = gCumulativeHistogram[i]+rNormalizedHistogram[i+1];
    		bCumulativeHistogram[i+1] = bCumulativeHistogram[i]+rNormalizedHistogram[i+1];
    	}
    	
    	for(int i = 0; i<256; i++){
    		rMultipliedIntensity[i] = (int) Math.floor(rCumulativeHistogram[i] *255); 
    		gMultipliedIntensity[i] = (int) Math.floor(gCumulativeHistogram[i] *255); 
    		bMultipliedIntensity[i] = (int) Math.floor(bCumulativeHistogram[i] *255); 
    	}
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray[x][y][1] = rMultipliedIntensity[ImageArray[x][y][1]];
    			ImageArray[x][y][2] = gMultipliedIntensity[ImageArray[x][y][2]];
    			ImageArray[x][y][3] = bMultipliedIntensity[ImageArray[x][y][3]];
    		}
    	}
    	
    	return convertToBimage(ImageArray);
    }
    
    //***********************************************
    //Image Convolution
    //***********************************************
    public BufferedImage ImageConvolution(BufferedImage timg, int option){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	double[][][] ImageArray2 = new double[height][width][4];
    	double [][] Mask = new double[3][3];
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray2[x][y][1] = (double) ImageArray[x][y][1];
    			ImageArray2[x][y][2] = (double) ImageArray[x][y][2];
    			ImageArray2[x][y][3] = (double) ImageArray[x][y][3];
    		}
    	}
    	
    	if(option == 1){
    		for(int i =0; i<3; i++){
        		for(int j= 0; j<3; j++){
        			Mask[j][i] = (double) 1/9;
        		}
        	}
    		System.out.println(Mask[1][1]);
    	}
    	else if(option == 2){
    		Mask[0][0] = 1;
        	Mask[0][1] = 2;
        	Mask[0][2] = 1;
        	Mask[1][0] = 2;
        	Mask[1][1] = 4;
        	Mask[1][2] = 2;
        	Mask[2][0] = 1;
        	Mask[2][1] = 2;
        	Mask[2][2] = 1;
    		for(int i =0; i<3; i++){
        		for(int j= 0; j<3; j++){
        			Mask[j][i] = (double) (1.0/16.0)*Mask[j][i];
        		}
        	}
    	}else if (option == 3){
    		Mask[0][0] = 0;
        	Mask[0][1] = -1;
        	Mask[0][2] = 0;
        	Mask[1][0] = -1;
        	Mask[1][1] = 4;
        	Mask[1][2] = -1;
        	Mask[2][0] = 0;
        	Mask[2][1] = -1;
        	Mask[2][2] = 0;
    	}
    	else if(option == 4){
    		Mask[0][0] = -1;
        	Mask[0][1] = -1;
        	Mask[0][2] = -1;
        	Mask[1][0] = -1;
        	Mask[1][1] = 8;
        	Mask[1][2] = -1;
        	Mask[2][0] = -1;
        	Mask[2][1] = -1;
        	Mask[2][2] = -1;
    	}
    	else if(option == 5){
    		Mask[0][0] = 0;
        	Mask[0][1] = -1;
        	Mask[0][2] = 0;
        	Mask[1][0] = -1;
        	Mask[1][1] = 5;
        	Mask[1][2] = -1;
        	Mask[2][0] = 0;
        	Mask[2][1] = -1;
        	Mask[2][2] = 0;
    	}
    	else if (option == 6){
    		Mask[0][0] = -1;
        	Mask[0][1] = -1;
        	Mask[0][2] = -1;
        	Mask[1][0] = -1;
        	Mask[1][1] = 9;
        	Mask[1][2] = -1;
        	Mask[2][0] = -1;
        	Mask[2][1] = -1;
        	Mask[2][2] = -1;
    	}
    	else if(option == 7){
    		Mask[0][0] = 0;
        	Mask[0][1] = 0;
        	Mask[0][2] = 0;
        	Mask[1][0] = 0;
        	Mask[1][1] = 0;
        	Mask[1][2] = -1;
        	Mask[2][0] = 0;
        	Mask[2][1] = 1;
        	Mask[2][2] = 0;
    		
    	}
    	else if(option == 8){
    		Mask[0][0] = 0;
        	Mask[0][1] = 0;
        	Mask[0][2] = 0;
        	Mask[1][0] = 0;
        	Mask[1][1] = -1;
        	Mask[1][2] = 0;
        	Mask[2][0] = 0;
        	Mask[2][1] = 0;
        	Mask[2][2] = 1;
    	}
    	else if(option == 9){
    		Mask[0][0] = -1;
        	Mask[0][1] = 0;
        	Mask[0][2] = 1;
        	Mask[1][0] = -2;
        	Mask[1][1] = 0;
        	Mask[1][2] = 2;
        	Mask[2][0] = -1;
        	Mask[2][1] = 0;
        	Mask[2][2] = 1;
    	}
    	else if(option == 10){
    		Mask[0][0] = -1;
        	Mask[0][1] = -2;
        	Mask[0][2] = -1;
        	Mask[1][0] = 0;
        	Mask[1][1] = 0;
        	Mask[1][2] = 0;
        	Mask[2][0] = 1;
        	Mask[2][1] = 2;
        	Mask[2][2] = 1;
    	}
    	
    	for(int y=1; y<height-1; y++){
    		 for(int x=1; x<width-1; x++){
	    		 double r = 0; 
	    		 double g = 0; 
	    		 double b = 0;
	    		 for(int s=-1; s<=1; s++){
		    		 for(int t=-1; t<=1; t++){
		    		 r = r+Mask[1-s][1-t]*ImageArray2[x+s][y+t][1]; //r
		    		 g = g+Mask[1-s][1-t]*ImageArray2[x+s][y+t][2]; //g
		    		 b = b+Mask[1-s][1-t]*ImageArray2[x+s][y+t][3]; //b
		    		 }
	    		 }
	    		 

	    		 
	    		 ImageArray[x][y][1] = boundsChecker((int)Math.floor(r)); //r
	    		 ImageArray[x][y][2] = boundsChecker((int)Math.floor(g)); //g
	    		 ImageArray[x][y][3] = boundsChecker((int)Math.floor(b)); //b
    		 }
    	}
    	
    	return convertToBimage(ImageArray);
    }
    
    //***********************************************
    //	Salt and Pepper
    //***********************************************
    public BufferedImage SaltAndPepper(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int[][][] ImageArray = convertToArray(timg);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			double randomValue = Math.random();
    			
    			if(randomValue <= 0.05){
    				ImageArray[x][y][1] = 0;
    				ImageArray[x][y][2] = 0;
    				ImageArray[x][y][3] = 0;
    			}
    			else if(randomValue >= 0.95){
    				ImageArray[x][y][1] = 255;
    				ImageArray[x][y][2] = 255;
    				ImageArray[x][y][3] = 255;
    			}
    		}
    	}
    	
    	return convertToBimage(ImageArray);
    }
    
    //***********************************************
    //	Minimum Filter
    //***********************************************
    public BufferedImage minimumFilter(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	int [][][] ImageArray2 = new int[height][width][4];
    	
    	int [] rWindow = new int[9];
    	int [] gWindow = new int[9];
    	int [] bWindow = new int[9];
    	
    	for(int y=1; y<height-1; y++){
    		 for(int x=1; x<width-1; x++){
	    		 int k = 0;
		    		 for(int s=-1; s<=1; s++){
			    		 for(int t=-1; t<=1; t++){
				    		 rWindow[k] = ImageArray[x+s][y+t][1]; //r
				    		 gWindow[k] = ImageArray[x+s][y+t][2]; //g
				    		 bWindow[k] = ImageArray[x+s][y+t][3]; //b
				    		 k++;
			    		 }
		    		 }
		    		 
		    		 Arrays.sort(rWindow);
		    		 Arrays.sort(gWindow);
		    		 Arrays.sort(bWindow);
		    		 ImageArray2[x][y][1] = rWindow[0]; //r
		    		 ImageArray2[x][y][2] = gWindow[0]; //g
		    		 ImageArray2[x][y][3] = bWindow[0]; //b
		    		
	    	}
    	}
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray2[x][y][0] = ImageArray[x][y][0];
    			if(ImageArray2[x][y][1] == 0 && ImageArray2[x][y][2] == 0 && ImageArray2[x][y][3] == 0){
    				ImageArray2[x][y][1] = ImageArray[x][y][1];
    				ImageArray2[x][y][2] = ImageArray[x][y][2];
    				ImageArray2[x][y][3] = ImageArray[x][y][3];
    			}
    		}
    	}
    	
    	return convertToBimage(ImageArray2);
    }
    
    //***********************************************
    //	Maximum Filter
    //***********************************************
    public BufferedImage maximumFilter(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	int [][][] ImageArray2 = new int[height][width][4];
    	int [] rWindow = new int[9];
    	int [] gWindow = new int[9];
    	int [] bWindow = new int[9];
    	
    	for(int y=1; y<height-1; y++){
    		 for(int x=1; x<width-1; x++){
	    		 int k = 0;
		    		 for(int s=-1; s<=1; s++){
			    		 for(int t=-1; t<=1; t++){
				    		 rWindow[k] = ImageArray[x+s][y+t][1]; //r
				    		 gWindow[k] = ImageArray[x+s][y+t][2]; //g
				    		 bWindow[k] = ImageArray[x+s][y+t][3]; //b
				    		 k++;
			    		 }
		    		 }
		    		 
		    		 Arrays.sort(rWindow);
		    		 Arrays.sort(gWindow);
		    		 Arrays.sort(bWindow);
	    		 
		    		 ImageArray2[x][y][1] = rWindow[8]; //r
		    		 ImageArray2[x][y][2] = gWindow[8]; //g
		    		 ImageArray2[x][y][3] = bWindow[8]; //b
    		 }
		}
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			ImageArray2[x][y][0] = ImageArray[x][y][0];
    			if(ImageArray2[x][y][1] == 0 && ImageArray2[x][y][2] == 0 && ImageArray2[x][y][3] == 0){
    				ImageArray2[x][y][1] = ImageArray[x][y][1];
    				ImageArray2[x][y][2] = ImageArray[x][y][2];
    				ImageArray2[x][y][3] = ImageArray[x][y][3];
    			}
    		}
    	}
    	return convertToBimage(ImageArray2);
    }
    
    //***********************************************
    //	Mid-Point Filtering
    //***********************************************
    public BufferedImage midPointFilter(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	int [] rWindow = new int[9];
    	int [] gWindow = new int[9];
    	int [] bWindow = new int[9];
    	
    	for(int y=1; y<height-1; y++){
    		 for(int x=1; x<width-1; x++){
	    		 int k = 0;
		    		 for(int s=-1; s<=1; s++){
			    		 for(int t=-1; t<=1; t++){
				    		 rWindow[k] = ImageArray[x+s][y+t][1]; //r
				    		 gWindow[k] = ImageArray[x+s][y+t][2]; //g
				    		 bWindow[k] = ImageArray[x+s][y+t][3]; //b
				    		 k++;
			    		 }
		    		 }
		    		 
		    		 Arrays.sort(rWindow);
		    		 Arrays.sort(gWindow);
		    		 Arrays.sort(bWindow);
		    		 int r = Math.floorDiv((rWindow[0] + rWindow[8]),2);
		    		 int g = Math.floorDiv((gWindow[0] + gWindow[8]),2);
		    		 int b = Math.floorDiv((bWindow[0] + bWindow[8]),2);
		    		 
		    		 ImageArray[x][y][1] = r; //r
		    		 ImageArray[x][y][2] = g; //g
		    		 ImageArray[x][y][3] = b; //b
	    		 }
    		 
    		}
    	
    	for(int i = 0; i<9; i++){
    		System.out.println(gWindow[i]);
    		
    	}
    	return convertToBimage(ImageArray);

    }
    
    //***********************************************
    //	Median Filtering
    //***********************************************
    public BufferedImage medianFilter(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	int [] rWindow = new int[9];
    	int [] gWindow = new int[9];
    	int [] bWindow = new int[9];
    	
    	for(int y=1; y<height-1; y++){
    		 for(int x=1; x<width-1; x++){
	    		 int k = 0;
		    		 for(int s=-1; s<=1; s++){
			    		 for(int t=-1; t<=1; t++){
				    		 rWindow[k] = ImageArray[x+s][y+t][1]; //r
				    		 gWindow[k] = ImageArray[x+s][y+t][2]; //g
				    		 bWindow[k] = ImageArray[x+s][y+t][3]; //b
				    		 k++;
			    		 }
		    		 }
		    		 
		    		 Arrays.sort(rWindow);
		    		 Arrays.sort(gWindow);
		    		 Arrays.sort(bWindow);
		    		 ImageArray[x][y][1] = rWindow[4]; //r
		    		 ImageArray[x][y][2] = gWindow[4]; //g
		    		 ImageArray[x][y][3] = bWindow[4]; //b
	    		 }
    		}
    	return convertToBimage(ImageArray);
    }
    
    //***********************************************
    //	Mean and Standard Deviation 
    //***********************************************
    public void meanAndStandardDeviation(BufferedImage timg){
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int pixelCount = 0;
    	int sumOfRPixels = 0;
    	int sumOfGPixels = 0;
    	int sumOfBPixels = 0;
    	int [][][] ImageArray = convertToArray(timg);
    	
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			pixelCount++;
    			
    			sumOfRPixels += ImageArray[x][y][1];
    			sumOfGPixels += ImageArray[x][y][2];
    			sumOfBPixels += ImageArray[x][y][3];
    		}
    	}
    	
    	double rMean = (double) sumOfRPixels/pixelCount;
    	double gMean = (double) sumOfGPixels/pixelCount;
    	double bMean = (double) sumOfBPixels/pixelCount;
    	
    	System.out.println("The rMean is: "+rMean);
    	System.out.println("The gMean is: "+gMean);
    	System.out.println("The bMean is: "+bMean);
    	
    	double rDistance = 0;
    	double gDistance = 0;
    	double bDistance = 0;
    	for(int y = 0; y<height; y++){
    		for(int x = 0; x<width; x++){
    			rDistance += Math.pow((double)ImageArray[x][y][1]-rMean, 2);
    			gDistance += Math.pow((double)ImageArray[x][y][2]-gMean, 2);
    			bDistance += Math.pow((double)ImageArray[x][y][3]-bMean, 2);
    		}
    	}
    	
    	double rDivision = (double) rDistance/pixelCount;
    	double gDivision = (double) gDistance/pixelCount;
    	double bDivision = (double) bDistance/pixelCount;
    	
    	double rsd = Math.sqrt(rDivision);
    	double gsd = Math.sqrt(gDivision);
    	double bsd = Math.sqrt(bDivision);
    	
    	System.out.println("The rsd is: "+rsd);
    	System.out.println("The gsd is: "+gsd);
    	System.out.println("The bsd is: "+bsd);
    }
    
    //***********************************************
    //	Simple Thresholding
    //***********************************************
    public BufferedImage simpleThresholding(BufferedImage timg, int value){
    	
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	for(int y = 0; y<width; y++){
    		for(int x = 0; x<height; x++){
    			
    			if(ImageArray[x][y][1] > value){
    				ImageArray[x][y][1] = 255;
    			}else{
    				ImageArray[x][y][1] = 0;
    			}
    			
    			if(ImageArray[x][y][2] > value){
    				ImageArray[x][y][2] = 255;
    			}else{
    				ImageArray[x][y][2] = 0;
    			}
    			
    			if(ImageArray[x][y][3] > value){
    				ImageArray[x][y][3] = 255;
    			}else{
    				ImageArray[x][y][3] = 0;
    			}
    		}
    	}
    	
    	return convertToBimage(ImageArray);
    }
    
    //***********************************************
    //	Automated Thresholding
    //***********************************************
    public BufferedImage automatedThresholding(BufferedImage timg){
    	
    	int width = timg.getWidth();
    	int height = timg.getHeight();
    	
    	int [][][] ImageArray = convertToArray(timg);
    	
    	int mean_back_r = 0;
		int mean_back_g = 0;
		int mean_back_b = 0;
		int mean_obj_r = 0;
		int mean_obj_g = 0;
		int mean_obj_b = 0;
		//Initiation: assume that background is only 4 corners and object the others
		//find mean of obj and mean of background
		for( int y=0;y<height;y++){
          		    for(int x=0;x<width;x++){
			int r = ImageArray[x][y][1];
			int g = ImageArray[x][y][2];
			int b = ImageArray[x][y][3];

			if((y == 0 && x == 0) 
			|| (y == height-1 && x == 0) 
			|| (y == 0 && x == width-1) 
			|| (y ==height-1 && x == width-1)){
				mean_back_r += r;
				mean_back_g += g;
				mean_back_b += b;
			}else{
				mean_obj_r += r;
				mean_obj_g += g;
				mean_obj_b += b;
			}
		    }
		}
		mean_back_r = mean_back_r/4;
		mean_back_g = mean_back_g/4;
		mean_back_b = mean_back_b/4;
		mean_obj_r = mean_obj_r/((width*height)-4);
		mean_obj_g = mean_obj_g/((width*height)-4);
		mean_obj_b = mean_obj_b/((width*height)-4);
		int t1_r = (mean_back_r + mean_obj_r)/2;
		int t1_g = (mean_back_g + mean_obj_g)/2;
		int t1_b = (mean_back_b + mean_obj_b)/2;
		
		
		//Iteration to find proper threshold for red
		while(true){
			//System.out.println("In");
			mean_obj_r = 0;
			mean_back_r = 0;
			int cnt_obj_r = 0;
			int cnt_back_r = 0;
			int t_r = t1_r;
			for( int y = 0;y<height;y++)
          			    for(int x= 0;x<width;x++){
				int r = ImageArray[x][y][1];
				//compute background and object mean over segmented image
				if(r >= t_r){
					mean_obj_r += r;
					cnt_obj_r += 1;
				}else if (r < t_r){
					mean_back_r += r;
					cnt_back_r += 1;
				}
			     }
			if(mean_back_r>0){mean_back_r = mean_back_r/cnt_back_r;}
			if(mean_obj_r>0){mean_obj_r = mean_obj_r/cnt_obj_r;}

			t1_r = (mean_back_r + mean_obj_r)/2;
			//Stop loop
			if(Math.abs(t1_r - t_r)<1){
				break;			
			}			
		}
		//green
		while(true){
			//System.out.println("In");
			mean_obj_g = 0;
			mean_back_g = 0;
			int cnt_obj_g = 0;
			int cnt_back_g = 0;
			int t_g = t1_g;
			for( int y =0;y<height;y++)
          			    for(int x=0;x<width;x++){
				int g = ImageArray[x][y][2];
				//compute background and object mean over segmented image
				if(g >= t_g){
					mean_obj_g += g;
					cnt_obj_g += 1;
				}else if (g < t_g){
					mean_back_g += g;
					cnt_back_g += 1;
				}
			     }
			if(mean_back_g>0){mean_back_g = mean_back_g/cnt_back_g;}
			if(mean_obj_g>0){mean_obj_g = mean_obj_g/cnt_obj_g;}
			
			t1_g = (mean_back_g + mean_obj_g)/2;
			//Stop loop
			if(Math.abs(t1_g - t_g)<1){
				break;			
			}			
		}
		//blue
		while(true){
			//System.out.println("In");
			mean_obj_b = 0;
			mean_back_b = 0;
			int cnt_obj_b = 0;
			int cnt_back_b = 0;
			int t_b = t1_b;
			for( int y =0; y<height;y++)
          			    for(int x =0;x<width;x++){
          		int b = ImageArray[x][y][3];
				//compute background and object mean over segmented image
				if(b >= t_b){
					mean_obj_b += b;
					cnt_obj_b += 1;
				}else if (b < t_b){
					mean_back_b += b;
					cnt_back_b += 1;
				}
			     }
			if(mean_back_b>0){mean_back_b = mean_back_b/cnt_back_b;}
			if(mean_obj_b>0){mean_obj_b = mean_obj_b/cnt_obj_b;}
			t1_b = (mean_back_b + mean_obj_b)/2;
			//Stop loop
			if(Math.abs(t1_b - t_b)<1){
				break;			
			}			
		}
		//Apply threshold in order to segment image
		for( int y =0;y<height;y++){
          		    for(int x=0;x<width;x++){
			int r = ImageArray[x][y][1];
			int g = ImageArray[x][y][2];
			int b = ImageArray[x][y][3];
			
			if(r >= t1_r){
				r = 255;
			}else if (r < t1_r){
				r = 0;
			}
			if(g >= t1_g){
				g = 255;
			}else if (g < t1_g){
				g = 0;
			}
			if(b >= t1_b){
				b = 255;
			}else if (b < t1_b){
				b = 0;
			}
			
			ImageArray[x][y][1] = r;
			ImageArray[x][y][2] = g;
			ImageArray[x][y][3] = b;
		 	
		    }
		}
    	
    	return convertToBimage(ImageArray);
    }
    
    //************************************
    //  You need to register your function here
    //************************************
    public void filterImage() {
    	
        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
        case 0: biFiltered = bi; /* original */
        		storedImages.add(biFiltered);
                return; 
        case 1: 
        		//biFiltered = bi;
        		biFiltered = ImageNegative(biFiltered); /* Image Negative */
        		storedImages.add(biFiltered);
                return;
        case 2: 
        		//biFiltered = bi;
        		Scanner scaleInput = new Scanner(System.in);
        		System.out.println("Please enter a value to scale by i.e 0.5, 0.7, 1, 2 etc.");
	        	float scaleValue =scaleInput.nextFloat();
	        	biFiltered = ImageRescale(biFiltered, scaleValue);
	        	storedImages.add(biFiltered);
	        	return;
        case 3:
        		//biFiltered = bi;
        		Scanner shiftInput = new Scanner(System.in);
        		System.out.println("Please enter a whole number that you want to shift the image by i.e 1, 2, 3 etc.");
        		int shiftValue = shiftInput.nextInt();
        		biFiltered = ImageShifting(biFiltered, shiftValue);
        		storedImages.add(biFiltered);
        		return;
        case 4:
        		//biFiltered = bi;
        		biFiltered = ImageShiftingAndRescaling(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 5:
        		biFiltered = ImageAddition(biOriginal, biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 6:
        		biFiltered = ImageSubtraction(biOriginal, biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 7: 
        		biFiltered = ImageMultiplication(biOriginal, biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 8:
        		biFiltered = ImageDivision(biOriginal, biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 9: 
        		biFiltered = NOTOperation(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 10:
        		//System.out.println("Addition");
        		biFiltered = ANDOperation(biOriginal, biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 11:
        		biFiltered = OROperation(biOriginal, biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 12:
        		biFiltered = XOROperation(biOriginal, biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 13:
        		Scanner roiInput = new Scanner(System.in);
        		System.out.println("Please enter the top left x coordinate");
        		int xCoordinate =roiInput.nextInt();
        		System.out.println("Please enter the top left y coordinate");
        		int yCoordinate =roiInput.nextInt();
        		System.out.println("Please enter the horizontal length");
        		int horizontalLength = roiInput.nextInt();
        		System.out.println("Please enter the verticle length");
        		int verticleLength1 = roiInput.nextInt();
        		biFiltered = GenerateROIWithANDOperation(biFiltered, xCoordinate, yCoordinate, horizontalLength, verticleLength1);
        		storedImages.add(biFiltered);
        		return;
        case 14:
        		biFiltered = LogTransformation(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 15:
        		Scanner powerInput = new Scanner(System.in);
        		System.out.println("Please enter a value for the power law (0.01 to 25)");
        		float powerValue = powerInput.nextFloat();
        		if(powerValue > 0 && powerValue <= 25){
        			biFiltered = PowerLaw(biFiltered, powerValue);
        			storedImages.add(biFiltered);
        		}
        		else{
        			System.out.println("Your value was out of range");
        		}
        		return;
        case 16:
        		biFiltered = RandomLUTTransformation(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 17:
        		biFiltered = BitPlaneSlicing(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 18:
        		ImageHistogram(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 19:
        		ImageHistogramNormalization(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 20:
        		biFiltered = ImageHistogramEqualization(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 21:
        		Scanner optionInput = new Scanner(System.in);
        		System.out.println("Please enter a number for the process you want:\n"
        				+ "1. Averaging\n"
        				+ "2.Weighted Average\n"
        				+ "3. 4-neighbour Laplacian\n"
        				+ "4. 8- neighbour Laplacian\n"
        				+ "5. 4-neighbour Laplacian Enhancement\n"
        				+ "6. 8-neighbour Laplacian Enhancement\n"
        				+ "7. Roberts with absolute value conversion type 1\n"
        				+ "8. Roberts with absolute value conversion type 2\n"
        				+ "9. Sobel X with absolute value conversion\n"
        				+ "10. Sobel Y with absolute value conversion");
        		int option = optionInput.nextInt();
        		biFiltered = ImageConvolution(biFiltered, option);
        		storedImages.add(biFiltered);
        		
        		return;
        case 22:
        		biFiltered = SaltAndPepper(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 23:
	        	biFiltered = minimumFilter(biFiltered);
	        	storedImages.add(biFiltered);
	    		return;
        case 24:
	        	biFiltered = maximumFilter(biFiltered);
	        	storedImages.add(biFiltered);
	    		return;
        case 25:
        		biFiltered = midPointFilter(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 26:
        		biFiltered = medianFilter(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 27:
        		meanAndStandardDeviation(biFiltered);
        		return;
        case 28:
        		Scanner inputThreshold = new Scanner(System.in);
        		System.out.println("Please enter a value between 0 and 255 (inclusive)");
        		
        		int thresholdValue = inputThreshold.nextInt();
        		if(thresholdValue > 255 || thresholdValue < 0){
        			System.out.println("value out of bounds");
        		}else{
        			biFiltered = simpleThresholding(biFiltered, thresholdValue);
        			storedImages.add(biFiltered);
        		}      		
        		return;
        case 29:
        		biFiltered = automatedThresholding(biFiltered);
        		storedImages.add(biFiltered);
        		return;
        case 30:
        		return;
        //************************************
        // case 2:
        //      return;
        //************************************

        }
 
    }
 
     public void actionPerformed(ActionEvent e) {
    	 if(e.getSource() instanceof JComboBox){
    		 JComboBox cb = (JComboBox)e.getSource();
             if (cb.getActionCommand().equals("SetFilter")) {
                 setOpIndex(cb.getSelectedIndex());
                 repaint();
             } else if (cb.getActionCommand().equals("Formats")) {
            	 String format = (String)cb.getSelectedItem();
                 File saveFile = new File("savedimage."+format);
                 JFileChooser chooser = new JFileChooser();
                 chooser.setSelectedFile(saveFile);
                 int rval = chooser.showSaveDialog(cb);
                 if (rval == JFileChooser.APPROVE_OPTION) {
                     saveFile = chooser.getSelectedFile();
                     try {
                         ImageIO.write(biFiltered, format, saveFile);
                     } catch (IOException ex) {
                     }
                 }
             }
    	 }
    	 else{
    		 
    	//To activate the undo button you must first click it; then any following clicks will undo the image
    		 JButton undo = (JButton)e.getSource();
    		 setPreviousImage();
    		 repaint();
    	 }
    }
     
     public void setPreviousImage(){
    	 if(!storedImages.isEmpty()){
     		biFiltered = storedImages.get(storedImages.size()-1);
     		storedImages.remove(storedImages.size()-1);
     		if(storedImages.isEmpty()){
     			storedImages.add(bi);
     		}
 		}
     	return;
     }
 
    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        String selection = JOptionPane.showInputDialog("Do you want to load a raw image Y/n?");
        if(!selection.equals("Y")){
        	readingRawImage = false;
        }
        Demo de = new Demo();
        f.add("Center", de);
        f.setPreferredSize(new Dimension(w*2, h+100));
        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JButton undo = new JButton("Undo");
        undo.setActionCommand("Undo");
        undo.addActionListener(de);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Image 2"));
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        panel.add(undo);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }
}
