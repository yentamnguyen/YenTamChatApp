/*
 * @author: Trương Nguyễn Yên Tâm - AT110541 - AT11E
 */

package com.parse.starter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.LogRecord;

import com.github.bassaer.chatmessageview.views.ChatView;
import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.models.User;

public class ChatActivity extends AppCompatActivity {

    String activeUser = "";

    ArrayList<String> messages = new ArrayList<>();

    Handler handler;

    ChatView mChatView;

    Date date = new Date();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        //User id
        int myId = 0;
        //User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_2);
        //User name
        String myName = ParseUser.getCurrentUser().getUsername();

        int yourId = 1;
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        String yourName = activeUser;

        final User me = new User(myId, myName, myIcon);
        final User you = new User(yourId, yourName, yourIcon);

        Intent intent = getIntent();

        activeUser = intent.getStringExtra("username");

        setTitle("Chat with " + activeUser);

        mChatView = (ChatView) findViewById(R.id.chat_view);


        //Chỉnh sửa giao diện
        mChatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.green500));
        mChatView.setLeftBubbleColor(Color.WHITE);
        mChatView.setSendButtonColor(ContextCompat.getColor(this, R.color.cyan900));
        mChatView.setSendIcon(R.drawable.ic_action_send);
        mChatView.setRightMessageTextColor(Color.WHITE);
        mChatView.setLeftMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.WHITE);
        mChatView.setSendTimeTextColor(Color.WHITE);
        mChatView.setDateSeparatorColor(Color.WHITE);
        mChatView.setInputTextHint("enter new message...");
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);

        //Nếu người dùng ấn Send Button
        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String messageContent = mChatView.getInputText();
                ParseObject message = new ParseObject("Message");

                message.put("sender", ParseUser.getCurrentUser().getUsername());
                message.put("recipient", activeUser);
                message.put("message", messageContent);

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {

                            messages.add(messageContent);

                        }

                    }
                });

                //Reset edit text
                mChatView.setInputText("");

            }
        });


        handler = new Handler();

        //Đặt giá trị date ở thời điểm khởi đầu (thời điểm quá khứ cách xa thời điểm hiện tại)
        //để ứng dụng nhận hết tất cả tin nhắn từ server trong lần mở đầu tiên
        date.setTime(0);

        //Kiểm tra dữ liệu lưu của đối tượng date
        //Log.i("date", date.toString());

        Runnable refresh = new Runnable() {
            @Override
            public void run() {


                ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");


                //sender - người gửi là người dùng hiện thời đang sử dụng app
                query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
                //recipient - người nhận là người dùng đang hoạt động
                query1.whereEqualTo("recipient", activeUser);

                ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");

                //recipient - người nhận là người dùng hiện thời đang sử dụng app
                query2.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
                //sender - người gửi là người dùng đang hoạt động
                query2.whereEqualTo("sender", activeUser);


                Log.i("date", date.toString());
                query1.whereGreaterThan("createdAt", date);
                query2.whereGreaterThan("createdAt", date);

                final List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

                queries.add(query1);
                queries.add(query2);

                /*
                ParseQuery<ParseObject> queryMess = new ParseQuery<ParseObject>("Message");

                queryMess.whereGreaterThan("createdAt", date);

                final List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

                queries.add(queryMess);
                */

                ParseQuery<ParseObject> query = ParseQuery.or(queries);

                query.orderByAscending("createdAt");


                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {

                            if (objects.size() > 0) {

                                Log.i("status", "success");

                                for (ParseObject message : objects) {

                                    if (message.getCreatedAt().after(date)) {

                                        String messageContent = message.getString("message");
                                        Log.i("messageContent", messageContent);

                                        if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                                            //Receive message
                                            Message receivedMessage = new Message.Builder()
                                                    .setUser(you)
                                                    .setRightMessage(false)
                                                    .setMessageText(messageContent)
                                                    .setCreatedAt(toCalendar(message.getCreatedAt()))
                                                    .build();

                                            mChatView.receive(receivedMessage);


                                        } else {
                                            //new message
                                            Message sentMessage = new Message.Builder()
                                                    .setUser(me)
                                                    .setRightMessage(true)
                                                    .setMessageText(messageContent)
                                                    .setCreatedAt(toCalendar(message.getCreatedAt()))
                                                    .hideIcon(true)
                                                    .build();
                                            //Set to chat view
                                            mChatView.send(sentMessage);
                                        }

                                        date = (Date) message.getCreatedAt().clone();
                                        Log.i("Info", messageContent);

                                    }

                                }

                            }

                        }

                    }

                });

                handler.postDelayed(this, 200);

            }

        };

        handler.postDelayed(refresh, 200);

    }

    //Chuyển đổi kiểu Date sang kiểu Calendar
    private static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

}


