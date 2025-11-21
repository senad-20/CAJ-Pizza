package pizzas;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe principale de gestion de la pizzeria.
 * <p>
 * Cette classe implémente à la fois les services destinés aux clients
 * ({@link InterClient}) et ceux destinés au pizzaïolo
 * ({@link InterPizzaiolo}).
 * Elle gère :
 * <ul>
 *     <li>les clients et leur authentification,</li>
 *     <li>les ingrédients disponibles,</li>
 *     <li>les pizzas en vente,</li>
 *     <li>les commandes des clients,</li>
 *     <li>les restrictions d'ingrédients par type de pizza,</li>
 *     <li>les évaluations de pizzas,</li>
 *     <li>les filtres de recherche de pizzas.</li>
 * </ul>
 */
public class Pizzaiolo implements InterPizzaiolo, InterClient {

    /**
     * Représente un compte client interne à la pizzeria.
     * Contient l'email, le mot de passe et les informations personnelles.
     */
    private static class Client {

        /** Informations personnelles du client. */
        private final InformationPersonnelle info;

        /** Adresse email du client (unique). */
        private final String email;

        /** Mot de passe du client. */
        private final String mdp;

        /**
         * Crée un nouveau client interne.
         *
         * @param email l'email du client
         * @param mdp   le mot de passe du client
         * @param info  les informations personnelles
         */
        Client(String email, String mdp, InformationPersonnelle info) {
            this.email = email;
            this.mdp = mdp;
            this.info = info;
        }

        /**
         * Retourne les informations personnelles du client.
         *
         * @return les informations personnelles
         */
        InformationPersonnelle getInfo() {
            return info;
        }

        /**
         * Retourne l'email du client.
         *
         * @return l'email
         */
        String getEmail() {
            return email;
        }

