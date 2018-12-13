package na.static_opencv;

import android.content.Context;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class CameraActivity extends AppCompatActivity implements CvCameraViewListener2 {

    // Used for logging success or failure messages
    private static final String TAG = "OCVSample::Activity";

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    // Used in Camera selection from menu (when implemented)
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    public static int Affect = 0;

    private CascadeClassifier cascadeClassifier;
    private int absoluteFaceSize;
    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;
    Mat transform;


    private void initializeOpenCVDependencies() {

        try {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile;
            mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);


            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            // Load the cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Error loading cascade", e);
        }

        // And we are ready to go
        mOpenCvCameraView.enableView();
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    initializeOpenCVDependencies();
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    public void onCameraViewStarted(int width, int height) {

        Log.i(TAG, "height and width is " + height + " " + width);

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        transform = new Mat(height, width, CvType.CV_8UC4);
    }


    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        // TODO Auto-generated method stub
        mRgba = inputFrame.rgba();
        transform = inputFrame.rgba();
        Mat grey = new Mat();
        int orientation = this.getResources().getConfiguration().orientation;
        // Log.i(TAG, "the orientation is " + orientation);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Core.transpose(mRgba, mRgbaT);
            Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 1,1, 1);
            Core.flip(mRgba, mRgba, 1 );
        }

        switch (Affect){
            case 0:
                Imgproc.medianBlur(mRgba, mRgba, 7 );
                break;
            case 1:
                Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2GRAY, 4 );
                break;
            case 2:
                Core.flip(mRgba, mRgba, 1);
                break;
            case 3:
                Imgproc.dilate(mRgba, mRgba, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10)));
                break;
            case 4:
                Imgproc.erode(mRgba, mRgba, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20)));
                break;
            case 5:
                Imgproc.cvtColor(mRgba, grey, Imgproc.COLOR_BGR2GRAY, 4 );
                Imgproc.Canny(grey, mRgba, 60, 60*3, 3,false);
                break;
            case 6:
                MatOfRect faces = new MatOfRect();
                Imgproc.cvtColor(mRgba, grey, Imgproc.COLOR_BGR2GRAY, 4 );
                if (cascadeClassifier != null) {
                    cascadeClassifier.detectMultiScale(grey, faces, 1.1, 2, 2,
                            new Size(absoluteFaceSize, absoluteFaceSize), new Size());
                }
                Rect[] facesArray = faces.toArray();
                for (int i = 0; i <facesArray.length; i++)
                    Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
            default:
                break;
        }

        return mRgba; // This function must return
    }
}