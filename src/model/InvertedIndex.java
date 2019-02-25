/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 *
 * @author admin
 */
public class InvertedIndex {
    private ArrayList<Document> listOfDocument = new ArrayList<Document>();
    private ArrayList<Term> dictionary = new ArrayList<Term>();

    public ArrayList<Document> getListOfDocument() {
        return listOfDocument;
    }

    public void setListOfDocument(ArrayList<Document> listOfDocument) {
        this.listOfDocument = listOfDocument;
    }

    public ArrayList<Term> getDictionary() {
        return dictionary;
    }

    public void setDictionary(ArrayList<Term> dictionary) {
        this.dictionary = dictionary;
    }
    
    public InvertedIndex() {
    }
    
    public void addNewDocument(Document document){
        listOfDocument.add(document);
    }
    
    public ArrayList<Posting> getUnsortedPostingList(){
        // siapkan posting List
        ArrayList<Posting> list = new ArrayList<Posting>();
        // buat node Posting utk listofdocument
        for (int i = 0; i < listOfDocument.size(); i++) {
            // buat listOfTerm dari document ke -i
            String[] termResult = listOfDocument.get(i).getListofTerm();
            // loop sebanyak term dari document ke i
            for (int j = 0; j < termResult.length; j++) {
                // buat object tempPosting
                Posting tempPosting = new Posting(termResult[j],
                        listOfDocument.get(i));
                list.add(tempPosting);
            }
        }
        return list;
    }
    
    public ArrayList<Posting> getSortedPostingList(){
        // siapkan posting List
        ArrayList<Posting> list = new ArrayList<Posting>();
        // panggil list yang belum terurut
        list = this.getUnsortedPostingList();
        // urutkan
        Collections.sort(list);
        return list;
    }
    
    public ArrayList<Posting> search(String kunci) {
        makeDictionary();
        String[] q = kunci.split(" ");
        ArrayList<ArrayList<Posting>> posting = new ArrayList<>();
        
        for (int i = 0; i < q.length; i++) {
            posting.add(searchOneWord(q[i]));
        }
        return intersection(posting.get(0), posting.get(1));
    }

    public ArrayList<Posting> intersection(ArrayList<Posting> p1, ArrayList<Posting> p2) {
        if (p1 == null || p2 == null) {
            return new ArrayList<>();
        }
        
        ArrayList<Posting> posting = new ArrayList<>();
        int index_p1 = 0;
        int index_p2 = 0;

        Posting post1 = p1.get(index_p1);
        Posting post2 = p2.get(index_p2);

        while (true) {
            if (post1.getDocument().getId() == post2.getDocument().getId()) {
                try {
                    posting.add(post1);
                    index_p1++;
                    index_p2++;
                    post1 = p1.get(index_p1);
                    post2 = p2.get(index_p2);
                } catch (Exception e) {
                    break;
                }
            } else if (post1.getDocument().getId() < post2.getDocument().getId()) {
                try {
                    index_p1++;
                    post1 = p1.get(index_p1);
                } catch (Exception e) {
                    break;
                }
            } else {
                try {
                    index_p2++;
                    post2 = p2.get(index_p2);
                } catch (Exception e) {
                    break;
                }
            }
        }
        return posting;
    }
    
    public ArrayList<Posting> searchOneWord(String word){
        Term tempTerm = new Term (word);
        if(getDictionary().isEmpty()){
            //dictionary kosong
            return null;
        }
        else{
            int positionTerm = Collections.binarySearch(dictionary, tempTerm);
            return dictionary.get(positionTerm).getPostingList();
        
        }
    }
    
    public void makeDictionary(){
        // buat posting list term terurut
        ArrayList<Posting> list = getSortedPostingList();
        // looping buat list of term (dictionary)
        for (int i = 0; i < list.size(); i++) {
            // cek dictionary kosong?
            if(dictionary.isEmpty()){
                // buat term
                Term term = new Term(list.get(i).getTerm());
                // tambah posting ke posting list utk term ini
                term.getPostingList().add(list.get(i));
                // tambah ke dictionary
                getDictionary().add(term);
            } else{
                // dictionary sudah ada isinya
                Term tempTerm = new Term(list.get(i).getTerm());
                // pembandingan apakah term sudah ada atau belum
                // luaran dari binarysearch adalah posisi
                int position= Collections.binarySearch(dictionary, tempTerm);
                if(position<0){
                    // term baru
                    // tambah postinglist ke term
                    tempTerm.getPostingList().add(list.get(i));
                    // tambahkan term ke dictionary
                    dictionary.add(tempTerm);
                } else{
                    // term ada
                    // tambahkan postinglist saja dari existing term
                    dictionary.get(position).
                            getPostingList().add(list.get(i));
                    // urutkan posting list
                    Collections.sort(dictionary.get(position)
                            .getPostingList());
                }
                // urutkan term dictionary
                Collections.sort(dictionary);
            }
            
        }
        
    }

}
