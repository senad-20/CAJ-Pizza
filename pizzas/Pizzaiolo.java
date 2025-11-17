package pizzas;

import java.util.*;
import java.util.stream.Collectors;

public class Pizzaiolo implements InterPizzaiolo, InterClient{
    private class Client {

        private final InformationPersonnelle info;

        private final String email;

        private String mdp;


        public Client(String email, String mdp, InformationPersonnelle info) {
            this.email = email;
            this.mdp = mdp;
            this.info = info;
        }
        public InformationPersonnelle getInfo() {
            return info;
        }

        public String getEmail() {
            return email;
        }

        public String getMdp() {
            return mdp;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            return this.email.equals(((Client) obj).email);
        }

        @Override
        public int hashCode() {
            return email.hashCode();
        }

    }

    private Client clientConnecte = null;
    private final List<Client> clients = new ArrayList<>();

    private final List<Commande> commandes = new ArrayList<>();

    private final Set<Pizza> pizzas = new HashSet<>();


    private final Set<Object> filtres = new HashSet<>();
    
    private final Set<Ingredient> ingredients = new HashSet<>();

    public int inscription(String email, String mdp, InformationPersonnelle info) {
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if(!email.matches(emailPattern)){
            return -4;
        }
        String namePattern = "^[A-Za-zÀ-ÖØ-öø-ÿ\\-\\s']+$";
        if(info == null
                || info.getNom() == null
                || info.getPrenom() == null
                || info.getAdresse() == null
                || info.getNom().trim().isEmpty()
                || info.getPrenom().trim().isEmpty()
                || info.getAdresse().trim().isEmpty()
                || !info.getNom().matches(namePattern)
                || !info.getPrenom().matches(namePattern)) {
            return -3;
        }
        if(email == null || mdp == null || email.trim().isEmpty() || mdp.trim().isEmpty()) {
            return -2;
        }
        if (clients.stream().anyMatch(c -> c.email.equals(email))) {
            return -1;
        }
        Client c = new Client(email, mdp, info);
        clients.add(c);
        return 0;
    }

