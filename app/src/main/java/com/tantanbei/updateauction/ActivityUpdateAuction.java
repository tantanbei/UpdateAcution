package com.tantanbei.updateauction;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActivityUpdateAuction extends Activity {

    public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
    private final String url = Const.SERVER_IP + "/auction/update";
    private final String urlGet = Const.SERVER_IP + "/auction/price";
    private final String urlStart = Const.SERVER_IP + "/auction/start";
    private final String urlEnd = Const.SERVER_IP + "/auction/end";

    EditText cautionPrice;
    EditText limitation;
    EditText overTime;
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

        cautionPrice = (EditText) findViewById(R.id.caution_price);
        limitation = (EditText) findViewById(R.id.limitation);
        overTime = (EditText) findViewById(R.id.over_time);
        plusOneHundred = (Button) findViewById(R.id.plus_one_hundred);
        localPrice = (TextView) findViewById(R.id.local_price);
        webPrice = (TextView) findViewById(R.id.web_price);
        start = (Button) findViewById(R.id.start);
        end = (Button) findViewById(R.id.end);

        plusOneHundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPrice == 0) {
                    currentPrice = Integer.parseInt(cautionPrice.getText().toString());
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

                if (XString.IsEmpty(cautionPrice.getText().toString())||XString.IsEmpty(limitation.getText().toString())||XString.IsEmpty(overTime.getText().toString())){
                    startAuction.cautionPrice = 84800;
                    startAuction.overTime = 1469244600000L;
                    startAuction.limitation = 11475;
                }else {
                    startAuction.cautionPrice = Integer.parseInt(cautionPrice.getText().toString());
                    startAuction.overTime = Long.parseLong(overTime.getText().toString());
                    startAuction.limitation = Integer.parseInt(limitation.getText().toString());
                }

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

//    private void getCurrentPrice(String url) {
//        Request request = new Request.Builder()
//                .url(url)
//                .method("GET", null)
//                .build();
//
//        Call call = client.newCall(request);
//
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("tan", e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                final String str = response.body().string();
//
//                final CurrentPrice currPrice = LoganSquare.parse(str, CurrentPrice.class);
//
//                Log.d("tan", "carPrices: " + currPrice.toString());
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                Log.d("tan", "text price:" + webPrice.getText().toString() + " curr price:" + currPrice.price);
//                                if (webPrice.getText().toString().equals("")) {
//                                    webPrice.setText(String.valueOf(currPrice.price));
//                                } else if (Integer.parseInt(webPrice.getText().toString()) < currPrice.price) {
//                                    webPrice.setText(String.valueOf(currPrice.price));
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        });
//    }
}
