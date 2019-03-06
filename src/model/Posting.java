package model;

/**
 *
 * @author admin
 */
public class Posting implements Comparable<Posting>{
    private String term;
    private Document document;
    private int numberOfTerm = 1;
    private double weight=0.0;
    
    public Posting(Document document) {
        this.document = document;
    }

    public Posting(String term, Document document) {
        this.term = term;
        this.document = document;
    }

    Posting() {
    }
    

    public Document getDocument() {
        return document;
    }
 void setDocument(Document document) {
        this.document = document;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @Override
    public int compareTo(Posting posting) {
        return term.compareToIgnoreCase(posting.getTerm());
    }

   
    public int getNumberOfTerm() {
        return numberOfTerm;
    }
    
    public void setNumberOfTerm(int numberOfTerm) {
        this.numberOfTerm = numberOfTerm;
    }
    
    public double getWeight() {
        return weight;
}
    public void setWeight(double weight) {
        this.weight = weight;
}
}