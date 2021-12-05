package DSCoinPackage;

import HelperClasses.Pair;
import java.util.*;

public class Moderator{

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
    if(DSObj.latestCoinID == null)
      DSObj.latestCoinID = "99999";

    Members mod = new Members();
    mod.UID = "Moderator";
    int coin = 99999;
    int numblocks = coinCount/DSObj.bChain.tr_count;
    int num = 0;
    int f = 0;
    while(num < DSObj.memberlist.length && DSObj.memberlist[num] != null)
      num++;
    Transaction[][] robin = new Transaction[numblocks][DSObj.bChain.tr_count];

    for(int i = 0; i < numblocks; i++){
      for(int j = 0; j < DSObj.bChain.tr_count; j++){
        robin[i][j] = new Transaction();
      }
    }

    TransactionBlock[] blocks = new TransactionBlock[numblocks];
    Transaction[] for_block = new Transaction[DSObj.bChain.tr_count];

    for(int i = 0; i < DSObj.bChain.tr_count; i++)
      for_block[i] = new Transaction();

    for(int i = 0; i < numblocks; i++){
      for(int j = 0; j < DSObj.bChain.tr_count; j++){
        DSObj.latestCoinID = Integer.toString(coin + 1);
        coin = Integer.parseInt(DSObj.latestCoinID); // creating coin
        robin[i][j].coinID = Integer.toString(coin);
        robin[i][j].Source = mod;
        robin[i][j].Destination = DSObj.memberlist[f%num];
        robin[i][j].coinsrc_block = null;
        for_block[j] = robin[i][j];
        f++;
      }

      blocks[i] = new TransactionBlock(for_block);  //creating and inserting block to blockchain // error is in this line
      DSObj.bChain.InsertBlock_Honest(blocks[i]);
    }

    // adding coins to member's mycoins
    f = 0;
    for(int i = 0; i < numblocks; i++){
      for(int j = 0; j < DSObj.bChain.tr_count; j++){
        DSObj.memberlist[f%num].mycoins.add(new Pair<String, TransactionBlock>(robin[i][j].coinID, blocks[i]));
        f++;
      }
    }
  }





  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
    if(DSObj.latestCoinID == null)
      DSObj.latestCoinID = "99999";

    Members mod = new Members();
    mod.UID = "Moderator";
    int coin = 99999;
    int numblocks = coinCount/DSObj.bChain.tr_count;
    int num = 0;
    int f = 0;
    while(num < DSObj.memberlist.length && DSObj.memberlist[num] != null)
      num++;
    Transaction[][] robin = new Transaction[numblocks][DSObj.bChain.tr_count];

    for(int i = 0; i < numblocks; i++){
      for(int j = 0; j < DSObj.bChain.tr_count; j++){
        robin[i][j] = new Transaction();
      }
    }

    TransactionBlock[] blocks = new TransactionBlock[numblocks];
    Transaction[] for_block = new Transaction[DSObj.bChain.tr_count];

    for(int i = 0; i < DSObj.bChain.tr_count; i++)
      for_block[i] = new Transaction();

    for(int i = 0; i < numblocks; i++){
      for(int j = 0; j < DSObj.bChain.tr_count; j++){
        DSObj.latestCoinID = Integer.toString(coin + 1);
        coin = Integer.parseInt(DSObj.latestCoinID); // creating coin
        robin[i][j].coinID = Integer.toString(coin);
        robin[i][j].Source = mod;
        robin[i][j].Destination = DSObj.memberlist[f%num];
        robin[i][j].coinsrc_block = null;
        for_block[j] = robin[i][j];
        f++;
      }

      blocks[i] = new TransactionBlock(for_block); //creating and inserting block to blockchain
      DSObj.bChain.InsertBlock_Malicious(blocks[i]);
    }

    // adding coins to member's mycoins
    f = 0;
    for(int i = 0; i < numblocks; i++){
      for(int j = 0; j < DSObj.bChain.tr_count; j++){
        DSObj.memberlist[f%num].mycoins.add(new Pair<String, TransactionBlock>(robin[i][j].coinID, blocks[i]));
        f++;
      }
    }
  }
}
