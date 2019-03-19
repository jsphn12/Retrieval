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
    
    public InvertedIndex() {
    }
    
    public void addNewDocument(Document document) {
        getListOfDocument().add(document);
    }
    
    public ArrayList<Posting> getUnsortedPostingList() {
        // cek untuk term yang muncul lebih dari 1 kali
        // siapkan posting List
        ArrayList<Posting> list = new ArrayList<Posting>();
        // buat node Posting utk listofdocument
        for (int i = 0; i < getListOfDocument().size(); i++) {
            // buat listOfTerm dari document ke -i
            String[] termResult = getListOfDocument().get(i).getListofTerm();
            // loop sebanyak term dari document ke i
            for (int j = 0; j < termResult.length; j++) {
                // buat object tempPosting
                Posting tempPosting = new Posting(termResult[j],
                        getListOfDocument().get(i));
                // cek kemunculan term
                list.add(tempPosting);
            }
        }
        return list;
    }
    
    public ArrayList<Posting> getUnsortedPostingListWithTermNumber() {
        // cek untuk term yang muncul lebih dari 1 kali
        // siapkan posting List
        ArrayList<Posting> list = new ArrayList<Posting>();
        // buat node Posting utk listofdocument
        for (int i = 0; i < getListOfDocument().size(); i++) {
            // buat listOfTerm dari document ke -i
            //String[] termResult = getListOfDocument().get(i).getListofTerm();
            ArrayList<Posting> postingDocument = getListOfDocument().get(i).getListofPosting();
            // loop sebanyak term dari document ke i
            for (int j = 0; j < postingDocument.size(); j++) {
                // ambil objek posting
                Posting tempPosting = postingDocument.get(j);
                // cek kemunculan term
                list.add(tempPosting);
            }
        }
        return list;
    }
    
    public ArrayList<Posting> getSortedPostingList() {
        // siapkan posting List
        ArrayList<Posting> list = new ArrayList<Posting>();
        // panggil list yang belum terurut
        list = this.getUnsortedPostingList();
        // urutkan
        Collections.sort(list);
        return list;
    }
    
    public ArrayList<Posting> getSortedPostingListWithTermNumber() {
        // siapkan posting List
        ArrayList<Posting> list = new ArrayList<Posting>();
        // panggil list yang belum terurut
        list = this.getUnsortedPostingListWithTermNumber();
        // urutkan
        Collections.sort(list);
        return list;
    }

    /**
     * Fungsi cari dokumen
     *
     * @param query
     * @return
     */
    public ArrayList<Posting> search(String query) {
        // buat index/dictionary
//        makeDictionary();
        String tempQuery[] = query.split(" ");
        ArrayList<Posting> result = new ArrayList<Posting>();
        for (int i = 0; i < tempQuery.length; i++) {
            String string = tempQuery[i];
            if (i == 0) {
                result = searchOneWord(string);
            } else {
                ArrayList<Posting> result1 = searchOneWord(string);
                result = intersection(result, result1);
            }
        }
        return result;
    }

    /**
     * Fungsi untuk menggabungkan 2 buah posting Made by Johan
     *
     * @param p1
     * @param p2
     * @return
     */
    public ArrayList<Posting> intersection(ArrayList<Posting> p1,
            ArrayList<Posting> p2) {
        if (p1 == null || p2 == null) {
            return new ArrayList<>();
        }
        
        ArrayList<Posting> postings = new ArrayList<>();
        int p1Index = 0;
        int p2Index = 0;
        
        Posting post1 = p1.get(p1Index);
        Posting post2 = p2.get(p2Index);
        
        while (true) {
            if (post1.getDocument().getId() == post2.getDocument().getId()) {
                try {
                    postings.add(post1);
                    p1Index++;
                    p2Index++;
                    post1 = p1.get(p1Index);
                    post2 = p2.get(p2Index);
                } catch (Exception e) {
                    break;
                }
                
            } else if (post1.getDocument().getId() < post2.getDocument().getId()) {
                try {
                    p1Index++;
                    post1 = p1.get(p1Index);
                } catch (Exception e) {
                    break;
                }
                
            } else {
                try {
                    p2Index++;
                    post2 = p2.get(p2Index);
                } catch (Exception e) {
                    break;
                }
            }
        }
        return postings;
    }
    
    public ArrayList<Posting> searchOneWord(String word) {
        Term tempTerm = new Term(word);
        if (getDictionary().isEmpty()) {
            // dictionary kosong
            return null;
        } else {
            int positionTerm = Collections.binarySearch(dictionary, tempTerm);
            if (positionTerm < 0) {
                // tidak ditemukan
                return null;
            } else {
                return dictionary.get(positionTerm).getPostingList();
            }
        }
    }
    
    public void makeDictionary() {
        // cek deteksi ada term yang frekuensinya lebih dari 
        // 1 pada sebuah dokumen
        // buat posting list term terurut
        ArrayList<Posting> list = getSortedPostingList();
        // looping buat list of term (dictionary)
        for (int i = 0; i < list.size(); i++) {
            // cek dictionary kosong?
            if (getDictionary().isEmpty()) {
                // buat term
                Term term = new Term(list.get(i).getTerm());
                // tambah posting ke posting list utk term ini
                term.getPostingList().add(list.get(i));
                // tambah ke dictionary
                getDictionary().add(term);
            } else {
                // dictionary sudah ada isinya
                Term tempTerm = new Term(list.get(i).getTerm());
                // pembandingan apakah term sudah ada atau belum
                // luaran dari binarysearch adalah posisi
                int position = Collections.binarySearch(getDictionary(), tempTerm);
                if (position < 0) {
                    // term baru
                    // tambah postinglist ke term
                    tempTerm.getPostingList().add(list.get(i));
                    // tambahkan term ke dictionary
                    getDictionary().add(tempTerm);
                } else {
                    // term ada
                    // tambahkan postinglist saja dari existing term
                    getDictionary().get(position).
                            getPostingList().add(list.get(i));
                    // urutkan posting list
                    Collections.sort(getDictionary().get(position)
                            .getPostingList());
                }
                // urutkan term dictionary
                Collections.sort(getDictionary());
            }
            
        }
        
    }
    
    public void makeDictionaryWithTermNumber() {
        // cek deteksi ada term yang frekuensinya lebih dari 
        // 1 pada sebuah dokumen
        // buat posting list term terurut
        ArrayList<Posting> list = getSortedPostingListWithTermNumber();
        // looping buat list of term (dictionary)
        for (int i = 0; i < list.size(); i++) {
            // cek dictionary kosong?
            if (getDictionary().isEmpty()) {
                // buat term
                Term term = new Term(list.get(i).getTerm());
                // tambah posting ke posting list utk term ini
                term.getPostingList().add(list.get(i));
                // tambah ke dictionary
                getDictionary().add(term);
            } else {
                // dictionary sudah ada isinya
                Term tempTerm = new Term(list.get(i).getTerm());
                // pembandingan apakah term sudah ada atau belum
                // luaran dari binarysearch adalah posisi
                int position = Collections.binarySearch(getDictionary(), tempTerm);
                if (position < 0) {
                    // term baru
                    // tambah postinglist ke term
                    tempTerm.getPostingList().add(list.get(i));
                    // tambahkan term ke dictionary
                    getDictionary().add(tempTerm);
                } else {
                    // term ada
                    // tambahkan postinglist saja dari existing term
                    getDictionary().get(position).
                            getPostingList().add(list.get(i));
                    // urutkan posting list
                    Collections.sort(getDictionary().get(position)
                            .getPostingList());
                }
                // urutkan term dictionary
                Collections.sort(getDictionary());
            }
        }
        
    }

    /**
     * @return the listOfDocument
     */
    public ArrayList<Document> getListOfDocument() {
        return listOfDocument;
    }

    /**
     * @param listOfDocument the listOfDocument to set
     */
    public void setListOfDocument(ArrayList<Document> listOfDocument) {
        this.listOfDocument = listOfDocument;
    }

    /**
     * @return the dictionary
     */
    public ArrayList<Term> getDictionary() {
        return dictionary;
    }

    /**
     * @param dictionary the dictionary to set
     */
    public void setDictionary(ArrayList<Term> dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Fungsi mencari frequensi sebuah term dalam sebuah index
     *
     * @param term
     * @return
     */
    public int getDocumentFrequency(String term) {
        Term tempTerm = new Term(term);
        // cek apakah term ada di dictionary
        int index = Collections.binarySearch(dictionary, tempTerm);
        if (index > 0) {
            // term ada
            // ambil ArrayList<Posting> dari object term
            ArrayList<Posting> tempPosting = dictionary.get(index)
                    .getPostingList();
            // return ukuran posting list
            return tempPosting.size();
        } else {
            // term tidak ada
            return -1;
        }
    }

    /**
     * Fungsi untuk mencari inverse term dari sebuah index
     *
     * @param term
     * @return
     */
    public double getInverseDocumentFrequency(String term) {
        Term tempTerm = new Term(term);
        // cek apakah term ada di dictionary
        int index = Collections.binarySearch(dictionary, tempTerm);
        if (index > 0) {
            // term ada
            // jumlah total dokumen
            int N = listOfDocument.size();
            // jumlah dokumen dengan term i
            int ni = getDocumentFrequency(term);
            // idf = log10(N/ni)
            double nni = (double)N/ni;
            return Math.log10(nni);
        } else {
            // term tidak ada
            // nilai idf = 0
            return 0.0;
        }
    }

    /**
     * Fungsi untuk mencari term frequency
     *
     * @param term
     * @param idDocument
     * @return
     */
    public int getTermFrequency(String term, int idDocument) {
        Document document = new Document();
        document.setId(idDocument);
        int pos = Collections.binarySearch(listOfDocument, document);
        if (pos >= 0) {
            ArrayList<Posting> tempPosting = listOfDocument.get(pos).getListofPosting();
            Posting posting = new Posting();
            posting.setTerm(term);
            int postingIndex = Collections.binarySearch(tempPosting, posting);
            if (postingIndex >= 0) {
                return tempPosting.get(postingIndex).getNumberOfTerm();
            }
            return 0;
        }
        
        return 0;
    }

    /**
     * Fungsi untuk menghitung TF-IDF dari sebuah dokumen
     *
     * @param idDocument
     */
    public ArrayList<Posting> makeTFIDF(int idDocument) {
        Document doc = new Document();
        doc.setId(idDocument);
        int cek = Collections.binarySearch(listOfDocument, doc);
        if(cek < 0 ){
            return null;
        }
        else {
            // dokumen ada
            doc = listOfDocument.get(cek);
            // buat posting list tanpa nilai TFIDF
            ArrayList<Posting> result = doc.getListofPosting();
            // melakukan pengurutan
            Collections.sort(result);
            // isi atribut weight dari masing-masing posting
            
            for (int i = 0; i < result.size(); i++) {
                //buat temp posting
                Posting temp = result.get(i);
                //panggil fungsi hitung idf
                double idf = getInverseDocumentFrequency(temp.getTerm());
                //panggil fungsi hitung tf
                int tf = temp.getNumberOfTerm();
                //hitung tfidf
                double weight = tf*idf;
                // set atribut weight di posting list
                result.get(i).setWeight(weight);
            }
            return result;
        }
    }
    
    /**
     * Fungsi untuk membuat posting list dari suatu query
     */
    
        public ArrayList<Posting> getQueryPosting(String query){
            //panggil class dokumen
        Document doc = new Document();
        doc.setContent(query);
        //buat posting list dengan query
        ArrayList<Posting> result = doc.getListofPosting();
        for (int i = 0; i < result.size(); i++) {
            // hitung weight = tf * idf
            double weight = result.get(i).getNumberOfTerm() * getInverseDocumentFrequency(result.get(i).getTerm());
            // hasil dari weight
            result.get(i).setWeight(weight);
        }

        return result;
    }

    /**
     *
     * @param postlist1 posting list1
     * @param postlist2 posting list2
     * @return hasil perkalian 2 buah postinglist
     */
    public double getInnerProduct(ArrayList<Posting> postlist1, ArrayList<Posting> postlist2) {
        double result = 0;
        for (int i = 0; i < postlist1.size(); i++) {
            int pos = Collections.binarySearch(postlist2, postlist1.get(i));
            if (pos >= 0) {
                result = result + (postlist1.get(i).getWeight() * postlist2.get(pos).getWeight());
            }
        }

        return result;
    }
    
    //mencari panjang dokumen
    public double getLengthOfPosting(ArrayList<Posting> posting) {
        //membuat variabel penyimpanan posting sementara
        double tempPost = 0;
        // lakukan perulangan sepanjang posting
        for (int i = 0; i < posting.size(); i++) {
            tempPost += Math.pow(posting.get(i).getWeight(), 2);
        }
        //lakukan perhitungan matematikanya.
        return Math.sqrt(tempPost);
    }
    
    // membuat kelas untuk menghitung nilai cosines similarity
    public double getCosineSimilarity(ArrayList<Posting> posting, ArrayList<Posting> posting1) {
        // membuat variabel untuk memanggil nilai inner produk
        double innerProduct = getInnerProduct(posting, posting1);
        double hasil = 0;
        // menghitung nilai cosines
        hasil = innerProduct / (getLengthOfPosting(posting) * getLengthOfPosting(posting1));
        return hasil;
    }
    
    //membuat kelas search TFIDF
    public ArrayList<SearchingResult> searchTFIDF(String query) {
        // membuat arraylist baru untuk menampung hasil search
        ArrayList<SearchingResult> result = new ArrayList<SearchingResult>();
        ArrayList<Posting> queryPost = this.getQueryPosting(query);
        
        // perulangan untuk melakukan pengecekan dokumen bedasarkan query. 
        for (int i = 1; i <= listOfDocument.size(); i++) {
            ArrayList<Posting> tempDoc = this.makeTFIDF(i);
            double  innerProduct = this.getInnerProduct(queryPost, tempDoc);
            SearchingResult doc = new SearchingResult(innerProduct, tempDoc.get(i-1).getDocument());
            result.add(doc);
        }
        // jika query yang dicari ketemu, maka hasilnya di ranking
        Collections.sort(result);
        Collections.reverse(result);
        return result;
    }
    
    //membuat kelas search Cosines Similarity
    public ArrayList<SearchingResult> searchCosineSimilarity(String query) {
        // membuat arraylist baru untuk menampung hasil search 
        ArrayList<SearchingResult> result = new ArrayList<SearchingResult>();
        ArrayList<Posting> queryPost = this.getQueryPosting(query);
        
        // perulangan untuk melakukan pengecekan dokumen berdasarkan query
        for (int i = 1; i <= listOfDocument.size(); i++) {
            ArrayList<Posting> tempDoc = this.makeTFIDF(i);
            double similarity = this.getCosineSimilarity(queryPost, tempDoc);
            SearchingResult doc = new SearchingResult(similarity,tempDoc.get(i-1).getDocument());
            result.add(doc);
        }
        // jika query yang dicari ketemu, maka hasilnya akan di ranking.
        Collections.sort(result);
        Collections.reverse(result);
        return result;
}

}