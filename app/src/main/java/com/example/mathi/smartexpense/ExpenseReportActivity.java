package com.example.mathi.smartexpense;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.mathi.smartexpense.model.ExpenseReport;
import com.example.mathi.smartexpense.network.ExpenseReportAdapter;
import com.example.mathi.smartexpense.model.User;
import com.example.mathi.smartexpense.network.HttpGetRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Pierre Gyejacquot, Ahmed Hamad and Mathilde Person.
 */

public class ExpenseReportActivity extends AppCompatActivity {

    final String FILE_EXPENSE_REPORT = "file_expense_report";
    final String EXPENSE_REPORT_KEY = "expense_report";
    final String LOGIN_PASS_KEY = "user_profile";
    final String FILE_PROFILE = "file_user_profile";

    private User user = new User();

    Button buttonAdd;
    Button buttonReturn;

    private ExpenseReportAdapter adapter;
    ListView liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        buttonAdd = findViewById(R.id.addERButton);
        buttonReturn = findViewById(R.id.returnButton);
        liste = findViewById(R.id.listExpenseReport);

/** récupération des données stockées dans le fichier SharedPreferences */
        SharedPreferences myPref = this.getSharedPreferences(FILE_PROFILE, Context.MODE_PRIVATE);
        String user_profile = myPref.getString(LOGIN_PASS_KEY, "{}");
        Log.v("shared_preferences", user_profile);
        JSONObject userProfile = null;
        try {
            userProfile = new JSONObject(user_profile);
            user = user.jsonToUser(userProfile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /** Gestion de la ListView */
        // liste des notes de frais qui seront injectées dans la ListView
        List<ExpenseReport> erList = new ArrayList<ExpenseReport>();
        // URL de l'API qui récupère les données des notes de frais
        String myURL = "http://www.gyejacquot-pierre.fr/API/public/expensereports?idUser="+user.getIdUser();
        //String myURL = "http://10.0.2.2/API/public/expensereports?idUser="+user.getIdUser();
        // on instancie la classe HttpGetRequest qui permet de créer la requete HTTP avec l'url de l'API
        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            // résultat de la requete http
            String result = getRequest.execute(myURL).get();
            // tableau JSON qui contient le résultat
            JSONArray array = new JSONArray(result);
            // boucle sur la longueur du tableau JSON qui contient le résultat de la requete
            for (int i= 0; i < array.length(); i++) {
                // à chaque tour de boucle, on récupère un objet JSON du tableau, qui contient les données d'une note de frais, 1 note de frais = 1 objet
                JSONObject obj = new JSONObject(array.getString(i));
                String comment;
                String submissionDate;
                // si le champ expenseReportComment de la note de frais est vide
                if (obj.isNull("expenseReportComment")) {
                    comment = "";
                } else {
                    comment = obj.getString("expenseReportComment");
                }
                // si le champ submissionDate de la note de frais est vide
                if (obj.isNull("submissionDate")) {
                    submissionDate = "";
                } else {
                    submissionDate = obj.getString("submissionDate");
                }
                // calcul du montant total de la note de frais (addition des montants des dépenses)
                float erTotal = Float.parseFloat(obj.getString("totalTravel"))+Float.parseFloat(obj.getString("totalBusiness"));
                // on ajoute la note de frais à la liste
                erList.add(new ExpenseReport(obj.getString("expenseReportDate"), obj.getString("expenseReportCity"),comment, obj.getInt("expenseReportCode"), submissionDate, erTotal, obj.getInt("idUser"), obj.getInt("idCustomer")));
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        // génère les cellules de la ListView
        adapter = new ExpenseReportAdapter(ExpenseReportActivity.this, erList);
        // affecte les cellules à notre ListView
        liste.setAdapter(adapter);

        /** Gestion du clic sur une cellule de la ListView */
        liste.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) {

                // On ajoute les données de la note de frais à notre fichier sharedpreferences
                SharedPreferences sharedPreferencesER = getSharedPreferences(FILE_EXPENSE_REPORT, Context.MODE_PRIVATE);
                sharedPreferencesER.edit()
                        .putString(EXPENSE_REPORT_KEY, adapter.getItem(position).toJSON())
                        .apply();

                // lien vers la page Note de frais - Détails
                Intent intentNextPage = new Intent(ExpenseReportActivity.this, ERDetailsActivity.class);
                startActivity(intentNextPage);
            }
        });

        /** Gestion du clic sur le bouton Ajouter une nouvelle note de frais */
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lien vers la vue Ajouter une Note de Frais
                Intent intentNewER = new Intent(ExpenseReportActivity.this, NewERActivity.class);
                startActivity(intentNewER);
            }
        });

        /** Gestion du clic sur le bouton retour */
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /* Lien vers la vue Tableau de bord */
                Intent intentReturn = new Intent(ExpenseReportActivity.this, DashboardActivity.class);
                startActivity(intentReturn);
            }
        });
    }
}

