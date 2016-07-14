package com.tantanbei.updateauction;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActivityUpdateAuction extends Activity {

    public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
    private final String url = "http://192.168.1.4:80/auction/update";
    private final String urlGet = "http://192.168.1.4:80/auction/price";
    private final String urlStart = "http://192.168.1.4:80/auction/start";
    private final String urlEnd = "http://192.168.1.4:80/auction/end";

    EditText warningPrice;
    Button plusOneHundred;
    TextView localPrice;
    TextView webPrice;
    Button start;
    Button end;

    int currentPrice;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_auction);

        warningPrice = (EditText) findViewById(R.id.warning_price);
        plusOneHundred = (Button) findViewById(R.id.plus_one_hundred);
        localPrice = (TextView) findViewById(R.id.local_price);
        webPrice = (TextView) findViewById(R.id.web_price);
        start = (Button) findViewById(R.id.start);
        end = (Button) findViewById(R.id.end);

        plusOneHundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPrice == 0) {
                    currentPrice = Integer.parseInt(warningPrice.getText().toString());
                }
                currentPrice += 100;
                localPrice.setText(String.valueOf(currentPrice));
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            post(url, String.valueOf(currentPrice));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

//                Thread thread2 = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (true) {
//                            getCurrentPrice(urlGet);
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//                thread2.start();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartAuction startAuction = new StartAuction();
                startAuction.cautionPrice = Integer.parseInt(warningPrice.getText().toString());
                startAuction.overTime = System.currentTimeMillis() + 120000;

                RequestBody body = null;
                try {
                    body = RequestBody.create(TEXT, LoganSquare.serialize(startAuction));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final Request request = new Request.Builder()
                        .url(urlStart)
                        .method("POST", body)
                        .build();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Request request = new Request.Builder()
                        .url(urlEnd)
                        .method("GET", null)
                        .build();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }

    private String post(String url, String price) throws IOException {

        RequestBody body = RequestBody.create(TEXT, price);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    private void getCurrentPrice(String url) {
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("tan", e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String str = response.body().string();

                final CurrentPrice currPrice = LoganSquare.parse(str, CurrentPrice.class);

                Log.d("tan", "carPrices: " + currPrice.toString());
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Log.d("tan", "text price:" + webPrice.getText().toString() + " curr price:" + currPrice.price);
                                if (webPrice.getText().toString().equals("")) {
                                    webPrice.setText(String.valueOf(currPrice.price));
                                } else if (Integer.parseInt(webPrice.getText().toString()) < currPrice.price) {
                                    webPrice.setText(String.valueOf(currPrice.price));
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
