package applicationfrais.gsb.com.applicationfrais;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import applicationfrais.gsb.com.applicationfrais.Classes.Utilisateur;
import applicationfrais.gsb.com.applicationfrais.JSON.JSONConnexion;
import applicationfrais.gsb.com.applicationfrais.JSON.JSONParser;

public class MainActivity extends AppCompatActivity {

    private static String URL_LOGIN = "http://10.0.2.2/gsbapplicationfrais/web/app_dev.php/api/connexion";

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();

    private EditText identifiantEt, mdpEt;
    private String identifiant, motDePasse;
    private Button connexionBtn;

    private JSONConnexion JSONConnexion = new JSONConnexion();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Connexion");

        identifiantEt = (EditText) findViewById(R.id.identifiant_et);
        mdpEt = (EditText) findViewById(R.id.mdp_et);
        connexionBtn = (Button) findViewById(R.id.connexion_btn);

        // Evenement Click du bouton connexion
        connexionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recuperation des champs saisis par l'utilisateur
                identifiant = identifiantEt.getText().toString();
                motDePasse = mdpEt.getText().toString();

                if (motDePasse.isEmpty()){
                    mdpEt.setError("Champ obligatoire");
                    mdpEt.requestFocus();
                }

                if (identifiant.isEmpty()){
                    identifiantEt.setError("Champ obligatoire");
                    identifiantEt.requestFocus();
                }

                if (identifiant != null && !identifiant.isEmpty() && motDePasse != null && !motDePasse.isEmpty()) {
                    new TestConnection().execute();
                }
            }
        });
    }

    private class TestConnection extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Vérification des informations. Veuillez patientez...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Construction de l'URL pour passer en parametre les donnees login et mot de passe
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("login", identifiant));
            params.add(new BasicNameValuePair("mdp", motDePasse));
            JSONObject json = jParser.makeHttpRequest(URL_LOGIN, "GET", params);

            Log.i("Reponse ====>", json.toString());
            try {
                // Recuperation des donnees en JSON
                JSONConnexion.Success = json.getInt(Global.TAG_SUCCESS);
                JSONConnexion.Message = json.getString(Global.TAG_MESSAGE);

                if (JSONConnexion.Success == 1) {
                    JSONObject userInformations = json.getJSONObject(Global.TAG_UTILISATEUR);
                    Utilisateur user = new Utilisateur();
                    user.Id = userInformations.getInt(Global.TAG_ID);
                    user.Login = userInformations.getString(Global.TAG_LOGIN);
                    user.Nom = userInformations.getString(Global.TAG_NOM);
                    user.Prenom = userInformations.getString(Global.TAG_PRENOM);

                    JSONConnexion.Utilisateur = user;
                    Global.Utilisateur = JSONConnexion.Utilisateur;

                    // Lancement de l'activite ListeFicheFraisActivity
                    Intent i = new Intent(getApplicationContext(), ListeFicheFraisActivity.class);
                    startActivity(i);

                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), JSONConnexion.Message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}