        /**
         * Retourne le mot de passe du client.
         *
         * @return le mot de passe
         */
        String getMdp() {
            return mdp;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Client)) {
                return false;
            }
            Client other = (Client) obj;
            return Objects.equals(email, other.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email);
        }
    }

    /** Client actuellement connecté (ou {@code null} si aucun). */
    private Client clientConnecte;

    /** Clients enregistrés, indexés par email. */
    private final Map<String, Client> clients = new HashMap<>();

    /** Ensemble des ingrédients connus, indexés par nom. */
    private final Map<String, Ingredient> ingredients = new HashMap<>();

    /** Ensemble des pizzas en vente. */
    private final Set<Pizza> pizzas = new HashSet<>();

    /** Liste de toutes les commandes. */
    private final List<Commande> commandes = new ArrayList<>();

    /**
     * Map des ingrédients interdits :
     * pour chaque ingrédient, l'ensemble des types de pizzas où il est interdit.
     */
    private final Map<Ingredient, Set<TypePizza>> ingredientsInterdits = new HashMap<>();

    /** Filtre de type de pizza (peut être {@code null} si non défini). */
    private TypePizza filtreType;

    /** Filtre sur les ingrédients (tous doivent être présents dans la pizza). */
    private final Set<Ingredient> filtresIngredients = new HashSet<>();

    /** Filtre sur le prix maximum (peut être {@code null} si non défini). */
    private Double filtrePrixMax;

    // -------------------------------------------------------------------------
    //  Méthodes utilitaires privées
    // -------------------------------------------------------------------------

    /**
     * Vérifie qu'un client est connecté, sinon lance une exception.
     *
     * @throws NonConnecteException si aucun client n'est connecté
     */
    private void verifierConnecte() throws NonConnecteException {
        if (clientConnecte == null) {
            throw new NonConnecteException();
        }
    }

    /**
     * Retourne l'ingrédient associé à un nom.
     *
     * @param nom le nom de l'ingrédient
     * @return l'ingrédient ou {@code null} s'il n'existe pas
     */
    private Ingredient getIngredientByName(String nom) {
        if (nom == null) {
            return null;
        }
        return ingredients.get(nom);
    }

    /**
     * Retourne la pizza associée à un nom.
     *
     * @param nom le nom de la pizza
     * @return la pizza ou {@code null} si elle n'existe pas
     */
    private Pizza getPizzaByName(String nom) {
        if (nom == null) {
            return null;
        }
        for (Pizza p : pizzas) {
            if (p.getNom().equals(nom)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Vérifie qu'une pizza est valide (non nulle et gérée par la pizzeria).
     *
     * @param pizza la pizza à vérifier
     * @return {@code true} si la pizza est valide, {@code false} sinon
     */
    private boolean pizzaValide(Pizza pizza) {
        return pizza != null && pizzas.contains(pizza);
    }

    /**
     * Vérifie qu'une commande est valide (non nulle et connue).
     *
     * @param commande la commande à vérifier
     * @return {@code true} si la commande est valide, {@code false} sinon
     */
    private boolean commandeValide(Commande commande) {
        return commande != null && commandes.contains(commande);
    }

    /**
     * Retourne l'ensemble des commandes déjà traitées (statut TRAITEE),
     * triées de la plus ancienne à la plus récente.
     *
     * @return la liste ordonnée des commandes traitées
     */
    private List<Commande> commandesTraitees() {
        return commandes.stream()
                .filter(c -> c.getStatut() == StatutCommande.TRAITEE)
                .sorted(Comparator.comparing(Commande::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Retourne le prix de vente d'une pizza (méthode interne).
     * <ul>
     *     <li>Si la pizza a un prix fixé, on le retourne.</li>
     *     <li>Sinon, on retourne le prix minimal.</li>
     * </ul>
     *
     * @param pizza la pizza concernée (supposée valide)
     * @return le prix de vente de la pizza
     */
    private double prixVentePizza(Pizza pizza) {
        double fixe = pizza.getPrixfixe();
        if (fixe >= 0) {
            return fixe;
        }
        return calculerPrixMinimalPizza(pizza);
    }

    // -------------------------------------------------------------------------
    //  Implémentation de InterClient
    // -------------------------------------------------------------------------

    @Override
    public int inscription(String email, String mdp, InformationPersonnelle info) {
        // -2 : email ou mdp vide
        if (email == null || mdp == null
                || email.trim().isEmpty()
                || mdp.trim().isEmpty()) {
            return -2;
        }

        // -3 : informations personnelles invalides
        if (info == null
                || info.getNom() == null || info.getNom().trim().isEmpty()
                || info.getPrenom() == null || info.getPrenom().trim().isEmpty()
                || info.getAdresse() == null || info.getAdresse().trim().isEmpty()) {
            return -3;
        }

        // -4 : email mal formé
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if (!email.matches(emailPattern)) {
            return -4;
        }

        // -1 : email déjà utilisé
        if (clients.containsKey(email)) {
            return -1;
        }

        // OK
        Client client = new Client(email, mdp, info);
        clients.put(email, client);
        return 0;
    }

    @Override
    public boolean connexion(String email, String mdp) {
        if (email == null || mdp == null
                || email.trim().isEmpty()
                || mdp.trim().isEmpty()) {
            return false;
        }
        Client c = clients.get(email);
        if (c != null && c.getMdp().equals(mdp)) {
            clientConnecte = c;
            return true;
        }
        return false;
    }

    @Override
    public void deconnexion() throws NonConnecteException {
        verifierConnecte();
        clientConnecte = null;
    }

    @Override
    public Commande debuterCommande() throws NonConnecteException {
        verifierConnecte();
        List<Pizza> liste = new ArrayList<>();
        Commande cmd = new Commande(liste, clientConnecte.getInfo());
        commandes.add(cmd);
        return cmd;
    }

    @Override
    public void ajouterPizza(Pizza pizza, int nombre, Commande cmd)
            throws NonConnecteException, CommandeException {
        verifierConnecte();
        if (!commandeValide(cmd)) {
            throw new CommandeException("Commande invalide.");
        }
        if (!cmd.getClient().equals(clientConnecte.getInfo())) {
            throw new CommandeException("La commande n'appartient pas au client connecté.");
        }
        if (cmd.getStatut() != StatutCommande.CREE) {
            throw new CommandeException("La commande n'est pas en cours de création.");
        }
        if (!pizzaValide(pizza)) {
            throw new CommandeException("Pizza invalide.");
        }
        if (nombre <= 0) {
            throw new CommandeException("Nombre de pizzas invalide.");
        }

        for (int i = 0; i < nombre; i++) {
            cmd.ajouterPizza(pizza);
        }
    }

    @Override
    public void validerCommande(Commande cmd)
            throws NonConnecteException, CommandeException {
        verifierConnecte();
        if (!commandeValide(cmd)) {
            throw new CommandeException("Commande invalide.");
        }
        if (!cmd.getClient().equals(clientConnecte.getInfo())) {
            throw new CommandeException("La commande n'appartient pas au client connecté.");
        }
        if (cmd.getStatut() != StatutCommande.CREE) {
            throw new CommandeException("La commande ne peut pas être validée.");
        }
        cmd.setStatut(StatutCommande.VALIDEE);
    }

    @Override
    public void annulerCommande(Commande cmd)
            throws NonConnecteException, CommandeException {
        verifierConnecte();
        if (!commandeValide(cmd)) {
            throw new CommandeException("Commande invalide.");
        }
        if (!cmd.getClient().equals(clientConnecte.getInfo())) {
            throw new CommandeException("La commande n'appartient pas au client connecté.");
        }
        if (cmd.getStatut() != StatutCommande.CREE) {
            throw new CommandeException("La commande ne peut pas être annulée.");
        }
        commandes.remove(cmd);
    }

    @Override
    public List<Commande> getCommandesEncours() throws NonConnecteException {
        verifierConnecte();
        return commandes.stream()
                .filter(c -> c.getClient().equals(clientConnecte.getInfo()))
                .filter(c -> c.getStatut() == StatutCommande.CREE)
                .sorted(Comparator.comparing(Commande::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Commande> getCommandePassees() throws NonConnecteException {
        verifierConnecte();
        return commandes.stream()
                .filter(c -> c.getClient().equals(clientConnecte.getInfo()))
                .filter(c -> c.getStatut() != StatutCommande.CREE)
                .sorted(Comparator.comparing(Commande::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Pizza> getPizzas() {
        return Collections.unmodifiableSet(pizzas);
    }

    @Override
    public void ajouterFiltre(TypePizza type) {
        filtreType = type;
    }

    @Override
    public void ajouterFiltre(String... nomsIngredients) {
        if (nomsIngredients == null) {
            return;
        }
        for (String nom : nomsIngredients) {
            if (nom == null || nom.trim().isEmpty()) {
                continue;
            }
            Ingredient ing = getIngredientByName(nom);
            if (ing != null) {
                filtresIngredients.add(ing);
            }
        }
    }

    @Override
    public void ajouterFiltre(double prixMaximum) {
        if (prixMaximum > 0) {
            filtrePrixMax = prixMaximum;
        }
    }

    @Override
    public Set<Pizza> selectionPizzaFiltres() {
        return pizzas.stream()
                .filter(p -> filtreType == null || p.getTypePizza() == filtreType)
                .filter(p -> filtresIngredients.isEmpty()
                        || p.getIngredients().containsAll(filtresIngredients))
                .filter(p -> filtrePrixMax == null
                        || prixVentePizza(p) <= filtrePrixMax)
                .collect(Collectors.toSet());
    }

    @Override
    public void supprimerFiltres() {
        filtreType = null;
        filtresIngredients.clear();
        filtrePrixMax = null;
    }

    @Override
    public Set<Evaluation> getEvaluationsPizza(Pizza pizza) {
        if (!pizzaValide(pizza)) {
            return null;
        }
        return new HashSet<>(pizza.getEvaluations());
    }

    @Override
    public double getNoteMoyenne(Pizza pizza) {
        if (!pizzaValide(pizza)) {
            return -2;
        }
        List<Evaluation> evals = pizza.getEvaluations().stream().toList();
        if (evals.isEmpty()) {
            return -1;
        }
        return evals.stream()
                .mapToDouble(Evaluation::getNote)
                .average()
                .orElse(-1);
    }

    @Override
    public boolean ajouterEvaluation(Pizza pizza, int note, String commentaire)
            throws NonConnecteException, CommandeException {
        verifierConnecte();
        if (!pizzaValide(pizza)) {
            return false;
        }
        if (note < 0 || note > 5) {
            return false;
        }

        InformationPersonnelle info = clientConnecte.getInfo();

        // Le client doit avoir commandé la pizza dans une commande VALIDEE
        boolean aDejaCommandePizza = commandes.stream()
                .filter(c -> c.getClient().equals(info))
                .filter(c -> c.getStatut() != StatutCommande.CREE)
                .anyMatch(c -> c.getPizzas().contains(pizza));

        if (!aDejaCommandePizza) {
            throw new CommandeException("Le client n'a jamais commandé cette pizza.");
        }

        // Vérifier si déjà évaluée par ce client
        boolean dejaEvaluee = pizza.getEvaluations().stream()
                .anyMatch(e -> e.getAuteur().equals(info));

        if (dejaEvaluee) {
            return false;
        }

        // Créer l'évaluation
        Evaluation evaluation = new Evaluation(info,note,commentaire);
        pizza.ajouternote(evaluation);
        return true;
    }

    // -------------------------------------------------------------------------
    //  Implémentation de InterPizzaiolo - gestion des ingrédients et pizzas
    // -------------------------------------------------------------------------

    @Override
    public int creerIngredient(String nom, double prix) {
        // -1 : nom invalide
        if (nom == null || nom.trim().isEmpty()) {
            return -1;
        }
        // -3 : prix invalide (<= 0)
        if (prix <= 0) {
            return -3;
        }
        // -2 : déjà existant
        if (ingredients.containsKey(nom)) {
            return -2;
        }
        Ingredient ing = new Ingredient(nom, prix);
        ingredients.put(nom, ing);
        return 0;
    }

    @Override
    public int changerPrixIngredient(String nom, double prix) {
        // -1 : nom invalide
        if (nom == null || nom.trim().isEmpty()) {
            return -1;
        }
        // -2 : prix invalide
        if (prix <= 0) {
            return -2;
        }
        Ingredient ing = getIngredientByName(nom);
        // -3 : ingrédient inexistant
        if (ing == null) {
            return -3;
        }
        ing.setPrix(prix);
        return 0;
    }

    @Override
    public boolean interdireIngredient(String nomIngredient, TypePizza type) {
        if (nomIngredient == null || nomIngredient.trim().isEmpty() || type == null) {
            return false;
        }
        Ingredient ing = getIngredientByName(nomIngredient);
        if (ing == null) {
            return false;
        }
        return ingredientsInterdits
                .computeIfAbsent(ing, k -> new HashSet<>())
                .add(type);
    }

    @Override
    public Pizza creerPizza(String nom, TypePizza type) {
        if (nom == null || nom.trim().isEmpty() || type == null) {
            return null;
        }
        if (getPizzaByName(nom) != null) {
            return null;
        }
        Pizza p = new Pizza(nom, type);
        pizzas.add(p);
        return p;
    }

    @Override
    public int ajouterIngredientPizza(Pizza pizza, String nomIngredient) {
        // -1 : pizza invalide
        if (!pizzaValide(pizza)) {
            return -1;
        }
        // -2 : ingrédient invalide
        if (nomIngredient == null || nomIngredient.trim().isEmpty()) {
            return -2;
        }
        Ingredient ing = getIngredientByName(nomIngredient);
        if (ing == null) {
            return -2;
        }
        // -3 : ingrédient interdit pour ce type de pizza
        if (ingredientsInterdits.containsKey(ing)
                && ingredientsInterdits.get(ing).contains(pizza.getTypePizza())) {
            return -3;
        }
        // Si déjà présent, on ne fait rien mais c'est un succès
        if (pizza.getIngredients().contains(ing)) {
            return 0;
        }
        pizza.ajouterIngredient(ing);
        return 0;
    }

    @Override
    public int retirerIngredientPizza(Pizza pizza, String nomIngredient) {
        // -1 : pizza invalide
        if (!pizzaValide(pizza)) {
            return -1;
        }
        // -2 : ingrédient invalide
        if (nomIngredient == null || nomIngredient.trim().isEmpty()) {
            return -2;
        }
        Ingredient ing = getIngredientByName(nomIngredient);
        if (ing == null) {
            return -2;
        }
        // -3 : l'ingrédient n'existait pas dans la pizza
        if (!pizza.getIngredients().contains(ing)) {
            return -3;
        }
        pizza.enleverIngredient(ing);
        return 0;
    }

    @Override
    public Set<String> verifierIngredientsPizza(Pizza pizza) {
        if (!pizzaValide(pizza)) {
            return null;
        }
        Set<String> interdits = new HashSet<>();
        TypePizza type = pizza.getTypePizza();
        for (Ingredient ing : pizza.getIngredients()) {
            if (ingredientsInterdits.containsKey(ing)
                    && ingredientsInterdits.get(ing).contains(type)) {
                interdits.add(ing.getNom());
            }
        }
        return interdits;
    }

    @Override
    public boolean ajouterPhoto(Pizza pizza, String file) throws IOException {
        if (!pizzaValide(pizza)) {
            return false;
        }
        if (file == null || file.trim().isEmpty()) {
            return false;
        }

        File f = new File(file);
        if (!f.exists() || !f.isFile()) {
            return false;
        }

        String lower = file.toLowerCase(Locale.ROOT);
        if (!(lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".gif"))) {
            return false;
        }

        pizza.setPhoto(file);
        return true;
    }

    @Override
    public double getPrixPizza(Pizza pizza) {
        if (!pizzaValide(pizza)) {
            return -1;
        }
        return prixVentePizza(pizza);
    }

    @Override
    public boolean setPrixPizza(Pizza pizza, double prix) {
        if (!pizzaValide(pizza)) {
            return false;
        }
        double minimal = calculerPrixMinimalPizza(pizza);
        if (prix < minimal) {
            return false;
        }
        pizza.setPrixfixe(prix);
        return true;
    }

    @Override
    public double calculerPrixMinimalPizza(Pizza pizza) {
        if (!pizzaValide(pizza)) {
            return -1;
        }
        double sum = pizza.getIngredients().stream()
                .mapToDouble(Ingredient::getPrix)
                .sum();
        double prix = sum * 1.4;
        // Arrondi au dixième supérieur
        return Math.ceil(prix * 10.0) / 10.0;
    }

    @Override
    public Set<InformationPersonnelle> ensembleClients() {
        return clients.values().stream()
                .map(Client::getInfo)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Commande> commandesDejaTraitees() {
        return commandesTraitees();
    }

    @Override
    public List<Commande> commandeNonTraitees() {
        // Commandes non traitées = VALIDEE mais pas encore lues par le pizzaïolo.
        List<Commande> aTraiter = commandes.stream()
                .filter(c -> c.getStatut() == StatutCommande.VALIDEE)
                .sorted(Comparator.comparing(Commande::getDate))
                .collect(Collectors.toList());

        // Une fois lues, elles deviennent TRAITEES.
        for (Commande c : aTraiter) {
            c.setStatut(StatutCommande.TRAITEE);
        }

        return aTraiter;
    }

    @Override
    public List<Commande> commandesTraiteesClient(InformationPersonnelle client) {
        if (client == null) {
            return null;
        }
        return commandesTraitees().stream()
                .filter(c -> c.getClient().equals(client))
                .collect(Collectors.toList());
    }

    @Override
    public Map<Pizza, Double> beneficeParPizza() {
        Map<Pizza, Double> resultat = new HashMap<>();
        for (Pizza p : pizzas) {
            double benef = prixVentePizza(p) - calculerPrixMinimalPizza(p);
            if (benef < 0) {
                benef = 0;
            }
            resultat.put(p, benef);
        }
        return resultat;
    }

    @Override
    public double beneficeCommandes(Commande commande) {
        if (!commandeValide(commande)) {
            return -1;
        }
        return commande.getPizzas().stream()
                .mapToDouble(p -> {
                    double benef = prixVentePizza(p) - calculerPrixMinimalPizza(p);
                    return Math.max(benef, 0);
                })
                .sum();
    }

    @Override
    public double beneficeToutesCommandes() {
        return commandesTraitees().stream()
                .mapToDouble(this::beneficeCommandes)
                .sum();
    }

    @Override
    public Map<InformationPersonnelle, Integer> nombrePizzasCommandeesParClient() {
        Map<InformationPersonnelle, Integer> resultat = new HashMap<>();

        // Initialiser les compteurs
        for (Client c : clients.values()) {
            resultat.put(c.getInfo(), 0);
        }

        // Compter sur les commandes TRAITEES
        for (Commande c : commandes) {
            if (c.getStatut() == StatutCommande.TRAITEE) {
                InformationPersonnelle info = c.getClient();
                int count = resultat.getOrDefault(info, 0);
                count += c.getPizzas().size();
                resultat.put(info, count);
            }
        }

        return resultat;
    }

    @Override
    public Map<InformationPersonnelle, Double> beneficeParClient() {
        Map<InformationPersonnelle, Double> resultat = new HashMap<>();
        for (Client c : clients.values()) {
            InformationPersonnelle info = c.getInfo();
            double total = commandesTraitees().stream()
                    .filter(cmd -> cmd.getClient().equals(info))
                    .mapToDouble(this::beneficeCommandes)
                    .sum();
            resultat.put(info, total);
        }
        return resultat;
    }

    @Override
    public int nombrePizzasCommandees(Pizza pizza) {
        if (!pizzaValide(pizza)) {
            return -1;
        }
        return commandes.stream()
                .filter(c -> c.getStatut() == StatutCommande.TRAITEE)
                .mapToInt(c -> Collections.frequency(c.getPizzas(), pizza))
                .sum();
    }

    @Override
    public List<Pizza> classementPizzasParNombreCommandes() {
        return pizzas.stream()
                .sorted(Comparator.comparingInt(this::nombrePizzasCommandees).reversed())
                .collect(Collectors.toList());
    }
}
