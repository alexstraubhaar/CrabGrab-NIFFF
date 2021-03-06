
package calibration.UI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.openkinect.processing.Kinect2;

import controlP5.Button;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Slider;
import controlP5.Slider2D;
import gab.opencv.OpenCV;
import processing.core.PApplet;
import processing.core.PVector;

public class MainWindow extends PApplet
	{


	ChessboardFrame frameBoard;
	ChessboardApplet ca;
	Slider2D guiCpos;
	ControlP5 cp5;
	Slider guiCwidth;
	Kinect2 kinect2;
	PVector guiPos;
	ArrayList<PVector> projPoints = new ArrayList<PVector>();
	int cx, cy, cwidth;
	Button guiCalibrate;
	OpenCV opencv;
	ArrayList<PVector> clickedPoints = new ArrayList<PVector>();
	ArrayList<PVector> testingPoints = new ArrayList<PVector>();

	int pWidth = 1024;
	int pHeight = 768;
	int wWidth = 1400;
	int wHeight = 768;
	int sidePanelWidth = 200;

	boolean calibrated = false;

	Mat calibrationMatrix;
	@Override
	public void setup()
		{
		size(wWidth, wHeight);
		textFont(createFont("Courier", 24));

		frameBoard = new ChessboardFrame();

		kinect2 = new Kinect2(this);
		//kinect2.initDepth();
		//kinect2.enableMirror(false);
		kinect2.initVideo();
		kinect2.initDevice();

		//kinect.alternativeViewPointDepthToImage();
		//opencv = new OpenCV(this, kinect.depthWidth(), kinect.depthHeight());
		opencv = new OpenCV(this, kinect2.depthWidth, kinect2.depthHeight);

		setupGui();
		}

	void setupGui()
		{
		cp5 = new ControlP5(this);
		cp5.setFont(createFont("Courier", 16));
		guiPos = new PVector(wWidth - sidePanelWidth, 60);

		guiCpos = cp5.addSlider2D("chessPosition").setLabel("Position").setPosition(guiPos.x, guiPos.y + 15).setSize(sidePanelWidth, (int)(sidePanelWidth * (wHeight / (float)(wWidth - sidePanelWidth)))).setArrayValue(new float[] { 0, 0 });

		guiCwidth = cp5.addSlider("cwidth").setPosition(guiPos.x, guiPos.y + 420).setHeight(30).setWidth(sidePanelWidth).setRange(5, 800).setValue(100).setLabel("Size");

		guiCalibrate = cp5.addButton("calibrate").setPosition(guiPos.x, guiPos.y + 600).setSize(sidePanelWidth, 32).addCallback(new CallbackListener()
			{


			@Override
			public void controlEvent(CallbackEvent arg0)
				{
				calibrate();
				}
			});
		}

	@Override
	public void draw()
		{
		cx = (int)map(guiCpos.getArrayValue()[0], 0, 100, 0, pWidth);
		cy = (int)map(guiCpos.getArrayValue()[1], 0, 100, 0, pHeight);
		projPoints = drawChessboard(cx, cy, cwidth);

		opencv.loadImage(kinect2.getVideoImage());
		opencv.gray();

		drawGui();

		for(PVector p:clickedPoints)
			{
			fill(255, 255, 0);
			ellipse(map(p.x, 0, kinect2.depthWidth, 0, wWidth-sidePanelWidth), map(p.y, 0, kinect2.depthHeight, 0, wHeight), 5, 5);
			}
		}

	ArrayList<PVector> drawChessboard(int x0, int y0, int cwidth)
		{
		ArrayList<PVector> projPoints = new ArrayList<PVector>();
		int cheight = (int)(cwidth * 0.8);
		ca.background(255);
		ca.fill(0);
		for(int j = 0; j < 4; j++)
			{
			for(int i = 0; i < 5; i++)
				{
				int x = (int)(x0 + map(i, 0, 5, 0, cwidth));
				int y = (int)(y0 + map(j, 0, 4, 0, cheight));
				if (i > 0 && j > 0)
					{
					projPoints.add(new PVector((float)x / pWidth, (float)y / pHeight));
					}
				if ((i + j) % 2 == 0)
					{
					ca.rect(x, y, cwidth / 5, cheight / 4);
					}
				}
			}
		ca.fill(0, 255, 0);
		/*if (calibrated)
			{
			ca.ellipse(testPointP.x, testPointP.y, 20, 20);
			}*/

		if (calibrated)
			{
			for(PVector p:testingPoints)
				{
				Mat matSrc = new Mat(1, 1, CvType.CV_32FC2);
				matSrc.put(0, 0, new float[] { p.x, p.y });
				Mat matDest = new Mat(1, 1, CvType.CV_32FC2);

				Core.perspectiveTransform(matSrc, matDest, calibrationMatrix);

				ca.fill(0, 255, 0);
				System.out.println(p.x + ", " + p.y + " => " + (int)matDest.get(0, 0)[0] + ", " + (int)matDest.get(0, 0)[1]);
				ca.ellipse((int)(matDest.get(0, 0)[0] * pWidth), (int)(matDest.get(0, 0)[1] * pHeight), 5, 5);
				}
			}
		ca.redraw();
		return projPoints;
		}

	void drawGui()
		{
		background(0, 100, 0);
		image(kinect2.getVideoImage(), 0, 0, wWidth - sidePanelWidth, wHeight);
		}

	@Override
	public void mousePressed()
		{
		if (mouseX < wWidth - sidePanelWidth)
			{
			if (!calibrated)
				{
				clickedPoints.add(new PVector(map(mouseX, 0, wWidth-sidePanelWidth, 0, kinect2.depthWidth), map(mouseY, 0, wHeight, 0, kinect2.depthHeight)));
				}
			else
				{
				testingPoints.add(new PVector(map(mouseX, 0, wWidth-sidePanelWidth, 0, kinect2.depthWidth), map(mouseY, 0, wHeight, 0, kinect2.depthHeight)));
				}
			}
		}

	void calibrate()
		{
		MatOfPoint2f obj = new MatOfPoint2f();
		List<Point> listProjPoints = new ArrayList<Point>(projPoints.size());
		for(PVector p:projPoints)
			{
			Point point = new Point(p.x, p.y);
			listProjPoints.add(point);
			}
		obj.fromList(listProjPoints);

		MatOfPoint2f scene = new MatOfPoint2f();
		List<Point> listClickedPoints = new ArrayList<Point>(clickedPoints.size());
		for(PVector p:clickedPoints)
			{
			Point point = new Point(p.x, p.y);
			listClickedPoints.add(point);
			}
		scene.fromList(listClickedPoints);

		calibrationMatrix = Calib3d.findHomography(scene, obj);

		if (!calibrated)
			{
			calibrated = true;
			//guiSave.show();
			//guiTesting.addItem("Testing Mode", 1);
			}

		saveCalibrationMatrix("matrix.csv");
		}

	public void saveCalibrationMatrix(String filename)
		{
		try
			{
			FileWriter writer = new FileWriter(filename);

			for(int i = 0; i < calibrationMatrix.rows(); i++)
				{
				for(int j = 0; j < calibrationMatrix.cols(); j++)
					{
					double[] v = calibrationMatrix.get(i, j);
					if (v.length > 1)
						{
						writer.append("(");
						}
					for(int k = 0; k < v.length; k++)
						{
						writer.append(v[k] + "");
						if (k + 1 < v.length)
							{
							writer.append(",");
							}
						}
					if (v.length > 1)
						{
						writer.append(")");
						}
					if (j + 1 < calibrationMatrix.cols())
						{
						writer.append(";");
						}
					}
				writer.append('\n');
				}

			writer.flush();
			writer.close();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		}

	public class ChessboardFrame extends JFrame
		{


		public ChessboardFrame()
			{
			setBounds(displayWidth, 0, pWidth, pHeight);
			ca = new ChessboardApplet();
			add(ca);
			removeNotify();
			setUndecorated(true);
			setAlwaysOnTop(false);
			setResizable(false);
			addNotify();
			ca.init();
			show();
			}
		}

	public class ChessboardApplet extends PApplet
		{


		@Override
		public void setup()
			{
			noLoop();
			}

		@Override
		public void draw()
			{
			}
		}
	}
