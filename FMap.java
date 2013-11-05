/*Catherine Patchell
 * patchell.c@husky.neu.edu
 * Assignment 8
 */

import java.util.*;

//FMap<K,V> is an immutable ADT whose values represent
//finite functions from keys of type K to values of type V.
public abstract class FMap<K, V> implements Iterable<K>{

    //abstract methods
    public abstract boolean isEmpty();
    public abstract int size();
    public abstract boolean containsKey(K x);
    public abstract V get (K x);
    public abstract FMap<K, V> add(K k, V v);

    //abstract methods that help the equals method
    public abstract boolean sameK(FMap<K, V> fm);
    public abstract boolean sameV(FMap<K, V> fm, FMap<K, V> acc);

    //abstract method to add keys of FMap to an arraylist
    public abstract ArrayList<K> addKeys(ArrayList<K> arl);
    
    //abstract method that accepts a visitor
    public abstract FMap<K,V> accept(Visitor<K,V> visitor);


    //creates a new EmptyMap
    public static <K,V> FMap<K,V> emptyMap(){
        return new EmptyMap<K,V>();
    }

    //creates a new EmptyMap
    public static <K,V> FMap<K,V> emptyMap(Comparator<? super K> c){
        return new EmptyMapBST<K, V>(c);
    }

    //returns a string representing the given FMap
    public String toString(){
        return "{...(" + this.size() + " entries)...}";
    }

    //returns the hashcode of the FMap
    public int hashCode(){
        return this.size() + this.toString().hashCode();
    }

    //determines if two FMap's are equal
    public boolean equals(Object o){
        if (o instanceof FMap){
            @SuppressWarnings(value="unchecked")
            FMap<K, V> fx = (FMap<K, V>) o;
            return this.sameK(fx) 
                    && fx.sameK(this) 
                    && this.sameV(fx, new EmptyMap<K, V>())
                    && fx.sameV(this, new EmptyMap<K, V>());
        } else 
            return false;  
    }

    //create an iterator from the given FMap
    public Iterator<K> iterator(){
        return new FMapIt<K>(this.addKeys(new ArrayList<K>()));
    }

    //create an iterator from the given FMap
    public Iterator<K> iterator(java.util.Comparator<? super K> c){
        ArrayList<K> arl = this.addKeys(new ArrayList<K>());
        Collections.sort(arl, c);
        return new FMapIt<K>(arl);
    }
}

//basic creator for FMap<K, V>
class EmptyMap<K, V> extends FMap<K, V>{
    Comparator <? super K> c;  //a comparator to order elements

    //constructor that takes in nothing
    EmptyMap(){}

    //determines of the FMap is empty
    @Override
    public boolean isEmpty(){
        return true;
    }

    //returns the size of the FMap 
    @Override
    public int size(){
        return 0;
    }

    //determines if the FMap contains the given key
    @Override
    public boolean containsKey(K x){
        return false;
    }

    //returns the value at the given key
    @Override
    public V get(K x){
        String msg = "Can not get element from emptyMap";
        throw new RuntimeException(msg);
    }

    //determines if the FMaps have the same K when empty
    public boolean sameK(FMap<K, V> f){
        return true;
    }

    //determines if the FMaps have the same V when empty
    public boolean sameV(FMap<K, V> f,  FMap<K, V> acc){
        return true;
    }

    //adds the keys of the FMap to an arraylist
    public ArrayList<K> addKeys(ArrayList<K> arl){
        return arl;
    }

    //Adds to the FMap
    @Override
    public FMap<K, V> add(K k, V v) {
        return new Add<K,V>(this, k, v);
    }

    //accepts the visitor to be applied to each element
	@Override
	public FMap<K, V> accept(Visitor<K, V> visitor) {
		return this;
	}
}

//basic creator for FMap<K, V>
class Add<K, V> extends FMap<K, V>{
    FMap<K, V> m0;    //represents an FMap
    K k0;              //represents the next K being added to FMap
    V v0;              //represents the next V being added to FMap
    Add(FMap<K, V> m0, K k0, V v0){
        this.m0 = m0;
        this.k0 = k0;
        this.v0 = v0;
    }

