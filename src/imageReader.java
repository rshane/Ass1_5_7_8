
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
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
	//	BufferedImage[] allFrames = bytes2IMG(width, height, totalFrames, bytes);

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
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);		
	
		for(int frameIndex = 0; frameIndex < totalFrames; frameIndex++){
				//ind contains where frameNumber is located in bytes array	
			int ind = width*height*frameIndex*3;
			startTime = System.currentTimeMillis();
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

			computeTime = System.currentTimeMillis() - startTime;
			lbIm1.setIcon(new ImageIcon(img));
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			frame.getContentPane().add(lbIm1, c);
				
			frame.pack();

			frame.setVisible(true);
						
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
		inputImg.flush();		
	}
	public byte[] duplicatePixl(int ratioWidth, int ratioHeight, int prevRatioWidth, int prevRatioHeight, int outputWidth, int outputHeight, byte[] outputBytes, int outputFrameIndex, byte inputRedPxl, byte inputGreenPxl, byte inputBluePxl) {
		int col = ratioWidth - 1;
		int row = ratioHeight - 1;
		int elements = (ratioHeight - prevRatioHeight) * (ratioWidth - prevRatioWidth);
		while (elements > 0) {
			int crntPxl = row*outputWidth + col;
			if(row > 539) {
				System.out.println(String.format("row: %d, col: %d, line266", row, col));
			}
			outputBytes[outputFrameIndex + crntPxl] = inputRedPxl; //current red pixel equal to inputRedPxl
			outputBytes[outputFrameIndex + crntPxl + outputWidth*outputHeight] = inputGreenPxl; //current green pixel equal to inputGreenPxl
			outputBytes[outputFrameIndex + crntPxl + outputWidth*outputHeight*2] = inputBluePxl; //current blue pixel equal to inputBluePxl
			if (col == 0) {
				row--;
				col = ratioWidth -1;
				elements --;
			} else {
				col--;
				elements--;
			}
		}
		return outputBytes;
	}
	public void playFrame(byte[] outputBytes, int desiredFrame, int width, int height) {
		BufferedImage desiredImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int ind = width*height*desiredFrame*3;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){			
				byte a = 0;
				byte r = outputBytes[ind];
				byte g = outputBytes[ind+height*width];
				byte b = outputBytes[ind+height*width*2]; 
				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				//int pix = ((a << 24) + (r << 16) + (g << 8) + b); bit shifting
				desiredImg.setRGB(x,y,pix);
				ind++;
			}
		}
		displayImg(desiredImg, width, height);
		
	}
	
	public byte[] scaleUp(long inputLength, int inputHeight, int inputWidth, int outputWidth, int outputHeight, File inputFile){
		long totalFrames = inputLength/(inputWidth*inputHeight*3);
		byte[] outputBytes = new byte[(int) (outputWidth * outputHeight * totalFrames*3)];
		float resampleWidth = (float) outputWidth/inputWidth;
		float resampleHeight = (float) outputHeight/inputHeight;
		byte[] inputBytes = RGBFile2Bytes(inputFile, inputWidth, inputHeight);
		int inputOffset = 0;
		int inputIndex = 0;
		int outputFrameIndex = 0;
		for(int frameIndex = 0; frameIndex < totalFrames; frameIndex++){
			inputOffset = inputHeight*inputWidth*3*frameIndex; //frame we are on in smaller picture
			outputFrameIndex = outputHeight*outputWidth*3*frameIndex; //frame we are on in larger picture
			int prevResampleHeight = 0;
			for(int irow = 0; irow < inputHeight; irow++) {
				int orow = (int) ((irow +1) * resampleHeight); //number of rows need to copy
				int prevResampleWidth = 0;
				for(int icol = 0; icol < inputWidth; icol++) {
					int inputCurntPxlIndex = irow * inputWidth + icol; //current pixel you are on in smaller picture
					byte inputCurntRedPxl = inputBytes[inputOffset + inputCurntPxlIndex];
					byte inputCurntGreenPxl = inputBytes[inputOffset + inputWidth*inputHeight + inputCurntPxlIndex];
					byte inputCurntBluePxl = inputBytes[inputOffset + 2*inputWidth*inputHeight + inputCurntPxlIndex];
					int ocol = (int) ((icol + 1) * resampleWidth); //ocol is starting from 1 not 0 that is why add 1 to icol
					outputBytes = duplicatePixl(ocol, orow, prevResampleWidth, prevResampleHeight, outputWidth, outputHeight, outputBytes, outputFrameIndex, inputCurntRedPxl, inputCurntGreenPxl, inputCurntBluePxl);
					prevResampleWidth = ocol;
				}
				prevResampleHeight = orow;
			}
			
		}
		playFrame(outputBytes, 50, outputWidth, outputHeight);
		return outputBytes;
	}
	

	public void resize(String[] args){
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String operation = args[2];
		int antiAliasing = Integer.parseInt(args[3]);
		int inputWidth, inputHeight, outputWidth, outputHeight = 0;
		int icol, irow = 0;
		int cPxl = 0;
		int  nbrAvg = 0;
		byte[] bytes = null;
		byte[] outputBytes = null;
		long inputLen = inputFile.length();
		long totalFrames = 0; 
		float resampleWidth, resampleHeight = 0;
		
		if (operation.equals("HD2SD")) {
			inputWidth = 960;
			inputHeight = 540;
			outputWidth = 176;
			outputHeight = 144;
			resampleWidth = (float) inputWidth/outputWidth;
			resampleHeight = (float) inputHeight/outputHeight;
			totalFrames = inputLen/(inputWidth*inputHeight*3);
			outputBytes = new byte[(int) (outputWidth * outputHeight * totalFrames*3)];
			bytes = RGBFile2Bytes(inputFile, inputWidth, inputHeight);
			//BufferedImage[] allFramesInput = bytes2IMG(inputWidth, inputHeight, totalFrames, bytes);
			

			int inputOffset = 0;
			int inputIndex = 0;
			int outputIndex = 0;
			//int nbrW, nbrE, nbrS, nbrN, nbrNW, nbrNE, nbrSW, nbrSE
			for(int frameIndex = 0; frameIndex < totalFrames; frameIndex++){
				inputOffset = inputHeight*inputWidth*3*frameIndex;
				// int outputIndex = 0;
				outputIndex = outputHeight*outputWidth*3*frameIndex; 
				for(int counterRow = 1; (counterRow * resampleHeight) < inputHeight; counterRow++){ //start at position (1,1) so when avg have values for 3x3
					irow = (int) (counterRow * resampleHeight);
					for(int counterCol = 1; (counterCol*resampleWidth) < inputWidth; counterCol++){  //inputCol =inputCol*resampleWidth
						icol= (int) (counterCol* resampleWidth); // column rounded to nearest int
						inputIndex = inputOffset +  (irow - 1) * inputWidth + (icol - 1);
						int a = 0;
						byte cPxlRByte = bytes[inputIndex];
						byte cPxlGByte = bytes[inputIndex+inputHeight*inputWidth];						
						byte cPxlBByte = bytes[inputIndex+inputHeight*inputWidth*2];
						int outputPxl = (counterCol -1) + (counterRow -1 )*outputWidth;
						if (antiAliasing == 1) {
							//get all cPxl neighbors and average them
							int nbrIndex = 0, totalNbrR = 0, totalNbrG = 0, totalNbrB= 0;
							int nbrAvgR =0, nbrAvgG = 0, nbrAvgB = 0;
							for(int y= -1; y < 2; y++) {
								for(int x =-1; x < 2; x++) {
									nbrIndex = inputOffset + (irow + y) * inputWidth + (icol + x); //help is it irow -1 +y?
									int nbrR = (bytes[nbrIndex] & 0xff) << 16;
									int nbrG = (bytes[nbrIndex+inputHeight*inputWidth] & 0xff) << 8;
									int nbrB = (bytes[nbrIndex+inputHeight*inputWidth*2] & 0xff);
									totalNbrR = totalNbrR + nbrR;
									totalNbrG = totalNbrG + nbrG;
									totalNbrB = totalNbrB + nbrB;
								}
							}
							nbrAvgR = 0x00ff0000 & (totalNbrR/9);
							nbrAvgG = 0x0000ff00 & (totalNbrG/9);
							nbrAvgB = 0x000000ff & (totalNbrB/9);
							byte nbrAvgRByte = (byte) (nbrAvgR >> 16);
							byte nbrAvgGByte = (byte) (nbrAvgG >> 8);
							byte nbrAvgBByte = (byte) (nbrAvgB);
	
							outputBytes[outputIndex + outputPxl] = nbrAvgRByte; //outputIndex
							outputBytes[outputIndex + outputPxl + outputHeight*outputWidth] = nbrAvgGByte;
							outputBytes[outputIndex + outputPxl + outputHeight*outputWidth*2] = nbrAvgBByte;		
						} else {
							outputBytes[outputIndex + outputPxl] = cPxlRByte;
							outputBytes[outputIndex + outputPxl + outputHeight*outputWidth] = cPxlGByte;
							outputBytes[outputIndex + outputPxl + outputHeight*outputWidth*2] = cPxlBByte;
						}
					}
				}				
			}	      				
		}
		if (operation.equals("SD2HD")) {
			inputWidth = 176;
			inputHeight = 144; 
			outputWidth = 960;
			outputHeight = 540;
			resampleWidth = (float) outputWidth/inputWidth;
			resampleHeight = (float) outputHeight/inputHeight;
			totalFrames = inputLen/(inputWidth*inputHeight*3);
			outputBytes = new byte[(int) (outputWidth * outputHeight * totalFrames*3)];
			bytes = RGBFile2Bytes(inputFile, inputWidth, inputHeight);
			outputBytes = scaleUp(inputLen, inputHeight, inputWidth, outputWidth, outputHeight, inputFile);
		}
		try {
			outputStream.write(outputBytes);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public static void main(String[] args) {
		imageReader ren = new imageReader();
		ren.resize(args);
		String[] test = {"/Users/shane/Documents/workspace/imageReader/test/prison_960_540.rgb","960","540","10"};
		ren.showIms(test);
	}

}