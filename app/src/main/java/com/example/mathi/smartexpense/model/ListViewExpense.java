package com.example.mathi.smartexpense.model;

import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Pierre Gyejacquot, Ahmed Hamad and Mathilde Person.
 */

/** rôle de mini controlleur, associé à chaque cellule, et qui va recevoir les données de chaque dépense
Ce contrôleur va ensuite être stocké en tant que propriété de la vue (plus précisément dans l’attribut tag)
afin de pouvoir garder toujours le même principe de recyclage, une vue n’a qu’un seul ViewHolder, et inversement. */

public class ListViewExpense {
    public TextView dateExpense;
    public TextView categoryExpense;
    public TextView commentExpense;
    public TextView amountExpense;
    public Button deleteE;
}