    //determines of the FMap is empty
    @Override
    public boolean isEmpty(){
        return false;
    }

    //returns the size of the FMap 
    @Override
    public int size(){
        if (m0.containsKey(k0)){
            return m0.size();
        } else
            return 1 + m0.size();
    }

    //determines if the FMap contains the given key
    @Override
    public boolean containsKey(K x){
        if (x.equals(k0)){
            return true;
        } else
            return m0.containsKey(x);
    }

    //returns the value at the given key
    @Override
    public V get(K x){
        if (x.equals(k0)){
            return v0;
        } else
            return m0.get(x);
    }

    //determines if the FMaps have the same values(v's
    public boolean sameV(FMap<K, V> fm, FMap<K, V> acc){
        if (!(acc.containsKey(k0))){
            return this.get(k0).equals(fm.get(k0))
                    && m0.sameV(fm, acc.add(k0, v0));
        } else
            return m0.sameV(fm, acc);
    }

    //determines if the FMaps contain the same keys(k's)
    public boolean sameK(FMap<K, V> fm){
        return (fm.containsKey(k0) && this.m0.sameK(fm));
    }

    //adds the keys of the FMap to an arraylist
    public ArrayList<K> addKeys(ArrayList<K> arl){
        if (!(arl.contains(k0))){
            arl.add(k0);
            return this.m0.addKeys(arl);
        } else
            return m0.addKeys(arl);
    }      
    //Adds to the FMap
    @Override
    public FMap<K, V> add(K k, V v) {
        return new Add<K,V>(this, k, v);
    }

    //accepts the visitor to be applied to each element
	@Override
	public FMap<K, V> accept(Visitor<K, V> visitor){
		return new Add<K,V>(m0.accept(visitor), 
				              k0, visitor.visit(k0, v0));
	}
}


//basic creator for FMap<K, V>
class EmptyMapBST<K, V> extends FMap<K, V>{
    Comparator <? super K> c;  //a comparator to order elements

    //constructor that takes in a comparator
    EmptyMapBST(java.util.Comparator<? super K> c){
        this.c = c;
    }

    //determines of the FMap is empty
    @Override
    public boolean isEmpty(){
        return true;
    }

    //returns the size of the FMap 
    @Override
    public int size(){
        return 0;
    }

    //determines if the FMap contains the given key
    @Override
    public boolean containsKey(K x){
        return false;
    }

    //returns the value at the given key
    @Override
    public V get(K x){
        String msg = "Can not get element from emptyMap";
        throw new RuntimeException(msg);
    }

    //determines if the FMaps have the same K when empty
    public boolean sameK(FMap<K, V> f){
        return true;
    }

    //determines if the FMaps have the same V when empty
    public boolean sameV(FMap<K, V> f, FMap<K,V> fm){
        return true;
    }

    //adds the keys of the FMap to an arraylist
    public ArrayList<K> addKeys(ArrayList<K> arl){
        return arl;
    }

    //adds element
    public FMap<K, V> add(K k, V v) {
        return new AddBST<K,V>(c, k, v, 
                new EmptyMapBST<K,V>(c), 
                new EmptyMapBST<K,V>(c));
    }

    //accepts the visitor to be applied to each element
	@Override
	public FMap<K, V> accept(Visitor<K, V> v){
		return this;
	}
}

//basic creator for FMap<K, V>
class AddBST<K, V> extends FMap<K, V>{
    Comparator <? super K> c;  //comparator used to order FMap
    K k0;                      //represents next K to add to FMap
    V v0;                      //represents next V to add to FMap
    FMap<K,V> left;            //represents the FMap on the left
    FMap<K,V> right;           //represents the FMap on the right
    private int size;          //represents the size of the FMap
    AddBST(Comparator <? super K> c, K k0, V v0, 
            FMap<K,V> left, FMap<K,V> right){
        this.c = c;
        this.k0 = k0;
        this.v0 = v0;
        this.left = left;
        this.right = right;
        this.size = 1 + this.left.size() + this.right.size();
    }

