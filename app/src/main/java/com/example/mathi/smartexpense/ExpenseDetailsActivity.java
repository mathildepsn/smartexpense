package com.example.mathi.smartexpense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mathi.smartexpense.model.Expense;
import com.example.mathi.smartexpense.network.HttpGetRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ExpenseDetailsActivity extends AppCompatActivity {

    final String EXPENSE_LABEL = "expense_label";
    final String EXPENSE_ID = "expense_id";
    final String REFUND_TRACKER = "refund_tracker";
    final String FILE_EXPENSE_REPORT = "file_expense_report";
    private Button buttonDetails;
    SharedPreferences sharedPreferencesER;
    private String expLabel;
    private int expId;
    private Boolean refundTracker; // indique si on vient de la page Suivi des remboursements ou Note de frais Détails, true pour Suivi des rb, false pour Note de frais

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

// on récupère les données de notre fichier SharedPreferences
        sharedPreferencesER = this.getSharedPreferences(FILE_EXPENSE_REPORT, MODE_PRIVATE);
        if (sharedPreferencesER.contains(EXPENSE_LABEL) && sharedPreferencesER.contains(EXPENSE_ID) && sharedPreferencesER.contains(REFUND_TRACKER)) {
            expLabel = sharedPreferencesER.getString(EXPENSE_LABEL, null);
            expId = sharedPreferencesER.getInt(EXPENSE_ID, 0);
            refundTracker = sharedPreferencesER.getBoolean(REFUND_TRACKER, false);
        }

/* Gestion du clic sur le bouton Retour */
        Button buttonReturn = (Button) findViewById(R.id.returnButtonExpenseDetails);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // si on vient de la page Note de frais - détails
            if (refundTracker.equals(false)) {
                /* Lien vers la vue Note de frais - détails */
                Intent intentReturn = new Intent(ExpenseDetailsActivity.this, ERDetailsActivity.class);
                startActivity(intentReturn);
            // si on vient de la page Suivi des remboursements
            } else {
                /* Lien vers la vue Suivi des remboursements */
                Intent intentReturn = new Intent(ExpenseDetailsActivity.this, RefundTrackerActivity.class);
                startActivity(intentReturn);
            }
            }
        });

/* Gestion du clic sur le bouton Détails */
        buttonDetails = (Button) findViewById(R.id.detailsButton);
        buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Lien vers la vue Dépense - Détails */
                Intent intentDetails = new Intent(ExpenseDetailsActivity.this, ExpenseMoreDetailsActivity.class);
                startActivity(intentDetails);
            }
        });

/* Déclaration des TextView de la vue */
        TextView label = (TextView) findViewById(R.id.expenseCategory);
        label.setText(expLabel);
        TextView date = (TextView) findViewById(R.id.expenseDate);
        TextView status = (TextView) findViewById(R.id.expenseStatus);
        TextView validationDate = (TextView) findViewById(R.id.expenseValidationDate);
        TextView totalAmount = (TextView) findViewById(R.id.expenseTotalAmount);
        TextView refundAmount = (TextView) findViewById(R.id.expenseRefundAmount);
        TextView paymentDate = (TextView) findViewById(R.id.expensePaymentDate);

/* Récupération des données d'une dépense et injection dans les TextView de la vue */
        /* Si c'est un trajet */
        if (expLabel.equals("Trajet")) {
            String myURL = "http://www.gyejacquot-pierre.fr/API/public/travel?idExpenseT="+expId;
            //String myURL = "http://10.0.2.2/smartExpenseApi/API/public/travel?idExpenseT="+expId;

            HttpGetRequest getRequest = new HttpGetRequest();
            try {
                String result = getRequest.execute(myURL).get();
                System.out.println("Retour HTTPGetRequest : " + result);
                if (!result.isEmpty()) {
                    JSONArray array = new JSONArray(result);
                    JSONObject obj = new JSONObject(array.getString(0));
                    date.setText(obj.getString("departureDate"));
                    totalAmount.setText(String.valueOf(obj.getInt("expenseTotalT")) + "€");
                    if (obj.isNull("validationState")) {
                        status.setText("Non soumise");
                    } else {
                        status.setText(obj.getString("validationState"));
                    }
                    if (obj.isNull("dateValidation")) {
                        validationDate.setText("");
                    } else {
                        validationDate.setText(obj.getString("dateValidation"));
                    }
                    if (obj.isNull("paymentDateT")) {
                        paymentDate.setText("");
                    } else {
                        paymentDate.setText(obj.getString("paymentDateT"));
                    }
                    if (obj.isNull("refundAmountT")) {
                        refundAmount.setText("");
                    } else {
                        refundAmount.setText(String.valueOf(obj.getInt("refundAmountT")) + "€");
                    }
                // si le résultat est vide, l'appli ne crash pas, les champs sont vides
                } else {
                    date.setText("");
                    totalAmount.setText("");
                    status.setText("");
                    paymentDate.setText("");
                    refundAmount.setText("");
                    validationDate.setText("");
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        /* Si c'est un autre frais */
        } else {
            /* Disparition du bouton Détails (qui mène vers les infos d'un trajet) */
            buttonDetails.setVisibility(View.GONE);
            String myURL = "http://www.gyejacquot-pierre.fr/API/public/businessexpense?idExpenseB="+expId;
            //String myURL = "http://10.0.2.2/smartExpenseApi/API/public/businessexpense?idExpenseB="+expId;

            HttpGetRequest getRequest = new HttpGetRequest();
            try {
                String result = getRequest.execute(myURL).get();
                System.out.println("Retour HTTPGetRequest : " + result);
                if (!result.isEmpty()) {
                    JSONArray array = new JSONArray(result);
                    JSONObject obj = new JSONObject(array.getString(0));
                    date.setText(obj.getString("businessExpenseDate"));
                    totalAmount.setText(String.valueOf(obj.getInt("expenseTotalB")) + "€");
                    if (obj.isNull("validationState")) {
                        status.setText("Non soumise");
                    } else {
                        status.setText(obj.getString("validationState"));
                    }
                    if (obj.isNull("dateValidation")) {
                        validationDate.setText("");
                    } else {
                        validationDate.setText(String.valueOf(setDateFormat(obj.getString("dateValidation"))));
                    }
                    if (obj.isNull("paymentDateB")) {
                        paymentDate.setText("");
                    } else {
                        paymentDate.setText(String.valueOf(setDateFormat(obj.getString("paymentDateB"))));
                    }
                    if (obj.isNull("refundAmountB")) {
                        refundAmount.setText("");
                    } else {
                        refundAmount.setText(String.valueOf(obj.getInt("refundAmountB")) + "€");
                    }
                // si le résultat est vide, l'appli ne crash pas, les champs sont vides
                } else {
                    date.setText("");
                    totalAmount.setText("");
                    status.setText("");
                    paymentDate.setText("");
                    refundAmount.setText("");
                    validationDate.setText("");
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public String setDateFormat(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = format.parse(date);

        format = new SimpleDateFormat("dd/MM/yyyy");
        date = format.format(newDate);
        return date;
    }
}
