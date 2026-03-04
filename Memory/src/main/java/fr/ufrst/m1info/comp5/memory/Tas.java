package fr.ufrst.m1info.comp5.memory;

import java.util.*;

public class Tas {
    private int MAX_SIZE = 4096;
    private  int heapSize ;
    private Object[] heap;
    private LinkedList<Integer>[] listOfBlocks;
    private ArrayList<InfoTab> tableOfArrays;
    private int counterOfArray;
    public Tas() throws TasException {
        this(512);
    }
    public Tas(int customHeapSize) throws TasException {
        if(!isPowerOfTwo(customHeapSize)){
            throw new TasException("The size is not a power of two");
        }
        this.heap = new Object[customHeapSize];
        heapSize = customHeapSize;
        this.tableOfArrays = new ArrayList<>();
        this.listOfBlocks = new LinkedList[logN(heapSize,2)+1];
        int maxIndex = listOfBlocks.length;
        for(int i =0 ; i<maxIndex ; i++){
            listOfBlocks[i] = new LinkedList<>();
        }
        LinkedList<Integer> myList = new LinkedList<>();
        myList.add(0);
        listOfBlocks[maxIndex-1] = myList;
        counterOfArray = 0;
    }
    public void createNewListOfBlocks(){
        this.listOfBlocks = new LinkedList[logN(heapSize,2)+1];
        int maxIndex = listOfBlocks.length;
        for(int i =0 ; i<maxIndex ; i++){
            listOfBlocks[i] = new LinkedList<>();
        }
    }
    public void setTableOfArrays(ArrayList<InfoTab> newList){
        this.tableOfArrays = newList;
    }
    public void setHeap(Object[] newHeap){
        this.heap  = newHeap;
    }
    public List<InfoTab> getTableOfArrays(){
        return this.tableOfArrays;
    }
    public Object[] getHeap (){
        return this.heap;
    }
    public LinkedList<Integer>[] getListOfBlocks(){
        return this.listOfBlocks;
    }

    public boolean isPowerOfTwo(int number){
        return (number > 0) && ((number & (number-1))==0);
    }

    public int logN(int number, int base){
        return (int) Math.ceil((Math.log(number) / Math.log(base)));
    }
    public int getUpperBound(int size){
        int mySize = 1;
        while (mySize < size ){
            mySize = mySize * 2;
        }
        return mySize;
    }
    public int getLowerBound(int size){
        int mySize =1;
        while(mySize < size){
            mySize = mySize * 2;
        }
        // In case where size is a power of 2
        if(mySize==size){
            return mySize;
        }
        return mySize /2;
    }
    public int getBestBlock(int size) throws TasException {
        if(size > heapSize){
            throw new TasException("The size of the block is too big");
        }
        int index = logN(size,2);
        List<Integer> myList = listOfBlocks[index];
        if(myList.isEmpty()){
            return getBestBlock(getUpperBound(size*2));
        }
        return getUpperBound(size);
    }
    public int amountOfFreeSlots(){
        int sum = 0;
        for(int i =0; i< listOfBlocks.length;i++){
            if(!listOfBlocks[i].isEmpty()){
                for(int y =0 ; y < listOfBlocks[i].size();y++){
                    sum += (int) Math.pow(2,i);
                }
            }
        }
        return sum;
    }
    // Identify the best free block and free all the space inside it
    // Insert the array inside the newly freed space
    // If the array doesnt fill all the free space, insert new free block
    public int addBlock(int size) throws TasException {
        if(size > amountOfFreeSlots()){
            increaseSize();
        }
        int index = logN(size,2);
        for(int i =index ; i< listOfBlocks.length ;i++){
            if(!listOfBlocks[i].isEmpty()){
                index = i;
                break;
            }
        }
        if(index == listOfBlocks.length){
            reconstructHeap();
            return addBlock(size);
        }
        // We have the best free block at "index", we will get the real index inside the heap of the free block
        int realIndex = listOfBlocks[index].poll();
        int startOfFreeAfterArray = realIndex + size ;
        int endingOfFreedBLock = (int) Math.pow(2,index) + realIndex;
        createFreeBlocks(startOfFreeAfterArray,endingOfFreedBLock);
        InfoTab array = new InfoTab(counterOfArray,realIndex,size,1);
        counterOfArray++;
        tableOfArrays.add(array);
        return array.getId();
    }

