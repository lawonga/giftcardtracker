package lawonga.giftcardtracker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lawonga on 11/17/2015.
 */
public class Credits extends Activity {
    TextView exit;
    CircleImageView andywong, kevinchua;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits);
        exit = (TextView)findViewById(R.id.exit_button);
        andywong = (CircleImageView)findViewById(R.id.andy_wong);
        kevinchua = (CircleImageView)findViewById(R.id.kevin_chua);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        andywong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.andy-wong.ca")));
            }
        });
        kevinchua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.kevinchua.ca")));
            }
        });
    }
}
