package sg.edu.rp.webservices.c302_p13_omdb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewMovieDetailsActivity extends AppCompatActivity {

    private EditText etTitle, etRated, etReleased, etRuntime, etGenre, etActors, etPlot, etLanguage, etPoster;
    private Button btnUpdate, btnDelete;
    private String movieId;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private String TAG = "ViewMovieDetailsActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie_details);

        etTitle = findViewById(R.id.etTitle);
        etRated = findViewById(R.id.etRated);
        etReleased = findViewById(R.id.etReleased);
        etRuntime = findViewById(R.id.etRuntime);
        etGenre = findViewById(R.id.etGenre);
        etActors = findViewById(R.id.etActors);
        etPlot = findViewById(R.id.etPlot);
        etLanguage = findViewById(R.id.etLanguage);
        etPoster = findViewById(R.id.etPoster);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        movieId = intent.getStringExtra("movie_id");

	//TODO: get the movie record from Firestore based on the movieId
	// set the edit fields with the details
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("movies").document(movieId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Movie movie = document.toObject(Movie.class);
                        etTitle.setText(movie.getTitle());
                        etRated.setText(movie.getRating());
                        etReleased.setText(movie.getReleased());
                        etRuntime.setText(movie.getRuntime());
                        etGenre.setText(movie.getGenre());
                        etActors.setText(movie.getActors());
                        etPlot.setText(movie.getPlot());
                        etLanguage.setText(movie.getLanguage());
                        etPoster.setText(movie.getPoster());
                    }
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdateOnClick(v);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteOnClick(v);
            }
        });
    }//end onCreate

    
    private void btnUpdateOnClick(View v) {
		//TODO: create a Movie object and populate it with the values in the edit fields
		//save it into Firestore based on the movieId
        String title = etTitle.getText().toString();
        String rating = etRated.getText().toString();
        String released = etReleased.getText().toString();
        String runtime = etRuntime.getText().toString();
        String genre = etGenre.getText().toString();
        String actors = etActors.getText().toString();
        String plot = etPlot.getText().toString();
        String language = etLanguage.getText().toString();
        String poster = etPoster.getText().toString();

        Movie updateMovie = new Movie("", 0, title, rating, released, runtime, genre, "", "", actors, plot, language, poster);

        docRef
                .set(updateMovie)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        Toast.makeText(getApplicationContext(), "Movie record updated successfully", Toast.LENGTH_SHORT).show();

        finish();
    }//end btnUpdateOnClick

    private void btnDeleteOnClick(View v) {
		//TODO: delete from Firestore based on the movieId
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        Toast.makeText(getApplicationContext(), "Movie record deleted successfully", Toast.LENGTH_SHORT).show();

        finish();
    }//end btnDeleteOnClick

}//end class