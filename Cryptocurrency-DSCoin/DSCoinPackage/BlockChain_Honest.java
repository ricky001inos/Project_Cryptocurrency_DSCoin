package DSCoinPackage;

import HelperClasses.CRF;


public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF obj = new CRF(64);
    if(lastBlock == null){
      //newBlock.previous = null;   // already done in the construtor

      // setting nonce

      String non = new String();
      long i = 1000000000L;
      long b = 9999999999L;
      while(i - b <= 0){
        non = obj.Fn(BlockChain_Honest.start_string + "#" + newBlock.trsummary + "#" + String.valueOf(i));
        if(non.substring(0, 4).equals("0000")){
          newBlock.nonce = String.valueOf(i);
          break;
        }
        i++;
      }
      newBlock.dgst = obj.Fn(BlockChain_Honest.start_string + "#" + newBlock.trsummary + "#" + newBlock.nonce);
      lastBlock = newBlock;
    }
    else{

      // setting nonce

      String non = new String();
      long i = 1000000000L;
      long b = 9999999999L;
      while(i - b <= 0){
        non = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + String.valueOf(i));
        if(non.substring(0, 4).equals("0000")){
          newBlock.nonce = String.valueOf(i);
          break;
        }
        i++;
      }

      newBlock.dgst = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + newBlock.nonce);
      newBlock.previous = lastBlock;
      lastBlock = newBlock;
    }
  }
}
