package com.example.myapplication;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import okhttp3.MediaType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText userPromptEditText;
    private TextView responseTextView;
    private Button sendButton;

    private static final String API_KEY = "-c3iSIFGTf3TD4zJoQ6Q2T3BlbkFJgMk9rBmnrAjqrjk5LWAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userPromptEditText = findViewById(R.id.editTextTextMultiLine2);
        responseTextView = findViewById(R.id.textView6);
        sendButton = findViewById(R.id.button3);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPrompt = userPromptEditText.getText().toString();
                new OpenAIRequestTask().execute(userPrompt);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private class OpenAIRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(@NonNull String... params) {
            String userPrompt = params[0];
            String response = null;

            try {
                OkHttpClient client = new OkHttpClient();
                //String json = "{\"prompt\":\"" + userPrompt + "\"}";
                String json = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"user\",\"content\":\""+userPrompt+"\"}],\"temperature\":0.7}";
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .post(requestBody)
                        .build();

                Response apiResponse = client.newCall(request).execute();
                response = apiResponse.body().string();
                Log.d("OpenAIResponse", response);
                for (int i = 0; i < params.length; i++) {
                    Log.d("ArrayValues", "Index " + i + ": " + params[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray choices = jsonObject.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    System.out.println(choice);
                    JSONObject generatedText = choice.getJSONObject("message");
                    String ans = generatedText.getString("content");
                    responseTextView.setText(ans);
                }
            } catch (JSONException e) {
                e.toString();
                e.printStackTrace();
                responseTextView.setText("Error parsing response");
            }
        }
    }
}