package sg.edu.rp.webservices.c302_p13_omdb;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CreateMovieActivity extends AppCompatActivity {

    private EditText etTitle, etRated, etReleased, etRuntime, etGenre, etActors, etPlot, etLanguage, etPoster;
    private Button btnCreate, btnSearch;
    private ImageButton btnCamera;
    private String apikey;
    private FirebaseFirestore db;
    private CollectionReference colRef;
    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_movie);
        client = new AsyncHttpClient();

        etTitle = findViewById(R.id.etTitle);
        etRated = findViewById(R.id.etRated);
        etReleased = findViewById(R.id.etReleased);
        etRuntime = findViewById(R.id.etRuntime);
        etGenre = findViewById(R.id.etGenre);
        etActors = findViewById(R.id.etActors);
        etPlot = findViewById(R.id.etPlot);
        etLanguage = findViewById(R.id.etLanguage);
        etPoster = findViewById(R.id.etPoster);
        btnCreate = findViewById(R.id.btnCreate);
        btnSearch = findViewById(R.id.btnSearch);
        btnCamera = findViewById(R.id.btnCamera);


        //TODO: Retrieve the apikey from SharedPreferences
        //If apikey is empty, redirect back to LoginActivity
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CreateMovieActivity.this);
        apikey = pref.getString("apiKey", "");
        Log.i("apikey", apikey);
        if (apikey != "") {

            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnCreateOnClick(v);
                }
            });

            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnSearchOnClick(v);
                }
            });

            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnCameraOnClick(v);
                }
            });
        } else {
            Intent i = new Intent(CreateMovieActivity.this, LoginActivity.class);
            Toast.makeText(CreateMovieActivity.this, "Invalid Apikey!", Toast.LENGTH_SHORT).show();
            startActivity(i);
        }

    }//end onCreate

    //TODO: extract the fields and populate into a new instance of Movie class
    // Add the new movie into Firestore

    private void btnCreateOnClick(View v) {
        db = FirebaseFirestore.getInstance();
        colRef = db.collection("movies");
        String title = etTitle.getText().toString();
        String rating = etRated.getText().toString();
        String released = etReleased.getText().toString();
        String runtime = etRuntime.getText().toString();
        String genre = etGenre.getText().toString();
        String actors = etActors.getText().toString();
        String plot = etPlot.getText().toString();
        String language = etLanguage.getText().toString();
        String poster = etPoster.getText().toString();

        Movie newMovie = new Movie("", 0, title, rating, released, runtime, genre, "", "", actors, plot, language, poster);
        colRef
                .add(newMovie)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreateMovieActivity.this, "Movie added to Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateMovieActivity.this, "Movie not added to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
        //TODO: Task 3: Retrieve name and age from EditText and instantiate a new Student object
        //TODO: Task 4: Add student to database and go back to main screen

        finish();
    }

    //TODO: Call www.omdbapi.com passing the title and apikey as parameters
    // extract from JSON response and set into the edit fields
    private void btnSearchOnClick(View v) {
        RequestParams params = new RequestParams();
        params.put("t", etTitle.getText().toString());
        params.put("apikey", apikey);
        client.get("http://www.omdbapi.com", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.i("JSON Response", response.toString());
                    String title = response.getString("Title");
                    int year = Integer.parseInt(response.getString("Year"));
                    String rated = response.getString("Rated");
                    String released = response.getString("Released");
                    String runtime = response.getString("Runtime");
                    String genre = response.getString("Genre");
                    String director = response.getString("Director");
                    String writer = response.getString("Writer");
                    String actors = response.getString("Actors");
                    String plot = response.getString("Plot");
                    String language = response.getString("Language");
                    String poster = response.getString("Poster");
                    etTitle.setText(title);
                    etRated.setText(rated);
                    etReleased.setText(released);
                    etRuntime.setText(runtime);
                    etGenre.setText(genre);
                    etActors.setText(actors);
                    etPlot.setText(plot);
                    etLanguage.setText(language);
                    etPoster.setText(poster);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void btnCameraOnClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //TODO: feed imageBitmap into FirebaseVisionImage for text recognizing
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);

            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    // Task completed successfully
                                    // ...
                                    for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                                        String blockText = block.getText();
                                        etTitle.setText(blockText);
                                    }
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });
        }
    }
}