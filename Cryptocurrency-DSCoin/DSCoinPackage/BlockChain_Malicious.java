package DSCoinPackage;

import HelperClasses.CRF;

import HelperClasses.MerkleTree;
import HelperClasses.Pair;


public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    CRF obj = new CRF(64);

    //checking dgst

    if(!tB.dgst.substring(0, 4).equals("0000"))
      return false; // invalid nonce
    if(tB.previous != null){
      if(!tB.dgst.equals(obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce)))
        return false; // invalid dgst
    }
    else{
      if(!tB.dgst.equals(obj.Fn(BlockChain_Malicious.start_string + "#" + tB.trsummary + "#" + tB.nonce)))
        return false; // invalid dgst
    }

    //checking tree summary

    MerkleTree tree = new MerkleTree();
    tree.Build(tB.trarray);
    String my_summary = tree.rootnode.val;
    if(!my_summary.equals(tB.trsummary)){
      return false;
    }
    //checking all the transactions

    int x = 0;
    while(x < tB.trarray.length && tB.trarray[x] != null){
      if(!tB.checkTransaction(tB.trarray[x])){
        return false;
      }
      x++;
    }

    //if all goes well

    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    int f = 0;
    while(f < lastBlocksList.length && lastBlocksList[f] != null)
      f++;
    int[] number = new int[f];
    TransactionBlock[] lastvalid = new TransactionBlock[f];
    TransactionBlock current = lastBlocksList[0];
    for(int i = 0; i < f; i++){
      number[i] = 0;
      lastvalid[i] = lastBlocksList[i];
      current = lastBlocksList[i];
      while(current != null){
        if(checkTransactionBlock(current)){
          number[i]++;
        }
        else{               // we won't enter here when we're at the tail of the list
          number[i] = 0;    // since it's given we can assume some of the starting blocks are surely valid as they are created by moderator
          lastvalid[i] = current.previous;
        }
        current = current.previous;
      }
    }
    Pair<Integer, Integer> max = new Pair<Integer, Integer>(number[0], 0); // will store value and index of max no. of valid chain;
    for(int d = 0; d < f; d++){
      if(number[d] > max.get_first()){
        max.first = number[d];
        max.second = d;
      }
    }

    return lastvalid[max.get_second()];
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) { // how do I check if the blockchain is empty?
    CRF obj = new CRF(64);
    if(lastBlocksList[0] == null){ // if blockchain is empty
      // setting nonce

      String non = new String();
      long i = 1000000000L;
      long b = 9999999999L;
      while(i - b <= 0){
        non = obj.Fn(BlockChain_Malicious.start_string + "#" + newBlock.trsummary + "#" + String.valueOf(i));
        if(non.substring(0, 4).equals("0000")){
          newBlock.nonce = String.valueOf(i);
          break;
        }
        i++;
      }
      newBlock.dgst = obj.Fn(BlockChain_Malicious.start_string + "#" + newBlock.trsummary + "#" + newBlock.nonce);
      lastBlocksList[0] = newBlock;
    }

    else{ // if blockchain is not empty
      int f = 0;
      while(lastBlocksList[f] != null)
        f++;
      TransactionBlock currentlast = FindLongestValidChain();
      newBlock.previous = currentlast;

      // setting nonce

      String non = new String();
      long i = 1000000000L;
      long b = 9999999999L;
      while(i - b <= 0){
        non = obj.Fn(currentlast.dgst + "#" + newBlock.trsummary + "#" + String.valueOf(i));
        if(non.substring(0, 4).equals("0000")){
          newBlock.nonce = String.valueOf(i);
          break;
        }
        i++;
      }
      newBlock.dgst = obj.Fn(currentlast.dgst + "#" + newBlock.trsummary + "#" + newBlock.nonce);
      // updating the lastBlocksList with new lastBlock

      int flag = 0;
      for(int j = 0; j < f; j++){
        if(lastBlocksList[j] == currentlast){
          lastBlocksList[j] = newBlock;
          flag = 1;
          break;
        }
      }
      if(flag == 0){
        lastBlocksList[f] = newBlock;
      }
    }
  }
}