    @Override
    public boolean connexion(String email, String mdp) {
        if(email !=null && mdp != null && !email.trim().isEmpty() && !mdp.trim().isEmpty()){
            for(Client c : clients){
                if(c.email.equals(email) && c.mdp.equals(mdp)){
                    this.clientConnecte = c;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void deconnexion() throws NonConnecteException {
        if (clientConnecte == null) {
            throw new NonConnecteException();
        }
        clientConnecte = null;

    }

    @Override
    public Commande debuterCommande() throws NonConnecteException {
            if (clientConnecte != null){
                List<Pizza> pizzas = new ArrayList<>();
                Commande cmd = new Commande(pizzas, clientConnecte.getInfo());
                commandes.add(cmd);
                return cmd;
            }
        throw new NonConnecteException();
    }

    @Override
    public void ajouterPizza(Pizza pizza, int nombre, Commande cmd) throws NonConnecteException, CommandeException {
        if (clientConnecte == null){
            throw new NonConnecteException();
            }
        if (!commandes.contains(cmd)){
            throw new CommandeException("La commande n'existe pas");
        }

        for (int i = 0; i < nombre; i++){
            cmd.ajouterPizza(pizza);}

    }

    @Override
    public void validerCommande(Commande cmd) throws NonConnecteException, CommandeException {
        if (clientConnecte == null){
            throw new NonConnecteException();
        }
        if (!commandes.contains(cmd)){
            throw new CommandeException("La commande n'existe pas");
        }
        cmd.setStatut(StatutCommande.VALIDEE);
    }

    @Override
    public void annulerCommande(Commande cmd) throws NonConnecteException, CommandeException {
        if (clientConnecte == null){
            throw new NonConnecteException();
        }
        if (!commandes.contains(cmd)){
            throw new CommandeException("La commande n'existe pas");
        }
        if (!((cmd.getStatut().equals(StatutCommande.CREE)))){
            throw new CommandeException("Cette commande ne peut pas être annulée");
        }
        commandes.remove(cmd);

    }

    @Override
    public List<Commande> getCommandesEncours() throws NonConnecteException {
        if (clientConnecte == null){
            throw new NonConnecteException();
        }
        return commandes.stream()
                .filter(c -> c.getClient().equals(clientConnecte.getInfo()))
                .filter(c -> c.getStatut() == StatutCommande.CREE)
                .sorted(Comparator.comparing(Commande::getDate))
                .toList();
    }

    @Override
    public List<Commande> getCommandePassees() throws NonConnecteException {
        if (clientConnecte == null){
            throw new NonConnecteException();
        }
        return commandes.stream()
                .filter(c -> c.getClient().equals(clientConnecte.getInfo()))
                .sorted(Comparator.comparing(Commande::getDate))
                .toList();
    }

    @Override
    public Set<Pizza> getPizzas() {
        return pizzas;
    }

    @Override
    public void ajouterFiltre(TypePizza type) {
        filtres.add(type);

    }

    @Override
    public void ajouterFiltre(String... ing) {
        if (ing == null) {
            return;
        }

        for (String nom : ing) {
            if (nom == null || nom.isBlank()) {
                continue; 
            }
            ingredients.stream().
                    filter(ingr -> ingr.getNom().equals(nom)).findFirst().ifPresent(filtres::add);
        }
    }

    @Override
    public void ajouterFiltre(double prixMaximum) {
        if (prixMaximum > 0) {filtres.add(prixMaximum);}
    }

    @Override
    public Set<Pizza> selectionPizzaFiltres() {
        return pizzas.stream()
                .filter(p -> filtres.stream()
                        .allMatch(f -> {
                            if (f instanceof TypePizza type) {
                                return p.getTypePizza() == type;
                            } else if (f instanceof Ingredient ingr) {
                                return p.getIngredients().contains(ingr);
                            } else if (f instanceof Double prixMax) {
                                return p.getPrixfixe() <= prixMax;
                            }
                            return true;
                        }))
                .collect(Collectors.toSet());
    }

    @Override
    public void supprimerFiltres() {
        filtres.clear();
    }

    @Override
    public Set<Evaluation> getEvaluationsPizza(Pizza pizza) {
        if (pizza == null || !pizzas.contains(pizza)) {return null;}
        return pizza.getEvaluations();
    }

    @Override
    public double getNoteMoyenne(Pizza pizza) {
        if (pizza == null || !pizzas.contains(pizza)) {return -2;}
        return getEvaluationsPizza(pizza).stream()
                .mapToDouble(Evaluation::getNote)
                .average()
                .orElse(-1);
    }

    @Override
    public boolean ajouterEvaluation(Pizza pizza, int note, String commentaire)
        throws NonConnecteException, CommandeException {

        // 1. Vérifier que quelqu’un est connecté
        if (clientConnecte==null) {
            throw new NonConnecteException();
        }

        // 2. Vérifier que la pizza est valide
        if (pizza == null) {
            return false;
        }

        // 3. Vérifier que la note est dans [0, 5]
        if (note < 0 || note > 5) {
            return false;
        }

        // 4. Vérifier que le client connecté a déjà commandé cette pizza
        //    dans une commande VALIDEE
        boolean aDejaCommandeCettePizza = commandes.stream()
                .filter(c -> c.getClient().equals(info)) // commandes de ce client
                .filter(c -> c.getStatut() == StatutCommande.VALIDEE)
                .anyMatch(c -> c.getPizzas().contains(pizza));

        if (!aDejaCommandeCettePizza) {
            // Spécification: CommandeException si le client n'avait jamais commandé cette pizza
            throw new CommandeException("Le client n'a jamais commandé cette pizza.");
        }

        // 5. Vérifier que le client n’a pas déjà évalué cette pizza
        boolean dejaEvaluee = pizza.getEvaluations().stream()
                // À adapter selon votre classe Evaluation :
                // soit e.getClient().equals(info),
                // soit e.equals(new Evaluation(info, pizza, note, commentaire)),
                // soit un autre critère.
                .anyMatch(e -> e.getClient().equals(info));

        if (dejaEvaluee) {
            // Évaluation déjà faite → ignorée
            return false;
        }

        // 6. Créer et ajouter l’évaluation
        Evaluation evaluation = new Evaluation(info, pizza, note, commentaire);
        pizza.ajouternote(evaluation);

        return true;`
    }
}
