package sg.edu.rp.c346.p12webservice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private ListView lvIncidents;
    private ArrayList<Incident> alIncident;
    private ArrayAdapter<Incident> aaIncident;
    private AsyncHttpClient client;
    private FirebaseFirestore db;
    private Date date = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvIncidents = findViewById(R.id.listViewIncidents);

        alIncident = new ArrayList<Incident>();
        client = new AsyncHttpClient();
        client.addHeader("AccountKey", "cYsiznKuReChgmNVjkun9Q==");
        client.get("http://datamall2.mytransport.sg/ltaodataservice/TrafficIncidents", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("value");
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String type = jsonObj.getString("Type");
                        double latitude = jsonObj.getDouble("Latitude");
                        double longitude =  jsonObj.getDouble("Longitude");
                        String message = jsonObj.getString("Message");
                        Incident incident = new Incident(type, latitude, longitude, message, date);
                        alIncident.add(incident);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                aaIncident = new IncidentAdapter(getApplicationContext(), R.layout.row, alIncident);
                lvIncidents.setAdapter(aaIncident);
            }
        });

        lvIncidents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Incident selectedIncident = alIncident.get(position);
                Intent i = new Intent(getBaseContext(), MapsActivity.class);
                i.putExtra("incident", selectedIncident);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.upload) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Upload to Firestore");
            builder.setMessage("Proceed to upload to Firestore?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    db = FirebaseFirestore.getInstance();
                    CollectionReference colRef = db.collection("incidents");
                    for (int i = 0; i<alIncident.size(); i++) {
                        Incident newIncident = new Incident(alIncident.get(i).getType(),
                                alIncident.get(i).getLatitude(), alIncident.get(i).getLongitude(),
                                alIncident.get(i).getMessage(), date);
                        colRef.add(newIncident)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getBaseContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getBaseContext(), "Upload failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } else if (id == R.id.viewAllIncidents) {
            Intent i = new Intent(getBaseContext(), AllMapsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
