package pl.mkarebski.longanddifficulturlhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private int port = 8080;
    private AndroidHttpServer httpServer = new AndroidHttpServer(port);
    private EditText urlEditText;
    private ToggleButton toggleButton;
    private Context context = this;
    private TextView showIpView;
    private Button clearUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        urlEditText = (EditText) findViewById(R.id.url_et);
        toggleButton = (ToggleButton) findViewById(R.id.toggle_server);
        showIpView = (TextView) findViewById(R.id.show_ip);
        clearUrl = (Button) findViewById(R.id.clear_url);

        clearUrl.setEnabled(false);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String urlToRedirect = urlEditText.getText().toString();
                    if (!urlToRedirect.isEmpty()) {
                        Log.i("MainActivity", "Starting server");
                        try {
                            httpServer.start(urlToRedirect);
                        } catch (IOException e) {
                            Log.e("MainActivity", e.getMessage());
                            Toast.makeText(context, "Cannot start server", Toast.LENGTH_LONG).show();
                        }
                        Log.i("MainActivity", "Server started");
                        String ipAddress = getIpAddress();

                        showIpView.setText(String.format("Enter %s:%d to access your url", ipAddress, port));

                        clearUrl.setEnabled(false);
                    } else {
                        Toast.makeText(context, "Empty Url", Toast.LENGTH_LONG).show();
                    }
                } else {
                    stopServer();
                    clearUrl.setEnabled(true);
                    showIpView.setText("");
                }
            }
        });

        try {
            handleActionSend(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlEditText.setText("");
            }
        });
    }

    private void handleActionSend(Activity activity) throws IOException {
        Intent intent = activity.getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String urlToRedirect = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.i("MainActivity", "Content from other app: " + urlToRedirect);
                urlEditText.setText(urlToRedirect);
                toggleButton.setChecked(true);
                httpServer.start(urlToRedirect);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "Killing server");
        stopServer();
    }

    private void stopServer() {
        if (httpServer != null && httpServer.isAlive()) {
            httpServer.stop();
        }
    }

    @SuppressWarnings("deprecation")
    private String getIpAddress() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
}
