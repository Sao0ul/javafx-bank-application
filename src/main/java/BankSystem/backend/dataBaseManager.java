package BankSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class dataBaseManager {

    static private Connection conn;

    static public void initialiseDataBase() {
        conn = apiDataBase.connect();
        if (conn != null) {
            // faire tes requêtes
        } else {
            System.out.println("Impossible de se connecter");
        }

    }

    static public Account findAccount(String nameAccount, String passwordAccount) {

        // Prépare la requête SQL avec des paramètres (évite l'injection SQL)
        String request = "SELECT * FROM accounts WHERE name=? AND password=?";

        // Crée un PreparedStatement pour exécuter la requête
        try (PreparedStatement ps = conn.prepareStatement(request)) {

            // Définit le premier paramètre (nom du compte) la methode va remplacer le ?
            // numero 1 par la valeur de nameAccount
            ps.setString(1, nameAccount);
            // Définit le deuxième paramètre (mot de passe)
            ps.setString(2, passwordAccount);

            // Exécute la requête et récupère les résultats
            ResultSet rs = ps.executeQuery();

            // Vérifie s'il y a un résultat (compte trouvé)
            if (rs.next()) {
                // La méthode rs.getString("id") récupère la valeur de la colonne "id" du
                // résultat
                // Elle attend en argument le nom de la colonne à lire (ici "id")
                String id = rs.getString("id");
                // récupère la valeur de la colonne "name"
                String name = rs.getString("name");
                // récupère la valeur entière de la colonne "balance"
                int balance = rs.getInt("balance");
                // Crée et retourne un nouvel objet Account avec les données récupérées
                return new Account(id, name, balance);
            }
        } catch (SQLException e) {
            // Gère les erreurs SQL
            System.out.println("Erreur lors de la recherche du compte : " + e.getMessage());
        }
        // Retourne null si aucun compte n'est trouvé ou en cas d'erreur
        return null;
    }

    static public void deposit(String nameAccount, String passwordAccount, int montant) {
        // code pour deposer de l'argent
        String request = "UPDATE accounts SET balance=balance +? WHERE name=? AND password=?";

        try (PreparedStatement ps = conn.prepareStatement(request)) {

            ps.setInt(1, montant);
            ps.setString(2, nameAccount);
            ps.setString(3, passwordAccount);

            int rowsUpdated = ps.executeUpdate(); // met à jour la base, retourne le nombre de lignes modifiées
            // ResultSet rs = ps.executeQuery(); // exécute la requête et récupère les
            // résultats ici on veut les modifier putot
            if (rowsUpdated == 1) {
                managerView.showView(new noErrorView());
            } else {
                managerView.showErrorpuppup("many account found that's should be impossible");
            }
        } catch (SQLException e) {
            // Gère les erreurs SQL
            managerView.showErrorpuppup("Erreur lors de la recherche du compte ou base de donne indisponible");
        }
    }

    static public void debit(String nameAccount, String passwordAccount, int montant) {
        // code pour retirer de l'argent
        String request = "UPDATE accounts SET balance=balance -? WHERE name=? AND password=?";

        try (PreparedStatement ps = conn.prepareStatement(request)) {

            int soldeSource = getSolde(nameAccount, passwordAccount);
            if (soldeSource < montant) {
                managerView.showErrorpuppup("Solde insuffisant sur le compte.");
                return;
            }

            ps.setInt(1, montant);
            ps.setString(2, nameAccount);
            ps.setString(3, passwordAccount);
            int rowsUpdated = ps.executeUpdate(); // met à jour la base, retourne le nombre de lignes modifiées

            if (rowsUpdated == 1) {
                managerView.showView(new noErrorView());
            } else {
                managerView.showErrorpuppup("Password incorrect for this account");
                System.out.println("nombre de ligne mise a jour: " + rowsUpdated);
            }
        } catch (SQLException e) {
            // Gère les erreurs SQL
            managerView.showErrorpuppup("Erreur lors de la recherche du compte ou base de donne indisponible");
            System.err.println(e.getMessage());
        }

    }

    static public int getSolde(String nameAccount, String passwordAccount) {
        // code pour consulter le solde
        String request = "SELECT balance FROM accounts WHERE name=? AND password=?";

        try (PreparedStatement ps = conn.prepareStatement(request)) {
            ps.setString(1, nameAccount);
            ps.setString(2, passwordAccount);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int balance = rs.getInt("balance");
                return balance;
            } else {
                managerView.showErrorpuppup("Password incorrect for this account");
            }
        } catch (SQLException e) {
            // Gère les erreurs SQL
            managerView.showErrorpuppup("Erreur lors de la recherche du compte ou base de donne indisponible");
        }

        return -1;
    }

    public static void transferTo(String nameAccount1, String passwordAccount1,
            String nameAccount2, String passwordAccount2,
            int montant) {
        String sqlDebit = "UPDATE accounts SET balance = balance - ? WHERE name=? AND password=?";
        String sqlCredit = "UPDATE accounts SET balance = balance + ? WHERE name=? AND password=?";

        try {
            Connection conn = apiDataBase.connect();
            conn.setAutoCommit(false); // démarrer la transaction

            // Débiter le compte source
            try (PreparedStatement ps1 = conn.prepareStatement(sqlDebit)) {
                ps1.setInt(1, montant);
                ps1.setString(2, nameAccount1);
                ps1.setString(3, passwordAccount1);
                int rows1 = ps1.executeUpdate();
                if (rows1 != 1) {
                    conn.rollback();
                    managerView.showErrorpuppup("Compte source introuvable ou mot de passe incorrect.");
                    return;
                }
            }

            // Créditer le compte destination
            try (PreparedStatement ps2 = conn.prepareStatement(sqlCredit)) {
                ps2.setInt(1, montant);
                ps2.setString(2, nameAccount2);
                ps2.setString(3, passwordAccount2);
                int rows2 = ps2.executeUpdate();
                if (rows2 != 1) {
                    conn.rollback();
                    managerView.showErrorpuppup("Compte destinataire introuvable ou mot de passe incorrect.");
                    return;
                }
            }

            conn.commit(); // si tout a marché, on valide
            managerView.showView(new noErrorView());

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                apiDataBase.connect().rollback(); // annuler si erreur
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            managerView.showErrorpuppup("Erreur lors du virement.");
        } finally {
            try {
                apiDataBase.connect().setAutoCommit(true); // on remet à la normale
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }

}

/*
 * notes:
 * 1. Use PreparedStatement to prevent SQL injection.
 * 2. Always close your database resources (Connection, PreparedStatement,
 * ResultSet) to avoid memory leaks.
 * | Objet | Rôle |
 * | ------------------- |
 * --------------------------------------------------------------- |
 * | `Connection` | Représente la **liaison** entre ton programme Java et MySQL.
 * |
 * | `PreparedStatement` | Représente une **requête SQL** précompilée avec des
 * paramètres. |
 * | `ResultSet` | Contient les **résultats** d’une requête SELECT. |
 * 
 */