    //determines of the FMap is empty
    @Override
    public boolean isEmpty(){
        return false;
    }

    //returns the size of the FMap
    @Override
    public int size() {
        return this.size;
    }

    //determines if the FMap contains the given key
    @Override
    public boolean containsKey(K k){
        if (c.compare(k0, k) == 0){
            return true;
        } else
            if (c.compare(k0, k) < 0){
                return this.left.containsKey(k); 
            } else
                return this.right.containsKey(k);
    }

    //returns the value at the given key
    @Override
    public V get(K k){
        if (c.compare(k0, k) == 0){
            return v0;
        } if (c.compare(k0, k) < 0){
            return this.left.get(k);
        } else
            return this.right.get(k);
    }

    //determines if the FMaps have the same values(v's
    public boolean sameV(FMap<K,V> fm, FMap<K,V> acc){
        return this.get(k0).equals(fm.get(k0))
                && this.left.sameV(fm, acc)
                && this.right.sameV(fm, acc);
    }

    //determines if the FMaps contain the same keys(k's)
    public boolean sameK(FMap<K, V> fm){
        return (fm.containsKey(k0) 
                && this.left.sameK(fm))
                && this.right.sameK(fm);
    }

    //adds the keys of the FMap to an arraylist
    public ArrayList<K> addKeys(ArrayList<K> arl){
        arl.add(k0);
        this.left.addKeys(arl);
        this.right.addKeys(arl);
        return arl;
    }

    //adds given K,V to the FMap
    public FMap<K, V> add(K k, V v) {
        if (c.compare(k0, k) == 0){
            return new AddBST<K,V>(c, k, v, this.left, this.right);
        }
        if (c.compare(k0, k) < 0){
            return new AddBST<K,V>(c, k0, v0, 
                    this.left.add(k, v), 
                    this.right);
        } else
            return new AddBST<K,V>(c, k0, v0, 
                    this.left, 
                    this.right.add(k,v));
    }

    //accepts the visitor to be applied to each element
	@Override
	public FMap<K, V> accept(Visitor<K, V> visitor) {
		return new AddBST<K,V>(c, k0, visitor.visit(k0, v0),
				    left.accept(visitor), right.accept(visitor));
	}

}

//iterator class
class FMapIt<K> implements Iterator<K>{
    ArrayList<K> ar;        //array list of k values
    int count;              //how many elements into arraylist
    FMapIt(ArrayList<K> ar){
        this.ar = ar;
        this.count = 0;
    }

    //determines if this has a next element
    @Override
    public boolean hasNext() {
        return this.ar.size() > count;
    }

    //returns the next element to be returned
    @Override
    public K next() {
        if (this.hasNext()){
            K k = this.ar.get(count);
            this.count++;
            return k;
        }else 
            throw new NoSuchElementException();
    }

    //remove the previous element - should throw exception    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }   
}



////returns the hashcode of the FMap
//public int hashCode(){
//	return ((k0.hashCode() + v0.hashCode()) * 1487
//			+ this.m0.hashCode());
//}

////hashcode for an Add
//public int hashCode() {
//return (k0.hashCode()*31 + v0.hashCode()*17) ^ m0.hashCode();
//}
//

////returns the hashcode of the FMap
//public int hashCode(){
//	int hash = 31;
//	hash = hash * 1487 + k0.hashCode() + this.left.hashCode();
//	hash = hash * 1487 + v0.hashCode() + this.right.hashCode();
////	this.left.hashCode();
////	this.right.hashCode();
//	return hash;
////	return ((k0.hashCode() + v0.hashCode()) * 1487
////			+ this.right.hashCode())
////			+ this.left.hashCode();
//}

////hashcode for a Node
//public int hashCode() {
//  return (k0.hashCode()*31 + v0.hashCode()*17) ^
//      left.hashCode() ^ right.hashCode();
//}

