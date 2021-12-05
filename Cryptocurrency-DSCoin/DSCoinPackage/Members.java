package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;
import HelperClasses.CRF;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins; //they'll initialize
  public Transaction[] in_process_trans; //they'll initialize it; size is 100

  /**********************************************/

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    String coinid = mycoins.get(0).get_first();
    TransactionBlock srcblk = mycoins.get(0).get_second();
    mycoins.remove(0);

    Members dew = new Members();
    for(Members dest : DSobj.memberlist){
      if(dest.UID.equals(destUID)){
        dew = dest;
        break;
      }
    }

    TransactionBlock finder = DSobj.bChain.lastBlock;
    TransactionBlock current = DSobj.bChain.lastBlock;
    int a = 0;
    int flag = 0;
    while(finder != null && flag == 0){
      a = 0;
      while(a < finder.trarray.length && finder.trarray[a] != null){
        if(finder.trarray[a].coinID.equals(coinid)){
          flag = 1;
          current = finder;
          break;
        }
        a++;
      }
      finder = finder.previous;
    }

    Transaction tN = new Transaction();
    tN.coinID = coinid;
    tN.Source = this;
    tN.Destination = dew;
    tN.coinsrc_block = current;

    //add tN to in_process_trans
    int i = 0;
    while(i < in_process_trans.length && in_process_trans[i] != null)
      i++;
    in_process_trans[i] = new Transaction();
    in_process_trans[i] = tN;
    DSobj.pendingTransactions.AddTransactions(tN);
  }

  /********************************************/
  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
    String coinid = mycoins.get(0).get_first();
    TransactionBlock srcblk = mycoins.get(0).get_second();
    mycoins.remove(0);

    Members dew = new Members();
    for(Members dest : DSobj.memberlist){
      if(dest.UID.equals(destUID)){
        dew = dest;
        break;
      }
    }

    TransactionBlock finder = DSobj.bChain.FindLongestValidChain();
    TransactionBlock current = DSobj.bChain.FindLongestValidChain();
    int a = 0;
    int flag = 0;
    while(finder != null && flag == 0){
      a = 0;
      while(a < finder.trarray.length && finder.trarray[a] != null){
        if(finder.trarray[a].coinID.equals(coinid)){
          flag = 1;
          current = finder;
          break;
        }
        a++;
      }
      finder = finder.previous;
    }

    Transaction tN = new Transaction();
    tN.coinID = coinid;
    tN.Source = this;
    tN.Destination = dew;
    tN.coinsrc_block = current;

    //add tN to in_process_trans
    int i = 0;
    while(i < in_process_trans.length && in_process_trans[i] != null)
      i++;
    in_process_trans[i] = new Transaction();
    in_process_trans[i] = tN;
    DSobj.pendingTransactions.AddTransactions(tN);
  }


  /********************************************/

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    List<Pair<String, String>> path_to_root = new ArrayList<Pair<String, String>>();
    List<Pair<String, String>> dgst_path = new ArrayList<Pair<String, String>>();

    // finding block containing tobj

    TransactionBlock finder = DSObj.bChain.lastBlock;
    TransactionBlock current = DSObj.bChain.lastBlock;
    int a = 0;
    int flag = 0;
    while(finder != null && flag == 0){
      a = 0;
      while(a < finder.trarray.length && finder.trarray[a] != null){
        if(finder.trarray[a].coinID.equals(tobj.coinID)){
          flag = 1;
          current = finder;
          break;
        }
        a++;
      }
      finder = finder.previous;
    }

    if(flag == 0)
      throw new MissingTransactionException();

    TransactionBlock copy = current;

    // finding sibling coupled path to root of tobj in current.trarray

    int tN_idx = -1;
    int c = 0;
    while(c < current.trarray.length && current.trarray[c] != null){
      if(current.trarray[c] == tobj){
        tN_idx = c;
        break;
      }
      c++;
    }
    path_to_root = current.Tree.QueryDocument(tN_idx + 1);

    // finding dgst_path to lastblock

    CRF obj = new CRF(64);
    TransactionBlock last = DSObj.bChain.lastBlock;
    while(last != current){ // added tblocks which come after current; not added current yet
      dgst_path.add(new Pair<String, String>(last.dgst, last.previous.dgst + "#" + last.trsummary + "#" + last.nonce));
      last = last.previous;
    }

    if(last.previous == null){
      dgst_path.add(new Pair<String, String>(last.dgst, DSObj.bChain.start_string + "#" + last.trsummary + "#" + last.nonce));
      dgst_path.add(new Pair<String, String>(DSObj.bChain.start_string, null));
    }
    else{
      dgst_path.add(new Pair<String, String>(last.dgst, last.previous.dgst + "#" + last.trsummary + "#" + last.nonce));
      dgst_path.add(new Pair<String, String>(last.previous.dgst, null));
    }
    Collections.reverse(dgst_path);

    // removing tobj from in_process_trans

    int index = -1;
    int b = 0;
    while(b < in_process_trans.length && in_process_trans[b] != null){
      if(in_process_trans[b].coinID.equals(tobj.coinID)){
        index = b;
        break;
      }
      b++;
    }
    if(index == -1)
      throw new MissingTransactionException();

    for(int i = index; i < in_process_trans.length - 1; i++){
      in_process_trans[i] = in_process_trans[i+1];
    }
    in_process_trans[in_process_trans.length - 1] = null;
    int t = 0;
    while(t < tobj.Destination.mycoins.size() && Integer.parseInt(tobj.Destination.mycoins.get(t).get_first()) < Integer.parseInt(tobj.coinID))
      t++;
    tobj.Destination.mycoins.add(t, new Pair<String, TransactionBlock>(tobj.coinID, copy));
    return new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(path_to_root, dgst_path);
  }

  /***********************************************/

  public void MineCoin(DSCoin_Honest DSObj) {
    Transaction[] arr = new Transaction[DSObj.bChain.tr_count];
    int count = 0;
    int flag = 0;
    Transaction trash = new Transaction();

    while(count < DSObj.bChain.tr_count - 1){
      try{
        trash = DSObj.pendingTransactions.RemoveTransaction();
        flag = 0;
        if(!DSObj.bChain.lastBlock.checkTransaction_for_minecoin(trash))
          continue;

        if(count > 0){
          for(int i = 0; i < count - 1; i++){
            if(arr[i].coinID.equals(trash.coinID)){
              flag = 1;
              break;
            }
          }
        }
        if(flag == 0){
          arr[count] = trash;
          count++;
        }
      }
      catch(EmptyQueueException e){
        System.out.println(e);
        break;
      }
    }

    Transaction reward = new Transaction();
    DSObj.latestCoinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
    reward.coinID = DSObj.latestCoinID;
    reward.Source = null;
    reward.Destination = this;
    reward.coinsrc_block = null;
    arr[DSObj.bChain.tr_count - 1] = reward;

    TransactionBlock block = new TransactionBlock(arr);
    DSObj.bChain.InsertBlock_Honest(block);
    mycoins.add(new Pair<String, TransactionBlock>(reward.coinID, block));
  }

  /*******************************************************/

  public void MineCoin(DSCoin_Malicious DSObj) {
    Transaction[] arr = new Transaction[DSObj.bChain.tr_count];
    int count = 0;
    int flag = 0;
    int q = 0;
    Transaction trash = new Transaction();
    TransactionBlock longestblock = DSObj.bChain.FindLongestValidChain();
    while(count < DSObj.bChain.tr_count - 1){
      try{
        trash = DSObj.pendingTransactions.RemoveTransaction();
        q++;
        flag = 0;
        if(!longestblock.checkTransaction_for_minecoin(trash)){
          continue;
        }

        if(count > 0){
          for(int i = 0; i < count - 1; i++){
            if(arr[i].coinID.equals(trash.coinID)){
              flag = 1;
              break;
            }
          }
        }
        if(flag == 0){
          arr[count] = trash;
          count++;
        }
      }
      catch(EmptyQueueException e){
        System.out.println(e);
        break;
      }
    }

    Transaction reward = new Transaction();
    DSObj.latestCoinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
    reward.coinID = DSObj.latestCoinID;
    reward.Source = null;
    reward.Destination = this;
    reward.coinsrc_block = null;
    arr[DSObj.bChain.tr_count - 1] = reward;

    TransactionBlock block = new TransactionBlock(arr);
    DSObj.bChain.InsertBlock_Malicious(block);
    mycoins.add(new Pair<String, TransactionBlock>(reward.coinID, block));
  }
}
