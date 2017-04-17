package applicationfrais.gsb.com.applicationfrais;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import applicationfrais.gsb.com.applicationfrais.Classes.FicheFrais;
import applicationfrais.gsb.com.applicationfrais.JSON.JSONFicheFrais;
import applicationfrais.gsb.com.applicationfrais.JSON.JSONParser;

public class ListeFicheFraisActivity extends AppCompatActivity {

    private static String URL_FICHE_FRAIS = "http://10.0.2.2/gsbapplicationfrais/web/app_dev.php/api/fichefrais/";

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();

    private JSONFicheFrais JSONFicheFrais = new JSONFicheFrais();

    private ListView listeFicheLv;
    private TextView compteurFfTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_fiche_frais);
        setTitle("Fiches frais");

        listeFicheLv = (ListView) findViewById(R.id.listeFiche_lv);
        compteurFfTv = (TextView) findViewById(R.id.compteur_ff);

        new GetFicheFrais().execute();

        // Evenement Click d'un element de la liste
        listeFicheLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lancement de l'activite DetailFicheFraisActivity en passant en paramètre l'id de la fiche
                Intent i = new Intent(ListeFicheFraisActivity.this, DetailFicheFraisActivity.class);
                i.putExtra("id", JSONFicheFrais.FicheFrais.get(position).Id);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.informations:
                Toast.makeText(getApplicationContext(), "Utilisateur connecté : " + Global.Utilisateur.Login, Toast.LENGTH_LONG).show();
                return true;

            case R.id.deconnexion:
                Global.Utilisateur = null;
                Intent i = new Intent(ListeFicheFraisActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    class GetFicheFrais extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListeFicheFraisActivity.this);
            pDialog.setMessage("Récupération des informations. Veuillez patientez...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Construction de l'URL pour passer en parametre l'id de la fiche
            JSONObject json = jParser.makeHttpRequest(URL_FICHE_FRAIS + Global.Utilisateur.Id, "GET", null);
            Log.i("JSONFicheFrais ======>", json.toString());
            try {
                // Recuperation des donnees en JSON
                JSONFicheFrais.Success = json.getInt(Global.TAG_SUCCESS);
                JSONFicheFrais.Message = json.getString(Global.TAG_MESSAGE);

                JSONArray ficheFrais = json.getJSONArray(Global.TAG_FICHE_FRAIS);
                for (int i = 0; i < ficheFrais.length(); i++) {
                    JSONObject c = ficheFrais.getJSONObject(i);

                    FicheFrais fiche = new FicheFrais();
                    fiche.Id = c.getInt(Global.TAG_ID);
                    fiche.Mois = c.getInt(Global.TAG_MOIS);
                    fiche.Annee = c.getInt(Global.TAG_ANNEE);

                    JSONFicheFrais.FicheFrais.add(fiche);
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
                    compteurFfTv.setText(JSONFicheFrais.FicheFrais.size() + " résultat(s)");
                    FicheFraisAdapter adapter = new FicheFraisAdapter(getApplicationContext(), JSONFicheFrais.FicheFrais);
                    listeFicheLv.setAdapter(adapter);

                    Toast.makeText(getApplicationContext(), JSONFicheFrais.Message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    class FicheFraisAdapter extends ArrayAdapter<FicheFrais> {

        public FicheFraisAdapter(Context context, ArrayList<FicheFrais> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FicheFrais user = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_fichefrais, parent, false);
            }

            TextView tvId = (TextView) convertView.findViewById(R.id.item_id);
            TextView tvMois = (TextView) convertView.findViewById(R.id.item_mois);
            TextView tvAnnee = (TextView) convertView.findViewById(R.id.item_annee);

            if (user != null){
                tvId.setText(String.valueOf(user.Id));
                tvMois.setText(String.valueOf(user.Mois));
                tvAnnee.setText(String.valueOf(user.Annee));
            }
            return convertView;
        }
    }
}