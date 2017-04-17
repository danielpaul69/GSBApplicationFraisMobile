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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import applicationfrais.gsb.com.applicationfrais.JSON.JSONDetail;
import applicationfrais.gsb.com.applicationfrais.Classes.LigneFraisForfait;
import applicationfrais.gsb.com.applicationfrais.Classes.LigneFraisHorsForfait;
import applicationfrais.gsb.com.applicationfrais.JSON.JSONParser;

public class DetailFicheFraisActivity extends AppCompatActivity {
    private static String URL_DETAIL = "http://10.0.2.2/gsbapplicationfrais/web/app_dev.php/api/detail/";

    JSONDetail JSONDetail = new JSONDetail();

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();

    int fichefraisId;

    private ListView listeLigneFraisForfaitLv, listeLigneFraisHorsForfaitLv;
    private TextView compteurLff, compteurLfhf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_fiche_frais);
        setTitle("Détail");

        listeLigneFraisForfaitLv = (ListView) findViewById(R.id.ligne_frais_forfait_lv);
        listeLigneFraisHorsForfaitLv = (ListView) findViewById(R.id.ligne_frais_hors_forfait_lv);
        compteurLff = (TextView) findViewById(R.id.compteur_lff);
        compteurLfhf = (TextView) findViewById(R.id.compteur_lfhf);

        new GetDetail().execute();

        // Recuperation de la l'id de la fiche
        Intent i = getIntent();
        fichefraisId = i.getIntExtra("id", 0);
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
                Intent i = new Intent(DetailFicheFraisActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    class GetDetail extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailFicheFraisActivity.this);
            pDialog.setMessage("Récupération des informations. Veuillez patientez...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            JSONObject json = jParser.makeHttpRequest(URL_DETAIL + fichefraisId, "GET", null);
            Log.i("JSONDetail ====>", json.toString());
            try {
                // Recuperation des donnees en JSON
                JSONDetail.Success = json.getInt(Global.TAG_SUCCESS);
                JSONDetail.Message = json.getString(Global.TAG_MESSAGE);

                // Recuperation des lignes frait forfait
                try {
                    JSONArray ligneFF = json.getJSONArray(Global.TAG_LIGNE_FRAIS_FORFAIT);
                    Log.i("FraisForfait =====>", ligneFF.toString());
                    if (ligneFF.length() > 0) {
                        for (int i = 0; i < ligneFF.length(); i++) {
                            JSONObject c = ligneFF.getJSONObject(i);

                            LigneFraisForfait ligneFraisForfait = new LigneFraisForfait();
                            ligneFraisForfait.Id = c.getInt(Global.TAG_ID);
                            ligneFraisForfait.FraisForfait = c.getString(Global.TAG_FRAIS_FORFAIT);
                            ligneFraisForfait.Montant = c.getDouble(Global.TAG_MONTANT);

                            JSONDetail.LignesFraisForfait.add(ligneFraisForfait);
                        }
                    }
                } catch (Exception e) {
                    Log.e("FraisForfait ===>", e.getMessage());
                }

                // Recuperation des lignes frait hors forfait
                try {
                    JSONArray ligneFHF = json.getJSONArray(Global.TAG_LIGNE_HORS_FORFAIT);
                    Log.i("FraisHorsForfait =====>", ligneFHF.toString());
                    if (ligneFHF.length() > 0) {
                        for (int i = 0; i < ligneFHF.length(); i++) {
                            JSONObject c = ligneFHF.getJSONObject(i);

                            LigneFraisHorsForfait ligneFraisHorsForfait = new LigneFraisHorsForfait();
                            ligneFraisHorsForfait.Id = c.getInt(Global.TAG_ID);
                            ligneFraisHorsForfait.Libelle = c.getString(Global.TAG_LIBELLE);
                            ligneFraisHorsForfait.Montant = c.getDouble(Global.TAG_MONTANT);

                            JSONDetail.LignesFraisHorsForfait.add(ligneFraisHorsForfait);
                        }
                    }
                } catch (Exception e) {
                    Log.e("FraisHorsForfait =====>", e.getMessage());
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

                    compteurLff.setText(JSONDetail.LignesFraisForfait.size() + " résultat(s)");
                    compteurLfhf.setText(JSONDetail.LignesFraisHorsForfait.size() + " résultat(s)");

                    LigneFraisForfaitAdapter adapterff = new LigneFraisForfaitAdapter(getApplicationContext(), JSONDetail.LignesFraisForfait);
                    listeLigneFraisForfaitLv.setAdapter(adapterff);

                    LigneFraisHorsForfaitAdapter adapterfhf = new LigneFraisHorsForfaitAdapter(getApplicationContext(), JSONDetail.LignesFraisHorsForfait);
                    listeLigneFraisHorsForfaitLv.setAdapter(adapterfhf);

                    Toast.makeText(getApplicationContext(), JSONDetail.Message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private class LigneFraisForfaitAdapter extends ArrayAdapter<LigneFraisForfait> {

        public LigneFraisForfaitAdapter(Context context, ArrayList<LigneFraisForfait> lignes) {
            super(context, 0, lignes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LigneFraisForfait ligne = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_ligne_frais_forfait, parent, false);
            }

            TextView tvId = (TextView) convertView.findViewById(R.id.item_lff_id);
            TextView tvFraisForfait = (TextView) convertView.findViewById(R.id.item_lff_frais_forfait);
            TextView tvMontant = (TextView) convertView.findViewById(R.id.item_lff_montant);

            tvId.setText(String.valueOf(ligne.Id));
            tvFraisForfait.setText(String.valueOf(ligne.FraisForfait));
            tvMontant.setText(String.valueOf(ligne.Montant));

            return convertView;
        }
    }

    private class LigneFraisHorsForfaitAdapter extends ArrayAdapter<LigneFraisHorsForfait> {

        public LigneFraisHorsForfaitAdapter(Context context, ArrayList<LigneFraisHorsForfait> lignes) {
            super(context, 0, lignes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LigneFraisHorsForfait ligne = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_ligne_frais_hors_forfait, parent, false);
            }

            TextView tvId = (TextView) convertView.findViewById(R.id.item_lfhf_id);
            TextView tvLibelle = (TextView) convertView.findViewById(R.id.item_lfhf_libelle);
            TextView tvMontant = (TextView) convertView.findViewById(R.id.item_lfhf_montant);

            if (ligne != null) {
                tvId.setText(String.valueOf(ligne.Id));
                tvLibelle.setText(String.valueOf(ligne.Libelle));
                tvMontant.setText(String.valueOf(ligne.Montant));
            }
            return convertView;
        }
    }
}