    /**
     * Create free blocks between start and end, and add them to the list of free blocks
     * @param start start of the free space
     * @param end End of the free space
     */
    public void createFreeBlocks(int start, int end){
        int difference = end - start;
        int counter = 0;
        // We create
        while(difference != 0){
            int biggestPowerOfTwo = getLowerBound(difference);
            difference -= biggestPowerOfTwo;

            int indexOfBiggest = logN(biggestPowerOfTwo,2);
            listOfBlocks[indexOfBiggest].add(start+counter);
            counter += biggestPowerOfTwo;
        }
    }
    public void reconstructHeap(){
        Object[] newHeap = new Object[heapSize];
        reconstructHeapWithParam(newHeap);
    }
    /**
     * Moves all arrays to the beginning of the heap, allowing to have bigger free blocks.
     */
    public void reconstructHeapWithParam(Object[] newHeap){
        int index = 0;
        // put all arrays at the beginning of the heap
        ArrayList<InfoTab> newList = new ArrayList<>();
        int counter = 0;
        for(InfoTab infoTab : tableOfArrays){
            //swap the values to the new place in the new heap.
            for(int i =0 ; i< infoTab.getTaille();i++){
                int startValueIndex = infoTab.getAdr();
                newHeap[index+i] = heap[startValueIndex+i];
            }
            InfoTab array = new InfoTab(counter,index, infoTab.getTaille(), infoTab.getRef());
            newList.add(array);
            counter++;
            index+= infoTab.getTaille();
        }
        createNewListOfBlocks();
        setTableOfArrays(newList);
        //Create Free blocks with the space left
        createFreeBlocks(index,heapSize);
        setHeap(newHeap);
    }

    public void increaseSize() throws TasException {
        if(heapSize >= MAX_SIZE){
            throw new TasException("Heap overflow");
        }
        int newHeapSize = heapSize * 2;
        Object[] newHeap = new Object[newHeapSize];
        LinkedList<Integer>[] newListOfBlocks =  new LinkedList[logN(newHeapSize,2)+1];
        int maxIndex = newListOfBlocks.length;
        for(int i =0 ; i<maxIndex ; i++){
            newListOfBlocks[i] = new LinkedList<>();
        }
        LinkedList<Integer> myList = new LinkedList<>();
        myList.add(0);
        newListOfBlocks[maxIndex-1] = myList;
        listOfBlocks = newListOfBlocks;
        heapSize = newHeapSize;
        reconstructHeapWithParam(newHeap);
    }

    public InfoTab getArrayWithId(int id) throws TasException {
        for(InfoTab infoTab : tableOfArrays) {
            if(infoTab.getId() == id){
                return infoTab;
            }
        }
        throw new TasException("Array is not found inside the list of arrays");
    }
    public void setValueOfArray(int id,int index, Object value) throws TasException {
        InfoTab infoTab = getArrayWithId(id);
        if( index < 0 || index > infoTab.getTaille()){
            throw new IndexOutOfBoundsException("The index is not a correct one");
        }
        heap[infoTab.getAdr() + index] = value;
    }
    public Object getValueOfArray(int id, int index) throws TasException {
        InfoTab infoTab = getArrayWithId(id);
        if( index < 0 || index >= infoTab.getTaille()){
            throw new IndexOutOfBoundsException("The index is not a correct one");
        }
        return heap[infoTab.getAdr() + index];
    }
    public void incrementRef(int id) throws TasException {
        InfoTab infoTab = getArrayWithId(id);
        infoTab.incrementRef();

    }
    public void decrementRef(int id) throws TasException {
        InfoTab infoTab = getArrayWithId(id);
        infoTab.decrementRef();
    }

    /**
     * Utilisé pour la synonymie : t1 = t2; On clear les blocks de t1 et on ajoute une ref a t2; l'increment de la ref n'est pas traitée ici
     * @param id
     * @throws TasException
     */
    public void clearArrayValues(int id) throws TasException {
        InfoTab infoTab = getArrayWithId(id);
        int start = infoTab.getAdr();
        int end = start + infoTab.getTaille();
        for(int i = 0; i< infoTab.getTaille(); i++){
            heap[start +i] = null;
        }
        createFreeBlocks(start, end );
    }
    public void removeArray(int id) throws TasException {
        InfoTab infoTab = getArrayWithId(id);
        if(infoTab.getRef() > 1 ){
            infoTab.decrementRef();
        } else {
            clearArrayValues(id);
            tableOfArrays.remove(infoTab);
        }
    }
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for(InfoTab infoTab : tableOfArrays){
            //print info
            res.append("ID : ").append(infoTab.getId()).append("; SIZE : ").append(infoTab.getTaille());
            res.append("; ADR : ").append(infoTab.getAdr()).append("; NB REF : ").append(infoTab.getRef());
            // print content
            res.append("; CONTENT : [ ");
            for(int i = 0; i < infoTab.getTaille() ; i++){
                try {
                    if(getValueOfArray(infoTab.getId(),  i) == null ){
                        res.append("? ");
                    } else {
                        res.append(getValueOfArray(infoTab.getId(), i)).append(" ");
                    }
                } catch(TasException e){ }
            }
            res.append("]");
            res.append("\n");
        }
        return res.toString();
    }

}
