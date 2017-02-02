
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;

import javax.swing.*;


public class imageReader {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;

	public void showIms_Original(String[] args){
		int width = Integer.parseInt(args[1]);
		int height = Integer.parseInt(args[2]);
		
		//
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		try {
			File file = new File(args[0]);
			InputStream is = new FileInputStream(file);

			long len = file.length();
			byte[] bytes = new byte[(int)len];
			//read the whole file into  to temp buffer called bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				//is - inputer stream, read offset(start) bytes for bytes.length bytes and store it into bytes
				//reading files into bytes array
				offset += numRead;
			}

			//write to image buffer
			int ind = 0;
			for(int y = 0; y < height; y++){
				long startTime = System.nanoTime();
				for(int x = 0; x < width; x++){
					
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b); bit shifting
					img.setRGB(x,y,pix);
					ind++;
				}	
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);
		String result = String.format("Video height: %d, width: %d", height, width);
		JLabel lbText1 = new JLabel(result);
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		
		lbIm1 = new JLabel(new ImageIcon(img));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public byte[] RGBFile2Bytes(File file, int width, int height) {
		byte[] bytes = null;
		try {
			//file only contains RGB no alpha
			InputStream is = new FileInputStream(file);
			long len = file.length();
			bytes = new byte[(int)len];

			//read the whole file into  to temp buffer called bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				//is(byte[], off, len) reads up to len bytes from is, attempt to read len bytes but smaller amount may be read
				//return number of bytes read as int, offset tells b[off] through b[off+k-1] where k is amount read
				offset += numRead;
			}
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public BufferedImage[] bytes2IMG(int width, int height, long totalFrames, byte[] bytes) {
		BufferedImage[] allFramesAsImages = new BufferedImage[(int)totalFrames];
		for(int frameIndex = 0; frameIndex < totalFrames; frameIndex++){
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			//ind contains where frameNumber is located in bytes array	
			int ind = width*height*frameIndex*3;
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){				
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 
					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b); bit shifting
					img.setRGB(x,y,pix);
					ind++;
					}
				}
			allFramesAsImages[frameIndex] = img;
			img.flush();
		}
		return allFramesAsImages;

	}
	
	
	public void showIms(String[] args){
		int width = Integer.parseInt(args[1]);
		int height = Integer.parseInt(args[2]);
		int framePerSec = Integer.parseInt(args[3]);
		File file = new File(args[0]);
		
		
		//TYPE_INT_RGB Represents an image with 8-bit RGB color components packed into integer pixels 
		img = null;
		long len = file.length(); 
		long totalFrames = len/(width*height*3);
		long sleepTime = 1000/framePerSec;
		byte[] bytes = null;
		sleepTime = 1000/framePerSec;
		long startTime = 0; //used to find compute time
		long computeTime = 0;

		bytes = RGBFile2Bytes(file, width, height);
		BufferedImage[] allFrames = bytes2IMG(width, height, totalFrames, bytes);

		// Use labels to display the images
		frame = new JFrame();
		//when click x button frame closes
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//GridBagLayout places components in a grid of rows and columns
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);
		String result = String.format("Video height: %d, width: %d", height, width);
		JLabel lbText1 = new JLabel(result);
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel();
		
		GridBagConstraints c = new GridBagConstraints();
		//Stretches frame horizontally
		c.fill = GridBagConstraints.HORIZONTAL; //Resize the component horizontally but not vertically
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5; //Specifies how to distribute extra horizontal space
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);
			
	
		for(int frameIndex = 0; frameIndex < allFrames.length; frameIndex++){
			startTime = System.currentTimeMillis();
			img = allFrames[frameIndex];
			computeTime = System.currentTimeMillis() - startTime;
			lbIm1.setIcon(new ImageIcon(img));
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			frame.getContentPane().add(lbIm1, c);
				
			frame.pack();

			frame.setVisible(true);
			System.out.println(String.format("sleepTime: %d, computeTime: %d, nst:%d, frame:%d", sleepTime, computeTime,sleepTime - computeTime, frameIndex));
			
			try {
				Thread.sleep(sleepTime - computeTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				

			img.flush();
			if (frameIndex + 1 >= totalFrames) {
				frameIndex = 0;
			}
		}

	}
	
	public void displayImg(BufferedImage inputImg, int width, int height) {
		frame = new JFrame();
		//when click x button frame closes
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//GridBagLayout places components in a grid of rows and columns
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);
		String result = String.format("Video height: %d, width: %d", height, width);
		JLabel lbText1 = new JLabel(result);
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel();
		
		GridBagConstraints c = new GridBagConstraints();
		//Stretches frame horizontally
		c.fill = GridBagConstraints.HORIZONTAL; //Resize the component horizontally but not vertically
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5; //Specifies how to distribute extra horizontal space
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);
		lbIm1.setIcon(new ImageIcon(inputImg));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);
			
		frame.pack();

		frame.setVisible(true);
		img.flush();		
	}
	

	public void resize(String[] args){
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		String operation = args[2];
		int antiAliasing = Integer.parseInt(args[3]);
		int inputWidth, inputHeight, outputWidth, outputHeight = 0;
		int icol, irow, nbrW, nbrNW, nbrN, nbrNE, nbrE, nbrSE, nbrS, nbrSW, nbrAvg, cPxl = 0;
		byte[] bytes = null;
		long inputLen = inputFile.length();
		long totalFrames = 0; 
		float resampleWidth, resampleHeight = 0;
		BufferedImage inputImg, outputImg = null;
		
		if (operation.equals("HD2SD")) {
			inputWidth = 960;
			inputHeight = 540;
			outputWidth = 176;
			outputHeight = 144;
			resampleWidth = (float) inputWidth/outputWidth;
			resampleHeight = (float) inputHeight/outputHeight;
			outputImg = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB);
			totalFrames = inputLen/(inputWidth*inputHeight*3);
			
			bytes = RGBFile2Bytes(inputFile, inputWidth, inputHeight);
			BufferedImage[] allFramesInput = bytes2IMG(inputWidth, inputHeight, totalFrames, bytes);
			
		

			int frameIndex = 50;
			inputImg =allFramesInput[frameIndex];
			displayImg(inputImg, inputWidth, inputHeight);
			int orow = 0, ocol = 0, crntPxl = 0;
			for(int counterRow = 1; (counterRow * resampleHeight) < inputHeight; counterRow++){ //start at position (1,1) so when avg have values for 3x3
				irow = (int) (counterRow * resampleHeight);
				ocol = 0;
				for(int counterCol = 1; (counterCol*resampleWidth) < inputWidth; counterCol++){  //inputCol =inputCol*resampleWidth
					icol= (int) (counterCol* resampleWidth); // column rounded to nearest int
					 
					//grab all adjacent pixels and center pixel
					cPxl = inputImg.getRGB(icol, irow);
					//int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					byte r = (byte) ((0x00ff0000 & cPxl) >> 16);
					byte g = (byte) ((0x0000ff00 & cPxl) >> 8);
					byte b = (byte) (0x000000ff & cPxl);
					
					if (antiAliasing == 1) { 
						nbrW = inputImg.getRGB(icol - 1, irow); //left neighbor
						nbrNW = inputImg.getRGB(icol - 1, irow + 1); //top left neighbor
						nbrN = inputImg.getRGB(icol, irow + 1); //above neighbor
						nbrNE = inputImg.getRGB(icol + 1, irow + 1); //top right neighbor
						nbrE = inputImg.getRGB(icol + 1, irow); //right neighbor
						nbrSE = inputImg.getRGB(icol + 1, irow - 1); //bottom right neighbor
						nbrS = inputImg.getRGB(icol - 1, irow); //bottom neighbor
						nbrSW = inputImg.getRGB(icol - 1, irow - 1); //bottom left neighbor
						//Avg all neighbors
						nbrAvg = (cPxl + nbrW + nbrNW + nbrN + nbrNE + nbrE + nbrSE + nbrS + nbrSW)/9;
					} else {
						nbrAvg = cPxl;
					}
					
					outputImg.setRGB(ocol, orow, nbrAvg);
					if (counterCol == 175) {
						System.out.println("test line 303");
					}
					ocol++;
				}
				orow++;
			}
			// Use labels to display the images
			frame = new JFrame();
			//when click x button frame closes
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//GridBagLayout places components in a grid of rows and columns
			GridBagLayout gLayout = new GridBagLayout();
			frame.getContentPane().setLayout(gLayout);
			String result = String.format("Video height: %d, width: %d", outputHeight, outputWidth);
			JLabel lbText1 = new JLabel(result);
			lbText1.setHorizontalAlignment(SwingConstants.CENTER);
			lbIm1 = new JLabel();
			
			GridBagConstraints c = new GridBagConstraints();
			//Stretches frame horizontally
			c.fill = GridBagConstraints.HORIZONTAL; //Resize the component horizontally but not vertically
			c.anchor = GridBagConstraints.CENTER;
			c.weightx = 0.5; //Specifies how to distribute extra horizontal space
			c.gridx = 0;
			c.gridy = 0;
			frame.getContentPane().add(lbText1, c);
			lbIm1.setIcon(new ImageIcon(outputImg));
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			frame.getContentPane().add(lbIm1, c);
				
			frame.pack();

			frame.setVisible(true);
			img.flush();
			
				
		}
		
	}


	public static void main(String[] args) {
		imageReader ren = new imageReader();
		//ren.showIms(args);
		ren.resize(args);
	}

}