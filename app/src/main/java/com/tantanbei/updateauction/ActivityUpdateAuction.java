package com.tantanbei.updateauction;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActivityUpdateAuction extends Activity {

    public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
    private final String urlUpdatePrice = Const.SERVER_IP + "/auction/update";
    private final String urlUpdatePeople = Const.SERVER_IP + "/auction/update/people";
    private final String urlGet = Const.SERVER_IP + "/auction/price";
    private final String urlStart = Const.SERVER_IP + "/auction/start";
    private final String urlEnd = Const.SERVER_IP + "/auction/end";

    Timer timer = new Timer(true);
    TimerTask timerTask;

    EditText cautionPrice;
    EditText limitation;
    EditText overTime;
    Button plusOneHundred;
    Button addOne;
    Button addTen;
    Button addHundred;
    Button addThousand;
    TextView webStatus;
    Button start;
    Button end;

    int currentPrice = 0;
    int currentPeople = -1;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_auction);

        cautionPrice = (EditText) findViewById(R.id.caution_price);
        limitation = (EditText) findViewById(R.id.limitation);
        overTime = (EditText) findViewById(R.id.over_time);
        plusOneHundred = (Button) findViewById(R.id.plus_one_hundred);
        addOne = (Button) findViewById(R.id.add_one);
        addTen = (Button) findViewById(R.id.add_ten);
        addHundred = (Button) findViewById(R.id.add_hundred);
        addThousand = (Button) findViewById(R.id.add_thousand);
        webStatus = (TextView) findViewById(R.id.web_status);
        start = (Button) findViewById(R.id.start);
        end = (Button) findViewById(R.id.end);

        plusOneHundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPrice == 0) {
                    return;
                }
                currentPrice += 100;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            post(urlUpdatePrice, String.valueOf(currentPrice));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartAuction startAuction = new StartAuction();

                if (XString.IsEmpty(cautionPrice.getText().toString()) || XString.IsEmpty(limitation.getText().toString()) || XString.IsEmpty(overTime.getText().toString())) {
                    startAuction.cautionPrice = 84800;
                    startAuction.overTime = 1469244600000L;
                    startAuction.limitation = 11475;
                } else {
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
                            client.newCall(request).execute().close();
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
                            client.newCall(request).execute().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPeople < 0) {
                    return;
                }

                int tmp = currentPeople;

                switch (v.getId()) {
                    case R.id.add_one:
                        tmp++;
                        break;
                    case R.id.add_ten:
                        tmp += 10;
                        break;
                    case R.id.add_hundred:
                        tmp += 100;
                        break;
                    case R.id.add_thousand:
                        tmp += 1000;
                        break;
                }

                RequestBody body = RequestBody.create(TEXT, String.valueOf(tmp));
                final Request request = new Request.Builder()
                        .url(urlUpdatePeople)
                        .post(body)
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.newCall(request).execute().close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };

        addOne.setOnClickListener(listener);
        addTen.setOnClickListener(listener);
        addHundred.setOnClickListener(listener);
        addThousand.setOnClickListener(listener);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                getCurrentPrice(Const.SERVER_IP + "/auction/price");
            }
        };

        timer.schedule(timerTask, 1000, 300);
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

        try {

            Response response = call.execute();
            final byte[] bs = response.body().bytes();
            if (bs == null || bs.length == 0) {
                return;
            }

            switch (bs[0]) {
                case '1':
                    App.Uihandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityUpdateAuction.this, "unstart", Toast.LENGTH_LONG).show();
                        }
                    });
                    timerTask.cancel();
                    return;
                case '2':
                    App.Uihandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityUpdateAuction.this, "over", Toast.LENGTH_LONG).show();
                        }
                    });
                    timerTask.cancel();
                    return;
            }

            final CurrentPacket currentPacket = LoganSquare.parse(new String(bs, 1, bs.length - 1, "UTF-8"), CurrentPacket.class);
            final int price = currentPacket.currentTransactionPrice;
            final int people = currentPacket.peopleNumber;

            App.Uihandler.post(new Runnable() {

                @Override
                public void run() {
                    currentPrice = price;
                    currentPeople = people;
                    webStatus.setText("price:" + price + "\npeople:" + people);

                }
            });
        } catch (IOException e) {
            e.printStackTrace();

            App.Uihandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ActivityUpdateAuction.this, "request error", Toast.LENGTH_LONG).show();
                }
            });

            timer.cancel();
        } finally {
            call.cancel();
        }
    }
}
