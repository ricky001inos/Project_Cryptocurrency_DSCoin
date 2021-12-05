package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
    if(firstTransaction == null){
      firstTransaction = transaction;
      lastTransaction = transaction;
      firstTransaction.previous = null;
      firstTransaction.next = null;
      lastTransaction.previous = null;
      lastTransaction.next = null;
      numTransactions = 1;
    }
    else{
      lastTransaction.next = transaction;
      transaction.previous = lastTransaction;
      transaction.next = null;
      lastTransaction = transaction;
      numTransactions++;
    }
  }

  public Transaction RemoveTransaction () throws EmptyQueueException {
    if(firstTransaction == null){
      throw new EmptyQueueException();
    }
    else if(numTransactions == 1){
      Transaction current = firstTransaction;
      firstTransaction = null;
      lastTransaction = null;
      numTransactions = 0;
      return current;
    }
    else{
      Transaction current = firstTransaction;
      firstTransaction = firstTransaction.next;
      firstTransaction.previous = null;
      numTransactions--;
      return current;
    }
  }

  public int size() {
    int i = 0;
    Transaction current = firstTransaction;
    while(current != null){
      i++;
      current = current.next;
    }
    return i;
  }
}
