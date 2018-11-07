package na.static_opencv;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG2 = "Selection_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(this);
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(this);
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(this);
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(this);
        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(this);
        Button button5 = findViewById(R.id.button5);
        button5.setOnClickListener(this);
        Button button6 = findViewById(R.id.button6);
        button6.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button:
                CameraActivity.Affect = 0;
                Log.i(TAG2, "clicked button 1");
                break;
            case R.id.button1:
                CameraActivity.Affect = 1;
                Log.i(TAG2, "clicked button 2");
                break;
            case R.id.button2:
                CameraActivity.Affect = 2;
                Log.i(TAG2, "clicked button 3");
                break;
            case R.id.button3:
                CameraActivity.Affect = 3;
                Log.i(TAG2, "clicked button 4");
                break;
            case R.id.button4:
                CameraActivity.Affect = 4;
                Log.i(TAG2, "clicked button 5");
                break;
            case R.id.button5:
                CameraActivity.Affect = 5;
                Log.i(TAG2, "clicked button 6");
                break;
            default:
                CameraActivity.Affect = 6;
                break;
        }

        Intent switch_scene = new Intent(SelectionActivity.this, CameraActivity.class);
        startActivity(switch_scene);
    }
}
