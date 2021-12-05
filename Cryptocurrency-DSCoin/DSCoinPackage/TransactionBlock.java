package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  public TransactionBlock(Transaction[] t) {
    trarray = new Transaction[t.length];
    for(int i = 0; i < t.length; i++)
      trarray[i] = null;
    for(int i = 0 ; i < t.length; i++)
      trarray[i] = t[i];
    previous = null;
    MerkleTree tree = new MerkleTree();
    trsummary = tree.Build(trarray);
    Tree = tree;
    dgst = null;

  }

  public boolean checkTransaction (Transaction t) {
    TransactionBlock current = this;
    int x = 0;

    //testing spending in intermediate blocks

    current = current.previous;
    if(t.coinsrc_block != null){
      while(current != t.coinsrc_block){
        x = 0;
        while(x < current.trarray.length && current.trarray[x] != null){
          if(current.trarray[x].coinID.equals(t.coinID))
            return false;
          x++;
        }
        current = current.previous;
      }
    }
    else{
      while(current != null){
        x = 0;
        while(x < current.trarray.length && current.trarray[x] != null){
          if(current.trarray[x].coinID.equals(t.coinID)){
            return false;
          }
          x++;
        }
        current = current.previous;
      }
    }

    //checking if transaction is present in it's source block or not

    if(t.coinsrc_block == null) // means coin is distributed by the moderator/ created by the miner as reward for himself, hence no need to check
      return true;
    else{
      x = 0;
      while(x < t.coinsrc_block.trarray.length && t.coinsrc_block.trarray[x] != null){
        if(t.coinsrc_block.trarray[x].coinID.equals(t.coinID) && t.coinsrc_block.trarray[x].Destination == t.Source)
          return true;
        x++;
      }
      return false;
    }
  }


  public boolean checkTransaction_for_minecoin (Transaction t) {
    TransactionBlock current = this;
    int x = 0;

    //testing spending in intermediate blocks

    if(t.coinsrc_block != null){
      while(current != t.coinsrc_block){
        x = 0;
        while(x < current.trarray.length && current.trarray[x] != null){
          if(current.trarray[x].coinID.equals(t.coinID))
            return false;
          x++;
        }
        current = current.previous;
      }
    }
    else{
      while(current != null){
        x = 0;
        while(x < current.trarray.length && current.trarray[x] != null){
          if(current.trarray[x].coinID.equals(t.coinID)){
            return false;
          }
          x++;
        }
        current = current.previous;
      }
    }

    //checking if transaction is present in it's source block or not

    if(t.coinsrc_block == null) // means coin is distributed by the moderator/ created by the miner as reward for himself, hence no need to check
      return true;
    else{
      x = 0;
      while(x < t.coinsrc_block.trarray.length && t.coinsrc_block.trarray[x] != null){
        if(t.coinsrc_block.trarray[x].coinID.equals(t.coinID) && t.coinsrc_block.trarray[x].Destination == t.Source)
          return true;
        x++;
      }
      return false;
    }
  }
}
