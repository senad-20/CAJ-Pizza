//package pizzas;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//public class Client implements InterClient{
//
//    private static final List<Client> clients = new ArrayList<>();
//
//    private final InformationPersonnelle info;
//
//    private final String email;
//
//    private String mdp;
//
//    private boolean connecte;
//
//    public Client(String email, String mdp, InformationPersonnelle info) {
//        this.email = email;
//        this.mdp = mdp;
//        this.info = info;
//        connecte = false;
//    }
//    public static List<Client> getClients() {
//        return List.copyOf(clients);
//    }
//
//    public InformationPersonnelle getInfo() {
//        return info;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getMdp() {
//        return mdp;
//    }
//
//    public boolean isConnecte() {
//        return connecte;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        return email.equals(((Client) obj).email);
//    }
//
//    @Override
//    public int inscription(String email, String mdp, InformationPersonnelle info) {
//        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
//        if(!email.matches(emailPattern)){
//            return -4;
//        }
//        String namePattern = "^[A-Za-zÀ-ÖØ-öø-ÿ\\-\\s']+$";
//        if(info == null
//                || info.getNom() == null
//                || info.getPrenom() == null
//                || info.getAdresse() == null
//                || info.getNom().trim().isEmpty()
//                || info.getPrenom().trim().isEmpty()
//                || info.getAdresse().trim().isEmpty()
//                || !info.getNom().matches(namePattern)
//                || !info.getPrenom().matches(namePattern)) {
//                        return -3;
//        }
//        if(email == null || mdp == null || email.trim().isEmpty() || mdp.trim().isEmpty()) {
//            return -2;
//        }
//        if (clients.contains(this)) {
//            return -1;
//        }
//        Client c = new Client(email, mdp, info);
//        clients.add(c);
//        return 0;
//    }
//
//    @Override
//    public boolean connexion(String email, String mdp) {
//        if(email !=null && mdp != null && !email.trim().isEmpty() && !mdp.trim().isEmpty()){
//            for(Client c : clients){
//                if(c.email.equals(email) && c.mdp.equals(mdp)){
//                    this.connecte = true;
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void deconnexion() throws NonConnecteException {
//        for(Client c : clients){
//                if (c.isConnecte()){
//                    c.connecte = false;
//                    return;
//                }
//        } throw new NonConnecteException();
//
//    }
//
//    @Override
//    public Commande debuterCommande() throws NonConnecteException {
//        for(Client c : clients){
//            if (c.isConnecte()){
//                List<Pizza> pizzas = new ArrayList<>();
//                return new Commande(pizzas, c.getInfo());
//            }
//        }
//        throw new NonConnecteException();
//    }
//
//    @Override
//    public void ajouterPizza(Pizza pizza, int nombre, Commande cmd) throws NonConnecteException, CommandeException {
//        for(Client c : clients){
//            if(c.isConnecte()){
//                for (int i = 0; i < nombre; i++){
//                    cmd.ajouterPizza(pizza);}
//                    return;
//            }
//        }
//        throw new NonConnecteException();
//    }
//
//    @Override
//    public void validerCommande(Commande cmd) throws NonConnecteException, CommandeException {
//        for(Client c : clients){
//            if(c.isConnecte()){
//                cmd.setStatut(StatutCommande.VALIDEE);
//                return;
//            }
//        }
//        throw new NonConnecteException();
//    }
//
//    @Override
//    public void annulerCommande(Commande cmd) throws NonConnecteException, CommandeException {
//        for(Client c : clients){
//            if(c.isConnecte()){
//                cmd.setStatut(StatutCommande.VALIDEE);
//                return;
//            }
//        }
//        throw new NonConnecteException();
//
//    }
//
//    @Override
//    public List<Commande> getCommandesEncours() throws NonConnecteException {
//        return List.of();
//    }
//
//    @Override
//    public List<Commande> getCommandePassees() throws NonConnecteException {
//        return List.of();
//    }
//
//    @Override
//    public Set<Pizza> getPizzas() {
//        return Set.of();
//    }
//
//    @Override
//    public void ajouterFiltre(TypePizza type) {
//
//    }
//
//    @Override
//    public void ajouterFiltre(String... ingredients) {
//
//    }
//
//    @Override
//    public void ajouterFiltre(double prixMaximum) {
//
//    }
//
//    @Override
//    public Set<Pizza> selectionPizzaFiltres() {
//        return Set.of();
//    }
//
//    @Override
//    public void supprimerFiltres() {
//
//    }
//
//    @Override
//    public Set<Evaluation> getEvaluationsPizza(Pizza pizza) {
//        return Set.of();
//    }
//
//    @Override
//    public double getNoteMoyenne(Pizza pizza) {
//        return 0;
//    }
//
//    @Override
//    public boolean ajouterEvaluation(Pizza pizza, int note, String commentaire) throws NonConnecteException, CommandeException {
//        return false;
//    }
//}
